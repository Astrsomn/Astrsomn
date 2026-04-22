@echo off
setlocal enabledelayedexpansion

REM Script directory: deploy/windows/run
set "SCRIPT_DIR=%~dp0"
set "JAR_DIR=%SCRIPT_DIR%..\..\..\astrsomn-server\target"
set "RUNTIME_DIR=%SCRIPT_DIR%.runtime"
set "PID_FILE=%RUNTIME_DIR%\astrsomn-server.pid"
set "LOG_FILE=%RUNTIME_DIR%\astrsomn-server.log"
set "ERR_LOG_FILE=%RUNTIME_DIR%\astrsomn-server.err.log"

if not exist "%JAR_DIR%" (
  echo ERROR: Jar directory not found: %JAR_DIR%
  echo Please run ..\build.bat first.
  pause
  exit /b 1
)

if not exist "%RUNTIME_DIR%" mkdir "%RUNTIME_DIR%"

if exist "%PID_FILE%" (
  set /p EXIST_PID=<"%PID_FILE%"
  powershell -NoProfile -Command "if (Get-Process -Id !EXIST_PID! -ErrorAction SilentlyContinue) { exit 0 } else { exit 1 }"
  if not errorlevel 1 (
    echo ERROR: Service already running with PID !EXIST_PID!
    echo Use stop-jar.bat first if you need to restart.
    pause
    exit /b 1
  )
  del /q "%PID_FILE%" 2>nul
)

set "JAR_FILE="
for /f "delims=" %%F in ('dir /b /a:-d /o:-d "%JAR_DIR%\*.jar" 2^>nul') do (
  set "JAR_FILE=%%F"
  goto :jar_found
)

:jar_found
if "%JAR_FILE%"=="" (
  echo ERROR: No jar file found under: %JAR_DIR%
  echo Please run ..\build.bat first.
  pause
  exit /b 1
)

set "JAR_PATH=%JAR_DIR%\%JAR_FILE%"
echo Jar directory: %JAR_DIR%
echo Jar file: %JAR_FILE%
echo Starting in background: %JAR_PATH%
echo Log file: %LOG_FILE%
echo Error log: %ERR_LOG_FILE%

jar tf "%JAR_PATH%" | findstr /i "BOOT-INF/classes/static/index.html" >nul
if errorlevel 1 (
  echo ERROR: current jar does not include frontend static files.
  echo Please run deploy\windows\build.bat to rebuild UI + server jar.
  pause
  exit /b 1
)

if not exist "%LOG_FILE%" type nul > "%LOG_FILE%"
if not exist "%ERR_LOG_FILE%" type nul > "%ERR_LOG_FILE%"

powershell -NoProfile -Command "$p = Start-Process -FilePath 'java' -ArgumentList @('-jar','%JAR_PATH%') -RedirectStandardOutput '%LOG_FILE%' -RedirectStandardError '%ERR_LOG_FILE%' -WindowStyle Hidden -PassThru; $p.Id | Out-File -FilePath '%PID_FILE%' -Encoding ascii -NoNewline"
if errorlevel 1 (
  echo ERROR: failed to start java process.
  pause
  exit /b 1
)

set /p NEW_PID=<"%PID_FILE%"
timeout /t 2 /nobreak >nul
powershell -NoProfile -Command "if (Get-Process -Id %NEW_PID% -ErrorAction SilentlyContinue) { exit 0 } else { exit 1 }"
if errorlevel 1 (
  echo ERROR: java process exited quickly. Check log:
  echo %LOG_FILE%
  echo %ERR_LOG_FILE%
  echo.
  type "%LOG_FILE%"
  if exist "%ERR_LOG_FILE%" (
    echo.
    echo ---- STDERR ----
    type "%ERR_LOG_FILE%"
  )
  pause
  exit /b 1
)

echo Started. PID: %NEW_PID%
echo.
echo Showing live logs (Ctrl+C to exit log view, service keeps running)...
echo ------------------------------------------------------------
powershell -NoProfile -Command "Get-Content -Path '%LOG_FILE%' -Tail 80 -Wait -ErrorAction Stop"
echo.
echo Log view exited.
pause
exit /b 0
