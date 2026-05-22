package br.ifsp.demo.ui.objects;

import br.ifsp.demo.ui.base.BasePageObject;
import org.openqa.selenium.WebDriver;

public class RegistrationPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "frontend";

    public RegistrationPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle()))
            throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }
}