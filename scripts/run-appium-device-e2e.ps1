# Run full TestNG suite on the first authorized USB device (Windows self-hosted runner).
$ErrorActionPreference = 'Stop'
$Root = if ($env:GITHUB_WORKSPACE) { $env:GITHUB_WORKSPACE } else { (Resolve-Path (Join-Path $PSScriptRoot '..')).Path }
Set-Location $Root

if (-not (Get-Command adb -ErrorAction SilentlyContinue)) { throw 'adb not on PATH. Install Android platform-tools.' }
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) { throw 'mvn not on PATH. Install Maven.' }

adb start-server
Write-Host 'adb devices:'
adb devices

$deviceLine = (adb devices | Select-String "`tdevice$" | Select-Object -First 1).Line
if (-not $deviceLine) { throw "No device in 'device' state. Enable USB debugging and authorize this PC." }

if ([string]::IsNullOrWhiteSpace($env:APPIUM_UDID)) {
    $udid = ($deviceLine -split "`t")[0].Trim()
    if (-not $udid) { $udid = ($deviceLine -split '\s+')[0].Trim() }
    $env:APPIUM_UDID = $udid
    if ($env:GITHUB_ENV) {
        "APPIUM_UDID=$udid" | Out-File -FilePath $env:GITHUB_ENV -Append -Encoding utf8
    }
    Write-Host "APPIUM_UDID not set - using first device: $udid"
}

$port = if ($env:APPIUM_PORT) { $env:APPIUM_PORT } else { '4723' }
$base = if ($env:APPIUM_BASE_PATH) { $env:APPIUM_BASE_PATH } else { '/wd/hub' }
$statusUrl = "http://127.0.0.1:${port}${base}/status"

$appiumUp = $false
try {
    Invoke-WebRequest -Uri $statusUrl -UseBasicParsing -TimeoutSec 5 | Out-Null
    $appiumUp = $true
} catch { }

if (-not $appiumUp) {
    Write-Host 'Installing / starting Appium...'
    npm i -g appium@latest
    appium -v
    appium driver install uiautomator2 2>$null
    $logPath = Join-Path $Root 'appium.log'
    $appiumCmd = (Get-Command appium.cmd -ErrorAction SilentlyContinue).Source
    if (-not $appiumCmd) { $appiumCmd = (Get-Command appium -ErrorAction SilentlyContinue).Source }
    if (-not $appiumCmd) { throw 'appium CLI not found after npm i -g appium' }
    Start-Process -FilePath $appiumCmd -ArgumentList @('server', '--address', '127.0.0.1', '--port', $port, '--base-path', $base) `
        -WindowStyle Hidden -RedirectStandardOutput $logPath -RedirectStandardError $logPath -PassThru | Out-Null
    for ($i = 0; $i -lt 30; $i++) {
        try {
            Invoke-WebRequest -Uri $statusUrl -UseBasicParsing -TimeoutSec 5 | Out-Null
            Write-Host 'Appium ready.'
            break
        } catch { Start-Sleep -Seconds 2 }
    }
    try {
        Invoke-WebRequest -Uri $statusUrl -UseBasicParsing -TimeoutSec 5 | Out-Null
    } catch {
        throw "Appium did not become ready at $statusUrl. Check appium.log in repo root."
    }
} else {
    Write-Host "Appium already listening at $statusUrl"
}

$suite = if ($env:TESTNG_SUITE) { $env:TESTNG_SUITE } else { 'testng.xml' }
Write-Host "APPIUM_UDID=$($env:APPIUM_UDID)"
Write-Host "Running mvn test with suite: $suite (root: $Root)"
$pom = Join-Path $Root 'appiumtests\pom.xml'
$mvnSuiteArg = '-Dsurefire.suiteXmlFiles=' + $suite
& mvn -B -f $pom test $mvnSuiteArg
