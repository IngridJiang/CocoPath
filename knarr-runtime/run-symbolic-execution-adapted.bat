@echo off
REM ============================================================================
REM Galette-Knarr Symbolic Execution Runner (Adapted)
REM ============================================================================
REM This script runs automatic path exploration for Vitruvius model transformations.
REM It uses the PathExplorer API to automatically generate test inputs by:
REM   1. Executing transformations with concrete values
REM   2. Collecting path constraints
REM   3. Negating constraints to find unexplored paths
REM   4. Solving for new inputs automatically
REM
REM Usage:
REM   run-symbolic-execution-adapted.bat              # Interactive mode (prompts for choice)
REM   run-symbolic-execution-adapted.bat internal     # Single-variable mode (5 paths, simplified)
REM   run-symbolic-execution-adapted.bat external     # Single-variable mode (5 paths, full Vitruvius)
REM   run-symbolic-execution-adapted.bat multivar     # Multi-variable mode (25 paths, full Vitruvius)
REM   run-symbolic-execution-adapted.bat brake        # TinyBrake single-disc (2 vars)
REM   run-symbolic-execution-adapted.bat brake-multivar  # TinyBrake two-disc (4 vars)
REM
REM ============================================================================
REM CONFIGURATION - Modify this section for your PC
REM ============================================================================
REM If using EXTERNAL or MULTIVAR mode, update EXTERNAL_PATH to point to your
REM Amalthea-acset repository location.
REM
REM Example:
REM   set "EXTERNAL_PATH=C:\Users\YourUsername\Amalthea-acset"
REM   set "EXTERNAL_PATH=D:\Projects\Amalthea-acset"
REM
REM ============================================================================
REM JAVA VERSION CONFIGURATION
REM ============================================================================
REM Set JAVA_HOME_OVERRIDE to use a specific Java version. Requires Java 17+
REM Leave empty to use system JAVA_HOME. Common Windows locations:
REM   C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot
REM   C:\Program Files\Microsoft\jdk-17.x.x.x-hotspot
REM   C:\Program Files\Java\jdk-17
REM ============================================================================

setlocal enabledelayedexpansion

set "USE_EXTERNAL=false"
set "USE_MULTIVAR=false"
set "USE_BRAKE=false"
set "USE_BRAKE_MULTIVAR=false"
set "EXTERNAL_PATH=C:\Users\10239\Amathea-acset"
REM                  ^^^^^^^^^^^ MODIFY THIS for your PC ^^^^^^^^^^^
set "INTERACTIVE_MODE=true"
set "JAVA_HOME_OVERRIDE="
REM set "JAVA_HOME_OVERRIDE=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"

REM Parse arguments
if /i "%~1"=="internal" (
    set "USE_EXTERNAL=false"
    set "USE_MULTIVAR=false"
    set "INTERACTIVE_MODE=false"
)
if /i "%~1"=="external" (
    set "USE_EXTERNAL=true"
    set "USE_MULTIVAR=false"
    set "INTERACTIVE_MODE=false"
)
if /i "%~1"=="multivar" (
    set "USE_EXTERNAL=true"
    set "USE_MULTIVAR=true"
    set "INTERACTIVE_MODE=false"
)
if /i "%~1"=="brake" (
    set "USE_BRAKE=true"
    set "USE_BRAKE_MULTIVAR=false"
    set "INTERACTIVE_MODE=false"
)
if /i "%~1"=="brake-multivar" (
    set "USE_BRAKE=true"
    set "USE_BRAKE_MULTIVAR=true"
    set "INTERACTIVE_MODE=false"
)

REM Apply Java override if set
if not "%JAVA_HOME_OVERRIDE%"=="" (
    if exist "%JAVA_HOME_OVERRIDE%\bin\java.exe" (
        set "JAVA_HOME=%JAVA_HOME_OVERRIDE%"
        set "PATH=%JAVA_HOME_OVERRIDE%\bin;%PATH%"
        echo Using Java from: %JAVA_HOME_OVERRIDE%
        echo.
    )
) else (
    echo Using system Java
    echo.
)

echo ================================================================================
echo CocoPath
echo ================================================================================
echo.

