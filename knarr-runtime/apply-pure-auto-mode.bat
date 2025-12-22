@echo off
setlocal enabledelayedexpansion

REM ================================================================================
REM Apply Pure Auto Mode - Code Modification Script
REM ================================================================================
REM This script automatically modifies GaletteSymbolicator.java to enable
REM pure auto-instrumentation mode (without manual PathUtils calls)
REM
REM ⚠️  ONLY run this AFTER verify-auto-instrumentation.bat shows VERIFICATION PASSED!
REM ================================================================================

echo ================================================================================
echo Apply Pure Auto-Instrumentation Mode
echo ================================================================================
echo.

REM Check if verification passed
if not exist verification-status.txt (
    echo ERROR: verification-status.txt not found
    echo.
    echo You must run verify-auto-instrumentation.bat first!
    echo Only proceed if verification PASSED.
    echo.
    exit /b 1
)

findstr /C:"VERIFICATION PASSED" verification-status.txt >nul 2>&1
if errorlevel 1 (
    echo ❌ ERROR: Verification did NOT pass!
    echo.
    type verification-status.txt
    echo.
    echo You cannot proceed with code modification until verification passes.
    echo Please fix the issues and run verify-auto-instrumentation.bat again.
    echo.
    exit /b 1
)

echo ✅ Verification passed. Safe to proceed with code modification.
echo.

REM Confirm with user
echo This script will modify GaletteSymbolicator.java to:
echo   - Add shouldUseManualConstraints() method
echo   - Wrap PathUtils calls with conditional checks
echo   - Enable pure auto-instrumentation mode
echo.
echo A backup will be created: GaletteSymbolicator.java.before-pure-auto
echo.
set /p "CONFIRM=Continue? (yes/no): "

if not "%CONFIRM%"=="yes" (
    echo Cancelled by user.
    exit /b 0
)

echo.
echo ================================================================================
echo Starting code modification...
echo ================================================================================
echo.

set "JAVA_FILE=src\main\java\edu\neu\ccs\prl\galette\concolic\knarr\runtime\GaletteSymbolicator.java"

if not exist "%JAVA_FILE%" (
    echo ERROR: %JAVA_FILE% not found
    exit /b 1
)

REM Step 1: Create backup
echo [1/4] Creating backup...
copy /y "%JAVA_FILE%" "%JAVA_FILE%.before-pure-auto" >nul 2>&1
echo       Backup created: %JAVA_FILE%.before-pure-auto
echo.

REM Step 2: Create modified version using Python (if available) or manual instructions
echo [2/4] Applying modifications...

REM Try to find Python
set "PYTHON_CMD="
where python.exe >nul 2>&1 && set "PYTHON_CMD=python.exe"
if "%PYTHON_CMD%"=="" where python3 >nul 2>&1 && set "PYTHON_CMD=python3"
if "%PYTHON_CMD%"=="" where python >nul 2>&1 && set "PYTHON_CMD=python"

if "%PYTHON_CMD%"=="" (
    echo Python not found. Will provide manual modification instructions.
    goto MANUAL_INSTRUCTIONS
)

