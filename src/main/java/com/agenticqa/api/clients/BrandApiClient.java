package com.agenticqa.api.clients;

import com.agenticqa.api.models.BrandsListResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

/**
 * Client for brandsList.
 *
 * @author Akash Aggarwal
 */
public class BrandApiClient extends BaseApiClient {

    private final ObjectMapper mapper = new ObjectMapper();

    public Response getAllBrandsRaw() {
        Response response = withOverloadRetry("brandsList",
                () -> givenJson().when().get("/api/brandsList"));
        attachResponse("brandsList", response);
        return response;
    }

    public BrandsListResponse getAllBrands() {
        return parseBrandsList(getAllBrandsRaw());
    }

    public BrandsListResponse parseBrandsList(Response response) {
        try {
            return mapper.readValue(normalizeBody(response.asString()), BrandsListResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse brands list: " + normalizeBody(response.asString()), e);
        }
    }
}
