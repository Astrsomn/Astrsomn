@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Astrsomn Maven Deploy Script
REM Deploy integrations modules to GitHub Packages
REM ========================================

echo.
echo ========================================
echo  Astrsomn Integrations Deploy Script
echo ========================================
echo.

REM Set project root directory
set PROJECT_ROOT=D:\Project\astrsomn\Astrsomn
set MODULES_DIR=%PROJECT_ROOT%\astrsomn-integrations

REM List of modules to deploy
set MODULES= astrsomn-runtime-starter

echo [1/2] Installing all dependencies to local repository...
cd /d %PROJECT_ROOT%
call mvn install -DskipTests -pl astrsomn-common,astrsomn-api,astrsomn-integrations -am
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to install dependencies!
    exit /b 1
)

echo.
echo [2/2] Deploying integrations modules to GitHub Packages...
echo.

for %%m in (%MODULES%) do (
    echo ----------------------------------------
    echo Deploying module: %%m
    echo ----------------------------------------
    cd /d %MODULES_DIR%\%%m
    call mvn deploy -DskipTests
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to deploy module %%m!
        exit /b 1
    )
    echo.
)

echo.
echo ========================================
echo  Deployment completed!
echo ========================================
echo.

endlocal
