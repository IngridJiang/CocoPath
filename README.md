# CoCoPath - Concolic Exploration of Consistency-Preserving Paths

---

> **ECMFA 2025 Second Round — Evaluation Data**
>
> ### Execution Time — BrakeSystem-ControlSystem Scenario (2 symbolic variables, 10 paths)
>
> | Path | Profile  | Calibration  | Constraints | Time (ms) |
> |-----:|----------|--------------|------------:|----------:|
> |  P1  | Skip     | —            |           2 |       140 |
> |  P2  | Off-road | Conservative |           4 |       295 |
> |  P3  | Off-road | Standard     |           4 |       101 |
> |  P4  | Off-road | Track        |           3 |       109 |
> |  P5  | Comfort  | Conservative |           3 |       192 |
> |  P6  | Comfort  | Standard     |           4 |       120 |
> |  P7  | Comfort  | Track        |           3 |        89 |
> |  P8  | Sport    | Conservative |           2 |       161 |
> |  P9  | Sport    | Standard     |           3 |       107 |
> | P10  | Sport    | Track        |           2 |        90 |
>
> | Paths | Initialisation (ms) | Avg. / Path (ms) | Total (ms) |
> |------:|--------------------:|-----------------:|-----------:|
> |    10 |              33,905 |            140.4 |     35,309 |
---

## Overview

CoCoPath is a concolic execution framework for systematically exploring execution paths in consistency-preserving model transformations. By combining dynamic taint tracking, concolic execution, and model transformation frameworks, CoCoPath enables automatic path exploration to derive potential target models based on source models, consistency preservation rules (CPRs), and optional domain constraints.

This project integrates:
- **Galette** - Dynamic taint tracking for the JVM
- **Modified Knarr Runtime** - Symbolic execution engine
- **Z3 Solver** - SMT constraint solver (via JNI)
- **Vitruvius Framework** - Model transformation and consistency preservation

The framework provides **concolic execution** (combined concrete + symbolic execution) for model transformations, enabling developers to explore the consequences of different user decisions and resolve temporary inconsistencies in an informed manner.

![CocoPath Overview Diagram](overview.png)

## Key Features

### Systematic Path Exploration
1. **Executes transformations** with concrete input values
2. **Tracks symbolic values** through transformation logic via dynamic taint tracking
3. **Collects path constraints** from consistency preservation rules
4. **Generates new inputs** by negating constraints and solving with Z3
5. **Explores all feasible paths** automatically until complete

### Architecture Components

#### Galette (Dynamic Taint Tracking)
- **Purpose**: Propagates symbolic tags through JVM bytecode execution
- **Role**: Attaches and propagates symbolic identities alongside concrete values
- **Key APIs**:
  - `Tainter.setTag(value, tag)` - Attach symbolic tag to value
  - `Tainter.getTag(value)` - Retrieve tag from value
  - Tag propagation through arithmetic, method calls, field access

#### Modified Knarr Runtime
- **Purpose**: Symbolic execution and path constraint management
- **Location**: `knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/`
- **Key Classes**:
  - `GaletteSymbolicator` - Creates and manages symbolic values with tag reuse
  - `PathExplorer` - Orchestrates systematic path exploration
  - `PathUtils` - Manages path conditions and constraints
  - `SymbolicComparison` - Records constraints from Vitruvius reactions
  - `Z3ConstraintSolver` - Interfaces with Z3 for constraint solving

#### Vitruvius Integration
- **Purpose**: Provides real-world model transformation scenarios
- **Example**: AMALTHEA ↔ ASCET model synchronization
- **Location**: `amalthea-acset-integration/`
- Consistency preservation rules require user decisions that cannot be resolved automatically

### Constraint Collection Approach

CoCoPath employs a **CPR-level constraint registration mechanism** that makes decision semantics explicit at the transformation logic level:

1. **Symbolic Variable Registration**: Vitruvius reactions call `GaletteSymbolicator.getOrMakeSymbolicInt()` to create or reuse symbolic tags
2. **Constraint Recording**: Reactions invoke `SymbolicComparison.symbolicVitruviusChoice()` to record path constraints
3. **Tag Reuse**: Qualified names (e.g., "CreateAscetTaskRoutine:execute:userChoice_forTask_task1") enable tag reuse across iterations

This approach prioritizes framework compatibility and reliability over full automation, avoiding bytecode verification issues while maintaining systematic path exploration capabilities.

## Project Structure

