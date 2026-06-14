@echo off
setlocal

set "APP_DIR=%~dp0.."

REM ---- Read env config ----
if exist "%~dp0env" for /f "usebackq eol=# delims== tokens=1,*" %%a in ("%~dp0env") do set "%%a=%%b"

REM ---- Resolve Java ----
if defined JAVA_HOME (
    set "JAVA=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA=java"
)

pushd "%APP_DIR%" 2>nul || (
    echo [ERROR] Cannot access app directory
    pause
    exit /b 1
)

if not exist "astrsomn-server.jar" (
    echo [ERROR] astrsomn-server.jar not found
    popd
    pause
    exit /b 1
)

if not exist "config\application.yml" (
    echo [ERROR] config\application.yml not found
    popd
    pause
    exit /b 1
)

echo ============================================
echo   Astrsomn AI Gateway
echo   http://localhost:4481
echo ============================================
echo.
echo Close this window to stop the server.
echo.

"%JAVA%" %JAVA_OPTS% -jar astrsomn-server.jar

popd
pause
