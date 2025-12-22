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
#   .\run-symbolic-execution.ps1 -Internal          # Single-variable mode (5 paths, simplified)
#   .\run-symbolic-execution.ps1 -UseExternal       # Single-variable mode (5 paths, full Vitruvius)
#   .\run-symbolic-execution.ps1 -MultiVar          # Multi-variable mode (25 paths, full Vitruvius)
#
# Or specify custom path:
#   .\run-symbolic-execution.ps1 -UseExternal -ExternalPath "D:\Projects\Amalthea-acset"
#
# ============================================================================
# CONFIGURATION - Modify the default path below for your PC
# ============================================================================
# If using EXTERNAL or MULTIVAR mode, update the default ExternalPath parameter
# to point to your Amalthea-acset repository location.
#
# Example:
#   [string]$ExternalPath = "C:\Users\YourUsername\Amalthea-acset"
#   [string]$ExternalPath = "D:\Projects\Amalthea-acset"
#
# The path should point to the root directory where you cloned:
#   https://github.com/IngridJiang/Amalthea-acset
# ============================================================================

param(
    [switch]$UseExternal = $false,
    [switch]$Internal = $false,
    [switch]$MultiVar = $false,
    [string]$ExternalPath = "C:\Users\10239\Amathea-acset"  # <-- MODIFY THIS for your PC
)

$INTERACTIVE_MODE = (-not $UseExternal) -and (-not $Internal) -and (-not $MultiVar)

Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host "CocoPath" -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host ""

