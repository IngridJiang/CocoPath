#!/bin/bash
set -euo pipefail

# ============================================================================
# Enhanced Instrumented Runner with Build Control
# ============================================================================
# This script combines features from run-instrumented-with-option-flags.sh
# and the cleanBuildOfMain run-example.sh, providing full control over
# building components and bytecode interception.
#
# Features:
# - Granular build control flags (clean, agent, classes, java)
# - Bytecode interception support with proper flags
# - Internal/External mode switching
# - Smart rebuild logic with timestamp checking
#
# ============================================================================

# Resolve key paths
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

# ============================================================================
# BUILD CONFIGURATION FLAGS
# ============================================================================
# Set these to control what gets rebuilt
FORCE_CLEAN_BUILD=false      # Set to true for complete clean rebuild (overrides all others)
FORCE_REBUILD_GREEN=false    # Force rebuild Green solver and Galette modules dependencies
FORCE_REBUILD_AGENT=false    # Force rebuild galette-agent JAR only
FORCE_REBUILD_CLASSES=false  # Force rebuild knarr-runtime Java classes only
FORCE_REBUILD_JAVA=false     # Force rebuild instrumented Java installation only

# Execution mode flags
USE_EXTERNAL=false
INTERACTIVE_MODE=true
EXTERNAL_PATH="/home/anne/CocoPath/Amalthea-acset"
SKIP_EXTERNAL_BUILD=false
COPY_ONLY=false

usage() {
  cat <<'EOF'
Usage: ./run-instrumented-enhanced.sh [OPTIONS]

Execution Modes:
  --internal, -i         Use internal amalthea-acset-integration module (default)
  --external, -e         Use external Amalthea-acset repository
  --external-path PATH   Override path to external Amalthea-acset checkout
  --copy-only, -c        Only copy generated files from external to internal, then exit

Build Control Flags:
  --clean                Force complete clean rebuild (overrides all other flags)
  --rebuild-agent        Force rebuild galette-agent JAR with interception
  --rebuild-classes      Force rebuild knarr-runtime Java classes
  --rebuild-java         Force rebuild instrumented Java installation
  --rebuild-green        Force rebuild Green solver dependencies
  --rebuild-all          Rebuild all components (same as --clean)
  --no-build             Skip all builds (use existing)

Other Options:
  --skip-external-build, -s  Skip building external Amalthea-acset
  --help, -h             Show this help message

Examples:
  # Run with internal dependencies, rebuilding agent only
  ./run-instrumented-enhanced.sh --internal --rebuild-agent

  # Clean rebuild everything and run with external dependencies
  ./run-instrumented-enhanced.sh --external --clean

  # Run without any rebuilding
  ./run-instrumented-enhanced.sh --no-build
EOF
}

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
      if [[ $# -lt 2 ]]; then
        echo "ERROR: --external-path requires a value" >&2
        exit 1
      fi
      EXTERNAL_PATH="$2"
      INTERACTIVE_MODE=false
      shift 2
      ;;
    --skip-external-build|-s)
      SKIP_EXTERNAL_BUILD=true
      shift
      ;;
    --copy-only|-c)
      COPY_ONLY=true
      INTERACTIVE_MODE=false
      shift
      ;;
    --clean|--rebuild-all)
      FORCE_CLEAN_BUILD=true
      shift
      ;;
    --rebuild-agent)
      FORCE_REBUILD_AGENT=true
      shift
      ;;
    --rebuild-classes)
      FORCE_REBUILD_CLASSES=true
      shift
      ;;
    --rebuild-java)
      FORCE_REBUILD_JAVA=true
      shift
      ;;
    --rebuild-green)
      FORCE_REBUILD_GREEN=true
      shift
      ;;
    --no-build)
      SKIP_EXTERNAL_BUILD=true
      # Don't force any rebuilds
      shift
      ;;
    --help|-h)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage
      exit 1
      ;;
  esac
done

# ============================================================================
# JAVA CONFIGURATION
# ============================================================================
# Prefer Java 17; allow override via JAVA_HOME if already set
JAVA_HOME_DEFAULT="/usr/lib/jvm/java-17-openjdk-amd64"
if [[ -z "${JAVA_HOME:-}" && -x "${JAVA_HOME_DEFAULT}/bin/java" ]]; then
  export JAVA_HOME="$JAVA_HOME_DEFAULT"
fi
if [[ -n "${JAVA_HOME:-}" ]]; then
  export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "☕ Using java: $(java -version 2>&1 | head -1)"
echo ""

