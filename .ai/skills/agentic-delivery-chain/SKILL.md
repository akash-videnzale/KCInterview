---
name: agentic-delivery-chain
description: Full chain — requirement → inventory → prioritized cases → automate → validate.
---

# agentic-delivery-chain

## Purpose
Run the **agentic delivery system**: read requirements and knowledge, list what is already automated, produce prioritized test cases for gaps only, then automate and validate.

## When to use
- New feature / Part 2 extension ask
- “Generate tests for …” / “Automate this requirement”
- Starting any multi-step agent session

## Required inputs
Requirement text or path under `docs/requirements/` (plus optional focus: ui | api | both).

## Preconditions
Must read, in order:
1. `docs/requirements/AUTOMATION_REQUIREMENTS.md`
2. `.ai/knowledge/automation-inventory.md`
3. Relevant knowledge (`architecture`, `api-contracts` or `selenium-patterns`)
4. `docs/recon/automation-exercise-findings.md` if touching AUT

## Workflow

### Step A — Inventory dump (mandatory in your reply)
Copy the structure from `automation-inventory.md` and list:
- Existing **UI tests** (class#method, P0/P1)
- Existing **API tests**
- Existing **test data** (factories / KnownProducts / messages)
- Existing **pages/clients** you will reuse

### Step B — Gap analysis
Map the ask to `REQ-*` IDs. Mark each: **COVERED** (link inventory row) or **GAP**.

### Step C — Prioritized test cases (gaps only)
For each gap output:

```markdown
### TC-xxx
- Priority: P0 | P1 | P2
- REQ: REQ-…
- Layer: ui | api | integration
- Title:
- Steps:
- Expected:
- Data: factory / Known*
- Reuse: existing page/client/test class
- Automate?: yes | defer
```

Do **not** invent cases that duplicate inventory methods.

### Step D — Automate into sandbox (P0 first, then P1)
Write **only** under `agent-generated/` (see `.ai/knowledge/agent-generated-sandbox.md`):
- Cases → `agent-generated/cases/`
- Java tests → `agent-generated/src/test/java/...`
- Pages/clients/factories → `agent-generated/src/main/java/...`
- UI/API skills: `create-ui-test` / `create-api-test` / …
- **Never** write drafts into framework `src/main` or `src/test`

### Step E — Validate drafts
```bash
mvn -q -DskipTests exec:java \
  -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="agent-generated/src/test/java"
```
Promotion into `src/` + `mvn test` only when the human asks.

### Step F — Summary
Paths under `agent-generated/`, TCs drafted, validation result, leftover P2 backlog.

## Output format (full reply)
```markdown
## 1. Current automation inventory
…

## 2. Requirement mapping
…

## 3. Prioritized test cases (gaps only)
… (also saved to agent-generated/cases/)

## 4. Automation performed (sandbox only)
- agent-generated/src/...

## 5. Validation
…

## 6. Backlog (not automated this run)
…
```

## Prohibited
- Skipping the inventory section
- Writing agent code into `src/main` or `src/test`
- Re-writing Signup/Discovery/Checkout happy paths that already exist
- Playwright / JUnit / AssertJ
- LLM calls inside `@Test`
