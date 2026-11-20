package com.agenticqa.agents.llm;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Loads LLM and email settings from environment / .env.
 * Secrets must never be committed.
 */
public final class LlmConfig {

    private final Dotenv dotenv;

    public LlmConfig() {
        this.dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
    }

    public String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String fromFile = dotenv.get(key);
        return fromFile == null ? "" : fromFile.trim();
    }

    public String require(String key) {
        String value = get(key);
        if (value.isBlank()) {
            throw new IllegalStateException("Missing required config: " + key + " (set in .env)");
        }
        return value;
    }

    public LlmProvider providerFor(String roleKey, LlmProvider fallback) {
        String value = get(roleKey);
        return value.isBlank() ? fallback : LlmProvider.from(value);
    }

    public String openAiKey() {
        return require("OPENAI_API_KEY");
    }

    public String openAiModel() {
        String model = get("OPENAI_MODEL");
        return model.isBlank() ? "gpt-4o-mini" : model;
    }

    public String anthropicKey() {
        return require("ANTHROPIC_API_KEY");
    }

    public String anthropicModel() {
        String model = get("ANTHROPIC_MODEL");
        return model.isBlank() ? "claude-sonnet-4-20250514" : model;
    }

    public String deepSeekKey() {
        return require("DEEPSEEK_API_KEY");
    }

    public String deepSeekModel() {
        String model = get("DEEPSEEK_MODEL");
        return model.isBlank() ? "deepseek-chat" : model;
    }

    public String deepSeekBaseUrl() {
        String url = get("DEEPSEEK_BASE_URL");
        return url.isBlank() ? "https://api.deepseek.com" : url.replaceAll("/$", "");
    }
}
