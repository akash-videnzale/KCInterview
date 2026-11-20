# Failure Analysis Workflow
1. Collect failure info (surefire + Allure)
2. Read logs under target/logs
3. Inspect screenshot in target/artifacts
4. Inspect failure screenshot / page-source artifacts
5. Classify failure (heuristic)
6. Identify likely root cause
7. Suggest correction
8. Re-run single test to verify
