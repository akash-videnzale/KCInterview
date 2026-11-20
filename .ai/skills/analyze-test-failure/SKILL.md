---
name: analyze-test-failure
description: Diagnose a failed TestNG run using logs, Extent, and screenshots.
---

# analyze-test-failure

## Purpose
Find root cause of a failed UI/API test and propose a **minimal** fix.

## When to use
Surefire/TestNG failure; unclear stack trace.

## Required inputs
Test name; optional run flags (`HEADLESS`, groups).

## Where evidence lives
| Artifact | Path |
|---|---|
| Extent report | `target/extent-report/index.html` |
| Screenshots / text | `target/artifacts/` |
| Console logs | Surefire / Logback output |

(Allure is not the primary reporter in this framework — use Extent.)

## Workflow
1. Read assertion message + stack
2. Open failure screenshot / attached text if UI
3. For API: check JSON `responseCode` / body in Extent (not only HTTP)
4. Use `FailureClassifier` as a **hint only**
5. Common AE causes: ads/consent, duplicate email, missing wait for `#cartModal`, wrong business code
6. Fix the smallest page/client/assert issue; re-run the single test

## Re-run
```bash
mvn -Dtest=SignupUiTest#shouldRegisterUniqueUserAndDeleteAccount test -DHEADLESS=true
mvn -Dtest=ProductListApiTest test
```

## Prohibited
- Papering over flakes with `Thread.sleep`
- Broad unrelated refactors while “debugging”
