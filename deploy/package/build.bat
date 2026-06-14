@echo off
setlocal enabledelayedexpansion

REM ============================================================================
REM Astrsomn Multi-Platform Packaging Build Script (Windows)
REM ============================================================================
REM Produces three platform packages:
REM   - astrsomn-<version>-linux.tar.gz
REM   - astrsomn-<version>-macos.tar.gz
REM   - astrsomn-<version>-windows.zip
REM ============================================================================

REM Resolve project root from this script location: deploy/package
set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..\..") do set "PROJECT_ROOT=%%~fI"

set "UI_DIR=%PROJECT_ROOT%\astrsomn-ui"
set "SERVER_DIR=%PROJECT_ROOT%\astrsomn-server"
set "SERVER_TARGET=%SERVER_DIR%\target"
set "OUTPUT_DIR=%SCRIPT_DIR%output"

REM Extract version from root pom.xml (first <version> after filtering xml/model)
set "VERSION="
for /f "tokens=*" %%i in ('findstr /c:"<version>" "%PROJECT_ROOT%\pom.xml" ^| findstr /v "xml model"') do (
    set "V_LINE=%%i"
    goto :version_found
)
:version_found
for /f "tokens=2 delims=<>" %%a in ("!V_LINE!") do set "VERSION=%%a"
if "!VERSION!"=="" set "VERSION=0.0.0-UNKNOWN"
set "ARTIFACT=astrsomn-%VERSION%"

echo ============================================
echo  Astrsomn Multi-Platform Build
echo  Version: %VERSION%
echo ============================================
echo.

REM ============================================================
REM Step 1: Check prerequisites
REM ============================================================
echo [1/5] Checking prerequisites...

where node >nul 2>nul
if errorlevel 1 (
    echo ERROR: node not found in PATH. Install Node.js.
    pause
    exit /b 1
)

where npm >nul 2>nul
if errorlevel 1 (
    echo ERROR: npm not found in PATH. Install Node.js.
    pause
    exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
    echo ERROR: mvn not found in PATH. Install Maven.
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('node --version') do echo   node: %%i
for /f "tokens=*" %%i in ('npm --version')  do echo   npm:  %%i
for /f "tokens=*" %%i in ('mvn --version 2^>^&1 ^| findstr /i "Apache"') do echo   mvn:  %%i
echo.

REM ============================================================
REM Step 2: Build frontend
REM ============================================================
echo [2/5] Building frontend UI...

pushd "%UI_DIR%" || (
    echo ERROR: UI directory not found: %UI_DIR%
    exit /b 1
)
call npm install --silent
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

REM Ensure static resources exist — if Vite outDir is misconfigured, copy from dist/ as fallback
set "STATIC_DIR=%SERVER_DIR%\src\main\resources\static"
if not exist "%STATIC_DIR%\index.html" (
    echo   Static dir empty, copying from dist/ fallback...
    if not exist "%STATIC_DIR%" mkdir "%STATIC_DIR%"
    xcopy "%UI_DIR%\dist\*" "%STATIC_DIR%\" /e /i /h /y >nul
)
echo   Frontend build complete.
echo.

REM ============================================================
REM Step 3: Build server fat JAR
REM ============================================================
echo [3/5] Building server JAR...

pushd "%PROJECT_ROOT%" || (
    echo ERROR: Project root not found: %PROJECT_ROOT%
    exit /b 1
)
call mvn clean package -pl astrsomn-server -am -DskipTests -q
if errorlevel 1 (
    echo ERROR: Maven package failed.
    popd
    exit /b 1
)
popd
echo   Server build complete.
echo.

REM ============================================================
REM Step 4: Verify artifacts
REM ============================================================
echo [4/5] Verifying artifacts...

set "JAR_FILE="
for /f "delims=" %%F in ('dir /b /a:-d /o:-d "%SERVER_TARGET%\*.jar" 2^>nul ^| findstr /v "sources javadoc"') do (
    set "JAR_FILE=%SERVER_TARGET%\%%F"
    goto :jar_found
)
:jar_found

if "%JAR_FILE%"=="" (
    echo ERROR: No server JAR found in %SERVER_TARGET%
    pause
    exit /b 1
)

REM Verify JAR contains frontend static files
jar tf "%JAR_FILE%" 2>nul | findstr /i "BOOT-INF/classes/static/index.html" >nul
if errorlevel 1 (
    echo WARNING: JAR does not contain frontend static files.
    echo   The Vite build may not have run correctly.
    echo   Continuing anyway...
) else (
    echo   JAR:  %JAR_FILE%
    echo   UI:   embedded ^(verified^)
)

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
echo.

REM ============================================================
REM Step 5: Assemble platform packages
REM ============================================================
echo [5/5] Assembling platform packages...

set "STAGE_DIR=%OUTPUT_DIR%\astrsomn"
if exist "%STAGE_DIR%" rmdir /s /q "%STAGE_DIR%"
mkdir "%STAGE_DIR%\config" "%STAGE_DIR%\bin" "%STAGE_DIR%\database"

REM --- Common files (same for all platforms) ---
copy "%JAR_FILE%" "%STAGE_DIR%\astrsomn-server.jar" >nul
copy "%SCRIPT_DIR%config\application.yml" "%STAGE_DIR%\config\" >nul
copy "%SCRIPT_DIR%README.md" "%STAGE_DIR%\" >nul
if exist "%SCRIPT_DIR%README.en.md" copy "%SCRIPT_DIR%README.en.md" "%STAGE_DIR%\" >nul
copy "%SERVER_DIR%\src\main\resources\db\migration\*.sql" "%STAGE_DIR%\database\" >nul
echo %VERSION%> "%STAGE_DIR%\VERSION"

REM --- assemble for each platform ---
call :assemble linux tar.gz
call :assemble macos tar.gz
call :assemble windows zip

rmdir /s /q "%STAGE_DIR%"
echo.
echo ============================================
echo  Build completed successfully!
echo ============================================
echo  Output: %OUTPUT_DIR%
dir /b "%OUTPUT_DIR%\*.tar.gz" "%OUTPUT_DIR%\*.zip" 2>nul
pause
exit /b 0

REM ============================================================
REM assemble_package subroutine
REM ============================================================
:assemble
set "PLATFORM=%1"
set "EXT=%2"
set "ARCHIVE=astrsomn-%PLATFORM%.%EXT%"

REM Clean + copy platform scripts
if exist "%STAGE_DIR%\bin" rmdir /s /q "%STAGE_DIR%\bin"
mkdir "%STAGE_DIR%\bin"

copy "%SCRIPT_DIR%bin\.env" "%STAGE_DIR%\bin\" >nul
if "%PLATFORM%"=="windows" (
    copy "%SCRIPT_DIR%bin\start.bat" "%STAGE_DIR%\bin\" >nul
    copy "%SCRIPT_DIR%bin\stop.bat" "%STAGE_DIR%\bin\" >nul
) else (
    copy "%SCRIPT_DIR%bin\start.sh" "%STAGE_DIR%\bin\" >nul
    copy "%SCRIPT_DIR%bin\stop.sh" "%STAGE_DIR%\bin\" >nul
)

pushd "%OUTPUT_DIR%"
if exist "%ARCHIVE%" del /q "%ARCHIVE%"

if "%EXT%"=="zip" (
    powershell -NoProfile -Command "Compress-Archive -Path 'astrsomn' -DestinationPath '%ARCHIVE%' -Force"
) else (
    tar -czf "%ARCHIVE%" "astrsomn"
)
popd

echo   %ARCHIVE%
exit /b
