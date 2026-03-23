Below is a build plan for **adding Knarr-style symbolic-expression propagation** to **CoCoPath** on the `comparison-interception-internal` branch.

## 1. What this implementation is trying to achieve

The goal is to move the branch from **“taint tells us that a branch depends on symbolic input”** to **“the taint/shadow state carries the full symbolic expression that reached the branch.”** In Knarr, this is what lets a later comparison recover a predicate like `a + b > 10` without manually registering it in the CPR. Knarr does this by turning the taint tag into an abstract expression and propagating that expression as computation proceeds; when the branch is reached, the branch condition already carries the full symbolic expression. ([jonbell.net][1])

That is exactly the missing capability in the current branch. Right now, the branch already intercepts comparisons and conditional jumps via `ComparisonInterceptorVisitor`, but arithmetic propagation in `TagPropagator` still uses tag union, which is dependency tracking, not expression construction. The current `GaletteGreenBridge` also maps labels to variables rather than systematically propagating composed Green expressions. ([GitHub][2])

## 2. Why this matters for CoCoPath specifically

Your paper’s CoCoPath design separates **symbolic propagation** from **constraint construction** and presents two mechanisms: bytecode-level automatic extraction (Mechanism A) and CPR-level explicit registration (Mechanism B). The evaluated prototype in the paper uses CPR-level registration because reliable bytecode-level extraction was not yet realized inside the Vitruvius runtime. 

The `comparison-interception-internal` branch is the natural place to realize more of **Mechanism A**. It already intercepts bytecode comparisons and jumps, but without Knarr-style expression propagation it can only reliably recover direct comparisons over symbolic values, not compound expressions built earlier in the dataflow. ([GitHub][2])

## 3. Ground truth about the current branch

Another agent implementing this should begin from the following understanding of the branch:

`ComparisonInterceptorVisitor` already rewrites comparison instructions (`LCMP`, `FCMPL`, `DCMPG`, etc.) and jump instructions such as `IF_ICMP*` to instrumented runtime helpers. That part is in place. ([GitHub][2])

`TagPropagator` still handles arithmetic instructions such as `IADD`, `ISUB`, `IMUL`, `DADD`, `DMUL`, `LCMP`, etc. with `TAG_UNION`, meaning result tags are just merged labels, not symbolic ASTs. ([GitHub][3])

`GaletteGreenBridge` currently converts a `Tag` to a Green expression mostly by taking the first label and creating a variable for it; it explicitly notes that multi-label handling is still a TODO. ([GitHub][4])

Galette itself is a **dynamic taint tracking** system for modern JVMs that prioritizes precise and robust taint propagation on modern Java versions. It is not, by itself, a symbolic execution engine; symbolic-expression propagation must be added on top. ([jonbell.net][5])

## 4. Desired end state

After this work, CoCoPath on this branch should support the following execution model:

* A symbolic user input starts as a **Green variable expression** attached to the corresponding runtime value.
* Every JVM arithmetic, bitwise, conversion, string, and relevant library operation builds a **new symbolic expression** for the produced value, rather than unioning labels.
* Comparison and branch interception consume those propagated expressions and add the correct Green predicate to the current path condition.
* Manual CPR-level registration remains available as a fallback, but simple arithmetic predicates in Java bytecode no longer require it. This aligns the implementation much more closely with the Knarr architecture described in Confetti. ([jonbell.net][1])

## 5. Architectural decision: do not overload `Tag` with Green ASTs

The cleanest implementation on top of Galette is **not** to store Green `Expression` objects directly inside `Tag`. Instead, keep Galette’s `Tag` as a lightweight identity carrier, and add a **parallel expression shadow domain** keyed by those tags.

This is the right fit for Galette because:

* Galette’s existing design centers on propagating `Tag`s through shadow locals and the operand stack. ([jonbell.net][5])
* The current code already hints at a separate bridge layer (`GaletteGreenBridge`) and at tag→expression association rather than direct expression-in-tag storage. ([GitHub][4])

So the implementation should treat:

* `Tag` = stable symbolic identity / lookup key
* `ExpressionStore` = authoritative map `Tag -> Green Expression`

That yields Knarr-like semantics without forcing invasive changes to Galette internals.

## 6. Core implementation strategy

### 6.1 Add an expression store

Introduce a runtime component in `knarr-runtime`, for example:

* `SymbolicExpressionStore`
* thread-local or execution-local
* API:

  * `associate(Tag tag, Expression expr)`
  * `Expression lookup(Tag tag)`
  * `Tag freshTagFor(Expression expr)`
  * `boolean isSymbolic(Tag tag)`
  * `clear()`

