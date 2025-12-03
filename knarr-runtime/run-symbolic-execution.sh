#!/bin/bash

# ============================================================================
# Galette-Knarr Symbolic Execution Runner
# ============================================================================
# This script runs automatic path exploration for Vitruvius model transformations.
# It uses the PathExplorer API to automatically generate test inputs by:
#   1. Executing transformations with concrete values
#   2. Collecting path constraints
#   3. Negating constraints to find unexplored paths
#   4. Solving for new inputs automatically
#
# Usage:
#   ./run-symbolic-execution.sh               # Interactive mode (prompts for choice)
#   ./run-symbolic-execution.sh --internal    # Single-variable mode (5 paths, simplified)
#   ./run-symbolic-execution.sh --external    # Single-variable mode (5 paths, full Vitruvius)
#   ./run-symbolic-execution.sh --multivar    # Multi-variable mode (25 paths, full Vitruvius)
#
# ============================================================================
# CONFIGURATION - Modify this section for your PC
# ============================================================================
# If using EXTERNAL or MULTIVAR mode, update EXTERNAL_PATH to point to your
# Amalthea-acset repository location.
#
# Example paths:
#   Linux:   EXTERNAL_PATH="/home/username/Amalthea-acset"
#   macOS:   EXTERNAL_PATH="/Users/username/Amalthea-acset"
#   Windows: EXTERNAL_PATH="C:/Users/username/Amalthea-acset"
#   WSL:     EXTERNAL_PATH="/mnt/c/Users/username/Amalthea-acset"
#
# Or specify dynamically:
#   ./run-symbolic-execution.sh --external --external-path /path/to/Amalthea-acset
# ============================================================================

set -e

USE_EXTERNAL=false
USE_MULTIVAR=false
EXTERNAL_PATH="C:/Users/10239/Amathea-acset"  # <-- MODIFY THIS for your PC
INTERACTIVE_MODE=true

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --external|-e)
            USE_EXTERNAL=true
            INTERACTIVE_MODE=false
            shift
            ;;
        --internal|-i)
            USE_EXTERNAL=false
            INTERACTIVE_MODE=false
            shift
            ;;
        --multivar|-m)
            USE_EXTERNAL=true
            USE_MULTIVAR=true
            INTERACTIVE_MODE=false
            shift
            ;;
        --external-path)
            EXTERNAL_PATH="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [--internal|--external|--multivar] [--external-path PATH]"
            exit 1
            ;;
    esac
done

echo "================================================================================"
echo "CocoPath"
echo "================================================================================"
echo ""

# Interactive mode selection if no flag provided
if [ "$INTERACTIVE_MODE" = true ]; then
    echo "Please select execution mode:"
    echo ""
    echo "  1) INTERNAL MODE (Fast, simplified stub - single variable)"
    echo "     - Output: Basic XMI stubs"
    echo "     - Explores: 5 paths (one user choice)"
    echo "     - No external repository needed"
    echo ""
    echo "  2) EXTERNAL MODE (Full Vitruvius transformations - single variable)"
    echo "     - Output: Complete Vitruvius reactions & transformations"
    echo "     - Explores: 5 paths (one user choice)"
    echo "     - Requires external Amalthea-acset repository"
    echo ""
    echo "  3) MULTI-VARIABLE MODE (Full Vitruvius - TWO user choices)"
    echo "     - Output: Complete Vitruvius reactions & transformations"
    echo "     - Explores: 25 paths (5 × 5 combinations)"
    echo "     - Requires external Amalthea-acset repository"
    echo ""
    read -p "Enter your choice (1, 2, or 3): " choice
    echo ""

    case $choice in
        1)
            USE_EXTERNAL=false
            USE_MULTIVAR=false
            echo "Selected: INTERNAL MODE (single variable)"
            ;;
        2)
            USE_EXTERNAL=true
            USE_MULTIVAR=false
            echo "Selected: EXTERNAL MODE (single variable)"
            ;;
        3)
            USE_EXTERNAL=true
            USE_MULTIVAR=true
            echo "Selected: MULTI-VARIABLE MODE (two variables, 25 paths)"
            ;;
        *)
            echo "Invalid choice. Defaulting to INTERNAL MODE."
            USE_EXTERNAL=false
            USE_MULTIVAR=false
            ;;
    esac
    echo ""
fi

echo "================================================================================"
echo ""

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

