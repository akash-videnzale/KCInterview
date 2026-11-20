# Test Case Reviewer Runtime Agent

Optional **LLM** review gate for generated cases (sufficiency, not TestNG execution).

## Role
Review-only: are P0/P1 cases enough for the requirement? Call out P2 gaps without expanding scope wildly.

## Defaults
- Provider: Anthropic (`REVIEWER_LLM=anthropic`)
- Model: `claude-haiku-4-5`

## Review focus
- Assignment critical flows present (Signup, Discovery, Checkout + API)
- Negative/cleanup cases where accounts are created
- No invented AE APIs
- Depth over breadth

## Implementation
`com.agenticqa.agents.TestCaseReviewerAgent`

## Note
This does **not** replace the coding-agent **QA Reviewer** + `AgentValidator` for Java contributions.