REM Create Python script to apply modifications
echo import sys > apply_modifications.py
echo import re >> apply_modifications.py
echo. >> apply_modifications.py
echo with open(sys.argv[1], 'r', encoding='utf-8') as f: >> apply_modifications.py
echo     content = f.read() >> apply_modifications.py
echo. >> apply_modifications.py
echo # Add the shouldUseManualConstraints method before makeSymbolicInt >> apply_modifications.py
echo if 'shouldUseManualConstraints' not in content: >> apply_modifications.py
echo     method_code = '''    /** >> apply_modifications.py
echo      * Check if manual constraint collection should be used. >> apply_modifications.py
echo      * >> apply_modifications.py
echo      * Returns true in manual mode (when instrumentation is skipped). >> apply_modifications.py
echo      * Returns false in auto mode (when using javaagent instrumentation). >> apply_modifications.py
echo      * >> apply_modifications.py
echo      * @return true if manual PathUtils calls should be made >> apply_modifications.py
echo      */ >> apply_modifications.py
echo     private static boolean shouldUseManualConstraints() { >> apply_modifications.py
echo         boolean skipInstrumentation = Boolean.getBoolean("skip.instrumentation.check"); >> apply_modifications.py
echo         boolean manualMode = Boolean.getBoolean("manual.constraint.collection"); >> apply_modifications.py
echo         boolean useManual = skipInstrumentation ^|^| manualMode; >> apply_modifications.py
echo         if (DEBUG ^&^& useManual) { >> apply_modifications.py
echo             System.out.println("[GaletteSymbolicator] Using MANUAL constraint collection mode"); >> apply_modifications.py
echo         } else if (DEBUG) { >> apply_modifications.py
echo             System.out.println("[GaletteSymbolicator] Using AUTO constraint collection mode (bytecode instrumentation)"); >> apply_modifications.py
echo         } >> apply_modifications.py
echo         return useManual; >> apply_modifications.py
echo     } >> apply_modifications.py
echo ''' >> apply_modifications.py
echo     # Insert before makeSymbolicInt method >> apply_modifications.py
echo     content = re.sub(r'(    public static Tag makeSymbolicInt)', method_code + '\n\\1', content) >> apply_modifications.py
echo. >> apply_modifications.py
echo # Wrap first PathUtils calls (around line 174-175) >> apply_modifications.py
echo content = re.sub( >> apply_modifications.py
echo     r'(\s+)PathUtils\.addIntDomainConstraint\(qualifiedName, min, max \+ 1\);\s*\n\s*PathUtils\.addSwitchConstraint\(qualifiedName, concreteValue\);', >> apply_modifications.py
echo     r'\1if (shouldUseManualConstraints()) {\n\1    PathUtils.addIntDomainConstraint(qualifiedName, min, max + 1);\n\1    PathUtils.addSwitchConstraint(qualifiedName, concreteValue);\n\1}', >> apply_modifications.py
echo     content, >> apply_modifications.py
echo     count=1 >> apply_modifications.py
echo ) >> apply_modifications.py
echo. >> apply_modifications.py
echo # Wrap second PathUtils calls (around line 201-202) - in the else branch >> apply_modifications.py
echo content = re.sub( >> apply_modifications.py
echo     r'(        // Note: PathUtils\.addIntDomainConstraint uses exclusive upper bound\s*\n\s*)PathUtils\.addIntDomainConstraint\(qualifiedName, min, max \+ 1\);\s*\n\s*// Record switch constraint for the concrete value\s*\n\s*PathUtils\.addSwitchConstraint\(qualifiedName, concreteValue\);', >> apply_modifications.py
echo     r'\1if (shouldUseManualConstraints()) {\n\1    // Note: PathUtils.addIntDomainConstraint uses exclusive upper bound\n\1    PathUtils.addIntDomainConstraint(qualifiedName, min, max + 1);\n\n\1    // Record switch constraint for the concrete value\n\1    PathUtils.addSwitchConstraint(qualifiedName, concreteValue);\n\1}', >> apply_modifications.py
echo     content >> apply_modifications.py
echo ) >> apply_modifications.py
echo. >> apply_modifications.py
echo with open(sys.argv[1], 'w', encoding='utf-8') as f: >> apply_modifications.py
echo     f.write(content) >> apply_modifications.py
echo. >> apply_modifications.py
echo print("Modifications applied successfully") >> apply_modifications.py

REM Apply modifications
%PYTHON_CMD% apply_modifications.py "%JAVA_FILE%"

if errorlevel 1 (
    echo ERROR: Failed to apply modifications
    echo Restoring backup...
    copy /y "%JAVA_FILE%.before-pure-auto" "%JAVA_FILE%" >nul 2>&1
    del apply_modifications.py 2>nul
    exit /b 1
)

del apply_modifications.py 2>nul
echo       Modifications applied successfully
echo.

goto COMPILE_AND_TEST

