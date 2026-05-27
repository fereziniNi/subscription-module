package br.ifsp.demo.ui.test;

import br.ifsp.demo.ui.base.BaseSeleniumTest;
import br.ifsp.demo.ui.objects.AuthenticationPageObject;
import br.ifsp.demo.ui.objects.HomePageObject;

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


}