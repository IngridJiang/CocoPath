# How to Run Symbolic Execution

## Quick Start

### First Time Setup (One-Time Only)

**Important:** This project requires Vitruvius framework dependencies (version 3.1.2) which aren't in public Maven repositories.

#### If You DON'T Have External Amathea-acset

The project is currently configured to use **external mode** by default, which requires the external Amathea-acset repository. If you don't have it:

**Option 1: Skip Vitruvius Integration (Run Galette Tests Only)**
```bash
cd galette-vitruv
mvn test -pl galette-agent,galette-integration-tests
```
This runs the core Galette symbolic execution tests without Vitruvius.

**Option 2: Use Pre-installed Vitruvius Dependencies**
If someone already built Amathea-acset on this machine, Vitruvius jars may already be in `~/.m2/repository/tools/vitruv/`. Check:
```bash
ls ~/.m2/repository/tools/vitruv/tools.vitruv.framework.vsum/3.1.2/
```
If the jar exists, you can run symbolic execution directly (see "Running Symbolic Execution" below).

**Option 3: Get the External Amathea-acset Repository**
Contact the project maintainer for access to the external Amathea-acset repository, then install dependencies:
```bash
cd /path/to/Amathea-acset
mvn clean install -DskipTests -Dcheckstyle.skip=true
```
This installs Vitruvius artifacts to `~/.m2/repository/`. **You only need to do this once.**

### Running Symbolic Execution

After the one-time setup, you can run symbolic execution in **interactive mode** (recommended) or with command-line arguments.

#### Interactive Mode (Recommended)

Simply run the script without arguments and you'll be prompted to choose:

**Windows (PowerShell):**
```powershell
cd C:\Users\10239\galette-vitruv\knarr-runtime
.\run-symbolic-execution.ps1
```

**Linux/WSL (Bash):**
```bash
cd /path/to/galette-vitruv/knarr-runtime
./run-symbolic-execution.sh
```

**Windows (Batch):**
```cmd
cd C:\Users\10239\galette-vitruv\knarr-runtime
run-symbolic-execution.bat
```

You'll see this prompt:

```
Please select execution mode:

  1) INTERNAL MODE (Fast, simplified stub)
     - Execution time: ~2-5ms per path
     - Output: Basic XMI stubs
     - No external repository needed

  2) EXTERNAL MODE (Full Vitruvius transformations)
     - Execution time: ~26-45ms per path
     - Output: Complete Vitruvius reactions & transformations
     - Requires external Amathea-acset repository

Enter your choice (1 or 2):
```

#### Command-Line Mode

You can also specify the mode directly:

**PowerShell:**
```powershell
.\run-symbolic-execution.ps1 -Internal    # Force internal mode
.\run-symbolic-execution.ps1 -UseExternal # Force external mode
```

**Bash:**
```bash
./run-symbolic-execution.sh --internal    # Force internal mode
./run-symbolic-execution.sh --external    # Force external mode
```

**Batch:**
```cmd
run-symbolic-execution.bat internal       # Force internal mode
run-symbolic-execution.bat external       # Force external mode
```

## Understanding Internal vs External Modes

The scripts support two execution modes with different characteristics:

### Internal Mode (Option 1)

**What it is:**
- Uses the simplified `amathea-acset-integration` module built within this repository
- Provides a **stub implementation** of Vitruvius transformations
- Creates basic XMI output files without full transformation logic

**Performance:**
- **Speed:** ~2-5ms per execution path (very fast)
- **Why fast:** Just writes simple XML stubs, no complex transformations

**Output:**
- Basic XMI files with task type markers
- Shows which path was explored (e.g., InterruptTask, PeriodicTask)
- Does **NOT** include full Vitruvius reaction code or complete model transformations

**When to use:**
- Quick testing and debugging of symbolic execution logic
- Verifying path exploration works correctly
- You don't have access to the external Amathea-acset repository
- You want fast iteration times

**Requirements:**
- Vitruvius framework dependencies (v3.1.2) must be installed once
- No external repository needed for execution

### External Mode (Option 2)

**What it is:**
- Uses the full `Amathea-acset` repository from `C:\Users\10239\Amathea-acset`
- Provides **complete Vitruvius transformations** with generated reaction code
- Executes real model-to-model transformations (Amathea → ASCET)

**Performance:**
- **Speed:** ~26-45ms per execution path (slower but complete)
- **Why slower:** Performs full VSUM initialization, reaction execution, and model transformations

**Output:**
- Complete XMI files with full metamodel transformations
- Generated reaction code execution (from `.reactions` files)
- Real Vitruvius VSUM state with bidirectional transformations
- Includes ComponentContainer → Task mappings with all attributes

**When to use:**
- Testing actual model transformations
- Verifying Vitruvius reaction behavior under symbolic execution
- Need complete output for downstream tools
- Research on model-driven engineering with symbolic execution

**Requirements:**
- External Amathea-acset repository must be built at least once
- More disk space and build time required

### Comparison Table

