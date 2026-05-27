package br.ifsp.demo.ui.objects;

import br.ifsp.demo.ui.base.BasePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePageObject extends BasePageObject {
    private static final By PAGE_TITLE = By.cssSelector(".page-title");
    private static final By PAGE_SUBTITLE = By.cssSelector(".page-subtitle");
    private static final By LOGOUT_BUTTON = By.cssSelector("button.danger");
    private static final By CREATE_SUBSCRIPTION_LINK = By.linkText("Create subscription");
    private static final By VIEW_SUBSCRIPTIONS_LINK = By.linkText("View subscriptions");
    private static final By GENERATE_INVOICE_LINK = By.linkText("Generate invoice");
    private static final By BACK_BUTTON = By.cssSelector("button.secondary");

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

    public String getPageSubtitle() { return getText(PAGE_SUBTITLE); }

    public boolean isLogoutButtonVisible() {
        return isElementPresent(LOGOUT_BUTTON);
    }

    public boolean isCreateSubscriptionLinkVisible() { return isElementPresent(CREATE_SUBSCRIPTION_LINK); }

    public boolean isViewSubscriptionsLinkVisible() { return isElementPresent(VIEW_SUBSCRIPTIONS_LINK); }

    public boolean isGenerateInvoiceLinkVisible() { return isElementPresent(GENERATE_INVOICE_LINK); }

    public String getCreateSubscriptionLinkHref() { return waitForElement(CREATE_SUBSCRIPTION_LINK).getAttribute("href"); }

    public String getViewSubscriptionsLinkHref() { return waitForElement(VIEW_SUBSCRIPTIONS_LINK).getAttribute("href"); }

    public String getGenerateInvoiceLinkHref() { return waitForElement(GENERATE_INVOICE_LINK).getAttribute("href"); }

    public boolean isBackButtonVisible() { return isElementPresent(BACK_BUTTON); }

    public String getLogoutButtonText() {return getText(LOGOUT_BUTTON);}

    public String getBackButtonText() { return getText(BACK_BUTTON);}

    public void logout() {
        click(LOGOUT_BUTTON);
    }

    public void clickBackButton() {click(BACK_BUTTON);}


    public void goToCreateSubscription() {
        click(CREATE_SUBSCRIPTION_LINK);
    }

    public void goToViewSubscriptions() {
        click(VIEW_SUBSCRIPTIONS_LINK);
    }

    public void goToGenerateInvoice() {
        click(GENERATE_INVOICE_LINK);
    }

    public boolean hasDataDiscoverAttribute(String linkText) {
        try {
            var element = driver.findElement(By.linkText(linkText));
            String attr = element.getAttribute("data-discover");
            return "true".equals(attr);
        } catch (Exception e) {
            return false;
        }
    }
}