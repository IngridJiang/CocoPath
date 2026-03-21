#!/bin/bash

# ============================================================================
# Galette-Knarr Symbolic Execution Runner with Full Instrumentation
# ============================================================================
# This is an enhanced version of run-symbolic-execution.sh that properly
# builds and uses instrumented Java with the Galette agent for full
# bytecode interception support.
#
# Key improvements over the original:
# - Builds galette-agent with ComparisonInterceptorVisitor
# - Creates and uses instrumented Java runtime
# - Runs with proper agent configuration
# - Supports bytecode-level comparison interception
#
# Usage:
#   ./run-symbolic-execution-instrumented.sh               # Interactive mode
#   ./run-symbolic-execution-instrumented.sh --internal    # Single-variable mode (5 paths, simplified)
#   ./run-symbolic-execution-instrumented.sh --external    # Single-variable mode (5 paths, full Vitruvius)
#   ./run-symbolic-execution-instrumented.sh --multivar    # Multi-variable mode (25 paths, full Vitruvius)
#   ./run-symbolic-execution-instrumented.sh --force-rebuild  # Force rebuild all components
#
# ============================================================================

set -e

USE_EXTERNAL=false
USE_MULTIVAR=false
FORCE_REBUILD=false
EXTERNAL_PATH="/home/anne/CocoPath/Amalthea-acset"
INTERACTIVE_MODE=true

# ============================================================================
# JAVA VERSION CONFIGURATION
# ============================================================================
export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using Java: $(java -version 2>&1 | head -1)"
echo ""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        --internal)
            USE_EXTERNAL=false
            USE_MULTIVAR=false
            INTERACTIVE_MODE=false
            shift
            ;;
        --external)
            USE_EXTERNAL=true
            USE_MULTIVAR=false
            INTERACTIVE_MODE=false
            shift
            ;;
        --multivar)
            USE_EXTERNAL=true
            USE_MULTIVAR=true
            INTERACTIVE_MODE=false
            shift
            ;;
        --external-path)
            EXTERNAL_PATH="$2"
            shift 2
            ;;
        --force-rebuild)
            FORCE_REBUILD=true
            shift
            ;;
        *)
            shift
            ;;
    esac
done

# Interactive mode selection
if [ "$INTERACTIVE_MODE" = true ]; then
    echo "================================================================================"
    echo "Galette-Knarr Symbolic Execution Runner (Instrumented Version)"
    echo "================================================================================"
    echo ""
    echo "Select execution mode:"
    echo "  1) INTERNAL - Single-variable mode with internal dependencies (5 paths)"
    echo "  2) EXTERNAL - Single-variable mode with external Amalthea-acset (5 paths)"
    echo "  3) MULTIVAR - Multi-variable mode with external dependencies (25 paths)"
    echo ""
    read -p "Enter choice (1-3): " choice

    case $choice in
        1)
            USE_EXTERNAL=false
            USE_MULTIVAR=false
            ;;
        2)
            USE_EXTERNAL=true
            USE_MULTIVAR=false
            ;;
        3)
            USE_EXTERNAL=true
            USE_MULTIVAR=true
            ;;
        *)
            echo "Invalid choice. Exiting."
            exit 1
            ;;
    esac
fi

echo ""
echo "================================================================================"
echo "Configuration"
echo "================================================================================"
echo "Mode: $([ "$USE_MULTIVAR" = true ] && echo "MULTIVAR" || ([ "$USE_EXTERNAL" = true ] && echo "EXTERNAL" || echo "INTERNAL"))"
echo "External path: $EXTERNAL_PATH"
echo "Force rebuild: $FORCE_REBUILD"
echo ""

# ============================================================================
# BUILD PHASE: Ensure all components are built
# ============================================================================

echo "================================================================================"
echo "Build Phase"
echo "================================================================================"
echo ""

# Function to check if rebuild is needed
needs_rebuild() {
    local source="$1"
    local target="$2"

    if [ "$FORCE_REBUILD" = true ]; then
        return 0  # true - needs rebuild
    fi

    if [ ! -e "$target" ]; then
        return 0  # true - needs rebuild
    fi

    # Check modification times
    if [ "$source" -nt "$target" ]; then
        return 0  # true - needs rebuild
    else
        return 1  # false - no rebuild needed
    fi
}

# Step 1: Build external Amalthea-acset if using external mode
if [ "$USE_EXTERNAL" = true ]; then
    echo "[1/6] Building external Amalthea-acset..."
    if [ ! -d "$EXTERNAL_PATH" ]; then
        echo "ERROR: External Amalthea-acset not found at: $EXTERNAL_PATH"
        exit 1
    fi

    if needs_rebuild "$EXTERNAL_PATH/src" "$HOME/.m2/repository/tools/vitruv/tools.vitruv.methodologisttemplate.vsum" || [ "$FORCE_REBUILD" = true ]; then
        (cd "$EXTERNAL_PATH" && mvn clean install -DskipTests -Dcheckstyle.skip=true -q)
        echo "      Done."
    else
        echo "      Skipping (up-to-date)"
    fi
else
    echo "[1/6] Checking Vitruvius dependencies..."
    if [ ! -d "$HOME/.m2/repository/tools/vitruv/tools.vitruv.methodologisttemplate.vsum" ]; then
        if [ -d "$EXTERNAL_PATH" ]; then
            echo "      Building external Amalthea-acset for dependencies..."
            (cd "$EXTERNAL_PATH" && mvn clean install -DskipTests -Dcheckstyle.skip=true -q)
            echo "      Done."
        else
            echo "      WARNING: Vitruvius dependencies may be missing"
        fi
    else
        echo "      Dependencies found."
    fi
