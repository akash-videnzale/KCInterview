# How to run — tests, reports, agents & skills

Companion HTML (open in a browser): [`docs/HOW_TO_RUN.html`](docs/HOW_TO_RUN.html)

---

## 0. One-time setup

```bash
cd agentic-qa-automation-framework
cp .env.example .env          # optional: LLM keys for AgentCli only
mvn -DskipTests dependency:resolve
mvn -DskipTests compile test-compile
```

**Need:** JDK 21+, Maven 3.9+, Chrome (or Firefox/Edge).  
**Default run mode:** single-threaded + **headed** browser (visible).

---

## 1. Run the test framework (`mvn test`)

### Full suite

```bash
mvn test
```

### By group

```bash
mvn test -Dgroups=smoke
mvn test -Dgroups=ui
mvn test -Dgroups=api
mvn test -Dgroups=integration
mvn test -Dgroups=unit
mvn test -Dgroups=regression
```

### Headless / parallel (overrides YAML)

```bash
mvn test -DHEADLESS=true
mvn test -DPARALLEL=true
mvn test -DPARALLEL=true -DTHREAD_COUNT=4 -DHEADLESS=true
```

### Single test

```bash
mvn test -Dtest=SignupUiTest
mvn test -Dtest=ProductListApiTest#shouldReturnProductCatalogue
mvn test -Dtest=CheckoutUiTest -DHEADLESS=true
```

### What to expect

| Run | Expect |
|---|---|
| `mvn test` | Browser windows for UI tests; API calls in console; BUILD SUCCESS when all pass |
| `-Dgroups=api` | No browser; REST Assured logs |
| `-Dgroups=unit` | Fast offline validator checks |
| Failure | Surefire failure + screenshot under `target/artifacts/` (UI) |

Inventory of existing tests: `.ai/knowledge/automation-inventory.md`

---

## 2. Where to see the report

### Extent HTML (primary)

After any `mvn test`:

```text
target/extent-report/index.html
```

Open it:

```bash
scripts/extent-report/open.sh
# or
open target/extent-report/index.html
```

**What you’ll see:** suite/tests, steps, API request/response attaches, screenshots on UI failure, pass/fail status.

### Other artifacts

| Artifact | Path |
|---|---|
| Failure screenshots / text | `target/artifacts/` |
| Surefire XML / console | Maven terminal output / `target/surefire-reports/` (if generated) |
| JaCoCo (after `mvn verify`) | `target/site/jacoco/index.html` |

---

## 3. Coding agents & skills (Cursor / Claude / Copilot)

These **do not** call GPT/Claude/DeepSeek by themselves. They instruct the IDE agent.

### How to run

1. Open this repo in Cursor (or Claude Code / Copilot Chat).
2. Point the agent at a skill or agent role, e.g.:
   - “Follow `.ai/skills/agentic-delivery-chain/SKILL.md` for Contact Us”
   - “Act as `.ai/agents/agentic-delivery-orchestrator.md`”
3. Or use Cursor commands: `/analyze-requirement`, `/create-ui-test`, `/create-api-test`, `/validate-contribution`, etc.

### What to expect

| Step | You should see |
|---|---|
| Start | Printed **automation inventory** (existing tests/data) |
| Analysis | COVERED vs GAP + P0/P1/P2 cases |
| Automation | New files only under **`agent-generated/`** (not `src/`) |
| Validate | AgentValidator on `agent-generated/src/test/java` |
| Promote | Only if you explicitly ask — then code moves into `src/` |

### Key paths

| Thing | Path |
|---|---|
| Skills | `.ai/skills/*/SKILL.md` |
| Agent roles | `.ai/agents/*.md` |
| Orchestrator | `.ai/agents/agentic-delivery-orchestrator.md` |
| Chain workflow | `.ai/workflows/agentic-delivery-chain.md` |
| Draft output | `agent-generated/` |

---

## 4. Runtime LLM agents (`AgentCli`)

Optional paid API pipeline (keys in `.env` only). **Not** part of normal `mvn test`.

### Cheapest defaults (see `DECISIONS.md` ADR-006)

| Role | Provider | Model |
|---|---|---|
| Requirement + case gen | OpenAI | `gpt-5.4-nano` |
| Reviewer + automators | Anthropic | `claude-haiku-4-5` |
| Report analysis | DeepSeek | `deepseek-flash` |

### Commands

```bash
# Analyze requirement → cases → review
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"

# Full pipeline (analyze + draft + execute smoke)
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="full --requirement docs/examples/sample-requirement.md --groups smoke"

# Execute + report only
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="execute --groups smoke"
```

### What to expect

- Markdown / logs under `agent-generated/runs/run-<timestamp>/`
- Draft Java (if automators run) under `agent-generated/src/...`
- Console: `Agent pipeline complete. Output: ...`
- After `full` / `execute`: simple pass/fail HTML at  
  `agent-generated/runs/run-<timestamp>/11-execution-report.html`  
  (also printed as `Execution HTML report: …`)
- Maven Extent report still at `target/extent-report/index.html`
- Drafts are **untrusted** until AgentValidator + human promotion into `src/`

---

## 5. Governance (validators)

```bash
scripts/agent-validator/run.sh src/test/java
scripts/agent-validator/run.sh agent-generated/src/test/java
scripts/architecture-validator/run.sh
scripts/secret-detector/run.sh
scripts/fixed-wait-detector/run.sh
```

Or:

```bash
mvn -q -DskipTests exec:java \
  -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="src/test/java/com/agenticqa/tests"
```

**Expect:** `ACCEPTED` (0 ERROR) for clean code; `REJECTED` for the flawed fixture under `evaluations/`.

---

## 6. Quick cheat sheet

| Goal | Command / action |
|---|---|
| Run everything | `mvn test` |
| UI only | `mvn test -Dgroups=ui` |
| API only | `mvn test -Dgroups=api` |
| Open report | `open target/extent-report/index.html` |
| IDE agent delivery | Orchestrator skill + inventory first |
| LLM pipeline | `AgentCli analyze` / `full` |
| See drafts | `agent-generated/` |

More detail: [`README.md`](README.md) · [`AGENTS.md`](AGENTS.md) · [`DECISIONS.md`](DECISIONS.md)