```
CocoPath/
├── knarr-runtime/
│   ├── src/main/java/
│   │   └── edu/neu/ccs/prl/galette/
│   │       ├── concolic/knarr/runtime/
│   │       │   ├── GaletteSymbolicator.java      # Symbolic value creation with tag reuse
│   │       │   ├── PathExplorer.java             # Systematic path exploration
│   │       │   ├── PathUtils.java                # Path constraint management
│   │       │   ├── SymbolicComparison.java       # Constraint recording from reactions
│   │       │   └── Z3ConstraintSolver.java       # Z3 SMT solver integration
│   │       └── vitruvius/
│   │           ├── AutomaticVitruvPathExploration.java         # Single-variable exploration
│   │           └── AutomaticVitruvMultiVarPathExploration.java # Multi-variable exploration
│   └── run-symbolic-execution-adapted.sh         # Execution scripts
│
├── amalthea-acset-integration/                   # AMALTHEA-ASCET case study
│   ├── vsum/src/main/java/.../Test.java         # Model transformation entry point
│   └── consistency/src/main/reactions/           # Consistency preservation rules
│
├── tinybrake-integration/                        # BrakeDisc-ControlSystem case study
│   ├── model/                                    # BrakeSystem / ControlSystem metamodels
│   ├── consistency/src/main/reactions/           # CPRs for brake-to-control mapping
│   └── vsum/                                     # VSUM configuration and test entry points
│
└── README.md                                      # This file
```

## Running CoCoPath

### Prerequisites

1. **Java 17** (OpenJDK recommended for building, supports Java 8-21 at runtime)
2. **Maven 3.6+**
3. **Python 3.x** (for dependency management scripts)
4. **Z3 Solver** (included via z3-turnkey dependency)
5. **Git** (for cloning external dependencies)

### Initial Setup

#### 1. Clone and Build External Dependencies
```bash
# Clone the external Amalthea-ASCET repository (required for Vitruvius)
cd /home/anne/CocoPath
git clone https://github.com/IngridJiang/Amalthea-acset.git

# Build it to populate Maven repository
cd Amalthea-acset
mvn clean install -DskipTests -Dcheckstyle.skip=true
```

#### 2. Build CoCoPath
```bash
cd /home/anne/CocoPath/CocoPath
mvn clean install -DskipTests -Dcheckstyle.skip=true
```

#### 3. Generate Instrumented Java VM
```bash
cd knarr-runtime
mvn process-test-resources
```

**Important**: The instrumented Java VM at `target/galette/java/` is required for dynamic taint tracking.

### Quick Start

```bash
cd knarr-runtime

# Interactive mode - choose execution type
./run-symbolic-execution-adapted.sh

# Direct execution modes:
./run-symbolic-execution-adapted.sh --internal        # Simplified test case (bundled AMALTHEA stub)
./run-symbolic-execution-adapted.sh --external        # Full Vitruvius transformations (external Amalthea-acset repo)
./run-symbolic-execution-adapted.sh --multivar        # Multi-variable AMALTHEA exploration (25 paths)
./run-symbolic-execution-adapted.sh --brake           # BrakeDisc-ControlSystem, single disc (10 paths)
./run-symbolic-execution-adapted.sh --brake-multivar  # BrakeDisc-ControlSystem, two discs 
```

**Windows:**
```cmd
run-symbolic-execution-adapted.bat
run-symbolic-execution-adapted.bat internal
run-symbolic-execution-adapted.bat multivar
run-symbolic-execution-adapted.bat brake
run-symbolic-execution-adapted.bat brake-multivar
```

**PowerShell:**
```powershell
.\run-symbolic-execution-adapted.ps1
.\run-symbolic-execution-adapted.ps1 -Internal
.\run-symbolic-execution-adapted.ps1 -MultiVar
.\run-symbolic-execution-adapted.ps1 -Brake
.\run-symbolic-execution-adapted.ps1 -BrakeMultiVar
```

### Execution Modes

#### Single-Variable Exploration (`--internal` / `--external`)
- Explores one symbolic user decision with 5 possible values
- Generates 5 execution paths
- Output: `execution_paths_automatic.json` and model files in `galette-output-automatic-{0..4}/`

#### Multi-Variable Exploration (`--multivar`)
- Explores TWO symbolic user decisions simultaneously
- Generates 25 execution paths (5 × 5 combinations)
- Output: `execution_paths_multivar.json` and models in `galette-output-multivar-{i}_{j}/`

#### Brake Single-Disc (`--brake`)
- Explores one `BrakeDisc` insertion with 2 symbolic variables: `profileChoice` (4 values) and `calibChoice` (3 values)
- Generates 10 paths (Skip skips calibration; Off-road/Comfort/Sport × 3 calibration options)
- Output: `execution_paths_brake.json` and models in `galette-output-brake-{p}_{c}/`

