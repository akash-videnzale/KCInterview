# Engineer Onboarding

1. Install JDK 21 + Maven 3.9+
2. Copy `.env.example` → `.env`
3. (Optional) Add OpenAI / Anthropic / DeepSeek keys for the LLM pipeline
4. Install Chromium via Playwright CLI
5. Run `mvn test -Dgroups=api`
6. Run `mvn test -Dgroups=unit`
7. Read `AGENTS.md` and one UI test as a template
8. (Optional) Run LLM analysis:

```bash
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"
```

9. Open output under `agent-output/run-*/`

Cheapest verified models: `gpt-5.4-nano`, `claude-haiku-4-5`, `deepseek-flash`.

See `docs/ai-platform/llm-runtime-agents.md`.
