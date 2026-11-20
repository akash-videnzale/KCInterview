package com.agenticqa.api.clients;

import com.agenticqa.api.responses.ApiBodyNormalizer;
import com.agenticqa.core.configuration.ConfigLoader;
import com.agenticqa.core.configuration.FrameworkConfig;
import com.agenticqa.core.reporting.ExtentLogger;
import com.agenticqa.core.reporting.ExtentRestAssuredFilter;
import com.agenticqa.core.reporting.SecretRedactor;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Shared REST Assured setup: specs, Extent filter, body normalization, business responseCode, and AE overload retries.
 *
 * @author Akash Aggarwal
 */
public abstract class BaseApiClient {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final FrameworkConfig config = ConfigLoader.get();

    /** Request starting point for JSON APIs ({@code Content-Type: application/json}). */
    protected RequestSpecification givenJson() {
        return RestAssured.given()
                .spec(baseSpec())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    /** Request starting point for AE form posts ({@code application/x-www-form-urlencoded}). */
    protected RequestSpecification givenForm() {
        return RestAssured.given()
                .spec(baseSpec())
                .contentType(ContentType.URLENC)
                .accept(ContentType.JSON);
    }

    /**
     * Common request specification: API base URI, relaxed HTTPS, console logging,
     * and {@link ExtentRestAssuredFilter} so request/response appear in Extent reports.
     */
    protected RequestSpecification baseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.apiBaseUrl())
                .setRelaxedHTTPSValidation()
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new ExtentRestAssuredFilter())
                .build();
    }

    /**
     * Logs a redacted status/body summary step to Extent.
     * Full request/response bodies are already captured by {@link ExtentRestAssuredFilter}.
     */
    protected void attachResponse(String name, Response response) {
        String body = SecretRedactor.redact(normalizeBody(response.asString()));
        String business = "";
        try {
            int code = ApiBodyNormalizer.jsonPath(response).getInt("responseCode");
            business = " / businessCode=" + code;
        } catch (Exception ignored) {
            // best-effort — not every AE payload has responseCode
        }
        ExtentLogger.step("API " + name + " → HTTP " + response.statusCode() + business);
        ExtentLogger.info(name + " body:\n" + (body.isBlank() ? "(empty)" : body));
    }

    /** Strips AE HTML wrappers so Jackson/JsonPath see pure JSON. */
    protected String normalizeBody(String raw) {
        return ApiBodyNormalizer.normalize(raw);
    }

    /**
     * Automation Exercise often returns HTTP 200 while the real outcome lives in JSON
     * {@code responseCode} (e.g. 201 created, 400 bad request, 404 not found).
     */
    protected int businessCode(Response response) {
        Integer code = response.jsonPath().getInt("responseCode");
        return code == null ? response.statusCode() : code;
    }

    /** Detects AE “under heavy load” / HTTP 503 responses that warrant a retry. */
    protected boolean isSiteOverloaded(Response response) {
        if (response == null) {
            return true;
        }
        if (response.statusCode() == 503) {
            return true;
        }
        String body = response.asString();
        return body != null && body.toLowerCase().contains("under heavy load");
    }

    /**
     * Executes {@code call} up to 3 times with backoff when AE reports overload.
     * Returns the last response even if still overloaded so callers can assert failure.
     */
    protected Response withOverloadRetry(String label, Supplier<Response> call) {
        Response last = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            last = call.get();
            if (!isSiteOverloaded(last)) {
                return last;
            }
            log.warn("{} overloaded (attempt {}/3), retrying...", label, attempt);
            try {
                Thread.sleep(1500L * attempt);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return last;
    }
}
