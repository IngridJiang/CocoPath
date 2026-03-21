#!/bin/bash
set -euo pipefail

# Resolve key paths
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Execution mode flags
USE_EXTERNAL=false
INTERACTIVE_MODE=true
EXTERNAL_PATH="/home/anne/CocoPath/Amalthea-acset"
SKIP_EXTERNAL_BUILD=false
COPY_ONLY=false

usage() {
  cat <<'EOF'
Usage: ./run-instrumented-copy.sh [--internal|--external] [--external-path PATH] [--skip-external-build]

Options:
  --internal, -i         Use internal amalthea-acset-integration module (default)
  --external, -e         Use external Amalthea-acset repository
  --external-path PATH   Override path to external Amalthea-acset checkout
  --skip-external-build, -s  Skip building external Amalthea-acset (use if nothing changed)
  --copy-only, -c        Only copy generated files from external to internal, then exit
  --help, -h             Show this help message
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

# Handle copy-only mode
if [[ "$COPY_ONLY" == true ]]; then
  echo "Copy-only mode: Copying generated files from external to internal project..."

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

    echo "Files copied successfully!"

    # Show what was copied
    echo ""
    echo "Verifying symbolic execution integration in copied files:"
    if grep -q "symbolicVitruviusChoice" "$INTERNAL_PATH/consistency/src/main/java/mir/routines/amalthea2ascet/CreateAscetTaskRoutine.java" 2>/dev/null; then
      echo "✓ Symbolic execution calls found in CreateAscetTaskRoutine.java"
    else
      echo "✗ WARNING: Symbolic execution calls NOT found in CreateAscetTaskRoutine.java"
      echo "  The external project may need to be rebuilt with the updated reactions."
    fi
  else
    echo "ERROR: Internal project structure not found at $INTERNAL_PATH" >&2
    exit 1
  fi

  echo ""
  echo "Done! You can now run without --copy-only to execute with the updated files."
  exit 0
fi

if [[ "$INTERACTIVE_MODE" == true ]]; then
  echo "Please select execution mode:"
  echo "  1) INTERNAL (uses amalthea-acset-integration module)"
  echo "  2) EXTERNAL (uses external Amalthea-acset repository)"
  read -rp "Enter your choice (1 or 2): " choice

  case ${choice:-} in
    1)
      USE_EXTERNAL=false
      echo "Selected: INTERNAL"
      ;;
    2)
      USE_EXTERNAL=true
      echo "Selected: EXTERNAL"
      ;;
    *)
      echo "Invalid choice. Defaulting to INTERNAL."
      USE_EXTERNAL=false
      ;;
  esac
  echo ""
fi

# Prefer Java 17; allow override via JAVA_HOME if already set
JAVA_HOME_DEFAULT="/usr/lib/jvm/java-17-openjdk-amd64"
if [[ -z "${JAVA_HOME:-}" && -x "${JAVA_HOME_DEFAULT}/bin/java" ]]; then
  export JAVA_HOME="$JAVA_HOME_DEFAULT"
fi
if [[ -n "${JAVA_HOME:-}" ]]; then
  export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "Using java: $(java -version 2>&1 | head -1)"

# Python detection for switch-dependency
pick_python() {
  if command -v python.exe >/dev/null 2>&1 && python.exe --version >/dev/null 2>&1; then
    echo "python.exe"
  elif command -v python3 >/dev/null 2>&1 && python3 --version >/dev/null 2>&1; then
    echo "python3"
  elif command -v python >/dev/null 2>&1 && python --version >/dev/null 2>&1; then
    echo "python"
  else
    echo ""
  fi
}

PYTHON_CMD="$(pick_python)"
if [[ -z "$PYTHON_CMD" ]]; then
  echo "ERROR: Python is required to switch dependencies" >&2
  exit 1
fi

# Always restore pom.xml on exit
RESTORE_POM=false
cleanup() {
  if [[ "$RESTORE_POM" == true && -f "${SCRIPT_DIR}/pom.xml.bak" ]]; then
    mv "${SCRIPT_DIR}/pom.xml.bak" "${SCRIPT_DIR}/pom.xml"
  fi
}
trap cleanup EXIT

if [[ "$USE_EXTERNAL" == true ]]; then
  echo "Mode: EXTERNAL (full Vitruvius)"
  if [[ ! -d "$EXTERNAL_PATH" ]]; then
    echo "ERROR: External Amalthea-acset not found at $EXTERNAL_PATH" >&2
    exit 1
  fi

  if [[ "$SKIP_EXTERNAL_BUILD" == true ]]; then
    echo "Skipping external Amalthea-acset build (--skip-external-build)"
  else
    echo "Building external Amalthea-acset for Vitruvius dependencies..."
    (cd "$EXTERNAL_PATH" && mvn -q clean install -DskipTests -Dcheckstyle.skip=true)
    echo "Done."
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


  "$PYTHON_CMD" "${SCRIPT_DIR}/switch-dependency.py" internal "${SCRIPT_DIR}/pom.xml"
  RESTORE_POM=true
fi

# Resolve Galette agent location
GALETTE_AGENT=""
if [[ -f "${ROOT_DIR}/galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar" ]]; then
  GALETTE_AGENT="${ROOT_DIR}/galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar"
elif [[ -f "$HOME/.m2/repository/edu/neu/ccs/prl/galette/galette-agent/1.0.0-SNAPSHOT/galette-agent-1.0.0-SNAPSHOT.jar" ]]; then
  GALETTE_AGENT="$HOME/.m2/repository/edu/neu/ccs/prl/galette/galette-agent/1.0.0-SNAPSHOT.jar"
else
  echo "Galette agent jar not found" >&2
  exit 1
fi

echo "Galette agent: $GALETTE_AGENT"

# Build knarr-runtime with instrumentation (run from root pom)
mvn -q -f "${ROOT_DIR}/pom.xml" clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dskip=true -pl knarr-runtime -am
mvn -q -f "${ROOT_DIR}/pom.xml" process-test-resources -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dskip=true -pl knarr-runtime

INSTRUMENTED_JAVA="${SCRIPT_DIR}/target/galette/java"
if [[ ! -x "$INSTRUMENTED_JAVA/bin/java" ]]; then
  echo "Instrumented java not found at $INSTRUMENTED_JAVA/bin/java" >&2
  exit 1
fi

# Build runtime classpath
mvn -q -f "${ROOT_DIR}/pom.xml" -DincludeScope=runtime -Dmdep.outputFile="${SCRIPT_DIR}/cp.txt" -pl knarr-runtime dependency:build-classpath
if [[ ! -f "${SCRIPT_DIR}/cp.txt" ]]; then
  echo "Failed to build classpath" >&2
  exit 1
fi
CP="${SCRIPT_DIR}/target/classes:${SCRIPT_DIR}/target/test-classes:$(cat "${SCRIPT_DIR}/cp.txt")"

echo "Classpath entries: $(echo "$CP" | tr ':' '\n' | wc -l)"

mkdir -p target/galette/cache

MAIN_CLASS="edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"

set -x
"$INSTRUMENTED_JAVA/bin/java" \
  -cp "$CP" \
  -Xbootclasspath/a:"$GALETTE_AGENT" \
  -javaagent:"$GALETTE_AGENT" \
  -Dgalette.cache=target/galette/cache \
  -Dgalette.coverage=true \
  -Dsymbolic.execution.debug=true \
  -Dgalette.debug=true \
  -Dpath.explorer.max.iterations=30 \
  -DDEBUG=true \
  -Dpath.explorer.debug=true \
  -Dconstraint.solver.debug=true \
  "$MAIN_CLASS" "$@"
