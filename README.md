# CocoPath - Concolic Exploration of Consistency-Preserving Paths

## Overview

A concolic method that systematically explores different CPR-triggered transformation paths, each of which preserves consistency.

This project integrates **Galette** (dynamic taint tracking for JVM), **Knarr** (symbolic execution engine), **GreenSolver** (constraint solver), and **Vitruvius** (model transformation framework) to enable automatic path exploration in model-driven engineering workflows.

This framework provides **concolic execution** (combined concrete + symbolic execution) for model transformations using a **hybrid approach**: automatic tag propagation with manual constraint collection. It:

1. **Executes transformations** with concrete input values
2. **Tracks symbolic values** through the transformation logic
3. **Collects path constraints** at decision points (if/switch statements)
4. **Generates new inputs** by negating constraints
5. **Explores all paths** automatically until complete

![CocoPath Overview Diagram](overview.png)

### Key Components

#### Galette
- **Purpose**: Dynamic taint tracking for JVM bytecode
- **Role**: Attaches symbolic "tags" to values and propagates them through operations
- **Location**: `galette-agent/`, `galette-instrument/`
- **Key APIs**:
  - `Tainter.setTag(value, tag)` - Attach tag to value
  - `Tainter.getTag(value)` - Read tag from value
  - `Tag.getLabels()` - Extract variable names from tag
  - `GaletteSymbolicator.getExpressionForTag(tag)` - Get symbolic expression

#### Knarr
- **Purpose**: Symbolic execution engine
- **Role**: Creates symbolic values and tracks path constraints
- **Location**: `knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/concolic/knarr/runtime/`
- **Key Classes**:
  - `GaletteSymbolicator` - Creates symbolic values with tags
    - `getOrMakeSymbolicInt(qualifiedName, value, min, max)` - Create or reuse tag with domain constraint
    - Tag reuse across iterations using qualified names (e.g., "CreateAscetTaskRoutine:execute:userChoice")
  - `SymbolicComparison` - Records switch constraints from reactions
    - `symbolicVitruviusChoice(selected, min, max)` - Records the actual choice made
  - `PathExplorer` - Automatic path exploration orchestration
  - `PathUtils` - Retrieves current path condition (`getCurPC()`)
  - `ConstraintSolver` - Negates and solves constraints using GREEN/Z3

#### GreenSolver
- **Purpose**: Constraint solving using Z3/CVC/other SMT solvers
- **Role**: Solves path constraints to generate new test inputs
- **Integration**: Used by `ConstraintSolver` to find satisfying assignments
- **Dependency**: `za.ac.sun.cs:green` (Maven)

#### Vitruvius Framework
- **Purpose**: Consistency preservation for model transformations
- **Role**: Provides real-world model transformation scenarios
- **Example**: Amalthea ↔ ASCET model synchronization
- **Location**: `amalthea-acset-integration/`

### Hybrid Approach: Automatic Tag Propagation + Manual Constraint Collection

CocoPath uses a **pragmatic hybrid approach** combining automatic and manual techniques:

#### What's Automatic (Galette)
- **Tag Creation**: Symbolic values are created via `GaletteSymbolicator.tagInteger()`
- **Tag Propagation**: Tags automatically flow through:
  - Arithmetic operations (`+`, `-`, `*`, `/`, `%`)
  - Method calls (via shadow stack)
  - Field reads/writes
  - Array accesses

#### What's Manual (PathUtils API)
- **Domain Constraints**: Define valid value ranges for symbolic variables such as from declaritive consistency
- **Path Constraints**: Record which branch/case was taken during execution

#### Why Manual Constraint Collection?

Initial attempts at fully automatic bytecode instrumentation for switch statements encountered **JVM bytecode verification errors** due to complex shadow stack management. Rather than compromise reliability, we adopted a manual constraint collection approach that prioritizes:

1. **Correctness**: No bytecode verification errors
2. **Simplicity**: Clear, understandable constraint collection points
3. **Maintainability**: Easy to debug and extend
4. **Pragmatism**: Works reliably for research purposes


