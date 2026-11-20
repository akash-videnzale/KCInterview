# Agent-generated sandbox

**All automation code produced by AI agents lands here.**  
It must **never** be written directly into the framework tree:

- ❌ `src/main/java/...`
- ❌ `src/test/java/...`

Those folders are the **reviewed product**. This folder is the **agent workspace**.

---

## Layout

```text
agent-generated/
├── README.md                 ← you are here
├── cases/                    ← prioritized test cases (markdown)
├── requirements/             ← requirement snapshots for a run
├── runs/                     ← timestamped AgentCli / pipeline outputs
└── src/
    ├── main/java/com/agenticqa/   ← draft pages, clients, factories, models
    └── test/java/com/agenticqa/   ← draft TestNG tests
```

Mirror the same packages as the framework (`ui.pages`, `api.clients`, `tests.ui`, …) so promotion is a clean move.

---

## Rules for agents

1. **Write only under `agent-generated/`**
2. First dump inventory from `.ai/knowledge/automation-inventory.md`
3. Generate cases under `agent-generated/cases/` (P0/P1/P2)
4. Generate Java under `agent-generated/src/...`
5. Do **not** edit production tests/pages unless the human explicitly asks to **promote**
6. Validate drafts with AgentValidator pointing at `agent-generated/src/test/java`
7. Framework `src/` changes are **human promotion** only (after review)

---

## Promotion (human / explicit request)

When a draft is accepted:

1. Review + `AgentValidator` ACCEPTED  
2. Move/copy files from `agent-generated/src/...` → matching path under `src/...`  
3. Update `.ai/knowledge/automation-inventory.md`  
4. Run `mvn test -Dtest=...`  
5. Delete or archive the draft under `agent-generated/runs/`

---

## Not on the Maven test classpath

`pom.xml` does **not** compile `agent-generated/` by default.  
Drafts will not mix into `mvn test` until promoted into `src/`.

Optional local check (after promotion only):

```bash
mvn -q test-compile
mvn test -Dtest=YourPromotedTest -DHEADLESS=true
```

Validate drafts in place:

```bash
mvn -q -DskipTests exec:java \
  -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="agent-generated/src/test/java"
```
