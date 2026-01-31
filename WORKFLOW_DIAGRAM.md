# CocoPath Workflow Diagrams

## 1. Complete System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            USER INTERFACE LAYER                              │
│                                                                              │
│  run-symbolic-execution.sh  →  Launches exploration                         │
│  execution_paths_automatic.json  ←  Results output                          │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│                    PATH EXPLORATION ORCHESTRATION                            │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  AutomaticVitruvPathExploration.java (Main Entry Point)              │   │
│ │                                                                       │   │
│ │  main()                                                               │   │
│ │    ├─ Load Test class via reflection                                 │   │
│ │    ├─ Create PathExplorer                                            │   │
│ │    └─ Call exploreInteger()                                          │   │
│ │                                                                       │   │
│ │  executeVitruvWithInput(testInstance, concreteInteger)               │   │
│ │    ├─ [T3] insertTask.invoke(testInstance, workDir, concreteInteger)│   │
│ │    │        → Triggers Vitruvius reaction                           │   │
│ │    │        → Reaction calls GaletteSymbolicator.getOrMakeSymbolicInt│   │
│ │    │        → Creates/reuses tag + adds domain constraint           │   │
│ │    │        → Reaction calls SymbolicComparison.symbolicVitruviusChoice│ │
│ │    │        → Records switch constraint                              │   │
│ │    └─ [T4] return PathUtils.getCurPC()                              │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  PathExplorer.java (Core Algorithm)                                  │   │
│ │                                                                       │   │
│ │  exploreInteger(varName, initialValue, executor)                     │   │
│ │    │                                                                  │   │
│ │    ├─ LOOP: while (hasUnexploredPaths && iteration < MAX)           │   │
│ │    │   │                                                             │   │
│ │    │   ├─ [T0] Pass concrete value to executor                      │   │
│ │    │   │        (No tagging - reactions handle it)                  │   │
│ │    │   │                                                             │   │
│ │    │   ├─ [T1] Reset PC: PathUtils.resetPC()                       │   │
│ │    │   │                                                             │   │
│ │    │   ├─ [T2-T4] Execute: executor(concreteInteger)               │   │
│ │    │   │        → Returns PathConditionWrapper                      │   │
│ │    │   │                                                             │   │
│ │    │   ├─ [T5] Store PathRecord {inputs, constraints, time}        │   │
│ │    │   │                                                             │   │
│ │    │   ├─ Find unexplored constraint to negate                      │   │
│ │    │   │                                                             │   │
│ │    │   ├─ Solve: ConstraintSolver.solveConstraints(negated)        │   │
│ │    │   │        → New value for next iteration                      │   │
│ │    │   │                                                             │   │
│ │    │   └─ iteration++                                               │   │
│ │    │                                                                  │   │
│ │    └─ return List<PathRecord>                                       │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│                       CONSTRAINT MANAGEMENT LAYER                            │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  PathUtils.java (Constraint Collection API)                          │   │
│ │                                                                       │   │
│ │  addIntDomainConstraint(varName, min, max)                          │   │
│ │    ├─ Create IntVariable(varName)                                   │   │
│ │    ├─ Build: (min <= var) AND (var < max)                           │   │
│ │    └─ curPC.get().addConstraint(expression)                         │   │
│ │                                                                       │   │
│ │  addSwitchConstraint(varName, value)                                │   │
│ │    ├─ Create IntVariable(varName)                                   │   │
│ │    ├─ Build: var == value                                           │   │
│ │    └─ curPC.get().addConstraint(expression)                         │   │
│ │                                                                       │   │
│ │  getCurPC() → ThreadLocal<PathConditionWrapper>                     │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  PathConditionWrapper.java (Storage)                                 │   │
│ │                                                                       │   │
│ │  private List<Expression> constraints = new ArrayList<>()           │   │
│ │                                                                       │   │
│ │  addConstraint(Expression e) { constraints.add(e); }                │   │
│ │  getConstraints() { return constraints; }                            │   │
│ │  size() { return constraints.size(); }                               │   │
│ │  clear() { constraints.clear(); }                                    │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  ConstraintSolver.java (GREEN Solver Wrapper)                        │   │
│ │                                                                       │   │
│ │  solveConstraints(List<Expression> constraints)                     │   │
│ │    ├─ Convert to GREEN format                                       │   │
│ │    ├─ Invoke GREEN.solve()                                          │   │
│ │    │   └─ (Uses Z3 SMT solver internally)                           │   │
│ │    └─ Extract solution: Map<varName, value>                         │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│                       TAG PROPAGATION LAYER (Galette)                        │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  GaletteSymbolicator.java (Called from Reactions)                   │   │
│ │                                                                       │   │
│ │  getOrMakeSymbolicInt(qualifiedName, value, min, max)               │   │
│ │    ├─ Check labelToTag map for existing tag                         │   │
│ │    ├─ If exists: reuse tag (tag reuse across iterations)           │   │
│ │    ├─ If new: create Tag + IntVariable + domain constraint         │   │
│ │    ├─ Apply tag with Tainter.setTag(value, tag)                    │   │
│ │    └─ Return tagged integer                                         │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  Tag.java (Symbolic Metadata)                                        │   │
│ │                                                                       │   │
│ │  String symbolicLabel;    // e.g., "user_choice"                    │   │
│ │  Expression expression;   // GREEN expression tree                   │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  TagPropagator.java (ASM Bytecode Instrumentation)                   │   │
│ │                                                                       │   │
│ │  ✅ ENABLED Features:                                                │   │
│ │    ├─ Arithmetic operation tag propagation                           │   │
│ │    ├─ Method call shadow stack management                            │   │
│ │    └─ If/branch constraint collection                                │   │
│ │                                                                       │   │
│ │  ❌ DISABLED Features (Bytecode Verification Errors):                │   │
│ │    ├─ visitTableSwitchInsn() - domain constraint extraction          │   │
│ │    │   └─ Should call: addIntDomainConstraintAuto(tag, min, max)    │   │
│ │    └─ recordSwitchConstraint() - path constraint collection          │   │
│ │        └─ Should call: recordSwitchConstraintAuto(tag, caseValue)   │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│                          APPLICATION LAYER                                   │
│ ┌──────────────────────────────────────────────────────────────────────┐   │
│ │  Test.java (Vitruvius Transformation)                                │   │
│ │                                                                       │   │
│ │  insertTask(Path projectDir, Integer userInput)                     │   │
│ │    │                                                                  │   │
│ │    ├─ [T4] switch (userInput)  // Tagged Integer!                   │   │
│ │    │          case 0: break;    // Concrete: goes here if value=0   │   │
│ │    │          case 1: break;    // Symbolic: tag propagated         │   │
│ │    │          ...                                                    │   │
│ │    │                                                                  │   │
│ │    ├─ Build Vitruvius VSUM                                          │   │
│ │    ├─ Execute transformation                                         │   │
│ │    │   └─ (May throw exceptions - ignored)                          │   │
│ │    └─ Save results                                                   │   │
│ └──────────────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────────────┘
```

## 2. Path Exploration Loop (Detailed)

```
START: PathExplorer.exploreInteger("user_choice", 0, executor)
│
├─ iteration = 0
│  │
│  ├─ [CREATE INPUT]
│  │  taggedInteger = GaletteSymbolicator.tagInteger(0, "user_choice")
│  │  ┌─────────────────────────────────┐
│  │  │ Tagged Integer Object:          │
│  │  │  - Concrete value: 0            │
│  │  │  - Symbolic label: "user_choice"│
│  │  │  - Tag: IntVariable("user_choice") │
│  │  └─────────────────────────────────┘
│  │
│  ├─ [RESET PC]
│  │  PathUtils.getCurPC().clear()
│  │  PC = []
│  │
│  ├─ [EXECUTE]
│  │  executeVitruvWithInput(testInstance, taggedInteger)
│  │  │
│  │  ├─ concreteValue = 0  (for display/directory only)
│  │  │
│  │  ├─ ** CONSTRAINT COLLECTION POINT 1 **
│  │  │  PathUtils.addIntDomainConstraint("user_choice", 0, 5)
│  │  │  └─ Creates: (0 <= user_choice) AND (user_choice < 5)
│  │  │  PC = [(0 <= user_choice) AND (user_choice < 5)]
│  │  │
│  │  ├─ insertTask.invoke(testInstance, workDir, taggedInteger)
│  │  │  └─ Test.insertTask(workDir, taggedInteger)
│  │  │     └─ switch (taggedInteger)  // taggedInteger carries tag!
│  │  │        ├─ Concrete: value is 0 → goes to case 0
│  │  │        └─ Symbolic: tag propagated to switch operand
│  │  │           (Automatic constraint collection here is DISABLED)
│  │  │
│  │  ├─ ** CONSTRAINT COLLECTION POINT 2 **
│  │  │  PathUtils.addSwitchConstraint("user_choice", 0)
│  │  │  └─ Creates: user_choice == 0
│  │  │  PC = [(0 <= user_choice < 5), (user_choice == 0)]
│  │  │
│  │  └─ return PC
│  │
│  ├─ [STORE PATH]
│  │  PathRecord {
│  │    pathId: 1,
│  │    inputs: {user_choice: 0},
│  │    constraints: [
│  │      "(0<=user_choice)&&(user_choice<5)",
│  │      "user_choice==0"
│  │    ],
│  │    executionTime: 42865
│  │  }
│  │
│  ├─ [FIND UNEXPLORED]
│  │  Unexplored constraint: "user_choice == 0"
│  │  Negate: "user_choice != 0"
│  │
│  └─ [SOLVE FOR NEW INPUT]
│     ConstraintSolver.solve([
│       (0 <= user_choice < 5),
│       user_choice != 0
│     ])
│     → Solution: user_choice = 1
│
├─ iteration = 1
│  │
│  ├─ [CREATE INPUT]
│  │  taggedInteger = GaletteSymbolicator.tagInteger(1, "user_choice")
│  │
│  ├─ [RESET PC]
│  │  PC = []
│  │
│  ├─ [EXECUTE]
│  │  executeVitruvWithInput(testInstance, taggedInteger)
│  │  ├─ Add domain: (0 <= user_choice < 5)
│  │  ├─ Execute switch: goes to case 1
│  │  └─ Add switch: user_choice == 1
│  │  PC = [(0 <= user_choice < 5), (user_choice == 1)]
│  │
│  ├─ [STORE PATH]
│  │  PathRecord { pathId: 2, inputs: {user_choice: 1}, ... }
│  │
│  ├─ [FIND UNEXPLORED]
│  │  Already explored: user_choice == 0
│  │  Unexplored: user_choice == 1
│  │  Negate: user_choice != 1
│  │
│  └─ [SOLVE FOR NEW INPUT]
│     Solve: [(0 <= user_choice < 5), user_choice != 0, user_choice != 1]
│     → Solution: user_choice = 2
│
├─ iteration = 2
│  └─ (Same process, generates user_choice = 2)
│
├─ iteration = 3
│  └─ (Same process, generates user_choice = 3)
│
├─ iteration = 4
│  └─ (Same process, generates user_choice = 4)
│
├─ iteration = 5
│  ├─ [FIND UNEXPLORED]
│  │  Already explored: 0, 1, 2, 3, 4
│  │  Solve: [(0 <= user_choice < 5), user_choice != 0, != 1, != 2, != 3, != 4]
│  │  → No solution (all values in [0, 5) are explored)
│  │
│  └─ EXIT LOOP
│
END: Return List<PathRecord> with 5 paths
```

## 3. Vitruvius Reaction Flow (NEW - Tag Creation in Reactions)

```
┌────────────────────────────────────────────────────────────────────────┐
│                     Vitruvius Reaction Execution                         │
│                                                                          │
│  1. UserInteraction.startInteraction() returns concrete value           │
│       ↓                                                                 │
│  2. Reaction calls GaletteSymbolicator.getOrMakeSymbolicInt()          │
│       - qualifiedName: "CreateAscetTaskRoutine:execute:userChoice"      │
│       - Creates tag on first iteration                                  │
│       - Reuses tag on subsequent iterations                            │
│       - Adds domain constraint [min, max]                              │
│       ↓                                                                 │
│  3. Tagged value returned to reaction                                   │
│       ↓                                                                 │
│  4. Reaction calls SymbolicComparison.symbolicVitruviusChoice()        │
│       - Records switch constraint for specific choice                   │
│       - Uses qualified name from tag                                    │
│       ↓                                                                 │
│  5. Reaction executes appropriate transformation                        │
└────────────────────────────────────────────────────────────────────────┘
```

## 4. Constraint Collection Timeline (Single Iteration)

**Note**: Constraints are collected **inside the Vitruvius reaction** (CreateAscetTaskRoutine.java),
not in AutomaticVitruvPathExploration.java. The reaction calls GaletteSymbolicator and
SymbolicComparison via reflection to register symbolic variables and record constraints.

```
Time  Event                              Location                           PC State
──────────────────────────────────────────────────────────────────────────────────────────
T0    Pass concrete value               PathExplorer                       []
      executor.execute(0)  (no tagging yet)

