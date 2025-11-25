# Input Generation from Path Constraints using GreenSolver

## Overview

This guide explains how to use GreenSolver to generate concrete input values for each execution path based on collected constraints.

## Current Approach: Constraint Negation

The system uses **constraint negation** to generate inputs for different paths. Here's how it works:

### Step-by-Step Process

#### 1. Execute First Path & Collect Constraints

```java
// Path 1: user_choice = 0
Tag tag = GaletteSymbolicator.makeSymbolicInt("user_choice", 0);
int tagged = Tainter.setTag(0, tag);

// Execute transformation
test.insertTask(workDir, tagged);

// GreenSolver collects constraint
PathConditionWrapper pc = PathUtils.getCurPC();
// pc.constraints = [(user_choice == 0)]
```

**Constraint collected**: `(user_choice == 0)`

#### 2. Generate Alternative Inputs by Negating Constraints

**File**: `GaletteSymbolicator.java:310-414`

```java
public static InputSolution solvePathCondition() {
    PathConditionWrapper pc = PathUtils.getCurPC();
    Expression constraint = pc.toSingleExpression();

    // Extract solution from constraint
    InputSolution solution = new InputSolution();
    extractSolutionFromConstraint(constraint, solution);

    return solution;
}

private static Map<String, Object> generateAlternativeValues(Expression constraint) {
    Map<String, Object> alternatives = new HashMap<>();

    // For constraint: (user_choice == 0)
    // Extract: variable = "user_choice", operator = EQ, threshold = 0

    String variable = extractVariableName(constraint);
    Operator operator = extractOperator(constraint);
    Double threshold = extractThreshold(constraint);

    // NEGATE the constraint to find alternative value
    Double alternativeValue = negateConstraint(operator, threshold);
    // EQ negated: 0 + 1.0 = 1.0

    alternatives.put(variable, alternativeValue);
    return alternatives;
}

private static Double negateConstraint(Operator op, Double threshold) {
    switch (op) {
        case EQ:  // x == threshold → x != threshold
            return threshold + 1.0;  // Return: threshold + 1
        case NE:  // x != threshold → x == threshold
            return threshold;         // Return: threshold
        case GT:  // x > threshold → x <= threshold
            return threshold - 0.1;   // Return: threshold - 0.1
        case GE:  // x >= threshold → x < threshold
            return threshold - 0.1;
        case LT:  // x < threshold → x >= threshold
            return threshold + 0.1;
        case LE:  // x <= threshold → x > threshold
            return threshold + 0.1;
        default:
            return threshold + 1.0;
    }
}
```

**Result**:
- Path 1 constraint: `(user_choice == 0)` → Next input: `1`
- Path 2 constraint: `(user_choice == 1)` → Next input: `2`
- Path 3 constraint: `(user_choice == 2)` → Next input: `3`
- etc.

### Example: Generating All 5 Paths

**File**: `AutomaticSymbolicExecutor.java:347-424`

```java
// Define all possible values for user_choice
Map<String, List<Object>> variableRanges = new HashMap<>();
variableRanges.put("user_choice", Arrays.asList(0, 1, 2, 3, 4));

// Generate all input combinations
List<Map<String, Object>> allInputs = generateInputCombinations(baseInputs, variableRanges);

// Result:
// [
//   {"user_choice": 0},  ← Path 1: InterruptTask
//   {"user_choice": 1},  ← Path 2: PeriodicTask
//   {"user_choice": 2},  ← Path 3: SoftwareTask
//   {"user_choice": 3},  ← Path 4: TimetableTask
//   {"user_choice": 4}   ← Path 5: Decide Later
// ]

// Execute each path
for (Map<String, Object> inputs : allInputs) {
    PathUtils.resetPC();  // Clear previous constraints

    // Create symbolic value with this input
    int userChoice = (int) inputs.get("user_choice");
    Tag tag = GaletteSymbolicator.makeSymbolicInt("user_choice_" + pathId, userChoice);
    int tagged = Tainter.setTag(userChoice, tag);

    // Execute transformation
    test.insertTask(workDir, tagged);

    // Collect constraints for this path
    PathConditionWrapper pc = PathUtils.getCurPC();
    // Path 0: [(user_choice_0 == 0)]
    // Path 1: [(user_choice_1 == 1)]
    // etc.
}
```

## Advanced: Using External SAT Solver

For complex constraints (multiple variables, non-linear relationships), you can use an external SAT solver.

### Architecture

```
Galette/Knarr  →  GreenSolver  →  SAT Server (Z3, CVC4, etc.)
    ↓                  ↓                ↓
 Collect        Convert to        Solve constraints
constraints     Green AST         Return satisfying
                                  assignments
```

### Setup SAT Solver Server