REM Interactive mode selection if no argument provided
if "%INTERACTIVE_MODE%"=="true" (
    echo Please select execution mode:
    echo.
    echo   1^) INTERNAL MODE ^(Fast, simplified stub - single variable^)
    echo      - Output: Basic XMI stubs
    echo      - Explores: 5 paths ^(one user choice^)
    echo      - No external repository needed
    echo.
    echo   2^) EXTERNAL MODE ^(Full Vitruvius transformations - single variable^)
    echo      - Output: Complete Vitruvius reactions ^& transformations
    echo      - Explores: 5 paths ^(one user choice^)
    echo      - Requires external Amalthea-acset repository
    echo.
    echo   3^) MULTI-VARIABLE MODE ^(Full Vitruvius - TWO user choices^)
    echo      - Output: Complete Vitruvius reactions ^& transformations
    echo      - Explores: 25 paths ^(5 x 5 combinations^)
    echo      - Requires external Amalthea-acset repository
    echo.
    echo   4^) BRAKE MODE ^(TinyBrakeVSUM - single disc, 2 symbolic variables^)
    echo      - Output: BrakeSystem/ControlSystem XMI models
    echo      - Explores: up to 10 paths ^(4 profile intervals x 3 calib intervals; skip has no calib^)
    echo      - Uses tinybrake-integration module ^(no external repo needed^)
    echo.
    echo   5^) BRAKE MULTI-VARIABLE MODE ^(TinyBrakeVSUM - two discs, 4 symbolic variables^)
    echo      - Output: BrakeSystem/ControlSystem XMI models
    echo      - Explores: 81 paths ^(3^4: 3 profiles x 3 calibs per disc, 2 discs; skip excluded by initial [0,0,0,0]^)
    echo      - Uses tinybrake-integration module ^(no external repo needed^)
    echo.
    set /p choice="Enter your choice (1-5): "
    echo.

    if "!choice!"=="1" (
        set "USE_EXTERNAL=false"
        set "USE_MULTIVAR=false"
        set "USE_BRAKE=false"
        echo Selected: INTERNAL MODE ^(single variable^)
    ) else if "!choice!"=="2" (
        set "USE_EXTERNAL=true"
        set "USE_MULTIVAR=false"
        set "USE_BRAKE=false"
        echo Selected: EXTERNAL MODE ^(single variable^)
    ) else if "!choice!"=="3" (
        set "USE_EXTERNAL=true"
        set "USE_MULTIVAR=true"
        set "USE_BRAKE=false"
        echo Selected: MULTI-VARIABLE MODE ^(two variables, 25 paths^)
    ) else if "!choice!"=="4" (
        set "USE_BRAKE=true"
        set "USE_BRAKE_MULTIVAR=false"
        echo Selected: BRAKE MODE ^(single disc, 2 variables^)
    ) else if "!choice!"=="5" (
        set "USE_BRAKE=true"
        set "USE_BRAKE_MULTIVAR=true"
        echo Selected: BRAKE MULTI-VARIABLE MODE ^(two discs, 4 variables^)
    ) else (
        echo Invalid choice. Defaulting to INTERNAL MODE.
        set "USE_EXTERNAL=false"
        set "USE_MULTIVAR=false"
        set "USE_BRAKE=false"
    )
    echo.
)

echo ================================================================================
echo.

