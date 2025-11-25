package edu.neu.ccs.prl.galette.concolic.knarr.listener;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;

/**
 * Interface for handling concolic execution events during taint propagation.
 *
 * This interface replaces Phosphor's DerivedTaintListener with Galette-compatible
 * methods for tracking symbolic execution events and path constraints.
 * @purpose Listener for concolic execution mode
 * @feature Combines concrete and symbolic execution
 *
 */
public interface ConcolicTaintListener {

    /**
     * Called when a conditional branch is encountered during execution.
     *
     * @param condition The tag associated with the branch condition
     * @param taken Whether the branch was taken (true) or not (false)
     */
    void onBranch(Tag condition, boolean taken);

    /**
     * Called when an arithmetic operation is performed on symbolic values.
     *
     * @param operand1 Tag of the first operand
     * @param operand2 Tag of the second operand
     * @param result Tag of the operation result
     */
    void onArithmetic(Tag operand1, Tag operand2, Tag result);

    /**
     * Called when a comparison operation is performed.
     *
     * @param left Tag of the left operand
     * @param right Tag of the right operand
     * @param opcode The bytecode opcode of the comparison operation
     * @param result The boolean result of the comparison
     */
    void onComparison(Tag left, Tag right, int opcode, boolean result);

    /**
     * Called when a new path constraint is generated.
     *
     * @param constraint The constraint object (typically a Green expression)
     */
    void onPathConstraint(Object constraint);

    /**
     * Called when an array access operation occurs on a symbolic array.
     *
     * @param arrayTag Tag of the array being accessed
     * @param indexTag Tag of the index value
     * @param valueTag Tag of the value being read/written
     * @param isWrite Whether this is a write (true) or read (false) operation
     */
    void onArrayAccess(Tag arrayTag, Tag indexTag, Tag valueTag, boolean isWrite);

    /**
     * Called when a method call is made with symbolic parameters.
     *
     * @param methodName Name of the method being called
     * @param arguments Array of tags for method parameters
     * @param result Tag for the return value (may be null)
     */
    void onMethodCall(String methodName, Tag[] arguments, Tag result);
}
