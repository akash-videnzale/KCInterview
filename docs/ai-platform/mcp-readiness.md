# MCP Readiness

## Intent

Make the repository ready for optional Model Context Protocol tooling without coupling test runtime to MCP.

## Useful optional MCP capabilities

| Capability | Use | Required for tests? |
|---|---|---|
| Playwright/browser exploration | Discover locators | No |
| GitHub repository inspection | Review PRs/diffs | No |
| Test result retrieval | Fetch CI artifacts | No |
| Documentation retrieval | Load AGENTS/skills | No |

## Security model

- MCP servers must be explicitly enabled by the engineer.
- Never send secrets from `.env` to third-party MCP servers.
- Redact credentials in Extent / API attachments (`SecretRedactor`).
- Validators remain the merge gate even when MCP is enabled.

## Setup (illustrative)

Configure MCP in your agent host using that host’s documented format. This repository does **not** ship a custom MCP server and does not claim untested host compatibility beyond instruction files (`AGENTS.md`, `CLAUDE.md`, Copilot instructions).
