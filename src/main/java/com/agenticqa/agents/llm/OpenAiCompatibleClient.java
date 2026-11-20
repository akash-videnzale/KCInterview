package com.agenticqa.agents.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * OpenAI-compatible chat completions client (OpenAI + DeepSeek).
 */
public final class OpenAiCompatibleClient implements LlmClient {

    private final LlmProvider provider;
    private final String apiKey;
    private final String model;
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();

    public OpenAiCompatibleClient(LlmProvider provider, String apiKey, String model, String baseUrl) {
        this.provider = provider;
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl.replaceAll("/$", "");
    }

    @Override
    public LlmProvider provider() {
        return provider;
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        long start = System.currentTimeMillis();
        try {
            ObjectNode body = mapper.createObjectNode();
            body.put("model", model);
            body.put("temperature", request.temperature());
            // OpenAI nano / newer chat models reject max_tokens; DeepSeek still expects it.
            if (provider == LlmProvider.OPENAI) {
                body.put("max_completion_tokens", request.maxTokens());
            } else {
                body.put("max_tokens", request.maxTokens());
            }
            ArrayNode messages = body.putArray("messages");
            messages.addObject().put("role", "system").put("content", request.systemPrompt());
            messages.addObject().put("role", "user").put("content", request.userPrompt());

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/chat/completions"))
                    .timeout(Duration.ofSeconds(120))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = http.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IllegalStateException(provider + " API error HTTP " + response.statusCode() + ": " + truncate(response.body()));
            }
            JsonNode root = mapper.readTree(response.body());
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            if (content.isBlank()) {
                throw new IllegalStateException(provider + " returned empty content: " + truncate(response.body()));
            }
            return new LlmResponse(provider, model, content.trim(), System.currentTimeMillis() - start);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(provider + " request failed: " + e.getMessage(), e);
        }
    }

    private static String truncate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 500 ? body.substring(0, 500) + "..." : body;
    }
}
