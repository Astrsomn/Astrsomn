@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Astrsomn Package Script
REM Package all modules (provider + vector)
REM Output to: PROJECT_ROOT/plugins/
REM ========================================

echo.
echo ========================================
echo  Astrsomn Package Script
echo ========================================
echo  Target: Package all modules
echo  Output: plugins/ directory
echo ========================================
echo.

pushd "%~dp0\..\.."
set "PROJECT_ROOT=%CD%"
popd

set "OUTPUT_DIR=%PROJECT_ROOT%\plugins"
if not exist "%OUTPUT_DIR%" (
    mkdir "%OUTPUT_DIR%"
)

echo [1/2] Installing all modules to local repository...
cd /d "%PROJECT_ROOT%"
call mvn clean install -DskipTests -q
if errorlevel 1 (
    echo.
    echo [ERROR] Failed to install modules!
    echo.
    echo Press any key to exit...
    pause > nul
    exit /b 1
)

echo.
echo [2/2] Packaging and copying modules...
echo.

set "PROVIDERS_DIR=%PROJECT_ROOT%\astrsomn-plugins\astrsomn-providers"

echo --- Packaging Providers ---
for %%m in (astrsomn-provider-openai astrsomn-provider-qianfan astrsomn-provider-qwen astrsomn-provider-zhipu astrsomn-provider-deepseek astrsomn-provider-ali) do (
    echo ----------------------------------------
    echo Packaging: %%m
    echo ----------------------------------------
    cd /d "%PROVIDERS_DIR%\%%m"
    call mvn clean package -DskipTests -q
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to package %%m!
        echo.
        echo Press any key to exit...
        pause > nul
        exit /b 1
    )
    echo Copying artifact to plugins directory...
    copy /y "%PROVIDERS_DIR%\%%m\target\%%m-*.jar" "%OUTPUT_DIR%\"
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to copy %%m!
        echo.
        echo Press any key to exit...
        pause > nul
        exit /b 1
    )
    echo.
)

set "VECTOR_DIR=%PROJECT_ROOT%\astrsomn-plugins\astrsomn-vector"

echo --- Packaging Vector Stores ---
for %%m in (astrsomn-vector-chroma astrsomn-vector-milvus astrsomn-vector-qdrant ) do (
    echo ----------------------------------------
    echo Packaging: %%m
    echo ----------------------------------------
    cd /d "%VECTOR_DIR%\%%m"
    call mvn clean package -DskipTests -q
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to package %%m!
        echo.
        echo Press any key to exit...
        pause > nul
        exit /b 1
    )
    echo Copying artifact to plugins directory...
    copy /y "%VECTOR_DIR%\%%m\target\%%m-*.jar" "%OUTPUT_DIR%\"
    if errorlevel 1 (
        echo.
        echo [ERROR] Failed to copy %%m!
        echo.
        echo Press any key to exit...
        pause > nul
        exit /b 1
    )
    echo.
)

echo.
echo ========================================
echo  Packaging completed!
echo  Artifacts located in: %OUTPUT_DIR%
echo ========================================
echo.
echo Press any key to exit...
pause > nul

endlocal