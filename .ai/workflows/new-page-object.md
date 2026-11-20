# New Page Object Workflow

1. Inspect AUT DOM (prefer `data-qa`) — see recon + `selector-strategy.md`
2. Check inventory / existing pages under `com.agenticqa.ui.pages` first
3. Identify elements and user actions
4. Reuse `HeaderComponent` / `ProductCardComponent` when possible
5. Implement draft under **`agent-generated/src/main/java/com/agenticqa/ui/pages/`**
6. Put visible English in `UiMessages`; assertions stay in `UiAssertions`
7. Validate / review; promote into `src/main/java/...` only when asked

Skill: `.ai/skills/create-page-object/SKILL.md`
