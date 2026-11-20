package com.agenticqa.agents.llm;

public record LlmResponse(
        LlmProvider provider,
        String model,
        String content,
        long latencyMs
) {
}
