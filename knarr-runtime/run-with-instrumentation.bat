@echo off
setlocal enabledelayedexpansion

REM ================================================================================
REM Galette Symbolic Execution - Automatic Instrumentation Mode
REM ================================================================================
REM This script uses Galette javaagent for AUTOMATIC constraint collection
REM (vs. manual PathUtils calls in run-symbolic-execution.bat)
REM ================================================================================

echo ================================================================================
echo Galette Symbolic Execution - AUTOMATIC Instrumentation Mode
echo ================================================================================
echo.

REM Resolve paths
set "SCRIPT_DIR=%~dp0"
set "ROOT_DIR=%SCRIPT_DIR%.."
set "GALETTE_AGENT=%ROOT_DIR%\galette-agent\target\galette-agent-1.0.0-SNAPSHOT.jar"
set "INSTRUMENTED_JAVA=%SCRIPT_DIR%target\galette\java\bin\java.exe"

REM Step 1: Build Galette agent
echo [1/5] Building Galette agent...
pushd "%ROOT_DIR%"
call mvn clean install -pl galette-agent -am -DskipTests -Dcheckstyle.skip=true -q
if errorlevel 1 (
    echo ERROR: Failed to build Galette agent
    popd
    exit /b 1
)
popd

if not exist "%GALETTE_AGENT%" (
    echo ERROR: Galette agent jar not found at %GALETTE_AGENT%
    exit /b 1
)
echo       Done. Agent: %GALETTE_AGENT%
echo.

REM Step 2: Interactive mode selection
set "USE_MULTIVAR=false"
set "MAIN_CLASS=edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"

echo [2/5] Select execution mode:
echo.
echo   1) Single-variable mode (5 paths)
echo   2) Multi-variable mode (25 paths)
echo.
set /p "MODE_CHOICE=Enter your choice (1 or 2): "

if "%MODE_CHOICE%"=="2" (
    set "USE_MULTIVAR=true"
    set "MAIN_CLASS=edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvMultiVarPathExploration"
    echo Selected: MULTI-VARIABLE MODE
) else (
    set "USE_MULTIVAR=false"
    set "MAIN_CLASS=edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration"
    echo Selected: SINGLE-VARIABLE MODE
)
echo.

REM Step 3: Build amalthea-acset-integration
echo [3/5] Building amalthea-acset-integration...
pushd "%ROOT_DIR%\amalthea-acset-integration"
call mvn clean install -DskipTests -Dcheckstyle.skip=true -q
if errorlevel 1 (
    echo ERROR: Failed to build amalthea-acset-integration
    popd
    exit /b 1
)
popd
echo       Done.
echo.

REM Step 4: Build knarr-runtime with instrumentation
echo [4/5] Building knarr-runtime and creating instrumented Java...
echo       This may take a minute...

REM Clean and build with instrumentation
pushd "%ROOT_DIR%"
call mvn clean install -pl knarr-runtime -am -DskipTests -Dcheckstyle.skip=true -q
if errorlevel 1 (
    echo ERROR: Failed to build knarr-runtime
    popd
    exit /b 1
)

REM Trigger instrumentation phase
call mvn process-test-resources -pl knarr-runtime -Dcheckstyle.skip=true -q
if errorlevel 1 (
    echo ERROR: Failed to create instrumented Java
    popd
    exit /b 1
)
popd

if not exist "%INSTRUMENTED_JAVA%" (
    echo ERROR: Instrumented Java not found at %INSTRUMENTED_JAVA%
    echo        Maven plugin may not have run correctly.
    exit /b 1
)
echo       Done. Instrumented Java: %INSTRUMENTED_JAVA%
echo.

REM Step 5: Build classpath
echo [5/5] Building classpath...
pushd "%ROOT_DIR%"
call mvn dependency:build-classpath -pl knarr-runtime -DincludeScope=runtime -Dmdep.outputFile="%SCRIPT_DIR%cp.txt" -q
if errorlevel 1 (
    echo ERROR: Failed to build classpath
    popd
    exit /b 1
)
popd

