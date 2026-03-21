#!/bin/bash

# ============================================================================
# Vitruvius Test Runner with Full Instrumentation Support
# ============================================================================
# This script runs the Vitruvius CreateAscetTask test with proper bytecode
# interception and symbolic execution support.
#
# It ensures all components are built:
# - galette-agent (with ComparisonInterceptorVisitor)
# - Instrumented Java runtime
# - Amalthea-acset dependencies
#
# Usage:
#   ./run-vitruvius-test.sh              # Run with automatic rebuilding
#   ./run-vitruvius-test.sh --force      # Force rebuild all components
#   ./run-vitruvius-test.sh --no-build   # Skip all builds (use existing)
#
# ============================================================================

set -e  # Exit on any error

echo "🚀 Vitruvius Test Runner with Bytecode Interception"
echo "===================================================="
echo ""

# Parse arguments
FORCE_REBUILD=false
SKIP_BUILD=false
for arg in "$@"; do
    case $arg in
        --force)
            FORCE_REBUILD=true
            shift
            ;;
        --no-build)
            SKIP_BUILD=true
            shift
            ;;
        *)
            ;;
    esac
done

# Ensure Java 17 is used
export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"

echo "☕ Java Configuration:"
echo "   JAVA_HOME: $JAVA_HOME"
echo "   Java version: $(java -version 2>&1 | head -1)"
echo ""

# Function to check if a file/directory is newer than a target
needs_rebuild() {
    local source="$1"
    local target="$2"

    # If force rebuild is set, always rebuild
    if [ "$FORCE_REBUILD" = true ]; then
        return 0  # true - needs rebuild
    fi

    # If target doesn't exist, needs rebuild
    if [ ! -e "$target" ]; then
        return 0  # true - needs rebuild
    fi

    # Check if source is newer than target
    if [ "$source" -nt "$target" ]; then
        return 0  # true - needs rebuild
    else
        return 1  # false - no rebuild needed
    fi
}

# Step 1: Build Amalthea-acset if needed
EXTERNAL_PATH="/home/anne/CocoPath/Amalthea-acset"
if [ "$SKIP_BUILD" != true ]; then
    if [ -d "$EXTERNAL_PATH" ]; then
        if needs_rebuild "$EXTERNAL_PATH/src" "$HOME/.m2/repository/tools/vitruv/tools.vitruv.methodologisttemplate.vsum" || [ "$FORCE_REBUILD" = true ]; then
            echo "📦 Step 1: Building Amalthea-acset for Vitruvius dependencies..."
            (cd "$EXTERNAL_PATH" && mvn clean install -DskipTests -Dcheckstyle.skip=true -q)
            echo "✅ Amalthea-acset built"
        else
            echo "⚡ Step 1: Amalthea-acset is up-to-date"
        fi
    else
        echo "⚠️ Step 1: Amalthea-acset not found at $EXTERNAL_PATH"
        echo "         Vitruvius transformations may fail"
    fi
    echo ""
fi

# Step 2: Build galette-agent with bytecode interception
if [ "$SKIP_BUILD" != true ]; then
    GALETTE_AGENT="../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar"
    GALETTE_AGENT_SRC="../galette-agent/src"

    if needs_rebuild "$GALETTE_AGENT_SRC" "$GALETTE_AGENT" || [ "$FORCE_REBUILD" = true ]; then
        echo "📦 Step 2: Building galette-agent with ComparisonInterceptorVisitor..."
        (cd ../galette-agent && mvn clean package -DskipTests -Dcheckstyle.skip=true -q)
        if [ $? -ne 0 ]; then
            echo "❌ Failed to build galette-agent!"
            exit 1
        fi
        echo "✅ galette-agent built with bytecode interception support"
    else
        echo "⚡ Step 2: galette-agent is up-to-date"
    fi
    echo ""
fi

# Step 3: Build galette-instrument if needed
if [ "$SKIP_BUILD" != true ]; then
    GALETTE_INSTRUMENT="../galette-instrument/target/galette-instrument-1.0.0-SNAPSHOT.jar"
    GALETTE_INSTRUMENT_SRC="../galette-instrument/src"

    if needs_rebuild "$GALETTE_INSTRUMENT_SRC" "$GALETTE_INSTRUMENT" || [ "$FORCE_REBUILD" = true ]; then
        echo "📦 Step 3: Building galette-instrument..."
        (cd ../galette-instrument && mvn clean install -DskipTests -Dcheckstyle.skip=true -q)
        if [ $? -ne 0 ]; then
            echo "❌ Failed to build galette-instrument!"
            exit 1
        fi
        echo "✅ galette-instrument built"
    else
        echo "⚡ Step 3: galette-instrument is up-to-date"
    fi
    echo ""
