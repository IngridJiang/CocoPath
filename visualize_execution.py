#!/usr/bin/env python3
"""
Visualization script for Galette/Knarr symbolic execution results.

Generates:
1. Execution tree diagram showing all explored paths
2. Performance comparison chart
3. Constraint analysis summary

Usage:
    python visualize_execution.py <json_file>
    python visualize_execution.py execution_paths.json
"""

import json
import sys
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch
import networkx as nx
from typing import List, Dict, Any
import os

# Color scheme
COLORS = {
    'root': '#4A90E2',
    'path': '#7ED321',
    'constraint': '#F5A623',
    'output': '#BD10E0',
    'background': '#F8F9FA',
    'text': '#2C3E50'
}

def load_execution_data(json_file: str) -> List[Dict[str, Any]]:
    """Load execution paths from JSON file."""
    if not os.path.exists(json_file):
        print(f"Error: File '{json_file}' not found")
        sys.exit(1)

    with open(json_file, 'r') as f:
        return json.load(f)

def create_execution_tree(paths: List[Dict[str, Any]], output_file: str = 'execution_tree.png'):
    """Create execution tree visualization."""
    fig, ax = plt.subplots(figsize=(14, 10))
    fig.patch.set_facecolor(COLORS['background'])
    ax.set_facecolor(COLORS['background'])

    # Create graph
    G = nx.DiGraph()

    # Add root node
    G.add_node('root', label='Start\nSymbolic Execution', color=COLORS['root'])

    # Add path nodes
    for path in paths:
        path_id = path.get('pathId', 0)
        inputs = path.get('symbolicInputs', {})
        constraints = path.get('constraints', [])
        exec_time = path.get('executionTime', 0)

        # Create path node
        path_node = f'path_{path_id}'
        input_str = '\n'.join([f'{k}={v}' for k, v in inputs.items()])

        G.add_node(path_node,
                   label=f'Path {path_id}\n{input_str}\n{exec_time}ms',
                   color=COLORS['path'])

        G.add_edge('root', path_node)

        # Add constraint nodes
        if constraints:
            for i, constraint in enumerate(constraints[:3]):  # Show first 3 constraints
                constraint_node = f'constraint_{path_id}_{i}'
                constraint_text = str(constraint)
                if len(constraint_text) > 30:
                    constraint_text = constraint_text[:30] + '...'

                G.add_node(constraint_node,
                          label=constraint_text,
                          color=COLORS['constraint'])

                G.add_edge(path_node, constraint_node)

    # Layout
    pos = nx.spring_layout(G, k=2, iterations=50)

    # Draw nodes
    for node in G.nodes():
        node_color = G.nodes[node].get('color', COLORS['path'])
        node_label = G.nodes[node].get('label', node)

        # Draw node
        circle = mpatches.Circle(pos[node], 0.05, color=node_color, zorder=2)
        ax.add_patch(circle)

        # Draw label
        ax.text(pos[node][0], pos[node][1] + 0.08, node_label,
                ha='center', va='bottom', fontsize=8,
                bbox=dict(boxstyle='round,pad=0.3', facecolor='white', alpha=0.8))

    # Draw edges
    for edge in G.edges():
        start, end = edge
        x = [pos[start][0], pos[end][0]]
        y = [pos[start][1], pos[end][1]]
        ax.plot(x, y, 'k-', alpha=0.3, zorder=1)

    # Remove axes
    ax.axis('off')

    # Title
    plt.title('Symbolic Execution Tree\nGalette/Knarr with Vitruvius',
              fontsize=16, fontweight='bold', color=COLORS['text'], pad=20)

    # Legend
    legend_elements = [
        mpatches.Patch(color=COLORS['root'], label='Start'),
        mpatches.Patch(color=COLORS['path'], label='Execution Path'),
        mpatches.Patch(color=COLORS['constraint'], label='Path Constraint')
    ]
    ax.legend(handles=legend_elements, loc='upper right', framealpha=0.9)

    plt.tight_layout()
    plt.savefig(output_file, dpi=300, bbox_inches='tight', facecolor=COLORS['background'])
    print(f'✓ Execution tree saved to {output_file}')
    plt.close()

