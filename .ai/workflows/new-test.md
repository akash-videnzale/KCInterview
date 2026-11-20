# New Test Workflow
1. Analyze requirement (requirement-analysis skill)
2. Read create-ui-test or create-api-test skill
3. Inspect architecture and existing components
4. Choose layer (ui/api/integration)
5. Generate test + data
6. Run AgentValidator
7. `mvn -DskipTests test-compile`
8. Execute targeted TestNG test
9. Review Allure/artifacts on failure
10. QA Reviewer review-only pass
