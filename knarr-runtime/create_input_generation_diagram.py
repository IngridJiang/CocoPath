import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch, Rectangle, Circle
import matplotlib.lines as mlines

# Create figure
fig, ax = plt.subplots(1, 1, figsize=(16, 12))
ax.set_xlim(0, 16)
ax.set_ylim(0, 12)
ax.axis('off')

# Title
ax.text(8, 11.5, 'Input Generation for Path Exploration using GreenSolver',
        ha='center', va='center', fontsize=16, weight='bold', family='sans-serif')
ax.text(8, 11.0, 'How to generate concrete input values for each execution path',
        ha='center', va='center', fontsize=11, style='italic', family='sans-serif')

# Colors
color_step = '#E3F2FD'
color_constraint = '#FFF3E0'
color_solution = '#E8F5E9'
color_highlight = '#FFEBEE'

# ======================== LEFT SIDE: Method 1 - Manual Enumeration ========================
x_left = 0.5
y_start = 9.5

# Method 1 Title
method1_box = FancyBboxPatch((x_left, y_start), 7, 0.6, boxstyle="round,pad=0.05",
                             edgecolor='#1976D2', facecolor='#BBDEFB', linewidth=3)
ax.add_patch(method1_box)
ax.text(x_left + 3.5, y_start + 0.3, 'METHOD 1: Manual Enumeration (Current)',
        ha='center', va='center', fontsize=11, weight='bold')

# Step 1: Define values
step1 = FancyBboxPatch((x_left, y_start - 1.0), 7, 0.8, boxstyle="round,pad=0.05",
                       edgecolor='#1976D2', facecolor=color_step, linewidth=2)
ax.add_patch(step1)
ax.text(x_left + 3.5, y_start - 0.45, '1. Define All Possible Values',
        ha='center', va='center', fontsize=9, weight='bold')
ax.text(x_left + 3.5, y_start - 0.7, 'user_choice: [0, 1, 2, 3, 4]',
        ha='center', va='center', fontsize=8, family='monospace', color='#0D47A1')

# Step 2: Generate combinations
step2 = FancyBboxPatch((x_left, y_start - 2.0), 7, 0.8, boxstyle="round,pad=0.05",
                       edgecolor='#1976D2', facecolor=color_step, linewidth=2)
ax.add_patch(step2)
ax.text(x_left + 3.5, y_start - 1.45, '2. Generate Input Combinations',
        ha='center', va='center', fontsize=9, weight='bold')
ax.text(x_left + 3.5, y_start - 1.7, 'generateInputCombinations(ranges)',
        ha='center', va='center', fontsize=8, family='monospace')

# Step 3: Execute each
step3 = FancyBboxPatch((x_left, y_start - 3.0), 7, 0.8, boxstyle="round,pad=0.05",
                       edgecolor='#1976D2', facecolor=color_step, linewidth=2)
ax.add_patch(step3)
ax.text(x_left + 3.5, y_start - 2.45, '3. Execute Each Path',
        ha='center', va='center', fontsize=9, weight='bold')
ax.text(x_left + 3.5, y_start - 2.7, 'for input in [0, 1, 2, 3, 4]: execute(input)',
        ha='center', va='center', fontsize=7, family='monospace')

# Output boxes for Method 1
y_output1 = y_start - 4.5
outputs1 = [
    (0.5, 'Path 0', 'input=0'),
    (2.0, 'Path 1', 'input=1'),
    (3.5, 'Path 2', 'input=2'),
    (5.0, 'Path 3', 'input=3'),
    (6.5, 'Path 4', 'input=4')
]

for x_off, title, subtitle in outputs1:
    out_box = FancyBboxPatch((x_left + x_off, y_output1), 1.3, 0.5,
                             boxstyle="round,pad=0.03",
                             edgecolor='#388E3C', facecolor=color_solution, linewidth=1.5)
    ax.add_patch(out_box)
    ax.text(x_left + x_off + 0.65, y_output1 + 0.35, title,
            ha='center', va='center', fontsize=7, weight='bold')
    ax.text(x_left + x_off + 0.65, y_output1 + 0.15, subtitle,
            ha='center', va='center', fontsize=6, family='monospace')

