# CocoPath Class Origins Documentation

This document records the architectural history of CocoPath by classifying each class according to its origin. CocoPath extends the Galette dynamic taint tracking framework by integrating Knarr (concolic execution based on the older Phosphor tool) to enable path constraint collection.

## Origin Categories

| Category | Description |
|----------|-------------|
| **GALETTE_ORIGINAL** | Original unchanged class from [neu-se/galette](https://github.com/neu-se/galette) |
| **GALETTE_KNARR** | New Galette class created to support Knarr/concolic execution integration |
| **KNARR_ORIGINAL** | Original Knarr class from [gmu-swe/knarr](https://github.com/gmu-swe/knarr) (uses Phosphor imports) |
| **KNARR_GALETTE** | New class integrating Knarr concepts with Galette APIs (replaces Phosphor with Galette) |
| **COCOPATH_VITRUVIUS** | New class for Vitruvius reactions tracking use case |

---

## galette-agent Module

### edu.neu.ccs.prl.galette.internal.agent
| Class | Origin | Notes |
|-------|--------|-------|
| GaletteAgent.java | GALETTE_ORIGINAL | JVM agent entry point |

### edu.neu.ccs.prl.galette.internal.patch
| Class | Origin | Notes |
|-------|--------|-------|
| ConfigurationEmbedder.java | GALETTE_ORIGINAL | |
| JdkUnsafeWrapperPatcher.java | GALETTE_ORIGINAL | |
| MemberAccessGenerator.java | GALETTE_ORIGINAL | |
| Patcher.java | GALETTE_ORIGINAL | |
| RegistryPatcher.java | GALETTE_ORIGINAL | |

### edu.neu.ccs.prl.galette.internal.runtime
| Class | Origin | Notes |
|-------|--------|-------|
| ArrayTagStore.java | GALETTE_ORIGINAL | |
| ArrayWrapper.java | GALETTE_ORIGINAL | |
| ExceptionStore.java | GALETTE_ORIGINAL | |
| FieldTagStore.java | GALETTE_ORIGINAL | |
| Handle.java | GALETTE_ORIGINAL | |
| InvokedViaHandle.java | GALETTE_ORIGINAL | |
| Patched.java | GALETTE_ORIGINAL | |
| PrimitiveBoxer.java | GALETTE_ORIGINAL | |
| Tag.java | GALETTE_ORIGINAL | Core taint tag representation |
| TagFrame.java | GALETTE_ORIGINAL | Thread-local tag storage |
| TagFrameFactory.java | GALETTE_ORIGINAL | |
| TaggedObject.java | GALETTE_ORIGINAL | |
| Tainter.java | GALETTE_ORIGINAL | Core tainting API |

### edu.neu.ccs.prl.galette.internal.runtime.collection
| Class | Origin | Notes |
|-------|--------|-------|
| Arrays.java | GALETTE_ORIGINAL | |
| Function.java | GALETTE_ORIGINAL | |
| HashMap.java | GALETTE_ORIGINAL | |
| Iterator.java | GALETTE_ORIGINAL | |
| ObjectIntMap.java | GALETTE_ORIGINAL | |
| SimpleList.java | GALETTE_ORIGINAL | |
| SimpleMap.java | GALETTE_ORIGINAL | |
| Stack.java | GALETTE_ORIGINAL | |
| WeakDataStore.java | GALETTE_ORIGINAL | |

### edu.neu.ccs.prl.galette.internal.runtime.frame
| Class | Origin | Notes |
|-------|--------|-------|
| AugmentedFrame.java | GALETTE_ORIGINAL | |
| EmptyFrameAdjuster.java | GALETTE_ORIGINAL | |
| FrameAdjuster.java | GALETTE_ORIGINAL | |
| IndirectTagFrameStore.java | GALETTE_ORIGINAL | |
| MatchingFrameAdjuster.java | GALETTE_ORIGINAL | |
| SpareFrameStore.java | GALETTE_ORIGINAL | |

### edu.neu.ccs.prl.galette.internal.runtime.mask
| Class | Origin | Notes |
|-------|--------|-------|
| AbstractStringBuilderMasks.java | GALETTE_ORIGINAL | |
| ArrayMasks.java | GALETTE_ORIGINAL | |
| BooleanMasks.java | GALETTE_ORIGINAL | |
| BoxTypeAccessor.java | GALETTE_ORIGINAL | |
| ByteMasks.java | GALETTE_ORIGINAL | |
| CharacterMasks.java | GALETTE_ORIGINAL | |
| ClassLoaderMasks.java | GALETTE_ORIGINAL | |
| ClassMasks.java | GALETTE_ORIGINAL | |
| DoubleMasks.java | GALETTE_ORIGINAL | |
| EnumMasks.java | GALETTE_ORIGINAL | |
| FloatMasks.java | GALETTE_ORIGINAL | |
| GetCallerHelper.java | GALETTE_ORIGINAL | |
| IntegerMasks.java | GALETTE_ORIGINAL | |
| JdkFloatingDecimalMasks.java | GALETTE_ORIGINAL | |
| JdkUnsafeWrapper.java | GALETTE_ORIGINAL | |
| LongMasks.java | GALETTE_ORIGINAL | |
| Mask.java | GALETTE_ORIGINAL | |
| MaskType.java | GALETTE_ORIGINAL | |
| Masks.java | GALETTE_ORIGINAL | |
| MemberAccess.java | GALETTE_ORIGINAL | |
| ReflectionMasks.java | GALETTE_ORIGINAL | |
| SecurityManagerMasks.java | GALETTE_ORIGINAL | |
| SerializationMasks.java | GALETTE_ORIGINAL | |
| ShortMasks.java | GALETTE_ORIGINAL | |
| StringAccessor.java | GALETTE_ORIGINAL | |
| StringConcatHelperMasks.java | GALETTE_ORIGINAL | |
| SunFloatingDecimalMasks.java | GALETTE_ORIGINAL | |
| SunUnsafeWrapper.java | GALETTE_ORIGINAL | |
| SystemMasks.java | GALETTE_ORIGINAL | |
| ToDecimalMasks.java | GALETTE_ORIGINAL | |
| UnsafeFlagAccessor.java | GALETTE_ORIGINAL | |
| UnsafeMasks.java | GALETTE_ORIGINAL | |
| UnsafeTagLocator.java | GALETTE_ORIGINAL | |
| UnsafeWrapper.java | GALETTE_ORIGINAL | |

### edu.neu.ccs.prl.galette.internal.transform
| Class | Origin | Notes |
|-------|--------|-------|
| AccessModifier.java | GALETTE_ORIGINAL | |
| AsmUtil.java | GALETTE_ORIGINAL | |
| **BranchConstraintCollector.java** | **GALETTE_KNARR** | **NEW: Path constraint collection at branches** |
| Configuration.java | GALETTE_ORIGINAL | |
| DirectFrameInitializer.java | GALETTE_ORIGINAL | |
| ExclusionList.java | GALETTE_ORIGINAL | |
| FileUtil.java | GALETTE_ORIGINAL | |
| FrameInitializer.java | GALETTE_ORIGINAL | |
| FrameManager.java | GALETTE_ORIGINAL | |
| FrameRemover.java | GALETTE_ORIGINAL | |
| GaletteInstrumented.java | GALETTE_ORIGINAL | |
| GaletteLog.java | GALETTE_ORIGINAL | |
| GaletteNames.java | GALETTE_ORIGINAL | |
| GaletteTransformer.java | GALETTE_ORIGINAL | Main bytecode transformer |
| HandleRegistry.java | GALETTE_ORIGINAL | |
| HotSpotAnnotationRemover.java | GALETTE_ORIGINAL | |
| IndirectFrameInitializer.java | GALETTE_ORIGINAL | |
| IndirectFramePasser.java | GALETTE_ORIGINAL | |
| MaskApplier.java | GALETTE_ORIGINAL | |
| MaskRegistry.java | GALETTE_ORIGINAL | |
| MethodRecord.java | GALETTE_ORIGINAL | |
| NativeWrapperCreator.java | GALETTE_ORIGINAL | |
| ObjectMethod.java | GALETTE_ORIGINAL | |
| ObjectShadowCaller.java | GALETTE_ORIGINAL | |
| OffsetCacheAdder.java | GALETTE_ORIGINAL | |
| OriginalMethodProcessor.java | GALETTE_ORIGINAL | |
| ShadowFieldAdder.java | GALETTE_ORIGINAL | |
| ShadowLocals.java | GALETTE_ORIGINAL | |
| ShadowMethodCreator.java | GALETTE_ORIGINAL | |
| ShadowWrapperCreator.java | GALETTE_ORIGINAL | |
| SubroutineInliner.java | GALETTE_ORIGINAL | (if present) |
| TagPropagator.java | GALETTE_ORIGINAL | (if present) |
| ThreadLocalAdder.java | GALETTE_ORIGINAL | |
| TransformationCache.java | GALETTE_ORIGINAL | |

---

## galette-instrument Module

### edu.neu.ccs.prl.galette.instrument
| Class | Origin | Notes |
|-------|--------|-------|
| DeletingFileVisitor.java | GALETTE_ORIGINAL | |
| GaletteInstrumentation.java | GALETTE_ORIGINAL | |
| GaletteInstrumenter.java | GALETTE_ORIGINAL | |
| GaletteJLinkPlugin.java | GALETTE_ORIGINAL | |
| GenericInstrumenter.java | GALETTE_ORIGINAL | |
| InstrumentJLinkPlugin.java | GALETTE_ORIGINAL | |
| InstrumentUtil.java | GALETTE_ORIGINAL | |
| Instrumentation.java | GALETTE_ORIGINAL | |
| JLinkInvoker.java | GALETTE_ORIGINAL | |
| JLinkRegistrationAgent.java | GALETTE_ORIGINAL | |
| PackJLinkPlugin.java | GALETTE_ORIGINAL | |
| Packer.java | GALETTE_ORIGINAL | |
| ResourcePoolPacker.java | GALETTE_ORIGINAL | |

---

## galette-benchmark Module

### edu.neu.ccs.prl.galette.bench
All classes are **GALETTE_ORIGINAL** - benchmark and test infrastructure.

### edu.neu.ccs.prl.galette.bench.extension
All classes are **GALETTE_ORIGINAL** - test framework extensions.

---

## galette-integration-tests Module

All test classes are **GALETTE_ORIGINAL**.

---

## galette-maven-plugin Module

| Class | Origin | Notes |
|-------|--------|-------|
| InstrumentMojo.java | GALETTE_ORIGINAL | Maven plugin |

---

## knarr-runtime Module - New Integration Classes

### edu.neu.ccs.prl.galette.concolic.knarr.runtime
| Class | Origin | Notes |
|-------|--------|-------|
| **ArraySymbolicTracker.java** | **KNARR_GALETTE** | Array symbolic tracking with Galette tags |
| **ConcolicControlStack.java** | **KNARR_GALETTE** | Control flow stack management |
| **ConstraintSolver.java** | **KNARR_GALETTE** | Constraint solver interface |
| **CoverageTracker.java** | **KNARR_GALETTE** | Path coverage tracking |
| **GaletteKnarrTaintListener.java** | **KNARR_GALETTE** | Bridges Galette tags with Knarr constraints |
| **GaletteSymbolicator.java** | **KNARR_GALETTE** | Main symbolic execution engine (Galette-native) |
| **GaletteTaintListener.java** | **KNARR_GALETTE** | Galette-specific taint event handling |
| **InputSolution.java** | **KNARR_GALETTE** | Solution representation (adapted from Knarr) |
| **PathConditionWrapper.java** | **KNARR_GALETTE** | Path condition wrapper (adapted from Knarr) |
| **PathExplorer.java** | **KNARR_GALETTE** | Systematic path exploration engine |
| **PathUtils.java** | **KNARR_GALETTE** | Path constraint utilities (adapted from Knarr) |
| **StringSymbolicTracker.java** | **KNARR_GALETTE** | String symbolic tracking |
| **SymbolicComparison.java** | **COCOPATH_VITRUVIUS** | Vitruvius-specific constraint collection |
| **Z3ConstraintSolver.java** | **KNARR_GALETTE** | Z3 SMT solver integration |

### edu.neu.ccs.prl.galette.concolic.knarr.compat
| Class | Origin | Notes |
|-------|--------|-------|
| **Symbolicator.java** | **KNARR_GALETTE** | Compatibility interface |

### edu.neu.ccs.prl.galette.concolic.knarr.green
| Class | Origin | Notes |
|-------|--------|-------|
| **GaletteGreenBridge.java** | **KNARR_GALETTE** | Bridge between Galette Tags and Green expressions |

### edu.neu.ccs.prl.galette.concolic.knarr.listener
| Class | Origin | Notes |
|-------|--------|-------|
| **ConcolicTaintListener.java** | **KNARR_GALETTE** | Concolic execution event listener |

### edu.neu.ccs.prl.galette.vitruvius
| Class | Origin | Notes |
|-------|--------|-------|
| **AutomaticVitruvPathExploration.java** | **COCOPATH_VITRUVIUS** | Single-variable path exploration for Vitruvius |
| **AutomaticVitruvMultiVarPathExploration.java** | **COCOPATH_VITRUVIUS** | Multi-variable path exploration (e.g., 5x5=25 paths) |
| **AutomaticVitruvPathExplorationHelper.java** | **COCOPATH_VITRUVIUS** | Helper utilities for path exploration |

---

## knarr-runtime/knarr Submodule - Original Knarr Classes

### edu.gmu.swe.knarr.runtime
| Class | Origin | Notes |
|-------|--------|-------|
| AFLCoverage.java | KNARR_ORIGINAL | AFL-style coverage |
| CountBytecodeAdapter.java | KNARR_ORIGINAL | Bytecode counting |
| Coverage.java | KNARR_ORIGINAL | Coverage interface |
| ExpressionTaint.java | KNARR_ORIGINAL | Symbolic expression taint (uses Phosphor) |
| InputSolution.java | KNARR_ORIGINAL | Solution representation |
| JunitAssert.java | KNARR_ORIGINAL | JUnit integration |
| JunitTestAdapter.java | KNARR_ORIGINAL | JUnit test adapter |
| KnarrAutoTainter.java | KNARR_ORIGINAL | Automatic tainting |
| ModelUtils.java | KNARR_ORIGINAL | Model utilities |
| PathConditionWrapper.java | KNARR_ORIGINAL | Path constraint wrapper |
| PathConstraintTagFactory.java | KNARR_ORIGINAL | Constraint tag creation (Phosphor-based) |
| PathUtils.java | KNARR_ORIGINAL | Path utilities (uses Phosphor imports) |
| RedirectMethodsTaintAdapter.java | KNARR_ORIGINAL | Method redirection |
| StringOpcodes.java | KNARR_ORIGINAL | String operation codes |
| StringTagFactory.java | KNARR_ORIGINAL | String tag creation |
| StringUtils.java | KNARR_ORIGINAL | String utilities |
| Symbolicator.java | KNARR_ORIGINAL | Original symbolic execution API (uses Phosphor) |
| TaintListener.java | KNARR_ORIGINAL | Taint tracking listener interface |

---

## Summary Statistics

| Origin Category | Count | Description |
|-----------------|-------|-------------|
| GALETTE_ORIGINAL | ~100 | Unchanged from neu-se/galette |
| GALETTE_KNARR | 1 | New Galette class for Knarr support |
| KNARR_ORIGINAL | 18 | Original from gmu-swe/knarr (uses Phosphor) |
| KNARR_GALETTE | 15 | New classes integrating Knarr with Galette |
| COCOPATH_VITRUVIUS | 4 | New classes for Vitruvius use case |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         CocoPath                                │
├─────────────────────────────────────────────────────────────────┤
│  COCOPATH_VITRUVIUS Layer                                       │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ AutomaticVitruvPathExploration                            │  │
│  │ AutomaticVitruvMultiVarPathExploration                    │  │
│  │ SymbolicComparison (symbolicVitruviusChoice)              │  │
│  └───────────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│  KNARR_GALETTE Layer (Knarr concepts + Galette APIs)            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ GaletteSymbolicator ──────────> PathExplorer              │  │
│  │ PathUtils ────────────────────> Z3ConstraintSolver        │  │
│  │ GaletteGreenBridge ───────────> PathConditionWrapper      │  │
│  └───────────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│  GALETTE_ORIGINAL Layer (Dynamic Taint Tracking)                │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ Tag, Tainter, TagFrame ───────> GaletteTransformer        │  │
│  │ ArrayTagStore, FieldTagStore ─> GaletteAgent              │  │
│  │ Masks (String, Integer, etc.) ─> Instrumentation          │  │
│  └───────────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│  KNARR_ORIGINAL Layer (Reference, uses Phosphor)                │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ Symbolicator (original) ──────> PathUtils (original)      │  │
│  │ ExpressionTaint ──────────────> TaintListener             │  │
│  │ (Uses edu.columbia.cs.psl.phosphor.* imports)             │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Classes with @origin Javadoc Annotations

The following classes have been annotated with `@origin` tags in their Javadoc:

### GALETTE_KNARR (New Galette classes for Knarr integration)
- [BranchConstraintCollector.java](galette-agent/src/main/java/edu/neu/ccs/prl/galette/internal/transform/BranchConstraintCollector.java)

### KNARR_GALETTE (New classes integrating Knarr with Galette)
- [GaletteSymbolicator.java](knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/concolic/knarr/runtime/GaletteSymbolicator.java)
- [PathUtils.java](knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/concolic/knarr/runtime/PathUtils.java)
- [PathExplorer.java](knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/concolic/knarr/runtime/PathExplorer.java)
- [Z3ConstraintSolver.java](knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/concolic/knarr/runtime/Z3ConstraintSolver.java)
- [GaletteGreenBridge.java](knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/concolic/knarr/green/GaletteGreenBridge.java)

### COCOPATH_VITRUVIUS (New classes for Vitruvius use case)
- [SymbolicComparison.java](knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/concolic/knarr/runtime/SymbolicComparison.java)
- [AutomaticVitruvPathExploration.java](knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/vitruvius/AutomaticVitruvPathExploration.java)
- [AutomaticVitruvMultiVarPathExploration.java](knarr-runtime/src/main/java/edu/neu/ccs/prl/galette/vitruvius/AutomaticVitruvMultiVarPathExploration.java)

### KNARR_ORIGINAL (Original Knarr classes from gmu-swe/knarr)
- [TaintListener.java](knarr-runtime/knarr/src/main/java/edu/gmu/swe/knarr/runtime/TaintListener.java)
- [PathUtils.java](knarr-runtime/knarr/src/main/java/edu/gmu/swe/knarr/runtime/PathUtils.java)
- [Symbolicator.java](knarr-runtime/knarr/src/main/java/edu/gmu/swe/knarr/runtime/Symbolicator.java) (read but annotation edit pending)

---

## References

- **Original Galette**: https://github.com/neu-se/galette
- **Original Knarr**: https://github.com/gmu-swe/knarr
- **Original Phosphor**: https://github.com/gmu-swe/phosphor (archived, replaced by Galette)
- **Galette Paper**: "Dynamic Taint Tracking for Modern Java Virtual Machines" (FSE 2025)
- **Vitruvius Framework**: https://vitruv.tools/
