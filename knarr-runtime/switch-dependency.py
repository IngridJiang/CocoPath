#!/usr/bin/env python3
"""
Switch between Vitruvius integration dependencies in knarr-runtime/pom.xml.

Modes:
  internal  - Use amalthea-acset-integration module (internal stub, Options 1+2 active)
  external  - Use external Amalthea-acset artifact (Option 2 active only)
  brake     - Use tinybrake-integration module (Option 3 only, Options 1+2 commented out)
"""
import sys
import re


def switch_to_external(pom_content):
    """Switch to external Amalthea-acset dependency (Option 2 active)."""
    # Check if already in external mode
    external_active = re.search(
        r'<!-- Option 2.*?-->\s*\n\s*<!-- This provides[^>]*-->\s*\n\s*<dependency>\s*\n\s*<groupId>tools\.vitruv</groupId>\s*\n\s*<artifactId>tools\.vitruv\.methodologisttemplate\.vsum</artifactId>',
        pom_content, flags=re.DOTALL)
    internal_commented = re.search(
        r'<!-- Option 1.*?-->\s*\n\s*<!-- This requires[^>]*-->\s*\n\s*<!--\s*\n\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>',
        pom_content, flags=re.DOTALL)

    if external_active and internal_commented:
        print("Already in external mode")
        return pom_content

    # Comment out internal dependency block (if not already commented)
    internal_pattern = r'(<!-- Option 1:.*?-->\s*)\n(\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n\s*<artifactId>amalthea-acset-vsum</artifactId>\s*\n\s*<version>\$\{project\.version\}</version>\s*\n\s*</dependency>)'
    internal_replacement = r'\1\n        <!--\2\n        -->'
    pom_content = re.sub(internal_pattern, internal_replacement, pom_content, flags=re.DOTALL)

    # Uncomment external dependency block
    external_pattern = r'<!-- Option 2[^>]*-->\s*\n\s*<!-- This provides[^>]*-->\s*\n\s*<!--\s*(<dependency>.*?tools\.vitruv.*?methodologisttemplate\.vsum.*?</dependency>)\s*\n\s*-->'
    external_replacement = r'<!-- Option 2 (CURRENT): Use external Amathea-acset repository (FULL VITRUVIUS) -->\n        <!-- This provides complete Vitruvius reactions and transformations -->\n        \1'
    pom_content = re.sub(external_pattern, external_replacement, pom_content, flags=re.DOTALL)

    # Ensure Option 3 (brake) stays commented out
    pom_content = _ensure_brake_commented(pom_content)

    return pom_content


def switch_to_internal(pom_content):
    """Switch to internal amalthea-acset-integration dependency (Options 1+2 active)."""
    # Uncomment internal dependency block
    internal_pattern = r'<!-- Option 1:[^>]*-->\s*\n\s*<!-- This requires[^>]*-->\s*\n\s*<!--\s*(<dependency>.*?edu\.neu\.ccs\.prl\.galette.*?amalthea-acset-vsum.*?</dependency>)\s*\n\s*-->'
    internal_replacement = r'<!-- Option 1: Use internal amalthea-acset-integration module (SIMPLIFIED STUB) -->\n        <!-- This requires external Amalthea-acset to be built once for Vitruvius dependencies -->\n        \1'
    pom_content = re.sub(internal_pattern, internal_replacement, pom_content, flags=re.DOTALL)

    # Comment out external dependency block
    external_pattern = r'<!-- Option 2[^>]*-->\s*\n\s*<!-- This provides[^>]*-->\s*\n\s*(<dependency>.*?tools\.vitruv.*?methodologisttemplate\.vsum.*?</dependency>)'
    external_replacement = r'<!-- Option 2 (CURRENT): Use external Amathea-acset repository (FULL VITRUVIUS) -->\n        <!-- This provides complete Vitruvius reactions and transformations -->\n        <!--        \1\n        -->'
    pom_content = re.sub(external_pattern, external_replacement, pom_content, flags=re.DOTALL)

    # Ensure Option 3 (brake) stays commented out
    pom_content = _ensure_brake_commented(pom_content)

    return pom_content


