#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
REPORT="$ROOT/target/extent-report/index.html"
if [[ ! -f "$REPORT" ]]; then
  echo "No Extent report found at $REPORT. Run tests first: mvn test -Dgroups=ui" >&2
  exit 1
fi
echo "Opening $REPORT"
open "$REPORT"
