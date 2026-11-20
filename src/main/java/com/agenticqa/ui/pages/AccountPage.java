package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for post-signup / account-created and delete-account confirmations.
 *
 * @author Akash Aggarwal
 */
public class AccountPage extends BasePage {

    public AccountPage() {
        super();
    }

    public AccountPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAccountCreatedVisible() {
        try {
            waitVisible(byDataQa("account-created"));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isAccountDeletedVisible() {
        try {
            waitVisible(byDataQa("account-deleted"));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public HomePage continueAfterCreated() {
        ExtentLogger.step("Continue after account created");
        dismissOverlaysIfPresent();
        // Prefer direct home navigation — AE continue click often hangs the headed renderer under ads
        navigate("/");
        dismissOverlaysIfPresent();
        waitDefault().until(d -> !d.findElements(By.cssSelector("a[href='/logout']")).isEmpty());
        ExtentLogger.screenshot("Authenticated home");
        return new HomePage();
    }

    public HomePage continueAfterDeleted() {
        ExtentLogger.step("Continue after account deleted");
        dismissOverlaysIfPresent();
        navigate("/");
        return new HomePage();
    }

    public AccountPage deleteAccount() {
        ExtentLogger.step("Delete account");
        dismissOverlaysIfPresent();
        // Direct URL is more reliable than header click under AE ad iframes
        navigate("/delete_account");
        dismissOverlaysIfPresent();
        waitVisible(byDataQa("account-deleted"));
        ExtentLogger.screenshot("Account deleted confirmation");
        return this;
    }
}
