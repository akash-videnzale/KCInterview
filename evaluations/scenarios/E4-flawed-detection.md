# E4 — Detect intentionally flawed Java (static)

```bash
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.validation.AgentValidator" \
  -Dexec.args="evaluations/scenarios/flawed-examples/FlawedCheckoutTest.java"
```

Expected: RESULT REJECTED, multiple ERROR rule IDs (waits, browser bootstrap, credentials/URL, empty test, etc.).

Also covered by `AgentValidatorTest`.