if "%USE_BRAKE%"=="true" (
    echo Mode: BRAKE ^(switching to tinybrake-integration^)
    echo.

    REM Clean Maven cache for tinybrake to avoid stale JARs
    echo       Cleaning Maven repository cache...
    if exist "%USERPROFILE%\.m2\repository\edu\neu\ccs\prl\galette\tinybrake-integration-consistency\1.0.0-SNAPSHOT" (
        rmdir /s /q "%USERPROFILE%\.m2\repository\edu\neu\ccs\prl\galette\tinybrake-integration-consistency\1.0.0-SNAPSHOT" 2>nul
    )
    if exist "%USERPROFILE%\.m2\repository\edu\neu\ccs\prl\galette\tinybrake-integration-vsum\1.0.0-SNAPSHOT" (
        rmdir /s /q "%USERPROFILE%\.m2\repository\edu\neu\ccs\prl\galette\tinybrake-integration-vsum\1.0.0-SNAPSHOT" 2>nul
    )
    echo       Removed cached tinybrake JARs
    echo.

    echo [1/4] Switching pom.xml to brake dependency...
    python switch-dependency.py brake pom.xml
    if errorlevel 1 (
        echo ERROR: Failed to switch to brake dependency
        exit /b 1
    )
    echo       Switched to brake dependency.
    echo.

    echo [2/4] Building tinybrake-integration...
    pushd "..\tinybrake-integration"
    call mvn clean install -DskipTests -Dcheckstyle.skip=true
    if errorlevel 1 (
        echo ERROR: Failed to build tinybrake-integration
        popd
        exit /b 1
    )
    popd
    echo       Done.
    echo.

    set "STEP_OFFSET=2"
) else if "%USE_EXTERNAL%"=="true" (
    echo Mode: EXTERNAL ^(switching to external Amalthea-acset^)
    echo.

    REM Verify external path exists
    if not exist "%EXTERNAL_PATH%" (
        echo ERROR: External Amalthea-acset not found at: %EXTERNAL_PATH%
        echo Please check the path
        exit /b 1
    )

    REM Clean Maven repository to avoid stale JAR issues
    echo       Cleaning Maven repository cache...
    if exist "%USERPROFILE%\.m2\repository\tools\vitruv\tools.vitruv.methodologisttemplate.consistency\0.1.0-SNAPSHOT" (
        rmdir /s /q "%USERPROFILE%\.m2\repository\tools\vitruv\tools.vitruv.methodologisttemplate.consistency\0.1.0-SNAPSHOT" 2>nul
    )
    echo       Removed cached external consistency JAR

    echo [1/4] Building external Amalthea-acset at %EXTERNAL_PATH%...
    pushd "%EXTERNAL_PATH%"
    call mvn clean install -DskipTests -Dcheckstyle.skip=true
    if errorlevel 1 (
        echo ERROR: Failed to build external Amalthea-acset
        popd
        exit /b 1
    )
    popd
    echo       Done.
    echo.

    echo [2/4] Temporarily switching to external dependency...
    python switch-dependency.py external pom.xml
    if errorlevel 1 (
        echo ERROR: Failed to switch to external dependency
        exit /b 1
    )
    echo       Switched to external dependency.
    echo.

    set "STEP_OFFSET=2"
) else (
    echo Mode: INTERNAL ^(using amalthea-acset-integration module^)
    echo       Note: Requires external Amalthea-acset built once for Vitruvius dependencies
    echo.

    REM Clean Maven repository to avoid stale JAR issues
    echo       Cleaning Maven repository cache...
    if exist "%USERPROFILE%\.m2\repository\edu\neu\ccs\prl\galette\amalthea-acset-consistency\1.0.0-SNAPSHOT" (
        rmdir /s /q "%USERPROFILE%\.m2\repository\edu\neu\ccs\prl\galette\amalthea-acset-consistency\1.0.0-SNAPSHOT" 2>nul
    )
    echo       Removed cached internal consistency JAR
    echo.

    REM Check if Vitruvius dependencies are available
    if not exist "%USERPROFILE%\.m2\repository\tools\vitruv\tools.vitruv.methodologisttemplate.vsum" (
        echo WARNING: Vitruvius VSUM dependency not found in Maven repository
        echo          Building external Amalthea-acset to install it...
        echo.

        if exist "%EXTERNAL_PATH%" (
            pushd "%EXTERNAL_PATH%"
            call mvn clean install -DskipTests -Dcheckstyle.skip=true
            if errorlevel 1 (
                echo ERROR: Failed to build external Amalthea-acset
                popd
                exit /b 1
            )
            popd
            echo       Done. Vitruvius dependencies installed.
            echo.
        ) else (
            echo ERROR: External Amalthea-acset not found at: %EXTERNAL_PATH%
            echo        Please build it first or specify path
            exit /b 1
        )
    )

    echo [1/4] Switching to internal dependency...
    python switch-dependency.py internal pom.xml
    if errorlevel 1 (
        echo ERROR: Failed to switch to internal dependency
        exit /b 1
    )
    echo       Switched to internal dependency.
    echo.

    echo [2/4] Building internal amalthea-acset-integration...
    pushd "..\amalthea-acset-integration"
    call mvn clean install -DskipTests -Dcheckstyle.skip=true
    if errorlevel 1 (
        echo ERROR: Failed to build internal amalthea-acset-integration
        popd
        exit /b 1
    )
    popd
    echo       Done.
    echo.

    set "STEP_OFFSET=2"
)

set /a STEP1=3+%STEP_OFFSET%
set /a STEP2=4+%STEP_OFFSET%
set /a TOTAL_STEPS=4+%STEP_OFFSET%

echo [%STEP1%/%TOTAL_STEPS%] Cleaning previous outputs...
if exist galette-output-* rmdir /s /q galette-output-* 2>nul
if exist execution_paths.json del /q execution_paths.json 2>nul
echo       Done.
echo.

echo [%STEP2%/%TOTAL_STEPS%] Running symbolic execution...
echo       With semi-automatic constraint collection in reaction

REM Determine which main class to use
if "%USE_BRAKE%"=="true" (
    if "%USE_BRAKE_MULTIVAR%"=="true" (
        set "MAIN_CLASS=edu.neu.ccs.prl.galette.vitruvius.AutomaticBrakeMultiVarPathExploration"
        echo       Main class: AutomaticBrakeMultiVarPathExploration ^(brake two-disc^)
    ) else (
        set "MAIN_CLASS=edu.neu.ccs.prl.galette.vitruvius.AutomaticBrakePathExploration"
        echo       Main class: AutomaticBrakePathExploration ^(brake single-disc^)
    )
) else if "%USE_MULTIVAR%"=="true" (
    set "MAIN_CLASS=edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"
    echo       Main class: AutomaticVitruvMultiVarPathExploration ^(multi-variable^)
) else (
    set "MAIN_CLASS=edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"
    echo       Main class: AutomaticVitruvPathExploration ^(single-variable^)
)

