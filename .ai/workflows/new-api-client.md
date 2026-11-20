# New API Client Workflow

1. Confirm endpoint in `docs/recon/automation-exercise-findings.md`
2. Prefer extending existing `*ApiClient` in framework; draft new code under **`agent-generated/src/main/java/com/agenticqa/api/`**
3. Add Jackson POJO models (getters/setters) as needed
4. Add/reuse `ApiMessages` + `ApiResponseAssertions`
5. Add draft API test under `agent-generated/src/test/java/...`
6. Run AgentValidator on `agent-generated/src/test/java`
7. Promote into `src/` only when asked; then `mvn test -Dgroups=api`

Skill: `.ai/skills/create-api-client/SKILL.md` + `create-api-test`
