---
name: analyze-test-coverage
description: Compare requirements to automation-inventory; list covered scripts and gaps by priority.
---

# analyze-test-coverage

## Purpose
Show **what is already automated** (tests + data + scripts) vs gaps. Depth over breadth.

## When to use
Planning, Part 2, or step 2–4 of agentic delivery chain.

## Required inputs
Optional: new requirement text. Always use inventory + requirements docs.

## Preconditions
- `docs/requirements/AUTOMATION_REQUIREMENTS.md`
- `.ai/knowledge/automation-inventory.md`

## Workflow
1. Paste/summarize **full inventory tables** (UI, API, data) from `automation-inventory.md`
2. Mark assignment P0 journeys: Signup, Discovery, Checkout — must be COVERED
3. List P1 covered (brands, negatives, user detail, …)
4. Recommend **at most 2–3** P2 gaps for next work
5. For each gap: suggested skill + reuse assets

## Output format
```markdown
## Already automated (scripts)
### UI test scripts
| Method | Path | Priority |
### API test scripts
| Method | Path | Priority |
### Test data in use
| Factory/constant | Used by |

## Coverage vs requirements
| REQ | Status | Script |

## Suggested next automation (max 3)
1. (P2) … skill: create-ui-test … reuse: …
```

## Prohibited
- Claiming coverage without citing `Class#method`
- Proposing to rebuild existing P0 tests
