package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.lang.String.format;

@SuppressWarnings({"UnusedReturnValue", "FieldCanBeLocal"})
public class BooksPage extends TopMenu<BooksPage> {
    public BooksFilter filter;
    private final String bookCategoryButton = "//li[@class='portal-navigation__item']//a[contains(.,'%s')]";
    private String productTitle = "//app-goods-tile-default//*[contains(text(),'%s')]/..";

    public BooksPage(WebDriver webDriver) {
        driver = webDriver;
        PageFactory.initElements(driver, this);
        filter = new BooksFilter(driver);
    }

    public BooksPage waitPageLoaded() {
        super.waitPageLoaded("Книги");
        return this;
    }

    public BooksPage clickBookCategory(BookCategory cat) {
        driver.findElement(By.xpath(format(bookCategoryButton, cat.getTitle()))).click();
        return this;
    }

    public boolean isProductPresent(String productTitle) {
        try {
            return driver.findElement(By.xpath(format(this.productTitle, productTitle))).isDisplayed();
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    public BooksPage clickProductBuyButton(String productTitle) {
        scrollToElem(driver.findElement(By.xpath(format(this.productTitle, productTitle))));
        WebElement cartButton = driver.findElement(By.xpath(format(productBuyButton, productTitle)));
        cartButton.click();
        return this;
    }

    public BooksPage waitProductSelected(String productTitle) {
        new WebDriverWait(driver, SELENIUM_TIMEOUT_4s)
                .until(ExpectedConditions.attributeContains(driver.findElement(By.xpath(format(productBuyButton, productTitle))), "class", "buy-button_state_in-cart"));
        return this;
    }

    public static class BooksFilter extends Filter<BooksFilter> {
        private final String pubHouseDiv = "//div[@data-filter-name='izdatelstvo-73719']";
        private final String pubHouseItem = pubHouseDiv + "//input[@id='%s']//../..";

        public BooksFilter(WebDriver webDriver) {
            driver = webDriver;
            PageFactory.initElements(driver, this);
        }

        public BooksFilter checkPubHouse(String pubHouseName) {
            hover(driver.findElement(By.xpath(pubHouseDiv)));
            driver.findElement(By.xpath(format(pubHouseItem, pubHouseName))).click();
            return this;
        }
    }

    public enum BookCategory {
        FICTION("Художественная литература"),
        SCIENTIFIC_AND_TECHNICAL("Научная и техническая литература"),
        COMICS("Комиксы");

        private String title;

        BookCategory(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }
}
