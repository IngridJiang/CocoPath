#!/bin/bash

# ============================================================================
# Copy Generated Reactions from Amalthea-acset to amalthea-acset-integration
# ============================================================================
# This script copies the generated reactions and routines from the external
# Amalthea-acset project to the internal amalthea-acset-integration module.
#
# Usage:
#   ./copy-generated-reactions.sh
#   ./copy-generated-reactions.sh --external-path /path/to/Amalthea-acset
#
# ============================================================================

set -e

# Default paths
EXTERNAL_PATH="/home/anne/CocoPath/Amalthea-acset"
INTERNAL_PATH="/home/anne/CocoPath/CocoPath/amalthea-acset-integration"

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --external-path)
            EXTERNAL_PATH="$2"
            shift 2
            ;;
        --internal-path)
            INTERNAL_PATH="$2"
            shift 2
            ;;
        -h|--help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --external-path PATH  Path to external Amalthea-acset (default: $EXTERNAL_PATH)"
            echo "  --internal-path PATH  Path to internal amalthea-acset-integration (default: $INTERNAL_PATH)"
            echo "  -h, --help           Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use -h for help"
            exit 1
            ;;
    esac
done

echo "================================================================================"
echo "Copy Generated Reactions and Routines"
echo "================================================================================"
echo ""
echo "From: $EXTERNAL_PATH"
echo "To:   $INTERNAL_PATH"
echo ""

# Verify external path exists
if [ ! -d "$EXTERNAL_PATH" ]; then
    echo "ERROR: External Amalthea-acset not found at: $EXTERNAL_PATH"
    echo "Please specify correct path with --external-path"
    exit 1
fi

# Verify internal path exists
if [ ! -d "$INTERNAL_PATH" ]; then
    echo "ERROR: Internal amalthea-acset-integration not found at: $INTERNAL_PATH"
    echo "Please specify correct path with --internal-path"
    exit 1
fi

# Check if generated sources exist
GENERATED_DIR="$EXTERNAL_PATH/consistency/target/generated-sources/reactions"
if [ ! -d "$GENERATED_DIR" ]; then
    echo "ERROR: Generated sources not found at: $GENERATED_DIR"
    echo ""
    echo "You may need to build the external project first:"
    echo "  cd $EXTERNAL_PATH"
    echo "  mvn clean generate-sources"
    exit 1
fi

# Create target directory if it doesn't exist
TARGET_DIR="$INTERNAL_PATH/consistency/src/main/java/mir"
if [ ! -d "$TARGET_DIR" ]; then
    echo "Creating target directory: $TARGET_DIR"
    mkdir -p "$TARGET_DIR"
fi

# Copy reactions
if [ -d "$GENERATED_DIR/mir/reactions" ]; then
    echo "Copying reactions..."
    rm -rf "$TARGET_DIR/reactions" 2>/dev/null || true
    cp -r "$GENERATED_DIR/mir/reactions" "$TARGET_DIR/"
    REACTION_COUNT=$(find "$TARGET_DIR/reactions" -name "*.java" | wc -l)
    echo "  Copied $REACTION_COUNT reaction files"
else
    echo "WARNING: No reactions found to copy"
fi

# Copy routines
if [ -d "$GENERATED_DIR/mir/routines" ]; then
    echo "Copying routines..."
    rm -rf "$TARGET_DIR/routines" 2>/dev/null || true
    cp -r "$GENERATED_DIR/mir/routines" "$TARGET_DIR/"
    ROUTINE_COUNT=$(find "$TARGET_DIR/routines" -name "*.java" | wc -l)
    echo "  Copied $ROUTINE_COUNT routine files"
else
    echo "WARNING: No routines found to copy"
fi

echo ""

# Fix Java 8 compatibility issues
echo "Fixing Java 8 compatibility issues..."
CHANGE_SPEC_FILE="$TARGET_DIR/reactions/amalthea2ascet/Amalthea2ascetChangePropagationSpecification.java"
if [ -f "$CHANGE_SPEC_FILE" ]; then
    sed -i 's/import java.util.Set;/import java.util.Collections;/' "$CHANGE_SPEC_FILE"
    sed -i 's/Set\.of/Collections.singleton/g' "$CHANGE_SPEC_FILE"
    echo "  Fixed Set.of() -> Collections.singleton() in Amalthea2ascetChangePropagationSpecification.java"
else
    echo "  WARNING: ChangePropagationSpecification.java not found, skipping Java 8 fix"
fi

echo ""

# Clean Maven repository to avoid stale JAR issues
echo "Cleaning Maven repository cache..."
# Remove internal consistency JAR from Maven repo
rm -rf "$HOME/.m2/repository/edu/neu/ccs/prl/galette/amalthea-acset-consistency/1.0.0-SNAPSHOT/" 2>/dev/null || true
echo "  Removed cached amalthea-acset-consistency JAR"

# Also remove external consistency JAR if it exists
rm -rf "$HOME/.m2/repository/tools/vitruv/tools.vitruv.methodologisttemplate.consistency/0.1.0-SNAPSHOT/" 2>/dev/null || true
echo "  Removed cached external consistency JAR"

echo ""
echo "Building and installing internal project..."
cd "$INTERNAL_PATH"
mvn clean install -DskipTests -Dcheckstyle.skip=true -q
if [ $? -eq 0 ]; then
    echo "  Internal project built and installed successfully"
else
    echo "ERROR: Failed to build internal project"
    exit 1
fi

echo ""
echo "================================================================================"
echo "Copy and build completed successfully!"
echo "================================================================================"
echo ""
echo "Next steps:"
echo "1. Run the instrumented tests:"
echo "   cd /home/anne/CocoPath/CocoPath/knarr-runtime"
echo "   ./run-instrumented-with-option-flags.sh -i"
echo ""
echo "2. Or run symbolic execution:"
echo "   ./run-symbolic-execution-adapted.sh --internal"
echo ""