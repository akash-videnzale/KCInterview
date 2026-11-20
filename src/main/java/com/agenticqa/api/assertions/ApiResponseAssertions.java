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
        Assert.assertEquals(response.getResponseCode(), code, "responseCode mismatch");
        Assert.assertEquals(response.getMessage(), message, "message mismatch");
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
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.getResponseCode());
        Assert.assertTrue(body.productCount() >= 10, "Expected many products, got " + body.productCount());

        assertProductExact(body.requireById(KnownProducts.BLUE_TOP.getId()), KnownProducts.BLUE_TOP);
        assertProductExact(body.requireById(KnownProducts.MEN_TSHIRT.getId()), KnownProducts.MEN_TSHIRT);
        assertProductExact(body.requireById(KnownProducts.SLEEVELESS_DRESS.getId()), KnownProducts.SLEEVELESS_DRESS);
        ExtentLogger.pass("Products list ok: count=" + body.productCount());
    }

    public static void assertSearchResultsData(ProductsListResponse body, String term) {
        Assert.assertNotNull(body, "Search response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.getResponseCode());
        Assert.assertFalse(body.isEmpty(), "Expected products for term=" + term);

        boolean nameMatch = body.getProducts().stream()
                .anyMatch(p -> p.getName() != null && p.getName().toLowerCase().contains(term.toLowerCase()));
        Assert.assertTrue(nameMatch, "At least one product name should contain '" + term + "'");

        if (KnownProducts.SEARCH_TERM_TOP.equalsIgnoreCase(term)) {
            assertProductExact(body.requireById(KnownProducts.BLUE_TOP.getId()), KnownProducts.BLUE_TOP);
        }
        ExtentLogger.pass("Search ok for term='" + term + "', count=" + body.productCount());
    }

    public static void assertEmptyProductsList(ProductsListResponse body) {
        Assert.assertNotNull(body, "Products response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.getResponseCode());
        Assert.assertEquals(body.productCount(), 0, "Expected empty products list");
        ExtentLogger.pass("Empty products list ok");
    }

    public static void assertProductExact(Product actual, Product expected) {
        Assert.assertNotNull(actual, "Actual product is null");
        Assert.assertEquals(actual.getId(), expected.getId(), "id");
        Assert.assertEquals(actual.getName(), expected.getName(), "name");
        Assert.assertEquals(actual.getPrice(), expected.getPrice(), "price");
        Assert.assertEquals(actual.getBrand(), expected.getBrand(), "brand");
        Assert.assertEquals(actual.getCategory().getCategory(), expected.getCategory().getCategory(), "category");
        Assert.assertEquals(actual.getCategory().getUsertype().getUsertype(),
                expected.getCategory().getUsertype().getUsertype(), "usertype");
        ExtentLogger.pass("Product ok: id=" + expected.getId() + " name='" + expected.getName() + "'");
    }

    public static void assertBrandsListData(BrandsListResponse body) {
        Assert.assertNotNull(body, "Brands response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.getResponseCode());
        Assert.assertTrue(body.brandCount() >= 10, "Expected many brands, got " + body.brandCount());

        assertBrandExact(body.requireById(KnownBrands.POLO.getId()), KnownBrands.POLO);
        assertBrandExact(body.requireById(KnownBrands.HM.getId()), KnownBrands.HM);
        assertBrandExact(body.requireById(KnownBrands.MADAME.getId()), KnownBrands.MADAME);
        ExtentLogger.pass("Brands list ok: count=" + body.brandCount());
    }

    public static void assertBrandExact(Brand actual, Brand expected) {
        Assert.assertNotNull(actual, "Actual brand is null");
        Assert.assertEquals(actual.getId(), expected.getId(), "id");
        Assert.assertEquals(actual.getBrand(), expected.getBrand(), "brand");
        ExtentLogger.pass("Brand ok: id=" + expected.getId() + " brand='" + expected.getBrand() + "'");
    }

    public static void assertUserDetailResponse(UserDetailResponse body, RegistrationData expected) {
        Assert.assertNotNull(body, "User detail response must not be null");
        Assert.assertTrue(body.isSuccess(), "Expected responseCode 200, got " + body.getResponseCode());
        assertUserDetailMatches(body.getUser(), expected);
    }

    public static void assertUserDetailMatches(UserDetail user, RegistrationData expected) {
        Assert.assertNotNull(user, "user must be present");
        Assert.assertTrue(user.getId() > 0, "user.id should be positive");
        Assert.assertEquals(user.getName(), expected.name(), "name");
        Assert.assertEquals(user.getEmail(), expected.email(), "email");
        Assert.assertEquals(user.getTitle(), "Mr", "title");
        Assert.assertEquals(user.getBirthDay(), expected.birthDay(), "birth_day");
        Assert.assertEquals(user.getBirthMonth(), expected.birthMonth(), "birth_month");
        Assert.assertEquals(user.getBirthYear(), expected.birthYear(), "birth_year");
        Assert.assertEquals(user.getFirstName(), expected.firstName(), "first_name");
        Assert.assertEquals(user.getLastName(), expected.lastName(), "last_name");
        Assert.assertEquals(user.getCompany(), expected.company(), "company");
        Assert.assertEquals(user.getAddress1(), expected.address1(), "address1");
        Assert.assertEquals(user.getAddress2(), expected.address2(), "address2");
        Assert.assertEquals(user.getCountry(), expected.country(), "country");
        Assert.assertEquals(user.getState(), expected.state(), "state");
        Assert.assertEquals(user.getCity(), expected.city(), "city");
        Assert.assertEquals(user.getZipcode(), expected.zipcode(), "zipcode");
        ExtentLogger.pass("User detail ok for " + expected.email());
    }
}
