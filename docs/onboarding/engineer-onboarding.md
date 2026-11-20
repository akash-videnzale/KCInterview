# Engineer Onboarding

1. Install JDK 21 + Maven 3.9+
2. Copy `.env.example` → `.env` (optional LLM keys)
3. Ensure Chrome (or Firefox/Edge) is installed — WebDriverManager resolves the driver
4. Read `README.md`, `AGENTS.md`, and `.ai/knowledge/automation-inventory.md`
5. Run:
   ```bash
   mvn test -Dgroups=unit
   mvn test -Dgroups=api
   mvn test -Dgroups=ui -DHEADLESS=true
   ```
6. Open Extent: `scripts/extent-report/open.sh` or `target/extent-report/index.html`
7. (Optional) LLM analysis — drafts go to `agent-generated/runs/`:

```bash
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"
```

Agent-written Java must stay under `agent-generated/` until promoted into `src/`.

See `docs/ai-platform/llm-runtime-agents.md` and `agent-generated/README.md`.
