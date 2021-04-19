package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.lang.String.format;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

/**
 * Base abstract class for all PO classes inheritance.
 * Contains general methods for all pages in project.
 */
@SuppressWarnings({"unchecked", "UnusedReturnValue", "rawtypes"})
public abstract class BasePage<T extends BasePage> {
    protected WebDriver driver;
    public static final int SELENIUM_TIMEOUT_1s = 1000;
    public static final int SELENIUM_TIMEOUT_4s = 4000;
    public static final int SELENIUM_TIMEOUT_10s = 10000;
    public static final int SELENIUM_TIMEOUT_30s = 30000;
    public static final int SELENIUM_TIMEOUT_60s = 60000;

    protected final String pageTitle = "//h1[@class='catalog-heading' and contains(.,'%s')]";
    private final By productGridLoading = By.className("catalog-grid preloader_type_element preloader_type_goods");
    protected String productBuyButton = "//app-goods-tile-default//*[contains(text(),'%s')]/../../div[@class='goods-tile__prices']//button";

    public T hover(WebElement elem) {
        Actions action = new Actions(driver);
        action.moveToElement(elem).build().perform();
        return (T) this;
    }


    public T scrollToElem(WebElement elem) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", elem);
        return (T) this;
    }

    public T waitPageLoaded(String titleText) {
        new WebDriverWait(driver, SELENIUM_TIMEOUT_4s)
                .until(visibilityOfElementLocated(By.xpath(format(pageTitle, titleText))));
        return (T) this;
    }

    public T waitProductsLoaded() {
        new WebDriverWait(driver, SELENIUM_TIMEOUT_10s)
                .until(ExpectedConditions.invisibilityOfElementLocated(productGridLoading));
        return (T) this;
    }

    /**
     * The {@code ModalWindow} class represents abstract modal window.
     */
    public static abstract class ModalWindow<T extends ModalWindow> extends BasePage<ModalWindow> {
        protected final String modalHeader = "//h3[@class='modal__heading' and contains(.,'%s')]";
        protected final By modal = By.cssSelector("single-modal-window");
        private final By crossButton = By.className("modal__close");

        public boolean isModalShown() {
            return driver.findElement(modal).isDisplayed();
        }

        public T waitForModalAppearing() {
            new WebDriverWait(driver, SELENIUM_TIMEOUT_4s)
                    .until(visibilityOfElementLocated(modal));
            return (T) this;
        }

        public T waitForModalDisappearing() {
            new WebDriverWait(driver, SELENIUM_TIMEOUT_4s)
                    .until(ExpectedConditions.invisibilityOfElementLocated(modal));
            return (T) this;
        }

        public T clickCrossButton() {
            driver.findElement(crossButton).click();
            return (T) this;
        }
    }

    /**
     * The {@code Filter} class represents abstract filter on side panel.
     * Contains some methods for common elements and should be inherited for implementing specific filters.
     */
    public static abstract class Filter<T extends Filter> extends BasePage<Filter> {
        private final By priceMinSliderButton = By.className("rz-slider__range-button_type_left");
        private final By priceMaxSliderButton = By.className("rz-slider__range-button_type_right");
        private final By priceMinInput = By.cssSelector("input[formcontrolname='min']");
        private final By priceMaxInput = By.cssSelector("input[formcontrolname='max']");
        private final By priceOkButton = By.cssSelector("button[type='submit']");
        private final By sortingSelect = By.cssSelector("rz-sort select");

        public T clickPriceOkButton() {
            driver.findElement(priceOkButton).click();
            return (T) this;
        }

        public T setPriceMinValue(String minPrice) {
            driver.findElement(priceMinInput).sendKeys(minPrice);
            return (T) this;
        }

        public int getPriceMinValue() {
            return Integer.parseInt(driver.findElement(priceMinInput).getAttribute("value"));
        }

        public T setPriceMaxValue(String maxPrice) {
            driver.findElement(priceMaxInput).sendKeys(maxPrice);
            return (T) this;
        }

        public int getPriceMaxValue() {
            return Integer.parseInt(driver.findElement(priceMaxInput).getAttribute("value"));
        }

        public T movePriceMinSliderTo(int minPrice) {
            WebElement slider = driver.findElement(priceMinSliderButton);
            Actions move = new Actions(driver);
            int currentPrice = getPriceMinValue();
            while (currentPrice < minPrice) {
                move.dragAndDropBy(slider, 1, 0).build().perform();
                currentPrice = getPriceMinValue();
            }
            return (T) this;
        }

        public T movePriceMaxSliderTo(int maxPrice) {
            WebElement slider = driver.findElement(priceMaxSliderButton);
            Actions move = new Actions(driver);
            Action action;
            int currentPrice = getPriceMaxValue();
            while (currentPrice > maxPrice) {
                action = move.dragAndDropBy(slider, -1, 0).build();
                action.perform();
                currentPrice = getPriceMaxValue();
            }
            return (T) this;
        }

        public Sorting getSortingSelectValue() {
            Select select = new Select(driver.findElement(sortingSelect));
            return Sorting.fromString(select.getAllSelectedOptions().get(0).getText());
        }

        public T setSortingValue(Sorting sorting) {
            Select select = new Select(driver.findElement(sortingSelect));
            select.selectByValue(sorting.getValue());
//            driver.findElements()
            return (T) this;
        }

        /**
         * The {@code Sorting} enum represents types of product sorting for the sorting dropdown.
         */
        public enum Sorting {
            CHEAP("От дешевых к дорогим", "1: cheap"),
            EXPENSIVE("От дорогих к дешевым", "2: expensive"),
            POPULARITY("Популярные", "3: popularity"),
            NOVELTY("Новинки", "4: novelty"),
            ACTION("Акционные", "5: action"),
            RANK("По рейтингу", "6: rank");

            private String title;
            private String value;

            Sorting(String title, String value) {
                this.title = title;
                this.value = value;
            }

            public String getTitle() {
                return this.title;
            }

            public String getValue() {
                return this.value;
            }

            public static Sorting fromString(String sortingTitle) {
                for (Sorting sorting : Sorting.values()) {
                    if (sorting.getTitle().equals(sortingTitle.trim())) return sorting;
                }
                throw new IllegalArgumentException("Unknown sorting type");
            }
        }
    }

    /**
     * The {@code Category} enum represents categories of products for clear data pointing.
     */
    public enum Category {
        NOTEBOOKS_AND_COMPUTERS("Ноутбуки и компьютеры"),
        STATIONERY_AND_BOOKS("Канцтовары и книги"),
        SERVICES("Услуги и сервисы");

        private String title;

        Category(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }
}

