@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Astrsomn Maven Deploy Script
REM Deploy providers modules to Maven Central Repository
REM ========================================

echo.
echo ========================================
echo  Astrsomn Providers Deploy Script
echo ========================================
echo  Target: Maven Central Repository (Sonatype OSSRH)
echo ========================================
echo.

pushd "%~dp0\..\.."
set "PROJECT_ROOT=%CD%"
popd

set "MODULES_DIR=%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers"

REM List of modules to deploy
set MODULES= astrsomn-provider-zhipu astrsomn-provider-deepseek

echo [1/2] Installing all dependencies to local repository...
cd /d "%PROJECT_ROOT%"
REM Install all modules including sub-modules
call mvn install -DskipTests -pl astrsomn-common,astrsomn-api/astrsomn-api-runtime,astrsomn-api/astrsomn-api-storage,astrsomn-api/astrsomn-api-workflow,astrsomn-plugins -am
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to install dependencies!
    echo.
    echo Press any key to exit...
    pause > nul
    exit /b 1
)

echo.
echo [2/2] Deploying providers modules to Maven Central...
echo.

for %%m in (%MODULES%) do (
    echo ----------------------------------------
    echo Deploying module: %%m
    echo ----------------------------------------
    cd /d "%MODULES_DIR%\%%m"
    call mvn deploy -X -DskipTests -Possrh-release
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
