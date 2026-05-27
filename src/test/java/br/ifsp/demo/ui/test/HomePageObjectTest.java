package br.ifsp.demo.ui.test;

import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.objects.AuthenticationPageObject;
import br.ifsp.demo.ui.objects.HomePageObject;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HomePageObjectTest extends BaseSeleniumTest {
    private static final String LOGIN_URL = "https://subscription-module-seven.vercel.app/login";
    private static final String VALID_EMAIL = "teste@teste.com";
    private static final String VALID_PASSWORD = "teste";

    @Override
    public void setInitialPage() {
        driver.get(LOGIN_URL);
    }
    private HomePageObject loginAndNavigateToHome() {
        var authPage = new AuthenticationPageObject(driver);
        return authPage.loginSuccessfully(VALID_EMAIL, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Should display all UI elements correctly on home page load")
    void shouldDisplayAllUIElementsCorrectly() {
        var homePage = loginAndNavigateToHome();

        final SoftAssertions softly = new SoftAssertions();

        softly.assertThat(homePage.isLoaded()).isTrue();
        softly.assertThat(homePage.getPageTitle()).isEqualTo("Subscription Module");
        softly.assertThat(homePage.getPageSubtitle())
                .isEqualTo("Create, renew, cancel and inspect subscription cycles.");

        softly.assertAll();
    }

    @Test
    @DisplayName("Should display all menu links correctly")
    void shouldDisplayAllMenuLinks() {
        var homePage = loginAndNavigateToHome();

        final SoftAssertions softly = new SoftAssertions();

        softly.assertThat(homePage.isCreateSubscriptionLinkVisible()).isTrue();
        softly.assertThat(homePage.isViewSubscriptionsLinkVisible()).isTrue();
        softly.assertThat(homePage.isGenerateInvoiceLinkVisible()).isTrue();

        softly.assertThat(homePage.getCreateSubscriptionLinkHref()).contains("/subscriptions/create");
        softly.assertThat(homePage.getViewSubscriptionsLinkHref()).contains("/subscriptions");
        softly.assertThat(homePage.getGenerateInvoiceLinkHref()).contains("/invoices");

        softly.assertAll();
    }

    @Test
    @DisplayName("Should display Back and Logout buttons")
    void shouldDisplayActionButtons() {
        var homePage = loginAndNavigateToHome();

        final SoftAssertions softly = new SoftAssertions();

        softly.assertThat(homePage.isBackButtonVisible()).isTrue();
        softly.assertThat(homePage.getBackButtonText()).isEqualTo("Back");

        softly.assertThat(homePage.isLogoutButtonVisible()).isTrue();
        softly.assertThat(homePage.getLogoutButtonText()).isEqualTo("Logout");

        softly.assertAll();
    }

    @Test
   @DisplayName("Should navigate to Create Subscription page when clicking the link")
   void shouldNavigateToCreateSubscription() {
        var homePage = loginAndNavigateToHome();
        homePage.goToCreateSubscription();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/subscriptions/create"));
        assertThat(driver.getCurrentUrl()).contains("/subscriptions/create");
    }

    @Test
    @DisplayName("Should navigate to View Subscriptions page when clicking the link")
    void shouldNavigateToViewSubscriptions() {
        var homePage = loginAndNavigateToHome();
        homePage.goToViewSubscriptions();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/subscriptions"));
        assertThat(driver.getCurrentUrl()).contains("/subscriptions");
    }

    @Test
    @DisplayName("Should navigate to Generate Invoice page when clicking the link")
    void shouldNavigateToGenerateInvoice() {
        var homePage = loginAndNavigateToHome();
        homePage.goToGenerateInvoice();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/invoices"));
        assertThat(driver.getCurrentUrl()).contains("/invoices");
    }

    @Test
    @DisplayName("Should logout when clicking Logout button")
    void shouldLogoutWhenClickingLogoutButton() {
        var homePage = loginAndNavigateToHome();

        homePage.logout();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should have data-discover attribute on menu links")
    void shouldHaveDataDiscoverAttributeOnLinks() {
        var homePage = loginAndNavigateToHome();

        final SoftAssertions softly = new SoftAssertions();

        softly.assertThat(homePage.hasDataDiscoverAttribute("Create subscription")).isTrue();
        softly.assertThat(homePage.hasDataDiscoverAttribute("View subscriptions")).isTrue();
        softly.assertThat(homePage.hasDataDiscoverAttribute("Generate invoice")).isTrue();

        softly.assertAll();
    }
}