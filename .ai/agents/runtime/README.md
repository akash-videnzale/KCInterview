# Runtime LLM Agents

These agents call GPT / Claude / DeepSeek via API keys in local `.env`.

They are **orchestration helpers**. TestNG tests do **not** call LLMs at assertion time.

## Agents

| # | Agent | Class | Default LLM |
|---|---|---|---|
| 1 | Requirement + change analysis | `RequirementAnalysisAgent` | OpenAI `gpt-5.4-nano` |
| 2 | Test case generator (UI + API, P0/P1/P2) | `TestCaseGeneratorAgent` | OpenAI `gpt-5.4-nano` |
| 3 | Test case sufficiency reviewer (P0/P1 focus) | `TestCaseReviewerAgent` | Anthropic `claude-haiku-4-5` |
| 4 | API test automator | `ApiTestAutomatorAgent` | Anthropic `claude-haiku-4-5` |
| 5 | UI test automator | `UiTestAutomatorAgent` | Anthropic `claude-haiku-4-5` |
| 6 | Automation executor | `AutomationExecutorAgent` | none (Maven/TestNG) |
| 7 | Failed test re-executor | `FailedTestReexecutorAgent` | none |
| 8 | Report analyzer | `ReportAnalyzerAgent` | DeepSeek `deepseek-flash` |
| 9 | Email report sender | `EmailReportSenderAgent` | none (AWS SES) |

## Run

```bash
# Analysis only
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"

# Full pipeline including test execution
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="full --requirement docs/examples/sample-requirement.md --groups smoke"
```

Outputs land in `agent-output/run-<timestamp>/` (gitignored).

## Provider notes (verified)

| Provider | Status | Cheap model |
|---|---|---|
| OpenAI | Working | `gpt-5.4-nano` |
| Anthropic | Working with this project key | `claude-haiku-4-5` only |
| DeepSeek | Working | `deepseek-flash` (also `deepseek-v4-pro`) |

Full docs: `docs/ai-platform/llm-runtime-agents.md`
