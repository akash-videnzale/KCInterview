# Architecture overview

Canonical architecture for agents and engineers:

- [`../../README.md`](../../README.md) — layers, how it works, how to run
- [`../../.ai/knowledge/architecture.md`](../../.ai/knowledge/architecture.md) — agent quick reference
- [`../../.ai/knowledge/automation-inventory.md`](../../.ai/knowledge/automation-inventory.md) — existing tests
- [`../../DECISIONS.md`](../../DECISIONS.md) — ADRs (Selenium vs Playwright brief, etc.)

## Core types (current stack)

| Type | Role |
|---|---|
| `BrowserManager` / `SessionFactory` | Selenium WebDriver lifecycle |
| `UiSessionHolder` / `TestSession` | Per-test ThreadLocal driver |
| `ConfigLoader` / `FrameworkConfig` | YAML + `-D` / env overrides |
| `UiBaseTest` / `ApiBaseTest` | TestNG fixtures |
| `AgentValidator` / `ArchitectureValidator` | Deterministic governance |

Agent drafts belong in `agent-generated/`, not `src/`.
