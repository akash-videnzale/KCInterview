# Project structure

```text
agentic-qa-automation-framework/
├── README.md
├── pom.xml, Dockerfile, LICENSE
├── testng.xml, testng-parallel.xml
├── .env.example
├── docs/                  # human docs (decisions, AI usage, recon, onboarding)
├── .ai/                   # coding-agent skills, roles, workflows
├── .cursor/               # Cursor rules, commands, hooks
├── .github/               # Copilot / CI instructions
├── scripts/               # governance + Extent report helpers
├── evaluations/           # agent evaluation scenarios
└── src/
    ├── main/java/com/agenticqa/
    │   ├── core/          # browser, config, context, logging, reporting
    │   ├── ui/            # pages, components, fixtures, assertions
    │   ├── api/           # clients, models, requests, responses, fixtures, assertions
    │   ├── data/          # factories + models (test data)
    │   ├── agents/        # optional LLM runtime pipeline (AgentCli)
    │   └── governance/    # AgentValidator + ArchitectureValidator
    ├── main/resources/    # framework-config.yaml, JSON schemas
    └── test/java/com/agenticqa/tests/
        ├── ui/  api/  integration/  unit/
```

## Layer rules

1. **Tests** only orchestrate; they use page objects / API clients / factories.
2. **UI** owns Selenium locators and user actions.
3. **API** owns REST Assured + Jackson models.
4. **Data** owns unique/isolation-safe factories and value objects.
5. **Core** owns WebDriver lifecycle, config, Extent reporting.
6. **Governance** is deterministic (JavaParser); not LLM.
7. **Agents** are optional and never called from TestNG assertions.
