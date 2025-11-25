# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **Galette**, a dynamic taint tracking system for the JVM, enhanced with **Knarr integration** for symbolic execution in model transformations. The project implements bytecode instrumentation to track information flow through Java programs and includes specialized support for concolic execution.

**Key Integration**: The project now includes a complete **Amathea-ASCET model transformation example** that demonstrates symbolic execution in real-world model-driven engineering scenarios using the Vitruvius framework.

## Build Commands

### Primary Build Commands
- `mvn -DskipTests install` - Build all modules and install to local repository
- `mvn clean test` - Run all tests
- `mvn -pl :galette-integration-tests verify` - Run integration tests specifically
- `mvn clean package -DskipTests` - Build without running tests

### Module-Specific Commands
- `cd galette-agent && mvn clean package` - Build the Galette Java agent
- `cd galette-instrument && mvn clean package` - Build the instrumentation tool
- `cd knarr-runtime && mvn clean test` - Run Knarr symbolic execution tests
- `cd knarr-runtime && ./run-example.sh` - Run Knarr integration example
- `cd amathea-acset-integration && mvn clean compile` - Build Amathea-ASCET integration
- `cd amathea-acset-integration/vsum && mvn exec:java -Dexec.mainClass="tools.vitruv.methodologisttemplate.vsum.AutomaticSymbolicVitruvTest"` - Run Amathea-ASCET symbolic execution demo

### Code Quality
- `mvn checkstyle:check` - Run checkstyle validation (uses Google Java Style)
- `mvn spotless:check` - Run Spotless code formatting check (requires Java 11+)
- `mvn spotless:apply` - Apply Spotless formatting

## Key Architecture Components

### Core Galette Components
- **galette-agent**: Java instrumentation agent that performs runtime taint tracking
- **galette-instrument**: Tool for creating instrumented Java installations
- **galette-maven-plugin**: Maven integration for automated instrumentation

### Knarr Symbolic Execution Extension
- **knarr-runtime**: Symbolic execution engine built on Galette APIs
  - `GaletteSymbolicator` - Main symbolic execution engine (451 lines)
  - `ArraySymbolicTracker` - Array operations tracking (357 lines) 
  - `StringSymbolicTracker` - String operations tracking (523 lines)
  - `CoverageTracker` - Path and branch coverage (408 lines)

### Amathea-ASCET Model Transformation Integration  
- **amathea-acset-integration**: Complete Vitruvius-based model transformation example
  - Multi-level metamodel architecture (Amathea ↔ ASCET)
  - Symbolic execution of user selection dialogs (5 task type choices)
  - Real-world model-driven engineering scenario
  - `AutomaticSymbolicVitruvTest` - Fully automatic symbolic execution test framework
  - Integration with existing Vitruvius reactions system

### Integration Patterns

#### 1. Knarr Extension (Clean Wrapper Pattern)
```java
// Business logic remains unchanged  
BrakeDiscTarget result = BrakeDiscTransformationClean.transform(source, input);

// Symbolic analysis added via wrapper
SymbolicValue<Double> symbolicInput = makeSymbolicDouble("input_label", input);
// Path constraints automatically collected during execution
```

#### 2. Amathea-ASCET Integration (Vitruvius Reactions Pattern)
```java
// Original Vitruvius reaction with user dialog:
val Integer selected = userInteractor
    .singleSelectionDialogBuilder
    .message(userMsg)
    .choices(options)
    .startInteraction()

// Enhanced with symbolic execution:
SymbolicValue<Integer> symbolicSelection = makeSymbolicInt("user_choice", selected);
// Path constraints collected automatically when switch statement executes
switch (symbolicSelection.getValue()) {
    case 0: createInterruptTask(...); break;    // user_choice == 0
    case 1: createPeriodicTask(...); break;    // user_choice == 1  
    case 2: createSoftwareTask(...); break;    // user_choice == 2
    // ... constraints: user_choice == 0, user_choice == 1, etc.
}
```

## Runtime Requirements

### Java Agent Setup
Galette requires **both** components for full functionality:
1. **Instrumented Java Installation**: Created via `galette-instrument` or Maven plugin
2. **Galette Agent**: Loaded via JVM arguments

Required JVM arguments:
```bash
-javaagent:/path/to/galette-agent-1.0.0-SNAPSHOT.jar
-Xbootclasspath/a:/path/to/galette-agent-1.0.0-SNAPSHOT.jar
```

