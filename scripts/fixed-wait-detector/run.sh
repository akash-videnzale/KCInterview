#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"
if rg -n --glob '!target/**' -e 'Thread\.sleep\s*\(|waitForTimeout\s*\(' src/main/java src/test/java 2>/dev/null; then
  echo "FIXED WAIT DETECTOR: prohibited waits found" >&2
  exit 1
fi
echo "FIXED WAIT DETECTOR: clean"
