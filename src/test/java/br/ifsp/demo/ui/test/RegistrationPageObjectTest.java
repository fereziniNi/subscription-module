package br.ifsp.demo.ui.test;

import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.objects.AuthenticationPageObject;
import br.ifsp.demo.ui.objects.RegistrationPageObject;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegistrationPageObjectTest extends BaseSeleniumTest {
    private static final String URL = "https://subscription-module-seven.vercel.app/register";

    @Override
    public void setInitialPage() {
        driver.get(URL);
    }

    @Test
    @DisplayName("Should Registration a new profile")
    void shouldRegistrationANewProfile() {
        var registerPage = new RegistrationPageObject(driver);

        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        registerPage.register("teste1", "teste1", email, "teste1");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should not allow duplicated email registration")
    void shouldNotAllowDuplicatedEmailRegistration() {
        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        var registerPage = new RegistrationPageObject(driver);

        registerPage.register("teste","teste",email,"teste123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        driver.get("https://subscription-module-seven.vercel.app/register");

        registerPage.register("teste", "teste", email,"teste123");

        assertThat(registerPage.pageErrorMessage()).contains("Could not register user.");
    }
    @Test
    @DisplayName("Should display all UI elements correctly on page load")
    void shouldDisplayAllUIElementsCorrectly() {
        var registerPage = new RegistrationPageObject(driver);

        final SoftAssertions softly = new SoftAssertions();

        softly.assertThat(registerPage.getPageTitle()).isEqualTo("Register");
        softly.assertThat(registerPage.getPageSubtitle()).isEqualTo("Create an account to manage subscriptions.");

        softly.assertThat(registerPage.isNameFieldVisible()).isTrue();
        softly.assertThat(registerPage.getNamePlaceholder()).isEqualTo("Name");
        softly.assertThat(registerPage.isLastNameFieldVisible()).isTrue();
        softly.assertThat(registerPage.getLastnamePlaceholder()).isEqualTo("Lastname");
        softly.assertThat(registerPage.isEmailFieldVisible()).isTrue();
        softly.assertThat(registerPage.getEmailPlaceholder()).isEqualTo("you@example.com");
        softly.assertThat(registerPage.isPasswordFieldVisible()).isTrue();
        softly.assertThat(registerPage.getPasswordPlaceholder()).isEqualTo("Your password");

        softly.assertThat(registerPage.isCreateAccountButtonVisible()).isTrue();
        softly.assertThat(registerPage.getSubmitButtonText()).isEqualTo("Create account");
        softly.assertThat(registerPage.isLoginLinkVisible()).isTrue();
        softly.assertThat(registerPage.isBackButtonVisible()).isTrue();
        softly.assertThat(registerPage.getBackButtonText()).isEqualTo("Back");

        softly.assertAll();
    }

    @Test
    @DisplayName("Should have empty fields on initial page load")
    void shouldHaveEmptyFieldsOnInitialLoad() {
        var registerPage = new RegistrationPageObject(driver);

        final org.assertj.core.api.SoftAssertions softly = new org.assertj.core.api.SoftAssertions();
        softly.assertThat(registerPage.getNameValue()).isEmpty();
        softly.assertThat(registerPage.getLastnameValue()).isEmpty();
        softly.assertThat(registerPage.getEmailValue()).isEmpty();
        softly.assertThat(registerPage.getPasswordValue()).isEmpty();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should navigate to login page when clicking Login link")
    void shouldNavigateToLoginPageWhenClickingLink() {
        var registerPage = new RegistrationPageObject(driver);
        registerPage.clickLoginLink();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should navigate back to previous page when clicking Back button")
    void shouldNavigateBackToPreviousPage() {
        driver.get("https://subscription-module-seven.vercel.app/login");

        var authPage = new AuthenticationPageObject(driver);
        var registerPage = authPage.goToRegister();
        assertThat(driver.getCurrentUrl()).contains("/register");


        registerPage.clickBackButton();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    }