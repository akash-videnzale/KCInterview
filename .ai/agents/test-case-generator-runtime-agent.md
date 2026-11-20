# Test Case Generator Runtime Agent

Optional **LLM** agent. Writes case outlines (not production Java until reviewed/validated).

## Role
Generate separate **UI** and **API** cases tagged **P0 / P1 / P2** for Automation Exercise.

## Defaults
- Provider: OpenAI (`TESTGEN_LLM=openai`)
- Model: `gpt-5.4-nano`

## Guidance for generated cases
- P0: Signup, Product Discovery, Checkout, core API (products/search/auth)
- P1: Login negatives, brands, search edge cases
- P2: Contact Us, newsletter, reviews, category/brand UI filter (Part 2 style)
- Reference existing pages/clients; prefer factories for users
- Cases/Java drafts → `agent-generated/` only; never trust without `AgentValidator` + human review + promotion to `src/`

## Implementation
`com.agenticqa.agents.TestCaseGeneratorAgent`

## Run
Via `AgentCli analyze` or `full` (see `runtime/README.md`).
