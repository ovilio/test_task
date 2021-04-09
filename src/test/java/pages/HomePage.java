package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage extends TopMenu<HomePage> {
    private final By productTile = By.cssSelector("app-tile");

    public HomePage(WebDriver webDriver) {
        driver = webDriver;
        PageFactory.initElements(driver, this);
    }

    public HomePage waitPageLoaded() {
        new WebDriverWait(driver, SELENIUM_TIMEOUT_10s)
                .until(ExpectedConditions.visibilityOfElementLocated(productTile));
        return this;
    }
}