T1    Reset PC                          PathExplorer                       []
      PathUtils.getCurPC().clear()

T2    Create output directory           AutomaticVitruvPathExploration     []

T3    Invoke insertTask                 AutomaticVitruvPathExploration     []
      insertTask.invoke(testInstance, workDir, concreteInteger)
      │
      └─▶ Test.insertTask(workDir, concreteInteger)
          │
          └─▶ Vitruvius VSUM triggers CreateAscetTaskRoutine reaction
              │
              ├─ T4: UserInteractor.startInteraction() returns selection
              │
              ├─ T5: ** GaletteSymbolicator.getOrMakeSymbolicInt() **
              │      qualifiedName: "CreateAscetTaskRoutine:execute:userChoice_forTask_..."
              │      → Creates tag with qualified name
              │      → Internally adds domain constraint
              │      PC = [(0 <= var < 5)]
              │
              ├─ T6: ** SymbolicComparison.symbolicVitruviusChoice() **
              │      → Records switch constraint
              │      PC = [(0 <= var < 5), (var == 0)]
              │
              └─ T7: Execute transformation based on selection

T8    Retrieve PC                       AutomaticVitruvPathExploration     [(0 <= var < 5),
      pc = PathUtils.getCurPC()                                             (var == 0)]
      Returns: PathConditionWrapper with 2 constraints

