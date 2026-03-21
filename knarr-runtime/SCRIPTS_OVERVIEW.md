# CocoPath Run Scripts Overview

This document describes the various run scripts available in the knarr-runtime module and their purposes.

## Script Summary

### 🆕 New Scripts (Created Today)

#### 1. `run-vitruvius-test.sh`
**Purpose**: Complete test runner for Vitruvius transformations with full instrumentation
- **Features**:
  - Builds all required components (galette-agent, instrumented Java)
  - Smart rebuild logic (checks timestamps)
  - Runs with bytecode interception enabled
  - Proper agent and bootclasspath configuration
- **Usage**:
  ```bash
  ./run-vitruvius-test.sh              # Run with automatic rebuilding
  ./run-vitruvius-test.sh --force      # Force rebuild all components
  ./run-vitruvius-test.sh --no-build   # Skip all builds
  ```
- **Main Class**: `AutomaticVitruvPathExploration`

#### 2. `run-symbolic-execution-instrumented.sh`
**Purpose**: Enhanced version of run-symbolic-execution.sh with proper instrumentation
- **Features**:
  - Builds galette-agent with ComparisonInterceptorVisitor
  - Creates instrumented Java runtime
  - Runs with agent instead of mvn exec:java
  - Supports all three modes (internal/external/multivar)
- **Usage**:
  ```bash
  ./run-symbolic-execution-instrumented.sh              # Interactive mode
  ./run-symbolic-execution-instrumented.sh --internal   # Internal dependencies
  ./run-symbolic-execution-instrumented.sh --external   # External Amalthea-acset
  ./run-symbolic-execution-instrumented.sh --multivar   # Multi-variable exploration
  ```

#### 3. `rebuild-instrumented-java.sh`
**Purpose**: Rebuilds instrumented Java with bytecode interception support
- **Features**:
  - Rebuilds galette-agent with new ComparisonInterceptorVisitor
  - Rebuilds galette-instrument
  - Creates fresh instrumented Java
  - Cleans caches
- **Usage**:
  ```bash
  ./rebuild-instrumented-java.sh
  ```

#### 4. `run-interception-test.sh`
**Purpose**: Simple test for bytecode interception functionality
- **Features**:
  - Runs BytecodeInterceptionTest
  - Shows comparison with/without interception
  - Useful for debugging interception issues
- **Usage**:
  ```bash
  ./run-interception-test.sh
  ```

### 📦 Existing Scripts

#### 1. `run-instrumented.sh`
**Purpose**: Original instrumented runner with basic support
- **Features**:
  - Creates instrumented Java via `mvn process-test-resources`
  - Builds Amalthea-acset if needed
  - Uses existing galette-agent from Maven repo
- **Limitation**: Does not rebuild galette-agent
- **Main Class**: `AutomaticVitruvMultiVarPathExploration`

#### 2. `run-instrumented-with-option-flags.sh`
**Purpose**: Advanced runner with multiple configuration options and build control
- **Features**:
  - Supports internal/external mode switching
  - Can copy generated files between projects
  - Python dependency switching
  - Build control flags (clean, rebuild-agent, rebuild-classes, rebuild-java)
  - Smart rebuild detection based on file existence
  - Bytecode interception enabled with debug output
  - Full parity with cleanBuildOfMain build controls
- **Usage**:
  ```bash
  ./run-instrumented-with-option-flags.sh --internal
  ./run-instrumented-with-option-flags.sh --external --external-path /path/to/Amalthea-acset
  ./run-instrumented-with-option-flags.sh --copy-only
  ./run-instrumented-with-option-flags.sh --clean              # Full clean rebuild
  ./run-instrumented-with-option-flags.sh --rebuild-agent      # Rebuild agent only
  ./run-instrumented-with-option-flags.sh --rebuild-java       # Rebuild instrumented Java only
  ```

#### 3. `run-symbolic-execution.sh`
**Purpose**: Original symbolic execution runner
- **Features**:
  - Supports internal/external/multivar modes
  - Interactive mode selection
  - Builds Amalthea-acset dependencies
