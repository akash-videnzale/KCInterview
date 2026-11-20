package com.agenticqa.agents.llm;

public enum LlmProvider {
    OPENAI,
    ANTHROPIC,
    DEEPSEEK;

    public static LlmProvider from(String value) {
        if (value == null || value.isBlank()) {
            return OPENAI;
        }
        return LlmProvider.valueOf(value.trim().toUpperCase());
    }
}