# Interactive mode selection if no flag provided
if ($INTERACTIVE_MODE) {
    Write-Host "Please select execution mode:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  1) INTERNAL MODE (Fast, simplified stub - single variable)" -ForegroundColor Green
    Write-Host "     - Output: Basic XMI stubs" -ForegroundColor Gray
    Write-Host "     - Explores: 5 paths (one user choice)" -ForegroundColor Gray
    Write-Host "     - No external repository needed" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  2) EXTERNAL MODE (Full Vitruvius transformations - single variable)" -ForegroundColor Yellow
    Write-Host "     - Output: Complete Vitruvius reactions & transformations" -ForegroundColor Gray
    Write-Host "     - Explores: 5 paths (one user choice)" -ForegroundColor Gray
    Write-Host "     - Requires external Amalthea-acset repository" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  3) MULTI-VARIABLE MODE (Full Vitruvius - TWO user choices)" -ForegroundColor Magenta
    Write-Host "     - Output: Complete Vitruvius reactions & transformations" -ForegroundColor Gray
    Write-Host "     - Explores: 25 paths (5 × 5 combinations)" -ForegroundColor Gray
    Write-Host "     - Requires external Amalthea-acset repository" -ForegroundColor Gray
    Write-Host ""
    $choice = Read-Host "Enter your choice (1, 2, or 3)"
    Write-Host ""

    switch ($choice) {
        "1" {
            $UseExternal = $false
            $MultiVar = $false
            Write-Host "Selected: INTERNAL MODE (single variable)" -ForegroundColor Green
        }
        "2" {
            $UseExternal = $true
            $MultiVar = $false
            Write-Host "Selected: EXTERNAL MODE (single variable)" -ForegroundColor Yellow
        }
        "3" {
            $UseExternal = $true
            $MultiVar = $true
            Write-Host "Selected: MULTI-VARIABLE MODE (two variables, 25 paths)" -ForegroundColor Magenta
        }
        default {
            Write-Host "Invalid choice. Defaulting to INTERNAL MODE." -ForegroundColor Yellow
            $UseExternal = $false
            $MultiVar = $false
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
    Write-Host "Mode: EXTERNAL (switching to external Amalthea-acset)" -ForegroundColor Yellow
    Write-Host ""

    # Verify external path exists
    if (-not (Test-Path $ExternalPath)) {
        Write-Host "ERROR: External Amalthea-acset not found at: $ExternalPath" -ForegroundColor Red
        Write-Host "Please check the path" -ForegroundColor Red
        exit 1
    }

    Write-Host "[1/4] Building external Amalthea-acset at $ExternalPath..." -ForegroundColor Yellow
    Push-Location $ExternalPath
    mvn clean install -DskipTests -Dcheckstyle.skip=true
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Failed to build external Amalthea-acset" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
    Write-Host "      Done." -ForegroundColor Green
    Write-Host ""

    Write-Host "[2/4] Temporarily switching to external dependency..." -ForegroundColor Yellow
    # Use Python script to safely switch dependencies
    $pythonCmd = $null
    if (Get-Command python.exe -ErrorAction SilentlyContinue) {
        $pythonCmd = "python.exe"
    } elseif (Get-Command python3 -ErrorAction SilentlyContinue) {
        $pythonCmd = "python3"
    } elseif (Get-Command python -ErrorAction SilentlyContinue) {
        $pythonCmd = "python"
    } else {
        Write-Host "ERROR: Python not found. Cannot switch dependencies." -ForegroundColor Red
        exit 1
    }

    & $pythonCmd switch-dependency.py external pom.xml
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Failed to switch to external dependency" -ForegroundColor Red
        exit 1
    }
    Write-Host "      Switched to external dependency." -ForegroundColor Green
    Write-Host ""

    $stepOffset = 2
} else {
    Write-Host "Mode: INTERNAL (using amalthea-acset-integration module)" -ForegroundColor Green
    Write-Host "      Note: Requires external Amalthea-acset built once for Vitruvius dependencies" -ForegroundColor Gray
    Write-Host ""

    # Check if Vitruvius dependencies are available
    if (-not (Test-Path "$env:USERPROFILE\.m2\repository\tools\vitruv\tools.vitruv.methodologisttemplate.vsum")) {
        Write-Host "WARNING: Vitruvius VSUM dependency not found in Maven repository" -ForegroundColor Yellow
        Write-Host "         Building external Amalthea-acset to install it..." -ForegroundColor Yellow
        Write-Host ""

        if (Test-Path $ExternalPath) {
            Push-Location $ExternalPath
            mvn clean install -DskipTests -Dcheckstyle.skip=true
            if ($LASTEXITCODE -ne 0) {
                Write-Host "ERROR: Failed to build external Amalthea-acset" -ForegroundColor Red
                Pop-Location
                exit 1
            }
            Pop-Location
            Write-Host "      Done. Vitruvius dependencies installed." -ForegroundColor Green
            Write-Host ""
        } else {
            Write-Host "ERROR: External Amalthea-acset not found at: $ExternalPath" -ForegroundColor Red
            Write-Host "       Please build it first or specify path with -ExternalPath" -ForegroundColor Red
            exit 1
        }
    }

    Write-Host "[1/4] Switching to internal dependency..." -ForegroundColor Yellow
    # Use Python script to safely switch dependencies
    $pythonCmd = $null
    if (Get-Command python.exe -ErrorAction SilentlyContinue) {
        $pythonCmd = "python.exe"
    } elseif (Get-Command python3 -ErrorAction SilentlyContinue) {
        $pythonCmd = "python3"
    } elseif (Get-Command python -ErrorAction SilentlyContinue) {
        $pythonCmd = "python"
    } else {
        Write-Host "ERROR: Python not found. Cannot switch dependencies." -ForegroundColor Red
        exit 1
    }

    & $pythonCmd switch-dependency.py internal pom.xml
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Failed to switch to internal dependency" -ForegroundColor Red
        exit 1
    }
    Write-Host "      Switched to internal dependency." -ForegroundColor Green
    Write-Host ""

    Write-Host "[2/4] Building internal amalthea-acset-integration..." -ForegroundColor Yellow
    Push-Location (Join-Path (Split-Path $scriptDir -Parent) "amalthea-acset-integration")
    mvn clean install -DskipTests -Dcheckstyle.skip=true
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Failed to build internal amalthea-acset-integration" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
    Write-Host "      Done." -ForegroundColor Green
    Write-Host ""

    $stepOffset = 2
}

