package com.agenticqa.api.clients;

import com.agenticqa.api.models.ApiMessageResponse;
import com.agenticqa.api.models.ProductsListResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

/**
 * Client for productsList and searchProduct.
 *
 * @author Akash Aggarwal
 */
public class ProductApiClient extends BaseApiClient {

    private final ObjectMapper mapper = new ObjectMapper();

    public Response getAllProductsRaw() {
        Response response = withOverloadRetry("productsList",
                () -> givenJson().when().get("/api/productsList"));
        attachResponse("productsList", response);
        return response;
    }

    public ProductsListResponse getAllProducts() {
        return parseProductsList(getAllProductsRaw());
    }

    public Response searchProductRaw(String searchProduct) {
        Response response = withOverloadRetry("searchProduct", () -> givenForm()
                .formParam("search_product", searchProduct)
                .when()
                .post("/api/searchProduct"));
        attachResponse("searchProduct", response);
        return response;
    }

    public ProductsListResponse searchProduct(String searchProduct) {
        return parseProductsList(searchProductRaw(searchProduct));
    }

    public Response searchProductWithoutParameterRaw() {
        Response response = withOverloadRetry("searchProductMissingParam",
                () -> givenForm().when().post("/api/searchProduct"));
        attachResponse("searchProductMissingParam", response);
        return response;
    }

    public ApiMessageResponse searchProductWithoutParameter() {
        return parseMessage(searchProductWithoutParameterRaw());
    }

    public ProductsListResponse parseProductsList(Response response) {
        try {
            return mapper.readValue(normalizeBody(response.asString()), ProductsListResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse products list: " + normalizeBody(response.asString()), e);
        }
    }

    public ApiMessageResponse parseMessage(Response response) {
        try {
            return mapper.readValue(normalizeBody(response.asString()), ApiMessageResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse API message: " + normalizeBody(response.asString()), e);
        }
    }
}
