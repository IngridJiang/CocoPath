@echo off
REM Batch script to run Galette/Vitruvius Symbolic Execution
REM Usage: run.cmd

echo ================================================================================
echo GALETTE/KNARR SYMBOLIC EXECUTION WITH VITRUVIUS FRAMEWORK
echo ================================================================================
echo.

echo [1/3] Cleaning previous outputs...
if exist galette-output-* rmdir /s /q galette-output-*
if exist execution_paths.json del /q execution_paths.json
echo       Done.
echo.

echo [2/3] Running symbolic execution...
call mvn exec:java -Dcheckstyle.skip=true
set MVN_EXIT=%ERRORLEVEL%

if %MVN_EXIT% neq 0 (
    echo.
    echo WARNING: Maven execution had errors, but continuing to generate visualizations...
)

echo.
echo [3/3] Generating visualizations...

if exist execution_paths.json (
    python visualize_results.py
    if %ERRORLEVEL% equ 0 (
        echo   SUCCESS: Results visualizations generated
    ) else (
        echo   ERROR: Failed to generate results visualizations
    )

    python visualize_workflow.py
    if %ERRORLEVEL% equ 0 (
        echo   SUCCESS: Workflow diagram generated
    ) else (
        echo   ERROR: Failed to generate workflow diagram
    )
) else (
    echo   ERROR: No execution_paths.json found - skipping visualizations
    if %MVN_EXIT% neq 0 (
        echo.
        echo ERROR: Symbolic execution failed and no data was generated!
        exit /b 1
    )
)

echo.
echo ================================================================================
echo SUCCESS! Symbolic execution completed.
echo ================================================================================
echo.
echo Generated files:
echo   - execution_paths.json       (JSON data)
echo   - execution_summary.txt      (Text summary)
echo   - execution_tree.png         (Execution tree diagram)
echo   - performance_chart.png      (Performance charts)
echo   - workflow_diagram.png       (Workflow diagram)
echo   - galette-output-0/ to galette-output-4/ (Model outputs)
echo.
