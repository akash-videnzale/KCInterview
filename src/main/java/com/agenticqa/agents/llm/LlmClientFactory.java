package com.agenticqa.agents.llm;

public final class LlmClientFactory {

    private final LlmConfig config;

    public LlmClientFactory(LlmConfig config) {
        this.config = config;
    }

    public LlmClient forProvider(LlmProvider provider) {
        return switch (provider) {
            case OPENAI -> new OpenAiCompatibleClient(
                    LlmProvider.OPENAI,
                    config.openAiKey(),
                    config.openAiModel(),
                    "https://api.openai.com"
            );
            case DEEPSEEK -> new OpenAiCompatibleClient(
                    LlmProvider.DEEPSEEK,
                    config.deepSeekKey(),
                    config.deepSeekModel(),
                    config.deepSeekBaseUrl()
            );
            case ANTHROPIC -> new AnthropicClient(config.anthropicKey(), config.anthropicModel());
        };
    }

    public LlmClient forRole(String roleEnvKey, LlmProvider fallback) {
        return forProvider(config.providerFor(roleEnvKey, fallback));
    }
}
