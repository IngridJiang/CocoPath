# ============================================================================
# Galette-Knarr Symbolic Execution Runner (Adapted)
# ============================================================================
# This script runs automatic path exploration for Vitruvius model transformations.
# It uses the PathExplorer API to automatically generate test inputs by:
#   1. Executing transformations with concrete values
#   2. Collecting path constraints
#   3. Negating constraints to find unexplored paths
#   4. Solving for new inputs automatically
#
# Usage:
#   .\run-symbolic-execution-adapted.ps1                    # Interactive mode (prompts for choice)
#   .\run-symbolic-execution-adapted.ps1 -Internal          # Single-variable mode (5 paths, simplified)
#   .\run-symbolic-execution-adapted.ps1 -UseExternal       # Single-variable mode (5 paths, full Vitruvius)
#   .\run-symbolic-execution-adapted.ps1 -MultiVar          # Multi-variable mode (25 paths, full Vitruvius)
#   .\run-symbolic-execution-adapted.ps1 -ForceRebuild      # Force rebuild even if sources unchanged
#
# Or specify custom path:
#   .\run-symbolic-execution-adapted.ps1 -UseExternal -ExternalPath "D:\Projects\Amalthea-acset"
#
# ============================================================================
# CONFIGURATION - Modify this section for your PC
# ============================================================================
# If using EXTERNAL or MULTIVAR mode, update the default ExternalPath parameter
# to point to your Amalthea-acset repository location.
#
# ============================================================================

param(
    [switch]$UseExternal = $false,
    [switch]$Internal = $false,
    [switch]$MultiVar = $false,
    [switch]$ForceRebuild = $false,
    [string]$ExternalPath = "C:\Users\10239\Amathea-acset",  # <-- MODIFY THIS for your PC
    [string]$JavaHomeOverride = ""  # <-- Set to Java 17 path if needed, e.g. "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot"
)

# ============================================================================
# JAVA VERSION CONFIGURATION
# ============================================================================
# Set JavaHomeOverride to use a specific Java version. The script requires Java 17+
# Modify this to point to your Java 17 installation, or leave empty to use
# the system default. Common Windows locations:
#   C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot
#   C:\Program Files\Microsoft\jdk-17.x.x.x-hotspot
#   C:\Program Files\Java\jdk-17
#   $env:JAVA_HOME (uses system JAVA_HOME if set)
#
# If you have multiple Java versions, explicitly set it via the parameter:
#   .\run-symbolic-execution-adapted.ps1 -JavaHomeOverride "C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"

# Auto-detect Java 17 if not explicitly set
if ([string]::IsNullOrEmpty($JavaHomeOverride)) {
    $javaCandidates = @(
        (Get-Item "C:\Program Files\Eclipse Adoptium\jdk-17*" -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1)?.FullName,
        (Get-Item "C:\Program Files\Microsoft\jdk-17*" -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1)?.FullName,
        (Get-Item "C:\Program Files\Java\jdk-17*" -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1)?.FullName,
        (Get-Item "C:\Program Files\OpenJDK\jdk-17*" -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1)?.FullName,
        $env:JAVA_HOME
    )
    foreach ($candidate in $javaCandidates) {
        if (-not [string]::IsNullOrEmpty($candidate) -and (Test-Path "$candidate\bin\java.exe")) {
            $JavaHomeOverride = $candidate
            break
        }
    }
}

# Set JAVA_HOME if we found a valid path
if (-not [string]::IsNullOrEmpty($JavaHomeOverride) -and (Test-Path "$JavaHomeOverride\bin\java.exe")) {
    $env:JAVA_HOME = $JavaHomeOverride
    $env:PATH = "$JavaHomeOverride\bin;" + $env:PATH
    $javaVersion = & "$JavaHomeOverride\bin\java.exe" -version 2>&1 | Select-String 'version' | Select-Object -First 1
    Write-Host "Using Java: $javaVersion from $JavaHomeOverride"
    Write-Host ""
} else {
    $javaVersion = & java -version 2>&1 | Select-String 'version' | Select-Object -First 1
    Write-Host "Using system Java: $javaVersion"
    Write-Host ""
}

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
    Write-Host "     - Explores: 25 paths (5 x 5 combinations)" -ForegroundColor Gray
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

