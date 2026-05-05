#!/usr/bin/env bash
# Runs inside reactivecircus/android-emulator-runner "script" (emulator is already booted).
# Requires APK_URL (secret APPIUM_E2E_APK_URL) — direct HTTPS URL to the .apk.
# Optional: APPIUM_APP_PACKAGE / APPIUM_APP_ACTIVITY (GitHub Actions vars) must match the APK.
set -euxo pipefail
ROOT="${GITHUB_WORKSPACE:-$(pwd)}"
cd "$ROOT"

if [[ -z "${APK_URL:-}" ]]; then
  echo "::error::APK_URL is not set. Add repository secret APPIUM_E2E_APK_URL with a direct download link to the Android .apk."
  exit 1
fi

# Recover from transient "adb: device offline" after the runner finishes boot polling.
for _ in $(seq 1 30); do
  if adb devices 2>/dev/null | grep -qE "emulator-[0-9]+[[:space:]]+device"; then
    break
  fi
  adb reconnect 2>/dev/null || true
  sleep 2
done

adb devices
export APPIUM_UDID
APPIUM_UDID="$(adb devices | awk '$2=="device" && $1 ~ /^emulator/ { print $1; exit }')"
if [[ -z "${APPIUM_UDID}" ]]; then
  APPIUM_UDID="$(adb devices | awk 'NR>1 && $2=="device" { print $1; exit }')"
fi
if [[ -z "${APPIUM_UDID}" ]]; then
  echo "No Android device/emulator in adb 'device' state."
  exit 1
fi
echo "Using UDID=$APPIUM_UDID"

export APPIUM_PLATFORM_VERSION="${APPIUM_PLATFORM_VERSION:-}"
export APPIUM_DEVICE_NAME="${APPIUM_DEVICE_NAME:-Android Emulator}"

echo "Installing APK from CI..."
curl -fSL "$APK_URL" -o /tmp/e2e-stage.apk
if [[ ! -s /tmp/e2e-stage.apk ]]; then
  echo "::error::Downloaded APK is empty — check APPIUM_E2E_APK_URL (must be a direct file URL, not an HTML page)."
  exit 1
fi
python3 - <<'PY' || { echo "::error::Downloaded file is not a valid APK (ZIP). URL may point to HTML or wrong asset."; exit 1; }
import zipfile
z = zipfile.ZipFile("/tmp/e2e-stage.apk", "r")
names = z.namelist()
if not any(n.endswith("AndroidManifest.xml") for n in names):
    raise SystemExit("missing AndroidManifest in archive")
PY
adb install -r -t /tmp/e2e-stage.apk || adb install -r /tmp/e2e-stage.apk
export APK_PATH="/tmp/e2e-stage.apk"

npm i -g appium@latest
appium -v
appium driver install uiautomator2 2>/dev/null || true

PORT="${APPIUM_PORT:-4723}"
BASE="${APPIUM_BASE_PATH:-/wd/hub}"
STATUS_URL="http://127.0.0.1:${PORT}${BASE}/status"

if appium server --help >/dev/null 2>&1; then
  appium server --address 127.0.0.1 --port "$PORT" --base-path "$BASE" > appium.log 2>&1 &
else
  appium --base-path "$BASE" -p "$PORT" > appium.log 2>&1 &
fi

for _ in $(seq 1 35); do
  if curl -fsS "$STATUS_URL" >/dev/null 2>&1; then
    echo "Appium ready at $STATUS_URL"
    break
  fi
  sleep 2
done
curl -fsS "$STATUS_URL" >/dev/null 2>&1 || { tail -n 100 appium.log || true; exit 1; }

SUITE="${TESTNG_SUITE:-testng.xml}"
if [[ ! -f "appiumtests/${SUITE}" ]]; then
  echo "::error::Suite file appiumtests/${SUITE} not found (TESTNG_SUITE=${SUITE})."
  exit 1
fi
echo "Running TestNG suite: ${SUITE} (module appiumtests, all @Test in classes listed in suite)"
mvn -B -f appiumtests/pom.xml test -Dsurefire.suiteXmlFiles="$SUITE"
