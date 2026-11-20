# CLAUDE.md

Instructions for Claude Code and compatible coding agents working in this repository.

## Project

`agentic-qa-automation-framework` — Java 21 Selenium TestNG REST Assured QA platform for Automation Exercise.

## Before any code change

1. Read `AGENTS.md`
2. Read the relevant skill under `.ai/skills/*/SKILL.md`
3. Confirm endpoints/selectors against `docs/recon/automation-exercise-findings.md`

## Hard constraints

- TestNG only (no JUnit)
- TestNG Assert/SoftAssert only (no AssertJ/Hamcrest)
- Selenium Java WebDriver for UI (no Playwright)
- No `Thread.sleep` / `TimeUnit.sleep` in tests
- No hardcoded credentials or environment URLs in tests
- No direct `new ChromeDriver()` / browser-options bootstrap / `RestAssured.given()` inside tests
- No LLM API calls inside TestNG test methods
- Optional LLM orchestration is allowed only via `com.agenticqa.agents.AgentCli`

## Preferred workflows

- New test → `.ai/workflows/new-test.md`
- Failure → `.ai/workflows/debug-failure.md`
- Review → `.ai/workflows/review-contribution.md`
- Requirement → cases (LLM) → `docs/ai-platform/llm-runtime-agents.md`

## Completion gate

Run:

```bash
mvn -DskipTests test-compile
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.AgentValidator" -Dexec.args="src/test/java"
```

Do not claim success if AgentValidator reports ERROR severity violations.
