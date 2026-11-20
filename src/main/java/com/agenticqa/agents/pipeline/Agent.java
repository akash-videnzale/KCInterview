package com.agenticqa.agents.pipeline;

public interface Agent {
    String name();

    void run(PipelineContext context) throws Exception;
}
