# Failure Analysis Workflow

1. Collect failure info (Surefire + **Extent** report)
2. Open `target/extent-report/index.html`
3. Inspect screenshots / text under `target/artifacts/`
4. For API: check JSON `responseCode` (not only HTTP)
5. Classify failure (`FailureClassifier` is heuristic only)
6. Identify likely root cause (ads/consent, waits, data collision, wrong assert)
7. Suggest minimal correction
8. Re-run single test to verify:
   ```bash
   mvn -Dtest=YourTest#yourMethod test -DHEADLESS=true
   ```

Skill: `.ai/skills/analyze-test-failure/SKILL.md`  
Knowledge: `.ai/knowledge/troubleshooting.md`