| Feature | Internal Mode | External Mode |
|---------|---------------|---------------|
| **Speed** | ~2-5ms/path | ~26-45ms/path |
| **Output** | Basic XMI stubs | Complete transformations |
| **External repo needed** | No (after initial setup) | Yes |
| **Build time** | Fast (~30s) | Slower (~2-5min) |
| **Disk space** | Small | Larger |
| **Vitruvius reactions** | No | Yes (full generated code) |
| **Use case** | Testing, debugging | Production, research |

## What the Script Does

Regardless of mode, the script:

1. Compiles `knarr-runtime` module (or external Amathea-acset if using external mode)
2. Runs symbolic execution on Amathea↔ASCET model transformation
3. Explores 5 paths (user choices 0-4: InterruptTask, PeriodicTask, SoftwareTask, TimeTableTask, Decide Later)
4. Collects path constraints (e.g., `user_choice == 0`)
5. Generates model outputs for each path
6. Creates visualization PNGs and JSON data

The difference is in **how step 1 and 2 are executed** and **what the output contains**.

## Expected Outputs

All files are regenerated with fresh data every run:

```
knarr-runtime/
├── execution_paths.json       ← Fresh JSON data
├── execution_summary.txt      ← Fresh summary
├── execution_tree.png         ← Updated PNG
├── performance_chart.png      ← Updated PNG
├── workflow_diagram.png       ← Updated PNG
└── galette-output-0/ to 4/    ← Fresh model outputs
```

## Troubleshooting

### "Could not resolve dependencies for tools.vitruv"

**Problem:** Vitruvius dependencies not installed

**Solution:**
1. Check if Vitruvius jars exist: `ls ~/.m2/repository/tools/vitruv/tools.vitruv.framework.vsum/3.1.2/`
2. If missing, you need the external Amathea-acset repository (see "First Time Setup" above)
3. If you have access to it: `cd /path/to/Amathea-acset && mvn clean install -DskipTests -Dcheckstyle.skip=true`

### "Could not resolve dependencies for edu.gmu.swe.greensolver:green"

**Problem:** Green solver dependency missing

**Solution:** This is expected and should only be a warning. Green solver is bundled in the local Maven repo. If builds fail:
```bash
# Check if Green exists
ls ~/.m2/repository/edu/gmu/swe/greensolver/green/1.0-SNAPSHOT/green-1.0-SNAPSHOT.jar

# If missing, the jar needs to be installed manually (contact maintainer)
```

### "execution_paths.json not updated"

**Problem:** Maven build failed, symbolic execution didn't run

**Solution:** Check Maven output for errors. Most likely missing Vitruvius deps (see above).

### "Visualizations not generated"

**Problem:** Python or matplotlib missing

**Solution:**
```bash
pip install matplotlib networkx
```

### "Build failed on internal module"

**Problem:** Internal module can't find Vitruvius deps

**Solution:**
1. Check if Vitruvius dependencies are installed (see "Could not resolve dependencies for tools.vitruv" above)
2. If you don't have external Amathea-acset, the internal mode won't work - see "First Time Setup" for alternatives

### "Package mir.reactions.amalthea2ascet does not exist"

**Problem:** This error occurred in older versions when trying to build internal mode with full reactions

**Solution:** The internal mode now uses a simplified stub (`Test.java`) that doesn't require generated reaction code. This error should no longer occur.

## Architecture

```
Internal Mode (CURRENT DEFAULT):
  galette-vitruv/amathea-acset-integration/  ← Simplified Vitruvius stub
  ~/.m2/repository/tools/vitruv/...          ← Vitruvius dependencies (v3.1.2)
  ~/.m2/repository/edu/gmu/swe/greensolver/  ← Green solver (v1.0-SNAPSHOT)
  knarr-runtime/                             ← Galette symbolic execution wrapper

  Note: Creates basic output stubs, not full Vitruvius reactions

External Mode (FOR FULL REACTIONS):
  C:\Users\10239\Amathea-acset/              ← Full Vitruvius code + reactions
  ~/.m2/repository/tools/vitruv/...          ← Same Vitruvius dependencies (v3.1.2)
  knarr-runtime/                             ← Galette symbolic execution wrapper

  Note: Includes generated reaction code and complete transformations
```

## Current Configuration

The project now supports **interactive mode selection** when you run the scripts:
- Scripts will **prompt you** to choose between internal (1) or external (2) mode
- You can also force a specific mode using command-line arguments (see "Running Symbolic Execution" above)

Both modes:
- Use the same Vitruvius framework dependencies (v3.1.2)
- Explore all 5 symbolic paths
- Generate visualizations and analysis
- Produce the same JSON output structure (difference is in XMI content)

**Default behavior:** If you run the script without arguments, it enters interactive mode and asks you to choose.

**Configuration Location:** `knarr-runtime/pom.xml` (lines 60-74) - scripts automatically switch between internal/external dependencies

## For New Users Without External Repository

If you're a new user without access to the external Amathea-acset repository:

1. **Core Galette Features**: You can still use Galette's core symbolic execution on other Java projects
2. **Vitruvius Integration**: Requires external dependencies - see "First Time Setup" above
3. **Testing**: Run `mvn test -pl galette-agent` to verify core Galette functionality works

**Contact**: For access to external Amathea-acset or pre-built Vitruvius dependencies, contact the project maintainer.
