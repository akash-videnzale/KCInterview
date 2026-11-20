# Agent Evaluation Scenarios

These scenarios measure agent/framework effectiveness. Distinguish:

- **Static simulated evaluation** — fixtures + validators (no live agent)
- **Live-agent evaluation** — an actual coding agent performs the task; record real outcomes

Do not fabricate live results.

## Scenarios

| ID | Scenario | Static fixture | Live metric targets |
|---|---|---|---|
| E1 | Generate signup test | Existing `SignupUiTest` as reference | Compiles; passes; 0 validator ERRORs |
| E2 | Generate product API test | `ProductListApiTest` | Compiles; passes; schema assert present |
| E3 | Generate checkout test | `CheckoutUiTest` | Compiles; practice payment only; cleanup |
| E4 | Detect flawed Java | `scenarios/flawed-examples/FlawedCheckoutTest.java` | AgentValidator REJECTED with ≥5 ERRORs |
| E5 | Diagnose failing test | Inject failure locally, use debug-failure workflow | Root cause class + fix verified by re-run |

## Measurement fields

- Compilation success
- Test execution success
- Architecture compliance (ArchitectureValidator)
- Violation counts (AgentValidator)
- Correction count
- Final acceptance

## Evaluation rules

See `evaluation-rules/`.
