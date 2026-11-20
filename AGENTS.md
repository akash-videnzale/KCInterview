# AGENTS.md — Agentic QA Automation Framework

## Mission

Build and maintain an enterprise-grade, agentic-first QA platform for https://automationexercise.com/ using **Java 21 + Selenium + TestNG + REST Assured + Extent Reports**.

AI agents are first-class contributors, but **not trusted contributors**. Skills empower them; deterministic validators gate their work.

There are two AI layers:

1. **Coding-agent layer** — skills/rules/instructions for IDE agents writing framework code.
2. **LLM runtime pipeline** — optional GPT/Claude/DeepSeek orchestration via `AgentCli` (requirements → cases → drafts → execute → report).

**TestNG tests must not call LLM APIs during assertions.** Runtime LLMs stay in `com.agenticqa.agents.*`.

## Stack (mandatory)

| Area | Choice |
|---|---|
| Language | Java 21 |
| UI | **Selenium 4** WebDriver (+ WebDriverManager) |
| Runner | TestNG only |
| Assertions | TestNG Assert / SoftAssert (+ `UiAssertions` / `ApiResponseAssertions`) |
| API | REST Assured + Jackson POJOs (+ optional JSON Schema) |
| Build | Maven |
| Reporting | **Extent Reports** |
| Data | Java Faker factories |
| Config | YAML + env + `mvn -DHEADLESS` / `-DPARALLEL` |
| Governance | JavaParser `AgentValidator` |
| Optional LLM | OpenAI / Anthropic / DeepSeek via `.env` |

Do **not** use JUnit, AssertJ, Hamcrest, or Playwright.

## Layout

- `src/main/java/com/agenticqa/core` — browser, config, logging, reporting
- `src/main/java/com/agenticqa/ui` — pages, components, fixtures, assertions, messages
- `src/main/java/com/agenticqa/api` — clients, models, requests, responses, fixtures, assertions
- `src/main/java/com/agenticqa/data` — factories + models
- `src/main/java/com/agenticqa/agents` — optional LLM runtime pipeline + CLI
- `src/main/java/com/agenticqa/governance` — AgentValidator + ArchitectureValidator
- `src/test/java/com/agenticqa/tests` — ui / api / integration / unit (**curated product**)
- `agent-generated/` — **all agent drafts** (cases + Java); not on Maven classpath until promoted
- `.ai/skills` · `.ai/agents` · `.ai/knowledge` · `.ai/workflows`
- `docs/requirements/` · `.ai/knowledge/automation-inventory.md`
- `scripts/*` — deterministic governance + Extent helpers

## Operating rules

1. Read `.ai/skills/framework-onboarding/SKILL.md` before coding.
2. Read `docs/requirements/AUTOMATION_REQUIREMENTS.md` and **`.ai/knowledge/automation-inventory.md`** — print existing tests/data/scripts before proposing new ones.
3. Follow the agentic chain: requirement → inventory → P0/P1/P2 gap cases → automate → validate (`.ai/workflows/agentic-delivery-chain.md`).
4. **Write all agent-generated automation under `agent-generated/` only** — never into `src/main` or `src/test` unless the human explicitly asks to promote.
5. Inspect `docs/recon/automation-exercise-findings.md` — do not invent APIs/selectors.
6. UI tests extend `UiBaseTest`; API tests extend `ApiBaseTest`.
7. Pages use **no-arg** constructors (`new LoginPage()`); driver from `UiSessionHolder`.
8. Use factories for data; cleanup accounts when created.
9. Before finishing, run AgentValidator on `agent-generated/src/test/java` (drafts) or `src/test/java` (promoted code).
10. Treat drafts under `agent-generated/` as untrusted until validated, reviewed, and promoted into `src/`.

## Commands map (coding agents)

| Intent | Skill | Cursor command |
|---|---|---|
| Full delivery chain | agentic-delivery-chain | orchestrator agent |
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

Keys in local `.env` only (see `.env.example`). Outputs → `agent-generated/runs/`.

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
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="agent-generated/src/test/java"
# After promotion / for golden suite:
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="src/test/java/com/agenticqa/tests"
scripts/secret-detector/run.sh
scripts/fixed-wait-detector/run.sh
```

## Reviewer policy

QA Reviewer agent is **review-only by default**. Propose changes; do not silently rewrite reviewed code.
