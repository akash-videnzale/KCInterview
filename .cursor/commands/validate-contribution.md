# Validate Contribution

Follow skill: `.ai/skills/validate-agent-contribution/SKILL.md`

```bash
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="agent-generated/src/test/java"
```

Confirm no unexpected agent writes under `src/`. Report ACCEPT/REJECT.
