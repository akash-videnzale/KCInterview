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

public final class AnthropicClient implements LlmClient {

    private final String apiKey;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();

    public AnthropicClient(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public LlmProvider provider() {
        return LlmProvider.ANTHROPIC;
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        long start = System.currentTimeMillis();
        try {
            ObjectNode body = mapper.createObjectNode();
            body.put("model", model);
            body.put("max_tokens", request.maxTokens());
            body.put("temperature", request.temperature());
            body.put("system", request.systemPrompt());
            ArrayNode messages = body.putArray("messages");
            messages.addObject().put("role", "user").put("content", request.userPrompt());

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .timeout(Duration.ofSeconds(120))
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = http.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IllegalStateException("Anthropic API error HTTP " + response.statusCode() + ": " + truncate(response.body()));
            }
            JsonNode root = mapper.readTree(response.body());
            StringBuilder content = new StringBuilder();
            for (JsonNode block : root.path("content")) {
                if ("text".equals(block.path("type").asText())) {
                    content.append(block.path("text").asText());
                }
            }
            if (content.isEmpty()) {
                throw new IllegalStateException("Anthropic returned empty content: " + truncate(response.body()));
            }
            return new LlmResponse(LlmProvider.ANTHROPIC, model, content.toString().trim(), System.currentTimeMillis() - start);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Anthropic request failed: " + e.getMessage(), e);
        }
    }

    private static String truncate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 500 ? body.substring(0, 500) + "..." : body;
    }
}
