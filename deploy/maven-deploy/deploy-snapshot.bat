@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Astrsomn Snapshot Deploy Script
REM Deploy SNAPSHOT modules to Central Portal snapshot repository
REM Uses standard maven-deploy-plugin (NOT central-publishing-maven-plugin)
REM ========================================

echo.
echo ========================================
echo  Astrsomn Snapshot Deploy Script
echo ========================================
echo  Target: Central Portal Snapshot Repository
echo  Profile: snapshot (no GPG, no central-publishing)
echo ========================================
echo.

pushd "%~dp0\..\.."
set "PROJECT_ROOT=%CD%"
popd

echo [1/2] Installing all modules to local repository...
cd /d "%PROJECT_ROOT%"
call mvn clean install -DskipTests
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to install modules!
    echo.
    echo Press any key to exit...
    pause > nul
    exit /b 1
)

echo.
echo [2/2] Deploying SNAPSHOT modules...
echo.

REM --- Providers ---
set "PROVIDERS_DIR=%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers"

for %%m in (astrsomn-provider-openai astrsomn-provider-qianfan astrsomn-provider-qwen astrsomn-provider-zhipu astrsomn-provider-deepseek) do (
    echo ----------------------------------------
    echo Deploying: %%m
    echo ----------------------------------------
    cd /d "%PROVIDERS_DIR%\%%m"
    call mvn deploy -DskipTests -Psnapshot
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to deploy %%m!
        echo.
        echo Press any key to exit...
        pause > nul
        exit /b 1
    )
    echo.
)

REM --- Vector stores ---
set "VECTOR_DIR=%PROJECT_ROOT%\astrsomn-plugins\astrsomn-vector"

for %%m in (astrsomn-vector-chroma astrsomn-vector-milvus astrsomn-vector-qdrant astrsomn-vector-redis) do (
    echo ----------------------------------------
    echo Deploying: %%m
    echo ----------------------------------------
    cd /d "%VECTOR_DIR%\%%m"
    call mvn deploy -DskipTests -Psnapshot
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to deploy %%m!
        echo.
        echo Press any key to exit...
        pause > nul
        exit /b 1
    )
    echo.
)

REM --- Integrations ---
set "INTEGRATIONS_DIR=%PROJECT_ROOT%\astrsomn-integrations"

for %%m in (astrsomn-internal-storage astrsomn-system-starter astrsomn-vector-starter astrsomn-runtime-starter astrsomn-workflow-starter) do (
    echo ----------------------------------------
    echo Deploying: %%m
    echo ----------------------------------------
    cd /d "%INTEGRATIONS_DIR%\%%m"
    call mvn deploy -DskipTests -Psnapshot
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to deploy %%m!
        echo.
        echo Press any key to exit...
        pause > nul
        exit /b 1
    )
    echo.
)

echo.
echo ========================================
echo  Snapshot deployment completed!
echo  Verify: https://central.sonatype.com/repository/maven-snapshots/com/astrsomn/
echo ========================================
echo.
echo Press any key to exit...
pause > nul

endlocal