def switch_to_brake(pom_content):
    """Switch to tinybrake-integration dependency (Option 3 only).

    Comments out Options 1+2 (amalthea/external), uncomments Option 3 (brake).
    Both tinybrake-integration-consistency and tinybrake-integration-vsum are activated.
    """
    # --- Step 1: Comment out Option 1 (amalthea-acset-consistency + amalthea-acset-vsum) ---
    # Pattern: find both amalthea Option-1 deps between the Option 1 comment markers and wrap them
    option1_both_active = re.search(
        r'<!-- Option 1:.*?-->\s*\n\s*<!-- This requires.*?-->\s*\n(\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n\s*<artifactId>amalthea-acset-consistency</artifactId>.*?</dependency>\s*\n\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n\s*<artifactId>amalthea-acset-vsum</artifactId>.*?</dependency>)',
        pom_content, flags=re.DOTALL)

    if option1_both_active:
        option1_pattern = (
            r'(<!-- Option 1:.*?-->\s*\n\s*<!-- This requires.*?-->)\s*\n'
            r'(\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n'
            r'\s*<artifactId>amalthea-acset-consistency</artifactId>.*?</dependency>\s*\n'
            r'\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n'
            r'\s*<artifactId>amalthea-acset-vsum</artifactId>.*?</dependency>)'
        )
        option1_replacement = r'\1\n        <!--\n\2\n        -->'
        pom_content = re.sub(option1_pattern, option1_replacement, pom_content, flags=re.DOTALL)

    # --- Step 2: Comment out Option 2 (tools.vitruv.methodologisttemplate.vsum) ---
    option2_active = re.search(
        r'<!-- Option 2.*?-->\s*\n\s*<!-- This provides.*?-->\s*\n\s*<dependency>\s*\n\s*<groupId>tools\.vitruv</groupId>\s*\n\s*<artifactId>tools\.vitruv\.methodologisttemplate\.vsum</artifactId>',
        pom_content, flags=re.DOTALL)

    if option2_active:
        option2_pattern = (
            r'(<!-- Option 2[^>]*-->\s*\n\s*<!-- This provides[^>]*-->)\s*\n'
            r'(\s*<dependency>\s*\n\s*<groupId>tools\.vitruv</groupId>\s*\n'
            r'\s*<artifactId>tools\.vitruv\.methodologisttemplate\.vsum</artifactId>.*?</dependency>)'
        )
        option2_replacement = r'\1\n        <!--\n\2\n        -->'
        pom_content = re.sub(option2_pattern, option2_replacement, pom_content, flags=re.DOTALL)

    # --- Step 3: Uncomment Option 3 (tinybrake-integration-consistency + tinybrake-integration-vsum) ---
    option3_commented = re.search(
        r'<!-- Option 3:.*?-->\s*\n.*?<!--\s*\n\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n\s*<artifactId>tinybrake-integration-consistency</artifactId>',
        pom_content, flags=re.DOTALL)

    if option3_commented:
        option3_uncomment_pattern = (
            r'(<!-- Option 3:.*?-->\s*\n(?:.*?-->\s*\n)*?)'
            r'\s*<!--\s*\n'
            r'(\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n'
            r'\s*<artifactId>tinybrake-integration-consistency</artifactId>.*?</dependency>\s*\n'
            r'\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n'
            r'\s*<artifactId>tinybrake-integration-vsum</artifactId>.*?</dependency>)\s*\n'
            r'\s*-->'
        )
        option3_uncomment_replacement = r'\1\n\2'
        pom_content = re.sub(
            option3_uncomment_pattern, option3_uncomment_replacement, pom_content, flags=re.DOTALL)
    else:
        print("WARNING: Option 3 (tinybrake) already active or not found in expected commented form")

    return pom_content


def _ensure_brake_commented(pom_content):
    """Ensure Option 3 (tinybrake) deps are commented out (for internal/external modes)."""
    # Check if tinybrake deps are currently uncommented
    brake_active = re.search(
        r'<!-- Option 3:.*?-->\s*\n(?:(?!<!--).)*?<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n\s*<artifactId>tinybrake-integration-consistency</artifactId>',
        pom_content, flags=re.DOTALL)

    if brake_active:
        option3_comment_pattern = (
            r'(<!-- Option 3:.*?-->\s*\n(?:.*?-->\s*\n)*?)'
            r'(\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n'
            r'\s*<artifactId>tinybrake-integration-consistency</artifactId>.*?</dependency>\s*\n'
            r'\s*<dependency>\s*\n\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*\n'
            r'\s*<artifactId>tinybrake-integration-vsum</artifactId>.*?</dependency>)'
        )
        option3_comment_replacement = r'\1\n        <!--\n\2\n        -->'
        pom_content = re.sub(
            option3_comment_pattern, option3_comment_replacement, pom_content, flags=re.DOTALL)

    return pom_content


def main():
    if len(sys.argv) != 3:
        print("Usage: python switch-dependency.py <internal|external|brake> <pom.xml>")
        sys.exit(1)

    mode = sys.argv[1]
    pom_file = sys.argv[2]

    if mode not in ['internal', 'external', 'brake']:
        print("ERROR: Mode must be 'internal', 'external', or 'brake'")
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
    elif mode == 'internal':
        new_content = switch_to_internal(content)
    else:  # brake
        new_content = switch_to_brake(content)

    # Write back
    with open(pom_file, 'w', encoding='utf-8') as f:
        f.write(new_content)

    print(f"Switched to {mode} mode")


if __name__ == '__main__':
    main()
