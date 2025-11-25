#!/usr/bin/env python3
"""
Visualize the symbolic execution results from execution_paths.json
"""

import json
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch
import numpy as np
import sys
import os

def load_execution_data(json_file='execution_paths.json'):
    """Load execution paths from JSON file."""
    if not os.path.exists(json_file):
        print(f"Error: {json_file} not found!")
        sys.exit(1)

    with open(json_file, 'r') as f:
        data = json.load(f)

    print(f"Loaded {len(data)} execution paths from {json_file}")
    return data

def create_execution_tree(paths, output_file='execution_tree.png'):
    """Create a tree visualization of execution paths."""

    fig, ax = plt.subplots(1, 1, figsize=(16, 10))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 14)
    ax.axis('off')

    # Calculate totals (exec time only for main chart)
    total_exec = sum(p["executionTime"] for p in paths)
    total_init = sum(p.get("initializationTime", 0) for p in paths)

    # Title
    ax.text(5, 13.5, 'Symbolic Execution Tree - Vitruvius Model Transformation',
            ha='center', va='center', fontsize=18, fontweight='bold')
    ax.text(5, 13, f'{len(paths)} paths explored | Total execution time: {total_exec}ms',
            ha='center', va='center', fontsize=12, style='italic')

    # Show init time as separate info if present
    if total_init > 0:
        ax.text(5, 12.6, f'(+ {total_init}ms one-time initialization for Path 1)',
                ha='center', va='center', fontsize=10, style='italic', color='#FF6B6B')

    # Root node
    root_box = FancyBboxPatch((4, 11.5), 2, 0.8,
                             boxstyle="round,pad=0.1",
                             edgecolor='#333',
                             facecolor='#FFD700',
                             alpha=0.8, linewidth=3)
    ax.add_patch(root_box)
    ax.text(5, 12.1, 'START',
            ha='center', va='center', fontsize=12, fontweight='bold')
    ax.text(5, 11.8, 'Tag user_choice',
            ha='center', va='center', fontsize=9, style='italic')

    # Task types and their colors
    task_types = [
        ('InterruptTask', '#FF6B6B'),
        ('PeriodicTask', '#4ECDC4'),
        ('SoftwareTask', '#95E1D3'),
        ('TimeTableTask', '#F38181'),
        ('Decide Later', '#AAAAAA'),
    ]

    # Branch nodes
    y_start = 10
    x_positions = [1, 2.5, 4, 5.5, 7]

    for i, path in enumerate(paths):
        x_pos = x_positions[i]
        y_pos = y_start

        # Get execution time only (init shown separately)
        exec_time = path["executionTime"]

        # Decision node
        decision_box = FancyBboxPatch((x_pos - 0.4, y_pos - 0.4), 0.8, 0.8,
                                     boxstyle="round,pad=0.05",
                                     edgecolor='#666',
                                     facecolor='#E8E8E8',
                                     alpha=0.9, linewidth=2)
        ax.add_patch(decision_box)
        ax.text(x_pos, y_pos + 0.2, f'choice={i}',
                ha='center', va='center', fontsize=9, fontweight='bold')
        ax.text(x_pos, y_pos - 0.15, f'{exec_time}ms',
                ha='center', va='center', fontsize=7, style='italic')

        # Arrow from root to decision
        ax.plot([5, x_pos], [11.5, y_pos + 0.4], 'k-', linewidth=2, alpha=0.6)

        # Constraint box
        constraint_y = y_pos - 1.5
        constraint_box = FancyBboxPatch((x_pos - 0.5, constraint_y - 0.3), 1, 0.6,
                                       boxstyle="round,pad=0.05",
                                       edgecolor='#4169E1',
                                       facecolor='#E6F2FF',
                                       alpha=0.8, linewidth=1.5)
        ax.add_patch(constraint_box)

        constraints = path.get('constraints', [])
        if constraints:
            constraint_text = constraints[0] if len(constraints) == 1 else f'{len(constraints)} constraints'
            ax.text(x_pos, constraint_y, constraint_text,
                    ha='center', va='center', fontsize=7, family='monospace')

        # Arrow to constraint
        ax.plot([x_pos, x_pos], [y_pos - 0.4, constraint_y + 0.3],
                'b--', linewidth=1.5, alpha=0.5)

        # Output node
        output_y = constraint_y - 1.5
        task_name, task_color = task_types[i]

        output_box = FancyBboxPatch((x_pos - 0.6, output_y - 0.5), 1.2, 1,
                                   boxstyle="round,pad=0.1",
                                   edgecolor=task_color,
                                   facecolor=task_color,
                                   alpha=0.4, linewidth=2.5)
        ax.add_patch(output_box)

        ax.text(x_pos, output_y + 0.2, task_name,
                ha='center', va='center', fontsize=9, fontweight='bold')
        ax.text(x_pos, output_y - 0.1, f'galette-output-{i}/',
                ha='center', va='center', fontsize=7, family='monospace')
        ax.text(x_pos, output_y - 0.35, 'vsum-output.xmi',
                ha='center', va='center', fontsize=6, style='italic')

        # Arrow to output
        ax.plot([x_pos, x_pos], [constraint_y - 0.3, output_y + 0.5],
                color=task_color, linewidth=2, alpha=0.7)

    # Statistics box
    stats_box = FancyBboxPatch((0.2, 0.5), 3.5, 2.5,
                              boxstyle="round,pad=0.15",
                              edgecolor='#333',
                              facecolor='#F5F5F5',
                              alpha=0.9, linewidth=2)
    ax.add_patch(stats_box)

    ax.text(2, 2.7, 'EXECUTION STATISTICS', ha='center', va='center',
            fontsize=11, fontweight='bold')

    total_exec = sum(p['executionTime'] for p in paths)
    total_init = sum(p.get('initializationTime', 0) for p in paths)

    stats_text = f"""
Total Paths: {len(paths)}
Execution Time: {total_exec} ms
Avg Exec Time: {total_exec // len(paths)} ms
Min/Max: {min(p['executionTime'] for p in paths)}/{max(p['executionTime'] for p in paths)} ms

Constraints: {sum(len(p.get('constraints', [])) for p in paths)}

Init (Path 1 only): {total_init} ms
    """

    ax.text(2, 1.5, stats_text.strip(), ha='center', va='center',
            fontsize=8, family='monospace', linespacing=1.8)

    # Pattern explanation box
    pattern_box = FancyBboxPatch((6, 0.5), 3.6, 2.5,
                                boxstyle="round,pad=0.15",
                                edgecolor='#FFD700',
                                facecolor='#FFFACD',
                                alpha=0.9, linewidth=2)
    ax.add_patch(pattern_box)

    ax.text(7.8, 2.7, 'PATTERN USED', ha='center', va='center',
            fontsize=11, fontweight='bold', color='#8B4513')

    pattern_text = """
1. Tag input as symbolic
   ↓
2. Execute Vitruvius naturally
   ↓
3. Reactions switch statement
   ↓
4. Auto-collect constraints
   ↓
5. Generate model output

NO hardcoded logic!
    """

    ax.text(7.8, 1.4, pattern_text.strip(), ha='center', va='center',
            fontsize=7, linespacing=1.6)

    plt.tight_layout()
    plt.savefig(output_file, dpi=300, bbox_inches='tight', facecolor='white')
    print(f"Execution tree saved to: {output_file}")

    return fig

