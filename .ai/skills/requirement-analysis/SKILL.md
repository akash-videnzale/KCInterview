---
name: requirement-analysis
description: Map a requirement to inventory + prioritized gap test cases (no duplicate automation).
---

# requirement-analysis

## Purpose
Turn a requirement into a plan that **starts from current automation**, then lists prioritized gaps only.

## When to use
Before any create-* skill; always as step 1–5 of the agentic delivery chain.

## Required inputs
Requirement text or `docs/requirements/AUTOMATION_REQUIREMENTS.md` section; optional journey focus.

## Preconditions (read in order)
1. `docs/requirements/AUTOMATION_REQUIREMENTS.md`
2. `.ai/knowledge/automation-inventory.md` ← **mandatory**
3. `docs/recon/automation-exercise-findings.md` if AUT-facing
4. `.ai/knowledge/architecture.md`

## Workflow
1. **Dump inventory** in the reply (UI tests, API tests, test data, pages/clients) from `automation-inventory.md`
2. Map ask → `REQ-*` IDs
3. Mark each REQ: COVERED (cite `Class#method`) or GAP
4. For gaps only, draft cases with **P0 / P1 / P2**
5. Name reuse targets (existing test class / page / client)
6. Stop here unless user asked to automate (then hand off to `agentic-delivery-chain` or create-* skills)

## Output format
```markdown
## Current automation inventory
### UI
- …
### API
- …
### Test data
- …

## Requirement mapping
| REQ | Status | Evidence |
|---|---|---|
| REQ-… | COVERED / GAP | Class#method or — |

## Prioritized test cases (gaps only)
### TC-001 (P0) …
```

## Prohibited
- Proposing a new Signup/Discovery/Checkout happy-path that already exists
- Inventing endpoints
- Skipping the inventory section