# ============================================================================
# BUILD STATUS DISPLAY
# ============================================================================
echo "📦 Build Configuration:"
echo "   Clean Build: $FORCE_CLEAN_BUILD"
echo "   Rebuild Agent: $FORCE_REBUILD_AGENT"
echo "   Rebuild Classes: $FORCE_REBUILD_CLASSES"
echo "   Rebuild Java: $FORCE_REBUILD_JAVA"
echo "   Rebuild Green: $FORCE_REBUILD_GREEN"
echo ""

# ============================================================================
# HELPER FUNCTIONS
# ============================================================================
# Function to check if a rebuild is needed based on timestamps
needs_rebuild() {
    local source_dir="$1"
    local target="$2"

    # If force flag is set, always rebuild
    if [[ "$3" == "true" ]]; then
        return 0  # true - needs rebuild
    fi

    # If target doesn't exist, needs rebuild
    if [[ ! -e "$target" ]]; then
        return 0  # true - needs rebuild
    fi

    # Check if any source file is newer than target
    if [[ -d "$source_dir" ]]; then
        local newest_src=$(find "$source_dir" -name "*.java" -newer "$target" 2>/dev/null | head -1)
        if [[ -n "$newest_src" ]]; then
            return 0  # true - needs rebuild
        fi
    fi

    return 1  # false - no rebuild needed
}

# ============================================================================
# COPY-ONLY MODE
# ============================================================================
if [[ "$COPY_ONLY" == true ]]; then
  echo "📋 Copy-only mode: Copying generated files from external to internal project..."

  # Ensure external path exists
  if [[ ! -d "$EXTERNAL_PATH" ]]; then
    echo "ERROR: External path does not exist: $EXTERNAL_PATH" >&2
    exit 1
  fi

  INTERNAL_PATH="${ROOT_DIR}/amalthea-acset-integration"

  # Check if generated files exist
  if [[ ! -d "$EXTERNAL_PATH/consistency/target/generated-sources/reactions/mir" ]]; then
    echo "ERROR: Generated reaction files not found in $EXTERNAL_PATH" >&2
    echo "       Run 'mvn clean install' in $EXTERNAL_PATH first" >&2
    exit 1
  fi

  # Copy the files
  if [[ -d "$INTERNAL_PATH/consistency/src/main/java/mir" ]]; then
    echo "Copying reactions..."
    cp -r "$EXTERNAL_PATH/consistency/target/generated-sources/reactions/mir/reactions" \
          "$INTERNAL_PATH/consistency/src/main/java/mir/" 2>/dev/null || true

    echo "Copying routines..."
    cp -r "$EXTERNAL_PATH/consistency/target/generated-sources/reactions/mir/routines" \
          "$INTERNAL_PATH/consistency/src/main/java/mir/" 2>/dev/null || true

    # Some directories might not exist, so use rsync as backup
    which rsync &>/dev/null && rsync -av --delete \
      "$EXTERNAL_PATH/consistency/target/generated-sources/reactions/mir/" \
      "$INTERNAL_PATH/consistency/src/main/java/mir/"

    echo "✅ Files copied successfully"
  else
    echo "ERROR: Internal path not found: $INTERNAL_PATH/consistency/src/main/java/mir" >&2
    exit 1
  fi

  exit 0
fi

# ============================================================================
# INTERACTIVE MODE
# ============================================================================
if [[ "$INTERACTIVE_MODE" == true ]]; then
  echo "Select mode:"
  echo "  1) INTERNAL (amalthea-acset-integration module)"
  echo "  2) EXTERNAL (external Amalthea-acset repository)"
  read -p "Enter choice (1-2): " choice

  case $choice in
    1)
      USE_EXTERNAL=false
      ;;
    2)
      USE_EXTERNAL=true
      ;;
    *)
      echo "Invalid choice. Using INTERNAL mode."
      USE_EXTERNAL=false
      ;;
  esac
fi

# ============================================================================
# BUILD PHASE
# ============================================================================

# Determine what needs to be built
need_agent_build=false
need_classes_build=false
need_java_build=false
need_green_build=false

if [[ "$FORCE_CLEAN_BUILD" == "true" ]]; then
    echo "🧹 FORCE_CLEAN_BUILD enabled - will rebuild everything"
    need_agent_build=true
    need_classes_build=true
    need_java_build=true
    need_green_build=true

    # Clean Maven target directory
    echo "🧹 Cleaning Maven target directories..."
    mvn clean -q

    # Remove instrumented Java if it exists
    if [[ -d "${SCRIPT_DIR}/target/galette/java" ]]; then
        echo "🧹 Removing existing instrumented Java directory"
        rm -rf "${SCRIPT_DIR}/target/galette/java"
    fi
