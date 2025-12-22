@echo off
setlocal enabledelayedexpansion

set "INSTRUMENTED_JAVA=C:\Users\10239\galette-vitruv\knarr-runtime\target\galette\java\bin\java.exe"
set "GALETTE_AGENT=C:\Users\10239\galette-vitruv\galette-agent\target\galette-agent-1.0.0-SNAPSHOT.jar"
set "EMF_ECORE=C:\Users\10239\.m2\repository\org\eclipse\emf\org.eclipse.emf.ecore\2.38.0\org.eclipse.emf.ecore-2.38.0.jar"
set "EMF_COMMON=C:\Users\10239\.m2\repository\org\eclipse\emf\org.eclipse.emf.common\2.40.0\org.eclipse.emf.common-2.40.0.jar"
set "EMF_XMI=C:\Users\10239\.m2\repository\org\eclipse\emf\org.eclipse.emf.ecore.xmi\2.16.0\org.eclipse.emf.ecore.xmi-2.16.0.jar"

REM Read classpath
set /p "MAVEN_CP="<cp-test.txt
set "CP=target\classes;target\test-classes;%MAVEN_CP%"

echo ================================================================================
echo Step 1: Compiling SimpleAutomaticTest...
echo ================================================================================

javac -cp "%CP%" ^
  src\test\java\edu\neu\ccs\prl\galette\concolic\knarr\runtime\SimpleAutomaticTest.java ^
  -d target\test-classes

if errorlevel 1 (
    echo ERROR: Compilation failed
    exit /b 1
)

echo Compilation successful!
echo.

echo ================================================================================
echo Step 2: Running with AUTOMATIC instrumentation...
echo ================================================================================
echo.

"%INSTRUMENTED_JAVA%" ^
  -cp "%CP%" ^
  -Xbootclasspath/a:"%GALETTE_AGENT%;%EMF_ECORE%;%EMF_COMMON%;%EMF_XMI%" ^
  -javaagent:"%GALETTE_AGENT%" ^
  -Dgalette.debug=false ^
  edu.neu.ccs.prl.galette.concolic.knarr.runtime.SimpleAutomaticTest

set "EXIT_CODE=%ERRORLEVEL%"

echo.
echo ================================================================================
echo Test completed with exit code: %EXIT_CODE%
echo ================================================================================

if %EXIT_CODE%==0 (
    echo ✓ SUCCESS: Automatic constraint collection is working!
) else (
    echo ✗ FAILURE: Automatic constraint collection not working.
)

exit /b %EXIT_CODE%
