# Agent-generated sandbox

## Rule

**AI agents must put all new automation artifacts under `agent-generated/`.**

| Allowed | Forbidden (unless human says “promote”) |
|---|---|
| `agent-generated/cases/*.md` | `src/test/java/...` |
| `agent-generated/requirements/*` | `src/main/java/...` |
| `agent-generated/src/main/java/...` | Editing golden inventory tests in place |
| `agent-generated/src/test/java/...` | |
| `agent-generated/runs/...` | |

Framework code in `src/` is curated. Mixing agent drafts there pollutes the product.

---

## What goes where

| Artifact | Path |
|---|---|
| Prioritized test cases | `agent-generated/cases/<feature>-cases.md` |
| Requirement snapshot | `agent-generated/requirements/<feature>.md` |
| Draft UI/API tests | `agent-generated/src/test/java/com/agenticqa/tests/...` |
| Draft pages/clients/factories | `agent-generated/src/main/java/com/agenticqa/...` |
| AgentCli / pipeline run | `agent-generated/runs/run-<timestamp>/` |

Use the **same package names** as the framework so promotion is move-only.

---

## Agent reply must say

```markdown
## Output location
- Cases: agent-generated/cases/...
- Java drafts: agent-generated/src/...
- Note: not written to src/ (framework) until promotion
```

---

## Promotion

Only when explicitly requested: copy draft → `src/`, update `automation-inventory.md`, run tests.

See `agent-generated/README.md`.
