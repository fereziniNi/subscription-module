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

    @Nested
    @Tag("EquivalenceClassUi")
    class EquivalenceClassUi{
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

            registerPage.register("teste", "teste", email, "teste123");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            driver.get("https://subscription-module-seven.vercel.app/register");

            registerPage.register("teste", "teste", email, "teste123");

            final SoftAssertions softly = new SoftAssertions();
            softly.assertThat(driver.getCurrentUrl()).contains("/register");
            softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
            softly.assertAll();
        }

        @Test
        @DisplayName("Should not allow registration with empty name")
        void shouldNotAllowRegistrationWithEmptyName() {
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";

            registerPage.register("", "Sobrenome", email, "senha123");

            final SoftAssertions softly = new SoftAssertions();
            softly.assertThat(driver.getCurrentUrl()).contains("/register");
            softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
            softly.assertAll();
        }

        @Test
        @DisplayName("Should not allow registration with empty lastname")
        void shouldNotAllowRegistrationWithEmptyLastname() {
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";

            registerPage.register("Nome", "", email, "senha123");

            final SoftAssertions softly = new SoftAssertions();
            softly.assertThat(driver.getCurrentUrl()).contains("/register");
            softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
            softly.assertAll();
        }

        @Test
        @DisplayName("Should accept compound lastname with spaces")
        void shouldAcceptCompoundLastnameWithSpaces() {
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";

            registerPage.register("Nome", "da Silva", email, "senha123");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            assertThat(driver.getCurrentUrl()).contains("/login");
        }

        @Test
        @DisplayName("Should not allow registration with empty email")
        void shouldNotAllowRegistrationWithEmptyEmail() {
            var registerPage = new RegistrationPageObject(driver);

            registerPage.register("Nome", "Sobrenome", "", "senha123");

            assertThat(driver.getCurrentUrl()).contains("/register");
        }

        @ParameterizedTest
        @CsvSource({
                "usuario@",
                "@dominio.com",
                "usuario@dominio",
                "user @test.com",
                "user#$%@test.com",
                "..@test.com",
                "user..name@test.com",
                ".user@test.com",
                "user.@test.com",
                "usuariodominio.com"
        })
        @DisplayName("Should reject malformed email formats")
        void shouldRejectMalformedEmailFormats(String email) {
            var registerPage = new RegistrationPageObject(driver);

            registerPage.register("Nome", "Sobrenome", email, "senha123");

            final SoftAssertions softly = new SoftAssertions();
            softly.assertThat(driver.getCurrentUrl()).contains("/register");
            softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
            softly.assertAll();
        }

        @Test
        @DisplayName("Should accept valid email with dots")
        void shouldAcceptValidEmailWithDots() {
            var registerPage = new RegistrationPageObject(driver);
            String email = "user.name" + System.currentTimeMillis() + "@test.com";

            registerPage.register("Nome", "Sobrenome", email, "senha123");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            assertThat(driver.getCurrentUrl()).contains("/login");
        }

        @Test
        @DisplayName("Should not allow registration with empty password")
        void shouldNotAllowRegistrationWithEmptyPassword() {
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";

            registerPage.register("Nome", "Sobrenome", email, "");

            final SoftAssertions softly = new SoftAssertions();
            softly.assertThat(driver.getCurrentUrl()).contains("/register");
            softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
            softly.assertAll();
        }

        @Test
        @DisplayName("Should accept password with special characters")
        void shouldAcceptPasswordWithSpecialCharacters() {
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";

            registerPage.register("Nome", "Sobrenome", email, "P@ssw0rd!");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            assertThat(driver.getCurrentUrl()).contains("/login");
        }

        @Test
        @DisplayName("Should accept password with spaces")
        void shouldAcceptPasswordWithSpaces() {
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";

            registerPage.register("Nome", "Sobrenome", email, "my password");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            assertThat(driver.getCurrentUrl()).contains("/login");
        }

        @Test
        @DisplayName("Should not allow registration with all empty fields")
        void shouldNotAllowRegistrationWithAllEmptyFields() {
            var registerPage = new RegistrationPageObject(driver);

            registerPage.register("", "", "", "");

            final SoftAssertions softly = new SoftAssertions();
            softly.assertThat(driver.getCurrentUrl()).contains("/register");
            softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
            softly.assertAll();
        }

        @ParameterizedTest
        @CsvSource({
                "João, Silva, joao@test.com, senha123",
                "Maria Clara, Santos, maria@test.com, pass456",
                "A, B, ab@test.com, x",
                "José-Carlos, Oliveira-Santos, jose@test.com, MyP@ss123"
        })
        @DisplayName("Should accept valid registration data in equivalence class")
        void shouldAcceptValidRegistrationData(String name, String lastname, String email, String password) {
            var registerPage = new RegistrationPageObject(driver);
            String uniqueEmail = email.replace("@", System.currentTimeMillis() + "@");

            registerPage.register(name, lastname, uniqueEmail, password);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            assertThat(driver.getCurrentUrl()).contains("/login");
        }

        @ParameterizedTest
        @CsvSource({
                "password123",
                "P@ssw0rd!",
                "12345678",
                "my password with spaces",
                "パスワード"
        })
        @DisplayName("Should accept various password formats in equivalence class")
        void shouldAcceptVariousPasswordFormats(String password) {
            var registerPage = new RegistrationPageObject(driver);
            String email = "teste" + System.currentTimeMillis() + "@teste.com";

            registerPage.register("Nome", "Sobrenome", email, password);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.urlContains("/login"));

            assertThat(driver.getCurrentUrl()).contains("/login");
        }
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

    @Test
    @DisplayName("Should accept name with minimum valid length (1 character)")
    void shouldAcceptNameWithMinimumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";

        registerPage.register("A", "Sobrenome", email, "senha123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should accept name with maximum valid length (50 characters)")
    void shouldAcceptNameWithMaximumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        String longName = "A".repeat(50);

        registerPage.register(longName, "Sobrenome", email, "senha123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should accept lastname with minimum valid length (1 character)")
    void shouldAcceptLastnameWithMinimumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";

        registerPage.register("Nome", "S", email, "senha123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should accept lastname with maximum valid length (50 characters)")
    void shouldAcceptLastnameWithMaximumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        String longLastname = "S".repeat(50);

        registerPage.register("Nome", longLastname, email, "senha123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should accept email with minimum valid length")
    void shouldAcceptEmailWithMinimumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "a@b.co"; // 6 caracteres

        registerPage.register("Nome", "Sobrenome", email, "senha123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should reject email below minimum length")
    void shouldRejectEmailBelowMinimumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "a@b.c";

        registerPage.register("Nome", "Sobrenome", email, "senha123");

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(driver.getCurrentUrl()).contains("/register");
        softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should accept email with maximum valid length (254 characters)")
    void shouldAcceptEmailWithMaximumLength() {
        var registerPage = new RegistrationPageObject(driver);

        String localPart = "a".repeat(64);
        String domainPart = "b".repeat(243) + ".com";
        String maxEmail = localPart + "@" + domainPart;

        registerPage.register("Nome", "Sobrenome", maxEmail, "senha123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should reject email exceeding maximum length (255+ characters)")
    void shouldRejectEmailExceedingMaximumLength() {
        var registerPage = new RegistrationPageObject(driver);

        String tooLongEmail = "a".repeat(250) + "@test.com";

        registerPage.register("Nome", "Sobrenome", tooLongEmail, "senha123");

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(driver.getCurrentUrl()).contains("/register");
        softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should accept password with minimum length (1 character)")
    void shouldAcceptPasswordWithMinimumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";

        registerPage.register("Nome", "Sobrenome", email, "a");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should accept password with maximum valid length (128 characters)")
    void shouldAcceptPasswordWithMaximumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        String longPassword = "P".repeat(128);

        registerPage.register("Nome", "Sobrenome", email, longPassword);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "' OR '1'='1|Sobrenome|email@test.com|senha",
            "Nome|' OR '1'='1|email@test.com|senha",
            "Nome|Sobrenome|test@test.com|' OR '1'='1"
    })
    @DisplayName("Should safely handle SQL injection attempts in all fields")
    void shouldSafelyHandleSQLInjectionAttempts(String name, String lastname, String email, String password) {
        var registerPage = new RegistrationPageObject(driver);

        registerPage.register(name, lastname, email, password);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(driver.getCurrentUrl()).contains("/register");
        softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should reject name exceeding maximum length (51 characters)")
    void shouldRejectNameExceedingMaximumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        String tooLongName = "A".repeat(51);

        registerPage.register(tooLongName, "Sobrenome", email, "senha123");

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(driver.getCurrentUrl()).contains("/register");
        softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should reject lastname exceeding maximum length (51 characters)")
    void shouldRejectLastnameExceedingMaximumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        String tooLongLastname = "S".repeat(51);

        registerPage.register("Nome", tooLongLastname, email, "senha123");

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(driver.getCurrentUrl()).contains("/register");
        softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should reject password exceeding maximum length (129 characters)")
    void shouldRejectPasswordExceedingMaximumLength() {
        var registerPage = new RegistrationPageObject(driver);
        String email = "teste" + System.currentTimeMillis() + "@teste.com";
        String tooLongPassword = "P".repeat(129);

        registerPage.register("Nome", "Sobrenome", email, tooLongPassword);

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(driver.getCurrentUrl()).contains("/register");
        softly.assertThat(registerPage.pageErrorMessage()).isEqualTo("Could not register user.");
        softly.assertAll();
    }
}