# ============================================================================
# Galette-Knarr Symbolic Execution Runner
# ============================================================================
# This script runs automatic path exploration for Vitruvius model transformations.
# It uses the PathExplorer API to automatically generate test inputs by:
#   1. Executing transformations with concrete values
#   2. Collecting path constraints
#   3. Negating constraints to find unexplored paths
#   4. Solving for new inputs automatically
#
# Usage:
#   .\run-symbolic-execution.ps1                    # Interactive mode (prompts for choice)
#   .\run-symbolic-execution.ps1 -Internal          # Fast mode (2-5ms/path, simplified)
#   .\run-symbolic-execution.ps1 -UseExternal       # Full mode (26-45ms/path, complete Vitruvius)
# ============================================================================

param(
    [switch]$UseExternal = $false,
    [switch]$Internal = $false,
    [string]$ExternalPath = "C:\Users\10239\Amathea-acset"
)

$INTERACTIVE_MODE = (-not $UseExternal) -and (-not $Internal)

Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host "GALETTE/KNARR SYMBOLIC EXECUTION WITH VITRUVIUS FRAMEWORK" -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host ""

# Interactive mode selection if no flag provided
if ($INTERACTIVE_MODE) {
    Write-Host "Please select execution mode:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  1) INTERNAL MODE (Fast, simplified stub)" -ForegroundColor Green
    Write-Host "     - Execution time: ~2-5ms per path" -ForegroundColor Gray
    Write-Host "     - Output: Basic XMI stubs" -ForegroundColor Gray
    Write-Host "     - No external repository needed" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  2) EXTERNAL MODE (Full Vitruvius transformations)" -ForegroundColor Yellow
    Write-Host "     - Execution time: ~26-45ms per path" -ForegroundColor Gray
    Write-Host "     - Output: Complete Vitruvius reactions & transformations" -ForegroundColor Gray
    Write-Host "     - Requires external Amathea-acset repository" -ForegroundColor Gray
    Write-Host ""
    $choice = Read-Host "Enter your choice (1 or 2)"
    Write-Host ""

    switch ($choice) {
        "1" {
            $UseExternal = $false
            Write-Host "Selected: INTERNAL MODE" -ForegroundColor Green
        }
        "2" {
            $UseExternal = $true
            Write-Host "Selected: EXTERNAL MODE" -ForegroundColor Yellow
        }
        default {
            Write-Host "Invalid choice. Defaulting to INTERNAL MODE." -ForegroundColor Yellow
            $UseExternal = $false
        }
    }
    Write-Host ""
}

Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host ""

# Set working directory to script location
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

if ($UseExternal) {
    Write-Host "Mode: EXTERNAL (switching to external Amathea-acset)" -ForegroundColor Yellow
    Write-Host ""

    # Verify external path exists
    if (-not (Test-Path $ExternalPath)) {
        Write-Host "ERROR: External Amathea-acset not found at: $ExternalPath" -ForegroundColor Red
        Write-Host "Please check the path" -ForegroundColor Red
        exit 1
    }

    Write-Host "[1/4] Building external Amathea-acset at $ExternalPath..." -ForegroundColor Yellow
    Push-Location $ExternalPath
    mvn clean install -DskipTests -Dcheckstyle.skip=true
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Failed to build external Amathea-acset" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
    Write-Host "      Done." -ForegroundColor Green
    Write-Host ""

    Write-Host "[2/4] Temporarily switching to external dependency..." -ForegroundColor Yellow
    $pomPath = Join-Path $scriptDir "pom.xml"
    $pomContent = Get-Content $pomPath -Raw

    # Swap: comment out internal, uncomment external
    $pomContent = $pomContent -replace '(<dependency>\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*<artifactId>amathea-acset-vsum</artifactId>.*?</dependency>)', '<!-- $1 -->'
    $pomContent = $pomContent -replace '<!--\s*(<dependency>\s*<groupId>tools\.vitruv</groupId>\s*<artifactId>tools\.vitruv\.methodologisttemplate\.vsum</artifactId>.*?</dependency>)\s*-->', '$1'
    Set-Content $pomPath $pomContent
    Write-Host "      Switched to external dependency." -ForegroundColor Green
    Write-Host ""

    $stepOffset = 2
} else {
    Write-Host "Mode: INTERNAL (using amathea-acset-integration module)" -ForegroundColor Green
    Write-Host "      Note: Requires external Amathea-acset built once for Vitruvius dependencies" -ForegroundColor Gray
    Write-Host ""

    # Check if Vitruvius dependencies are available
    if (-not (Test-Path "$env:USERPROFILE\.m2\repository\tools\vitruv\tools.vitruv.methodologisttemplate.vsum")) {
        Write-Host "WARNING: Vitruvius VSUM dependency not found in Maven repository" -ForegroundColor Yellow
        Write-Host "         Building external Amathea-acset to install it..." -ForegroundColor Yellow
        Write-Host ""

        if (Test-Path $ExternalPath) {
            Push-Location $ExternalPath
            mvn clean install -DskipTests -Dcheckstyle.skip=true
            if ($LASTEXITCODE -ne 0) {
                Write-Host "ERROR: Failed to build external Amathea-acset" -ForegroundColor Red
                Pop-Location
                exit 1
            }
            Pop-Location
            Write-Host "      Done. Vitruvius dependencies installed." -ForegroundColor Green
            Write-Host ""
        } else {
            Write-Host "ERROR: External Amathea-acset not found at: $ExternalPath" -ForegroundColor Red
            Write-Host "       Please build it first or specify path with -ExternalPath" -ForegroundColor Red
            exit 1
        }
    }

    Write-Host "[1/3] Building internal amathea-acset-integration..." -ForegroundColor Yellow
    Push-Location (Join-Path (Split-Path $scriptDir -Parent) "amathea-acset-integration")
    mvn clean install -DskipTests -Dcheckstyle.skip=true
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Failed to build internal amathea-acset-integration" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
    Write-Host "      Done." -ForegroundColor Green
    Write-Host ""

    $stepOffset = 0
}

