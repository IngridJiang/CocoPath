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
    set /p choice="Enter your choice (1, 2, or 3): "
    echo.

    if "!choice!"=="1" (
        set "USE_EXTERNAL=false"
        set "USE_MULTIVAR=false"
        echo Selected: INTERNAL MODE ^(single variable^)
    ) else if "!choice!"=="2" (
        set "USE_EXTERNAL=true"
        set "USE_MULTIVAR=false"
        echo Selected: EXTERNAL MODE ^(single variable^)
    ) else if "!choice!"=="3" (
        set "USE_EXTERNAL=true"
        set "USE_MULTIVAR=true"
        echo Selected: MULTI-VARIABLE MODE ^(two variables, 25 paths^)
    ) else (
        echo Invalid choice. Defaulting to INTERNAL MODE.
        set "USE_EXTERNAL=false"
        set "USE_MULTIVAR=false"
    )
    echo.
)

echo ================================================================================
echo.

if "%USE_EXTERNAL%"=="true" (
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
if "%USE_MULTIVAR%"=="true" (
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
"%INSTRUMENTED_JAVA%\bin\java.exe" ^
    -cp "%CP%" ^
    -Xbootclasspath/a:"%GALETTE_AGENT%" ^
    -javaagent:"%GALETTE_AGENT%" ^
    -Dgalette.cache=target/galette/cache ^
    -Dpath.explorer.max.iterations=30 ^
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

if not exist execution_paths_automatic.json (
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
if "%USE_MULTIVAR%"=="true" (
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
