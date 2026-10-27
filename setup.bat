@echo off
setlocal EnableDelayedExpansion

echo ===================================================================
echo   CampusPulse: Framework & Environment Setup
echo   Downloading & Configuring Java 21 LTS, Maven, and Dependencies
echo ===================================================================
echo.

set "SCRIPT_DIR=%~dp0"
set "TOOLS_DIR=%SCRIPT_DIR%.tools"

if not exist "%TOOLS_DIR%" mkdir "%TOOLS_DIR%"

:: -------------------------------------------------------------
:: Step 1: Check / Download Java JDK 21 LTS
:: -------------------------------------------------------------
set "JAVA_FOUND="
if exist "%TOOLS_DIR%\jdk-21\bin\java.exe" (
    set "JAVA_HOME=%TOOLS_DIR%\jdk-21"
    echo [OK] Found portable JDK 21 in: !JAVA_HOME!
    set "JAVA_FOUND=1"
) else if exist "%USERPROFILE%\scoop\apps\openjdk21\current\bin\java.exe" (
    set "JAVA_HOME=%USERPROFILE%\scoop\apps\openjdk21\current"
    echo [OK] Found Scoop OpenJDK 21 in: !JAVA_HOME!
    set "JAVA_FOUND=1"
) else if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    echo [OK] Found system JAVA_HOME: %JAVA_HOME%
    set "JAVA_FOUND=1"
) else if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    echo [OK] Found JDK 21 in: !JAVA_HOME!
    set "JAVA_FOUND=1"
) else if exist "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
    echo [OK] Found Microsoft JDK 17 in: !JAVA_HOME!
    set "JAVA_FOUND=1"
)

if not defined JAVA_FOUND (
    where java >nul 2>&1
    if !errorlevel! equ 0 (
        echo [OK] Found java on system PATH.
        set "JAVA_FOUND=1"
    )
)

if not defined JAVA_FOUND (
    echo [*] Java (JDK 21 LTS) was not detected.
    echo [*] Downloading portable OpenJDK 21 LTS (Adoptium Temurin)...
    echo     Please wait, downloading framework runtime...
    
    set "JDK_ZIP=%TOOLS_DIR%\openjdk21.zip"
    set "JDK_URL=https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.6%%2B7/OpenJDK21U-jdk_x64_windows_hotspot_21.0.6_7.zip"
    
    curl.exe -fSL -o "!JDK_ZIP!" "!JDK_URL!"
    if !errorlevel! neq 0 (
        echo [ERROR] Failed to download OpenJDK 21. Please verify internet connectivity.
        pause
        exit /b 1
    )
    
    echo [*] Extracting OpenJDK 21...
    tar.exe -xf "!JDK_ZIP!" -C "%TOOLS_DIR%"
    del /f /q "!JDK_ZIP!" >nul 2>&1
    
    for /d %%D in ("%TOOLS_DIR%\jdk-21*") do (
        if exist "%%D\bin\java.exe" (
            set "JAVA_HOME=%%D"
        )
    )
    echo [OK] OpenJDK 21 installed successfully in: !JAVA_HOME!
)

:: -------------------------------------------------------------
:: Step 2: Check / Download Apache Maven
:: -------------------------------------------------------------
set "MVN_FOUND="
if exist "%TOOLS_DIR%\maven\bin\mvn.cmd" (
    set "MAVEN_HOME=%TOOLS_DIR%\maven"
    set "MVN_EXEC=%TOOLS_DIR%\maven\bin\mvn.cmd"
    echo [OK] Found portable Maven in: !MAVEN_HOME!
    set "MVN_FOUND=1"
) else if exist "%USERPROFILE%\scoop\apps\maven\current\bin\mvn.cmd" (
    set "MAVEN_HOME=%USERPROFILE%\scoop\apps\maven\current"
    set "MVN_EXEC=%USERPROFILE%\scoop\apps\maven\current\bin\mvn.cmd"
    echo [OK] Found Scoop Maven in: !MAVEN_HOME!
    set "MVN_FOUND=1"
) else (
    where mvn >nul 2>&1
    if !errorlevel! equ 0 (
        set "MVN_EXEC=mvn"
        echo [OK] Found Maven on system PATH.
        set "MVN_FOUND=1"
    )
)

if not defined MVN_FOUND (
    echo [*] Apache Maven was not detected.
    echo [*] Downloading portable Apache Maven 3.9.9...
    
    set "MVN_ZIP=%TOOLS_DIR%\apache-maven.zip"
    set "MVN_URL=https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"
    
    curl.exe -fSL -o "!MVN_ZIP!" "!MVN_URL!"
    if !errorlevel! neq 0 (
        echo [ERROR] Failed to download Apache Maven. Please verify internet connectivity.
        pause
        exit /b 1
    )
    
    echo [*] Extracting Apache Maven...
    tar.exe -xf "!MVN_ZIP!" -C "%TOOLS_DIR%"
    del /f /q "!MVN_ZIP!" >nul 2>&1
    
    for /d %%M in ("%TOOLS_DIR%\apache-maven-3*") do (
        if exist "%%M\bin\mvn.cmd" (
            move "%%M" "%TOOLS_DIR%\maven" >nul 2>&1
        )
    )
    set "MAVEN_HOME=%TOOLS_DIR%\maven"
    set "MVN_EXEC=%TOOLS_DIR%\maven\bin\mvn.cmd"
    echo [OK] Apache Maven installed successfully in: !MAVEN_HOME!
)

:: -------------------------------------------------------------
:: Step 3: Configure Environment & Pre-Download Frameworks
:: -------------------------------------------------------------
echo.
echo [*] Configuring build environment...
if defined JAVA_HOME (
    set "PATH=!JAVA_HOME!\bin;!PATH!"
)
if defined MAVEN_HOME (
    set "PATH=!MAVEN_HOME!\bin;!PATH!"
)

echo [*] Downloading and resolving framework dependencies...
echo     (Spring Boot, FlatDarkLaf UI, ZXing QR, Spring Data JPA, H2 Database)
echo.
call "%SCRIPT_DIR%mvn.cmd" clean compile -DskipTests
if !errorlevel! neq 0 (
    echo.
    echo [WARNING] Build encountered issues. Code: !errorlevel!
) else (
    echo.
    echo ===================================================================
    echo   CampusPulse Setup Completed Successfully!
    echo   Frameworks and dependencies are downloaded and verified.
    echo.
    echo   You can launch the application anytime by double-clicking:
    echo     run.bat
    echo ===================================================================
)

pause
