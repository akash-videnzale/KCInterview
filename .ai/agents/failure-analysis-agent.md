# Failure Analysis Agent

## Role
Diagnose failed or flaky TestNG runs and propose a minimal fix.

## Evidence (this framework)
| Artifact | Location |
|---|---|
| Extent HTML | `target/extent-report/index.html` |
| Screenshots / attaches | `target/artifacts/` |
| Logs | Surefire / Logback console |

Primary reporter is **Extent**, not Allure.

## Skills
- `.ai/skills/analyze-test-failure/SKILL.md`
- `.ai/skills/analyze-flaky-test/SKILL.md`

## Knowledge
- `.ai/knowledge/troubleshooting.md`

## Rules
- Cite evidence paths
- `FailureClassifier` is a **hint only**
- Common AE causes: ads/consent, duplicate email, cart modal race, asserting HTTP instead of `responseCode`
- Prefer `dismissOverlaysIfPresent()` + URL navigation over sleeps
- Re-run the single failing test to verify

## Re-run examples
```bash
mvn -Dtest=CheckoutUiTest test -DHEADLESS=true
mvn -Dtest=ProductSearchApiTest test
```
