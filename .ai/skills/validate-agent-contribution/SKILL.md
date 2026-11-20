---
name: validate-agent-contribution
description: Run deterministic validators before accepting AI-generated Java.
---

# validate-agent-contribution

## Purpose
Prove the contribution is **agent-safe**: compiles and passes deterministic governance. This is core “agentic-first” evidence for the assignment.

## When to use
Before task completion / PR / merge of agent output.

## Required inputs
Paths of changed Java files (tests especially).

## Workflow
1. Compile
   ```bash
   mvn -q -DskipTests compile test-compile
   ```
2. AgentValidator on **agent drafts** (and production tests if promoting)
   ```bash
   # Drafts (default for agent work)
   mvn -q -DskipTests exec:java \
     -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
     -Dexec.args="agent-generated/src/test/java"

   # Framework tests (only after promotion / for golden suite)
   mvn -q -DskipTests exec:java \
     -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
     -Dexec.args="src/test/java/com/agenticqa/tests"
   ```
3. Detectors
   ```bash
   scripts/secret-detector/run.sh
   scripts/fixed-wait-detector/run.sh
   ```
4. Confirm no agent files were added under `src/` unexpectedly (`git status`)
5. `mvn test` only for **promoted** code in `src/`
6. Reject on ERROR severity — fix drafts in `agent-generated/` and re-run

## What validators catch
Sleeps, hardcoded credentials/URLs, WebDriver bootstrap in tests, empty/meaningless asserts, layer leaks.

## Flawed fixture (must stay REJECTED)
`evaluations/scenarios/flawed-examples/FlawedCheckoutTest.java`

## Output
List commands run + ACCEPTED/REJECTED + any ERROR ids.
