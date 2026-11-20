---
name: review-test-quality
description: Review-only gate for agent-authored tests (assignment + framework rules).
---

# review-test-quality

## Purpose
ACCEPT / REJECT a contribution. Review only — do not silently rewrite code unless asked.

## When to use
After create-* skills; before saying “done”; QA Reviewer agent default.

## Required inputs
Diff or file paths.

## Checklist (assignment + framework)
1. **Critical flows still healthy:** Signup, Product Discovery, Checkout (+ API counterparts)
2. **Stack correct:** Selenium/TestNG — not Playwright/TS
3. **Layers:** tests thin; pages/clients/factories hold details
4. **Asserts:** `UiAssertions` / `ApiResponseAssertions` / meaningful `assert*` helpers
5. **Data:** unique factories + cleanup
6. **No sleeps / hardcoded secrets / raw WebDriver / raw RestAssured in tests**
7. **Exact values** where we claim exactness (`KnownProducts`, `ApiMessages`)
8. Run **AgentValidator** — ERROR ⇒ REJECT

## Workflow
1. `mvn -q test-compile`
2. Run AgentValidator on touched tests
3. Skim for prohibited patterns
4. Output ACCEPT or REJECT with findings

## Output
```text
RESULT: ACCEPTED | REJECTED
Findings:
- [FAIL] …
- [WARN] …
Required fixes:
- …
```

## Prohibited for reviewer
- Silent drive-by refactors unrelated to findings
- Approving code that invents AE APIs