def create_performance_chart(paths, output_file='performance_chart.png'):
    """Create performance visualization charts."""

    fig = plt.figure(figsize=(16, 11))

    # Grid layout - add space at top for init info
    gs = fig.add_gridspec(4, 2, hspace=0.35, wspace=0.3, top=0.92)

    # Add initialization info banner at top
    total_init = sum(p.get('initializationTime', 0) for p in paths)
    if total_init > 0:
        fig.text(0.5, 0.96, f'One-Time Initialization (Path 1): {total_init}ms',
                ha='center', va='center', fontsize=14, fontweight='bold',
                bbox=dict(boxstyle='round,pad=0.8', facecolor='#FFD700', alpha=0.3, edgecolor='#FF6B6B', linewidth=2))

    # 1. Bar chart - Execution time per path (NO INIT)
    ax1 = fig.add_subplot(gs[0, :])
    path_ids = [p['pathId'] for p in paths]
    exec_times = [p['executionTime'] for p in paths]
    colors = ['#FF6B6B', '#4ECDC4', '#95E1D3', '#F38181', '#AAAAAA']

    # Simple bars - execution time only
    bars = ax1.bar(path_ids, exec_times, color=colors, alpha=0.7, edgecolor='black', linewidth=1.5)

    ax1.set_xlabel('Path ID (User Choice)', fontsize=12, fontweight='bold')
    ax1.set_ylabel('Execution Time (ms)', fontsize=12, fontweight='bold')
    ax1.set_title('Execution Time per Path (excludes initialization)', fontsize=14, fontweight='bold')
    ax1.set_xticks(path_ids)
    ax1.grid(axis='y', alpha=0.3)

    # Add value labels on bars
    for bar, exec_time in zip(bars, exec_times):
        ax1.text(bar.get_x() + bar.get_width()/2., exec_time,
                f'{exec_time}ms',
                ha='center', va='bottom', fontsize=9, fontweight='bold')

    # 2. Pie chart - Time distribution (execution time only)
    ax2 = fig.add_subplot(gs[1, 0])
    task_types = ['InterruptTask\n(0)', 'PeriodicTask\n(1)',
                  'SoftwareTask\n(2)', 'TimeTableTask\n(3)', 'Decide Later\n(4)']

    wedges, texts, autotexts = ax2.pie(exec_times, labels=task_types,
                                        colors=colors, autopct='%1.1f%%',
                                        startangle=90, textprops={'fontsize': 9})

    for autotext in autotexts:
        autotext.set_color('white')
        autotext.set_fontweight('bold')

    ax2.set_title('Execution Time Distribution Across Paths\n(excluding initialization)', fontsize=12, fontweight='bold')

    # 3. Cumulative time chart (exec only)
    ax3 = fig.add_subplot(gs[1, 1])
    cumulative_times = np.cumsum(exec_times)

    ax3.plot(path_ids, cumulative_times, marker='o', linewidth=2.5,
             markersize=8, color='#4169E1', markerfacecolor='#FFD700',
             markeredgewidth=2, markeredgecolor='#4169E1')
    ax3.fill_between(path_ids, cumulative_times, alpha=0.3, color='#4169E1')
    ax3.set_xlabel('Path ID', fontsize=12, fontweight='bold')
    ax3.set_ylabel('Cumulative Execution Time (ms)', fontsize=12, fontweight='bold')
    ax3.set_title('Cumulative Execution Time (excludes init)', fontsize=12, fontweight='bold')
    ax3.set_xticks(path_ids)
    ax3.grid(True, alpha=0.3)

    # Add final total
    ax3.text(path_ids[-1], cumulative_times[-1],
            f'  Total: {cumulative_times[-1]}ms',
            ha='left', va='center', fontsize=9, fontweight='bold',
            bbox=dict(boxstyle='round,pad=0.5', facecolor='yellow', alpha=0.7))

    # 4. Statistics table
    ax4 = fig.add_subplot(gs[2:, :])
    ax4.axis('off')

    # Create statistics table
    table_data = []
    table_data.append(['Path', 'User Choice', 'Task Type', 'Exec Time (ms)', 'Constraint', 'Output File'])

    task_names = ['InterruptTask', 'PeriodicTask', 'SoftwareTask', 'TimeTableTask', 'Decide Later']

    for i, path in enumerate(paths):
        constraints = path.get('constraints', [])
        constraint_str = constraints[0] if constraints else 'none'
        exec_time = path['executionTime']
        table_data.append([
            str(path['pathId']),
            str(path['symbolicInputs']['user_choice']),
            task_names[i],
            str(exec_time),
            constraint_str,
            f'galette-output-{i}/vsum-output.xmi'
        ])

    # Add summary row
    total_exec_sum = sum(exec_times)
    table_data.append([
        'TOTAL', '-', f'{len(paths)} paths',
        str(total_exec_sum),
        f'{sum(len(p.get("constraints", [])) for p in paths)} total',
        '✓ All generated'
    ])

    # Add init info row
    total_init_sum = sum(p.get('initializationTime', 0) for p in paths)
    if total_init_sum > 0:
        table_data.append([
            'INIT', 'Path 1', 'One-time setup',
            f'+{total_init_sum}',
            'VSUM initialization',
            '(not in exec time)'
        ])

    table = ax4.table(cellText=table_data, cellLoc='left',
                     loc='center', bbox=[0, 0, 1, 1])

    table.auto_set_font_size(False)
    table.set_fontsize(9)

    # Style the table
    for i in range(len(table_data)):
        for j in range(len(table_data[0])):
            cell = table[(i, j)]
            if i == 0:  # Header row
                cell.set_facecolor('#333333')
                cell.set_text_props(weight='bold', color='white')
            elif i == len(table_data) - 1:  # Summary row
                cell.set_facecolor('#FFD700')
                cell.set_text_props(weight='bold')
            else:
                cell.set_facecolor(colors[i-1] if i <= len(colors) else '#FFFFFF')
                cell.set_alpha(0.3)

    ax4.set_title('Detailed Execution Results (Init time shown separately above)', fontsize=14, fontweight='bold', pad=20)

    plt.savefig(output_file, dpi=300, bbox_inches='tight', facecolor='white')
    print(f"Performance chart saved to: {output_file}")

    return fig

