@echo off
setlocal enabledelayedexpansion

REM Script directory: deploy/windows/run
set "SCRIPT_DIR=%~dp0"
set "RUNTIME_DIR=%SCRIPT_DIR%.runtime"
set "PID_FILE=%RUNTIME_DIR%\astrsomn-server.pid"

if not exist "%PID_FILE%" (
  echo No PID file found. Service may not be running.
  pause
  exit /b 0
)

set /p PID=<"%PID_FILE%"
if "%PID%"=="" (
  echo PID file is empty. Cleaning stale PID file.
  del /q "%PID_FILE%" 2>nul
  pause
  exit /b 0
)

powershell -NoProfile -Command "if (Get-Process -Id %PID% -ErrorAction SilentlyContinue) { exit 0 } else { exit 1 }"
if errorlevel 1 (
  echo Java process not found for PID %PID%. Cleaning stale PID file.
  del /q "%PID_FILE%" 2>nul
  pause
  exit /b 0
)

echo Stopping PID %PID% ...
taskkill /pid %PID% /t /f >nul
if errorlevel 1 (
  echo ERROR: failed to stop PID %PID%.
  pause
  exit /b 1
)

del /q "%PID_FILE%" 2>nul
echo Stopped.
pause
exit /b 0
