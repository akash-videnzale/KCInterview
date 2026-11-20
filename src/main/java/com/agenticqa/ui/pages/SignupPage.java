package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import com.agenticqa.data.models.RegistrationData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the account-registration form.
 *
 * @author Akash Aggarwal
 */
public class SignupPage extends BasePage {

    public SignupPage() {
        super();
    }

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAccountInfoVisible() {
        return isVisible(By.xpath("//b[contains(normalize-space(),'Enter Account Information')]"))
                || isVisible(byDataQa("create-account"));
    }

    public AccountPage completeRegistration(RegistrationData data) {
        ExtentLogger.step("Fill account information and create account");
        dismissOverlaysIfPresent();
        waitVisible(byDataQa("create-account"));
        click(By.id("id_gender1"));
        type(byDataQa("password"), data.password());
        selectByValue(byDataQa("days"), data.birthDay());
        selectByValue(byDataQa("months"), data.birthMonth());
        selectByValue(byDataQa("years"), data.birthYear());

        type(byDataQa("first_name"), data.firstName());
        type(byDataQa("last_name"), data.lastName());
        type(byDataQa("company"), data.company());
        type(byDataQa("address"), data.address1());
        type(byDataQa("address2"), data.address2());
        selectByVisibleText(byDataQa("country"), data.country());
        type(byDataQa("state"), data.state());
        type(byDataQa("city"), data.city());
        type(byDataQa("zipcode"), data.zipcode());
        type(byDataQa("mobile_number"), data.mobileNumber());

        dismissOverlaysIfPresent();
        click(byDataQa("create-account"));
        waitForDomReady();
        waitVisible(byDataQa("account-created"));
        ExtentLogger.screenshot("After create account");
        return new AccountPage();
    }
}