def create_performance_chart(paths: List[Dict[str, Any]], output_file: str = 'performance_chart.png'):
    """Create performance comparison chart."""
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 6))
    fig.patch.set_facecolor(COLORS['background'])

    # Extract data
    path_ids = [p.get('pathId', i) for i, p in enumerate(paths)]
    exec_times = [p.get('executionTime', 0) for p in paths]
    constraint_counts = [len(p.get('constraints', [])) for p in paths]

    # Plot 1: Execution time
    ax1.set_facecolor(COLORS['background'])
    bars1 = ax1.bar(path_ids, exec_times, color=COLORS['path'], alpha=0.7)
    ax1.set_xlabel('Path ID', fontsize=12, fontweight='bold')
    ax1.set_ylabel('Execution Time (ms)', fontsize=12, fontweight='bold')
    ax1.set_title('Execution Time per Path', fontsize=14, fontweight='bold')
    ax1.grid(True, alpha=0.3)

    # Add value labels on bars
    for bar in bars1:
        height = bar.get_height()
        ax1.text(bar.get_x() + bar.get_width()/2., height,
                f'{int(height)}ms',
                ha='center', va='bottom', fontsize=9)

    # Plot 2: Constraint count
    ax2.set_facecolor(COLORS['background'])
    bars2 = ax2.bar(path_ids, constraint_counts, color=COLORS['constraint'], alpha=0.7)
    ax2.set_xlabel('Path ID', fontsize=12, fontweight='bold')
    ax2.set_ylabel('Number of Constraints', fontsize=12, fontweight='bold')
    ax2.set_title('Path Constraints Collected', fontsize=14, fontweight='bold')
    ax2.grid(True, alpha=0.3)

    # Add value labels on bars
    for bar in bars2:
        height = bar.get_height()
        ax2.text(bar.get_x() + bar.get_width()/2., height,
                f'{int(height)}',
                ha='center', va='bottom', fontsize=9)

    plt.suptitle('Symbolic Execution Performance Analysis',
                 fontsize=16, fontweight='bold', y=1.02)

    plt.tight_layout()
    plt.savefig(output_file, dpi=300, bbox_inches='tight', facecolor=COLORS['background'])
    print(f'✓ Performance chart saved to {output_file}')
    plt.close()

def create_summary_report(paths: List[Dict[str, Any]], output_file: str = 'execution_summary.txt'):
    """Create text summary report."""
    with open(output_file, 'w') as f:
        f.write("=" * 80 + "\n")
        f.write("SYMBOLIC EXECUTION SUMMARY REPORT\n")
        f.write("Galette/Knarr with Vitruvius Integration\n")
        f.write("=" * 80 + "\n\n")

        f.write(f"Total paths explored: {len(paths)}\n\n")

        for path in paths:
            path_id = path.get('pathId', 0)
            inputs = path.get('symbolicInputs', {})
            constraints = path.get('constraints', [])
            exec_time = path.get('executionTime', 0)

            f.write("-" * 80 + "\n")
            f.write(f"Path {path_id}\n")
            f.write("-" * 80 + "\n")

            f.write("Symbolic Inputs:\n")
            for key, value in inputs.items():
                f.write(f"  • {key} = {value}\n")

            f.write(f"\nExecution Time: {exec_time} ms\n")

            f.write(f"\nPath Constraints ({len(constraints)}):\n")
            for i, constraint in enumerate(constraints, 1):
                f.write(f"  {i}. {constraint}\n")

            f.write("\n")

        # Statistics
        f.write("=" * 80 + "\n")
        f.write("STATISTICS\n")
        f.write("=" * 80 + "\n\n")

        exec_times = [p.get('executionTime', 0) for p in paths]
        constraint_counts = [len(p.get('constraints', [])) for p in paths]

        f.write(f"Execution Time:\n")
        f.write(f"  • Average: {sum(exec_times) / len(exec_times):.1f} ms\n")
        f.write(f"  • Min: {min(exec_times)} ms\n")
        f.write(f"  • Max: {max(exec_times)} ms\n\n")

        f.write(f"Path Constraints:\n")
        f.write(f"  • Average: {sum(constraint_counts) / len(constraint_counts):.1f} per path\n")
        f.write(f"  • Total: {sum(constraint_counts)}\n\n")

    print(f'✓ Summary report saved to {output_file}')

def main():
    if len(sys.argv) < 2:
        print("Usage: python visualize_execution.py <json_file>")
        print("Example: python visualize_execution.py execution_paths.json")
        sys.exit(1)

    json_file = sys.argv[1]

    print(f"\n{'='*80}")
    print("SYMBOLIC EXECUTION VISUALIZATION")
    print(f"{'='*80}\n")

    print(f"Loading execution data from {json_file}...")
    paths = load_execution_data(json_file)
    print(f"✓ Loaded {len(paths)} execution paths\n")

    print("Generating visualizations...")
    create_execution_tree(paths)
    create_performance_chart(paths)
    create_summary_report(paths)

    print(f"\n{'='*80}")
    print("VISUALIZATION COMPLETE")
    print(f"{'='*80}\n")

    print("Generated files:")
    print("  • execution_tree.png - Execution tree diagram")
    print("  • performance_chart.png - Performance analysis")
    print("  • execution_summary.txt - Text report\n")

    print("To view:")
    print("  xdg-open execution_tree.png")
    print("  xdg-open performance_chart.png")
    print("  cat execution_summary.txt\n")

if __name__ == '__main__':
    main()
