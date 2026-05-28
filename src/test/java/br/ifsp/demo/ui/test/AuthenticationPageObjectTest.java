package br.ifsp.demo.ui.test;


import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.objects.AuthenticationPageObject;
import br.ifsp.demo.ui.objects.RegistrationPageObject;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Tag("Ui")
public class AuthenticationPageObjectTest extends BaseSeleniumTest {
    private static final String URL = "https://subscription-module-seven.vercel.app/login";

    @Override
    public void setInitialPage() {
        driver.get(URL);
    }

    @Nested
    @Tag("EquivalenceClassUi")
    class EquivalenceClassUi{
        @Test
        @DisplayName("Should reject login with unregistered email")
        void shouldRejectLoginWithUnregisteredEmail() {
            var authPage = new AuthenticationPageObject(driver);
            String unregisteredEmail = "naoexiste" + System.currentTimeMillis() + "@test.com";

            authPage.attemptLogin(unregisteredEmail, "qualquersenha");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @Test
        @DisplayName("Should reject login with correct email but wrong password")
        void shouldRejectLoginWithCorrectEmailButWrongPassword() {
            var authPage = new AuthenticationPageObject(driver);

            authPage.attemptLogin("teste@teste.com", "senhaerrada123");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @ParameterizedTest
        @CsvSource({
                "emailsemarroba.com",
                "email@",
                "@dominio.com",
                "email@dominio",
                "email..duplo@test.com"
        })
        @DisplayName("Should reject login with malformed email formats")
        void shouldRejectLoginWithMalformedEmail(String malformedEmail) {
            var authPage = new AuthenticationPageObject(driver);

            authPage.attemptLogin(malformedEmail, "senha123");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @Test
        @DisplayName("Should reject login with empty email field")
        void shouldRejectLoginWithEmptyEmail() {
            var authPage = new AuthenticationPageObject(driver);

            authPage.attemptLogin("", "senha123");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @Test
        @DisplayName("Should reject login with empty password field")
        void shouldRejectLoginWithEmptyPassword() {
            var authPage = new AuthenticationPageObject(driver);

            authPage.attemptLogin("teste@teste.com", "");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @Test
        @DisplayName("Should reject login with both fields empty")
        void shouldRejectLoginWithBothFieldsEmpty() {
            var authPage = new AuthenticationPageObject(driver);

            authPage.attemptLogin("", "");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @Test
        @DisplayName("Should accept valid credentials")
        void shouldAcceptValidCredentials() {
            var authPage = new AuthenticationPageObject(driver);
            var homePage = authPage.loginSuccessfully("teste@teste.com", "teste");

            assertThat(homePage.isLoaded()).isTrue();
        }
    }

    @Nested
    @Tag("LimitValueUi")
    class LimitValueUi {
        @Test
        @DisplayName("Should accept email with minimum valid length")
        void shouldAcceptEmailWithMinimumLength() {
            driver.get("https://subscription-module-seven.vercel.app/register");
            var registerPage = new RegistrationPageObject(driver);
            String minEmail = "a@b.co";

            registerPage.register("Test", "User", minEmail, "senha123");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            // Agora testar login
            var authPage = new AuthenticationPageObject(driver);
            var homePage = authPage.loginSuccessfully(minEmail, "senha123");

            assertThat(homePage.isLoaded()).isTrue();
        }

        @Test
        @DisplayName("Should reject email below minimum length")
        void shouldRejectEmailBelowMinimumLength() {
            var authPage = new AuthenticationPageObject(driver);

            authPage.attemptLogin("a@b.c", "senha123");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @Test
        @DisplayName("Should accept email with maximum valid length")
        void shouldAcceptEmailWithMaximumLength() {
            var authPage = new AuthenticationPageObject(driver);
            String maxEmail = "a".repeat(243) + "@test.com";

            driver.get("https://subscription-module-seven.vercel.app/register");
            var registerPage = new RegistrationPageObject(driver);
            registerPage.register("Test", "User", maxEmail, "senha123");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            var authPage2 = new AuthenticationPageObject(driver);
            var homePage = authPage2.loginSuccessfully(maxEmail, "senha123");

            assertThat(homePage.isLoaded()).isTrue();
        }

        @Test
        @DisplayName("Should reject email exceeding maximum length")
        void shouldRejectEmailExceedingMaximumLength() {
            var authPage = new AuthenticationPageObject(driver);
            String tooLongEmail = "a".repeat(250) + "@test.com";

            authPage.attemptLogin(tooLongEmail, "senha123");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @Test
        @DisplayName("Should accept password with minimum length")
        void shouldAcceptPasswordWithMinimumLength() {
            driver.get("https://subscription-module-seven.vercel.app/register");
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";

            registerPage.register("Test", "User", email, "a");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            // Testar login
            var authPage = new AuthenticationPageObject(driver);
            var homePage = authPage.loginSuccessfully(email, "a");

            assertThat(homePage.isLoaded()).isTrue();
        }

        @Test
        @DisplayName("Should accept password with maximum valid length")
        void shouldAcceptPasswordWithMaximumLength() {
            driver.get("https://subscription-module-seven.vercel.app/register");
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";
            String maxPassword = "P".repeat(128);

            registerPage.register("Test", "User", email, maxPassword);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            // Testar login
            var authPage = new AuthenticationPageObject(driver);
            var homePage = authPage.loginSuccessfully(email, maxPassword);

            assertThat(homePage.isLoaded()).isTrue();
        }

        @Test
        @DisplayName("Should reject password exceeding maximum length")
        void shouldRejectPasswordExceedingMaximumLength() {
            var authPage = new AuthenticationPageObject(driver);
            String tooLongPassword = "P".repeat(129);

            authPage.attemptLogin("teste@teste.com", tooLongPassword);

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }

        @Test
        @DisplayName("Should reject empty password at boundary")
        void shouldRejectEmptyPasswordAtBoundary() {
            var authPage = new AuthenticationPageObject(driver);

            authPage.attemptLogin("teste@teste.com", "");

            assertThat(authPage.getErrorMessage()).isEqualTo("Invalid credentials.");
        }
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