#### Brake Two-Disc (`--brake-multivar`)
- Explores insertion of two `BrakeDisc` instances simultaneously (4 symbolic variables)
- Output: `execution_paths_brake_multivar.json` and models in `galette-output-brake-multivar-{p1}_{c1}_{p2}_{c2}/`

### Expected Output

```
knarr-runtime/
├── execution_paths_automatic.json        # Single-variable: 5 paths with constraints
├── execution_paths_multivar.json         # Multi-variable: 25 path combinations
├── galette-output-automatic-*/           # Generated models per path
└── galette-output-multivar-*_*/          # Models for each input combination
    └── galette-test-output/
        └── vsum-output.xmi               # Synchronized AMALTHEA+ASCET models
```

Each path record contains:
- **pathId**: Unique path identifier
- **inputs**: Concrete values for symbolic variables
- **constraints**: Path conditions as logical formulas
- **executionTime**: Performance metrics

## Case Study: AMALTHEA-ASCET Synchronization

The evaluated scenario demonstrates consistency preservation between:
- **AMALTHEA**: Models ECU architecture and operating system aspects
- **ASCET**: Specifies functional behavior of embedded control systems

When adding a Task to AMALTHEA, the CPR must create a corresponding task in ASCET. However, ASCET defines multiple concrete task subtypes (InitTask, PeriodicTask, SoftwareTask, TimeTableTask) while AMALTHEA uses abstract Tasks. This mapping requires domain knowledge and user decisions.

### Modifying Consistency Preservation Rules

To modify the reactions or experiment with different transformation logic:

1. **Clone the external AMALTHEA-ASCET repository**:
   ```bash
   git clone https://github.com/IngridJiang/Amalthea-acset
   ```

2. **Modify the reactions** in the external project and generate Java code:
   ```bash
   cd Amalthea-acset
   mvn clean generate-sources
   ```

3. **Copy generated reactions** to the internal CoCoPath project:
   ```bash
   cd /path/to/CocoPath/knarr-runtime
   ./copy-generated-reactions.sh --external-path /path/to/Amalthea-acset
   ```

4. **Rebuild the internal project** with updated reactions:
   ```bash
   cd ../amalthea-acset-integration
   mvn clean compile -Dcheckstyle.skip=true
   ```

The `copy-generated-reactions.sh` script copies generated Java code from:
- Source: `Amalthea-acset/consistency/target/generated-sources/reactions/mir/`
- Target: `amalthea-acset-integration/consistency/src/main/java/mir/`

CoCoPath systematically explores all possible mappings, generating:
- Target ASCET models for each choice
- Model difference metrics (added/modified/deleted elements)
- Path constraints characterizing each execution

This enables engineers to:
- Compare transformation outcomes quantitatively
- Identify high-impact decision points
- Understand consequences before committing changes

## Case Study: BrakeDisc-ControlSystem

The TinyBrake case study demonstrates CoCoPath on an automotive brake system scenario:

- **Source model**: `BrakeSystem` containing one or more `BrakeDisc` elements (identified by diameter)
- **Target model**: `ControlSystem` containing `AxleControlUnit` elements

When a `BrakeDisc` is inserted, the CPR must configure the corresponding `AxleControlUnit`. This requires two sequential user decisions, each modelled as a symbolic integer:

| Variable | Qualified name | Values |
|---|---|---|
| `profileChoice` | `CreateAndConfigureAxleUnitRoutine:execute:profileChoice_disc_{diameter}` | 0 = Sport, 1 = Comfort, 2 = Off-road, 3 = Skip |
| `calibChoice` | `ApplyCalibrationRoutine:execute:calibChoice_disc_{diameter}` | 0 = Standard (±0.0), 1 = Track (+0.5), 2 = Conservative (−0.5) |

When `profileChoice = 3` (Skip), the calibration match block is silently bypassed — `calibChoice` is never exercised for that disc, yielding 10 distinct paths for a single disc (1 + 3×3).

No external repository is needed: the `tinybrake-integration` module is built locally and selected automatically by the `--brake` / `--brake-multivar` flags.

## Troubleshooting

### Common Build Issues and Solutions

#### 1. Instrumented Java VM Issues

**Problem**: "Instrumented java not found at target/galette/java/bin/java"
```bash
# Solution: Force regeneration of instrumented Java
cd knarr-runtime
rm -rf target/galette/
mvn clean process-test-resources
```

