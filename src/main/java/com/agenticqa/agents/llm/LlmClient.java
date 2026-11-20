package com.agenticqa.agents.llm;

public interface LlmClient {
    LlmProvider provider();

    LlmResponse complete(LlmRequest request);
}
