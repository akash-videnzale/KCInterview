package com.agenticqa.agents.output;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AgentOutputWriter {

    private final Path root;

    public AgentOutputWriter(Path root) {
        this.root = root;
    }

    public Path write(String relativePath, String content) throws Exception {
        Path target = root.resolve(relativePath);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content == null ? "" : content, StandardCharsets.UTF_8);
        return target;
    }

    /**
     * Timestamped run directory under {@code agent-generated/runs/}
     * (agent drafts stay out of {@code src/}).
     */
    public static Path defaultRunDir() {
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return Path.of("agent-generated", "runs", "run-" + stamp);
    }
}
