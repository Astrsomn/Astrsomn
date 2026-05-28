@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Astrsomn RELEASE Deploy Script
REM Deploy all modules to Maven Central (Release)
REM Requires: non-SNAPSHOT version, GPG key, central-publishing-maven-plugin
REM For SNAPSHOT: use deploy-snapshot.bat instead
REM ========================================

echo.
echo ========================================
echo  Astrsomn Full RELEASE Deploy Script
echo ========================================
echo  Target: Maven Central Repository
echo  Profile: ossrh (central-publishing + GPG)
echo  WARNING: Version must NOT be SNAPSHOT!
echo ========================================
echo.

REM Repository root (parent of deploy/maven-deploy)
pushd "%~dp0\..\.."
set "PROJECT_ROOT=%CD%"
popd

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
echo [2/2] Deploying all modules to Maven Central...
echo.

REM --- API modules (deploy first, no inter-project deps beyond common) ---
echo ----------------------------------------
echo Deploying API modules...
echo ----------------------------------------
cd /d "%PROJECT_ROOT%\astrsomn-common"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-common!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-api\astrsomn-api-runtime"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-api-runtime!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-api\astrsomn-api-storage"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-api-storage!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-api\astrsomn-api-system"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-api-system!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-api\astrsomn-api-vector"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-api-vector!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-api\astrsomn-api-workflow"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-api-workflow!
    pause > nul & exit /b 1
)

REM --- Starter modules (depend on api-*) ---
echo.
echo ----------------------------------------
echo Deploying starter modules...
echo ----------------------------------------
cd /d "%PROJECT_ROOT%\astrsomn-integrations\astrsomn-system-starter"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-system-starter!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-integrations\astrsomn-vector-starter"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-vector-starter!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-integrations\astrsomn-internal-storage"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-internal-storage!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-integrations\astrsomn-runtime-starter"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-runtime-starter!
    pause > nul & exit /b 1
)
cd /d "%PROJECT_ROOT%\astrsomn-integrations\astrsomn-workflow-starter"
call mvn deploy -X -DskipTests -Possrh
if errorlevel 1 (
    echo [ERROR] Failed to deploy astrsomn-workflow-starter!
    pause > nul & exit /b 1
)

REM --- Providers ---
echo.
echo ----------------------------------------
echo Deploying provider modules...
echo ----------------------------------------
for %%m in (astrsomn-provider-openai astrsomn-provider-qianfan astrsomn-provider-qwen astrsomn-provider-zhipu astrsomn-provider-deepseek) do (
    cd /d "%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers\%%m"
    call mvn deploy -X -DskipTests -Possrh
    if errorlevel 1 (
        echo [ERROR] Failed to deploy %%m!
        pause > nul & exit /b 1
    )
)

REM --- Vector stores ---
echo.
echo ----------------------------------------
echo Deploying vector store modules...
echo ----------------------------------------
for %%m in (astrsomn-vector-chroma astrsomn-vector-milvus astrsomn-vector-qdrant astrsomn-vector-redis) do (
    cd /d "%PROJECT_ROOT%\astrsomn-plugins\astrsomn-vector\%%m"
    call mvn deploy -X -DskipTests -Possrh
    if errorlevel 1 (
        echo [ERROR] Failed to deploy %%m!
        pause > nul & exit /b 1
    )
)

echo.
echo ========================================
echo  All RELEASE modules deployed!
echo  Next: log in to https://central.sonatype.com to publish staged deployments
echo ========================================
echo.
echo Press any key to exit...
pause > nul

endlocal
