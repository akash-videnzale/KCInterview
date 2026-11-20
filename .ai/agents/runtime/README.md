# Runtime LLM Agents

Optional pipeline that calls GPT / Claude / DeepSeek using keys in local `.env`.

**Important**
- TestNG tests do **not** call LLMs during assertions
- Coding agents live in `.ai/agents/*.md` (Cursor/IDE) — different from this pipeline
- Drafts go to `agent-generated/runs/` (and Java under `agent-generated/src/`) — **not** into framework `src/`

---

## Agents

| # | Agent | Class | Default LLM |
|---|---|---|---|
| 1 | Requirement + change analysis | `RequirementAnalysisAgent` | OpenAI `gpt-5.4-nano` |
| 2 | Test case generator (UI + API, P0/P1/P2) | `TestCaseGeneratorAgent` | OpenAI `gpt-5.4-nano` |
| 3 | Test case sufficiency reviewer | `TestCaseReviewerAgent` | Anthropic `claude-haiku-4-5` |
| 4 | API test automator (draft Java) | `ApiTestAutomatorAgent` | Anthropic `claude-haiku-4-5` |
| 5 | UI test automator (draft Java) | `UiTestAutomatorAgent` | Anthropic `claude-haiku-4-5` |
| 6 | Automation executor | `AutomationExecutorAgent` | none (`mvn test`) |
| 7 | Failed test re-executor | `FailedTestReexecutorAgent` | none |
| 8 | Report analyzer | `ReportAnalyzerAgent` | DeepSeek `deepseek-flash` |
| 9 | Email report sender | `EmailReportSenderAgent` | none (AWS SES) |

Automators should draft against **Selenium page objects / ApiClients / UiAssertions / ApiResponseAssertions** — not Playwright.

---

## Run

```bash
# Analysis only (requirement → cases → review)
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"

# Full pipeline including test execution
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="full --requirement docs/examples/sample-requirement.md --groups smoke"

# Execute + report only
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="execute --groups smoke"
```

Outputs: `agent-generated/runs/run-<timestamp>/`

---

## Provider notes

| Provider | Status | Cheap model |
|---|---|---|
| OpenAI | Working | `gpt-5.4-nano` |
| Anthropic | Working | `claude-haiku-4-5` |
| DeepSeek | Working | `deepseek-flash` |

Keys stay in `.env` only (see `.env.example`). Never commit secrets.

Full docs: `docs/ai-platform/llm-runtime-agents.md`