else
    # Check individual rebuild requirements
    GALETTE_AGENT="${ROOT_DIR}/galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar"
    GALETTE_AGENT_SRC="${ROOT_DIR}/galette-agent/src"

    if needs_rebuild "$GALETTE_AGENT_SRC" "$GALETTE_AGENT" "$FORCE_REBUILD_AGENT"; then
        need_agent_build=true
        echo "📦 Galette agent rebuild needed"
    fi

    CLASSES_TARGET="${SCRIPT_DIR}/target/classes"
    CLASSES_SRC="${SCRIPT_DIR}/src/main/java"

    if needs_rebuild "$CLASSES_SRC" "$CLASSES_TARGET" "$FORCE_REBUILD_CLASSES"; then
        need_classes_build=true
        echo "📦 Java classes rebuild needed"
    fi

    INSTRUMENTED_JAVA="${SCRIPT_DIR}/target/galette/java"

    if [[ ! -d "$INSTRUMENTED_JAVA" ]] || [[ "$FORCE_REBUILD_JAVA" == "true" ]]; then
        need_java_build=true
        echo "📦 Instrumented Java rebuild needed"
    fi

    if [[ "$FORCE_REBUILD_GREEN" == "true" ]]; then
        need_green_build=true
        echo "📦 Green solver rebuild needed"
    fi
fi

# ============================================================================
# DEPENDENCY SWITCHING (if using external)
# ============================================================================
RESTORE_POM=false

if [[ "$USE_EXTERNAL" == true ]]; then
  echo "Mode: EXTERNAL (switching to external Amalthea-acset)"

  if [[ ! -d "$EXTERNAL_PATH" ]]; then
    echo "ERROR: External Amalthea-acset not found at: $EXTERNAL_PATH" >&2
    exit 1
  fi

  if [[ "$SKIP_EXTERNAL_BUILD" != true ]]; then
    echo "Building external Amalthea-acset..."
    (cd "$EXTERNAL_PATH" && mvn -q clean install -DskipTests -Dcheckstyle.skip=true)
    echo "Done."
  fi

  # Find Python command
  PYTHON_CMD=""
  if command -v python3 &> /dev/null; then
    PYTHON_CMD="python3"
  elif command -v python &> /dev/null; then
    PYTHON_CMD="python"
  else
    echo "ERROR: Python not found. Cannot switch dependencies." >&2
    exit 1
  fi

  "$PYTHON_CMD" "${SCRIPT_DIR}/switch-dependency.py" external "${SCRIPT_DIR}/pom.xml"
  RESTORE_POM=true
else
  echo "Mode: INTERNAL (amalthea-acset-integration)"

  if [[ ! -d "$HOME/.m2/repository/tools/vitruv/tools.vitruv.methodologisttemplate.vsum" ]]; then
    if [[ -d "$EXTERNAL_PATH" ]]; then
      if [[ "$SKIP_EXTERNAL_BUILD" == true ]]; then
        echo "WARNING: Vitruvius dependencies missing but --skip-external-build specified"
        echo "         Build may fail. Run without -s first if you get dependency errors."
      else
        echo "Vitruvius dependencies missing. Installing from external Amalthea-acset..."
        (cd "$EXTERNAL_PATH" && mvn -q clean install -DskipTests -Dcheckstyle.skip=true)
        echo "Done."
      fi
    else
      echo "ERROR: Vitruvius dependencies missing and external path not found at $EXTERNAL_PATH" >&2
      echo "       Provide --external-path or build Amalthea-acset manually."
      exit 1
    fi
  fi

  # Find Python command
  PYTHON_CMD=""
  if command -v python3 &> /dev/null; then
    PYTHON_CMD="python3"
  elif command -v python &> /dev/null; then
    PYTHON_CMD="python"
  else
    echo "ERROR: Python not found. Cannot switch dependencies." >&2
    exit 1
  fi

  "$PYTHON_CMD" "${SCRIPT_DIR}/switch-dependency.py" internal "${SCRIPT_DIR}/pom.xml"
  RESTORE_POM=true
fi

# ============================================================================
# BUILD COMPONENTS
# ============================================================================

# Step 0: Build Green solver if needed
if [[ "$need_green_build" == "true" ]]; then
    echo "🔨 Building Green solver dependencies..."
    GREEN_PATH="${ROOT_DIR}/../green-solver/green"
    if [[ -d "$GREEN_PATH" ]]; then
        (cd "$GREEN_PATH" && mvn -q clean install -DskipTests)
        echo "✅ Green solver built"
    else
        echo "⚠️  Green solver not found at $GREEN_PATH"
    fi
fi

# Step 1: Build galette-agent if needed
if [[ "$need_agent_build" == "true" ]]; then
    echo "🔨 Building galette-agent with ComparisonInterceptorVisitor..."
    (cd "${ROOT_DIR}/galette-agent" && mvn -q clean package -DskipTests -Dcheckstyle.skip=true)
    if [[ $? -ne 0 ]]; then
        echo "❌ Failed to build galette-agent!"
        exit 1
    fi
    echo "✅ Galette agent built with bytecode interception support"
