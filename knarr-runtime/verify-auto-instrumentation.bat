@echo off
setlocal enabledelayedexpansion

REM ================================================================================
REM Verification Script: Test if auto-instrumentation works correctly
REM ================================================================================
REM This script will:
REM 1. Run with manual mode (baseline)
REM 2. Run with auto-instrumentation mode
REM 3. Compare results to verify auto mode works correctly
REM 4. Only if verified, we can proceed to remove manual PathUtils calls
REM ================================================================================

echo ================================================================================
echo Auto-Instrumentation Verification Test
echo ================================================================================
echo.
echo This script will test if Galette javaagent can automatically collect
echo constraints without manual PathUtils calls.
echo.
echo Test plan:
echo   1. Backup current results (if any)
echo   2. Run MANUAL mode (baseline)        - uses PathUtils calls
echo   3. Run AUTO mode (with javaagent)    - uses instrumentation
echo   4. Compare results
echo   5. Report if auto mode is ready
echo.

pause

REM ================================================================================
REM Step 1: Backup existing results
REM ================================================================================
echo.
echo [Step 1/5] Backing up existing results...

if exist execution_paths_automatic.json (
    copy /y execution_paths_automatic.json execution_paths_automatic.json.backup >nul 2>&1
    echo       Backed up: execution_paths_automatic.json
)

if exist execution_paths_multivar.json (
    copy /y execution_paths_multivar.json execution_paths_multivar.json.backup >nul 2>&1
    echo       Backed up: execution_paths_multivar.json
)

echo       Done.
echo.

REM ================================================================================
REM Step 2: Run MANUAL mode (baseline)
REM ================================================================================
echo [Step 2/5] Running MANUAL mode (baseline)...
echo       Using: run-symbolic-execution.bat
echo       This mode uses manual PathUtils calls
echo.

REM Run manual mode automatically (select option 1)
echo 1 | call run-symbolic-execution.bat > manual-mode-output.log 2>&1

if errorlevel 1 (
    echo ERROR: Manual mode failed
    echo Check manual-mode-output.log for details
    exit /b 1
)

if not exist execution_paths_automatic.json (
    echo ERROR: Manual mode did not produce execution_paths_automatic.json
    exit /b 1
)

REM Rename to baseline
copy /y execution_paths_automatic.json execution_paths_manual_baseline.json >nul 2>&1

echo       Done. Results saved to: execution_paths_manual_baseline.json
echo.

REM Parse manual mode results
for /f "tokens=*" %%a in ('findstr /C:"pathId" execution_paths_manual_baseline.json ^| find /c "pathId"') do set "MANUAL_PATHS=%%a"
echo       Manual mode found: %MANUAL_PATHS% paths

REM Check constraints in manual mode
findstr /C:"numConstraints" execution_paths_manual_baseline.json > manual-constraints.tmp
set "MANUAL_HAS_CONSTRAINTS=0"
for /f "tokens=2 delims=:" %%a in (manual-constraints.tmp) do (
    set "LINE=%%a"
    set "LINE=!LINE:,=!"
    set "LINE=!LINE: =!"
    if not "!LINE!"=="0" set "MANUAL_HAS_CONSTRAINTS=1"
)
del manual-constraints.tmp 2>nul

if "%MANUAL_HAS_CONSTRAINTS%"=="1" (
    echo       Manual mode: ✓ Constraints collected successfully
) else (
    echo       Manual mode: ✗ WARNING - No constraints found!
    echo       This indicates the fix may not be working correctly.
)
echo.

REM ================================================================================
REM Step 3: Run AUTO instrumentation mode
REM ================================================================================
echo [Step 3/5] Running AUTO instrumentation mode...
echo       Using: run-with-instrumentation.bat
echo       This mode uses Galette javaagent
echo.

REM Check if script exists
if not exist run-with-instrumentation.bat (
    echo ERROR: run-with-instrumentation.bat not found
    echo Please create it first
    exit /b 1
)