if not exist "%SCRIPT_DIR%cp.txt" (
    echo ERROR: Classpath file not found
    exit /b 1
)

REM Read classpath from file
set /p "MAVEN_CP="<"%SCRIPT_DIR%cp.txt"
set "CP=%SCRIPT_DIR%target\classes;%SCRIPT_DIR%target\test-classes;%MAVEN_CP%"

echo       Done.
echo.

REM Create cache directory
if not exist "%SCRIPT_DIR%target\galette\cache" (
    mkdir "%SCRIPT_DIR%target\galette\cache"
)

REM Clean previous outputs
if exist "%SCRIPT_DIR%galette-output-automatic-*" (
    echo Cleaning previous output directories...
    for /d %%d in ("%SCRIPT_DIR%galette-output-automatic-*") do rd /s /q "%%d" 2>nul
    for /d %%d in ("%SCRIPT_DIR%galette-output-multivar-*") do rd /s /q "%%d" 2>nul
)

echo ================================================================================
echo Running with AUTOMATIC instrumentation
echo ================================================================================
echo Mode: %MAIN_CLASS%
echo Agent: Galette javaagent
echo Constraint Collection: AUTOMATIC (via bytecode instrumentation)
echo Z3 Solver: ENABLED
echo.

REM Run with javaagent
"%INSTRUMENTED_JAVA%" ^
  -cp "%CP%" ^
  -Xbootclasspath/a:"%GALETTE_AGENT%" ^
  -javaagent:"%GALETTE_AGENT%" ^
  -Dgalette.cache="%SCRIPT_DIR%target\galette\cache" ^
  -Dgalette.coverage=true ^
  -Dgalette.debug=false ^
  -Dsymbolic.execution.debug=true ^
  -Dpath.explorer.debug=true ^
  -Dconstraint.solver.debug=true ^
  -Duse.z3.solver=true ^
  -Dz3.solver.debug=true ^
  -Dz3.timeout.ms=10000 ^
  -Dz3.fallback.simple=true ^
  -Dpath.explorer.max.iterations=30 ^
  %MAIN_CLASS%

set "EXIT_CODE=%ERRORLEVEL%"

echo.
echo ================================================================================
echo Execution completed
echo ================================================================================

REM Check results
if "%USE_MULTIVAR%"=="true" (
    if exist "%SCRIPT_DIR%execution_paths_multivar.json" (
        echo Success! Results saved to: execution_paths_multivar.json
        echo.
        echo Summary:
        findstr /C:"total_paths" "%SCRIPT_DIR%execution_paths_multivar.json"
        findstr /C:"num_variables" "%SCRIPT_DIR%execution_paths_multivar.json"
    ) else (
        echo WARNING: execution_paths_multivar.json not found
    )
) else (
    if exist "%SCRIPT_DIR%execution_paths_automatic.json" (
        echo Success! Results saved to: execution_paths_automatic.json
        echo.
        echo Path count:
        find /c "pathId" "%SCRIPT_DIR%execution_paths_automatic.json"
    ) else (
        echo WARNING: execution_paths_automatic.json not found
    )
)

echo.
echo ================================================================================
echo Key Difference from Manual Mode:
echo ================================================================================
echo MANUAL MODE (run-symbolic-execution.bat):
echo   - Uses -Dskip.instrumentation.check=true
echo   - Constraints added via PathUtils.addIntDomainConstraint() calls
echo   - No javaagent required
echo.
echo AUTOMATIC MODE (this script):
echo   - Uses Galette javaagent
echo   - Constraints collected via bytecode instrumentation
echo   - No manual PathUtils calls needed
echo.
echo Both modes should produce identical results!
echo ================================================================================

exit /b %EXIT_CODE%