else
    echo "⚡ Using existing galette-agent JAR"
fi

# Step 2: Build knarr-runtime classes if needed
if [[ "$need_classes_build" == "true" ]] || [[ "$FORCE_CLEAN_BUILD" == "true" ]]; then
    echo "🔨 Building knarr-runtime classes..."
    mvn -q -f "${ROOT_DIR}/pom.xml" clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dskip=true -pl knarr-runtime -am
    if [[ $? -ne 0 ]]; then
        echo "❌ Failed to build knarr-runtime classes!"
        exit 1
    fi
    echo "✅ Classes built"
else
    echo "⚡ Using existing compiled classes"
fi

# Step 3: Create instrumented Java if needed
if [[ "$need_java_build" == "true" ]]; then
    echo "🔨 Creating instrumented Java runtime..."

    # Clean old instrumented Java if exists
    if [[ -d "${SCRIPT_DIR}/target/galette/java" ]]; then
        rm -rf "${SCRIPT_DIR}/target/galette/java"
    fi

    # Clean cache too
    if [[ -d "${SCRIPT_DIR}/target/galette/cache" ]]; then
        rm -rf "${SCRIPT_DIR}/target/galette/cache"
    fi

    mvn -q -f "${ROOT_DIR}/pom.xml" process-test-resources -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dskip=true -pl knarr-runtime
    if [[ $? -ne 0 ]]; then
        echo "❌ Failed to create instrumented Java!"
        exit 1
    fi
    echo "✅ Instrumented Java created"
else
    echo "⚡ Using existing instrumented Java"
fi

# ============================================================================
# VERIFY AND PREPARE EXECUTION
# ============================================================================

# Resolve Galette agent location
GALETTE_AGENT=""
if [[ -f "${ROOT_DIR}/galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar" ]]; then
  GALETTE_AGENT="${ROOT_DIR}/galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar"
elif [[ -f "$HOME/.m2/repository/edu/neu/ccs/prl/galette/galette-agent/1.0.0-SNAPSHOT/galette-agent-1.0.0-SNAPSHOT.jar" ]]; then
  GALETTE_AGENT="$HOME/.m2/repository/edu/neu/ccs/prl/galette/galette-agent/1.0.0-SNAPSHOT/galette-agent-1.0.0-SNAPSHOT.jar"
else
  echo "❌ Galette agent jar not found" >&2
  exit 1
fi

echo "🔧 Galette agent: $GALETTE_AGENT"

INSTRUMENTED_JAVA="${SCRIPT_DIR}/target/galette/java"
if [[ ! -x "$INSTRUMENTED_JAVA/bin/java" ]]; then
  echo "❌ Instrumented java not found at $INSTRUMENTED_JAVA/bin/java" >&2
  exit 1
fi

# Build runtime classpath
mvn -q -f "${ROOT_DIR}/pom.xml" -DincludeScope=runtime -Dmdep.outputFile="${SCRIPT_DIR}/cp.txt" -pl knarr-runtime dependency:build-classpath
if [[ ! -f "${SCRIPT_DIR}/cp.txt" ]]; then
  echo "❌ Failed to build classpath" >&2
  exit 1
fi
CP="${SCRIPT_DIR}/target/classes:${SCRIPT_DIR}/target/test-classes:$(cat "${SCRIPT_DIR}/cp.txt")"

echo "📋 Classpath entries: $(echo "$CP" | tr ':' '\n' | wc -l)"

# Create cache directory
mkdir -p target/galette/cache

# ============================================================================
# EXECUTION
# ============================================================================
MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"

echo ""
echo "🚀 Running with bytecode interception enabled..."
echo "================================================"
echo ""

set -x
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
  -Dpath.explorer.debug=true \
  -Dconstraint.solver.debug=true \
  "$MAIN_CLASS" "$@"

EXIT_CODE=$?
set +x

# ============================================================================
# CLEANUP
# ============================================================================
if [[ "$RESTORE_POM" == true && -f "${SCRIPT_DIR}/pom.xml.bak" ]]; then
  echo ""
  echo "Restoring pom.xml from backup..."
  mv "${SCRIPT_DIR}/pom.xml.bak" "${SCRIPT_DIR}/pom.xml" 2>/dev/null || true
fi

echo ""
echo "✅ Execution completed with exit code: $EXIT_CODE"
echo ""
echo "Features enabled:"
echo "  ✅ Bytecode-level comparison interception"
echo "  ✅ Automatic path constraint collection"
echo "  ✅ Symbolic execution with Galette tainting"
echo "  ✅ Debug output for troubleshooting"

exit $EXIT_CODE