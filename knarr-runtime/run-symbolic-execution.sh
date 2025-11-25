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
#   ./run-symbolic-execution.sh --internal    # Fast mode (2-5ms/path, simplified)
#   ./run-symbolic-execution.sh --external    # Full mode (26-45ms/path, complete Vitruvius)
# ============================================================================

set -e

USE_EXTERNAL=false
EXTERNAL_PATH="C:/Users/10239/Amathea-acset"
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
        --external-path)
            EXTERNAL_PATH="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [--internal|--external] [--external-path PATH]"
            exit 1
            ;;
    esac
done

echo "================================================================================"
echo "GALETTE/KNARR SYMBOLIC EXECUTION WITH VITRUVIUS FRAMEWORK"
echo "================================================================================"
echo ""

# Interactive mode selection if no flag provided
if [ "$INTERACTIVE_MODE" = true ]; then
    echo "Please select execution mode:"
    echo ""
    echo "  1) INTERNAL MODE (Fast, simplified stub)"
    echo "     - Execution time: ~2-5ms per path"
    echo "     - Output: Basic XMI stubs"
    echo "     - No external repository needed"
    echo ""
    echo "  2) EXTERNAL MODE (Full Vitruvius transformations)"
    echo "     - Execution time: ~26-45ms per path"
    echo "     - Output: Complete Vitruvius reactions & transformations"
    echo "     - Requires external Amathea-acset repository"
    echo ""
    read -p "Enter your choice (1 or 2): " choice
    echo ""

    case $choice in
        1)
            USE_EXTERNAL=false
            echo "Selected: INTERNAL MODE"
            ;;
        2)
            USE_EXTERNAL=true
            echo "Selected: EXTERNAL MODE"
            ;;
        *)
            echo "Invalid choice. Defaulting to INTERNAL MODE."
            USE_EXTERNAL=false
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
    echo "Mode: EXTERNAL (switching to external Amathea-acset)"
    echo ""

    # Verify external path exists
    if [ ! -d "$EXTERNAL_PATH" ]; then
        echo "ERROR: External Amathea-acset not found at: $EXTERNAL_PATH"
        echo "Please check the path"
        exit 1
    fi

    echo "[1/4] Building external Amathea-acset at $EXTERNAL_PATH..."
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
    echo "Mode: INTERNAL (using amathea-acset-integration module)"
    echo "      Note: Requires external Amathea-acset built once for Vitruvius dependencies"
    echo ""

    # Check if Vitruvius dependencies are available
    if [ ! -d "$HOME/.m2/repository/tools/vitruv/tools.vitruv.methodologisttemplate.vsum" ]; then
        echo "WARNING: Vitruvius VSUM dependency not found in Maven repository"
        echo "         Building external Amathea-acset to install it..."
        echo ""

        if [ -d "$EXTERNAL_PATH" ]; then
            (cd "$EXTERNAL_PATH" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
            if [ $? -ne 0 ]; then
                echo "ERROR: Failed to build external Amathea-acset"
                exit 1
            fi
            echo "      Done. Vitruvius dependencies installed."
            echo ""
        else
            echo "ERROR: External Amathea-acset not found at: $EXTERNAL_PATH"
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

    echo "[2/4] Building internal amathea-acset-integration..."
    (cd "$(dirname "$SCRIPT_DIR")/amathea-acset-integration" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
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
set +e
mvn exec:java -Dcheckstyle.skip=true
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

echo ""
echo "[OPTIONAL] Generating visualizations..."

if [ -f "execution_paths.json" ]; then
    PYTHON_CMD=""
    if command -v python.exe &> /dev/null && python.exe --version &> /dev/null; then
        PYTHON_CMD="python.exe"
    elif command -v python &> /dev/null && python --version &> /dev/null; then
        PYTHON_CMD="python"
    elif command -v python3 &> /dev/null && python3 --version &> /dev/null; then
        PYTHON_CMD="python3"
    fi

    if [ -n "$PYTHON_CMD" ]; then
        echo "  Running visualize_results.py..."
        if $PYTHON_CMD visualize_results.py; then
            echo "  ✓ Results visualizations generated"
        else
            echo "  ✗ Failed to generate results visualizations (see errors above)"
        fi

        echo "  Running visualize_workflow.py..."
        if $PYTHON_CMD visualize_workflow.py; then
            echo "  ✓ Workflow diagram generated"
        else
            echo "  ✗ Failed to generate workflow diagram (see errors above)"
        fi
    else
        echo "  ✗ Python not found - skipping visualizations"
    fi
else
    echo "  ✗ No execution_paths.json found - skipping visualizations"
    if [ $MVN_EXIT -ne 0 ]; then
        echo ""
        echo "ERROR: Symbolic execution failed!"
        exit 1
    fi
fi

echo ""
echo "================================================================================"
echo "SUCCESS! Symbolic execution completed."
echo "================================================================================"
echo ""
echo "Generated files:"
echo "  - execution_paths.json       (JSON data)"
echo "  - execution_summary.txt      (Text summary)"
echo "  - execution_tree.png         (Execution tree diagram)"
echo "  - performance_chart.png      (Performance charts)"
echo "  - workflow_diagram.png       (Workflow diagram)"
echo "  - galette-output-0/ to galette-output-4/ (Model outputs)"
echo ""
