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

### Why GitHub shows the runner as **offline**

GitHub marks a self-hosted runner **offline** when the **runner agent** is not connected—usually because:

- **`.\run.cmd`** was started in a **terminal** and the window was **closed**, or the user **signed out** (interactive mode stops when the session ends).
- The PC **restarted**, **shut down**, **slept**, or **hibernated**.
- The runner process **crashed** or was **killed** and was not restarted.

### Keep the runner **online** as much as possible (recommended)

You cannot force “always online” from GitHub’s website alone; the **PC must run the agent** (or a **Windows/Linux service** that runs it).

1. **Install the runner as a service** (best for “always on”): during **configure**, choose **Run as a service** (Windows), or on Linux use **systemd**. Official steps: [Configuring the self-hosted runner application as a service](https://docs.github.com/en/actions/hosting-your-own-runners/managing-self-hosted-runners/configuring-the-self-hosted-runner-application-as-a-service).
2. **Windows:** open **`services.msc`** → find **GitHub Actions Runner …** → set **Startup type** to **Automatic** → **Start** if it is stopped.
3. **Power:** disable **sleep** / **hibernate** on AC power for that PC so it does not drop the connection overnight.
4. If you only used **`.\run.cmd`** once and never installed a service, either **reconfigure** with “run as service” or follow GitHub’s docs to **install the existing runner as a service** for your OS.

GitHub will still show **offline** if the machine is off or has no network—there is no cloud switch to override that.

## 5. Optional repository variables

**Settings → Secrets and variables → Actions → Variables** (names used by `BaseClass`):

| Variable | Purpose |
|----------|---------|
| `RUN_EMULATOR_E2E_ON_PUSH` | Set to `true` to also run **`appium-emulator-e2e`** on push (needs secret **`APPIUM_E2E_APK_URL`**). **Default:** emulator job is **off**. |
| `RUN_UBUNTU_APPIUM_SMOKE_ON_PUSH` | Set to `true` to also run **`appium-ubuntu-smoke`** on push. **Default:** off. |
| `SKIP_DEVICE_E2E_ON_PUSH` | Set to `true` to **skip** **`appium-self-hosted-device`** on push (manual **Run workflow** still runs it). |
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

**Default on every push to `main`:** only **`appium-self-hosted-device`** runs **`testng.xml`** on your **USB phone** (self-hosted runner). Emulator and ubuntu jobs are **opt-in** (see variables **`RUN_EMULATOR_E2E_ON_PUSH`** and **`RUN_UBUNTU_APPIUM_SMOKE_ON_PUSH`** above).

**`appium-self-hosted-device` (real phone):**

- **Default:** runs on **every push** to `main` **and** on **Actions → Run workflow**. This workflow does **not** use **`pull_request`**; only **pushes** to `main` (and manual runs) start Actions here.  
- Your self-hosted runner must be **online** with the phone on USB (`adb devices` → **`device`** on that PC), or the job stays **Queued** and nothing runs on the phone.  
- **Optional:** set **`SKIP_DEVICE_E2E_ON_PUSH=true`** to stop scheduling this job on **push** only (manual runs still execute it).

1. Put your self-hosted runner **online** (see below).  
2. Push to `main`, or use **Run workflow** anytime.

Open the run → job **`appium-self-hosted-device`** → logs. On success, download artifact **`appium-artifacts-self-hosted`**.

### Validating with an empty commit

Yes. A push to `main` triggers **Mobile E2E (Appium)** with **`appium-self-hosted-device`** by default (unless **`SKIP_DEVICE_E2E_ON_PUSH=true`**). Emulator/ubuntu run only if you set **`RUN_EMULATOR_E2E_ON_PUSH`** / **`RUN_UBUNTU_APPIUM_SMOKE_ON_PUSH`** to **`true`**.

```bash
git commit --allow-empty -m "ci: validate workflow on push"
git push <remote> main
```

There is **no** separate **`RUN_DEVICE_E2E_ON_PUSH`** variable in this repo anymore: the real-device job is **on by default for every push**; use **`SKIP_DEVICE_E2E_ON_PUSH`** only when you want pushes to **skip** that job while the runner is down.

### Job stuck on “Queued”?

That happens when a **`self-hosted`** job is waiting for a runner (e.g. after a **push** while the runner is **offline**). Set **`SKIP_DEVICE_E2E_ON_PUSH=true`** if you want pushes to skip this job until the runner is fixed, or cancel the queued run.

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

### Full `testng.xml` on a real device (all tests in the suite)

`appiumtests/testng.xml` is the single suite Maven uses (`surefire.suiteXmlFiles`). On a **real device** it runs **every `@Test` in the classes listed there** — currently **`Tests.Tests`** then **`Tests.OPC`**, one Appium session for the whole suite (`AppiumSuiteListener` + `BaseClass`).

- **Local (this PC + USB):** repo root → `.\run-testng-device.ps1` (Windows) or `bash scripts/run-appium-device-e2e.sh` (Unix). Same command line CI uses for the device job.  
- **CI:** job **`appium-self-hosted-device`** (push to `main` or **Run workflow**), runner on the same machine as the phone, **`SKIP_DEVICE_E2E_ON_PUSH`** not set to `true` on push.

Other classes under `src/test/java/Tests/` (for example `Job_Board`, `Job_Management`) are **not** in `testng.xml` by default — they reuse similar priorities to `Tests` and would need a **separate** suite file or refactored priorities before being combined safely.

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
