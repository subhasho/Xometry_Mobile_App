# Run appiumtests/testng.xml on THIS PC when USB device + Appium are ready.
# - If Appium already listens on 127.0.0.1:4723/wd/hub, the script reuses it.
# - GitHub Actions cannot see your USB phone; use this script locally OR a self-hosted runner on this machine.
$ErrorActionPreference = 'Stop'
$Root = $PSScriptRoot
Set-Location $Root

if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $candidates = @(
        'C:\Program Files\Java\jdk-17',
        'C:\Program Files\Java\jdk-21',
        'C:\Program Files\Eclipse Adoptium\jdk-17.0.15.9-hotspot'
    )
    foreach ($j in $candidates) {
        if (Test-Path "$j\bin\java.exe") {
            $env:JAVA_HOME = $j
            break
        }
    }
}
if ($env:JAVA_HOME) {
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
}

Write-Host "==> Running testng.xml (device + Appium on this machine)"
& "$Root\scripts\run-appium-device-e2e.ps1"
