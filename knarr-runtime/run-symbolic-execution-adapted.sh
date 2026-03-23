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
#   ./run-symbolic-execution.sh --brake       # TinyBrake single-disc (2 vars: profile+calib)
#   ./run-symbolic-execution.sh --brake-multivar  # TinyBrake two-disc (4 vars)
#   ./run-symbolic-execution.sh --force-rebuild  # Force rebuild of Amalthea-acset even if sources unchanged
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
_LAST_ERR=""
trap '_LAST_ERR="line $LINENO: $BASH_COMMAND"' ERR
trap 'code=$?; if [ -f "pom.xml.bak" ]; then echo ""; echo "Restoring pom.xml from backup (trap)..."; mv pom.xml.bak pom.xml 2>/dev/null || true; fi; if [ $code -ne 0 ]; then echo ""; echo "=== Script exited with error (code $code) ==="; [ -n "$_LAST_ERR" ] && echo "Failed at: $_LAST_ERR"; read -rp "Press Enter to close..."; fi' EXIT

USE_EXTERNAL=false
USE_MULTIVAR=false
USE_BRAKE=false
USE_BRAKE_MULTIVAR=false
USE_INTERCEPTION=false
FORCE_REBUILD=false
EXTERNAL_PATH="/c/Users/10239/Amathea-acset"  # <-- MODIFY THIS for your PC
INTERACTIVE_MODE=true

# ============================================================================
# JAVA VERSION CONFIGURATION
# ============================================================================
# Set JAVA_HOME to use a specific Java version. The script requires Java 17+
# Modify this to point to your Java 17 installation, or leave empty to use
# the system default. Common locations:
#   /usr/lib/jvm/java-17-openjdk
#   /usr/lib/jvm/java-17-openjdk-amd64
#   /opt/java/openjdk-17
#   $JAVA_HOME (uses system JAVA_HOME if set)
#
# If you have multiple Java versions, explicitly set it here:
JAVA_HOME_OVERRIDE="/usr/lib/jvm/java-17-openjdk-amd64"  # Using Java 17 for compatibility

# Auto-detect Java 17 if not explicitly set
if [ -z "$JAVA_HOME_OVERRIDE" ]; then
    # Try to find Java 17 in common locations
    for java_path in \
        "/usr/lib/jvm/java-17-openjdk-amd64" \
        "/usr/lib/jvm/java-17-openjdk" \
        "/usr/libexec/java_home -v 17" \
        "/opt/java/openjdk-17" \
        "$JAVA_HOME"; do
        if [ -n "$java_path" ] && [ -x "$java_path/bin/java" ] 2>/dev/null; then
            JAVA_HOME_OVERRIDE="$java_path"
            break
        fi
    done
fi

# Set JAVA_HOME if we found a valid path
if [ -n "$JAVA_HOME_OVERRIDE" ] && [ -x "$JAVA_HOME_OVERRIDE/bin/java" ]; then
    export JAVA_HOME="$JAVA_HOME_OVERRIDE"
    export PATH="$JAVA_HOME/bin:$PATH"
    JAVA_VERSION=$("$JAVA_HOME/bin/java" -version 2>&1 | grep -oP 'version "\K[^"]*' || echo "unknown")
    echo "Using Java: $JAVA_VERSION from $JAVA_HOME"
    echo ""
else
    # Use system default
    JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]*' || echo "unknown")
    echo "Using system Java: $JAVA_VERSION"
    echo ""