This store must be reset per CoCoPath exploration run and per thread if multithreaded execution is possible.

The critical rule is:

> Every value that is symbolic must have a `Tag`, and every symbolic `Tag` must have a corresponding Green `Expression`.

This becomes the invariant the rest of the system relies on.

### 6.2 Add expression-building helpers

Create a runtime helper class that mirrors Knarr-style semantics for bytecodes, for example:

* `SymbolicOps`

  * `binaryIntOp(int concreteLeft, Tag leftTag, int concreteRight, Tag rightTag, Opcode op) -> ResultWithTag`
  * `binaryLongOp(...)`
  * `binaryFloatOp(...)`
  * `binaryDoubleOp(...)`
  * `unaryIntOp(...)`
  * `unaryLongOp(...)`
  * `castOp(...)`
  * `stringEquals(...)`
  * `stringStartsWith(...)`
  * etc.

Each helper should:

1. compute the **concrete** result exactly as the JVM would,
2. look up symbolic expressions for symbolic operands,
3. if neither operand is symbolic, return the existing empty/non-symbolic tag,
4. if at least one operand is symbolic, build a Green `Operation` node,
5. allocate or reuse a fresh result tag,
6. associate the result tag with the new expression.

This is the heart of the implementation.

### 6.3 Keep comparison interception, but make it expression-aware

Retain `ComparisonInterceptorVisitor`, but change the runtime helpers it calls so they no longer rely on raw labels or concrete-value guessing. Instead, they should:

* recover operand tags,
* look up full expressions from the expression store,
* construct the correct relational predicate, and
* append it to the current path condition.

This lets the branch predicate become `((a + b) > 10)` rather than just “some tagged thing compared to 10.”

## 7. Exact code areas to change

### 7.1 `galette-agent`: instrumentation

The major instrumentation gap is arithmetic. Right now arithmetic stays in `TagPropagator` as tag union only. ([GitHub][3])

The implementing agent should add a new visitor, ideally separate from `ComparisonInterceptorVisitor`, such as:

* `ArithmeticInterceptorVisitor`

It should rewrite bytecodes for the operations you want to support first:

* `IADD, ISUB, IMUL, IDIV, IREM`
* `LADD, LSUB, LMUL, LDIV, LREM`
* `FADD, FSUB, FMUL, FDIV, FREM`
* `DADD, DSUB, DMUL, DDIV, DREM`
* `INEG, LNEG, FNEG, DNEG`
* `IAND, IOR, IXOR, LAND, LOR, LXOR`
* `ISHL, ISHR, IUSHR, LSHL, LSHR, LUSHR`
* integer/long/float/double casts
* optionally `String.equals`, `startsWith`, `contains`, and concatenation if you want parity with Knarr’s string support trajectory. Knarr explicitly instruments common string operations. ([jonbell.net][1])

For each rewrite:

* preserve JVM semantics exactly,
* fetch shadow tags for operands,
* call a static helper that returns concrete result plus result tag,
* write the returned tag back into shadow state.

If a full instruction rewrite is too invasive, phase 1 can leave concrete arithmetic untouched and only add **parallel instrumentation calls** that build the symbolic result tag in shadow state while leaving the original operand/result values alone.

### 7.2 `galette-agent`: shadow-stack/local integration

Because Galette already tracks shadow locals and stack movement, do not replace that machinery. Extend it so that when an arithmetic instruction executes, it no longer uses plain `TAG_UNION` for symbolic-result-producing instructions. Instead, delegate to the new runtime symbolic op helper.

`TagPropagator` is where this behavior lives today, so this file becomes the central place to swap out union-only propagation for expression-aware propagation on result-producing instructions. ([GitHub][3])

### 7.3 `knarr-runtime`: expression bridge

Refactor `GaletteGreenBridge` so that it becomes a thin adapter over the new expression store, not the primary symbolic semantics engine. Right now it creates variables from labels and mostly uses the first label. That should become fallback behavior only for initial symbolic inputs or legacy/manual paths. ([GitHub][4])

New behavior:

* if `Tag` has an associated expression in the store, return it,
* else if `Tag` corresponds to a base symbolic input label, create/retrieve a variable and associate it,
* else return a concrete constant.

### 7.4 `knarr-runtime`: path-condition creation

Refactor the comparison helpers used by `ComparisonInterceptorVisitor` so that every intercepted comparison:

* gets concrete operands and their tags,
* resolves left and right Green expressions,
* builds the correct relational `Operation`,
* adds the taken-branch predicate to `curPC`,
* returns the same concrete boolean/comparison result as the JVM.