fi

# Step 2: Build galette-agent with bytecode interception
echo "[2/6] Building galette-agent with ComparisonInterceptorVisitor..."
GALETTE_AGENT="../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar"
GALETTE_AGENT_SRC="../galette-agent/src"

if needs_rebuild "$GALETTE_AGENT_SRC" "$GALETTE_AGENT" || [ "$FORCE_REBUILD" = true ]; then
    (cd ../galette-agent && mvn clean package -DskipTests -Dcheckstyle.skip=true -q)
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to build galette-agent!"
        exit 1
    fi
    echo "      Done."
else
    echo "      Skipping (up-to-date)"
fi

# Step 3: Build galette-instrument
echo "[3/6] Building galette-instrument..."
GALETTE_INSTRUMENT="../galette-instrument/target/galette-instrument-1.0.0-SNAPSHOT.jar"
GALETTE_INSTRUMENT_SRC="../galette-instrument/src"

if needs_rebuild "$GALETTE_INSTRUMENT_SRC" "$GALETTE_INSTRUMENT" || [ "$FORCE_REBUILD" = true ]; then
    (cd ../galette-instrument && mvn clean install -DskipTests -Dcheckstyle.skip=true -q)
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to build galette-instrument!"
        exit 1
    fi
    echo "      Done."
else
    echo "      Skipping (up-to-date)"
fi

# Step 4: Create instrumented Java
echo "[4/6] Creating instrumented Java runtime..."
INSTRUMENTED_JAVA="target/galette/java"

if [ ! -d "$INSTRUMENTED_JAVA" ] || [ "$FORCE_REBUILD" = true ]; then
    # Clean old instrumented Java if exists
    if [ -d "$INSTRUMENTED_JAVA" ]; then
        rm -rf "$INSTRUMENTED_JAVA"
    fi

    # Clean cache too
    if [ -d "target/galette/cache" ]; then
        rm -rf "target/galette/cache"
    fi

    mvn process-test-resources -Dcheckstyle.skip=true -q
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to create instrumented Java!"
        exit 1
    fi
    echo "      Done."
else
    echo "      Skipping (already exists)"
fi

# Step 5: Build internal amalthea-acset-integration if using internal mode
if [ "$USE_EXTERNAL" = false ]; then
    echo "[5/6] Building internal amalthea-acset-integration..."
    INTERNAL_DIR="../amalthea-acset-integration"
    if [ -d "$INTERNAL_DIR" ]; then
        (cd "$INTERNAL_DIR" && mvn clean install -DskipTests -Dcheckstyle.skip=true -q)
        echo "      Done."
    else
        echo "      WARNING: Internal module not found"
    fi
else
    echo "[5/6] Skipping internal build (using external)"
fi

# Step 6: Compile knarr-runtime
echo "[6/6] Compiling knarr-runtime classes..."
mvn compile test-compile -Dcheckstyle.skip=true -q
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile knarr-runtime!"
    exit 1
fi
echo "      Done."

echo ""

# ============================================================================
# EXECUTION PHASE: Run with instrumented Java
# ============================================================================

echo "================================================================================"
echo "Execution Phase"
echo "================================================================================"
echo ""

# Verify components exist
if [ ! -f "$INSTRUMENTED_JAVA/bin/java" ]; then
    echo "ERROR: Instrumented Java not found!"
    exit 1
fi

if [ ! -f "$GALETTE_AGENT" ]; then
    echo "ERROR: Galette agent not found!"
    exit 1
fi

# Clean previous outputs
echo "Cleaning previous outputs..."
rm -rf galette-output-* execution_paths*.json 2>/dev/null || true

# Generate classpath
echo "Generating classpath..."
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt -q -Dcheckstyle.skip=true
CP="target/classes:target/test-classes:$(cat cp.txt)"

# Create cache directory
mkdir -p target/galette/cache

# Determine main class
if [ "$USE_MULTIVAR" = true ]; then
    MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"
    echo "Main class: AutomaticVitruvMultiVarPathExploration (multi-variable)"
else
    MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"
    echo "Main class: AutomaticVitruvPathExploration (single-variable)"
fi

echo "Running symbolic execution with instrumented Java..."
echo ""

# Run with full instrumentation
"$INSTRUMENTED_JAVA/bin/java" \
  -cp "$CP" \
  -Xbootclasspath/a:"$GALETTE_AGENT" \
  -javaagent:"$GALETTE_AGENT" \
  -Dgalette.cache=target/galette/cache \
  -Dgalette.coverage=true \
  -Dsymbolic.execution.debug=true \
  -Dgalette.debug=true \
  -Dgalette.concolic.interception.enabled=true \
  -Dgalette.concolic.interception.debug=true \
  -Dpath.explorer.max.iterations=30 \
  -Dpath.explorer.debug=true \
  -DDEBUG=true \
  "$MAIN_CLASS"

echo ""
echo "================================================================================"
echo "Completed"
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
else
    echo "Generated files:"
    echo "  - execution_paths_automatic.json      (Path exploration results)"
    echo "  - galette-output-automatic-*/         (Model outputs per execution path)"
    echo ""
    echo "Single-variable exploration:"
    echo "  - Variable: user_choice"
    echo "  - Expected paths: 5 (cases 0-4)"
fi

echo ""
echo "Features enabled:"
echo "  ✅ Bytecode-level comparison interception"
echo "  ✅ Automatic path constraint collection"
echo "  ✅ Symbolic execution with Galette tainting"
echo "  ✅ Path exploration with constraint solving"