REM Run auto mode (select option 1)
echo 1 | call run-with-instrumentation.bat > auto-mode-output.log 2>&1

set "AUTO_EXIT=%ERRORLEVEL%"

if %AUTO_EXIT% neq 0 (
    echo WARNING: Auto mode exited with error code %AUTO_EXIT%
    echo This might be expected on first run (need to build instrumentation)
    echo Check auto-mode-output.log for details
    echo.
)

if not exist execution_paths_automatic.json (
    echo ERROR: Auto mode did not produce execution_paths_automatic.json
    echo.
    echo Possible reasons:
    echo   - Instrumented Java not found (run mvn process-test-resources)
    echo   - Galette agent not found (run mvn install -pl galette-agent)
    echo   - Execution failed (check auto-mode-output.log)
    echo.
    type auto-mode-output.log | findstr /C:"ERROR" /C:"Exception" /C:"not found"
    exit /b 1
)

REM Rename to auto results
copy /y execution_paths_automatic.json execution_paths_auto_instrumentation.json >nul 2>&1

echo       Done. Results saved to: execution_paths_auto_instrumentation.json
echo.

REM Parse auto mode results
for /f "tokens=*" %%a in ('findstr /C:"pathId" execution_paths_auto_instrumentation.json ^| find /c "pathId"') do set "AUTO_PATHS=%%a"
echo       Auto mode found: %AUTO_PATHS% paths

REM Check constraints in auto mode
findstr /C:"numConstraints" execution_paths_auto_instrumentation.json > auto-constraints.tmp
set "AUTO_HAS_CONSTRAINTS=0"
for /f "tokens=2 delims=:" %%a in (auto-constraints.tmp) do (
    set "LINE=%%a"
    set "LINE=!LINE:,=!"
    set "LINE=!LINE: =!"
    if not "!LINE!"=="0" set "AUTO_HAS_CONSTRAINTS=1"
)
del auto-constraints.tmp 2>nul

if "%AUTO_HAS_CONSTRAINTS%"=="1" (
    echo       Auto mode: ✓ Constraints collected successfully
) else (
    echo       Auto mode: ✗ WARNING - No constraints found!
    echo       Automatic instrumentation may not be working.
)
echo.

REM ================================================================================
REM Step 4: Compare results
REM ================================================================================
echo [Step 4/5] Comparing results...
echo.

echo   Manual mode paths: %MANUAL_PATHS%
echo   Auto mode paths:   %AUTO_PATHS%
echo.

set "VERIFICATION_PASSED=1"

REM Check path count
if "%MANUAL_PATHS%"=="%AUTO_PATHS%" (
    echo   ✓ Path count matches
) else (
    echo   ✗ Path count MISMATCH!
    echo     This indicates auto-instrumentation is not working correctly.
    set "VERIFICATION_PASSED=0"
)

REM Check constraints
if "%MANUAL_HAS_CONSTRAINTS%"=="1" (
    if "%AUTO_HAS_CONSTRAINTS%"=="1" (
        echo   ✓ Both modes collected constraints
    ) else (
        echo   ✗ Auto mode did NOT collect constraints
        echo     Javaagent may not be working.
        set "VERIFICATION_PASSED=0"
    )
) else (
    echo   ⚠ Manual mode has no constraints - fix may not be working
    set "VERIFICATION_PASSED=0"
)

REM Compare first few paths in detail
echo.
echo   Detailed comparison of first 3 paths:
echo.

