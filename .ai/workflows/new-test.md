# New Test Workflow

Prefer the full chain: `.ai/workflows/agentic-delivery-chain.md`

1. Read `docs/requirements/AUTOMATION_REQUIREMENTS.md`
2. Read `.ai/knowledge/automation-inventory.md` → **print current tests/data/scripts**
3. Run `requirement-analysis` skill → prioritized **gap** cases only (P0/P1/P2)
4. If UI gap → `create-ui-test` / … → files under **`agent-generated/src/...`**
5. If API gap → `create-api-test` / … → **`agent-generated/src/...`**
6. If data missing → `create-test-data` → **`agent-generated/src/...`**
7. Validate: AgentValidator on `agent-generated/src/test/java`
8. **Do not** write into `src/` unless the human asks to promote
9. After promotion: `mvn test -Dtest=...` (+ HEADLESS as needed)
10. On failure → `analyze-test-failure`; QA Reviewer ACCEPT/REJECT

**Do not** recreate Signup / Discovery / Checkout happy paths already in inventory.  
**Do not** mix agent drafts into the framework tree.
