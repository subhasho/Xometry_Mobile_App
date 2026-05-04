#!/usr/bin/env bash
# Run full TestNG suite on the first authorized USB device (self-hosted / local CI).
set -euo pipefail
ROOT="${GITHUB_WORKSPACE:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"
cd "$ROOT"

command -v adb >/dev/null 2>&1 || { echo "adb not on PATH"; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo "mvn not on PATH"; exit 1; }

adb start-server
echo "adb devices:"
adb devices

if ! adb devices | grep -q $'\tdevice'; then
  echo "No device in 'device' state (authorize USB debugging)."
  exit 1
fi

if [[ -z "${APPIUM_UDID:-}" ]]; then
  export APPIUM_UDID
  APPIUM_UDID="$(adb devices | awk 'NR>1 && $2=="device" { print $1; exit }')"
  echo "APPIUM_UDID not set — using first device: ${APPIUM_UDID}"
fi

PORT="${APPIUM_PORT:-4723}"
BASE="${APPIUM_BASE_PATH:-/wd/hub}"
STATUS_URL="http://127.0.0.1:${PORT}${BASE}/status"

if ! curl -fsS "$STATUS_URL" >/dev/null 2>&1; then
  echo "Installing / starting Appium..."
  npm i -g appium@latest
  appium -v
  appium driver install uiautomator2 2>/dev/null || true
  if appium server --help >/dev/null 2>&1; then
    nohup appium server --address 127.0.0.1 --port "$PORT" --base-path "$BASE" > appium.log 2>&1 &
  else
    nohup appium --base-path "$BASE" > appium.log 2>&1 &
  fi
  for _ in $(seq 1 30); do
    if curl -fsS "$STATUS_URL" >/dev/null 2>&1; then
      echo "Appium ready."
      break
    fi
    sleep 2
  done
  curl -fsS "$STATUS_URL" >/dev/null 2>&1 || { echo "Appium failed to start. tail appium.log:"; tail -n 80 appium.log; exit 1; }
else
  echo "Appium already listening at $STATUS_URL"
fi

SUITE="${TESTNG_SUITE:-testng.xml}"
echo "APPIUM_UDID=${APPIUM_UDID:-}"
echo "Running mvn test with suite: $SUITE (cwd: $ROOT)"
mvn -B -f appiumtests/pom.xml test -Dsurefire.suiteXmlFiles="$SUITE"
