#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"
mvn -q -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.ArchitectureValidator" -Dexec.args="."
