# CoCoPath - Concolic Exploration of Consistency-Preserving Paths

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
│   └── run-symbolic-execution.sh                 # Execution scripts
│
├── amalthea-acset-integration/                   # Vitruvius case study
│   ├── vsum/src/main/java/.../Test.java         # Model transformation entry point
│   └── consistency/src/main/reactions/           # Consistency preservation rules
│
└── README.md                                      # This file
```

## Running CoCoPath

### Prerequisites

1. **Java 17** (OpenJDK recommended)
2. **Maven 3.6+**
3. **Python 3.x** (for dependency management scripts)
4. **Z3 Solver** (automatically configured)

### Quick Start

```bash
cd knarr-runtime

# Interactive mode - choose execution type
./run-symbolic-execution.sh

# Direct execution modes:
./run-symbolic-execution.sh --internal   # Simplified test case
./run-symbolic-execution.sh --external   # Full Vitruvius transformations
./run-symbolic-execution.sh --multivar   # Multi-variable exploration (25 paths)
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

### Execution Modes

#### Single-Variable Exploration
- Explores one symbolic user decision with 5 possible values
- Generates 5 execution paths
- Output: `execution_paths_automatic.json` and model files in `galette-output-automatic-{0..4}/`

#### Multi-Variable Exploration
- Explores TWO symbolic user decisions simultaneously
- Generates 25 execution paths (5 × 5 combinations)
- Output: `execution_paths_multivar.json` and models in `galette-output-multivar-{i}_{j}/`

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