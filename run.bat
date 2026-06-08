@echo off
REM ============================================================
REM  ShrimpTopia - Startet das Spiel.
REM ============================================================
cd /d "%~dp0"

if not exist "ShrimpTopia.jar" (
  echo ShrimpTopia.jar nicht gefunden. Bitte zuerst build.bat ausfuehren.
  pause
  exit /b 1
)

java -jar ShrimpTopia.jar
if errorlevel 1 pause
