#!/usr/bin/env bash
# Runs inside reactivecircus/android-emulator-runner "script" (emulator is already booted).
# Set APK_URL (e.g. from secret APPIUM_E2E_APK_URL) to a direct .apk download for com.xometry.workcenter.preview.stage.
set -euxo pipefail
ROOT="${GITHUB_WORKSPACE:-$(pwd)}"
cd "$ROOT"

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

export APPIUM_PLATFORM_VERSION="${APPIUM_PLATFORM_VERSION:-11}"
export APPIUM_DEVICE_NAME="${APPIUM_DEVICE_NAME:-Android Emulator}"

if [[ -n "${APK_URL:-}" ]]; then
  echo "Installing APK from CI secret..."
  curl -fSL "$APK_URL" -o /tmp/e2e-stage.apk
  adb install -r -t /tmp/e2e-stage.apk || adb install -r /tmp/e2e-stage.apk
else
  echo "::warning::APK_URL / secret APPIUM_E2E_APK_URL is empty. Install the stage app on the AVD first or set the secret to a direct .apk URL."
fi

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
mvn -B -f appiumtests/pom.xml test -Dsurefire.suiteXmlFiles="$SUITE"