Do **not** add arithmetic expressions directly to the path condition as a side effect of arithmetic. Arithmetic should only produce expressions for values. Path constraints should only be added when a control-flow predicate or explicit symbolic branch is encountered. That matches CoCoPath’s conceptual model of collecting path constraints at decision points.

## 8. Representation choices for Green expressions

Use Green ASTs directly as the symbolic IR, because CoCoPath already targets Green/Z3 and the rest of the stack already expects `za.ac.sun.cs.green.expr.Expression`. 

Recommended mapping:

* integer arithmetic -> `Operation(ADD/SUB/MUL/DIV/MOD, left, right)`
* comparisons -> `Operation(EQ/NE/LT/LE/GT/GE, left, right)`
* unary negation -> `Operation(NEG, expr)`
* casts:

  * integer widening can often preserve the underlying numeric expression,
  * narrowing casts need an explicit cast node if Green supports it, otherwise a conservative approximation or a dedicated wrapper,
* booleans -> 0/1 integer convention, if consistent with your current Green integration,
* strings:

  * only implement if Green/the solver pipeline already supports the required operators.

Do not encode expression identity via `Tag.of("expr_" + hash(expr))` as the primary mechanism. That is fragile and makes aliasing and reuse hard to reason about. Instead, generate a fresh symbolic result tag and associate it directly with the concrete `Expression`.

## 9. Concrete phased roadmap

### Phase 0: baseline characterization

Before changing anything, add tests that expose the current limitation.

Minimal examples:

* `x = sym + 5; if (x > 10) ...`
* `x = a + sym; if (x > 10) ...`
* `x = sym1 + sym2; if (x > 10) ...`
* `x = sym1 * 2; if (x < 7) ...`
* `x = sym1 + sym2; y = x - 3; if (y == 0) ...`

Expected current behavior on this branch: branch is detected as depending on symbolic data, but the recorded constraint is either incomplete or value/label-based rather than the full arithmetic expression. This baseline will prove the improvement.

### Phase 1: support integer arithmetic only

Implement expression propagation for:

* `IADD, ISUB, IMUL, IDIV, IREM`
* `INEG`
* `IF_ICMP*`
* compare-to-zero integer branches if present in the codebase

This is enough to validate the architecture and cover the most common CPR conditions.

Acceptance criteria:

* the path condition for `if (a + b > 10)` becomes a Green AST rooted at `GT(ADD(expr(a), expr(b)), 10)`,
* solver-generated models can negate this predicate and produce new concrete inputs.

### Phase 2: support long/double/float arithmetic

Add:

* `L*`, `D*`, `F*` operations,
* `LCMP`, `FCMP*`, `DCMP*`,
* proper conversions and comparison semantics.

Acceptance criteria:

* no loss of concrete semantics,
* correct predicate construction across these types.

### Phase 3: support casts and bit operations

Implement:

* numeric casts,
* bitwise ops and shifts where meaningful.

Be explicit that shifts and some bitvector semantics may need conservative handling if Green modeling is weak. If needed, gate these behind a feature flag and initially fall back to dependency-only propagation for unsupported operations.

### Phase 4: support strings and selected library methods

Only do this if it matters for your transformations. Knarr supports common string operations by explicit instrumentation. If CoCoPath decision logic uses `equals`, `startsWith`, or string concatenation, then instrument them; otherwise skip them for now. ([jonbell.net][1])

### Phase 5: integrate with Vitruvius/CoCoPath examples

Re-run the AMALTHEA/ASCET and BrakeSystem/ControlSystem style examples and add at least one synthetic CPR whose guard contains arithmetic over symbolic values, because that is exactly the capability this work adds. Your paper already distinguishes discrete enumeration and inequality-based ranges; this phase demonstrates bytecode-level compound arithmetic predicates beyond the current evaluated prototype. 

## 10. Testing strategy

### Unit tests

Add low-level tests for every symbolic op helper:

* both operands symbolic,
* one symbolic / one concrete,
* neither symbolic,
* negative numbers,
* division edge cases,
* cast edge cases.

### Bytecode instrumentation tests

Compile tiny Java snippets and assert:

* concrete output unchanged,
* resulting shadow tag is symbolic when expected,
* associated expression matches the expected Green AST shape.

### Path-condition tests

For each branch example:

* run with one concrete input,
* inspect `curPC`,
* assert the stored path predicate structurally matches the expected expression,
* negate and solve,
* rerun with produced model,
* assert branch flips.

### Regression tests for Galette semantics

Galette’s value proposition is precise taint propagation with preserved program semantics on modern JVMs. Do not regress that. For every rewritten instruction, add tests proving:

* concrete semantics identical,
* exceptions unchanged,
* operand stack/local behavior unchanged. ([jonbell.net][5])

