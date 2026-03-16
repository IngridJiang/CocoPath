package edu.neu.ccs.prl.galette.internal.transform;

import static org.objectweb.asm.Opcodes.*;

import edu.neu.ccs.prl.galette.internal.runtime.Handle;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.tree.MethodNode;

class TagPropagator extends MethodVisitor {
    private final ShadowLocals shadowLocals;

    /**
     * True when the class being instrumented is an application class whose classloader can
     * resolve PathUtils (knarr-runtime) at runtime.  False for JDK bootstrap classes (java/,
     * sun/, jdk/, javax/, com/sun/) which use the bootstrap classloader and cannot access
     * PathUtils.  When false, branch constraint injection is skipped even if
     * galette.symbolic.enabled=true.
     */
    private final boolean isAppClass;

    private TagPropagator(ShadowLocals shadowLocals, MethodVisitor mv, boolean isAppClass) {
        super(GaletteTransformer.ASM_VERSION, mv);
        if (shadowLocals == null) {
            throw new NullPointerException();
        }
        this.shadowLocals = shadowLocals;
        this.isAppClass = isAppClass;
    }

    @Override
    public void visitInsn(int opcode) {
        switch (opcode) {
            case Opcodes.NOP:
                break;
            case ACONST_NULL:
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
                // ... -> ..., value
                Handle.TAG_GET_EMPTY.accept(mv);
                shadowLocals.push();
                break;
            case LCONST_0:
            case LCONST_1:
            case DCONST_0:
            case DCONST_1:
                // ... -> ..., value, top
                Handle.TAG_GET_EMPTY.accept(mv);
                shadowLocals.pushWide();
                break;
            case IALOAD:
            case FALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
                // ..., arrayref, index -> ..., value
                super.visitInsn(Opcodes.DUP2);
                // arrayref, index, arrayref, index
                shadowLocals.peek(1);
                shadowLocals.peek(0);
                // arrayref, index, arrayref, index, arrayref-tag, index-tag
                Handle.ARRAY_TAG_STORE_GET_TAG.accept(mv);
                // arrayref, index, value-tag
                shadowLocals.pop(2);
                shadowLocals.push();
                break;
            case LALOAD:
            case DALOAD:
                // ..., arrayref, index -> ..., value, top
                super.visitInsn(Opcodes.DUP2);
                // arrayref, index, arrayref, index
                shadowLocals.peek(1);
                shadowLocals.peek(0);
                // arrayref, index, arrayref, index, arrayref-tag, index-tag
                Handle.ARRAY_TAG_STORE_GET_TAG.accept(mv);
                // arrayref, index, value-tag
                shadowLocals.pop(2);
                shadowLocals.pushWide();
                break;
            case IASTORE:
            case FASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                // ..., arrayref, index, value -> ...
                super.visitInsn(Opcodes.DUP_X2);
                // value, arrayref, index, value
                super.visitInsn(Opcodes.POP);
                // value, arrayref, index
                super.visitInsn(Opcodes.DUP2_X1);
                // arrayref, index, value, arrayref, index
                shadowLocals.peek(2);
                shadowLocals.peek(1);
                shadowLocals.peek(0);
                // arrayref, index, value, arrayref, index, arrayref-tag, index-tag, value-tag
                Handle.ARRAY_TAG_STORE_SET_TAG.accept(mv);
                // arrayref, index, value
                shadowLocals.pop(3);
                break;
            case LASTORE:
            case DASTORE:
                // ..., arrayref, index, value, top -> ...
                super.visitInsn(Opcodes.DUP2_X2);
                // value, top, arrayref, index, value, top
                super.visitInsn(Opcodes.POP2);
                // value, top, arrayref, index
                super.visitInsn(Opcodes.DUP2_X2);
                // arrayref, index, value, top, arrayref, index
                shadowLocals.peek(3);
                shadowLocals.peek(2);
                shadowLocals.peek(1);
                // arrayref, index, value, top, arrayref, index, arrayref-tag, index-tag, value-tag
                Handle.ARRAY_TAG_STORE_SET_TAG.accept(mv);
                // arrayref, index, value, top
                shadowLocals.pop(4);
                break;
            case Opcodes.POP:
                // ..., value -> ...
                shadowLocals.pop(1);
                break;
            case Opcodes.POP2:
                // ..., value1, value2 -> ...
                shadowLocals.pop(2);
                break;
            case Opcodes.DUP:
                // ..., value -> ..., value, value
                shadowLocals.peek(0);
                shadowLocals.push();
                break;
            case Opcodes.DUP_X1:
                // ..., value1, value2 -> ..., value2, value1, value2
                shadowLocals.performOperation(opcode, 2, 3);
                break;
            case Opcodes.DUP_X2:
                // ..., value1, value2, value3 -> ..., value3, value1, value2, value3
                shadowLocals.performOperation(opcode, 3, 4);
                break;
            case Opcodes.DUP2:
                // ..., value1, value2 -> ..., value1, value2, value1, value2
                shadowLocals.performOperation(opcode, 2, 4);
                break;
            case Opcodes.DUP2_X1:
                // ..., value1, value2, value3 -> ..., value2, value3, value1, value2, value3
                shadowLocals.performOperation(opcode, 3, 5);
                break;
            case Opcodes.DUP2_X2:
                // ..., value1, value2, value3, value4 -> ..., value3, value4, value1, value2, value3, value4
                shadowLocals.performOperation(opcode, 4, 6);
                break;
            case Opcodes.SWAP:
                shadowLocals.performOperation(opcode, 2, 2);
                break;
            case IADD:
            case FADD:
            case ISUB:
            case FSUB:
            case IMUL:
            case FMUL:
            case IDIV:
            case FDIV:
            case IREM:
            case FREM:
            case ISHL:
            case ISHR:
            case IUSHR:
            case IAND:
            case IOR:
            case IXOR:
            case FCMPL:
            case FCMPG:
                // ..., value1, value2 -> ..., result
                shadowLocals.peek(1);
                shadowLocals.peek(0);
                // ..., value1, value2, tag1, tag2
                Handle.TAG_UNION.accept(mv);
                shadowLocals.pop(2);
                shadowLocals.push();
                break;
            case LADD:
            case DADD:
            case LSUB:
            case DSUB:
            case LMUL:
            case DMUL:
            case LDIV:
            case DDIV:
            case LREM:
            case DREM:
            case LAND:
            case LOR:
            case LXOR:
                // ..., value1, top, value2, top -> ..., result, top
                shadowLocals.peek(3);
                shadowLocals.peek(1);
                Handle.TAG_UNION.accept(mv);
                shadowLocals.pop(4);
                shadowLocals.pushWide();
                break;
            case LSHL:
            case LUSHR:
            case LSHR:
                // ..., value1, top, value2 -> ..., result, top
                shadowLocals.peek(2);
                shadowLocals.peek(0);
                Handle.TAG_UNION.accept(mv);
                shadowLocals.pop(3);
                shadowLocals.pushWide();
                break;
            case LCMP:
            case DCMPL:
            case DCMPG:
                // ..., value1, top, value2, top -> ..., result
                shadowLocals.peek(3);
                shadowLocals.peek(1);
                Handle.TAG_UNION.accept(mv);
                shadowLocals.pop(4);
                shadowLocals.push();
                break;
            case Opcodes.INEG:
            case Opcodes.FNEG:
            case Opcodes.I2F:
            case Opcodes.F2I:
            case Opcodes.I2B:
            case Opcodes.I2C:
            case Opcodes.I2S:
                // ..., value -> ..., result
                // No need to do anything for data flow propagation
                break;
            case Opcodes.LNEG:
            case Opcodes.DNEG:
            case Opcodes.L2D:
            case Opcodes.D2L:
                // ..., value, top -> ..., result, top
                // No need to do anything for data flow propagation
                break;
            case Opcodes.I2L:
            case Opcodes.I2D:
            case Opcodes.F2L:
            case Opcodes.F2D:
                // ..., value -> ..., result, top
                shadowLocals.peek(0);
                shadowLocals.pop(1);
                shadowLocals.pushWide();
                break;
            case Opcodes.L2I:
            case Opcodes.L2F:
            case Opcodes.D2I:
            case Opcodes.D2F:
                // ..., value, top -> ..., result
                shadowLocals.pop(1);
                break;
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
            case Opcodes.ARETURN:
                // ..., value -> ...
                shadowLocals.peek(0);
                shadowLocals.getFrameManager().setReturnTag();
                shadowLocals.pop(1);
                break;
            case Opcodes.DRETURN:
            case Opcodes.LRETURN:
                // ..., value, top -> ...
                shadowLocals.peek(1);
                shadowLocals.getFrameManager().setReturnTag();
                shadowLocals.pop(2);
                break;
            case Opcodes.RETURN:
                // ..., -> []
                break;
            case Opcodes.ARRAYLENGTH:
                // ..., arrayref -> ..., length
                super.visitInsn(DUP);
                shadowLocals.peek(0);
                // arrayref, arrayref, arrayref-tag
                Handle.ARRAY_TAG_STORE_GET_LENGTH_TAG.accept(mv);
                // arrayref
                shadowLocals.pop(1);
                shadowLocals.push();
                break;
            case Opcodes.ATHROW:
                // ..., objectref -> []
                super.visitInsn(DUP);
                shadowLocals.peek(0);
                // objectref, objectref, objectref-tag
                Handle.EXCEPTION_STORE_SET.accept(mv);
                // objectref
                shadowLocals.pop(1);
                break;
            case Opcodes.MONITORENTER:
            case Opcodes.MONITOREXIT:
                // ..., objectref -> ...
                shadowLocals.pop(0);
                break;
            default:
                throw new IllegalArgumentException();
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        switch (opcode) {
            case BIPUSH:
            case SIPUSH:
                // ... -> ..., value
                Handle.TAG_GET_EMPTY.accept(mv);
                shadowLocals.push();
                super.visitIntInsn(opcode, operand);
                break;
            case NEWARRAY:
                // ..., count -> ..., arrayref
                super.visitIntInsn(opcode, operand);
                // arrayref
                super.visitInsn(DUP);
                shadowLocals.peek(0);
                // arrayref, arrayref, count-tag
                Handle.ARRAY_TAG_STORE_SET_LENGTH_TAG.accept(mv);
                // arrayref
                Handle.TAG_GET_EMPTY.accept(mv);
                shadowLocals.pop(1);
                shadowLocals.push();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        switch (opcode) {
            case ILOAD:
            case FLOAD:
            case ALOAD:
                // ... -> ..., value
                shadowLocals.loadShadowVar(varIndex);
                shadowLocals.push();
                break;
            case DLOAD:
            case LLOAD:
                // ... -> ..., value, top
                shadowLocals.loadShadowVar(varIndex);
                shadowLocals.pushWide();
                break;
            case ISTORE:
            case FSTORE:
            case ASTORE:
                // ..., value -> ...
                shadowLocals.peek(0);
                shadowLocals.storeShadowVar(varIndex);
                shadowLocals.pop(1);
                break;
            case LSTORE:
            case DSTORE:
                // ..., value, top -> ...
                shadowLocals.peek(1);
                shadowLocals.storeShadowVar(varIndex);
                shadowLocals.pop(2);
                break;
            case RET:
                break;
            default:
                throw new IllegalArgumentException();
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        switch (opcode) {
            case NEW:
                visitNew(type);
                break;
            case ANEWARRAY:
                visitNewReferenceArray(type);
                break;
            case CHECKCAST:
                visitCheckCast(type);
                break;
            case INSTANCEOF:
                visitInstanceOf(type);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void visitInstanceOf(String type) {
        // ..., objectref -> ..., result
        // No need to do anything for data flow propagation
        super.visitTypeInsn(INSTANCEOF, type);
    }

    private void visitCheckCast(String type) {
        // ..., objectref -> ..., objectref
        // No need to do anything for data flow propagation
        super.visitTypeInsn(CHECKCAST, type);
    }

    private void visitNewReferenceArray(String type) {
        // ..., count -> ..., arrayref
        super.visitTypeInsn(ANEWARRAY, type);
        // arrayref
        super.visitInsn(DUP);
        shadowLocals.peek(0);
        // arrayref, arrayref, count-tag
        Handle.ARRAY_TAG_STORE_SET_LENGTH_TAG.accept(mv);
        // arrayref
        Handle.TAG_GET_EMPTY.accept(mv);
        shadowLocals.pop(1);
        shadowLocals.push();
    }

    private void visitNew(String type) {
        // ... -> ..., objectref
        // Must visit the original NEW instruction first to keep it next to the label used to represent
        // it in stack frames
        super.visitTypeInsn(NEW, type);
        Handle.TAG_GET_EMPTY.accept(mv);
        shadowLocals.push();
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        switch (opcode) {
            case GETSTATIC:
                visitGetStatic(owner, name, descriptor);
                break;
            case PUTSTATIC:
                visitPutStatic(owner, name, descriptor);
                break;
            case GETFIELD:
                visitGetField(owner, name, descriptor);
                break;
            case PUTFIELD:
                visitPutField(owner, name, descriptor);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void visitPutField(String owner, String name, String descriptor) {
        // ..., objectref, value -> ...
        int valueSize = Type.getType(descriptor).getSize();
        if (isShadowedField(owner)) {
            prepareForPutField(valueSize);
            super.visitFieldInsn(
                    PUTFIELD, owner, ShadowFieldAdder.getShadowFieldName(name), ShadowFieldAdder.TAG_DESCRIPTOR);
        } else if (isMirroredField(owner, name, false)) {
            prepareForPutField(valueSize);
            super.visitLdcInsn(owner + '#' + name + '#' + descriptor);
            Handle.FIELD_TAG_STORE_PUT_FIELD.accept(mv);
        }
        // Remove the tags on the shadow stack for the slots consumed by this instruction
        shadowLocals.pop(valueSize + 1);
        super.visitFieldInsn(PUTFIELD, owner, name, descriptor);
    }

    private void prepareForPutField(int valueSize) {
        if (valueSize == 2) {
            // objectref, value, top
            super.visitInsn(DUP2_X1);
            // value, top, objectref, value, top
            super.visitInsn(POP2);
            // value, top, objectref
            super.visitInsn(DUP_X2);
            // objectref, value, top, objectref
            shadowLocals.peek(1);
            // objectref, value, top, objectref, value-tag
        } else {
            // objectref, value
            super.visitInsn(DUP2);
            // objectref, value, objectref, value
            super.visitInsn(POP);
            // objectref, value, objectref
            shadowLocals.peek(0);
            // objectref, value, objectref, value-tag
        }
    }

    private void visitGetField(String owner, String name, String descriptor) {
        // ..., objectref -> ..., value
        if (isShadowedField(owner)) {
            super.visitInsn(DUP);
            // objectref, objectref
            super.visitFieldInsn(
                    GETFIELD, owner, ShadowFieldAdder.getShadowFieldName(name), ShadowFieldAdder.TAG_DESCRIPTOR);
            // objectref, value-tag
        } else if (isMirroredField(owner, name, false)) {
            super.visitInsn(DUP);
            // objectref, objectref
            super.visitLdcInsn(owner + '#' + name + '#' + descriptor);
            Handle.FIELD_TAG_STORE_GET_FIELD.accept(mv);
            // objectref, value-tag
        } else {
            Handle.TAG_GET_EMPTY.accept(mv);
        }
        if (ShadowFieldAdder.isBoxedType(owner) && "value".equals(name)) {
            // Propagate from boxed type to its value
            shadowLocals.peek(0);
            Handle.TAG_UNION.accept(mv);
        }
        // Remove the tags on the shadow stack for the slot consumed by this instruction
        shadowLocals.pop(1);
        if (Type.getType(descriptor).getSize() == 2) {
            shadowLocals.pushWide();
        } else {
            shadowLocals.push();
        }
        super.visitFieldInsn(GETFIELD, owner, name, descriptor);
    }

    private void visitPutStatic(String owner, String name, String descriptor) {
        // ..., value -> ...
        int valueSize = Type.getType(descriptor).getSize();
        // value OR value, top
        if (isShadowedField(owner)) {
            shadowLocals.peek(valueSize - 1);
            super.visitFieldInsn(
                    PUTSTATIC, owner, ShadowFieldAdder.getShadowFieldName(name), ShadowFieldAdder.TAG_DESCRIPTOR);
        } else if (isMirroredField(owner, name, true)) {
            shadowLocals.peek(valueSize - 1);
            super.visitLdcInsn(owner + '#' + name + '#' + descriptor);
            Handle.FIELD_TAG_STORE_PUT_STATIC.accept(mv);
        }
        // value or value, top
        // Remove the tags on the shadow stack for the slots consumed by this instruction
        shadowLocals.pop(valueSize);
        super.visitFieldInsn(PUTSTATIC, owner, name, descriptor);
    }

    private void visitGetStatic(String owner, String name, String descriptor) {
        // ... -> ..., value
        if (ShadowFieldAdder.hasShadowFields(owner)) {
            super.visitFieldInsn(
                    GETSTATIC, owner, ShadowFieldAdder.getShadowFieldName(name), ShadowFieldAdder.TAG_DESCRIPTOR);
        } else if (isMirroredField(owner, name, true)) {
            super.visitLdcInsn(owner + '#' + name + '#' + descriptor);
            Handle.FIELD_TAG_STORE_GET_STATIC.accept(mv);
        } else {
            Handle.TAG_GET_EMPTY.accept(mv);
        }
        if (Type.getType(descriptor).getSize() == 2) {
            shadowLocals.pushWide();
        } else {
            shadowLocals.push();
        }
        super.visitFieldInsn(GETSTATIC, owner, name, descriptor);
    }

    @Override
    public void visitInvokeDynamicInsn(
            String name,
            String descriptor,
            org.objectweb.asm.Handle bootstrapMethodHandle,
            Object... bootstrapMethodArguments) {
        // Handled by IndirectFramePasser
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        switch (opcode) {
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
                // ..., int-value -> ...
                if (shouldRecordBranchConstraints()) {
                    recordBranchConstraint(opcode, label);
                    // recordBranchConstraint emits the original jump instruction
                    shadowLocals.pop(1);
                    return;
                }
                shadowLocals.pop(1);
                break;
            case IFNULL:
            case IFNONNULL:
                // ..., objectref -> ...  (reference comparison; no integer constraint recording)
                shadowLocals.pop(1);
                break;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
                // ..., value1, value2 -> ...
                if (shouldRecordBranchConstraints()) {
                    recordTwoValueBranchConstraint(opcode, label);
                    shadowLocals.pop(2);
                    return;
                }
                shadowLocals.pop(2);
                break;
            case IF_ACMPEQ:
            case IF_ACMPNE:
                // ..., value1, value2 -> ...
                shadowLocals.pop(2);
                break;
            case GOTO:
                // ... -> ...
                break;
            case JSR:
                // ... -> ..., address
                Handle.TAG_GET_EMPTY.accept(mv);
                shadowLocals.push();
                break;
            default:
                throw new IllegalArgumentException();
        }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        Handle.TAG_GET_EMPTY.accept(mv);
        if (value instanceof Double || value instanceof Long) {
            // ... -> ..., value, top
            shadowLocals.pushWide();
        } else {
            // ... -> ..., value
            shadowLocals.push();
        }
        super.visitLdcInsn(value);
    }

    @Override
    public void visitIincInsn(int varIndex, int increment) {
        // No need to do anything for data flow propagation
        super.visitIincInsn(varIndex, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        // ..., index -> ...
        // Note: Switch constraint collection is handled manually via PathUtils API
        // See AutomaticVitruvPathExploration.executeVitruvWithInput() for manual collection
        shadowLocals.pop(1);
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        // ..., key -> ...
        // Note: Switch constraint collection is handled manually via PathUtils API
        shadowLocals.pop(1);
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        // ..., count1, [count2, ...] -> ..., arrayref
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
        // arrayref
        super.visitInsn(DUP);
        // arrayref, arrayref
        AsmUtil.pushInt(mv, numDimensions);
        super.visitTypeInsn(ANEWARRAY, Type.getInternalName(Tag.class));
        // arrayref, arrayref, tag-array
        int index = 0;
        for (int i = numDimensions - 1; i >= 0; i--, index++) {
            super.visitInsn(DUP);
            AsmUtil.pushInt(mv, index);
            shadowLocals.peek(i);
            // arrayref, arrayref, tag-array, tag-array, count-tag
            super.visitInsn(AASTORE);
            // arrayref, arrayref, tag-array
        }
        // arrayref, arrayref, tag-array
        Handle.ARRAY_TAG_STORE_SET_LENGTH_TAGS.accept(mv);
        // arrayref
        // Set the tag for the newly created array in the shadow stack
        Handle.TAG_GET_EMPTY.accept(mv);
        shadowLocals.pop(numDimensions);
        shadowLocals.push();
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (IndirectFramePasser.isSignaturePolymorphic(owner, name)) {
            // Handled by IndirectFramePasser
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        } else {
            boolean createFrame = !isGetCallerClass(owner, name, descriptor) && !isIgnoredMethod(owner, name);
            // Consume tags from the shadow stack for the arguments of the call
            shadowLocals.prepareForCall(opcode == INVOKESTATIC, descriptor, createFrame);
            if (createFrame) {
                // Directly pass the frame as a shadow argument
                descriptor = ShadowMethodCreator.getShadowMethodDescriptor(descriptor);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            if (isGetCallerClass(owner, name, descriptor)) {
                // Check for a stored caller class
                // [returned-caller]
                shadowLocals.getFrameManager().loadCaller();
                // [return-caller, stored-caller]
                Handle.GET_CALLER_HELPER.accept(mv);
                // [resolved-caller]
            }
            // Set the tag for the return value
            shadowLocals.restoreFromCall(descriptor, createFrame);
        }
    }

    private static boolean isGetCallerClass(String owner, String name, String descriptor) {
        // The result of getCallerClass depends on the caller, so we cannot use the native wrapper without changing the
        // caller
        if (name.equals("getCallerClass") && "()Ljava/lang/Class;".equals(descriptor)) {
            return owner.equals("jdk/internal/reflect/Reflection") || owner.equals("sun/reflect/Reflection");
        }
        return false;
    }

    private static boolean isShadowedField(String owner) {
        return !isIgnoredClass(owner) && ShadowFieldAdder.hasShadowFields(owner);
    }

    private static boolean isIgnoredClass(String owner) {
        // Internal tainting class are not instrumented but should be treated as though they were
        // They are expected to declare shadow members for all of their members that are accessible
        // from instrumented classes
        if (Configuration.isInternalTaintingClass(owner)) {
            return false;
        }
        // We cannot add shadow methods to Object or arrays
        if (owner.equals("java/lang/Object") || owner.startsWith("[")) {
            return true;
        }
        // Shadows are not created for classes explicitly excluded
        return GaletteTransformer.isExcluded(owner);
    }

    private static boolean isIgnoredMethod(String owner, String name) {
        // Needed for compatability with JProfiler's agent
        if (owner.startsWith("com/jprofiler")) {
            return true;
        }
        return isIgnoredClass(owner) || !ShadowMethodCreator.shouldShadow(name);
    }

    private static boolean isMirroredField(String owner, String name, boolean isStatic) {
        // Cannot mirror tags for Reference
        return !ShadowFieldAdder.hasShadowFields(owner) && !owner.equals("java/lang/ref/Reference");
    }

    // ===== Symbolic Execution Support =====

    /** Check if symbolic execution is enabled via system property */
    private static final boolean SYMBOLIC_EXECUTION_ENABLED = Boolean.getBoolean("galette.symbolic.enabled");

    /** Internal name of PathUtils runtime class for constraint collection */
    private static final String PATH_UTILS_INTERNAL_NAME = "edu/neu/ccs/prl/galette/concolic/knarr/runtime/PathUtils";

    /** Descriptor for Tag class */
    private static final String TAG_DESC = "Ledu/neu/ccs/prl/galette/internal/runtime/Tag;";

    /**
     * Check if branch constraint recording should be performed for this method.
     * Returns true only when symbolic execution is globally enabled AND the class
     * being instrumented is an application class (not a JDK bootstrap class).
     */
    private boolean shouldRecordBranchConstraints() {
        return SYMBOLIC_EXECUTION_ENABLED && isAppClass;
    }

    /**
     * Record a branch constraint for single-value branch instructions (IFEQ, IFLT, IFGE etc.).
     * Uses value-based tag lookup via PathUtils.testAndRecordSingleValueBranch.
     *
     * No new branch-target labels are introduced — the constraint method is called BEFORE
     * the original jump instruction, so no stack-map frames need to be emitted.
     *
     * Stack contract:
     *   Entry:  ..., value
     *   Exit:   jumps to {@code label} if branch taken, falls through otherwise
     *           (identical to the original branch instruction)
     */
    private void recordBranchConstraint(int opcode, Label label) {
        // Stack: ..., value
        super.visitInsn(DUP);
        // Stack: ..., value, value
        AsmUtil.pushInt(mv, opcode);
        // Stack: ..., value, value, opcode
        // testAndRecordSingleValueBranch(int value, int opcode): tests condition internally and records constraint
        super.visitMethodInsn(INVOKESTATIC, PATH_UTILS_INTERNAL_NAME, "testAndRecordSingleValueBranch", "(II)V", false);
        // Stack: ..., value  (copy and opcode consumed by method)
        // Emit the original branch — no new labels needed, no VerifyError.
        super.visitJumpInsn(opcode, label);
        // Stack: ... (value consumed by branch)
    }

    /**
     * Record a branch constraint for two-value branch instructions (IF_ICMPEQ/LT/GE etc.).
     * Uses value-based tag lookup for the left operand (v1) via PathUtils.testAndRecordTwoValueBranch.
     *
     * No new branch-target labels are introduced — the constraint method is called BEFORE
     * the original jump instruction, so no stack-map frames need to be emitted.
     *
     * Stack contract:
     *   Entry:  ..., v1, v2
     *   Exit:   jumps to {@code label} if branch taken, falls through otherwise
     *           (identical to the original branch instruction)
     */
    private void recordTwoValueBranchConstraint(int opcode, Label label) {
        // Stack: ..., v1, v2
        super.visitInsn(DUP2);
        // Stack: ..., v1, v2, v1, v2
        AsmUtil.pushInt(mv, opcode);
        // Stack: ..., v1, v2, v1, v2, opcode
        // testAndRecordTwoValueBranch(int v1, int v2, int opcode): tests + records constraint
        super.visitMethodInsn(INVOKESTATIC, PATH_UTILS_INTERNAL_NAME, "testAndRecordTwoValueBranch", "(III)V", false);
        // Stack: ..., v1, v2  (copies and opcode consumed by method)
        // Emit the original branch — no new labels needed, no VerifyError.
        super.visitJumpInsn(opcode, label);
        // Stack: ... (v1, v2 consumed by branch)
    }

    // Note: Automatic switch constraint collection methods removed.
    // Switch constraints are now collected manually via PathUtils API.
    // See AutomaticVitruvPathExploration.executeVitruvWithInput() for implementation.

    /**
     * Comma-separated list of internal class-name prefixes that should have branch constraints
     * injected.  Only classes whose internal name starts with one of these prefixes will have
     * {@code testAndRecordSingleValueBranch} / {@code testAndRecordTwoValueBranch} calls emitted.
     *
     * <p>Set via {@code -Dgalette.instrument.prefix=mir/routines/,tools/example/} on the JVM
     * command line.  An empty (or absent) property means "instrument no class", which prevents
     * infinite recursion and spurious constraints from framework code.
     *
     * <p>JDK bootstrap classes and CoCoPath project classes are always excluded regardless.
     */
    private static final String[] INSTRUMENT_PREFIXES = loadInstrumentPrefixes();

    private static String[] loadInstrumentPrefixes() {
        String prop = System.getProperty("galette.instrument.prefix", "");
        if (prop.isEmpty()) {
            return new String[0];
        }
        return prop.split(",");
    }

    /**
     * Returns true when branch constraint injection should be applied to the given class.
     * The class must:
     *  1. Not be a JDK bootstrap class (java/, sun/, jdk/, javax/, com/sun/, com/oracle/)
     *  2. Not be a CoCoPath project class (edu/neu/ccs/prl/galette/) — would cause recursion
     *  3. Match at least one prefix in {@code galette.instrument.prefix}
     */
    private static boolean isAppClass(String owner) {
        // Always exclude JDK bootstrap classes (can't resolve PathUtils from boot classloader)
        if (owner.startsWith("java/")
                || owner.startsWith("sun/")
                || owner.startsWith("jdk/")
                || owner.startsWith("javax/")
                || owner.startsWith("com/sun/")
                || owner.startsWith("com/oracle/")) {
            return false;
        }
        // Always exclude CoCoPath internals to prevent infinite recursion
        if (owner.startsWith("edu/neu/ccs/prl/galette/")) {
            return false;
        }
        // Require at least one prefix match
        for (String prefix : INSTRUMENT_PREFIXES) {
            if (!prefix.isEmpty() && owner.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    static MethodVisitor newInstance(MethodVisitor mv, MethodNode original, boolean isShadow, String owner) {
        ShadowLocals shadowLocals = ShadowLocals.newInstance(mv, original, isShadow);
        TagPropagator propagator = new TagPropagator(shadowLocals, shadowLocals, isAppClass(owner));
        AnalyzerAdapter analyzer =
                new AnalyzerAdapter(owner, original.access, original.name, original.desc, propagator);
        IndirectFramePasser iPasser = new IndirectFramePasser(shadowLocals, analyzer, analyzer);
        return new ObjectShadowCaller(owner, original.access, original.name, original.desc, iPasser);
    }
}
