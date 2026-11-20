# Architecture Overview

See README architecture diagram and ADR set in `DECISIONS.md`.

## Core types

- `BrowserManager` — suite Playwright/Browser
- `SessionFactory` / `TestSession` / `UiSessionHolder` — per-test isolation
- `ConfigLoader` / `FrameworkConfig` — YAML + env
- `UiBaseTest` / `ApiBaseTest` — TestNG fixtures
- `AgentValidator` / `ArchitectureValidator` — governance
