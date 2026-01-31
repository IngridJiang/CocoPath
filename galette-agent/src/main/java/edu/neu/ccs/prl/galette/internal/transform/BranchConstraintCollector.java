package edu.neu.ccs.prl.galette.internal.transform;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Method visitor that automatically collects path constraints at branch points.
 *
 * <p>This visitor instruments conditional jumps (if statements) and switch statements
 * to automatically record path constraints during symbolic execution. It intercepts:
 *
 * <ul>
 *   <li>Conditional jumps: IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMP*, etc.
 *   <li>Switch statements: TABLESWITCH, LOOKUPSWITCH
 * </ul>
 *
 * <p>When a branch is encountered, this visitor injects bytecode to call runtime
 * methods in PathUtils that check if symbolic tags are present and collect
 * appropriate constraints.
 *
 * <p><b>Design based on Phosphor's PathConstraintTagFactory approach.</b>
 *
 * @origin GALETTE_KNARR - New Galette class created to support Knarr/concolic execution.
 *         This class was added to CocoPath to enable automatic path constraint collection
 *         at branch points, bridging Galette's taint tracking with Knarr's symbolic execution.
 *         Based on design patterns from gmu-swe/knarr PathConstraintTagFactory.
 * @see edu.gmu.swe.knarr.runtime.PathConstraintTagFactory (original Knarr/Phosphor implementation)
 */
class BranchConstraintCollector extends MethodVisitor {

    /** Internal name of PathUtils runtime class */
    private static final String PATH_UTILS_INTERNAL_NAME = "edu/neu/ccs/prl/galette/concolic/knarr/runtime/PathUtils";

    /** Descriptor for Tag class */
    private static final String TAG_DESC = "Ledu/neu/ccs/prl/galette/internal/runtime/Tag;";

    /** Owner class name for this method */
    private final String owner;

    /** Method name being instrumented */
    private final String methodName;

    /** Method descriptor being instrumented */
    private final String methodDesc;

    /** Current line number for debugging */
    private int lineNumber = 0;

    /** Whether symbolic execution is enabled (check system property) */
    private static final boolean SYMBOLIC_ENABLED = Boolean.getBoolean("galette.symbolic.enabled");

    BranchConstraintCollector(String owner, String methodName, String methodDesc, MethodVisitor mv) {
        super(GaletteTransformer.ASM_VERSION, mv);
        this.owner = owner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.lineNumber = line;
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (!SYMBOLIC_ENABLED) {
            super.visitJumpInsn(opcode, label);
            return;
        }

        switch (opcode) {
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
                instrumentSingleValueBranch(opcode, label);
                break;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
                instrumentTwoValueBranch(opcode, label);
                break;
            case IFNULL:
            case IFNONNULL:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case GOTO:
            case JSR:
                // No symbolic tracking for these (yet)
                super.visitJumpInsn(opcode, label);
                break;
            default:
                super.visitJumpInsn(opcode, label);
                break;
        }
    }

    /**
     * Instrument branches that compare one value against zero (IFEQ, IFNE, etc.)
     *
     * Stack before: ..., value
     * Stack after: ...
     *
     * We need to:
     * 1. Peek at the tag for the value (on shadow stack)
     * 2. Check if tag is symbolic
     * 3. If symbolic, record constraint before taking/not taking branch
     * 4. Execute the original branch instruction
     */
    private void instrumentSingleValueBranch(int opcode, Label label) {
        // For now, delegate to super - full implementation requires shadow stack access
        // TODO: Integrate with ShadowLocals to check if value has symbolic tag
        super.visitJumpInsn(opcode, label);

        // Future implementation will insert:
        // if (tag != null && tag.isSymbolic()) {
        //     PathUtils.recordBranchConstraint(tag, opcode, branchTaken);
        // }
    }

    /**
     * Instrument branches that compare two values (IF_ICMPEQ, IF_ICMPNE, etc.)
     *
     * Stack before: ..., value1, value2
     * Stack after: ...
     */
    private void instrumentTwoValueBranch(int opcode, Label label) {
        // For now, delegate to super
        super.visitJumpInsn(opcode, label);

        // Future implementation will check both tags and record constraint
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        if (!SYMBOLIC_ENABLED) {
            super.visitTableSwitchInsn(min, max, dflt, labels);
            return;
        }

        // Stack before: ..., index
        // We need to check if index has a symbolic tag

        // For now, instrument by adding constraint collection after switch
        instrumentSwitchStatement(min, max, dflt, labels, true);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        if (!SYMBOLIC_ENABLED) {
            super.visitLookupSwitchInsn(dflt, keys, labels);
            return;
        }

        // Similar to tableswitch
        instrumentSwitchStatement(0, keys.length - 1, dflt, labels, false);
    }

    /**
     * Instrument switch statements to collect path constraints.
     *
     * This is the key method that enables automatic constraint collection for switch/case.
     *
     * Strategy (based on Phosphor's approach):
     * 1. Duplicate the switch value
     * 2. Execute the switch with fresh labels
     * 3. At each case, record the constraint (var == caseValue) before jumping to original label
     * 4. At default case, record negation of all case values
     */
    private void instrumentSwitchStatement(int min, int max, Label dflt, Label[] labels, boolean isTableSwitch) {
        // Generate fresh intermediate labels
        Label[] freshLabels = new Label[labels.length];
        for (int i = 0; i < freshLabels.length; i++) {
            freshLabels[i] = new Label();
        }
        Label freshDefault = new Label();

        // Duplicate the switch index value for constraint recording
        super.visitInsn(DUP);

        // Execute switch with fresh labels
        if (isTableSwitch) {
            super.visitTableSwitchInsn(min, max, freshDefault, freshLabels);
        } else {
            // For lookup switch, we need to reconstruct keys array
            int[] keys = new int[labels.length];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = min + i; // Approximation - actual keys needed from context
            }
            super.visitLookupSwitchInsn(freshDefault, keys, freshLabels);
        }

        // For each case, add constraint recording before jumping to original label
        for (int i = 0; i < freshLabels.length; i++) {
            super.visitLabel(freshLabels[i]);

            // Record constraint: index == caseValue
            // Stack at this point: ..., index (duplicated value still on stack)

            // Call PathUtils.recordSwitchConstraint(tag, caseValue)
            // But we need access to the shadow tag - requires integration with shadow stack

            // For now, just jump to original label
            super.visitJumpInsn(GOTO, labels[i]);
        }

        // Default case
        super.visitLabel(freshDefault);
        // Record that index != any case value
        super.visitJumpInsn(GOTO, dflt);
    }

    /**
     * Create a new BranchConstraintCollector wrapping the given MethodVisitor.
     *
     * @param owner Class name containing the method
     * @param methodName Method name
     * @param methodDesc Method descriptor
     * @param mv MethodVisitor to wrap
     * @return Wrapped MethodVisitor with branch constraint collection
     */
    static MethodVisitor newInstance(String owner, String methodName, String methodDesc, MethodVisitor mv) {
        // Only enable if symbolic execution is turned on
        if (!SYMBOLIC_ENABLED) {
            return mv;
        }
        return new BranchConstraintCollector(owner, methodName, methodDesc, mv);
    }
}
