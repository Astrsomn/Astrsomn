@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Astrsomn Maven Deploy Script
REM Deploy all modules to Maven Central Repository
REM ========================================

echo.
echo ========================================
echo  Astrsomn Full Deploy Script
echo ========================================
echo  Target: Maven Central Repository (Sonatype OSSRH)
echo ========================================
echo.

REM Repository root (parent of deploy/maven-deploy)
pushd "%~dp0..\.."
set "PROJECT_ROOT=%CD%"
popd

echo [1/2] Installing all dependencies to local repository...
cd /d "%PROJECT_ROOT%"
call mvn install -DskipTests -pl astrsomn-common,astrsomn-api,astrsomn-integrations,astrsomn-plugins -am
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to install dependencies!
    exit /b 1
)

echo.
echo [2/2] Deploying all modules to Maven Central...
echo.

REM Deploy providers modules
echo ----------------------------------------
echo Deploying providers modules...
echo ----------------------------------------
cd /d "%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers\astrsomn-provider-openai"
call mvn deploy -DskipTests -Possrh-release
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to deploy astrsomn-provider-openai!
    exit /b 1
)

cd /d "%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers\astrsomn-provider-qianfan"
call mvn deploy -DskipTests -Possrh-release
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to deploy astrsomn-provider-qianfan!
    exit /b 1
)

cd /d "%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers\astrsomn-provider-qwen"
call mvn deploy -DskipTests -Possrh-release
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to deploy astrsomn-provider-qwen!
    exit /b 1
)

cd /d "%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers\astrsomn-provider-zhipu"
call mvn deploy -DskipTests -Possrh-release
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to deploy astrsomn-provider-zhipu!
    exit /b 1
)

cd /d "%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers\astrsomn-provider-deepseek"
call mvn deploy -DskipTests -Possrh-release
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to deploy astrsomn-provider-deepseek!
    exit /b 1
)

REM Deploy integrations modules
echo.
echo ----------------------------------------
echo Deploying integrations modules...
echo ----------------------------------------
cd /d "%PROJECT_ROOT%\astrsomn-integrations\astrsomn-internal-storage"
call mvn deploy -DskipTests -Possrh-release
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to deploy astrsomn-internal-storage!
    exit /b 1
)

cd /d "%PROJECT_ROOT%\astrsomn-integrations\astrsomn-runtime-starter"
call mvn deploy -DskipTests -Possrh-release
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to deploy astrsomn-runtime-starter!
    exit /b 1
)

cd /d "%PROJECT_ROOT%\astrsomn-integrations\astrsomn-workflow-starter"
call mvn deploy -DskipTests -Possrh-release
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to deploy astrsomn-workflow-starter!
    exit /b 1
)

echo.
echo ========================================
echo  All modules deployed successfully!
echo ========================================
echo.

endlocal
