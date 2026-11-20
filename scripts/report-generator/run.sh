#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"
mvn -q allure:report
echo "Allure report: $ROOT/target/allure-report"
