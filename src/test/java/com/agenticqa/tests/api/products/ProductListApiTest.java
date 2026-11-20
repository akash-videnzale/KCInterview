package com.agenticqa.tests.api.products;

import com.agenticqa.api.assertions.ApiResponseAssertions;
import com.agenticqa.api.clients.ProductApiClient;
import com.agenticqa.api.fixtures.ApiBaseTest;
import com.agenticqa.api.models.KnownProducts;
import com.agenticqa.api.models.Product;
import com.agenticqa.api.models.ProductsListResponse;
import com.agenticqa.api.responses.SchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * GET /api/productsList — schema + exact product values.
 *
 * @author Akash Aggarwal
 */
public class ProductListApiTest extends ApiBaseTest {

    private final ProductApiClient productApi = new ProductApiClient();

    @Test(groups = {"api", "smoke", "regression"})
    public void shouldReturnProductCatalogue() {
        Response raw = productApi.getAllProductsRaw();
        Assert.assertEquals(raw.statusCode(), 200);
        SchemaValidator.assertMatches(raw, "schemas/products-list-schema.json");

        ProductsListResponse body = productApi.parseProductsList(raw);
        ApiResponseAssertions.assertProductsListData(body);

        Product blueTop = body.requireById(KnownProducts.BLUE_TOP.id());
        Assert.assertEquals(blueTop.name(), KnownProducts.BLUE_TOP.name());
        Assert.assertEquals(blueTop.price(), KnownProducts.BLUE_TOP.price());
        Assert.assertEquals(blueTop.brand(), KnownProducts.BLUE_TOP.brand());
    }
}
