@echo off
setlocal

REM Script directory: Astrsomn/deploy/windows/run
set "SCRIPT_DIR=%~dp0"
set "OUTPUT_DIR=%SCRIPT_DIR%..\..\..\..\astrsomn-launcher\distribution\windows\jre"
for %%I in ("%OUTPUT_DIR%\..") do set "OUTPUT_PARENT=%%~fI"

REM Resolve JDK path: prefer JAVA_HOME, fallback to a common install location.
set "JDK_HOME=%JAVA_HOME%"
if "%JDK_HOME%"=="" (
  set "JDK_HOME=C:\Program Files\Java\jdk-21"
)

if not exist "%JDK_HOME%\bin\jlink.exe" (
  echo ERROR: jlink not found.
  echo Current JDK_HOME: %JDK_HOME%
  echo Please set JAVA_HOME to a JDK that includes jlink.
  pause
  exit /b 1
)

if not exist "%JDK_HOME%\jmods" (
  echo ERROR: jmods directory not found: %JDK_HOME%\jmods
  pause
  exit /b 1
)

echo JDK_HOME: %JDK_HOME%
echo Output: %OUTPUT_DIR%

if exist "%OUTPUT_DIR%" (
  echo Cleaning old runtime...
  rmdir /s /q "%OUTPUT_DIR%"
)
if not exist "%OUTPUT_PARENT%" mkdir "%OUTPUT_PARENT%"

echo Building custom JRE...
"%JDK_HOME%\bin\jlink.exe" ^
  --module-path "%JDK_HOME%\jmods" ^
  --add-modules java.base,java.sql,java.desktop,java.xml,java.net.http,jdk.unsupported ^
  --output "%OUTPUT_DIR%" ^
  --compress=2 ^
  --no-header-files ^
  --no-man-pages

if errorlevel 1 (
  echo ERROR: jlink failed.
  pause
  exit /b 1
)

echo.
echo Custom JRE created successfully.
echo Output directory: %OUTPUT_DIR%
pause
exit /b 0
