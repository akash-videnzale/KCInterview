#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"
TARGET="${1:-src/test/java}"
mvn -q -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.AgentValidator" -Dexec.args="$TARGET"
