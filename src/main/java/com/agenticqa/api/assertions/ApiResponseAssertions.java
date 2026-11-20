package com.agenticqa.api.assertions;

import com.agenticqa.api.models.ApiMessageResponse;
import com.agenticqa.api.models.ApiMessages;
import com.agenticqa.api.models.Brand;
import com.agenticqa.api.models.BrandsListResponse;
import com.agenticqa.api.models.KnownBrands;
import com.agenticqa.api.models.KnownProducts;
import com.agenticqa.api.models.Product;
import com.agenticqa.api.models.ProductsListResponse;
import com.agenticqa.api.models.UserDetail;
import com.agenticqa.api.models.UserDetailResponse;
import com.agenticqa.core.reporting.ExtentLogger;
import com.agenticqa.data.models.RegistrationData;
import org.testng.Assert;

/**
 * Exact-value assertions for typed API responses.
 *
 * @author Akash Aggarwal
 */
public final class ApiResponseAssertions {

    private ApiResponseAssertions() {
    }

    public static void assertMessage(ApiMessageResponse response, int code, String message) {
        Assert.assertNotNull(response, "Response must not be null");
        Assert.assertEquals(response.responseCode(), code, "responseCode mismatch");
        Assert.assertEquals(response.message(), message, "message mismatch");
        ExtentLogger.pass("API message ok: " + code + " / " + message);
    }

    public static void assertUserCreated(ApiMessageResponse response) {
        assertMessage(response, 201, ApiMessages.USER_CREATED);
    }

    public static void assertUserExists(ApiMessageResponse response) {
        assertMessage(response, 200, ApiMessages.USER_EXISTS);
    }

    public static void assertAccountDeleted(ApiMessageResponse response) {
        assertMessage(response, 200, ApiMessages.ACCOUNT_DELETED);
    }

    public static void assertUserNotFound(ApiMessageResponse response) {
        assertMessage(response, 404, ApiMessages.USER_NOT_FOUND);
    }

    public static void assertEmailAlreadyExists(ApiMessageResponse response) {
        assertMessage(response, 400, ApiMessages.EMAIL_ALREADY_EXISTS);
    }

    public static void assertSearchParamMissing(ApiMessageResponse response) {
        assertMessage(response, 400, ApiMessages.SEARCH_PRODUCT_PARAM_MISSING);
    }

    public static void assertProductsListData(ProductsListResponse body) {
        Assert.assertNotNull(body, "Products response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.responseCode());
        Assert.assertTrue(body.productCount() >= 10, "Expected many products, got " + body.productCount());

        assertProductExact(body.requireById(KnownProducts.BLUE_TOP.id()), KnownProducts.BLUE_TOP);
        assertProductExact(body.requireById(KnownProducts.MEN_TSHIRT.id()), KnownProducts.MEN_TSHIRT);
        assertProductExact(body.requireById(KnownProducts.SLEEVELESS_DRESS.id()), KnownProducts.SLEEVELESS_DRESS);
        ExtentLogger.pass("Products list ok: count=" + body.productCount());
    }

    public static void assertSearchResultsData(ProductsListResponse body, String term) {
        Assert.assertNotNull(body, "Search response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.responseCode());
        Assert.assertFalse(body.isEmpty(), "Expected products for term=" + term);

        boolean nameMatch = body.products().stream()
                .anyMatch(p -> p.name() != null && p.name().toLowerCase().contains(term.toLowerCase()));
        Assert.assertTrue(nameMatch, "At least one product name should contain '" + term + "'");

        if (KnownProducts.SEARCH_TERM_TOP.equalsIgnoreCase(term)) {
            assertProductExact(body.requireById(KnownProducts.BLUE_TOP.id()), KnownProducts.BLUE_TOP);
        }
        ExtentLogger.pass("Search ok for term='" + term + "', count=" + body.productCount());
    }

    public static void assertEmptyProductsList(ProductsListResponse body) {
        Assert.assertNotNull(body, "Products response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.responseCode());
        Assert.assertEquals(body.productCount(), 0, "Expected empty products list");
        ExtentLogger.pass("Empty products list ok");
    }

    public static void assertProductExact(Product actual, Product expected) {
        Assert.assertNotNull(actual, "Actual product is null");
        Assert.assertEquals(actual.id(), expected.id(), "id");
        Assert.assertEquals(actual.name(), expected.name(), "name");
        Assert.assertEquals(actual.price(), expected.price(), "price");
        Assert.assertEquals(actual.brand(), expected.brand(), "brand");
        Assert.assertEquals(actual.category().category(), expected.category().category(), "category");
        Assert.assertEquals(actual.category().usertype().usertype(),
                expected.category().usertype().usertype(), "usertype");
        ExtentLogger.pass("Product ok: id=" + expected.id() + " name='" + expected.name() + "'");
    }

    public static void assertBrandsListData(BrandsListResponse body) {
        Assert.assertNotNull(body, "Brands response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.responseCode());
        Assert.assertTrue(body.brandCount() >= 10, "Expected many brands, got " + body.brandCount());

        assertBrandExact(body.requireById(KnownBrands.POLO.id()), KnownBrands.POLO);
        assertBrandExact(body.requireById(KnownBrands.HM.id()), KnownBrands.HM);
        assertBrandExact(body.requireById(KnownBrands.MADAME.id()), KnownBrands.MADAME);
        ExtentLogger.pass("Brands list ok: count=" + body.brandCount());
    }

    public static void assertBrandExact(Brand actual, Brand expected) {
        Assert.assertNotNull(actual, "Actual brand is null");
        Assert.assertEquals(actual.id(), expected.id(), "id");
        Assert.assertEquals(actual.brand(), expected.brand(), "brand");
        ExtentLogger.pass("Brand ok: id=" + expected.id() + " brand='" + expected.brand() + "'");
    }

    public static void assertUserDetailResponse(UserDetailResponse body, RegistrationData expected) {
        Assert.assertNotNull(body, "User detail response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.responseCode());
        assertUserDetailMatches(body.user(), expected);
    }

    public static void assertUserDetailMatches(UserDetail user, RegistrationData expected) {
        Assert.assertNotNull(user, "user must be present");
        Assert.assertTrue(user.id() > 0, "user.id should be positive");
        Assert.assertEquals(user.name(), expected.name(), "name");
        Assert.assertEquals(user.email(), expected.email(), "email");
        Assert.assertEquals(user.title(), "Mr", "title");
        Assert.assertEquals(user.birthDay(), expected.birthDay(), "birth_day");
        Assert.assertEquals(user.birthMonth(), expected.birthMonth(), "birth_month");
        Assert.assertEquals(user.birthYear(), expected.birthYear(), "birth_year");
        Assert.assertEquals(user.firstName(), expected.firstName(), "first_name");
        Assert.assertEquals(user.lastName(), expected.lastName(), "last_name");
        Assert.assertEquals(user.company(), expected.company(), "company");
        Assert.assertEquals(user.address1(), expected.address1(), "address1");
        Assert.assertEquals(user.address2(), expected.address2(), "address2");
        Assert.assertEquals(user.country(), expected.country(), "country");
        Assert.assertEquals(user.state(), expected.state(), "state");
        Assert.assertEquals(user.city(), expected.city(), "city");
        Assert.assertEquals(user.zipcode(), expected.zipcode(), "zipcode");
        ExtentLogger.pass("User detail ok for " + expected.email());
    }
}
