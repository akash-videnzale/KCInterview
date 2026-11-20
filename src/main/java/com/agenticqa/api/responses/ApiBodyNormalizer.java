package com.agenticqa.api.responses;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Automation Exercise occasionally wraps JSON payloads in a trivial HTML body.
 */
public final class ApiBodyNormalizer {

    private ApiBodyNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return trimmed;
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }

    public static JsonPath jsonPath(Response response) {
        return JsonPath.from(normalize(response.asString()));
    }
}