# Arrow from step 3 to outputs
arrow_m1 = FancyArrowPatch((x_left + 3.5, y_start - 3.0), (x_left + 3.5, y_output1 + 0.5),
                          arrowstyle='->', mutation_scale=20, linewidth=2, color='#1976D2')
ax.add_patch(arrow_m1)

# Arrows between steps
for i in range(2):
    y_from = y_start - 1.0 - i
    arrow = FancyArrowPatch((x_left + 3.5, y_from), (x_left + 3.5, y_from - 0.2),
                           arrowstyle='->', mutation_scale=15, linewidth=2, color='#666')
    ax.add_patch(arrow)

# Pros/Cons for Method 1
pros_cons1 = FancyBboxPatch((x_left, y_output1 - 1.2), 7, 0.9, boxstyle="round,pad=0.05",
                            edgecolor='#388E3C', facecolor='#F1F8E9', linewidth=1.5)
ax.add_patch(pros_cons1)
ax.text(x_left + 0.3, y_output1 - 0.5, 'PROS:', ha='left', va='center', fontsize=7, weight='bold', color='#388E3C')
ax.text(x_left + 0.3, y_output1 - 0.7, '• Simple, easy to understand', ha='left', va='center', fontsize=6)
ax.text(x_left + 0.3, y_output1 - 0.85, '• No external dependencies', ha='left', va='center', fontsize=6)
ax.text(x_left + 0.3, y_output1 - 1.0, '• Works for discrete choices', ha='left', va='center', fontsize=6)

ax.text(x_left + 4.0, y_output1 - 0.5, 'CONS:', ha='left', va='center', fontsize=7, weight='bold', color='#D32F2F')
ax.text(x_left + 4.0, y_output1 - 0.7, '• Must know all values', ha='left', va='center', fontsize=6)
ax.text(x_left + 4.0, y_output1 - 0.85, '• Doesn\'t scale to continuous', ha='left', va='center', fontsize=6)
ax.text(x_left + 4.0, y_output1 - 1.0, '• Manual enumeration', ha='left', va='center', fontsize=6)

# ======================== RIGHT SIDE: Method 2 - Constraint-Based ========================
x_right = 8.5
y_start_r = 9.5

# Method 2 Title
method2_box = FancyBboxPatch((x_right, y_start_r), 7, 0.6, boxstyle="round,pad=0.05",
                             edgecolor='#F57C00', facecolor='#FFE0B2', linewidth=3)
ax.add_patch(method2_box)
ax.text(x_right + 3.5, y_start_r + 0.3, 'METHOD 2: Constraint-Based (Advanced)',
        ha='center', va='center', fontsize=11, weight='bold')

# Step 1: Execute first path
step2_1 = FancyBboxPatch((x_right, y_start_r - 1.0), 7, 0.8, boxstyle="round,pad=0.05",
                         edgecolor='#F57C00', facecolor=color_step, linewidth=2)
ax.add_patch(step2_1)
ax.text(x_right + 3.5, y_start_r - 0.45, '1. Execute Path & Collect Constraint',
        ha='center', va='center', fontsize=9, weight='bold')
ax.text(x_right + 3.5, y_start_r - 0.7, 'input=0 → constraint: (user_choice == 0)',
        ha='center', va='center', fontsize=7, family='monospace', color='#E65100')

# Step 2: Negate constraint
step2_2 = FancyBboxPatch((x_right, y_start_r - 2.0), 7, 0.8, boxstyle="round,pad=0.05",
                         edgecolor='#F57C00', facecolor=color_constraint, linewidth=2)
ax.add_patch(step2_2)
ax.text(x_right + 3.5, y_start_r - 1.45, '2. Negate Constraint',
        ha='center', va='center', fontsize=9, weight='bold')
ax.text(x_right + 3.5, y_start_r - 1.65, '(user_choice == 0) → (user_choice != 0)',
        ha='center', va='center', fontsize=7, family='monospace')
