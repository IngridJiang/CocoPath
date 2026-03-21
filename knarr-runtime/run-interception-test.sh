#!/bin/bash

# Script to run the bytecode interception test
# This demonstrates automatic constraint collection from native Java comparisons

set -e  # Exit on any error

echo "🚀 Bytecode Interception Test"
echo "============================="
echo ""

# Ensure Java 17 is used - detect JAVA_HOME if not already set correctly
if [ ! -x "${JAVA_HOME:-}/bin/java" ]; then
    if [ -x "/usr/lib/jvm/java-17-openjdk-amd64/bin/java" ]; then
        export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
    elif [ -x "/usr/local/sdkman/candidates/java/current/bin/java" ]; then
        export JAVA_HOME="/usr/local/sdkman/candidates/java/current"
    fi
fi
export PATH="$JAVA_HOME/bin:$PATH"

echo "☕ Java Configuration:"
echo "   JAVA_HOME: $JAVA_HOME"
echo "   Java version: $(java -version 2>&1 | head -1)"
echo ""

# Check if we need to build
if [ ! -f "target/test-classes/edu/neu/ccs/prl/galette/concolic/knarr/runtime/BytecodeInterceptionTest.class" ]; then
    echo "📦 Building test classes..."
    mvn test-compile -q -Dcheckstyle.skip=true
    if [ $? -ne 0 ]; then
        echo "❌ Failed to compile test classes!"
        exit 1
    fi
    echo "✅ Test classes compiled"
fi

# Check if instrumented Java exists
INSTRUMENTED_JAVA="target/galette/java"
if [ ! -f "$INSTRUMENTED_JAVA/bin/java" ]; then
    echo "❌ Instrumented Java not found at: $INSTRUMENTED_JAVA"
    echo "   Run './rebuild-instrumented-java.sh' first"
    exit 1
fi

# Find Galette agent JAR
GALETTE_AGENT="../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar"
if [ ! -f "$GALETTE_AGENT" ]; then
    echo "❌ Galette agent JAR not found at: $GALETTE_AGENT"
    echo "   Run './rebuild-instrumented-java.sh' first"
    exit 1
fi

echo "🔧 Configuration:"
echo "   Instrumented Java: $INSTRUMENTED_JAVA/bin/java"
echo "   Galette Agent: $GALETTE_AGENT"
echo ""

# Generate classpath
if [ ! -f cp.txt ] || [ $(find cp.txt -mmin +60 2>/dev/null | wc -l) -eq 1 ]; then
    echo "📋 Generating classpath..."
    mvn dependency:build-classpath -Dmdep.outputFile=cp.txt -q -Dcheckstyle.skip=true
fi

# Create classpath with test classes
CP="target/classes:target/test-classes:$(cat cp.txt)"

echo "🧪 Running WITHOUT bytecode interception (regular Java)..."
echo "============================================="
java -cp "$CP" edu.neu.ccs.prl.galette.concolic.knarr.runtime.BytecodeInterceptionTest
echo ""
echo ""

echo "🔬 Running WITH bytecode interception (instrumented Java + Galette agent)..."
echo "========================================================================"
"$INSTRUMENTED_JAVA/bin/java" \
  -cp "$CP" \
  -Xbootclasspath/a:"$GALETTE_AGENT" \
  -javaagent:"$GALETTE_AGENT" \
  -Dgalette.concolic.interception.enabled=true \
  -Dgalette.concolic.interception.debug=true \
  -Dgalette.debug=true \
  edu.neu.ccs.prl.galette.concolic.knarr.runtime.BytecodeInterceptionTest

echo ""
echo "✅ Test completed"
echo ""
echo "📊 Summary:"
echo "   - The first run shows normal Java execution"
echo "   - The second run should show interception messages if working correctly"
echo "   - Look for 'PathUtils.instrumentedDcmpl' and similar messages"