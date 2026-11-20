# AI_USAGE.md

This log records **actual** AI assistance used while building this repository. Entries are added only when real prompts/results exist. Do not invent prompts or outcomes.

## Principles

- AI assists design, requirement analysis, and authoring speed.
- Deterministic validators and human review gate quality.
- TestNG tests do not call LLM APIs during assertions.
- Optional LLM runtime pipeline (`AgentCli`) is separate from test execution assertions.

## Authorship

**Author / owner of this framework codebase: Akash Aggarwal.**

AI coding assistants were used as implementation aids only. They are not authors of the project; attribution in JavaDoc (`@author Akash Aggarwal`), Maven `pom.xml` developers, and Extent reports (`assignAuthor`) reflects the human author.

## Tools used

| Tool | Role |
|---|---|
| Coding agent (implementation aid) | Scaffold/assist under Akash Aggarwal authorship; skills, validators, LLM pipeline |
| OpenAI (`gpt-5.4-nano`) | Requirement analysis + test case generation (verified) |
| Anthropic (`claude-haiku-4-5`) | Test case review (verified with project key) |
| DeepSeek (`deepseek-flash`) | Report analysis provider (configured) |
| Live HTTP inspection (`curl`) | Verify Automation Exercise APIs + LLM provider models |
| Maven / TestNG / Selenium | Compile and execute verification |

## Evidence log

| Timestamp (local) | Prompt / task summary | AI-produced artifacts | Human corrections | How mistake found | Validation |
|---|---|---|---|---|---|
| 2026-11-20 | Build full agentic QA platform | Core/pages/API/tests, 14 skills, validators | Duplicate createAccount removed; search SoftAssert adjusted; HTML JSON normalizer; detector false-positive fixes | Failing assertions + parse errors | unit/api/ui/integration/parallel PASS; AgentValidator ACCEPTED |
| 2026-11-20 | Extend with GPT requirement analysis + multi-agent pipeline | `com.agenticqa.agents.*`, `AgentCli`, `.env` providers | Claude model id corrected to `claude-haiku-4-5`; OpenAI `max_completion_tokens`; DeepSeek model `deepseek-flash`; cheapest models selected | Anthropic 404 on sonnet/older haiku; OpenAI nano rejected `max_tokens` | `AgentCli analyze` PASS with OPENAI/gpt-5.4-nano + Anthropic reviewer |
| 2026-11-20 | Convert UI layer Playwright → Selenium Java (approved) | `BrowserManager`/`SessionFactory`, pages/components, Dockerfile, docs | Stale/ad overlay flakes; continue/delete via direct URLs; API 503 retry | Headed/UI failures + AE “heavy load” HTML | UI+API headed 12/12 PASS after fixes; Vivek OK on Selenium Java |

## Incorrect suggestions / pitfalls observed

| Issue | Identification | Resolution |
|---|---|---|
| Inventing selectors/endpoints | Assignment constraint + recon | `docs/recon/automation-exercise-findings.md` + live API probes |
| Duplicate createAccount in auth test | Code review | Single `createAccountRaw` parse path |
| Search results always contain query substring | SoftAssert failure | Assert structure + at least one matching name |
| AE JSON always raw | HTML-wrapped body parse failures | `ApiBodyNormalizer` |
| Claude key “broken” | All sonnet ids 404 | Key works; only `claude-haiku-4-5` available |
| OpenAI nano + `max_tokens` | API invalid_request_error | Use `max_completion_tokens` for OpenAI |
| Docs still said “don’t use Selenium” / Playwright install | Human review vs live stack | Rewrote DECISIONS/README/AGENTS + Selenium rules |

## Current cheapest model defaults

| Provider | Model |
|---|---|
| OpenAI | `gpt-5.4-nano` |
| Anthropic | `claude-haiku-4-5` |
| DeepSeek | `deepseek-flash` |

## Template for future entries

```markdown
### Entry
- Date:
- Prompt:
- Files touched:
- AI output accepted:
- AI output rejected/corrected:
- Validator result:
- Tests/pipeline run + outcome:
```

## Limitations

- AI cannot guarantee selector stability if the AUT DOM changes.
- FailureClassifier / report analysis are heuristic, not authoritative RCA.
- LLM drafts under `agent-output/` require human review + AgentValidator before merge.
- Live evaluation scenarios in `evaluations/` need real runs; do not fabricate scores.
