# LLM Runtime Agent Pipeline

Optional orchestration layer that calls GPT / Claude / DeepSeek to analyze requirements, generate P0/P1/P2 cases, draft automation, execute TestNG, re-run failures, analyze reports, and email results.

**Important:** TestNG tests themselves do **not** call LLM APIs. LLMs are used only by `com.agenticqa.agents.*` via `AgentCli`.

## Cheapest verified models

| Provider | Env keys | Cheap model (verified) | Used for |
|---|---|---|---|
| OpenAI | `OPENAI_API_KEY`, `OPENAI_MODEL` | `gpt-5.4-nano` | Requirement analysis, test-case generation |
| Anthropic | `ANTHROPIC_API_KEY`, `ANTHROPIC_MODEL` | `claude-haiku-4-5` | Reviewer, UI/API automators |
| DeepSeek | `DEEPSEEK_API_KEY`, `DEEPSEEK_MODEL` | `deepseek-flash` | Report analysis |

Role routing (`.env`):

```bash
REQUIREMENT_LLM=openai
TESTGEN_LLM=openai
REVIEWER_LLM=anthropic
AUTOMATOR_LLM=anthropic
REPORT_LLM=deepseek
```

Keys live in local `.env` only. Never commit secrets. See `.env.example`.

### Anthropic note

This project’s Anthropic key works with **`claude-haiku-4-5`**. Older model ids (for example `claude-3-5-sonnet-*`) return HTTP 404 `model not found` for this key.

OpenAI nano models require `max_completion_tokens` (handled in `OpenAiCompatibleClient`).

## Agents

| # | Agent | Class | LLM? |
|---|---|---|---|
| 1 | Requirement + change analysis | `RequirementAnalysisAgent` | Yes (GPT) |
| 2 | Test case generator (UI + API, P0/P1/P2) | `TestCaseGeneratorAgent` | Yes (GPT) |
| 3 | Test case sufficiency reviewer (P0/P1 focus) | `TestCaseReviewerAgent` | Yes (Claude) |
| 4 | API test automator | `ApiTestAutomatorAgent` | Yes (Claude) |
| 5 | UI test automator | `UiTestAutomatorAgent` | Yes (Claude) |
| 6 | Automation executor | `AutomationExecutorAgent` | No (`mvn test`) |
| 7 | Failed test re-executor | `FailedTestReexecutorAgent` | No |
| 8 | Report analyzer | `ReportAnalyzerAgent` | Yes (DeepSeek) |
| 9 | Email report sender | `EmailReportSenderAgent` | No (AWS SES) |

## CLI

```bash
# Analysis only: requirement → cases → review
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"

# Full pipeline: analyze → automate drafts → execute → reexec → report → email
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="full --requirement docs/examples/sample-requirement.md --groups smoke"

# Execute + report only
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="execute --groups smoke"
```

Optional flags:

- `--diff <patch-file>` — change analysis input
- `--groups <testng-groups>` — execution groups (default `smoke`)

Outputs: `agent-output/run-<timestamp>/` (gitignored)

Example artifacts:

- `01-requirement-analysis.md`
- `02-ui-test-cases.md` / `03-api-test-cases.md`
- `04-test-case-review.md`
- `05-api-automation-draft.md` / `06-ui-automation-draft.md`
- `07-execution-log.txt` / `08-reexecution-log.txt`
- `09-report-analysis.md` / `10-email-status.txt`

## Email (optional)

Set in `.env`:

```bash
REPORT_EMAIL_ENABLED=true
SES_FROM_EMAIL=donotreply@example.com
SES_TO_EMAIL=you@example.com
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
AWS_REGION=ap-southeast-2
```

## Safety / governance

- Draft Java under `agent-output/` is **not** production until reviewed.
- Before merging generated tests into `src/test`, run:

```bash
mvn -DskipTests exec:java -Dexec.mainClass=com.agenticqa.validation.AgentValidator \
  -Dexec.args="src/test/java"
```

- Do not put API keys in source, commits, or Allure attachments.
