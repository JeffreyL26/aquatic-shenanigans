@echo off
REM ============================================================
REM  ShrimpTopia - Startet das Spiel.
REM ============================================================
cd /d "%~dp0"

REM Bevorzugt das JDK/JRE aus JAVA_HOME (immun gegen PATH-Reihenfolge, z.B. eine
REM aeltere Java-8-Shim im System-PATH); sonst Rueckfall auf 'java' aus dem PATH.
set "JAVA_CMD=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"

if not exist "ShrimpTopia.jar" (
  echo ShrimpTopia.jar nicht gefunden. Bitte zuerst build.bat ausfuehren.
  pause
  exit /b 1
)

"%JAVA_CMD%" -jar ShrimpTopia.jar
if errorlevel 1 pause