REM Build the project first to ensure everything is compiled
echo       Building project...
call mvn compile -U -Dcheckstyle.skip=true -q

REM Check if instrumented Java is available
set "INSTRUMENTED_JAVA=target\galette\java"
if not exist "%INSTRUMENTED_JAVA%\bin\java.exe" (
    echo ERROR: Instrumented Java not found. Building it now...
    call mvn process-test-resources -U -Dcheckstyle.skip=true -q
)

REM Resolve Galette agent
set "GALETTE_AGENT="
if exist "..\galette-agent\target\galette-agent-1.0.0-SNAPSHOT.jar" (
    set "GALETTE_AGENT=..\galette-agent\target\galette-agent-1.0.0-SNAPSHOT.jar"
) else if exist "%USERPROFILE%\.m2\repository\edu\neu\ccs\prl\galette\galette-agent\1.0.0-SNAPSHOT\galette-agent-1.0.0-SNAPSHOT.jar" (
    set "GALETTE_AGENT=%USERPROFILE%\.m2\repository\edu\neu\ccs\prl\galette\galette-agent\1.0.0-SNAPSHOT\galette-agent-1.0.0-SNAPSHOT.jar"
) else (
    echo ERROR: Galette agent jar not found
    exit /b 1
)

REM Build classpath
call mvn -q -DincludeScope=runtime -Dmdep.outputFile=cp.txt dependency:build-classpath
set /p CP_DEPS=<cp.txt
set "CP=target\classes;target\test-classes;%CP_DEPS%"

echo       Using instrumented JVM with Galette agent

REM No extra JVM flags needed: constraint recording for brake modes uses explicit
REM PathUtils.addIfComparisonConstraint calls in the reactions, so TagPropagator
REM branch instrumentation is not required.
set "SYMBOLIC_FLAG="

"%INSTRUMENTED_JAVA%\bin\java.exe" ^
    -cp "%CP%" ^
    -Xbootclasspath/a:"%GALETTE_AGENT%" ^
    -javaagent:"%GALETTE_AGENT%" ^
    -Dgalette.cache=target/galette/cache ^
    -Dpath.explorer.max.iterations=200 ^
    %SYMBOLIC_FLAG% ^
    %MAIN_CLASS%
set "EXEC_EXIT=%ERRORLEVEL%"

if not "%EXEC_EXIT%"=="0" (
    echo.
    echo WARNING: Execution had errors
)

REM Restore pom.xml from backup
if exist pom.xml.bak (
    echo.
    echo Restoring pom.xml from backup...
    copy /y pom.xml.bak pom.xml >nul 2>&1
    del pom.xml.bak 2>nul
    echo       Done.
)

if "%USE_BRAKE%"=="true" (
    if "%USE_BRAKE_MULTIVAR%"=="true" (
        set "EXPECTED_JSON=execution_paths_brake_multivar.json"
    ) else (
        set "EXPECTED_JSON=execution_paths_brake.json"
    )
) else if "%USE_MULTIVAR%"=="true" (
    set "EXPECTED_JSON=execution_paths_multivar.json"
) else (
    set "EXPECTED_JSON=execution_paths_automatic.json"
)

if not exist "%EXPECTED_JSON%" (
    if not "%EXEC_EXIT%"=="0" (
        echo.
        echo ERROR: Symbolic execution failed!
        exit /b 1
    )
)

echo.
echo ================================================================================
echo Completed.
echo ================================================================================
echo.
if "%USE_BRAKE%"=="true" (
    if "%USE_BRAKE_MULTIVAR%"=="true" (
        echo Generated files:
        echo   - execution_paths_brake_multivar.json  ^(Path exploration results^)
        echo   - galette-output-brake-multivar-*/     ^(Model outputs per path combination^)
        echo.
    ) else (
        echo Generated files:
        echo   - execution_paths_brake.json           ^(Path exploration results^)
        echo   - galette-output-brake-*/              ^(Model outputs per path^)
        echo.
    )
) else if "%USE_MULTIVAR%"=="true" (
    echo Generated files:
    echo   - execution_paths_multivar.json       ^(Path exploration results^)
    echo   - galette-output-multivar-*/          ^(Model outputs per path combination^)
    echo.
) else (
    echo Generated files:
    echo   - execution_paths_automatic.json      ^(Path exploration results^)
    echo   - galette-output-automatic-*/         ^(Model outputs per path^)
    echo.
)
