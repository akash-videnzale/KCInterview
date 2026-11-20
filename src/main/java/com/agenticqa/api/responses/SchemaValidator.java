package com.agenticqa.api.responses;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

/**
 * JSON Schema validation helper using Rest Assured's schema validator.
 * Tests still use TestNG assertions for business checks; this only validates JSON shape.
 */
public final class SchemaValidator {

    private SchemaValidator() {
    }

    public static void assertMatches(Response response, String classpathSchema) {
        String body = ApiBodyNormalizer.normalize(response.asString());
        Response normalized = new ResponseBuilder()
                .setStatusCode(response.statusCode())
                .setContentType(ContentType.JSON)
                .setBody(body)
                .build();
        normalized.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(classpathSchema));
    }
}
