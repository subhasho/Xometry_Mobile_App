# Run `testng.xml` on a real phone (GitHub self-hosted runner)

The workflow job **`appium-self-hosted-device`** (`.github/workflows/mobile-e2e-appium.yml`) runs **`mvn … testng.xml`** on the **same computer** where the GitHub Actions **self-hosted runner** is installed, using **USB debugging** on your Android device.

## 1. One PC = runner + USB cable

Use the machine that stays on while you work (or a small always-on box). Plug the phone in with a **data** USB cable.

## 2. Phone: developer options

1. **Settings → About phone** → tap **Build number** 7× to enable Developer options.  
2. **Settings → Developer options** → enable **USB debugging**.  
3. Connect USB → accept **“Allow USB debugging?”** (RSA fingerprint) on the phone.  
4. On the PC, run `adb devices` — the line must end with **`device`** (not `unauthorized` or `offline`).

## 3. Install on that PC

- **Java 17** (set `JAVA_HOME` if `mvn -version` does not show 17)  
- **Maven** (`mvn` on `PATH`)  
- **Node.js 20** (`npm` / `npx` on `PATH`)  
- **Android Platform-Tools** (`adb` on `PATH`)  
- **Appium** with UiAutomator2:  
  `npm i -g appium@latest` then `appium driver install uiautomator2`

## 4. Register the self-hosted runner (GitHub)

1. Repo **Settings → Actions → Runners → New self-hosted runner**.  
2. Choose **Windows** or **Linux** to match the PC.  
3. Follow the **download + configure + run** commands on that PC.  
4. Prefer **Run the application as a service** (Windows) or **systemd** (Linux) so the runner stays up after logout.

Labels: default label is **`self-hosted`**. The workflow uses `runs-on: self-hosted`, so any runner with that label in this repo can pick the job.

## 5. Optional repository variables

**Settings → Secrets and variables → Actions → Variables** (names used by `BaseClass`):

| Variable | Purpose |
|----------|---------|
| `APPIUM_UDID` | From `adb devices` (optional if only one device) |
| `APPIUM_PLATFORM_VERSION` | Android version of the phone |
| `APK_PATH` | Full path to `.apk` on the runner PC if the app is not already installed |

Secret **`APPIUM_URL`** only if Appium is not on `http://127.0.0.1:4723/wd/hub`.

## 6. Appium on the runner PC

Either:

- Start Appium yourself before the job:  
  `appium server --address 127.0.0.1 --port 4723 --base-path /wd/hub`  
  The CI script skips starting a second server if that URL already responds.

- Or let the workflow script install/start Appium (same as local script).

## 7. Run the job

- **Push to `main`** (job runs unless **`SKIP_DEVICE_E2E_ON_PUSH`** is `true`), or  
- **Actions → Mobile E2E (Appium) → Run workflow**.

Open the run → job **`appium-self-hosted-device`** → logs. On success, download artifact **`appium-artifacts-self-hosted`**.

### Job stuck on “Queued”?

GitHub only runs **`runs-on: self-hosted`** jobs when a **runner for this repo is online**. If the PC is off, the runner process was closed, or the Windows service is stopped, the job waits (often until timeout).

1. Repo **Settings → Actions → Runners** — each runner should show **Idle** or **Active** (not offline).  
2. On the runner PC: open the install folder (e.g. `actions-runner`) and run **`.\run.cmd`**, or start the **GitHub Actions Runner** service.  
3. From repo root, with [GitHub CLI](https://cli.github.com/) logged in:  
   `powershell -File scripts\validate-self-hosted-prereqs.ps1`  
   It checks `adb`, Maven, Node, Appium, and lists runner status via `gh api`.

## 8. Same machine, without GitHub (local)

From repo root:

```powershell
.\run-testng-device.ps1
```

This runs the same `scripts/run-appium-device-e2e.ps1` flow against the connected device.
