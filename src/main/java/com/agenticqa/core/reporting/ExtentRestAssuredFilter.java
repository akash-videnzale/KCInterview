package com.agenticqa.core.reporting;

import com.agenticqa.api.responses.ApiBodyNormalizer;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.Map;

/**
 * Captures REST Assured request + response into the active Extent test.
 */
public final class ExtentRestAssuredFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        ExtentLogger.apiRequest(buildRequestLog(requestSpec));
        Response response = ctx.next(requestSpec, responseSpec);
        ExtentLogger.apiResponse(buildResponseLog(response));
        return response;
    }

    private static String buildRequestLog(FilterableRequestSpecification request) {
        StringBuilder sb = new StringBuilder();
        sb.append(nullToEmpty(request.getMethod())).append(' ').append(nullToEmpty(request.getURI())).append('\n');
        appendMap(sb, "Headers", request.getHeaders() == null ? null : toHeaderMap(request));
        appendMap(sb, "Query params", request.getQueryParams());
        appendMap(sb, "Form params", request.getFormParams());
        appendMap(sb, "Path params", request.getPathParams());
        Object body = request.getBody();
        if (body != null) {
            sb.append("Body:\n").append(body).append('\n');
        }
        return SecretRedactor.redact(sb.toString().trim());
    }

    private static String buildResponseLog(Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP ").append(response.statusCode()).append('\n');
        sb.append("Headers:\n").append(response.getHeaders()).append('\n');
        String body = ApiBodyNormalizer.normalize(response.asString());
        sb.append("Body:\n").append(body == null ? "" : body);
        return SecretRedactor.redact(sb.toString().trim());
    }

    private static Map<String, String> toHeaderMap(FilterableRequestSpecification request) {
        return request.getHeaders().asList().stream()
                .collect(java.util.stream.Collectors.toMap(
                        h -> h.getName(),
                        h -> h.getValue(),
                        (a, b) -> a + ", " + b,
                        java.util.LinkedHashMap::new
                ));
    }

    private static void appendMap(StringBuilder sb, String label, Map<String, ?> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        sb.append(label).append(":\n");
        values.forEach((k, v) -> sb.append("  ").append(k).append('=').append(v).append('\n'));
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
