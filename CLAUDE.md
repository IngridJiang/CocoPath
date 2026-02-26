# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**CoCoPath** is a concolic execution framework for systematically exploring execution paths in Vitruvius model transformations. It combines:
- **Galette** – JVM dynamic taint tracking via ASM bytecode instrumentation
- **Modified Knarr Runtime** – Symbolic execution and path constraint management
- **Z3 / GREEN solver** – SMT constraint solving
- **Vitruvius** – Eclipse-based model transformation framework (EMF/XMI)

## Prerequisites

- Java 17 (OpenJDK recommended; supports 8–21 at runtime)
- Maven 3.6+
- Python 3.x (for `switch-dependency.py`)
- External dependency: clone and build [Amalthea-acset](https://github.com/IngridJiang/Amalthea-acset)

## Build Commands

```bash
# 1. Build external dependency (one-time)
git clone https://github.com/IngridJiang/Amalthea-acset.git
cd Amalthea-acset && mvn clean install -DskipTests -Dcheckstyle.skip=true

# 2. Build CoCoPath
mvn clean install -DskipTests -Dcheckstyle.skip=true

# 3. Generate instrumented Java VM (required for taint tracking; run from knarr-runtime/)
cd knarr-runtime && mvn process-test-resources
```

If the instrumented VM is missing or stale:
```bash
cd knarr-runtime && rm -rf target/galette/ && mvn clean process-test-resources
```

If Maven cache is corrupted:
```bash
rm -rf ~/.m2/repository/edu/neu/ccs/prl/galette/
rm -rf ~/.m2/repository/tools/vitruv/methodologisttemplate/
mvn clean install -U -DskipTests
```

## Running Path Exploration

All scripts run from `knarr-runtime/`:

```bash
# Linux/macOS
./run-symbolic-execution.sh             # interactive
./run-symbolic-execution.sh --internal  # simplified stub (no Vitruvius)
./run-symbolic-execution.sh --external  # full Vitruvius transformations
./run-symbolic-execution.sh --multivar  # 25 paths (5×5 multi-variable)

# Windows CMD
run-symbolic-execution.bat [internal|external|multivar]

# PowerShell
.\run-symbolic-execution.ps1 [-Internal | -External | -MultiVar]
```

## Running Tests

```bash
mvn test                                    # all modules
mvn test -pl galette-agent                  # ASM instrumentation unit tests (13)
mvn test -pl galette-integration-tests      # integration tests (70+)
mvn test -pl knarr-runtime                  # constraint solver tests
```

## Architecture

### Four-Layer Stack

```
Application Layer     → amalthea-acset-integration/.../Test.java
                           (Vitruvius model transformation, user decisions)
Path Exploration      → knarr-runtime/.../vitruvius/AutomaticVitruvPathExploration.java
                           (single-variable) / AutomaticVitruvMultiVarPathExploration.java
                           (multi-variable, Cartesian product of N symbolic vars)
Constraint Management → knarr-runtime/.../runtime/PathExplorer.java
                           PathUtils.java (manual constraint API)
                           ConstraintSolver.java (GREEN/Z3 wrapper)
Tag Propagation       → galette-agent/ (ASM bytecode instrumentation)
                           GaletteSymbolicator.java (symbolic value creation & tag reuse)
```

### Key Source Files

| File | Role |
|------|------|
| `knarr-runtime/src/main/java/.../runtime/PathExplorer.java` | Core exploration loop: execute → collect constraints → negate → solve → repeat |
| `knarr-runtime/src/main/java/.../runtime/PathUtils.java` | Manual API for domain constraints (`addIntDomainConstraint`) and path constraints (`addSwitchConstraint`) |
| `knarr-runtime/src/main/java/.../runtime/GaletteSymbolicator.java` | Creates tagged symbolic integers; reuses tags via qualified names across iterations |
| `knarr-runtime/src/main/java/.../runtime/Z3ConstraintSolver.java` | Direct Z3 JNI integration |
| `knarr-runtime/src/main/java/.../runtime/ConstraintSolver.java` | GREEN solver wrapper |
| `knarr-runtime/src/main/java/.../vitruvius/AutomaticVitruvMultiVarPathExploration.java` | Multi-variable exploration entry point |
| `amalthea-acset-integration/vsum/src/main/java/.../Test.java` | Vitruvius transformation entry point; calls PathUtils to register constraints |
| `galette-agent/src/main/java/.../GaletteAgent.java` | JVM instrumentation agent |

### Constraint Collection Pattern

Constraints are registered **manually** in Vitruvius reactions (automatic bytecode instrumentation is disabled due to JVM verification errors):

```java
// Domain constraint (defines variable range)
PathUtils.addIntDomainConstraint("user_choice", 0, 5);
// → (0 <= user_choice) AND (user_choice < 5)

// Path constraint (records executed branch)
PathUtils.addSwitchConstraint("user_choice", 0);
// → user_choice == 0
```

Tag reuse across exploration iterations is achieved via qualified names:
```java
GaletteSymbolicator.getOrMakeSymbolicInt("CreateAscetTaskRoutine:execute:userChoice")
```

### Output Structure

```
knarr-runtime/
├── execution_paths_automatic.json     # 5 paths: {pathId, inputs, constraints, executionTime}
├── execution_paths_multivar.json      # 25 paths
├── galette-output-automatic-{0..4}/   # EMF model files per path
│   ├── example.model                  # AMALTHEA source
│   ├── example.model2                 # ASCET target
│   └── galette-test-output/vsum-output.xmi
└── galette-output-multivar-{i}_{j}/
```

### Modifying Consistency Preservation Rules

Reactions live in the external Amalthea-acset repository. After editing:
```bash
cd Amalthea-acset && mvn clean generate-sources
cd /path/to/galette-vitruv/knarr-runtime
./copy-generated-reactions.sh --external-path /path/to/Amalthea-acset
cd ../amalthea-acset-integration && mvn clean compile -Dcheckstyle.skip=true
```
Generated Java is copied from `Amalthea-acset/consistency/target/generated-sources/reactions/mir/` to `amalthea-acset-integration/consistency/src/main/java/mir/`.

## Known Limitations

- Only integer symbolic variables are supported (no strings, booleans, arrays)
- Automatic switch instrumentation from bytecode is disabled; constraints must be registered via `PathUtils` manually
- No automatic inter-procedural constraint propagation