**File**: `GaletteSymbolicator.java:489-535`

```java
// Configuration
static String SERVER_HOST = System.getProperty("SATServer", "127.0.0.1");
static int SERVER_PORT = Integer.valueOf(System.getProperty("SATPort", "9090"));

public static boolean connectToServer() {
    serverConnection = new Socket(SERVER_HOST, SERVER_PORT);
    return serverConnection.isConnected();
}

public static InputSolution solveViaServer() {
    // Send Green expression to server
    ObjectOutputStream out = new ObjectOutputStream(serverConnection.getOutputStream());

    PathConditionWrapper pc = PathUtils.getCurPC();
    Expression constraint = pc.toSingleExpression();

    // Send constraint
    out.writeObject(constraint);
    out.flush();

    // Receive solution
    ObjectInputStream in = new ObjectInputStream(serverConnection.getInputStream());
    InputSolution solution = (InputSolution) in.readObject();

    return solution;
}
```

### Using SAT Solver

```java
// Start SAT solver server (separate process)
// e.g., Z3 server listening on port 9090

// In your code
System.setProperty("SATServer", "127.0.0.1");
System.setProperty("SATPort", "9090");

// Connect to solver
if (GaletteSymbolicator.connectToServer()) {
    // Solve constraint
    InputSolution solution = GaletteSymbolicator.solveViaServer();

    if (solution.isSatisfiable()) {
        int nextUserChoice = solution.getIntValue("user_choice", 0);
        System.out.println("Next input to explore: " + nextUserChoice);
    } else {
        System.out.println("No more paths to explore (UNSAT)");
    }
}
```

## Complete Example: Path Exploration

Here's a complete example showing input generation for all paths:

```java
import edu.neu.ccs.prl.galette.vitruvius.AutomaticSymbolicExecutor;
import java.util.*;

public class PathExplorationExample {

    public static void main(String[] args) {
        // METHOD 1: Manual enumeration (current approach)
        explorePathsManually();

        // METHOD 2: Constraint-based generation (advanced)
        // explorePathsWithSolver();
    }

    /**
     * METHOD 1: Enumerate all possible values manually
     * This is what the current system does
     */
    public static void explorePathsManually() {
        System.out.println("=== Manual Path Exploration ===\n");

        // Define possible values for decision variables
        Map<String, List<Object>> variableRanges = new HashMap<>();
        variableRanges.put("user_choice", Arrays.asList(0, 1, 2, 3, 4));

        // Base inputs (if any)
        Map<String, Object> baseInputs = new HashMap<>();

        // Execute all paths
        List<String> results = AutomaticSymbolicExecutor.exploreAllPaths(
            baseInputs,
            variableRanges,
            inputs -> {
                int userChoice = (int) inputs.get("user_choice");
                return executeTransformation(userChoice);
            }
        );

        System.out.println("\nExplored " + results.size() + " paths");
    }

    /**
     * METHOD 2: Generate inputs by solving constraints
     * This requires SAT solver integration
     */
    public static void explorePathsWithSolver() {
        System.out.println("=== Constraint-Based Path Exploration ===\n");

        List<InputSolution> pathInputs = new ArrayList<>();
        Set<String> exploredConstraints = new HashSet<>();

        // Start with initial input
        InputSolution currentInput = new InputSolution();
        currentInput.setValue("user_choice", 0);

        while (currentInput != null) {
            // Execute with current input
            int userChoice = currentInput.getIntValue("user_choice", 0);
            executeTransformation(userChoice);

            // Collect constraints
            PathConditionWrapper pc = PathUtils.getCurPC();
            String constraintStr = pc.toString();

            if (exploredConstraints.contains(constraintStr)) {
                break;  // Already explored this path
            }
            exploredConstraints.add(constraintStr);

            System.out.println("Path " + pathInputs.size() + ": " + constraintStr);
            pathInputs.add(currentInput);

            // Generate next input by negating constraint
            currentInput = generateNextInput(pc);

            // Reset for next iteration
            PathUtils.resetPC();
        }

        System.out.println("\nExplored " + pathInputs.size() + " paths");
    }

    /**
     * Generate next input by negating current path constraint
     */
    private static InputSolution generateNextInput(PathConditionWrapper pc) {
        if (pc.isEmpty()) {
            return null;
        }

        // Get current constraint
        Expression constraint = pc.toSingleExpression();

        // Negate it to find alternative path
        Expression negated = negateExpression(constraint);

        // Solve negated constraint
        return solveConstraint(negated);
    }

    /**
     * Negate a Green expression
     */
    private static Expression negateExpression(Expression expr) {
        if (expr instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) expr;
            Operator op = binOp.getOperator();

            // Negate operator
            Operator negatedOp = negateOperator(op);

            return new BinaryOperation(negatedOp, binOp.left, binOp.right);
        }

        // For complex expressions, use NOT
        return new UnaryOperation(Operator.NOT, expr);
    }

    /**
     * Negate an operator
     */
    private static Operator negateOperator(Operator op) {
        switch (op) {
            case EQ: return Operator.NE;
            case NE: return Operator.EQ;
            case GT: return Operator.LE;
            case GE: return Operator.LT;
            case LT: return Operator.GE;
            case LE: return Operator.GT;
            default: return op;
        }
    }

    /**
     * Solve constraint to get input values
     * Can use SAT solver or simple extraction
     */
    private static InputSolution solveConstraint(Expression constraint) {
        // Simple approach: extract from equality constraint
        if (constraint instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) constraint;

            if (binOp.left instanceof Variable && binOp.right instanceof IntConstant) {
                String varName = ((Variable) binOp.left).getName();
                int value = ((IntConstant) binOp.right).getValue();

                InputSolution solution = new InputSolution();
                solution.setValue(varName, value);
                return solution;
            }
        }

        // For complex constraints, would use SAT solver here
        return GaletteSymbolicator.solvePathCondition();
    }

    private static String executeTransformation(int userChoice) {
        // Execute actual transformation
        // ... Vitruvius code here ...
        return "Result for choice " + userChoice;
    }
}
```