T9    Store in PathRecord               PathExplorer
      paths.add(new PathRecord(inputs, pc, time))
```

## 4. Data Structure Evolution

### Path 1 (user_choice = 0)

```
Input Creation:
┌──────────────────────┐
│ Tagged Integer       │
│  concrete: 0         │
│  tag: "user_choice"  │
│  symbolic: α         │
└──────────────────────┘

Constraint Collection:
Step 1: Domain
  [(0 <= user_choice) AND (user_choice < 5)]

Step 2: Switch
  [(0 <= user_choice) AND (user_choice < 5),
   (user_choice == 0)]

Storage:
{
  "pathId": 1,
  "symbolicInputs": {"user_choice": 0},
  "constraints": [
    "(0<=user_choice)&&(user_choice<5)",
    "user_choice==0"
  ],
  "executionTime": 42865
}
```

### Path 2 (user_choice = 1)

```
Constraint Negation (from Path 1):
  Original: user_choice == 0
  Negated:  user_choice != 0

Solving:
  Constraints: [(0 <= user_choice < 5), (user_choice != 0)]
  Solution: user_choice = 1  ✓

Input Creation:
┌──────────────────────┐
│ Tagged Integer       │
│  concrete: 1         │
│  tag: "user_choice"  │
│  symbolic: α         │
└──────────────────────┘

