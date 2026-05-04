# Run on the Windows PC that will host the GitHub runner + USB phone.
# Checks tools and (if gh is logged in) whether this repo's self-hosted runner is online.
$ErrorActionPreference = 'Continue'
$ok = $true

Write-Host "=== Local tools ==="
if (-not (Get-Command adb -ErrorAction SilentlyContinue)) { Write-Host "[MISSING] adb"; $ok = $false } else { Write-Host "[OK] adb"; adb devices }
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) { Write-Host "[MISSING] mvn"; $ok = $false } else { Write-Host "[OK] mvn" }
if (-not (Get-Command node -ErrorAction SilentlyContinue)) { Write-Host "[MISSING] node"; $ok = $false } else { Write-Host "[OK] node" }
if (-not (Get-Command appium.cmd -ErrorAction SilentlyContinue) -and -not (Get-Command appium -ErrorAction SilentlyContinue)) {
    Write-Host "[MISSING] appium - npm i -g appium@latest"
    $ok = $false
} else { Write-Host "[OK] appium" }

$java17 = 'C:\Program Files\Java\jdk-17\bin\java.exe'
if (Test-Path $java17) { Write-Host "[OK] JDK 17 at $java17" } else { Write-Host "[WARN] JDK 17 path not found - set JAVA_HOME for Maven tests" }

Write-Host ""
Write-Host "=== GitHub self-hosted runner (this repo) ==="
if (Get-Command gh -ErrorAction SilentlyContinue) {
    $repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
    $remoteNames = @(git -C $repoRoot remote 2>$null)
    $remote = $null
    foreach ($rn in $remoteNames) {
        $remote = git -C $repoRoot remote get-url $rn 2>$null
        if ($remote) { break }
    }
    $owner = $null
    $repoName = $null
    if ($remote) {
        $idx = $remote.IndexOf('github.com')
        if ($idx -ge 0) {
            $tail = $remote.Substring($idx + 'github.com'.Length).TrimStart(':/')
            $parts = $tail -split '/', 3
            if ($parts.Length -ge 2) {
                $owner = $parts[0]
                $repoName = $parts[1] -replace '\.git$', ''
            }
        }
    }
    if ($owner -and $repoName) {
        $json = gh api "repos/$owner/$repoName/actions/runners" 2>$null
        if ($json) {
            $runners = $json | ConvertFrom-Json
            $anyOnline = $false
            foreach ($r in $runners.runners) {
                $st = $r.status
                $color = if ($st -eq 'online') { 'Green' } else { 'Yellow' }
                $labels = ($r.labels | ForEach-Object { $_.name }) -join ', '
                Write-Host ("Runner: {0} | status: {1} | labels: {2}" -f $r.name, $st, $labels) -ForegroundColor $color
                if ($st -eq 'online') { $anyOnline = $true }
                if ($st -ne 'online') {
                    Write-Host "  -> To bring online: on that machine run run.cmd in the runner folder, or start the GitHub Actions Runner service." -ForegroundColor Yellow
                }
            }
            if ($runners.total_count -eq 0) {
                Write-Host "[NONE] No runners. Add one: https://github.com/$owner/$repoName/settings/actions/runners/new"
                $ok = $false
            } elseif (-not $anyOnline) {
                Write-Host "[BLOCKED] No runner is online - device E2E jobs stay Queued until a runner is started." -ForegroundColor Red
                $ok = $false
            }
        }
    } else {
        Write-Host "[SKIP] Could not parse git remote for gh api"
    }
} else {
    Write-Host "[SKIP] gh not installed - install GitHub CLI to check runner status"
}

Write-Host ""
Write-Host "=== Run testng.xml locally (same flow as CI self-hosted job) ==="
Write-Host "  .\run-testng-device.ps1"

if (-not $ok) { exit 1 }
Write-Host ""
Write-Host "All checks passed."
exit 0
