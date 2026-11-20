# AGENTS.md — Agentic QA Automation Framework

## Mission

Build and maintain an enterprise-grade, agentic-first QA platform for https://automationexercise.com/ using **Java 21 + Playwright for Java + TestNG + TestNG Assert/SoftAssert + REST Assured**.

AI agents are first-class contributors, but **not trusted contributors**. Skills empower them; deterministic validators gate their work.

There are two AI layers:

1. **Coding-agent layer** — skills/rules/instructions for IDE agents writing framework code.
2. **LLM runtime pipeline** — optional GPT/Claude/DeepSeek orchestration via `AgentCli` (requirements → cases → drafts → execute → report).

**TestNG tests must not call LLM APIs during assertions.** Runtime LLMs stay in `com.agenticqa.agents.*`.

## Stack (mandatory)

| Area | Choice |
|---|---|
| Language | Java 21 |
| UI | Playwright Java |
| Runner | TestNG only |
| Assertions | TestNG Assert / SoftAssert |
| API | REST Assured + JSON Schema |
| Build | Maven |
| Reporting | Allure TestNG |
| Data | Java Faker factories |
| Config | YAML + env |
| Governance | JavaParser AgentValidator |
| Optional LLM | OpenAI / Anthropic / DeepSeek via `.env` |

Do **not** use JUnit, AssertJ, Hamcrest, or Selenium.

## Layout

- `src/main/java/com/agenticqa/core` — browser, config, logging, reporting
- `src/main/java/com/agenticqa/pages|components|api|factories` — automation services
- `src/main/java/com/agenticqa/agents` — LLM runtime pipeline + CLI
- `src/test/java/com/agenticqa/tests` — ui / api / integration / unit
- `.ai/skills` — coding-agent skills (`SKILL.md`)
- `.ai/agents` — specialized agent roles (+ `runtime/` for LLM agents)
- `scripts/*` — deterministic governance scripts
- `agent-generated/` — agent drafts + LLM run outputs (not mixed into `src/`)

## Operating rules

1. Read `.ai/skills/framework-onboarding/SKILL.md` before coding.
2. Read `docs/requirements/AUTOMATION_REQUIREMENTS.md` and **`.ai/knowledge/automation-inventory.md`** — print existing tests/data/scripts before proposing new ones.
3. Follow the agentic chain: requirement → inventory → P0/P1/P2 gap cases → automate → validate (`.ai/workflows/agentic-delivery-chain.md`).
4. **Write all agent-generated automation under `agent-generated/` only** — never into `src/main` or `src/test` unless the human explicitly asks to promote (see `.ai/knowledge/agent-generated-sandbox.md`).
5. Inspect `docs/recon/automation-exercise-findings.md` — do not invent APIs/selectors.
6. UI tests extend `UiBaseTest`; API tests extend `ApiBaseTest` (Selenium — not Playwright).
7. Use factories for data; cleanup accounts when created.
8. Before finishing, run AgentValidator on `agent-generated/src/test/java` and fix ERROR violations.
9. Treat drafts under `agent-generated/` as untrusted until validated, reviewed, and promoted into `src/`.

## Commands map (coding agents)

| Intent | Skill | Cursor command |
|---|---|---|
| Full delivery chain | agentic-delivery-chain | (orchestrator agent) |
| Analyze requirement | requirement-analysis | `/analyze-requirement` |
| Coverage vs inventory | analyze-test-coverage | — |
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
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.validation.AgentValidator" -Dexec.args="src/test/java"
scripts/secret-detector/run.sh
scripts/fixed-wait-detector/run.sh
```

## Reviewer policy

QA Reviewer agent is **review-only by default**. Propose changes; do not silently rewrite reviewed code.
