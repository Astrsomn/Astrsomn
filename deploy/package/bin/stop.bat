@echo off
taskkill /fi "imagename eq java.exe" 2>nul
echo Astrsomn Server stopped.
pause
