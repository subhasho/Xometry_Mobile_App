#!/usr/bin/env python3
"""
Gate a deployment / CI run on emulator E2E results (Surefire testng-results.xml).

Exits non-zero if:
  - report is missing
  - any TestNG test failed
  - zero tests passed (e.g. everything skipped before real assertions)

Usage:
  python3 scripts/validate-testng-for-deployment.py [path/to/testng-results.xml]
"""
from __future__ import annotations

import os
import sys
import xml.etree.ElementTree as ET

DEFAULT_REPORT = "appiumtests/target/surefire-reports/testng-results.xml"


def main() -> int:
    path = sys.argv[1] if len(sys.argv) > 1 else DEFAULT_REPORT

    if not os.path.isfile(path):
        print(f"::error::No TestNG report at {path} — E2E did not produce Surefire output (emulator or mvn failed early).")
        return 1

    root = ET.parse(path).getroot()
    passed = root.get("passed", "0")
    failed = root.get("failed", "0")
    skipped = root.get("skipped", "0")
    total = root.get("total", "0")

    def n(s: str) -> int:
        try:
            return int(s)
        except ValueError:
            return 0

    pn, fn, sn, tn = n(passed), n(failed), n(skipped), n(total)

    print(f"::notice::Deployment check — TestNG passed={pn} failed={fn} skipped={sn} total={tn}")

    if fn > 0:
        print(f"::error::{fn} TestNG test(s) failed on emulator — do not treat this build as validated for deployment.")
        return 1

    if pn == 0:
        print(
            "::error::Zero tests passed (all skipped or nothing ran). "
            "Emulator E2E did not validate the app — check APK, package/activity, and earlier steps."
        )
        return 1

    print("::notice::Emulator E2E gate OK — at least one test passed and none failed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
