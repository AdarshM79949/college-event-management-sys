@echo off
setlocal EnableDelayedExpansion

echo ===================================================================
echo   CampusPulse: MGM College of Engineering
echo   Pure Java Desktop Application (Java 21 LTS + Swing GUI)
echo ===================================================================
echo.

set "SCRIPT_DIR=%~dp0"

:: 1. Detect Java Runtime
set "JAVA_FOUND="
if exist "%SCRIPT_DIR%.tools\jdk-21\bin\java.exe" (
    set "JAVA_HOME=%SCRIPT_DIR%.tools\jdk-21"
    set "JAVA_VER=Portable Java 21 LTS (.tools)"
    set "JAVA_FOUND=1"
) else if exist "%USERPROFILE%\scoop\apps\openjdk21\current\bin\java.exe" (
    set "JAVA_HOME=%USERPROFILE%\scoop\apps\openjdk21\current"
    set "JAVA_VER=Java 21 LTS (Scoop)"
    set "JAVA_FOUND=1"
) else if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_VER=Java (%JAVA_HOME%)"
    set "JAVA_FOUND=1"
) else if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    set "JAVA_VER=Java 21 LTS (Standard)"
    set "JAVA_FOUND=1"
) else if exist "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
    set "JAVA_VER=Java 17 LTS (Microsoft)"
    set "JAVA_FOUND=1"
)

if not defined JAVA_FOUND (
    where java >nul 2>&1
    if !errorlevel! equ 0 (
        set "JAVA_VER=System PATH Java"
        set "JAVA_FOUND=1"
    )
)

:: 2. Detect Maven
set "MVN_FOUND="
if exist "%SCRIPT_DIR%.tools\maven\bin\mvn.cmd" (
    set "MVN_FOUND=1"
) else if exist "%USERPROFILE%\scoop\apps\maven\current\bin\mvn.cmd" (
    set "MVN_FOUND=1"
) else (
    where mvn >nul 2>&1
    if !errorlevel! equ 0 (
        set "MVN_FOUND=1"
    )
)

:: If Java or Maven is missing, automatically run setup.bat
if not defined JAVA_FOUND (
    echo [!] Java runtime not found. Launching setup to download framework runtime...
    call "%SCRIPT_DIR%setup.bat"
    goto :EXECUTE
)
if not defined MVN_FOUND (
    echo [!] Maven not found. Launching setup to download framework runtime...
    call "%SCRIPT_DIR%setup.bat"
    goto :EXECUTE
)

:EXECUTE
if defined JAVA_HOME (
    set "PATH=!JAVA_HOME!\bin;!PATH!"
)
if exist "%SCRIPT_DIR%.tools\maven\bin" (
    set "PATH=%SCRIPT_DIR%.tools\maven\bin;!PATH!"
) else if exist "%USERPROFILE%\scoop\apps\maven\current\bin" (
    set "PATH=%USERPROFILE%\scoop\apps\maven\current\bin;!PATH!"
)

echo [*] Using Java Runtime: %JAVA_VER%
echo [*] Launching CampusPulse Desktop GUI...
echo ===================================================================
echo.

call "%SCRIPT_DIR%mvn.cmd" spring-boot:run
if %errorlevel% neq 0 (
    echo.
    echo [!] CampusPulse exited with code %errorlevel%.
)
pause
