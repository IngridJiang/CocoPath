#!/bin/bash

# Script to rebuild instrumented Java with updated GaletteTransformer
# This ensures our modified GaletteTransformer with ComparisonInterceptorVisitor is embedded

set -e  # Exit on any error

echo "🔧 Rebuilding Instrumented Java with Bytecode Interception Support"
echo "=================================================================="
echo ""
echo "This script will:"
echo "1. Clean and rebuild galette-agent (contains ComparisonInterceptorVisitor)"
echo "2. Clean and rebuild galette-instrument (uses galette-agent classes)"
echo "3. Delete existing instrumented Java"
echo "4. Create new instrumented Java with embedded updated classes"
echo ""

# Ensure Java 17 is used
export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"

echo "☕ Java Configuration:"
echo "   JAVA_HOME: $JAVA_HOME"
echo "   Java version: $(java -version 2>&1 | head -1)"
echo ""

# Step 1: Clean and rebuild galette-agent
echo "📦 Step 1: Rebuilding galette-agent with bytecode interception..."
cd ../galette-agent
mvn clean install -DskipTests -Dcheckstyle.skip=true -q
if [ $? -ne 0 ]; then
    echo "❌ Failed to build galette-agent!"
    echo "   Check for compilation errors in ComparisonInterceptorVisitor.java"
    exit 1
fi
echo "✅ galette-agent built and installed to local Maven repository"

# Step 2: Clean and rebuild galette-instrument
echo ""
echo "📦 Step 2: Rebuilding galette-instrument (depends on galette-agent)..."
cd ../galette-instrument
mvn clean install -DskipTests -Dcheckstyle.skip=true -q
if [ $? -ne 0 ]; then
    echo "❌ Failed to build galette-instrument!"
    exit 1
fi
echo "✅ galette-instrument built and installed to local Maven repository"

# Step 3: Delete existing instrumented Java
echo ""
echo "🗑️ Step 3: Deleting existing instrumented Java..."
cd ../knarr-runtime
if [ -d "target/galette/java" ]; then
    rm -rf target/galette/java
    echo "✅ Deleted existing instrumented Java"
else
    echo "⚠️ No existing instrumented Java found"
fi

# Also delete the cache to ensure fresh transformation
if [ -d "target/galette/cache" ]; then
    rm -rf target/galette/cache
    echo "✅ Deleted transformation cache"
fi

# Step 4: Rebuild instrumented Java
echo ""
echo "🔨 Step 4: Creating new instrumented Java with embedded updated classes..."
mvn process-test-resources -Dcheckstyle.skip=true -q
if [ $? -ne 0 ]; then
    echo "❌ Failed to create instrumented Java!"
    echo "   Check if galette-maven-plugin is configured in pom.xml"
    exit 1
fi

# Verify instrumented Java was created
if [ ! -d "target/galette/java" ]; then
    echo "❌ Instrumented Java directory not created!"
    echo "   Check galette-maven-plugin configuration"
    exit 1
fi

if [ ! -f "target/galette/java/bin/java" ]; then
    echo "❌ Instrumented Java binary not found!"
    exit 1
fi

# Step 5: Build the knarr-runtime classes
echo ""
echo "📦 Step 5: Building knarr-runtime classes..."
mvn compile -Dcheckstyle.skip=true -q
if [ $? -ne 0 ]; then
    echo "❌ Failed to build knarr-runtime classes!"
    exit 1
fi
echo "✅ knarr-runtime classes compiled successfully"

echo ""
echo "✅ SUCCESS! Instrumented Java rebuilt with bytecode interception support"
echo ""
echo "The instrumented Java at target/galette/java now contains:"
echo "- ComparisonInterceptorVisitor for automatic bytecode-level constraint collection"
echo "- Updated GaletteTransformer with visitor registration"
echo "- PathUtils with instrumented comparison methods"
echo ""
echo "Features enabled:"
echo "- Automatic interception of DCMPL, LCMP, FCMPL, FCMPG, DCMPG instructions"
echo "- Automatic interception of IF_ICMP* and IF_ACMP* jump instructions"
echo "- Thread-local storage for path constraints"
echo ""
echo "Next steps:"
echo "1. Run tests with: ./run-with-interception.sh"
echo "2. Enable interception: -Dgalette.concolic.interception.enabled=true"