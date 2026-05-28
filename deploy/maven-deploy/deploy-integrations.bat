@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Astrsomn Maven Deploy Script
REM Deploy integrations modules to Maven Central Repository
REM ========================================

echo.
echo ========================================
echo  Astrsomn Integrations Deploy Script
echo ========================================
echo  Target: Maven Central Repository (Sonatype OSSRH)
echo ========================================
echo.

pushd "%~dp0\..\.."
set "PROJECT_ROOT=%CD%"
popd

set "MODULES_DIR=%PROJECT_ROOT%\astrsomn-integrations"

REM List of modules to deploy
set MODULES= astrsomn-internal-storage astrsomn-runtime-starter astrsomn-workflow-starter

echo [1/2] Installing all modules to local repository...
cd /d "%PROJECT_ROOT%"
REM Install entire project including all sub-modules to ensure inter-module dependencies are resolved
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
echo  Deployment completed!
echo ========================================
echo.
echo Press any key to exit...
pause > nul

endlocal