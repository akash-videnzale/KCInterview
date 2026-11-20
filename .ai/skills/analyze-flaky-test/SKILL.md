---
name: analyze-flaky-test
description: Find non-determinism (ads, shared state, sleeps, email collisions).
---

# analyze-flaky-test

## Purpose
Stabilize intermittent failures on Automation Exercise.

## When to use
Test passes/fails across runs without code changes.

## Common flake sources on this AUT
1. Consent/ad overlays stealing clicks → `dismissOverlaysIfPresent()` + URL navigation
2. Shared/reused email → always `RegistrationDataFactory.uniqueUser()`
3. Fixed waits → replace with `waitVisible` / ExpectedConditions
4. Cart modal race → wait for `#cartModal` before View Cart
5. Shared static WebDriver → must use per-method `UiBaseTest` session
6. Parallel without unique data → factories + ThreadLocal sessions

## Workflow
1. Run the test 3–5 times headed once to observe
   ```bash
   for i in 1 2 3 4 5; do mvn -Dtest=ProductDiscoveryUiTest test -DHEADLESS=true || break; done
   ```
2. `scripts/fixed-wait-detector/run.sh`
3. Grep for static mutable fields / hardcoded emails
4. Tighten locators to `data-qa` / `UiMessages`
5. Re-run until stable

## Prohibited
- “Fixing” flake with longer sleeps
- Disabling the test without root cause
