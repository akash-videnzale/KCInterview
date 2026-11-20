package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import com.agenticqa.ui.messages.UiMessages;
import org.openqa.selenium.WebDriver;

/**
 * Page object for login / signup entry (email, password, new-user name).
 *
 * @author Akash Aggarwal
 */
public class LoginPage extends BasePage {

    public LoginPage() {
        super();
    }

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        ExtentLogger.step("Open login page");
        navigate("/login");
        dismissOverlaysIfPresent();
        if (!isVisible(byDataQa("login-button"))) {
            navigate("/login");
            dismissOverlaysIfPresent();
        }
        waitVisible(byDataQa("login-button"));
        waitVisible(byDataQa("signup-button"));
        ExtentLogger.screenshot("Login page ready");
        return this;
    }

    public HomePage login(String email, String password) {
        ExtentLogger.step("Submit login credentials");
        dismissOverlaysIfPresent();
        waitVisible(byDataQa("login-email"));
        type(byDataQa("login-email"), email);
        type(byDataQa("login-password"), password);
        click(byDataQa("login-button"));
        waitForDomReady();
        ExtentLogger.screenshot("After login submit");
        return new HomePage();
    }

    public SignupPage startSignup(String name, String email) {
        ExtentLogger.step("Start signup with name/email");
        dismissOverlaysIfPresent();
        waitVisible(byDataQa("signup-name"));
        waitVisible(byDataQa("signup-email"));
        waitVisible(byDataQa("signup-button"));
        type(byDataQa("signup-name"), name);
        type(byDataQa("signup-email"), email);
        dismissOverlaysIfPresent();
        click(byDataQa("signup-button"));
        waitForDomReady();
        dismissOverlaysIfPresent();
        // If AE ads blocked signup submit, retry once from a fresh login page
        if (!isVisible(byDataQa("create-account"))) {
            navigate("/login");
            dismissOverlaysIfPresent();
            type(byDataQa("signup-name"), name);
            type(byDataQa("signup-email"), email);
            click(byDataQa("signup-button"));
            waitForDomReady();
            dismissOverlaysIfPresent();
        }
        waitVisible(byDataQa("create-account"));
        ExtentLogger.screenshot("Account information form");
        return new SignupPage();
    }

    public boolean isLoginFormVisible() {
        dismissOverlaysIfPresent();
        try {
            waitVisible(byDataQa("login-button"));
            return isVisible(byDataQa("login-button"));
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isInvalidCredentialsVisible() {
        return isVisible(byTagTextContains("p", UiMessages.INVALID_LOGIN));
    }
}
