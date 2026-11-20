package com.agenticqa.tests.api.products;

import com.agenticqa.api.assertions.ApiResponseAssertions;
import com.agenticqa.api.clients.BrandApiClient;
import com.agenticqa.api.fixtures.ApiBaseTest;
import com.agenticqa.api.models.Brand;
import com.agenticqa.api.models.BrandsListResponse;
import com.agenticqa.api.models.KnownBrands;
import com.agenticqa.api.responses.SchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * GET /api/brandsList — schema + exact brand values.
 *
 * @author Akash Aggarwal
 */
public class BrandListApiTest extends ApiBaseTest {

    private final BrandApiClient brandApi = new BrandApiClient();

    @Test(groups = {"api", "smoke", "regression"})
    public void shouldReturnBrandCatalogue() {
        Response raw = brandApi.getAllBrandsRaw();
        Assert.assertEquals(raw.statusCode(), 200);
        SchemaValidator.assertMatches(raw, "schemas/brands-list-schema.json");

        BrandsListResponse body = brandApi.parseBrandsList(raw);
        ApiResponseAssertions.assertBrandsListData(body);

        Brand polo = body.requireById(KnownBrands.POLO.id());
        Assert.assertEquals(polo.brand(), KnownBrands.POLO.brand());
    }
}
