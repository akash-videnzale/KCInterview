# Requirement Analysis Runtime Agent

Uses OpenAI by default (`REQUIREMENT_LLM=openai`, model `gpt-5.4-nano`).

## Responsibilities
- Analyze product requirements
- Analyze optional change diffs (`--diff`)
- Produce impacted journeys, risks, P0/P1/P2 themes

## Implementation
`com.agenticqa.agents.RequirementAnalysisAgent`

## Docs
`docs/ai-platform/llm-runtime-agents.md`
