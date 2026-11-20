package com.agenticqa.agents.pipeline;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shared mutable bag of artifacts passed between orchestration agents.
 */
public final class PipelineContext {

    private final Path outputDir;
    private final Map<String, String> artifacts = new LinkedHashMap<>();
    private final Map<String, Object> meta = new LinkedHashMap<>();

    public PipelineContext(Path outputDir) {
        this.outputDir = outputDir;
    }

    public Path outputDir() {
        return outputDir;
    }

    public void putArtifact(String key, String content) {
        artifacts.put(key, content);
    }

    public String artifact(String key) {
        return artifacts.getOrDefault(key, "");
    }

    public Map<String, String> artifacts() {
        return Map.copyOf(artifacts);
    }

    public void putMeta(String key, Object value) {
        meta.put(key, value);
    }

    public Object meta(String key) {
        return meta.get(key);
    }
}
