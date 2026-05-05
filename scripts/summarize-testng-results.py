#!/usr/bin/env python3
"""Append TestNG surefire report summary to GITHUB_STEP_SUMMARY (GitHub Actions job summary)."""
from __future__ import annotations

import os
import sys
import xml.etree.ElementTree as ET


def main() -> int:
    path = sys.argv[1] if len(sys.argv) > 1 else "appiumtests/target/surefire-reports/testng-results.xml"
    summary_file = os.environ.get("GITHUB_STEP_SUMMARY")

    def w(line: str = "") -> None:
        print(line)
        if summary_file:
            with open(summary_file, "a", encoding="utf-8") as f:
                f.write(line + "\n")

    if not os.path.isfile(path):
        w("## TestNG (`testng.xml`)")
        w("")
        w(f"_No report at `{path}` — tests may have failed before Surefire wrote results._")
        return 0

    root = ET.parse(path).getroot()
    passed = root.get("passed", "0")
    failed = root.get("failed", "0")
    skipped = root.get("skipped", "0")
    ignored = root.get("ignored", "0")
    total = root.get("total", "0")

    w("## TestNG suite — pass / fail / skip (emulator run)")
    w("")
    w("| Passed | Failed | Skipped | Ignored | Total |")
    w("| --- | --- | --- | --- | --- |")
    w(f"| **{passed}** | **{failed}** | **{skipped}** | **{ignored}** | **{total}** |")
    w("")

    passed_rows: list[tuple[str, str]] = []
    failed_rows: list[tuple[str, str]] = []
    other_rows: list[tuple[str, str, str]] = []

    for cls_el in root.iter("class"):
        cname = cls_el.get("name", "")
        for tm in cls_el:
            if tm.tag != "test-method" or tm.get("is-config") == "true":
                continue
            name = tm.get("name", "")
            status = tm.get("status", "")
            key = (cname, name)
            if status == "PASS":
                passed_rows.append(key)
            elif status == "FAIL":
                failed_rows.append(key)
            else:
                other_rows.append((cname, name, status))

    if failed_rows:
        w("### Failed")
        w("")
        for cname, name in failed_rows:
            w(f"- `{cname}.{name}`")
        w("")

    if other_rows:
        w("### Skipped / other")
        w("")
        for cname, name, status in other_rows:
            w(f"- `{cname}.{name}` — {status}")
        w("")

    if passed_rows:
        w(f"### Passed ({len(passed_rows)} methods)")
        w("")
        for cname, name in passed_rows:
            w(f"- `{cname}.{name}`")
        w("")

    # Surface counts in the Actions log (Annotations / search).
    print(f"::notice::TestNG — passed={passed} failed={failed} skipped={skipped} total={total}")
    try:
        fc = int(failed)
    except ValueError:
        fc = 0
    if fc > 0:
        print(f"::error::TestNG reported {failed} failed test(s). See job Summary and Surefire artifact.")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