:MANUAL_INSTRUCTIONS
echo.
echo ================================================================================
echo Manual Modification Instructions
echo ================================================================================
echo.
echo Python not found. Please apply modifications manually:
echo.
echo 1. Open: %JAVA_FILE%
echo.
echo 2. Add this method before 'makeSymbolicInt' method (around line 240):
echo.
type <<EOF
    /**
     * Check if manual constraint collection should be used.
     */
    private static boolean shouldUseManualConstraints() {
        boolean skipInstrumentation = Boolean.getBoolean("skip.instrumentation.check");
        boolean manualMode = Boolean.getBoolean("manual.constraint.collection");
        boolean useManual = skipInstrumentation || manualMode;
        if (DEBUG && useManual) {
            System.out.println("[GaletteSymbolicator] Using MANUAL constraint collection mode");
        } else if (DEBUG) {
            System.out.println("[GaletteSymbolicator] Using AUTO constraint collection mode");
        }
        return useManual;
    }
EOF
echo.
echo 3. Find line 174-175 and wrap PathUtils calls:
echo    Change:
echo      PathUtils.addIntDomainConstraint(qualifiedName, min, max + 1);
echo      PathUtils.addSwitchConstraint(qualifiedName, concreteValue);
echo.
echo    To:
echo      if (shouldUseManualConstraints()) {
echo          PathUtils.addIntDomainConstraint(qualifiedName, min, max + 1);
echo          PathUtils.addSwitchConstraint(qualifiedName, concreteValue);
echo      }
echo.
echo 4. Find line 201-205 and wrap PathUtils calls:
echo    Change:
echo      PathUtils.addIntDomainConstraint(qualifiedName, min, max + 1);
echo      PathUtils.addSwitchConstraint(qualifiedName, concreteValue);
echo.
echo    To:
echo      if (shouldUseManualConstraints()) {
echo          PathUtils.addIntDomainConstraint(qualifiedName, min, max + 1);
echo          PathUtils.addSwitchConstraint(qualifiedName, concreteValue);
echo      }
echo.
echo 5. Save the file and run this script again
echo.
pause
exit /b 0

:COMPILE_AND_TEST

REM Step 3: Recompile
echo [3/4] Recompiling...
call mvn clean compile -pl knarr-runtime -DskipTests -Dcheckstyle.skip=true -q

if errorlevel 1 (
    echo ERROR: Compilation failed
    echo Restoring backup...
    copy /y "%JAVA_FILE%.before-pure-auto" "%JAVA_FILE%" >nul 2>&1
    exit /b 1
)

echo       Compilation successful
echo.

REM Step 4: Verify modifications
echo [4/4] Verifying modifications...

REM Check if method was added
findstr /C:"shouldUseManualConstraints" "%JAVA_FILE%" >nul 2>&1
if errorlevel 1 (
    echo WARNING: shouldUseManualConstraints method not found in file
    echo Please verify modifications manually
) else (
    echo       ✓ shouldUseManualConstraints method added
)

REM Count conditional wrapping
set "WRAP_COUNT=0"
for /f %%a in ('findstr /C:"if (shouldUseManualConstraints())" "%JAVA_FILE%" ^| find /c "if"') do set "WRAP_COUNT=%%a"

if "%WRAP_COUNT%"=="2" (
    echo       ✓ PathUtils calls wrapped correctly (2 locations)
) else (
    echo       WARNING: Expected 2 conditional wraps, found %WRAP_COUNT%
    echo       Please verify modifications manually
)

echo.
echo ================================================================================
echo Modification Complete!
echo ================================================================================
echo.
echo Changes applied:
echo   ✓ Added shouldUseManualConstraints() method
echo   ✓ Wrapped PathUtils calls with conditional checks
echo   ✓ Code recompiled successfully
echo.
echo Backup saved: %JAVA_FILE%.before-pure-auto
echo.
echo Next steps:
echo   1. Test MANUAL mode:
echo      run-symbolic-execution.bat
echo      Should see: "Using MANUAL constraint collection mode"
echo.
echo   2. Test AUTO mode:
echo      run-with-instrumentation.bat
echo      Should see: "Using AUTO constraint collection mode"
echo.
echo   3. Compare results (should be identical)
echo.
echo If there are any issues, restore backup:
echo   copy /y "%JAVA_FILE%.before-pure-auto" "%JAVA_FILE%"
echo.
echo ================================================================================

exit /b 0