#### Two Types of Constraints

**Domain Constraint** (limits solver search space):
```
0 <= user_choice < 5
```
**Path Constraint** (records execution):
```
user_choice == 0
```
Records that this specific execution took the first case.

Together, they form the **Path Condition** for solver-based path exploration:
```
(0 <= user_choice < 5) AND (user_choice == 0)
```

The solver negates path constraints to generate new inputs exploring different paths.

### Tag Tracking Implementation

CocoPath now **actively reads and utilizes tag information** during path exploration:

#### What Tags Store
Each tag attached to a symbolic value contains:
1. **Variable Name** (label) - e.g., "user_choice", "user_choice_1"
2. **Symbolic Expression** (GREEN Expression object) - e.g., IntVariable, BinaryOperation

#### Benefits of Tag Reading

1. **Dynamic Variable Names**: Variable names are no longer hardcoded, extracted from tags at runtime
2. **Metadata Propagation**: Tags serve as a pipeline carrying symbolic information through execution
3. **Expression Access**: Symbolic expressions can be retrieved for advanced constraint manipulation
4. **Debugging Support**: Tag detection messages help verify proper tag propagation

#### Current Role of Tags

**Active Uses:**
- Variable name extraction 
- Symbolic expression retrieval (for potential advanced use)
- Runtime tag presence verification

**Not Yet Used:**
- Automatic constraint collection via bytecode instrumentation (disabled due to verification errors)

**Architecture Status:**
- Tag infrastructure is fully operational and actively utilized
- Constraint collection remains manual but uses tag-derived metadata
- System bridges automatic tag propagation (Galette) with manual constraint APIs (PathUtils)

## Project Structure

```
knarr-runtime/
├── src/main/java/
│   └── edu/neu/ccs/prl/galette/
│       ├── concolic/knarr/runtime/
│       │   ├── GaletteSymbolicator.java      # Creates symbolic values
│       │   ├── PathUtils.java                # Collects path constraints
│       │   ├── PathExplorer.java             # Automatic path exploration
│       │   └── ConstraintSolver.java         # Constraint negation & solving
│       └── vitruvius/
│           └── AutomaticVitruvPathExploration.java      # Automatic path exploration
├── src/test/java/                            # Unit tests
├── run-symbolic-execution.sh/.bat/.ps1       # Execution scripts
└── README.md                                 # This file

amalthea-acset-integration/                    # Vitruvius example
├── vsum/src/main/java/.../Test.java          # Model transformation entry point
└── consistency/src/main/reactions/           # Transformation reactions
```

## Running the Project

### Prerequisites

1. **Java 17**
2. **Maven 3.6+**
3. **Python 3.x** (for dependency switching)


### Helper Classes

- **`AutomaticVitruvPathExplorationHelper`** - Shared utilities for path exploration
  - `verifyInstrumentation()` - Checks that Galette instrumentation is working
  - `initializeEMF()` - Initializes EMF resource factories
  - `loadVitruviusTestClass()` - Loads the Vitruvius Test class
  - `createWorkingDirectory()` - Creates output directories
  - `exportSingleVarResults()` - Exports single-variable exploration results
  - `exportMultiVarResults()` - Exports multi-variable exploration results

### Syncing Generated Reactions (After Upstream Changes)

When the upstream Amalthea-acset project changes (e.g., renaming InterruptTask to InitTask), you need to sync the generated reactions code:

```bash
# 1. Build external Amalthea-acset to generate new reactions
cd /path/to/Amalthea-acset
mvn clean generate-sources

# 2. Copy generated reactions to internal project
cd knarr-runtime
./copy-generated-reactions.sh

# Or with custom paths:
./copy-generated-reactions.sh --external-path /path/to/Amalthea-acset

# 3. Build internal project with updated reactions
cd ../amalthea-acset-integration
mvn clean compile -Dcheckstyle.skip=true
```