$step1 = 3 + $stepOffset
$step2 = 4 + $stepOffset
$totalSteps = 4 + $stepOffset

Write-Host "[$step1/$totalSteps] Cleaning previous outputs..." -ForegroundColor Yellow
Get-ChildItem -Path "galette-output-*" -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force
Remove-Item "execution_paths_automatic.json" -ErrorAction SilentlyContinue
Remove-Item "execution_paths_multivar.json" -ErrorAction SilentlyContinue
Write-Host "      Done." -ForegroundColor Green
Write-Host ""

Write-Host "[$step2/$totalSteps] Running symbolic execution..." -ForegroundColor Yellow
Write-Host "      With manual constraint collection via PathUtils API" -ForegroundColor Gray

# Determine which main class to use
if ($MultiVar) {
    $mainClass = "edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"
    Write-Host "      Main class: AutomaticVitruvMultiVarPathExploration (multi-variable)" -ForegroundColor Gray
} else {
    $mainClass = "edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"
    Write-Host "      Main class: AutomaticVitruvPathExploration (single-variable)" -ForegroundColor Gray
}

# Note: Javaagent is not compatible with mvn exec:java
# We use manual constraint collection via PathUtils.addIntDomainConstraint() and addSwitchConstraint()

$mvnSuccess = $true
try {
    mvn exec:java "-Dexec.mainClass=$mainClass" "-Dcheckstyle.skip=true" `
        "-Dskip.instrumentation.check=true" `
        "-Duse.z3.solver=true" "-Dz3.solver.debug=false" `
        "-Dz3.timeout.ms=10000" "-Dz3.fallback.simple=true"
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

# Restore pom.xml from backup
if (Test-Path "pom.xml.bak") {
    Write-Host ""
    Write-Host "Restoring pom.xml from backup..." -ForegroundColor Yellow
    Copy-Item "pom.xml.bak" "pom.xml" -Force
    Remove-Item "pom.xml.bak" -ErrorAction SilentlyContinue
    Write-Host "      Done." -ForegroundColor Green
}

if ($MultiVar) {
    if (-not (Test-Path "execution_paths_multivar.json")) {
        if (-not $mvnSuccess) {
            Write-Host ""
            Write-Host "ERROR: Symbolic execution failed!" -ForegroundColor Red
            exit 1
        }
    }
} else {
    if (-not (Test-Path "execution_paths_automatic.json")) {
        if (-not $mvnSuccess) {
            Write-Host ""
            Write-Host "ERROR: Symbolic execution failed!" -ForegroundColor Red
            exit 1
        }
    }
}

Write-Host ""
Write-Host "================================================================================" -ForegroundColor Green
Write-Host "Completed." -ForegroundColor Green
Write-Host "================================================================================" -ForegroundColor Green
Write-Host ""
if ($MultiVar) {
    Write-Host "Generated files:" -ForegroundColor Cyan
    Write-Host "  - execution_paths_multivar.json       (Path exploration results)" -ForegroundColor White
    Write-Host "  - galette-output-multivar-*/          (Model outputs per path combination)" -ForegroundColor White
    Write-Host ""
    Write-Host "Multi-variable exploration:" -ForegroundColor Cyan
    Write-Host "  - Variables: user_choice_1, user_choice_2" -ForegroundColor White
    Write-Host "  - Expected paths: 5 × 5 = 25" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "Generated files:" -ForegroundColor Cyan
    Write-Host "  - execution_paths_automatic.json      (Path exploration results)" -ForegroundColor White
    Write-Host "  - galette-output-automatic-*/         (Model outputs per path)" -ForegroundColor White
    Write-Host ""
}