fi



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
        --brake|-b)
            USE_BRAKE=true
            USE_BRAKE_MULTIVAR=false
            INTERACTIVE_MODE=false
            shift
            ;;
        --brake-multivar|--bm)
            USE_BRAKE=true
            USE_BRAKE_MULTIVAR=true
            INTERACTIVE_MODE=false
            shift
            ;;
        --force-rebuild|-f)
            FORCE_REBUILD=true
            shift
            ;;
        --interception)
            USE_INTERCEPTION=true
            shift
            ;;
        --external-path)
            EXTERNAL_PATH="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [--internal|--external|--multivar|--brake|--brake-multivar] [--interception] [--external-path PATH] [--force-rebuild]"
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
    echo "  4) BRAKE MODE (TinyBrakeVSUM - single disc, 2 symbolic variables)"
    echo "     - Output: BrakeSystem/ControlSystem XMI models"
    echo "     - Explores: up to 10 paths (4 profile intervals × 3 calib intervals; skip has no calib)"
    echo "     - Uses tinybrake-integration module (no external repo needed)"
    echo ""
    echo "  5) BRAKE MULTI-VARIABLE MODE (TinyBrakeVSUM - two discs, 4 symbolic variables)"
    echo "     - Output: BrakeSystem/ControlSystem XMI models"
    echo "     - Explores: 81 paths (3^4: 3 profiles x 3 calibs per disc, 2 discs; skip excluded by initial [0,0,0,0])"
    echo "     - Uses tinybrake-integration module (no external repo needed)"
    echo ""
    read -p "Enter your choice (1-5): " choice
    echo ""

    case $choice in
        1)
            USE_EXTERNAL=false
            USE_MULTIVAR=false
            USE_BRAKE=false
            echo "Selected: INTERNAL MODE (single variable)"
            ;;
        2)
            USE_EXTERNAL=true
            USE_MULTIVAR=false
            USE_BRAKE=false
            echo "Selected: EXTERNAL MODE (single variable)"
            ;;
        3)
            USE_EXTERNAL=true
            USE_MULTIVAR=true
            USE_BRAKE=false
            echo "Selected: MULTI-VARIABLE MODE (two variables, 25 paths)"
            ;;
        4)
            USE_BRAKE=true
            USE_BRAKE_MULTIVAR=false
            echo "Selected: BRAKE MODE (single disc, 2 variables)"
            ;;
        5)
            USE_BRAKE=true
            USE_BRAKE_MULTIVAR=true
            echo "Selected: BRAKE MULTI-VARIABLE MODE (two discs, 4 variables)"
            ;;
        *)
            echo "Invalid choice. Defaulting to INTERNAL MODE."
            USE_EXTERNAL=false
            USE_MULTIVAR=false
            USE_BRAKE=false
            ;;
    esac
    echo ""
fi

echo "================================================================================"
echo ""

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# Function to check if sources in a directory have been modified since last build
check_sources_changed() {
    local src_dir="$1"
    local target_dir="$src_dir/target"
    
    if [ ! -d "$target_dir" ]; then
        # No target directory means it hasn't been built yet
        return 0  # Sources "changed" (first build)
    fi
    
    # Get the most recent modification time of target directory (Linux uses -c%Y)
    local target_mtime=$(stat -c%Y "$target_dir" 2>/dev/null || echo 0)

    # Get the most recent modification time in src directory
    local src_mtime=$(find "$src_dir/src" -type f -printf '%T@\n' 2>/dev/null | sort -rn | head -1 || echo 0)

    # Also check pom.xml
    local pom_mtime=$(stat -c%Y "$src_dir/pom.xml" 2>/dev/null || echo 0)
    pom_mtime=${pom_mtime:-0}
    
    # Get the maximum of src and pom modification times
    local max_source_mtime=$src_mtime
    if [ "$pom_mtime" -gt "$max_source_mtime" ]; then
        max_source_mtime=$pom_mtime
    fi
    
    # If any source is newer than target, sources have changed
    if [ "$max_source_mtime" -gt "$target_mtime" ]; then
        return 0  # Sources have changed
    else
        return 1  # Sources have NOT changed
    fi
}