### Creating Instrumented Java
```bash
# Via Maven plugin (recommended)
mvn process-test-resources  # Creates target/galette/java/

# Via standalone tool
java -jar galette-instrument/target/galette-instrument-1.0.0-SNAPSHOT.jar $JAVA_HOME ./instrumented-java
```

## Key APIs

### Taint Tracking API
- `Tainter.setTag(value, tag)` - Associate taint tag with value
- `Tainter.getTag(value)` - Retrieve taint tag from value
- `Tag.of("label")` - Create taint tag with label

### Symbolic Execution API (Knarr Extension)
- `SymbolicExecutionWrapper.makeSymbolicDouble(label, value)` - Create symbolic value
- `PathUtils.getCurPC()` - Get current path constraints
- `SymbolicExecutionWrapper.analyzePathConstraints()` - Analyze collected constraints

## Testing

### Test Structure
- **galette-integration-tests**: Core Galette functionality tests
- **knarr-runtime/src/test**: Symbolic execution tests (17 tests, 100% pass rate)
- **galette-benchmark**: Performance and correctness benchmarks
- **amathea-acset-integration/vsum**: Model transformation symbolic execution tests

### Running Tests
```bash
# All tests
mvn clean test

# Integration tests only
mvn -pl :galette-integration-tests verify

# Knarr symbolic execution tests
cd knarr-runtime && mvn test

# Amathea-ASCET symbolic execution demo
cd amathea-acset-integration/vsum && mvn exec:java -Dexec.mainClass="tools.vitruv.methodologisttemplate.vsum.AutomaticSymbolicVitruvTest"

# Performance benchmarks
cd galette-benchmark && mvn exec:java
```

### Amathea-ASCET Example Features
- **Multi-path exploration**: Automatically tests all 5 user choice paths (0-4)
- **Constraint collection**: Gathers path constraints like `user_choice == 0`, `user_choice == 1`
- **Vitruvius integration**: Works with actual `.reactions` files and VSUM
- **Metamodel transformation**: Amathea ComponentContainer → ASCET tasks (InterruptTask, PeriodicTask, etc.)
- **Output analysis**: Saves both transformed models and constraint analysis

## Important Notes

### Platform Compatibility
- **Build Requirements**: Java 17 (for building)
- **Runtime Support**: Java 8-21 (with proper instrumentation)
- **Primary Development Platform**: WSL2/Linux (Windows native has path limitations)

### Oracle Java Limitation
Oracle JDK installations cannot be used with Galette due to cryptographic JAR signature requirements. Use OpenJDK, Adoptium, or Amazon Corretto instead.

### Debugging
Set these properties for verbose output:
- `-Dgalette.debug=true` - Enable Galette debug logging
- `-Dsymbolic.execution.debug=true` - Enable symbolic execution debug output
- `-Dgalette.coverage=true` - Enable coverage tracking

## Module Dependencies

Key modules in dependency order:
1. **galette-agent** (foundation)
2. **galette-instrument** (depends on agent)
3. **knarr-runtime** (depends on agent, adds symbolic execution)
4. **galette-integration-tests** (depends on agent)
5. **galette-benchmark** (depends on agent and knarr-runtime)
6. **amathea-acset-integration** (depends on knarr-runtime, adds Vitruvius model transformations)

## Key File Locations

### Main Examples
- `AmathaeaModelTransformationExample.java` - Standalone Amathea-ASCET transformation demo
- `AmatheaAscetTransformation.java` - Core transformation logic with symbolic execution support
- `AutomaticSymbolicVitruvTest.java` - Fully automatic symbolic execution with Vitruvius integration

### Symbolic Execution Entry Points
- User selection at `templateReactions.reactions:78-82` - Where `userInteractor.singleSelectionDialog` creates integer choices (0-4)
- Transform logic in `AmatheaAscetTransformation.transform()` - Where switch statements collect path constraints  
- Automatic path exploration in `AutomaticSymbolicExecutor.exploreAllPaths()` - Where symbolic values are created and paths explored automatically

### Critical Integration Points
- **Integer symbolic values**: Created via `SymbolicExecutionWrapper.makeSymbolicInt("user_choice", selection)`
- **Path constraint collection**: Happens automatically during switch statement execution with Galette tags
- **Multi-path exploration**: `exploreAllUserChoices()` method tests all 5 decision paths