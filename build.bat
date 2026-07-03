@echo off
REM ============================================================
REM  ShrimpTopia - Build-Skript (Windows)
REM  Kompiliert alle Java-Quellen und baut die ausfuehrbare JAR.
REM  Voraussetzung: JDK 17+ (javac und jar im PATH).
REM ============================================================
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo [ShrimpTopia] Pruefe Java...
where javac >nul 2>nul
if errorlevel 1 (
  echo FEHLER: 'javac' nicht gefunden. Bitte ein JDK 17+ installieren.
  pause
  exit /b 1
)

echo [ShrimpTopia] Kompiliere Quellcode...
if exist out rmdir /s /q out
mkdir out
REM Quelldateien sammeln. Pfade quoten (Leerzeichen) UND \ -> / (sonst frisst
REM das javac-@argfile die Backslashes als Escape-Zeichen).
if exist sources.txt del sources.txt
dir /s /b src\*.java > sources.tmp
for /f "usebackq delims=" %%F in ("sources.tmp") do (
  set "p=%%F"
  echo "!p:\=/!">>sources.txt
)
del sources.tmp
REM --release 17: erzeugt Java-17-Bytecode auch unter neueren JDKs, damit die
REM JAR (wie im README versprochen) auf jeder Java-17+-Laufzeit startet.
javac --release 17 -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
  echo [ShrimpTopia] BUILD FEHLGESCHLAGEN.
  del sources.txt
  pause
  exit /b 1
)
del sources.txt

echo [ShrimpTopia] Kopiere Ressourcen (Avatare)...
mkdir out\com\shrimptopia\ui\avatars 2>nul
xcopy src\com\shrimptopia\ui\avatars\*.svg out\com\shrimptopia\ui\avatars\ /Y /I /Q >nul
if errorlevel 1 (
  echo [ShrimpTopia] WARNUNG: Avatar-Ressourcen konnten nicht kopiert werden.
)

echo [ShrimpTopia] Baue ShrimpTopia.jar...
REM Alte JAR zuerst entfernen - das Ueberschreiben einer (von OneDrive
REM synchronisierten) bestehenden Datei kann sonst einen leeren Stub erzeugen.
if exist ShrimpTopia.jar del ShrimpTopia.jar
jar --create --file ShrimpTopia.jar --main-class com.shrimptopia.Main -C out .
if errorlevel 1 (
  echo [ShrimpTopia] JAR-Erstellung fehlgeschlagen.
  pause
  exit /b 1
)

echo.
echo [ShrimpTopia] Fertig. Erzeugt: ShrimpTopia.jar
echo Starten mit:  run.bat   (oder:  java -jar ShrimpTopia.jar)
echo.
pause
