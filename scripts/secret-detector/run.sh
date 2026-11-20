#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"
# Deterministic ripgrep-based secret heuristics (no network)
PATTERN='(password\s*=\s*\"[^\"]+\"|api[_-]?key\s*=\s*\"[^\"]+|Authorization:\s*Bearer\s+[A-Za-z0-9._-]+)'
if rg -n -i --glob '!target/**' --glob '!.git/**' -e "$PATTERN" src evaluations scripts 2>/dev/null; then
  echo "SECRET DETECTOR: potential secrets found" >&2
  exit 1
fi
echo "SECRET DETECTOR: no hardcoded secret patterns found in src/evaluations/scripts"