Constraint Collection:
  [(0 <= user_choice < 5), (user_choice == 1)]

Storage:
{
  "pathId": 2,
  "symbolicInputs": {"user_choice": 1},
  "constraints": [
    "(0<=user_choice)&&(user_choice<5)",
    "user_choice==1"
  ],
  "executionTime": 88
}
```

## 5. Component Interaction Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  [PathExplorer]                                                          │
│      │                                                                    │
│      │ 1. tagInteger(value, "user_choice")                               │
│      ├──────────────────────────────────────────────────────────────┐   │
│      │                                                               ▼   │
│      │                                                    [GaletteSymbolicator]
│      │                                                        │           │
│      │ 2. Tagged Integer                                     │           │
│      │◀──────────────────────────────────────────────────────┘           │
│      │                                                                    │
│      │ 3. getCurPC().clear()                                             │
│      ├──────────────────────────────────────────────────────────────┐   │
│      │                                                               ▼   │
│      │                                                         [PathUtils]
│      │                                                               │   │
│      │                                                               │   │
│      │ 4. executeVitruvWithInput(testInstance, taggedInteger)       │   │
│      ├──────────────────────────────────────────────────────┐       │   │
│      │                                                       ▼       │   │
│      │                                        [AutomaticVitruvPathExploration]
│      │                                                │              │   │
│      │                                                │ 5. addIntDomainConstraint()
│      │                                                ├──────────────┼──▶│
│      │                                                │              │   │
│      │                                                │ 6. invoke Test.insertTask()
│      │                                                ├──────────────┼──▶[Test]
│      │                                                │              │   │   │
│      │                                                │              │   │   │ switch(tagged)
│      │                                                │              │   │   │
│      │                                                │ 7. addSwitchConstraint()
│      │                                                ├──────────────┼──▶│
│      │                                                │              │   │
│      │                                                │ 8. getCurPC()│   │
│      │                                                ├──────────────┼──▶│
│      │                                                │              │   │
│      │ 9. PathConditionWrapper (2 constraints)       │              │   │
│      │◀───────────────────────────────────────────────┘              │   │
│      │                                                               │   │
│      │ 10. Find unexplored constraint, negate                       │   │
│      │                                                               │   │
│      │ 11. solveConstraints(negated constraints)                    │   │
│      ├──────────────────────────────────────────────────────────────┼──▶[ConstraintSolver]
│      │                                                               │        │
│      │                                                               │        │ GREEN.solve()
│      │                                                               │        │
│      │ 12. Solution: {user_choice: 1}                               │        │
│      │◀──────────────────────────────────────────────────────────────────────┘
│      │                                                               │   │
│      │ 13. LOOP back to step 1 with new value                       │   │
│      │                                                               │   │
└─────────────────────────────────────────────────────────────────────────┘
```

