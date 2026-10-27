$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Detect Java
$javaHome = $null
if (Test-Path "$scriptDir\.tools\jdk-21\bin\java.exe") {
    $javaHome = "$scriptDir\.tools\jdk-21"
} elseif (Test-Path "$env:USERPROFILE\scoop\apps\openjdk21\current\bin\java.exe") {
    $javaHome = "$env:USERPROFILE\scoop\apps\openjdk21\current"
} elseif ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $javaHome = $env:JAVA_HOME
} elseif (Test-Path "C:\Program Files\Java\jdk-21\bin\java.exe") {
    $javaHome = "C:\Program Files\Java\jdk-21"
} elseif (Test-Path "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\java.exe") {
    $javaHome = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
}

# Detect Maven
$mvnCmd = "mvn"
if (Test-Path "$scriptDir\.tools\maven\bin\mvn.cmd") {
    $mvnCmd = "$scriptDir\.tools\maven\bin\mvn.cmd"
    $env:Path = "$scriptDir\.tools\maven\bin;$env:Path"
} elseif (Test-Path "$env:USERPROFILE\scoop\apps\maven\current\bin\mvn.cmd") {
    $mvnCmd = "$env:USERPROFILE\scoop\apps\maven\current\bin\mvn.cmd"
    $env:Path = "$env:USERPROFILE\scoop\apps\maven\current\bin;$env:Path"
}

if ($javaHome) {
    $env:JAVA_HOME = $javaHome
    $env:Path = "$javaHome\bin;$env:Path"
}

Write-Host "===================================================================" -ForegroundColor Cyan
Write-Host " Launching CampusPulse: MGM College of Engineering" -ForegroundColor Green
Write-Host " Pure Java Desktop Application (Swing GUI + Persistent SQL)" -ForegroundColor Green
Write-Host "===================================================================" -ForegroundColor Cyan

& $mvnCmd spring-boot:run