## Output Format

### InputSolution Object

```java
InputSolution solution = new InputSolution();

// Set values
solution.setValue("user_choice", 1);
solution.setValue("attribute_name", "InterruptTask");
solution.setValue("threshold", 42.0);

// Get values
int userChoice = solution.getIntValue("user_choice", 0);
String name = solution.getStringValue("attribute_name", "default");
double threshold = solution.getDoubleValue("threshold", 0.0);

// Check satisfiability
if (solution.isSatisfiable()) {
    System.out.println("Solution: " + solution);
    // Output: InputSolution{user_choice=1, attribute_name=InterruptTask, threshold=42.0}
} else {
    System.out.println("UNSATISFIABLE - no more paths");
}
```

### JSON Output

**File**: `execution_paths.json`

```json
[
  {
    "path_id": 0,
    "description": "Create InterruptTask",
    "constraints": ["(user_choice_0 == 0)"],
    "inputs": {
      "user_choice": 0
    },
    "execution_time_ms": 26.8
  },
  {
    "path_id": 1,
    "description": "Create PeriodicTask",
    "constraints": ["(user_choice_1 == 1)"],
    "inputs": {
      "user_choice": 1
    },
    "execution_time_ms": 28.3
  }
  // ... more paths
]
```

## Key Points

### Current Implementation

✅ **What works now**:
- Manual enumeration of input values
- Constraint collection via GreenSolver
- Simple constraint negation for next input
- Works for discrete choices (0, 1, 2, 3, 4)

⚠️ **Limitations**:
- Requires knowing all possible values upfront
- Simple negation logic (adds 1.0 to threshold)
- No complex constraint solving

### With SAT Solver Integration

✅ **Benefits**:
- Automatic input generation from constraints
- Handles complex constraints (multiple variables, non-linear)
- Finds edge cases automatically
- No need to enumerate all values

⚠️ **Requirements**:
- External SAT solver server (Z3, CVC4)
- Network communication overhead
- More complex setup

## Practical Usage

For **Vitruvius symbolic execution**, the current manual approach works well:

```java
// In AutomaticSymbolicVitruvTest.java
public void exploreAllUserChoices() {
    // Known values: user can choose 0, 1, 2, 3, or 4
    for (int userChoice = 0; userChoice <= 4; userChoice++) {
        // Create symbolic value
        Tag tag = GaletteSymbolicator.makeSymbolicInt("user_choice_" + userChoice, userChoice);
        int tagged = Tainter.setTag(userChoice, tag);

        // Execute transformation
        test.insertTask(workDir, tagged);

        // Constraints collected automatically!
        // Output: (user_choice_X == X)
    }
}
```

**Why this works**:
- User choices are discrete (5 options)
- All possible values are known
- Simple to implement and understand
- No external dependencies

**When you'd need SAT solver**:
- Continuous values (e.g., `thickness > 5.0`)
- Multiple interrelated variables
- Unknown number of paths
- Complex predicates

## References

**Key Files**:
- `GaletteSymbolicator.java:248-414` - Input generation logic
- `InputSolution.java` - Solution container
- `AutomaticSymbolicExecutor.java:347-452` - Path exploration
- `PathConditionWrapper.java` - Constraint storage

**Related Docs**:
- `GREENSOLVER_INTEGRATION.md` - How GreenSolver works
- `HOW_TO_RUN.md` - Running symbolic execution
- `execution_paths.json` - Example output
