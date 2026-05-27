package br.ifsp.demo.ui.test;

import br.ifsp.demo.ui.base.BaseSeleniumTest;

import static org.junit.jupiter.api.Assertions.*;

class HomePageObjectTest extends BaseSeleniumTest {
    private static final String LOGIN_URL = "https://subscription-module-seven.vercel.app/login";

    @Override
    public void setInitialPage() {
        driver.get(LOGIN_URL);
    }

}