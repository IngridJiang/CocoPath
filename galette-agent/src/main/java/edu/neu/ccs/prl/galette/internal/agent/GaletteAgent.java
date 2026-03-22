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
            // Force-load InterceptionPathUtils BEFORE registering the transformer.
            // Prevents classloader deadlock: when the transformer inserts INVOKESTATIC
            // references to InterceptionPathUtils into a class being loaded, the JVM
            // would try to resolve it while holding the ClassLoader lock.
            try {
                Class.forName("edu.neu.ccs.prl.galette.interception.InterceptionPathUtils");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to pre-load InterceptionPathUtils", e);
            }
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
                // Use COMPUTE_MAXS to fix max stack/locals. Pass 0 to accept()
                // to preserve existing stack map frames from the first pass.
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS) {
                    @Override
                    protected String getCommonSuperClass(String type1, String type2) {
                        return "java/lang/Object";
                    }
                };
                cr.accept(new InterceptionClassVisitor(cw), 0);
                byte[] result = cw.toByteArray();
                if (result.length != buf.length) {
                    System.out.println("[Interception2ndPass] Modified: " + className + " (" + buf.length + " -> "
                            + result.length + ")");
                }
                return result;
            } catch (Throwable t) {
                System.err.println("[Interception2ndPass] ERROR: " + className + ": " + t);
                return null;
            }
        }

        /**
         * Whitelist approach: only intercept classes that contain the user's
         * comparison logic (Vitruvius-generated reaction/routine classes).
         * Everything else is excluded to avoid classloader deadlocks and noise.
         */
        private static boolean isExcluded(String cn) {
            // Only intercept MIR-generated reaction/routine classes
            return !cn.startsWith("mir/");
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
                        // Single-operand jumps (IFEQ/IFNE/IFLT/IFGE/IFGT/IFLE) are NOT
                        // intercepted: they are ubiquitous in bytecode (boolean checks,
                        // null checks, loop counters) and produce massive constraint noise.
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