**Problem**: "Existing Java installation did not have correct settings"
- This occurs when the instrumented Java was built with different settings
- The Galette Maven plugin will automatically delete and recreate it

#### 2. Maven Cache Issues

**Problem**: Conflicting dependency versions or stale artifacts
```bash
# Solution 1: Clean local Maven cache for the project
rm -rf ~/.m2/repository/edu/neu/ccs/prl/galette/
rm -rf ~/.m2/repository/tools/vitruv/methodologisttemplate/

# Solution 2: Force update dependencies
mvn clean install -U -DskipTests
```

#### 3. External Amalthea-acset Build Failures

**Problem**: Missing CreateInterruptTaskRoutine or compilation errors
```bash
# Solution: Regenerate reactions from scratch
cd /home/anne/CocoPath/Amalthea-acset
mvn clean generate-sources
mvn install -DskipTests -Dcheckstyle.skip=true

# Then copy to internal project
cd /home/anne/CocoPath/CocoPath/knarr-runtime
./copy-generated-reactions.sh --external-path /home/anne/CocoPath/Amalthea-acset
```

#### 4. Z3 Library Loading Issues

**Problem**: UnsatisfiedLinkError for Z3 native library
- The z3-turnkey dependency should handle this automatically
- If issues persist, verify Z3 native libs are present:
```bash
ls ~/.m2/repository/tools/aqua/z3-turnkey/4.12.2.1/
# Should contain native libraries for your platform
```

#### 5. Java Version Conflicts

**Problem**: "var cannot be resolved to a type" or similar Java feature errors
- Ensure JAVA_HOME points to Java 17+:
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
java -version  # Should show 17.x.x
```

### Verification Steps

After setup, verify everything works:

```bash
# 1. Check Java version
java -version | grep "17"

# 2. Check Galette agent exists
ls ~/.m2/repository/edu/neu/ccs/prl/galette/galette-agent/1.0.0-SNAPSHOT/

# 3. Check instrumented Java exists
ls knarr-runtime/target/galette/java/bin/java

# 4. Run simple internal test
cd knarr-runtime
./run-symbolic-execution-adapted.sh --internal

# 5. Check output was generated
ls execution_paths_automatic.json
ls -d galette-output-automatic-*/
```

## Technical Implementation

### Symbolic Value Management
- **Tag Creation**: `GaletteSymbolicator.getOrMakeSymbolicInt(qualifiedName, value, min, max)`
- **Tag Reuse**: Qualified names enable consistent symbolic variables across iterations
- **Expression Mapping**: Tags are associated with Green/Z3 expressions for solving

### Path Constraint Construction
- **Domain Constraints**: Define valid value ranges (e.g., `0 <= userChoice < 5`)
- **Path Constraints**: Record executed branches (e.g., `userChoice == 2`)
- **Negation Strategy**: Systematically negate constraints to explore alternative paths

### Exploration Algorithm
1. Initialize with concrete input values
2. Execute transformation while collecting constraints
3. Negate selected constraints to generate new inputs
4. Solve constraint system with Z3
5. Re-execute with new inputs if satisfiable
6. Terminate when no unexplored feasible paths remain

## Evaluation Results

Based on the ECMFA-20 paper evaluation:

- **Path Coverage**: Achieves complete systematic exploration (5/5 single-variable, 25/25 multi-variable paths)
- **Scalability**: Per-path execution time increases only 1.42× despite 5× increase in paths
- **Memory Overhead**: Moderate 1.36× overhead, primarily from dynamic taint tracking
- **Compatibility**: Only symbolic execution tool fully compatible with Vitruvius/EMF/OSGi

## Limitations

- **Manual Constraint Registration**: Requires explicit constraint recording in CPRs (future work: automatic weaving)
- **Third-party Libraries**: Cannot track decisions in external code
- **Runtime Overhead**: Dynamic taint tracking introduces performance cost
- **Domain Constraints**: Must be manually specified based on domain knowledge

## Future Work

- Bytecode-level constraint extraction for full automation
- Support for additional transformation frameworks beyond Vitruvius
- Optimization of taint tracking overhead
- Integration with model verification techniques


## License

See [LICENSE](../LICENSE) in the project root.

## Acknowledgements

- **Galette**: Northeastern University (Dynamic Taint Tracking)
- **Knarr**: Original symbolic execution engine (modified for Galette integration)
- **Z3**: Microsoft Research (SMT Solver)
- **Vitruvius**: Karlsruhe Institute of Technology (Model Transformation Framework)
- **AMALTHEA-ASCET**: Bosch (Industrial case study)