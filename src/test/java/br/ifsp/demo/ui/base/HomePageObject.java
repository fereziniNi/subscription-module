package br.ifsp.demo.ui.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePageObject extends BasePageObject {
    private static final By PAGE_TITLE = By.cssSelector(".page-title");
    private static final By PAGE_SUBTITLE = By.cssSelector(".page-subtitle");
    private static final By LOGOUT_BUTTON = By.cssSelector("button.danger");
    private static final By CREATE_SUBSCRIPTION_LINK = By.linkText("Create subscription");
    private static final By VIEW_SUBSCRIPTIONS_LINK = By.linkText("View subscriptions");
    private static final By GENERATE_INVOICE_LINK = By.linkText("Generate invoice");

    public HomePageObject(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isElementPresent(PAGE_TITLE) &&
                isElementPresent(LOGOUT_BUTTON);
    }

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public boolean isLogoutButtonVisible() {
        return isElementPresent(LOGOUT_BUTTON);
    }

    public void logout() {
        click(LOGOUT_BUTTON);
    }

    // Navegação
    public void goToCreateSubscription() {
        click(CREATE_SUBSCRIPTION_LINK);
    }

    public void goToViewSubscriptions() {
        click(VIEW_SUBSCRIPTIONS_LINK);
    }

    public void goToGenerateInvoice() {
        click(GENERATE_INVOICE_LINK);
    }
}