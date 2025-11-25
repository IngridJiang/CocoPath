#!/usr/bin/env python3
"""
Switch between internal and external Vitruvius dependencies in pom.xml
"""
import sys
import re

def switch_to_external(pom_content):
    """Switch from internal to external dependency"""
    # Check if already in external mode (external dep uncommented, internal dep commented)
    external_active = re.search(
        r'<!-- Option 2.*?-->\s*\n\s*<dependency>\s*\n\s*<groupId>tools\.vitruv</groupId>\s*\n\s*<artifactId>tools\.vitruv\.methodologisttemplate\.vsum</artifactId>',
        pom_content, flags=re.DOTALL)
    internal_commented = re.search(
        r'<!-- Option 1.*?-->\s*\n\s*<!--\s*\n\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>',
        pom_content, flags=re.DOTALL)

    if external_active and internal_commented:
        print("Already in external mode")
        return pom_content

    # Comment out internal dependency block (if not already commented)
    internal_pattern = r'(<!-- Option 1:.*?-->\s*)\n(\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n\s*<artifactId>amathea-acset-vsum</artifactId>\s*\n\s*<version>\$\{project\.version\}</version>\s*\n\s*</dependency>)'
    internal_replacement = r'\1\n        <!--\2\n        -->'
    pom_content = re.sub(internal_pattern, internal_replacement, pom_content, flags=re.DOTALL)

    # Uncomment external dependency block
    # The block looks like:
    #   <!-- Option 2... -->
    #   <!-- comment... -->
    #   <!--        <dependency>...tools.vitruv...vsum...</dependency>
    #   -->
    external_pattern = r'<!-- Option 2[^>]*-->\s*\n\s*<!-- This provides[^>]*-->\s*\n\s*<!--\s*(<dependency>.*?tools\.vitruv.*?methodologisttemplate\.vsum.*?</dependency>)\s*\n\s*-->'
    external_replacement = r'<!-- Option 2 (CURRENT): Use external Amathea-acset repository (FULL VITRUVIUS) -->\n        <!-- This provides complete Vitruvius reactions and transformations -->\n        \1'
    pom_content = re.sub(external_pattern, external_replacement, pom_content, flags=re.DOTALL)

    return pom_content

def switch_to_internal(pom_content):
    """Switch from external to internal dependency"""
    # Uncomment internal dependency block
    internal_pattern = r'<!-- Option 1:[^>]*-->\s*\n\s*<!-- This requires[^>]*-->\s*\n\s*<!--\s*(<dependency>.*?edu\.neu\.ccs\.prl\.galette.*?amathea-acset-vsum.*?</dependency>)\s*\n\s*-->'
    internal_replacement = r'<!-- Option 1: Use internal amathea-acset-integration module (SIMPLIFIED STUB) -->\n        <!-- This requires external Amathea-acset to be built once for Vitruvius dependencies -->\n        \1'
    pom_content = re.sub(internal_pattern, internal_replacement, pom_content, flags=re.DOTALL)

    # Comment out external dependency block
    external_pattern = r'<!-- Option 2[^>]*-->\s*\n\s*<!-- This provides[^>]*-->\s*\n\s*(<dependency>.*?tools\.vitruv.*?methodologisttemplate\.vsum.*?</dependency>)'
    external_replacement = r'<!-- Option 2 (CURRENT): Use external Amathea-acset repository (FULL VITRUVIUS) -->\n        <!-- This provides complete Vitruvius reactions and transformations -->\n        <!--        \1\n        -->'
    pom_content = re.sub(external_pattern, external_replacement, pom_content, flags=re.DOTALL)

    return pom_content

def main():
    if len(sys.argv) != 3:
        print("Usage: python switch-dependency.py <internal|external> <pom.xml>")
        sys.exit(1)

    mode = sys.argv[1]
    pom_file = sys.argv[2]

    if mode not in ['internal', 'external']:
        print("ERROR: Mode must be 'internal' or 'external'")
        sys.exit(1)

    # Read pom.xml
    with open(pom_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Backup
    with open(pom_file + '.bak', 'w', encoding='utf-8') as f:
        f.write(content)

    # Switch
    if mode == 'external':
        new_content = switch_to_external(content)
    else:
        new_content = switch_to_internal(content)

    # Write back
    with open(pom_file, 'w', encoding='utf-8') as f:
        f.write(new_content)

    print(f"Switched to {mode} mode")

if __name__ == '__main__':
    main()