The `copy-generated-reactions.sh` script:
- Copies generated reactions from `Amalthea-acset/consistency/target/generated-sources/reactions/mir/`
- To `amalthea-acset-integration/consistency/src/main/java/mir/`
- Creates backups of existing files
- Supports custom paths with `--external-path` and `--internal-path` flags

### Quick Start

```bash
cd knarr-runtime

# Interactive mode (choose internal or external)
./run-symbolic-execution.sh

# Or specify directly:
./run-symbolic-execution.sh --internal   # Fast, simplified
./run-symbolic-execution.sh --external   # Full Vitruvius transformations
./run-symbolic-execution.sh --multivar   # Multi-variable (25 paths)
```

**Windows:**
```cmd
run-symbolic-execution.bat
run-symbolic-execution.bat internal
run-symbolic-execution.bat multivar
```

**PowerShell:**
```powershell
.\run-symbolic-execution.ps1
.\run-symbolic-execution.ps1 -Internal
.\run-symbolic-execution.ps1 -MultiVar
```

**Override path at runtime** (all platforms):
```bash
# Bash
./run-symbolic-execution.sh --external --external-path /custom/path

# PowerShell
.\run-symbolic-execution.ps1 -UseExternal -ExternalPath "D:\Custom\Path"
```

### Execution Modes

#### Internal Mode (Default)
- **Output**: Simplified XMI stubs
- **Requirements**: None (self-contained)
- **Use case**: Quick testing, demonstration

#### External Mode
- **Output**: Full Vitruvius reactions & transformations
- **Requirements**: External Amalthea-acset repository: https://github.com/IngridJiang/Amalthea-acset
- **Use case**: Real-world model transformation scenarios

### Expected Output

#### Single-Variable Exploration (5 paths)
Explores one user choice with 5 possible values:

**Output:**
- `execution_paths_automatic.json` - 5 paths (user_choice ∈ {0,1,2,3,4})
- `galette-output-automatic-{0..4}/` - Generated models per path

#### Multi-Variable Exploration (25 paths)
Explores TWO user choices simultaneously (5 × 5 = 25 combinations):

**Output:**
- `execution_paths_multivar.json` - 25 paths (all combinations)
- `galette-output-multivar-{i}_{j}/` - Models for each (i,j) combination where i,j ∈ {0,1,2,3,4}

### Output Files

```
knarr-runtime/
├── execution_paths_automatic.json        # Single-variable: 5 paths
├── execution_paths_multivar.json         # Multi-variable: 25 paths
├── galette-output-automatic-*/           # Single-var models per path
└── galette-output-multivar-*_*/          # Multi-var models per path combination
    └── galette-test-output/
        └── vsum-output.xmi               # Synchronized Amalthea+ASCET models
```



### Consistency Preservation via Vitruvius

Vitruvius ensures **bi-directional consistency** between models using *Consistency Preservation Rules (CPRs)*:

- When a task is added to Amalthea → corresponding ASCET task is created automatically
- User choices during transformation → trigger different reaction rules
- Each path produces a **valid consistent state** of the model pair

### What CocoPath Adds

CocoPath augments Vitruvius with **systematic path exploration**:

1. **Symbolic Inputs**: User decisions treated as symbolic variables
2. **Automatic Path Generation**: Constraint solver generates all valid input combinations
3. **Coverage Guarantee**: Explores every possible decision path
4. **Artifact Generation**: Each path produces a complete, consistent model pair

**For each explored path, CocoPath generates:**
- Amalthea model (with chosen task configuration)
- Synchronized ASCET model (produced by Vitruvius reactions)
- Path constraints (logical formula describing this execution)
- Execution time (performance measurement)

This enables engineers to **systematically explore alternative consistency-preserving evolution scenarios** of the V-SUM


## License

See [LICENSE](../LICENSE) in the project root for Galette.

## Acknowledgements

- **Galette**: Northeastern University
- **GreenSolver**: University of Stellenbosch
- **Vitruvius**: KIT (Karlsruhe Institute of Technology)
- **Amalthea-ASCET**: Automotive model transformation Example from Bosch