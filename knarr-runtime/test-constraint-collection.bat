@echo off
echo ========================================
echo Testing Constraint Collection Fix
echo ========================================

REM Compile first
echo.
echo [1/2] Compiling...
call mvn clean package -DskipTests -Dcheckstyle.skip=true > build.log 2>&1
if errorlevel 1 (
    echo ERROR: Compilation failed. Check build.log
    exit /b 1
)
echo ✓ Compilation successful

REM Run the multivar test
echo.
echo [2/2] Running multi-variable path exploration test...
echo.

set JAVA_OPTS=-Duse.z3.solver=true -Dz3.solver.debug=true -Dskip.instrumentation.check=true

java %JAVA_OPTS% -cp "target/classes;target/test-classes;%USERPROFILE%/.m2/repository/com/microsoft/z3/z3/4.8.7/z3-4.8.7.jar;target/dependency/*" ^
     edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration

echo.
echo ========================================
echo Checking results...
echo ========================================

REM Check if results file was generated
if exist execution_paths_automatic.json (
    echo ✓ Found execution_paths_automatic.json

    REM Count paths with constraints
    findstr /C:"numConstraints" execution_paths_automatic.json | findstr /V /C:"0" > nul
    if errorlevel 1 (
        echo ❌ FAILED: Most paths have 0 constraints!
        echo    The fix may not be working correctly.
    ) else (
        echo ✓ SUCCESS: Multiple paths have constraints collected!
        echo.
        echo Constraint count per path:
        findstr /C:"numConstraints" execution_paths_automatic.json
    )
) else (
    echo ❌ No results file generated
)

echo.
echo Test complete. Check execution_paths_automatic.json for details.
