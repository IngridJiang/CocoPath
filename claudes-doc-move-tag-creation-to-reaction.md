## Plan: Refactor Variable Tagging with Helper Class in templateReaction

### Summary
Create a Java helper class to cleanly separate the three concerns: (1) variable tagging, (2) constraint formulation, and (3) branch tracking. This will make the reaction code cleaner and more maintainable.

### Key Findings
✅ **Yes, Vitruv reactions CAN call Java helper classes!** Your codebase already uses:
- Direct imports (`import java.io.File`)  
- Reflection for optional dependencies (as seen in the current SymbolicComparison call)
- Full Java interoperability through Xbase/Xtend

### Implementation Plan

#### 1. Create Helper Class Structure
Create a new Java helper class in the consistency project:
```
/home/anne/CocoPath/Amalthea-acset/consistency/src/main/java/
└── tools/vitruv/methodologisttemplate/helpers/
    └── SymbolicExecutionHelper.java
```

#### 2. Design the Helper Class
```java
package tools.vitruv.methodologisttemplate.helpers;

public class SymbolicExecutionHelper {
    
    /**
     * Process a user selection for symbolic execution.
     * Separates three concerns:
     * 1. Variable tagging - Create/attach symbolic tag if needed
     * 2. Domain constraints - Record valid range of values
     * 3. Branch tracking - Record the actual selected value
     * 
     * @param selected The user's selection (may already be tagged)
     * @param variableName Name for the symbolic variable (e.g., "user_choice")
     * @param minValue Minimum valid value (inclusive)
     * @param maxValue Maximum valid value (inclusive)
     * @return Tagged value ready for use in switch statement
     */
    public static Integer processSymbolicChoice(Integer selected, String variableName, int minValue, int maxValue) {
        if (selected == null) return null;
        
        try {
            // Step 1: Variable Tagging
            // Check if value already has a tag
            Object tag = getExistingTag(selected);
            
            if (tag == null) {
                // Create new tag if needed
                tag = createSymbolicTag(variableName, selected);
                selected = attachTagToValue(selected, tag);
            }
            
            // Step 2: Domain Constraints
            // Record that variable can only be in range [minValue, maxValue]
            recordDomainConstraint(variableName, minValue, maxValue);
            
            // Step 3: Branch Tracking
            // Record which value was actually selected
            recordBranchConstraint(variableName, selected);
            
            return selected;
            
        } catch (Exception e) {
            // Graceful degradation if symbolic execution unavailable
            System.err.println("[SymbolicExecutionHelper] Symbolic processing failed: " + e.getMessage());
            return selected;
        }
    }
    
    // Private helper methods for each concern...
    private static Object getExistingTag(Integer value) throws Exception {
        Class<?> tainterClass = Class.forName("edu.neu.ccs.prl.galette.internal.runtime.Tainter");
        return tainterClass.getMethod("getTag", Object.class).invoke(null, value);
    }
    
    private static Object createSymbolicTag(String varName, Integer value) throws Exception {
        Class<?> symbolicatorClass = Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator");
        return symbolicatorClass.getMethod("makeSymbolicInt", String.class, Integer.TYPE)
                .invoke(null, varName, value);
    }
    
    private static Integer attachTagToValue(Integer value, Object tag) throws Exception {
        Class<?> tainterClass = Class.forName("edu.neu.ccs.prl.galette.internal.runtime.Tainter");
        return (Integer) tainterClass.getMethod("setTag", Integer.TYPE, Object.class)
                .invoke(null, value, tag);
    }
    
    private static void recordDomainConstraint(String varName, int min, int max) throws Exception {
        Class<?> pathUtilsClass = Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils");
        pathUtilsClass.getMethod("addIntDomainConstraint", String.class, int.class, int.class)
                .invoke(null, varName, min, max + 1); // +1 because PathUtils uses exclusive upper bound
    }
    
    private static void recordBranchConstraint(String varName, Integer value) throws Exception {
        Class<?> pathUtilsClass = Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils");
        pathUtilsClass.getMethod("addSwitchConstraint", String.class, int.class)
                .invoke(null, varName, value);
    }
}
```

#### 3. Update templateReaction.reactions
Replace lines 90-114 with cleaner code:
```xtend
// Record symbolic constraint for the user's choice
// This enables automatic path exploration through all dialog options
val Integer symbolicSelected = if (selected !== null) {
    try {
        // Use helper class to handle all symbolic execution concerns
        val helperClass = Class.forName("tools.vitruv.methodologisttemplate.helpers.SymbolicExecutionHelper")
        val method = helperClass.getMethod("processSymbolicChoice", 
            Integer, String, Integer.TYPE, Integer.TYPE)
        
        // Process with proper separation of concerns:
        // 1. Variable tagging (creates tag if missing)
        // 2. Domain constraints (0 to options.size-1)
        // 3. Branch tracking (records selected value)
        val result = method.invoke(null, 
            selected,                    // The user's selection
            "user_choice",              // Variable name for symbolic execution
            0,                          // Min valid value
            options.size - 1            // Max valid value (4 for 5 options)
        ) as Integer
        
        println("[Reaction] Symbolic processing complete: " + result)
        result
    } catch (Exception e) {
        // If symbolic execution is not available, use the original value
        println("[Reaction] Symbolic processing unavailable: " + e.message)
        selected
    }
} else {
    println("[Reaction] Selected is null")
    selected
}
```

#### 4. Simplify Path Exploration Classes
Remove tag extraction logic from:
- `AutomaticVitruvPathExploration.executeVitruvWithInput()` (lines 76-133)
- `AutomaticVitruvMultiVarPathExploration.executeVitruvWithTwoInputs()` (lines 104-183)

These classes should just pass values through without modification since tagging now happens in the reaction.

#### 5. Update Test.java
Remove manual constraint recording from `insertTwoTasks()` (lines 91-115) since this is now handled by the reaction.

### Benefits of This Approach

1. **Separation of Concerns**: Three distinct responsibilities are clearly separated
2. **Self-Contained Reactions**: Reactions handle their own symbolic execution needs
3. **Cleaner Code**: Less reflection boilerplate in the reaction
4. **Reusable**: Helper can be used by multiple reactions
5. **Maintainable**: Changes to symbolic execution logic are centralized
6. **Type-Safe**: Helper method has clear parameter types and documentation
7. **Graceful Degradation**: Works with or without symbolic execution available

### Testing Plan
1. Test single variable path exploration
2. Test multi-variable path exploration  
3. Verify constraints are properly collected
4. Ensure existing functionality is preserved
5. Confirm tags are created when missing and preserved when present

### File Changes Summary
1. **Create**: `/home/anne/CocoPath/Amalthea-acset/consistency/src/main/java/tools/vitruv/methodologisttemplate/helpers/SymbolicExecutionHelper.java`
2. **Modify**: `/home/anne/CocoPath/Amalthea-acset/consistency/src/main/reactions/tools/vitruv/methodologisttemplate/consistency/templateReactions.reactions` (lines 90-114)
3. **Simplify**: `AutomaticVitruvPathExploration.java` (remove tag extraction)
4. **Simplify**: `AutomaticVitruvMultiVarPathExploration.java` (remove tag extraction)
5. **Simplify**: `Test.java` (remove manual constraint recording)