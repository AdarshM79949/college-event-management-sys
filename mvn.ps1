if (Test-Path "C:\Users\adars\scoop\apps\openjdk21\current") {
    $env:JAVA_HOME = "C:\Users\adars\scoop\apps\openjdk21\current"
} else {
    $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
}
$env:Path = "$env:JAVA_HOME\bin;C:\Users\adars\scoop\apps\maven\current\bin;$env:Path"
& "C:\Users\adars\scoop\apps\maven\current\bin\mvn.cmd" $args