if [ "$USE_BRAKE" = true ]; then
    echo "Mode: BRAKE (switching to tinybrake-integration)"
    echo ""

    # Find Python
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

    # Clean Maven cache for tinybrake to avoid stale JARs
    echo "      Cleaning Maven repository cache..."
    rm -rf "$HOME/.m2/repository/edu/neu/ccs/prl/galette/tinybrake-integration-consistency/1.0.0-SNAPSHOT/" 2>/dev/null || true
    rm -rf "$HOME/.m2/repository/edu/neu/ccs/prl/galette/tinybrake-integration-vsum/1.0.0-SNAPSHOT/" 2>/dev/null || true
    echo "      Removed cached tinybrake JARs"
    echo ""

    echo "[1/4] Switching pom.xml to brake dependency..."
    $PYTHON_CMD switch-dependency.py brake pom.xml
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to switch to brake dependency"
        exit 1
    fi
    echo "      Switched to brake dependency."
    echo ""

    # Build tinybrake-integration module
    BRAKE_DIR="$(dirname "$SCRIPT_DIR")/tinybrake-integration"
    if [ "$FORCE_REBUILD" = true ]; then
        echo "[2/4] Building tinybrake-integration (--force-rebuild flag set)..."
        (cd "$BRAKE_DIR" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
    elif check_sources_changed "$BRAKE_DIR"; then
        echo "[2/4] Building tinybrake-integration (sources have changed)..."
        (cd "$BRAKE_DIR" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
    else
        echo "[2/4] Skipping tinybrake-integration build (sources unchanged)..."
    fi
    echo "      Done."
    echo ""

    STEP_OFFSET=2

elif [ "$USE_EXTERNAL" = true ]; then
    echo "Mode: EXTERNAL (switching to external Amalthea-acset)"
    echo ""

    # Verify external path exists
    if [ ! -d "$EXTERNAL_PATH" ]; then
        echo "ERROR: External Amalthea-acset not found at: $EXTERNAL_PATH"
        echo "Please check the path"
        exit 1
    fi

    # Clean Maven repository to avoid stale JAR issues
    echo "      Cleaning Maven repository cache..."
    rm -rf "$HOME/.m2/repository/tools/vitruv/tools.vitruv.methodologisttemplate.consistency/0.1.0-SNAPSHOT/" 2>/dev/null || true
    echo "      Removed cached external consistency JAR"

    # Check if rebuild is needed
    if [ "$FORCE_REBUILD" = true ]; then
        echo "[1/4] Building external Amalthea-acset (--force-rebuild flag set)..."
        (cd "$EXTERNAL_PATH" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
    elif check_sources_changed "$EXTERNAL_PATH"; then
        echo "[1/4] Building external Amalthea-acset (sources have changed)..."
        (cd "$EXTERNAL_PATH" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
    else
        echo "[1/4] Skipping external Amalthea-acset build (sources unchanged)..."
    fi
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

    # Clean Maven repository to avoid stale JAR issues
    echo "      Cleaning Maven repository cache..."
    rm -rf "$HOME/.m2/repository/edu/neu/ccs/prl/galette/amalthea-acset-consistency/1.0.0-SNAPSHOT/" 2>/dev/null || true
    echo "      Removed cached internal consistency JAR"
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

    # Check if rebuild is needed for internal module
    INTERNAL_DIR="$(dirname "$SCRIPT_DIR")/amalthea-acset-integration"
    if [ "$FORCE_REBUILD" = true ]; then
        echo "[2/4] Building internal amalthea-acset-integration (--force-rebuild flag set)..."
        (cd "$INTERNAL_DIR" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
    elif check_sources_changed "$INTERNAL_DIR"; then
        echo "[2/4] Building internal amalthea-acset-integration (sources have changed)..."
        (cd "$INTERNAL_DIR" && mvn clean install -DskipTests -Dcheckstyle.skip=true)
    else
        echo "[2/4] Skipping internal amalthea-acset-integration build (sources unchanged)..."
    fi
    echo "      Done."
    echo ""

    STEP_OFFSET=2
fi

STEP1=$((3 + STEP_OFFSET))
STEP2=$((4 + STEP_OFFSET))
TOTAL_STEPS=$((4 + STEP_OFFSET))

echo "[$STEP1/$TOTAL_STEPS] Cleaning previous outputs..."
rm -rf galette-output-* execution_paths*.json 2>/dev/null || true
echo "      Done."
echo ""

echo "[$STEP2/$TOTAL_STEPS] Running symbolic execution..."
echo "      With semi-automatic constraint collection in reaction"

# Determine which main class to use
if [ "$USE_BRAKE" = true ] && [ "$USE_BRAKE_MULTIVAR" = true ]; then
    MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticBrakeMultiVarPathExploration"
    echo "      Main class: AutomaticBrakeMultiVarPathExploration (brake two-disc)"
elif [ "$USE_BRAKE" = true ]; then
    MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticBrakePathExploration"
    echo "      Main class: AutomaticBrakePathExploration (brake single-disc)"
elif [ "$USE_MULTIVAR" = true ]; then
    MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"
    echo "      Main class: AutomaticVitruvMultiVarPathExploration (multi-variable)"
else
    MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"
    echo "      Main class: AutomaticVitruvPathExploration (single-variable)"
fi

# Build the project first to ensure everything is compiled
echo "      Building project..."
mvn compile -U -Dcheckstyle.skip=true -q

# Check if instrumented Java is available
INSTRUMENTED_JAVA="target/galette/java"
if [ ! -x "$INSTRUMENTED_JAVA/bin/java" ]; then
    echo "ERROR: Instrumented Java not found. Building it now..."
    mvn process-test-resources -U -Dcheckstyle.skip=true -q
fi

# Resolve Galette agent
GALETTE_AGENT=""
if [ -f "../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar" ]; then
    GALETTE_AGENT="../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar"
elif [ -f "$HOME/.m2/repository/edu/neu/ccs/prl/galette/galette-agent/1.0.0-SNAPSHOT/galette-agent-1.0.0-SNAPSHOT.jar" ]; then
    GALETTE_AGENT="$HOME/.m2/repository/edu/neu/ccs/prl/galette/galette-agent/1.0.0-SNAPSHOT/galette-agent-1.0.0-SNAPSHOT.jar"
else
    echo "ERROR: Galette agent jar not found"
    exit 1
fi

# Build classpath
mvn -q -DincludeScope=runtime -Dmdep.outputFile=cp.txt dependency:build-classpath
# Use ; as classpath separator on Windows (Git Bash/MSYS), : on Unix
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OS" == "Windows_NT" ]]; then
    CP_SEP=";"
else
    CP_SEP=":"
fi
CP="target/classes${CP_SEP}target/test-classes${CP_SEP}$(cat cp.txt)"

echo "      Using instrumented JVM with Galette agent"

# Clear stale Galette transformation cache entries for CocoPath classes
# (avoids VerifyError from cached instrumented bytecode when exclusions change)
rm -rf target/galette/cache/edu.neu.ccs.prl.galette.concolic.* 2>/dev/null || true

# Expression propagation flags: enable Knarr-style symbolic expression propagation
# for mir/ classes (tinybrake routines). TagPropagator will intercept IADD etc.
# and build compound Green expressions for branch predicates.
SYMBOLIC_FLAG="-Dgalette.symbolic.enabled=true -Dgalette.instrument.prefix=mir/"
echo "      Expression propagation ENABLED for mir/ classes"

# Native bytecode interception: intercepts comparison operations at bytecode level
if [ "$USE_INTERCEPTION" = true ]; then
    SYMBOLIC_FLAG="$SYMBOLIC_FLAG -Dgalette.concolic.interception.enabled=true -Dgalette.concolic.interception.debug=true -Dpath.explorer.debug=true"
    echo "      Native bytecode interception ENABLED"
fi

set +e
"$INSTRUMENTED_JAVA/bin/java" \
    -cp "$CP" \
    -Xbootclasspath/a:"$GALETTE_AGENT" \
    -javaagent:"$GALETTE_AGENT" \
    -Dgalette.cache=target/galette/cache \
    -Dpath.explorer.max.iterations=200 \
    ${SYMBOLIC_FLAG} \
    "$MAIN_CLASS"
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

if [ "$USE_BRAKE" = true ]; then
    EXPECTED_JSON="execution_paths_brake_multivar.json"
    [ "$USE_BRAKE_MULTIVAR" != true ] && EXPECTED_JSON="execution_paths_brake.json"
elif [ "$USE_MULTIVAR" = true ]; then
    EXPECTED_JSON="execution_paths_multivar.json"
else
    EXPECTED_JSON="execution_paths_automatic.json"
fi

if [ ! -f "$EXPECTED_JSON" ]; then
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
if [ "$USE_BRAKE" = true ] && [ "$USE_BRAKE_MULTIVAR" = true ]; then
    echo "Generated files:"
    echo "  - execution_paths_brake_multivar.json  (Path exploration results)"
    echo "  - galette-output-brake-multivar-*/     (Model outputs per path combination)"
    echo ""
elif [ "$USE_BRAKE" = true ]; then
    echo "Generated files:"
    echo "  - execution_paths_brake.json           (Path exploration results)"
    echo "  - galette-output-brake-*/              (Model outputs per path)"
    echo ""
elif [ "$USE_MULTIVAR" = true ]; then
    echo "Generated files:"
    echo "  - execution_paths_multivar.json       (Path exploration results)"
    echo "  - galette-output-multivar-*/          (Model outputs per path combination)"
    echo ""
else
    echo "Generated files:"
    echo "  - execution_paths_automatic.json      (Path exploration results)"
    echo "  - galette-output-automatic-*/         (Model outputs per path)"
    echo ""
fi
