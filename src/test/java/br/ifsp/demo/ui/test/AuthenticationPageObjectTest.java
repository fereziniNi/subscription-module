package br.ifsp.demo.ui.test;


import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.objects.AuthenticationPageObject;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthenticationPageObjectTest extends BaseSeleniumTest {
    private static final String URL = "https://subscription-module-seven.vercel.app/login";

    @Override
    public void setInitialPage() {
        driver.get(URL);
    }

    @Test
    @DisplayName("Should navigate to registration page by clicking the link")
    void shouldNavigateToRegistrationPageByClickingTheLink() {
        var authPage = new AuthenticationPageObject(driver);
        var registrationPage = authPage.goToRegister();

        final SoftAssertions softly = new SoftAssertions();

        softly.assertThat(driver.getCurrentUrl()).contains("/register");
        softly.assertThat(registrationPage.isNameFieldVisible()).isTrue();
        softly.assertThat(registrationPage.isLastNameFieldVisible()).isTrue();
        softly.assertThat(registrationPage.isEmailFieldVisible()).isTrue();
        softly.assertThat(registrationPage.isPasswordFieldVisible()).isTrue();
        softly.assertAll();
    }

    @ParameterizedTest
    @CsvSource({
            "wrong@email.com, wrong-password",
            " , wrong-password",
            "wrong@email.com, ",
            ","
    })
    @DisplayName("Should show invalid credentials message for invalid login attempts")
    void shouldShowInvalidCredentialsMessage(String email, String password) {
        var authPage = new AuthenticationPageObject(driver);
        authPage.attemptLogin(email, password);
        assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
    }

    @Test
    @DisplayName("Should login with valid credentials")
    void shouldLoginWithValidCredentials() {
        var loginPage = new AuthenticationPageObject(driver);
        var homePage = loginPage.loginSuccessfully("teste@teste.com", "teste");

        final SoftAssertions softly = new SoftAssertions();

        softly.assertThat(homePage.isLoaded()).isTrue();
        softly.assertThat(homePage.getPageTitle()).isEqualTo("Subscription Module");
        softly.assertThat(homePage.isLogoutButtonVisible()).isTrue();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should mask password input field")
    void shouldMaskPasswordInputField() {
        var loginPage = new AuthenticationPageObject(driver);
        assertThat(loginPage.getPasswordType()).isEqualTo("password");
    }

    @Test
    @DisplayName("Should display all UI elements correctly on page load")
    void shouldDisplayAllUIElementsCorrectly() {
        var authPage = new AuthenticationPageObject(driver);

        final SoftAssertions softly = new SoftAssertions();

        softly.assertThat(authPage.isPageTitleVisible()).isTrue();
        softly.assertThat(authPage.getPageTitle()).isEqualTo("Login");
        softly.assertThat(authPage.getPageSubtitle()).isEqualTo("Access your subscription workspace.");

        softly.assertThat(authPage.isEmailFieldVisible()).isTrue();
        softly.assertThat(authPage.getEmailPlaceholder()).isEqualTo("you@example.com");
        softly.assertThat(authPage.isPasswordFieldVisible()).isTrue();
        softly.assertThat(authPage.getPasswordPlaceholder()).isEqualTo("Your password");

        softly.assertThat(authPage.isSubmitButtonVisible()).isTrue();
        softly.assertThat(authPage.getSubmitButtonText()).isEqualTo("Sign in");
        softly.assertThat(authPage.isRegisterLinkVisible()).isTrue();
        softly.assertThat(authPage.getRegisterLinkText()).isEqualTo("Register");

        softly.assertAll();
    }

    @Test
    @DisplayName("Should have empty fields on initial page load")
    void shouldHaveEmptyFieldsOnInitialLoad() {
        var authPage = new AuthenticationPageObject(driver);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(authPage.getEmailValue()).isEmpty();
        softly.assertThat(authPage.getPasswordValue()).isEmpty();
        softly.assertAll();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "' OR '1'='1|password",
            "test@test.com|' OR '1'='1",
            "admin'--|password",
            "test@test.com|1' OR '1' = '1"
    })
    @DisplayName("Should safely handle SQL injection attempts")
    void shouldSafelyHandleSQLInjectionAttempts(String email, String password) {
        var authPage = new AuthenticationPageObject(driver);
        authPage.attemptLogin(email, password);

        assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
    }

    }