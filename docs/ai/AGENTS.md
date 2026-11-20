# AGENTS.md — Agentic QA Automation Framework

## Mission

Build and maintain an enterprise-grade, agentic-first QA platform for https://automationexercise.com/ using **Java 21 + Selenium for Java + TestNG + TestNG Assert/SoftAssert + REST Assured**.

AI agents are first-class contributors, but **not trusted contributors**. Skills empower them; deterministic validators gate their work.

There are two AI layers:

1. **Coding-agent layer** — skills/rules/instructions for IDE agents writing framework code.
2. **LLM runtime pipeline** — optional GPT/Claude/DeepSeek orchestration via `AgentCli` (requirements → cases → drafts → execute → report).

**TestNG tests must not call LLM APIs during assertions.** Runtime LLMs stay in `com.agenticqa.agents.*`.

## Stack (mandatory)

| Area | Choice |
|---|---|
| Language | Java 21 |
| UI | Selenium Java |
| Runner | TestNG only |
| Assertions | TestNG Assert / SoftAssert |
| API | REST Assured + JSON Schema |
| Build | Maven |
| Reporting | ExtentReports |
| Data | Java Faker factories |
| Config | YAML + env |
| Governance | JavaParser AgentValidator |
| Optional LLM | OpenAI / Anthropic / DeepSeek via `.env` |

Do **not** use JUnit, AssertJ, or Hamcrest.

## Layout

- `src/main/java/com/agenticqa/core` — browser, config, logging, reporting
- `src/main/java/com/agenticqa/ui/pages|components|api|factories` — automation services
- `src/main/java/com/agenticqa/agents` — LLM runtime pipeline + CLI
- `src/test/java/com/agenticqa/tests` — ui / api / integration / unit
- `.ai/skills` — coding-agent skills (`SKILL.md`)
- `.ai/agents` — specialized agent roles (+ `runtime/` for LLM agents)
- `scripts/*` — deterministic governance scripts
- `agent-output/` — LLM pipeline artifacts (gitignored)

## Operating rules

1. Read `.ai/skills/framework-onboarding/SKILL.md` before coding.
2. Inspect `docs/recon/automation-exercise-findings.md` — do not invent APIs/selectors.
3. UI tests extend `UiBaseTest`; API tests extend `ApiBaseTest`.
4. Use factories for data; cleanup accounts when created.
5. Before finishing, run AgentValidator and fix ERROR violations.
6. Treat LLM-generated Java under `agent-output/` as drafts until validated and reviewed.

## Commands map (coding agents)

| Intent | Skill | Cursor command |
|---|---|---|
| Analyze requirement | requirement-analysis | `/analyze-requirement` |
| UI test | create-ui-test | `/create-ui-test` |
| API test | create-api-test | `/create-api-test` |
| Page object | create-page-object | `/create-page-object` |
| Fixture | create-testng-fixture | `/create-fixture` |
| Debug failure | analyze-test-failure | `/debug-failure` |
| Review | review-test-quality | `/review-test` |
| Validate | validate-agent-contribution | `/validate-contribution` |

## LLM runtime pipeline

Keys in local `.env` only (see `.env.example`).

Cheapest verified defaults:

| Role | Provider | Model |
|---|---|---|
| Requirement / testgen | OpenAI | `gpt-5.4-nano` |
| Reviewer / automators | Anthropic | `claude-haiku-4-5` |
| Report analysis | DeepSeek | `deepseek-flash` |

```bash
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"

mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="full --requirement docs/examples/sample-requirement.md --groups smoke"
```

Docs: `docs/ai-platform/llm-runtime-agents.md` and `.ai/agents/runtime/README.md`

## Validation

```bash
mvn -DskipTests compile test-compile
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.AgentValidator" -Dexec.args="src/test/java"
scripts/secret-detector/run.sh
scripts/fixed-wait-detector/run.sh
```

## Reviewer policy

QA Reviewer agent is **review-only by default**. Propose changes; do not silently rewrite reviewed code.