# Function to check if sources in a directory have been modified since last build
function Test-SourcesChanged {
    param([string]$SrcDir)
    $targetDir = Join-Path $SrcDir "target"
    if (-not (Test-Path $targetDir)) {
        return $true  # No target directory means it hasn't been built yet
    }
    $targetTime = (Get-Item $targetDir).LastWriteTime
    $srcSubDir = Join-Path $SrcDir "src"
    $newestSrc = Get-ChildItem $srcSubDir -Recurse -File -ErrorAction SilentlyContinue |
                 Sort-Object LastWriteTime -Descending | Select-Object -First 1
    $pomFile = Join-Path $SrcDir "pom.xml"
    $newestPom = if (Test-Path $pomFile) { (Get-Item $pomFile).LastWriteTime } else { [datetime]::MinValue }
    $newestSource = if ($newestSrc -and $newestSrc.LastWriteTime -gt $newestPom) {
        $newestSrc.LastWriteTime
    } else { $newestPom }
    return $newestSource -gt $targetTime
}

if ($UseExternal) {
    Write-Host "Mode: EXTERNAL (switching to external Amalthea-acset)" -ForegroundColor Yellow
    Write-Host ""

    # Verify external path exists
    if (-not (Test-Path $ExternalPath)) {
        Write-Host "ERROR: External Amalthea-acset not found at: $ExternalPath" -ForegroundColor Red
        Write-Host "Please check the path" -ForegroundColor Red
        exit 1
    }

    # Clean Maven repository to avoid stale JAR issues
    Write-Host "      Cleaning Maven repository cache..." -ForegroundColor Gray
    $consistencyCache = "$env:USERPROFILE\.m2\repository\tools\vitruv\tools.vitruv.methodologisttemplate.consistency\0.1.0-SNAPSHOT"
    Remove-Item $consistencyCache -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "      Removed cached external consistency JAR" -ForegroundColor Gray

    # Check if rebuild is needed
    if ($ForceRebuild) {
        Write-Host "[1/4] Building external Amalthea-acset (--force-rebuild flag set)..." -ForegroundColor Yellow
        Push-Location $ExternalPath
        mvn clean install -DskipTests -Dcheckstyle.skip=true
        if ($LASTEXITCODE -ne 0) {
            Write-Host "ERROR: Failed to build external Amalthea-acset" -ForegroundColor Red
            Pop-Location
            exit 1
        }
        Pop-Location
    } elseif (Test-SourcesChanged $ExternalPath) {
        Write-Host "[1/4] Building external Amalthea-acset (sources have changed)..." -ForegroundColor Yellow
        Push-Location $ExternalPath
        mvn clean install -DskipTests -Dcheckstyle.skip=true
        if ($LASTEXITCODE -ne 0) {
            Write-Host "ERROR: Failed to build external Amalthea-acset" -ForegroundColor Red
            Pop-Location
            exit 1
        }
        Pop-Location
    } else {
        Write-Host "[1/4] Skipping external Amalthea-acset build (sources unchanged)..." -ForegroundColor Yellow
    }
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

    # Clean Maven repository to avoid stale JAR issues
    Write-Host "      Cleaning Maven repository cache..." -ForegroundColor Gray
    $internalCache = "$env:USERPROFILE\.m2\repository\edu\neu\ccs\prl\galette\amalthea-acset-consistency\1.0.0-SNAPSHOT"
    Remove-Item $internalCache -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "      Removed cached internal consistency JAR" -ForegroundColor Gray
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

    # Check if rebuild is needed for internal module
    $internalDir = Join-Path (Split-Path $scriptDir -Parent) "amalthea-acset-integration"
    if ($ForceRebuild) {
        Write-Host "[2/4] Building internal amalthea-acset-integration (-ForceRebuild flag set)..." -ForegroundColor Yellow
        Push-Location $internalDir
        mvn clean install -DskipTests -Dcheckstyle.skip=true
        if ($LASTEXITCODE -ne 0) {
            Write-Host "ERROR: Failed to build internal amalthea-acset-integration" -ForegroundColor Red
            Pop-Location
            exit 1
        }
        Pop-Location
    } elseif (Test-SourcesChanged $internalDir) {
        Write-Host "[2/4] Building internal amalthea-acset-integration (sources have changed)..." -ForegroundColor Yellow
        Push-Location $internalDir
        mvn clean install -DskipTests -Dcheckstyle.skip=true
        if ($LASTEXITCODE -ne 0) {
            Write-Host "ERROR: Failed to build internal amalthea-acset-integration" -ForegroundColor Red
            Pop-Location
            exit 1
        }
        Pop-Location
    } else {
        Write-Host "[2/4] Skipping internal amalthea-acset-integration build (sources unchanged)..." -ForegroundColor Yellow
    }
    Write-Host "      Done." -ForegroundColor Green
    Write-Host ""

    $stepOffset = 2
}