## 11. Important engineering pitfalls

The largest pitfall is to accidentally mix **dependency tags** and **expression tags** in an inconsistent way. Once expression propagation exists, any symbolic result must have exactly one authoritative expression in the store.

The second pitfall is over-eager path-condition construction. Arithmetic should not itself generate constraints; only branches should.

The third pitfall is identity reuse. Result tags should be fresh unless the exact same symbolic value is intentionally preserved through a move/cast that does not semantically change the expression.

The fourth pitfall is unsupported operations. For those, prefer a clear fallback policy:

* either mark result as symbolic but with an opaque uninterpreted variable,
* or drop to dependency-only propagation and mark the branch as “not solver-exact.”
  Do not silently produce wrong Green expressions.

## 12. Recommended fallback policy

Implement a feature flag such as:

* `cocopath.symbolicExpressions=true`
* `cocopath.strictSymbolicExpressions=true|false`

In strict mode:

* encountering an unsupported symbolic operation throws or logs a hard failure during testing.

In permissive mode:

* unsupported operations produce a fresh opaque symbolic variable, preserving soundness for exploration but reducing precision.

This makes rollout safer.

## 13. Recommended class-level division of responsibility

A clean separation would be:

* `galette-agent`

  * stack/local tag transport
  * bytecode rewriting
  * hooks into runtime helper methods

* `knarr-runtime`

  * symbolic-expression store
  * symbolic op semantics
  * path-condition construction
  * solver-facing Green expression management

* `cocopath/vitruvius integration`

  * symbolic input declaration
  * run/reset lifecycle
  * result/model collection

This matches the architectural split described in your paper between propagation, constraint construction, and exploration logic. 

## 14. What success should look like

At the end, the following should work automatically:

```java
int x = a + profileChoice;
if (x > 10) { ... }
```

If `profileChoice` is symbolic and `a` concrete, then after `IADD` the result value should carry a tag whose associated expression is `ADD(const(a), var(profileChoice))`. When the `IF_ICMPGT` branch is intercepted, the taken predicate should be recorded as `GT(ADD(const(a), var(profileChoice)), const(10))`. Negating it should let the solver produce a new concrete value for `profileChoice` that flips the branch.

That is the Knarr-style behavior you want.

## 15. Suggested implementation order for another agent

Start with integer arithmetic only. Do not begin with strings, doubles, or casts.

The shortest viable path is:

1. add `SymbolicExpressionStore`,
2. refactor `GaletteGreenBridge` to consult it,
3. add `SymbolicOps` for integer arithmetic,
4. replace `TAG_UNION` behavior for `IADD/ISUB/IMUL/IDIV/IREM/INEG` in `TagPropagator`,
5. refactor comparison helpers to consume propagated expressions,
6. add end-to-end tests for arithmetic branch flipping,
7. only then expand type coverage.

That sequence minimizes the number of moving parts and surfaces design mistakes early.

## 16. Reviewer-facing impact once implemented

Once this is in place, you will be able to say something more precise than the current paper text. Specifically, you could claim that the comparison-interception implementation does not merely detect tainted branch predicates, but propagates symbolic expressions through JVM computations in the style of Knarr, enabling automatic recovery of compound branch predicates at bytecode level. That would materially strengthen the story around Mechanism A. The current paper, by contrast, only evaluates the CPR-level approach and explicitly notes that bytecode-level extraction is not yet the realized prototype in the Vitruvius evaluation.

If you want, I can turn this into a handoff checklist with concrete tasks per file and test case names.

[1]: https://jonbell.net/publications/confetti?utm_source=chatgpt.com "CONFETTI: Amplifying Concolic Guidance for Fuzzers - Jon Bell"
[2]: https://raw.githubusercontent.com/AnneKoziolek/galette-concolic-model-transformation/comparison-interception-internal/galette-agent/src/main/java/edu/neu/ccs/prl/galette/internal/transform/ComparisonInterceptorVisitor.java "raw.githubusercontent.com"
[3]: https://raw.githubusercontent.com/AnneKoziolek/galette-concolic-model-transformation/comparison-interception-internal/galette-agent/src/main/java/edu/neu/ccs/prl/galette/internal/transform/TagPropagator.java "raw.githubusercontent.com"
[4]: https://raw.githubusercontent.com/AnneKoziolek/galette-concolic-model-transformation/comparison-interception-internal/knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/concolic/knarr/green/GaletteGreenBridge.java "raw.githubusercontent.com"
[5]: https://www.jonbell.net/preprint/fse25-galette.pdf?utm_source=chatgpt.com "Dynamic Taint Tracking for Modern Java Virtual Machines"
