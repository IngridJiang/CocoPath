package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.Stack;

/**
 * Galette-compatible replacement for Phosphor's ControlTaintTagStack.
 *
 * This class manages the control flow stack for concolic execution,
 * tracking which control flow branches affect the current execution path.
 * @purpose Control flow stack for concolic execution
 * @feature Tracks conditional branches
 * @feature Manages path constraint collection
 *
 */
public class ConcolicControlStack {

    /**
     * Thread-local stack to track control flow tags per thread.
     */
    private static final ThreadLocal<Stack<Tag>> controlStack = ThreadLocal.withInitial(Stack::new);

    /**
     * Thread-local stack to track depth of control flow nesting.
     */
    private static final ThreadLocal<Stack<Integer>> depthStack = ThreadLocal.withInitial(Stack::new);

    /**
     * Push a control flow tag onto the stack.
     * Called when entering a conditional block.
     *
     * @param tag The tag associated with the control condition
     */
    public static void pushControl(Tag tag) {
        Stack<Tag> stack = controlStack.get();
        stack.push(tag);

        Stack<Integer> depth = depthStack.get();
        depth.push(stack.size());
    }

    /**
     * Pop a control flow tag from the stack.
     * Called when exiting a conditional block.
     *
     * @return The tag that was popped, or null if stack was empty
     */
    public static Tag popControl() {
        Stack<Tag> stack = controlStack.get();
        Stack<Integer> depth = depthStack.get();

        if (!stack.isEmpty() && !depth.isEmpty()) {
            depth.pop();
            return stack.pop();
        }
        return null;
    }

    /**
     * Peek at the top control flow tag without removing it.
     *
     * @return The top tag, or null if stack is empty
     */
    public static Tag peekControl() {
        Stack<Tag> stack = controlStack.get();
        return stack.isEmpty() ? null : stack.peek();
    }

    /**
     * Get the current control flow tag that affects this execution.
     * This combines all active control flow conditions.
     *
     * @return Combined tag representing current control flow context
     */
    public static Tag getCurrentControlTag() {
        Stack<Tag> stack = controlStack.get();

        if (stack.isEmpty()) {
            return null;
        }

        // Combine all control tags in the stack
        Tag result = null;
        for (Tag tag : stack) {
            if (tag != null) {
                if (result == null) {
                    result = tag;
                } else {
                    result = Tag.union(result, tag);
                }
            }
        }

        return result;
    }

    /**
     * Get the current nesting depth of control flow.
     *
     * @return The depth of nested control structures
     */
    public static int getControlDepth() {
        return controlStack.get().size();
    }

    /**
     * Clear the control stack for the current thread.
     * Useful for cleanup or resetting state.
     */
    public static void clearControl() {
        controlStack.get().clear();
        depthStack.get().clear();
    }

    /**
     * Check if we're currently inside any control flow structure.
     *
     * @return true if inside a conditional block, false otherwise
     */
    public static boolean hasControlFlow() {
        return !controlStack.get().isEmpty();
    }
}
