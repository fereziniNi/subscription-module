package br.ifsp.demo.ui.test;


import br.ifsp.demo.ui.objects.AuthenticationPageObject;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        var registrationPage = authPage.navigateToRegistrationPage();

        final var softly = new SoftAssertions();

        softly.assertThat(registrationPage.pageTitle()).isEqualTo("frontend");
        softly.assertThat(registrationPage.isNameFieldVisible()).isTrue();
        softly.assertThat(registrationPage.isLastNameFieldVisible()).isTrue();
        softly.assertThat(registrationPage.isEmailFieldVisible()).isTrue();
        softly.assertThat(registrationPage.isPasswordFieldVisible()).isTrue();

        softly.assertAll();
    }

    @ParameterizedTest
    @CsvSource({
            "wrong@email.com, wrong-password",
            "' ', wrong-password",
            "'wrong@email.com', ' '"
    })
    @DisplayName("Should show invalid credentials message for invalid login attempts")
    void shouldShowInvalidCredentialsMessage(String email, String password) {
        var authPage = new AuthenticationPageObject(driver);
        authPage.authenticate(email, password);
        assertThat(authPage.pageErrorMessage())
                .isEqualTo("Invalid credentials.");
    }


}