ax.text(x_right + 3.5, y_start_r - 1.85, 'or: (user_choice == 0 + 1) = 1',
        ha='center', va='center', fontsize=7, family='monospace', style='italic')

# Step 3: Solve for new input
step2_3 = FancyBboxPatch((x_right, y_start_r - 3.0), 7, 0.8, boxstyle="round,pad=0.05",
                         edgecolor='#F57C00', facecolor=color_solution, linewidth=2)
ax.add_patch(step2_3)
ax.text(x_right + 3.5, y_start_r - 2.45, '3. Solve for New Input',
        ha='center', va='center', fontsize=9, weight='bold')
ax.text(x_right + 3.5, y_start_r - 2.7, 'solvePathCondition() → InputSolution{user_choice=1}',
        ha='center', va='center', fontsize=7, family='monospace')

# Iteration loop
loop_arrow = FancyArrowPatch((x_right + 6.8, y_start_r - 2.6), (x_right + 6.8, y_start_r - 0.6),
                            arrowstyle='->', mutation_scale=20, linewidth=2,
                            color='#F57C00', linestyle='dashed')
ax.add_patch(loop_arrow)
ax.text(x_right + 7.3, y_start_r - 1.6, 'Repeat\nuntil\nUNSAT',
        ha='left', va='center', fontsize=7, style='italic', color='#F57C00')

# Output visualization for Method 2
y_output2 = y_start_r - 4.5
constraint_flow = [
    (0.5, '(x==0)', 'input=0'),
    (2.0, '(x==1)', 'input=1'),
    (3.5, '(x==2)', 'input=2'),
    (5.0, '(x==3)', 'input=3'),
    (6.5, 'UNSAT', 'Done!')
]

for i, (x_off, title, subtitle) in enumerate(constraint_flow):
    if i < len(constraint_flow) - 1:
        out_box = FancyBboxPatch((x_right + x_off, y_output2), 1.3, 0.5,
                                 boxstyle="round,pad=0.03",
                                 edgecolor='#F57C00', facecolor=color_constraint, linewidth=1.5)
    else:
        out_box = FancyBboxPatch((x_right + x_off, y_output2), 1.3, 0.5,
                                 boxstyle="round,pad=0.03",
                                 edgecolor='#D32F2F', facecolor=color_highlight, linewidth=2)
    ax.add_patch(out_box)
    ax.text(x_right + x_off + 0.65, y_output2 + 0.35, title,
            ha='center', va='center', fontsize=7, weight='bold')
    ax.text(x_right + x_off + 0.65, y_output2 + 0.15, subtitle,
            ha='center', va='center', fontsize=6, family='monospace')

    # Arrows between constraints
    if i < len(constraint_flow) - 1:
        arrow_c = FancyArrowPatch((x_right + x_off + 1.3, y_output2 + 0.25),
                                 (x_right + x_off + 2.0, y_output2 + 0.25),
                                 arrowstyle='->', mutation_scale=12, linewidth=1.5, color='#F57C00')
        ax.add_patch(arrow_c)

# Arrow from step 3 to outputs
arrow_m2 = FancyArrowPatch((x_right + 3.5, y_start_r - 3.0), (x_right + 3.5, y_output2 + 0.5),
                          arrowstyle='->', mutation_scale=20, linewidth=2, color='#F57C00')
ax.add_patch(arrow_m2)

# Arrows between steps
for i in range(2):
    y_from = y_start_r - 1.0 - i
    arrow = FancyArrowPatch((x_right + 3.5, y_from), (x_right + 3.5, y_from - 0.2),
                           arrowstyle='->', mutation_scale=15, linewidth=2, color='#666')
    ax.add_patch(arrow)

# Pros/Cons for Method 2
pros_cons2 = FancyBboxPatch((x_right, y_output2 - 1.2), 7, 0.9, boxstyle="round,pad=0.05",
                            edgecolor='#F57C00', facecolor='#FFF8E1', linewidth=1.5)
