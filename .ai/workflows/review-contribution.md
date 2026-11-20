# Contribution Review Workflow

1. Inspect git diff
2. Confirm agent drafts are under `agent-generated/` (flag any unexpected `src/` writes)
3. Review against architecture + inventory (no duplicate P0 happy paths)
4. Run AgentValidator + secret/fixed-wait scripts
5. Validate TestNG conventions and assertion helpers
6. Execute impacted **promoted** tests if `src/` changed
7. Produce ACCEPT/REJECT report (review-only)

Skills: `review-test-quality`, `validate-agent-contribution`
