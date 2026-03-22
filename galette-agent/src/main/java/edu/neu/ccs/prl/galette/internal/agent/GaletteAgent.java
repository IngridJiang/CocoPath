package edu.neu.ccs.prl.galette.internal.agent;

import edu.neu.ccs.prl.galette.internal.runtime.*;
import edu.neu.ccs.prl.galette.internal.runtime.frame.SpareFrameStore;
import edu.neu.ccs.prl.galette.internal.transform.GaletteLog;
import edu.neu.ccs.prl.galette.internal.transform.GaletteTransformer;
import edu.neu.ccs.prl.galette.internal.transform.TransformationCache;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import org.objectweb.asm.*;

public final class GaletteAgent {
    static {
        SpareFrameStore.initialize();
    }

    private GaletteAgent() {
        throw new AssertionError();
    }

    @SuppressWarnings("unused")
    public static void premain(String agentArgs, Instrumentation inst, TagFrame frame) throws IOException {
        premain(agentArgs, inst);
    }

    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        GaletteLog.initialize(System.err);
        String cachePath = System.getProperty("galette.cache");
        TransformationCache cache = cachePath == null ? null : new TransformationCache(new File(cachePath));
        GaletteTransformer.setCache(cache);
        inst.addTransformer(new TransformerWrapper());
        // Register comparison interception as a second-pass transformer.
        if (Boolean.getBoolean("galette.concolic.interception.enabled")) {
            inst.addTransformer(new ComparisonInterceptionTransformer());
        }
    }

    private static final class TransformerWrapper implements ClassFileTransformer {
        private final GaletteTransformer transformer = new GaletteTransformer();

        @SuppressWarnings("unused")
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain pd,
                byte[] buf,
                TagFrame frame) {
            return transform(loader, className, classBeingRedefined, pd, buf);
        }

        @Override
        public byte[] transform(
                ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] buf) {
            if (classBeingRedefined != null) return null;
            return transformer.transform(buf, false);
        }
    }

    /**
     * Second-pass transformer that intercepts comparison bytecodes in application classes.
     * All interception logic is inlined here to avoid loading classes from internal.transform
     * (which may not exist in the jlink image).
     */
    private static final class ComparisonInterceptionTransformer implements ClassFileTransformer {
        private static final String PATH_UTILS = "edu/neu/ccs/prl/galette/interception/InterceptionPathUtils";

        @SuppressWarnings("unused")
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain pd,
                byte[] buf,
                TagFrame frame) {
            return transform(loader, className, classBeingRedefined, pd, buf);
        }

        @Override
        public byte[] transform(
                ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] buf) {
            if (classBeingRedefined != null || className == null || isExcluded(className)) return null;
            try {
                ClassReader cr = new ClassReader(buf);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                cr.accept(new InterceptionClassVisitor(cw), ClassReader.EXPAND_FRAMES);
                return cw.toByteArray();
            } catch (Throwable t) {
                return null;
            }
        }

        private static boolean isExcluded(String cn) {
            return cn.startsWith("java/")
                    || cn.startsWith("javax/")
                    || cn.startsWith("sun/")
                    || cn.startsWith("jdk/")
                    || cn.startsWith("com/sun/")
                    || cn.startsWith("edu/neu/ccs/prl/galette/internal/")
                    || cn.startsWith("edu/neu/ccs/prl/galette/interception/")
                    || cn.startsWith("edu/neu/ccs/prl/galette/concolic/")
                    || cn.startsWith("edu/neu/ccs/prl/galette/PathConstraintAPI")
                    || cn.startsWith("za/ac/sun/cs/green/")
                    || cn.startsWith("edu/gmu/swe/")
                    || cn.startsWith("org/objectweb/asm/")
                    || cn.startsWith("org/eclipse/")
                    || cn.startsWith("tools/vitruv/")
                    || cn.startsWith("com/google/");
        }

        /** Inlined ClassVisitor that intercepts comparison bytecodes. */
        private static final class InterceptionClassVisitor extends ClassVisitor {
            InterceptionClassVisitor(ClassVisitor cv) {
                super(Opcodes.ASM9, cv);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);
                return new InterceptionMethodVisitor(mv);
            }
        }

        /** Inlined MethodVisitor that replaces comparison instructions with PathUtils calls. */
        private static final class InterceptionMethodVisitor extends MethodVisitor {
            InterceptionMethodVisitor(MethodVisitor mv) {
                super(Opcodes.ASM9, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                switch (opcode) {
                    case Opcodes.LCMP:
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, PATH_UTILS, "instrumentedLcmp", "(JJ)I", false);
                        break;
                    case Opcodes.FCMPL:
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, PATH_UTILS, "instrumentedFcmpl", "(FF)I", false);
                        break;
                    case Opcodes.FCMPG:
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, PATH_UTILS, "instrumentedFcmpg", "(FF)I", false);
                        break;
                    case Opcodes.DCMPL:
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, PATH_UTILS, "instrumentedDcmpl", "(DD)I", false);
                        break;
                    case Opcodes.DCMPG:
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, PATH_UTILS, "instrumentedDcmpg", "(DD)I", false);
                        break;
                    default:
                        super.visitInsn(opcode);
                }
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                switch (opcode) {
                    case Opcodes.IF_ICMPEQ:
                    case Opcodes.IF_ICMPNE:
                    case Opcodes.IF_ICMPLT:
                    case Opcodes.IF_ICMPGE:
                    case Opcodes.IF_ICMPGT:
                    case Opcodes.IF_ICMPLE:
                        mv.visitLdcInsn(opToString(opcode));
                        mv.visitMethodInsn(
                                Opcodes.INVOKESTATIC,
                                PATH_UTILS,
                                "instrumentedIcmpJump",
                                "(IILjava/lang/String;)Z",
                                false);
                        mv.visitJumpInsn(Opcodes.IFNE, label);
                        break;
                    case Opcodes.IFEQ:
                    case Opcodes.IFNE:
                    case Opcodes.IFLT:
                    case Opcodes.IFGE:
                    case Opcodes.IFGT:
                    case Opcodes.IFLE:
                        // Single-operand: push 0 and use two-operand version
                        mv.visitInsn(Opcodes.ICONST_0);
                        mv.visitLdcInsn(opToString(opcode));
                        mv.visitMethodInsn(
                                Opcodes.INVOKESTATIC,
                                PATH_UTILS,
                                "instrumentedIcmpJump",
                                "(IILjava/lang/String;)Z",
                                false);
                        mv.visitJumpInsn(Opcodes.IFNE, label);
                        break;
                    default:
                        super.visitJumpInsn(opcode, label);
                }
            }

            private static String opToString(int opcode) {
                switch (opcode) {
                    case Opcodes.IF_ICMPEQ:
                    case Opcodes.IFEQ:
                        return "EQ";
                    case Opcodes.IF_ICMPNE:
                    case Opcodes.IFNE:
                        return "NE";
                    case Opcodes.IF_ICMPLT:
                    case Opcodes.IFLT:
                        return "LT";
                    case Opcodes.IF_ICMPGE:
                    case Opcodes.IFGE:
                        return "GE";
                    case Opcodes.IF_ICMPGT:
                    case Opcodes.IFGT:
                        return "GT";
                    case Opcodes.IF_ICMPLE:
                    case Opcodes.IFLE:
                        return "LE";
                    default:
                        return "UNKNOWN";
                }
            }
        }
    }
}
