package br.ifsp.demo.ui.test;

import br.ifsp.demo.ui.base.BasePageObject;

public class AuthenticationPageObjectTest extends BaseSeleniumTest {
    private static final String URL = "https://subscription-module-seven.vercel.app/login";

    @Override
    public void setInitialPage() {
        driver.get(URL);
    }


}