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

The **`appium-self-hosted-device`** job is **manual only** (it does **not** run on push to `main`), so routine pushes never sit **Queued** waiting for a self-hosted runner.

1. Put your self-hosted runner **online** (see below).  
2. **Actions → Mobile E2E (Appium) → Run workflow** → choose branch → **Run workflow**.

Open the run → job **`appium-self-hosted-device`** → logs. On success, download artifact **`appium-artifacts-self-hosted`**.

### Job stuck on “Queued”?

That only happens if you **manually** started the workflow while no runner is online. GitHub runs **`runs-on: self-hosted`** only when a runner for this repo is available.

1. Repo **Settings → Actions → Runners** — the runner should show **Idle** or **Active** (not offline).  
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

## 9. End-to-end checklist (verify listener + driver)

1. **Sync `main`** so you have `AppiumSuiteListener`, `testng.xml`, `BaseClass`, and `scripts/` (including `run-appium-device-e2e.sh` / `.ps1`).

2. **On the machine with the phone**, run `adb devices` — the device line must end with **`device`** (not `unauthorized` or `offline`).

3. **Run the suite** in one of these ways:
   - **GitHub:** self-hosted runner online → **Actions → Mobile E2E (Appium) → Run workflow** → open job **`appium-self-hosted-device`** logs.  
   - **Linux/macOS (local):** from repo root, `bash scripts/run-appium-device-e2e.sh`  
   - **Windows (local):** from repo root, `.\run-testng-device.ps1` or  
     `powershell -NoProfile -ExecutionPolicy Bypass -File scripts\run-appium-device-e2e.ps1`

4. **In the Maven / TestNG log**, confirm these lines appear **before** individual `@Test` methods run:
   - `>>> AppiumSuiteListener.onStart suite=` …  
   - `✅ Appium driver started (udid=` … (or `♻️ Reusing existing Appium driver session` if a session was already up)

   `AppiumSuiteListener` calls `BaseClass.createDriverOnce()` from `onStart`, so if you see `onStart` but no driver line, the failure is inside driver creation (Appium URL, capabilities, `adb`, etc.).

5. **If something still fails**, capture the **first stack trace** in the Maven/TestNG output that appears **after** `>>> AppiumSuiteListener.onStart` and share that (or the Surefire report under `appiumtests/target/surefire-reports/`).
