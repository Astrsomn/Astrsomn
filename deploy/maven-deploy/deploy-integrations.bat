@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Astrsomn RELEASE Deploy Script
REM Deploy integrations modules to Maven Central (Release)
REM For SNAPSHOT: use deploy-snapshot.bat instead
REM ========================================

echo.
echo ========================================
echo  Astrsomn Integrations RELEASE Deploy
echo ========================================
echo  Target: Maven Central Repository
echo  Profile: ossrh (central-publishing + GPG)
echo  WARNING: Version must NOT be SNAPSHOT!
echo ========================================
echo.

pushd "%~dp0\..\.."
set "PROJECT_ROOT=%CD%"
popd

set "MODULES_DIR=%PROJECT_ROOT%\astrsomn-integrations"

REM Deploy order matters: system-starter & vector-starter before runtime-starter
set MODULES= astrsomn-system-starter astrsomn-vector-starter astrsomn-internal-storage astrsomn-runtime-starter astrsomn-workflow-starter

echo [1/2] Installing all modules to local repository...
cd /d "%PROJECT_ROOT%"
call mvn clean install -DskipTests
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to install dependencies!
    echo.
    echo Press any key to exit...
    pause > nul
    exit /b 1
)

echo.
echo [2/2] Deploying integrations modules to Maven Central...
echo.

for %%m in (%MODULES%) do (
    echo ----------------------------------------
    echo Deploying module: %%m
    echo ----------------------------------------
    cd /d "%MODULES_DIR%\%%m"
    call mvn deploy -X -DskipTests -Possrh
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to deploy module %%m!
        echo.
        echo Press any key to exit...
        pause > nul
        exit /b 1
    )
    echo.
)

echo.
echo ========================================
echo  Integrations RELEASE deployment completed!
echo  Next: log in to https://central.sonatype.com to publish staged deployments
echo ========================================
echo.
echo Press any key to exit...
pause > nul

endlocal
