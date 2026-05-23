package br.ifsp.demo.ui.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class BaseSeleniumTest {
    protected WebDriver driver;
    @BeforeEach
    public void setUp() {
        // driver = new ChromeDriver();
        driver = new FirefoxDriver();
        setInitialPage();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    protected void setInitialPage(){}
}