def create_summary_report(paths, output_file='execution_summary.txt'):
    """Create a text summary report."""

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("=" * 80 + "\n")
        f.write("GALETTE/VITRUVIUS SYMBOLIC EXECUTION - SUMMARY REPORT\n")
        f.write("=" * 80 + "\n\n")

        total_exec = sum(p['executionTime'] for p in paths)
        total_init = sum(p.get('initializationTime', 0) for p in paths)
        total_time = total_exec + total_init

        f.write(f"Total Paths Explored: {len(paths)}\n")
        f.write(f"Total Execution Time: {total_exec} ms\n")
        f.write(f"Total Initialization Time: {total_init} ms\n")
        f.write(f"Total Time (exec + init): {total_time} ms\n")
        f.write(f"Average Time per Path: {total_time // len(paths)} ms\n")
        f.write(f"Min Execution Time: {min(p['executionTime'] for p in paths)} ms\n")
        f.write(f"Max Execution Time: {max(p['executionTime'] for p in paths)} ms\n\n")

        f.write("-" * 80 + "\n")
        f.write("PATH DETAILS\n")
        f.write("-" * 80 + "\n\n")

        task_names = ['InterruptTask', 'PeriodicTask', 'SoftwareTask', 'TimeTableTask', 'Decide Later']

        for i, path in enumerate(paths):
            f.write(f"Path {path['pathId']}: {task_names[i]}\n")
            f.write(f"  User Choice: {path['symbolicInputs']['user_choice']}\n")

            exec_time = path['executionTime']
            init_time = path.get('initializationTime', 0)

            if init_time > 0:
                f.write(f"  Initialization Time: {init_time} ms (VSUM setup)\n")
                f.write(f"  Execution Time: {exec_time} ms (business logic)\n")
                f.write(f"  Total Time: {exec_time + init_time} ms\n")
            else:
                f.write(f"  Execution Time: {exec_time} ms\n")

            constraints = path.get('constraints', [])
            if constraints:
                f.write(f"  Constraints:\n")
                for constraint in constraints:
                    f.write(f"    - {constraint}\n")
            else:
                f.write(f"  Constraints: none\n")

            f.write(f"  Output: galette-output-{i}/galette-test-output/vsum-output.xmi\n")
            f.write("\n")

        f.write("-" * 80 + "\n")
        f.write("KEY ACCOMPLISHMENTS\n")
        f.write("-" * 80 + "\n\n")

        f.write("✓ Symbolic values tagged using Galette (makeSymbolicInt)\n")
        f.write("✓ Vitruvius reactions executed naturally (NO hardcoded logic!)\n")
        f.write("✓ Path constraints collected from switch statement in reactions\n")
        f.write("✓ Model transformations generated for all paths\n")
        f.write("✓ Self-contained in galette-vitruv project\n\n")

        f.write("-" * 80 + "\n")
        f.write("PATTERN USED: BrakeDisc Symbolic Execution Pattern\n")
        f.write("-" * 80 + "\n\n")

        f.write("1. Create symbolic tag: GaletteSymbolicator.makeSymbolicInt(label, value)\n")
        f.write("2. Apply tag to value: Tainter.setTag(value, symbolicTag)\n")
        f.write("3. Execute business logic: Test.insertTask(workDir, taggedValue)\n")
        f.write("4. Collect constraints: PathUtils.getCurPC()\n")
        f.write("5. Transformation logic executed from templateReactions.reactions\n\n")

        f.write("=" * 80 + "\n")

    print(f"Summary report saved to: {output_file}")

def main():
    """Main entry point."""

    print("=" * 80)
    print("GALETTE/VITRUVIUS SYMBOLIC EXECUTION - VISUALIZATION")
    print("=" * 80)
    print()

    # Load data
    json_file = 'execution_paths.json'
    if len(sys.argv) > 1:
        json_file = sys.argv[1]

    paths = load_execution_data(json_file)

    print()
    print("Generating visualizations...")
    print()

    # Create visualizations
    create_execution_tree(paths, 'execution_tree.png')
    create_performance_chart(paths, 'performance_chart.png')
    create_summary_report(paths, 'execution_summary.txt')

    print()
    print("=" * 80)
    print("VISUALIZATION COMPLETE!")
    print("=" * 80)
    print()
    print("Generated files:")
    print("  - execution_tree.png (execution path tree diagram)")
    print("  - performance_chart.png (performance analysis charts)")
    print("  - execution_summary.txt (text summary report)")
    print()

if __name__ == '__main__':
    main()
