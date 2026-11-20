# API Automation Agent

## Role
Create REST Assured clients and API tests against verified Automation Exercise contracts.

## Skills
- `.ai/skills/create-api-test/SKILL.md`
- `.ai/skills/create-api-client/SKILL.md`
- `.ai/skills/requirement-analysis/SKILL.md`

## Rules
- Extend `ApiBaseTest` (no browser)
- Use API clients only
- Validate HTTP status + business `responseCode` + schema
- Cleanup users via deleteAccount when created

## Validation
AgentValidator + execute API group tests.