for /l %%i in (1,1,3) do (
    echo   Path %%i:

    REM Extract pathId and numConstraints from manual
    for /f "tokens=2 delims=:" %%a in ('findstr /C:"pathId.*%%i" execution_paths_manual_baseline.json ^| findstr /n "." ^| findstr "^1:"') do set "MANUAL_PATH_%%i=%%a"
    for /f "tokens=2 delims=:" %%a in ('type execution_paths_manual_baseline.json ^| findstr /C:"pathId" /C:"numConstraints" ^| findstr /A:%%i /C:"numConstraints"') do (
        set "MANUAL_CONSTRAINTS_%%i=%%a"
        set "MANUAL_CONSTRAINTS_%%i=!MANUAL_CONSTRAINTS_%%i:,=!"
        set "MANUAL_CONSTRAINTS_%%i=!MANUAL_CONSTRAINTS_%%i: =!"
    )

    REM Extract from auto
    for /f "tokens=2 delims=:" %%a in ('type execution_paths_auto_instrumentation.json ^| findstr /C:"pathId" /C:"numConstraints" ^| findstr /A:%%i /C:"numConstraints"') do (
        set "AUTO_CONSTRAINTS_%%i=%%a"
        set "AUTO_CONSTRAINTS_%%i=!AUTO_CONSTRAINTS_%%i:,=!"
        set "AUTO_CONSTRAINTS_%%i=!AUTO_CONSTRAINTS_%%i: =!"
    )

    echo     Manual: !MANUAL_CONSTRAINTS_%%i! constraints
    echo     Auto:   !AUTO_CONSTRAINTS_%%i! constraints

    if "!MANUAL_CONSTRAINTS_%%i!"=="!AUTO_CONSTRAINTS_%%i!" (
        echo     Status: ✓ Match
    ) else (
        echo     Status: ✗ Mismatch
        set "VERIFICATION_PASSED=0"
    )
    echo.
)

REM ================================================================================
REM Step 5: Final report and recommendations
REM ================================================================================
echo [Step 5/5] Verification Report
echo ================================================================================
echo.

if "%VERIFICATION_PASSED%"=="1" (
    echo   ✅ VERIFICATION PASSED!
    echo.
    echo   Auto-instrumentation is working correctly:
    echo     - Same number of paths as manual mode
    echo     - All paths have constraints collected
    echo     - Constraint counts match
    echo.
    echo   ✅ SAFE TO PROCEED with removing manual PathUtils calls
    echo.
    echo   Next steps:
    echo     1. Modify GaletteSymbolicator.java to remove manual calls
    echo     2. Recompile and test with run-with-instrumentation.bat
    echo     3. Verify results are still correct
    echo.

    echo VERIFICATION PASSED > verification-status.txt

) else (
    echo   ❌ VERIFICATION FAILED!
    echo.
    echo   Auto-instrumentation is NOT working correctly:

    if not "%MANUAL_PATHS%"=="%AUTO_PATHS%" (
        echo     ✗ Path count mismatch
    )

    if "%AUTO_HAS_CONSTRAINTS%"=="0" (
        echo     ✗ Auto mode did not collect constraints
    )

    echo.
    echo   ⚠️  DO NOT REMOVE manual PathUtils calls yet
    echo.
    echo   Troubleshooting steps:
    echo     1. Check auto-mode-output.log for errors
    echo     2. Verify Galette agent is loaded: grep "javaagent" auto-mode-output.log
    echo     3. Check for instrumentation errors: grep "Instrumented Java" auto-mode-output.log
    echo     4. Ensure galette-maven-plugin is configured correctly in pom.xml
    echo     5. Try running: mvn process-test-resources -pl knarr-runtime
    echo.

    echo VERIFICATION FAILED > verification-status.txt
)

echo ================================================================================
echo.
echo Results saved:
echo   - execution_paths_manual_baseline.json     (Manual mode results)
echo   - execution_paths_auto_instrumentation.json (Auto mode results)
echo   - manual-mode-output.log                    (Manual mode logs)
echo   - auto-mode-output.log                      (Auto mode logs)
echo   - verification-status.txt                   (Pass/Fail status)
echo.

if "%VERIFICATION_PASSED%"=="1" (
    echo Ready to proceed with code modification!
    exit /b 0
) else (
    echo Fix issues before proceeding.
    exit /b 1
)
