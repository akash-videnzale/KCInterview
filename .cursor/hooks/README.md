# Hooks

Portable deterministic validation is implemented as shell scripts under `scripts/`.

## Supported integrations

| Lifecycle | Portable script | Notes |
|---|---|---|
| Before task completion | `scripts/agent-validator/run.sh` | Primary gate |
| Before/after test execution | Maven Surefire + Extent | `scripts/extent-report/open.sh` |
| Secret scan | `scripts/secret-detector/run.sh` | Heuristic |
| Fixed wait scan | `scripts/fixed-wait-detector/run.sh` | Heuristic |

## Platform hook status

- **Cursor project hooks**: use these scripts from commands/skills; do not invent unsupported hook schema.
- **Claude Code hooks**: invoke the same scripts from project instructions in `CLAUDE.md`.
- **GitHub Copilot**: follow `.github/copilot-instructions.md`; no proprietary hook format claimed.

Where a vendor lifecycle event is undocumented or unstable, prefer explicit command invocation over speculative config files.
