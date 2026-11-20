package com.agenticqa.agents.llm;

public record LlmRequest(
        String systemPrompt,
        String userPrompt,
        double temperature,
        int maxTokens
) {
    public static LlmRequest of(String systemPrompt, String userPrompt) {
        return new LlmRequest(systemPrompt, userPrompt, 0.2, 4096);
    }
}