- **Limitation**: Uses `mvn exec:java` (no agent support)
- **Note**: Use `run-symbolic-execution-instrumented.sh` for full instrumentation

## Key Differences Between Scripts

### Build Capabilities

| Script | Builds galette-agent | Creates Instrumented Java | Builds Amalthea-acset |
|--------|---------------------|---------------------------|----------------------|
| run-vitruvius-test.sh | ✅ | ✅ | ✅ |
| run-symbolic-execution-instrumented.sh | ✅ | ✅ | ✅ |
| run-instrumented.sh | ❌ | ✅ | ✅ |
| run-instrumented-with-option-flags.sh | ✅ (with --rebuild-agent) | ✅ | ✅ |
| run-symbolic-execution.sh | ❌ | ❌ | ✅ |
| rebuild-instrumented-java.sh | ✅ | ✅ | ❌ |

### Execution Modes

| Script | Instrumented Java | Galette Agent | Bytecode Interception |
|--------|------------------|---------------|----------------------|
| run-vitruvius-test.sh | ✅ | ✅ | ✅ |
| run-symbolic-execution-instrumented.sh | ✅ | ✅ | ✅ |
| run-instrumented.sh | ✅ | ✅ | ⚠️ (if agent has it) |
| run-instrumented-with-option-flags.sh | ✅ | ✅ | ✅ (enabled via flags) |
| run-symbolic-execution.sh | ❌ | ❌ | ❌ |

## Recommended Usage

### For Testing Bytecode Interception
Use `run-vitruvius-test.sh` or `run-interception-test.sh`:
```bash
# Full Vitruvius test with interception
./run-vitruvius-test.sh

# Simple interception test
./run-interception-test.sh
```

### For Path Exploration
Use `run-symbolic-execution-instrumented.sh`:
```bash
# With full instrumentation support
./run-symbolic-execution-instrumented.sh --external
```

### For Quick Testing Without Rebuilds
Use existing scripts with `--no-build` or `-s` flags:
```bash
./run-vitruvius-test.sh --no-build
./run-instrumented-with-option-flags.sh -s --internal
```

### For Debugging Build Issues
Use scripts with `--force` or force rebuild flags:
```bash
./run-vitruvius-test.sh --force
./run-symbolic-execution-instrumented.sh --force-rebuild
./rebuild-instrumented-java.sh
```

## Component Locations

- **Instrumented Java**: `target/galette/java/`
- **Galette Agent JAR**: `../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar`
- **Cache Directory**: `target/galette/cache/`
- **Classpath File**: `cp.txt`
- **Output Directories**: `galette-output-*/`
- **Results**: `execution_paths*.json`

## Environment Requirements

- **Java Version**: Java 17 (OpenJDK recommended)
- **Default JAVA_HOME**: `/usr/lib/jvm/java-17-openjdk-amd64`
- **Maven**: 3.6+ with settings for Vitruvius repositories
- **External Dependencies**: Amalthea-acset at `/home/anne/CocoPath/Amalthea-acset`

## Troubleshooting

### Build Failures
1. Check Java version: `java -version` (should be 17)
2. Clear Maven cache: `rm -rf ~/.m2/repository/edu/neu/ccs/prl/galette`
3. Force rebuild: Add `--force` or `--force-rebuild` flag

### Missing Dependencies
1. Build Amalthea-acset: `cd ~/CocoPath/Amalthea-acset && mvn clean install`
2. Build galette modules: `cd ~/CocoPath/CocoPath && mvn clean install`

### Interception Not Working
1. Check galette-agent was rebuilt: `ls -la ../galette-agent/target/*.jar`
2. Verify ComparisonInterceptorVisitor exists in agent
3. Run with debug flags: `-Dgalette.debug=true -Dgalette.concolic.interception.debug=true`

### Out of Memory
Add JVM options to scripts:
```bash
-Xmx4G -XX:MaxMetaspaceSize=512M
```