$step1 = 2 + $stepOffset
$step2 = 3 + $stepOffset
$totalSteps = 3 + $stepOffset

Write-Host "[$step1/$totalSteps] Cleaning previous outputs..." -ForegroundColor Yellow
if (Test-Path "galette-output-*") {
    Remove-Item -Recurse -Force "galette-output-*"
}
if (Test-Path "execution_paths.json") {
    Remove-Item "execution_paths.json"
}
Write-Host "      Done." -ForegroundColor Green
Write-Host ""

Write-Host "[$step2/$totalSteps] Running symbolic execution..." -ForegroundColor Yellow
$mvnSuccess = $true
try {
    mvn exec:java "-Dcheckstyle.skip=true"
    if ($LASTEXITCODE -ne 0) {
        $mvnSuccess = $false
        Write-Host ""
        Write-Host "WARNING: Maven execution had errors" -ForegroundColor Yellow
    }
} catch {
    $mvnSuccess = $false
    Write-Host ""
    Write-Host "WARNING: Maven execution failed" -ForegroundColor Yellow
}

# Restore internal dependency if we switched to external
if ($UseExternal) {
    Write-Host ""
    Write-Host "Restoring internal dependency configuration..." -ForegroundColor Yellow
    $pomPath = Join-Path $scriptDir "pom.xml"
    $pomContent = Get-Content $pomPath -Raw

    # Restore: uncomment internal, comment out external
    $pomContent = $pomContent -replace '<!-- (<dependency>\s*<groupId>edu\.neu\.ccs\.prl\.galette</groupId>\s*<artifactId>amathea-acset-vsum</artifactId>.*?</dependency>) -->', '$1'
    $pomContent = $pomContent -replace '(<dependency>\s*<groupId>tools\.vitruv</groupId>\s*<artifactId>tools\.vitruv\.methodologisttemplate\.vsum</artifactId>.*?</dependency>)', '<!-- $1 -->'
    Set-Content $pomPath $pomContent
    Write-Host "      Done." -ForegroundColor Green
}

Write-Host ""
Write-Host "[OPTIONAL] Generating visualizations..." -ForegroundColor Yellow

# Always try to generate visualizations if execution_paths.json exists
if (Test-Path "execution_paths.json") {
    $pythonCmd = $null
    if (Get-Command python -ErrorAction SilentlyContinue) {
        $pythonCmd = "python"
    } elseif (Get-Command python3 -ErrorAction SilentlyContinue) {
        $pythonCmd = "python3"
    }

    if ($pythonCmd) {
        Write-Host "  Running visualize_results.py..." -ForegroundColor Gray
        & $pythonCmd visualize_results.py
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✓ Results visualizations generated" -ForegroundColor Green
        } else {
            Write-Host "  ✗ Failed to generate results visualizations (see errors above)" -ForegroundColor Yellow
        }

        Write-Host "  Running visualize_workflow.py..." -ForegroundColor Gray
        & $pythonCmd visualize_workflow.py
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✓ Workflow diagram generated" -ForegroundColor Green
        } else {
            Write-Host "  ✗ Failed to generate workflow diagram (see errors above)" -ForegroundColor Yellow
        }
    } else {
        Write-Host "  ✗ Python not found - skipping visualizations" -ForegroundColor Yellow
    }
} else {
    Write-Host "  ✗ No execution_paths.json found - skipping visualizations" -ForegroundColor Red
    if (-not $mvnSuccess) {
        Write-Host ""
        Write-Host "ERROR: Symbolic execution failed!" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "================================================================================" -ForegroundColor Green
Write-Host "SUCCESS! Symbolic execution completed." -ForegroundColor Green
Write-Host "================================================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Generated files:" -ForegroundColor Cyan
Write-Host "  - execution_paths.json       (JSON data)" -ForegroundColor White
Write-Host "  - execution_summary.txt      (Text summary)" -ForegroundColor White
Write-Host "  - execution_tree.png         (Execution tree diagram)" -ForegroundColor White
Write-Host "  - performance_chart.png      (Performance charts)" -ForegroundColor White
Write-Host "  - workflow_diagram.png       (Workflow diagram)" -ForegroundColor White
Write-Host "  - galette-output-0/ to galette-output-4/ (Model outputs)" -ForegroundColor White
Write-Host ""
