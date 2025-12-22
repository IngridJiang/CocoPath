@echo off
setlocal enabledelayedexpansion

echo Testing automatic instrumentation mode after removing manual PathUtils calls...
echo.

set "INSTRUMENTED_JAVA=C:\Users\10239\galette-vitruv\knarr-runtime\target\galette\java\bin\java.exe"
set "GALETTE_AGENT=C:\Users\10239\galette-vitruv\galette-agent\target\galette-agent-1.0.0-SNAPSHOT.jar"

REM Read classpath (with test dependencies for EMF)
set /p "MAVEN_CP="<cp-test.txt
set "CP=target\classes;target\test-classes;%MAVEN_CP%"

REM Create cache directory
if not exist "target\galette\cache" mkdir "target\galette\cache"

echo Running with AUTOMATIC instrumentation (no manual PathUtils calls)...
echo.

"%INSTRUMENTED_JAVA%" ^
  -cp "%CP%" ^
  -Xbootclasspath/a:"%GALETTE_AGENT%" ^
  -javaagent:"%GALETTE_AGENT%" ^
  -Dgalette.cache="target\galette\cache" ^
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
  edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExploration

echo.
echo ================================================================================
echo Test complete. Checking results...
echo ================================================================================
echo.

if exist execution_paths_automatic.json (
    echo SUCCESS: execution_paths_automatic.json created
    echo.
    echo Path count:
    find /c "pathId" execution_paths_automatic.json
    echo.
    echo Constraint counts:
    findstr /C:"numConstraints" execution_paths_automatic.json
) else (
    echo ERROR: execution_paths_automatic.json not found
)
