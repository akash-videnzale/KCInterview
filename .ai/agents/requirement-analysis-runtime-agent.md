# Requirement Analysis Runtime Agent

Optional **LLM** agent (not used during `mvn test`).

## Role
Analyze a product requirement (and optional diff) into AE journeys, risks, and P0/P1/P2 themes.

## Defaults
- Provider: OpenAI (`REQUIREMENT_LLM=openai`)
- Model: `gpt-5.4-nano` (override via `.env`)

## Must respect
- AUT: Automation Exercise only
- Stack in repo: Selenium Java TestNG (do not recommend Playwright for this codebase)
- Critical themes: Signup, Product Discovery, Checkout + API
- Depth over breadth; do not invent endpoints

## Implementation
`com.agenticqa.agents.RequirementAnalysisAgent`

## Run
```bash
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"
```

## Docs
`.ai/agents/runtime/README.md` · `docs/ai-platform/llm-runtime-agents.md`
