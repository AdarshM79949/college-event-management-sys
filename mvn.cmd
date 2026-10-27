@echo off
:: CampusPulse - Universal Maven Launcher
:: Auto-detects Portable (.tools), Scoop, System JAVA_HOME, and Standard Paths

set "SCRIPT_DIR=%~dp0"

:: 1. Detect Java
if exist "%SCRIPT_DIR%.tools\jdk-21\bin\java.exe" (
    set "JAVA_HOME=%SCRIPT_DIR%.tools\jdk-21"
) else if exist "%USERPROFILE%\scoop\apps\openjdk21\current\bin\java.exe" (
    set "JAVA_HOME=%USERPROFILE%\scoop\apps\openjdk21\current"
) else if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    rem System JAVA_HOME is valid
) else if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
) else if exist "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
)

:: 2. Detect Maven
set "MVN_EXEC="
if exist "%SCRIPT_DIR%.tools\maven\bin\mvn.cmd" (
    set "MVN_EXEC=%SCRIPT_DIR%.tools\maven\bin\mvn.cmd"
    set "MAVEN_HOME=%SCRIPT_DIR%.tools\maven"
) else if exist "%USERPROFILE%\scoop\apps\maven\current\bin\mvn.cmd" (
    set "MVN_EXEC=%USERPROFILE%\scoop\apps\maven\current\bin\mvn.cmd"
    set "MAVEN_HOME=%USERPROFILE%\scoop\apps\maven\current"
) else (
    set "MVN_EXEC=mvn.cmd"
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)
if defined MAVEN_HOME (
    set "PATH=%MAVEN_HOME%\bin;%PATH%"
)

"%MVN_EXEC%" %*
