@echo off
setlocal enabledelayedexpansion

REM Resolve project root from this script location: deploy/windows
set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%\..\..") do set "PROJECT_ROOT=%%~fI"

set "UI_DIR=%PROJECT_ROOT%\astrsomn-ui"
set "SERVER_DIR=%PROJECT_ROOT%\astrsomn-server"
set "SERVER_RES_STATIC=%SERVER_DIR%\src\main\resources\static"
set "OUTPUT_DIR=%SCRIPT_DIR%output"
set "SERVER_TARGET_DIR=%SERVER_DIR%\target"

where npm >nul 2>nul
if errorlevel 1 (
  echo ERROR: npm not found in PATH.
  echo Please install Node.js and make sure npm is available.
  pause
  exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
  echo ERROR: mvn not found in PATH.
  echo Maven is required to package astrsomn-server.
  echo Install Maven and add its bin directory to PATH, then retry.
  pause
  exit /b 1
)

echo [1/4] Build UI dist...
if not exist "%UI_DIR%" (
  echo ERROR: UI directory not found: %UI_DIR%
  exit /b 1
)

pushd "%UI_DIR%" || exit /b 1
call npm install
if errorlevel 1 (
  echo ERROR: npm install failed.
  popd
  exit /b 1
)

call npm run build
if errorlevel 1 (
  echo ERROR: npm run build failed.
  popd
  exit /b 1
)
popd

echo [2/4] Copy UI dist to server resources/static...
if not exist "%UI_DIR%\dist" (
  echo ERROR: dist directory not found: %UI_DIR%\dist
  exit /b 1
)
if not exist "%SERVER_RES_STATIC%" mkdir "%SERVER_RES_STATIC%"

REM Clean previous static assets to avoid stale files
rmdir /s /q "%SERVER_RES_STATIC%" 2>nul
mkdir "%SERVER_RES_STATIC%"
xcopy "%UI_DIR%\dist\*" "%SERVER_RES_STATIC%\" /e /i /h /y >nul
if errorlevel 1 (
  echo ERROR: copy dist to resources/static failed.
  exit /b 1
)

echo [3/4] Build server jar...
pushd "%PROJECT_ROOT%" || exit /b 1
call mvn -pl astrsomn-server -am clean package -DskipTests
if errorlevel 1 (
  echo ERROR: maven package failed.
  popd
  pause
  exit /b 1
)
popd

echo [4/4] Collect server jar to deploy output...
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
if not exist "%SERVER_TARGET_DIR%" (
  echo ERROR: server target directory not found: %SERVER_TARGET_DIR%
  pause
  exit /b 1
)

dir /b "%SERVER_TARGET_DIR%\astrsomn-server*.jar" >nul 2>nul
if errorlevel 1 (
  echo ERROR: no server jar generated in %SERVER_TARGET_DIR%
  echo Please check Maven output above for details.
  pause
  exit /b 1
)

del /q "%OUTPUT_DIR%\astrsomn-server*.jar" 2>nul
xcopy "%SERVER_TARGET_DIR%\astrsomn-server*.jar" "%OUTPUT_DIR%\" /y >nul
if errorlevel 1 (
  echo ERROR: failed to copy jar to %OUTPUT_DIR%
  pause
  exit /b 1
)

echo.
echo Build completed.
echo - Frontend dist copied to: %SERVER_RES_STATIC%
echo - Server jar output folder: %OUTPUT_DIR%
dir /b "%OUTPUT_DIR%\astrsomn-server*.jar"
pause
exit /b 0
