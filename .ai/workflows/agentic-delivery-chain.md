# Agentic delivery chain

End-to-end path from requirement → prioritized cases → automation, **grounded in what already exists**.

```text
┌─────────────────────┐
│ 1. Read requirement │  docs/requirements/ + user ask
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│ 2. Read knowledge   │  automation-inventory + architecture + api/ui knowledge
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│ 3. Emit inventory   │  List current tests / data / scripts in the reply
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│ 4. Gap analysis     │  REQ-* covered vs missing (P0/P1/P2)
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│ 5. Test cases       │  Only for gaps — with priority + layer
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│ 6. Automate         │  Write ONLY to agent-generated/ (not src/)
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│ 7. Validate drafts  │  AgentValidator on agent-generated/src/test/java
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│ 8. Promote (human)  │  Move into src/ only when explicitly requested
└─────────────────────┘
```

## Who does what

| Step | Agent | Skill |
|---|---|---|
| 1–5 | QA Architect / Requirement Analysis | `requirement-analysis`, `analyze-test-coverage` |
| 5 review | QA Reviewer / runtime reviewer | `review-test-quality` |
| 6 UI | UI Automation Agent | `create-ui-test`, `create-page-object`, `create-e2e-test` |
| 6 API | API Automation Agent | `create-api-test`, `create-api-client` |
| 6 data | Test Data Agent | `create-test-data` |
| 7 | QA Reviewer | `validate-agent-contribution` |
| Orchestrate all | **Agentic Delivery Orchestrator** | `agentic-delivery-chain` |

## Hard rules

1. Always print **current inventory** before new cases  
2. Never re-automate an existing method in the inventory  
3. Prefer extending an existing test class when adding a sibling scenario  
4. P0 gaps before P1/P2  
5. No Playwright; use Selenium patterns in this repo  

## Entry points

- Coding agent: follow `.ai/agents/agentic-delivery-orchestrator.md`
- Skill: `.ai/skills/agentic-delivery-chain/SKILL.md`
- Requirements: `docs/requirements/AUTOMATION_REQUIREMENTS.md`
- Inventory: `.ai/knowledge/automation-inventory.md`
