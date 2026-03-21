# Confetti Component Reuse Plan for Galette-Knarr Integration

## Overview

Our Knarr-to-Galette migration is essentially complete (95%). [Confetti](https://github.com/neu-se/confetti) (CONcolic Fuzzer Employing Taint Tracking Information) provides a higher-level hybrid fuzzing architecture built *on top of* Knarr — the question is which of those orchestration components add value for model transformation analysis.

## Confetti Architecture Summary

Confetti has a three-component architecture:

1. **Parametric Fuzzer (Zest-based)** — input generation and execution
2. **Whitebox Analysis Process (Knarr)** — dynamic taint tracking and constraint collection
3. **Coordinator** — orchestrates fuzzer, analyzer, and Z3 solver interactions

## Key Confetti Components Beyond Knarr

| Component | Purpose | Relevance for Our Use Case |
|-----------|---------|---------------------------|
| **Coordinator** | Multi-process orchestration of fuzzer + analyzer + solver | Low — adds complexity our use case doesn't need |
| **Branch State Tracking** (`Branch` class) | Tracks `trueExplored`/`falseExplored`, controlling bytes, solved status | **High** — useful for systematic path exploration |
| **Global Hinting** | Inserts "interesting bytes" from constraints at any position in inputs | Medium — valuable if generating test inputs |
| **String Hint Types** | `EQUALS`, `INDEXOF`, `STARTSWITH`, `ENDSWITH`, `LENGTH`, `ISEMPTY`, `Z3`, `CHAR`, `GLOBAL_DICTIONARY` | Medium — could enrich string analysis |
| **Constraint Serialization & GC** | File-based storage, eviction of unhelpful constraints | Medium — improves scalability |
| **Controlling Bytes Analysis** | `findControllingBytes()` identifies which inputs influence each branch | Medium — helps identify impactful user inputs |
| **Fuzzing Loop (Zest integration)** | Coverage-guided fuzzing | Low — not needed for model transformation |
| **Remote Z3 Worker** | Out-of-process Z3 execution with crash recovery | Low — Green solver integration is sufficient |

## Recommendations

### 1. Keep Current Architecture ✅

Our single-process Galette+Knarr integration is sufficient for model transformation analysis. Confetti's multi-process coordinator adds unnecessary complexity for our use case.

### 2. Consider Adopting Branch State Tracking ⭐⭐⭐

Implement branch exploration state tracking in `knarr-runtime` to track which branches are fully explored vs. unsolved. Key elements from Confetti's `Branch` class:

- `trueExplored` / `falseExplored` flags
- `controllingBytes` — which inputs influence the branch
- `inputsTried` — which inputs have been used for this branch
- `isSolved` / `isTimedOut` — exploration state
- `armsExplored[]` — for switch statements

This would help prioritize which paths to explore during systematic path exploration.

### 3. Optionally Port String Hint Types ⭐⭐

Confetti's `StringHint` categories could enrich our `StringSymbolicTracker` for string-heavy models:

- `EQUALS` — exact string matches
- `INDEXOF` — substring searches
- `STARTSWITH` / `ENDSWITH` — prefix/suffix checks
- `LENGTH` / `ISEMPTY` — string length constraints

### 4. Add Constraint GC If Scaling Issues Arise ⭐⭐

Confetti's constraint eviction logic (`Coordinator.garbageCollectConstraints()`) can be adapted if memory becomes a bottleneck during large model transformation analyses.

## Decision Points

Before implementing any Confetti components, answer these questions:

1. **Is automated test input generation a goal?**
   - If yes → Global hinting strategy adds significant value
   - If no (primarily path analysis) → Not needed

2. **Do we need systematic branch exploration?**
   - If yes → Adopt `Branch` class pattern
   - If no → Skip the bookkeeping overhead

3. **Are we hitting memory limits?**
   - If yes → Implement constraint serialization and GC
   - If no → Current approach is sufficient

4. **Is Z3 stability an issue?**
   - If yes → Consider remote Z3 worker for crash isolation
   - If no → Green solver integration is fine

## Current Gaps vs. Confetti

| Aspect | Our Current State | Confetti Addition Possible |
|--------|------------------|---------------------------|
| **Knarr Core** | ✅ Migrated to Galette | N/A (complete) |
| **Path Constraints** | ✅ Automatic collection | Branch state tracking |
| **Solver Integration** | ✅ Green/Z3 | Remote Z3 worker for stability |
| **Input Tracking** | ✅ Symbolic values | Controlling bytes analysis |
| **String Analysis** | ✅ Basic | String hint types |
| **Exploration** | ❌ Manual | Branch exploration tracking, input scoring |
| **Scalability** | ⚠️ Memory-bound | Constraint serialization, GC |

## Conclusion

Our current integration is **production-ready for model transformation analysis**. The Confetti components that would add the most value are:

1. **Branch exploration tracking** — for systematic path exploration
2. **Global hinting strategy** — only if automated test generation becomes a goal

The fuzzing-specific components (Zest integration, multi-process coordinator) are not needed for our model transformation use case.

## References

- [Confetti GitHub Repository](https://github.com/neu-se/confetti)
- [ICSE 2022 Paper](https://github.com/neu-se/confetti) — "CONFETTI: CONcolic Fuzzer Employing Taint Tracking Information"
- Our existing documentation: `KNARR_INTEGRATION.md`, `knarr-integration-plan.md`
