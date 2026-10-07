@echo off
title My Library Desktop Application
color 0B
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
set "PATH=%JAVA_HOME%\bin;C:\maven\apache-maven-3.9.9\bin;%PATH%"
cd /d "%~dp0"
echo ========================================================
echo    MY LIBRARY - DESKTOP APPLICATION LAUNCHER
echo ========================================================
echo.
echo Java: 21.0.12
echo Maven: 3.9.9
echo Backend: http://localhost:9090/api
echo.
echo Launching JavaFX desktop client...
echo.
call "C:\maven\apache-maven-3.9.9\bin\mvn.cmd" javafx:run > "%~dp0launch-log.txt" 2>&1
echo.
echo Application closed.
pause
