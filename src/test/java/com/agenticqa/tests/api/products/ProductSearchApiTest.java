package com.agenticqa.tests.api.products;

import com.agenticqa.api.assertions.ApiResponseAssertions;
import com.agenticqa.api.clients.ProductApiClient;
import com.agenticqa.api.fixtures.ApiBaseTest;
import com.agenticqa.api.models.ApiMessageResponse;
import com.agenticqa.api.models.KnownProducts;
import com.agenticqa.api.models.Product;
import com.agenticqa.api.models.ProductsListResponse;
import com.agenticqa.api.responses.SchemaValidator;
import com.agenticqa.data.factories.ProductSearchFactory;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * POST /api/searchProduct — known term, missing param, empty results.
 *
 * @author Akash Aggarwal
 */
public class ProductSearchApiTest extends ApiBaseTest {

    private final ProductApiClient productApi = new ProductApiClient();

    @Test(groups = {"api", "smoke", "regression"})
    public void shouldSearchProductsByKnownTerm() {
        String term = KnownProducts.SEARCH_TERM_TOP;

        Response raw = productApi.searchProductRaw(term);
        Assert.assertEquals(raw.statusCode(), 200);
        SchemaValidator.assertMatches(raw, "schemas/products-list-schema.json");

        ProductsListResponse body = productApi.parseProductsList(raw);
        ApiResponseAssertions.assertSearchResultsData(body, term);

        Product blueTop = body.requireById(KnownProducts.BLUE_TOP.id());
        Assert.assertEquals(blueTop.name(), KnownProducts.BLUE_TOP.name());
        Assert.assertEquals(blueTop.price(), KnownProducts.BLUE_TOP.price());
    }

    @Test(groups = {"api", "regression"})
    public void shouldRejectSearchWithoutParameter() {
        Response raw = productApi.searchProductWithoutParameterRaw();
        Assert.assertEquals(raw.statusCode(), 200);

        ApiMessageResponse body = productApi.parseMessage(raw);
        ApiResponseAssertions.assertSearchParamMissing(body);
    }

    @Test(groups = {"api", "regression"})
    public void shouldReturnEmptyListForUnknownTerm() {
        ProductsListResponse body = productApi.searchProduct(ProductSearchFactory.unlikelyMatchTerm());
        ApiResponseAssertions.assertEmptyProductsList(body);
    }
}