ax.add_patch(pros_cons2)
ax.text(x_right + 0.3, y_output2 - 0.5, 'PROS:', ha='left', va='center', fontsize=7, weight='bold', color='#388E3C')
ax.text(x_right + 0.3, y_output2 - 0.7, '• Automatic input generation', ha='left', va='center', fontsize=6)
ax.text(x_right + 0.3, y_output2 - 0.85, '• Handles complex constraints', ha='left', va='center', fontsize=6)
ax.text(x_right + 0.3, y_output2 - 1.0, '• Discovers edge cases', ha='left', va='center', fontsize=6)

ax.text(x_right + 4.0, y_output2 - 0.5, 'CONS:', ha='left', va='center', fontsize=7, weight='bold', color='#D32F2F')
ax.text(x_right + 4.0, y_output2 - 0.7, '• Needs SAT solver', ha='left', va='center', fontsize=6)
ax.text(x_right + 4.0, y_output2 - 0.85, '• More complex setup', ha='left', va='center', fontsize=6)
ax.text(x_right + 4.0, y_output2 - 1.0, '• Slower execution', ha='left', va='center', fontsize=6)

# ======================== BOTTOM: Code Examples ========================
y_code = 1.8

# Code example box
code_box = FancyBboxPatch((0.5, y_code - 1.2), 15, 1.2, boxstyle="round,pad=0.05",
                          edgecolor='#424242', facecolor='#FAFAFA', linewidth=2)
ax.add_patch(code_box)
ax.text(8, y_code - 0.2, 'GreenSolver Constraint Negation Logic',
        ha='center', va='center', fontsize=9, weight='bold', family='monospace')

code_lines = [
    "switch (operator) {",
    "    case EQ:  return threshold + 1.0;    // x == 5 → generate x = 6",
    "    case NE:  return threshold;          // x != 5 → generate x = 5",
    "    case GT:  return threshold - 0.1;    // x > 5 → generate x = 4.9",
    "    case LT:  return threshold + 0.1;    // x < 5 → generate x = 5.1",
    "}"
]

y_line = y_code - 0.45
for line in code_lines:
    ax.text(1.0, y_line, line, ha='left', va='center', fontsize=6.5, family='monospace', color='#424242')
    y_line -= 0.15

# InputSolution example
ax.text(9.5, y_code - 0.35, 'InputSolution solution = new InputSolution();',
        ha='left', va='center', fontsize=6.5, family='monospace', color='#1976D2')
ax.text(9.5, y_code - 0.50, 'solution.setValue("user_choice", 1);',
        ha='left', va='center', fontsize=6.5, family='monospace', color='#388E3C')
ax.text(9.5, y_code - 0.65, 'solution.setValue("constraint", "(x == 1)");',
        ha='left', va='center', fontsize=6.5, family='monospace', color='#388E3C')
ax.text(9.5, y_code - 0.80, 'solution.setSatisfiable(true);',
        ha='left', va='center', fontsize=6.5, family='monospace', color='#388E3C')
ax.text(9.5, y_code - 0.95, 'return solution;  // Next input to explore',
        ha='left', va='center', fontsize=6.5, family='monospace', color='#F57C00')

# Legend
legend_y = 0.4
legend_elements = [
    Rectangle((0, 0), 1, 1, fc=color_step, ec='#1976D2', linewidth=2, label='Execution Steps'),
    Rectangle((0, 0), 1, 1, fc=color_constraint, ec='#F57C00', linewidth=2, label='Constraints'),
    Rectangle((0, 0), 1, 1, fc=color_solution, ec='#388E3C', linewidth=2, label='Solutions/Inputs'),
    Rectangle((0, 0), 1, 1, fc=color_highlight, ec='#D32F2F', linewidth=2, label='Termination')
]
legend = ax.legend(handles=legend_elements, loc='lower center', ncol=4,
                  frameon=True, fontsize=7, title='KEY CONCEPTS')
legend.get_frame().set_facecolor('#FAFAFA')
legend.get_frame().set_edgecolor('#666')

plt.tight_layout()
plt.savefig('C:/Users/10239/galette-vitruv/knarr-runtime/input_generation_diagram.png',
            dpi=300, bbox_inches='tight', facecolor='white')
print("Input generation diagram created: knarr-runtime/input_generation_diagram.png")