## 6. Automatic vs Manual Constraint Collection Comparison

### Intended Design (Automatic - DISABLED)

```
┌──────────────────────────────────────────────────────────────┐
│ Test.java: switch (userInput)                                │
│   ↓                                                           │
│ [TagPropagator] detects switch on tagged value               │
│   ↓                                                           │
│ [TagPropagator.recordSwitchConstraint()]                     │
│   ├─ Extract tag from shadow stack                           │
│   ├─ Call addIntDomainConstraintAuto(tag, 0, 5)             │
│   │   └─ Converts tag to GREEN expression automatically      │
│   ├─ Execute switch with fresh labels                        │
│   └─ At case label: recordSwitchConstraintAuto(tag, 0)      │
│       └─ Converts tag to GREEN expression automatically      │
│   ↓                                                           │
│ PC = [(tag expr >= 0) AND (tag expr < 5), (tag expr == 0)]  │
└──────────────────────────────────────────────────────────────┘

✅ Pros:
  - Fully automatic (no code changes needed)
  - Works for any switch statement
  - Tag-based (precise symbolic tracking)

❌ Cons:
  - Complex bytecode generation
  - Stack management errors
  - JVM verification failures
  - CURRENTLY BROKEN
```

### Current Implementation (Reaction-Based - WORKING)

```
┌──────────────────────────────────────────────────────────────┐
│ AutomaticVitruvPathExploration.executeVitruvWithInput()     │
│   ↓                                                           │
│ Test.insertTask(workDir, concreteInteger)                   │
│   ↓                                                           │
│ Vitruvius VSUM triggers CreateAscetTaskRoutine reaction     │
│   ↓                                                           │
│ CreateAscetTaskRoutine.execute():                            │
│   │                                                           │
│   ├─ GaletteSymbolicator.getOrMakeSymbolicInt(...)          │
│   │   └─ Creates tag + adds domain constraint automatically │
│   │   └─ Creates: (0 <= var) AND (var < 5)                  │
│   │                                                           │
│   └─ SymbolicComparison.symbolicVitruviusChoice(...)        │
│       └─ Records switch constraint                           │
│       └─ Creates: var == 0                                   │
│   ↓                                                           │
│ PC = [(0 <= var < 5), (var == 0)]                           │
└──────────────────────────────────────────────────────────────┘

✅ Pros:
  - Constraint collection in reactions (close to decision point)
  - Tag reuse across iterations via qualified name
  - Works reliably with Vitruvius framework

❌ Cons:
  - Requires reflection calls in reaction code
  - Reactions must be modified to call GaletteSymbolicator/SymbolicComparison
  - Not fully automatic (reactions need explicit instrumentation)
```

## 7. Key Insight: Constraint Collection in Reactions

```
Concrete Value Passed (WORKING):
  PathExplorer → executor(concreteInteger)
  Result: Concrete integer passed to execution wrapper
           ↓
  Test.insertTask Invocation (WORKING):
  AutomaticVitruvPathExploration invokes Test.insertTask via reflection
           ↓
  Vitruvius Reaction Triggered (WORKING):
  CreateAscetTaskRoutine executes in response to model change
           ↓
  Tag Creation in Reaction (WORKING):
  GaletteSymbolicator.getOrMakeSymbolicInt():
    - Creates tag with qualified name on first iteration
    - Reuses existing tag on subsequent iterations
    - Adds domain constraint automatically
           ↓
  Switch Constraint Recording (WORKING):
  SymbolicComparison.symbolicVitruviusChoice():
    - Records switch constraint using tag's qualified name
           ↓
  Transformation Executes:
  Reaction performs model transformation based on selection
           ↓
  PC Retrieved:
  AutomaticVitruvPathExploration retrieves constraints via PathUtils.getCurPC()
```

**Conclusion**: Constraint collection happens inside Vitruvius reactions (e.g., CreateAscetTaskRoutine.java)
using GaletteSymbolicator and SymbolicComparison. Tags are created and reused across iterations via
qualified names. The automatic bytecode-based constraint collection in TagPropagator remains disabled.

---

**End of Workflow Diagrams**