if [ "$USE_EXTERNAL" = true ]; then
    echo "Mode: EXTERNAL (switching to external Amalthea-acset)"
    echo ""

    # Verify external path exists
    if [ ! -d "$EXTERNAL_PATH" ]; then
        echo "ERROR: External Amalthea-acset not found at: $EXTERNAL_PATH"
        echo "Please check the path"
        exit 1
    fi

    echo "[1/4] Building external Amalthea-acset at $EXTERNAL_PATH..."
    (cd "$EXTERNAL_PATH" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
    echo "      Done."
    echo ""

    echo "[2/4] Temporarily switching to external dependency..."
    # Use Python script to safely switch dependencies
    # Test each Python command to ensure it actually works (not just a stub)
    PYTHON_CMD=""
    if command -v python.exe &> /dev/null && python.exe --version &> /dev/null; then
        PYTHON_CMD="python.exe"
    elif command -v python3 &> /dev/null && python3 --version &> /dev/null; then
        PYTHON_CMD="python3"
    elif command -v python &> /dev/null && python --version &> /dev/null; then
        PYTHON_CMD="python"
    else
        echo "ERROR: Python not found. Cannot switch dependencies."
        exit 1
    fi

    $PYTHON_CMD switch-dependency.py external pom.xml
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to switch to external dependency"
        exit 1
    fi
    echo "      Switched to external dependency."
    echo ""

    STEP_OFFSET=2
else
    echo "Mode: INTERNAL (using amalthea-acset-integration module)"
    echo "      Note: Requires external Amalthea-acset built once for Vitruvius dependencies"
    echo ""

    # Check if Vitruvius dependencies are available
    if [ ! -d "$HOME/.m2/repository/tools/vitruv/tools.vitruv.methodologisttemplate.vsum" ]; then
        echo "WARNING: Vitruvius VSUM dependency not found in Maven repository"
        echo "         Building external Amalthea-acset to install it..."
        echo ""

        if [ -d "$EXTERNAL_PATH" ]; then
            (cd "$EXTERNAL_PATH" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
            if [ $? -ne 0 ]; then
                echo "ERROR: Failed to build external Amalthea-acset"
                exit 1
            fi
            echo "      Done. Vitruvius dependencies installed."
            echo ""
        else
            echo "ERROR: External Amalthea-acset not found at: $EXTERNAL_PATH"
            echo "       Please build it first or specify path with --external-path"
            exit 1
        fi
    fi

    echo "[1/4] Switching to internal dependency..."
    # Use Python script to safely switch dependencies
    # Test each Python command to ensure it actually works (not just a stub)
    PYTHON_CMD=""
    if command -v python.exe &> /dev/null && python.exe --version &> /dev/null; then
        PYTHON_CMD="python.exe"
    elif command -v python3 &> /dev/null && python3 --version &> /dev/null; then
        PYTHON_CMD="python3"
    elif command -v python &> /dev/null && python --version &> /dev/null; then
        PYTHON_CMD="python"
    else
        echo "ERROR: Python not found. Cannot switch dependencies."
        exit 1
    fi

    $PYTHON_CMD switch-dependency.py internal pom.xml
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to switch to internal dependency"
        exit 1
    fi
    echo "      Switched to internal dependency."
    echo ""

    echo "[2/4] Building internal amalthea-acset-integration..."
    (cd "$(dirname "$SCRIPT_DIR")/amalthea-acset-integration" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
    echo "      Done."
    echo ""

    STEP_OFFSET=2
fi

STEP1=$((3 + STEP_OFFSET))
STEP2=$((4 + STEP_OFFSET))
TOTAL_STEPS=$((4 + STEP_OFFSET))

echo "[$STEP1/$TOTAL_STEPS] Cleaning previous outputs..."
rm -rf galette-output-* execution_paths.json 2>/dev/null || true
echo "      Done."
echo ""

echo "[$STEP2/$TOTAL_STEPS] Running symbolic execution..."
echo "      With manual constraint collection via PathUtils API"

# Determine which main class to use
if [ "$USE_MULTIVAR" = true ]; then
    MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"
    echo "      Main class: AutomaticVitruvMultiVarPathExploration (multi-variable)"
else
    MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"
    echo "      Main class: AutomaticVitruvPathExploration (single-variable)"
fi

# Note: Javaagent is not compatible with mvn exec:java
# We use manual constraint collection via PathUtils.addIntDomainConstraint() and addSwitchConstraint()
# If you need dynamic instrumentation, run the JAR directly instead of using exec:java

set +e
mvn exec:java -Dexec.mainClass="$MAIN_CLASS" -Dcheckstyle.skip=true
MVN_EXIT=$?
set -e

if [ $MVN_EXIT -ne 0 ]; then
    echo ""
    echo "WARNING: Maven execution had errors"
fi

# Restore pom.xml from backup
if [ -f "pom.xml.bak" ]; then
    echo ""
    echo "Restoring pom.xml from backup..."
    mv pom.xml.bak pom.xml 2>/dev/null || true
    echo "      Done."
fi

if [ ! -f "execution_paths_automatic.json" ]; then
    if [ $MVN_EXIT -ne 0 ]; then
        echo ""
        echo "ERROR: Symbolic execution failed!"
        exit 1
    fi
fi

echo ""
echo "================================================================================"
echo "Completed."
echo "================================================================================"
echo ""
if [ "$USE_MULTIVAR" = true ]; then
    echo "Generated files:"
    echo "  - execution_paths_multivar.json       (Path exploration results)"
    echo "  - galette-output-multivar-*/          (Model outputs per path combination)"
    echo ""
    echo "Multi-variable exploration:"
    echo "  - Variables: user_choice_1, user_choice_2"
    echo "  - Expected paths: 5 × 5 = 25"
    echo ""
else
    echo "Generated files:"
    echo "  - execution_paths_automatic.json      (Path exploration results)"
    echo "  - galette-output-automatic-*/         (Model outputs per path)"
    echo ""
fi