fi

# Step 4: Create instrumented Java if needed
if [ "$SKIP_BUILD" != true ]; then
    INSTRUMENTED_JAVA="target/galette/java"

    if [ ! -d "$INSTRUMENTED_JAVA" ] || [ "$FORCE_REBUILD" = true ]; then
        echo "📦 Step 4: Creating instrumented Java runtime..."

        # Clean old instrumented Java if exists
        if [ -d "$INSTRUMENTED_JAVA" ]; then
            rm -rf "$INSTRUMENTED_JAVA"
        fi

        # Also clean cache
        if [ -d "target/galette/cache" ]; then
            rm -rf "target/galette/cache"
        fi

        # Create new instrumented Java
        mvn process-test-resources -Dcheckstyle.skip=true -q
        if [ $? -ne 0 ]; then
            echo "❌ Failed to create instrumented Java!"
            echo "   Check if galette-maven-plugin is configured in pom.xml"
            exit 1
        fi
        echo "✅ Instrumented Java created"
    else
        echo "⚡ Step 4: Instrumented Java already exists"
    fi
    echo ""
fi

# Step 5: Compile knarr-runtime classes
if [ "$SKIP_BUILD" != true ]; then
    echo "📦 Step 5: Compiling knarr-runtime classes..."
    mvn compile test-compile -Dcheckstyle.skip=true -q
    if [ $? -ne 0 ]; then
        echo "❌ Failed to compile knarr-runtime!"
        exit 1
    fi
    echo "✅ knarr-runtime compiled"
    echo ""
fi

# Verify required components exist
INSTRUMENTED_JAVA="target/galette/java"
GALETTE_AGENT="../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar"

if [ ! -f "$INSTRUMENTED_JAVA/bin/java" ]; then
    echo "❌ Instrumented Java not found at: $INSTRUMENTED_JAVA"
    echo "   Run with --force to rebuild"
    exit 1
fi

if [ ! -f "$GALETTE_AGENT" ]; then
    echo "❌ Galette agent not found at: $GALETTE_AGENT"
    echo "   Run with --force to rebuild"
    exit 1
fi

# Generate classpath
echo "📋 Generating classpath..."
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt -q -Dcheckstyle.skip=true
if [ ! -f "cp.txt" ]; then
    echo "❌ Failed to generate classpath!"
    exit 1
fi

CP="target/classes:target/test-classes:$(cat cp.txt)"
echo "   Classpath has $(echo "$CP" | tr ':' '\n' | wc -l) entries"
echo ""

# Create cache directory
mkdir -p target/galette/cache

# Choose main class - you can change this to test different scenarios
MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"

echo "🔧 Configuration:"
echo "   Instrumented Java: $INSTRUMENTED_JAVA/bin/java"
echo "   Galette Agent: $GALETTE_AGENT"
echo "   Main Class: $MAIN_CLASS"
echo ""

echo "🎯 Running Vitruvius Test with Bytecode Interception..."
echo "======================================================="
echo ""
echo "This test will:"
echo "1. Create Amalthea tasks through Vitruvius reactions"
echo "2. Use SymbolicComparison.symbolicVitruviusChoice for user choices"
echo "3. Intercept bytecode-level comparisons automatically"
echo "4. Collect path constraints for concolic execution"
echo "5. Explore all possible paths through the transformation"
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
  -DDEBUG=true \
  "$MAIN_CLASS" "$@"

echo ""
echo "✅ Test completed"
echo ""
echo "📊 Results:"
echo "   Check execution_paths_automatic.json for path exploration results"
echo "   Check galette-output-automatic-*/ directories for generated models"
echo ""
echo "🔍 Debug Tips:"
echo "   - Look for 'ComparisonInterceptorVisitor created' messages"
echo "   - Look for 'PathUtils.instrumentedDcmpl' messages for bytecode interception"
echo "   - Look for 'symbolicVitruviusChoice CALLED' for semantic tracking"