$step1 = 3 + $stepOffset
$step2 = 4 + $stepOffset
$totalSteps = 4 + $stepOffset

Write-Host "[$step1/$totalSteps] Cleaning previous outputs..." -ForegroundColor Yellow
Get-ChildItem -Path "galette-output-*" -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force
Remove-Item "execution_paths.json" -ErrorAction SilentlyContinue
Write-Host "      Done." -ForegroundColor Green
Write-Host ""

Write-Host "[$step2/$totalSteps] Running symbolic execution..." -ForegroundColor Yellow
Write-Host "      With semi-automatic constraint collection in reaction" -ForegroundColor Gray

# Determine which main class to use
if ($MultiVar) {
    $mainClass = "edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"
    Write-Host "      Main class: AutomaticVitruvMultiVarPathExploration (multi-variable)" -ForegroundColor Gray
} else {
    $mainClass = "edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"
    Write-Host "      Main class: AutomaticVitruvPathExploration (single-variable)" -ForegroundColor Gray
}

# Build the project first to ensure everything is compiled
Write-Host "      Building project..." -ForegroundColor Gray
mvn compile -U -Dcheckstyle.skip=true -q

# Check if instrumented Java is available
$instrumentedJava = "target\galette\java"
if (-not (Test-Path "$instrumentedJava\bin\java.exe")) {
    Write-Host "ERROR: Instrumented Java not found. Building it now..." -ForegroundColor Yellow
    mvn process-test-resources -U -Dcheckstyle.skip=true -q
}

# Resolve Galette agent
$galetteAgent = $null
if (Test-Path "..\galette-agent\target\galette-agent-1.0.0-SNAPSHOT.jar") {
    $galetteAgent = "..\galette-agent\target\galette-agent-1.0.0-SNAPSHOT.jar"
} elseif (Test-Path "$env:USERPROFILE\.m2\repository\edu\neu\ccs\prl\galette\galette-agent\1.0.0-SNAPSHOT\galette-agent-1.0.0-SNAPSHOT.jar") {
    $galetteAgent = "$env:USERPROFILE\.m2\repository\edu\neu\ccs\prl\galette\galette-agent\1.0.0-SNAPSHOT\galette-agent-1.0.0-SNAPSHOT.jar"
} else {
    Write-Host "ERROR: Galette agent jar not found" -ForegroundColor Red
    exit 1
}

# Build classpath
mvn -q "-DincludeScope=runtime" "-Dmdep.outputFile=cp.txt" dependency:build-classpath
$cp = "target\classes;target\test-classes;" + (Get-Content "cp.txt" -Raw).Trim()

Write-Host "      Using instrumented JVM with Galette agent" -ForegroundColor Gray
$mvnSuccess = $true
try {
    & "$instrumentedJava\bin\java.exe" `
        -cp "$cp" `
        "-Xbootclasspath/a:$galetteAgent" `
        "-javaagent:$galetteAgent" `
        "-Dgalette.cache=target/galette/cache" `
        "-Dpath.explorer.max.iterations=30" `
        $mainClass
    if ($LASTEXITCODE -ne 0) {
        $mvnSuccess = $false
        Write-Host ""
        Write-Host "WARNING: Execution had errors" -ForegroundColor Yellow
    }
} catch {
    $mvnSuccess = $false
    Write-Host ""
    Write-Host "WARNING: Execution failed" -ForegroundColor Yellow
}

# Restore pom.xml from backup
if (Test-Path "pom.xml.bak") {
    Write-Host ""
    Write-Host "Restoring pom.xml from backup..." -ForegroundColor Yellow
    Copy-Item "pom.xml.bak" "pom.xml" -Force
    Remove-Item "pom.xml.bak" -ErrorAction SilentlyContinue
    Write-Host "      Done." -ForegroundColor Green
}

if (-not (Test-Path "execution_paths_automatic.json")) {
    if (-not $mvnSuccess) {
        Write-Host ""
        Write-Host "ERROR: Symbolic execution failed!" -ForegroundColor Red
        exit 1
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
} else {
    Write-Host "Generated files:" -ForegroundColor Cyan
    Write-Host "  - execution_paths_automatic.json      (Path exploration results)" -ForegroundColor White
    Write-Host "  - galette-output-automatic-*/         (Model outputs per path)" -ForegroundColor White
    Write-Host ""
}
