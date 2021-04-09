package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashSet;

/**
 * The {@code TopMenu} abstract class provide functionality for interactions with top menu.
 * Should be extended by PO classes which implements pages with top menu.
 */
@SuppressWarnings({"unchecked", "rawtypes", "UnusedReturnValue"})
public abstract class TopMenu<T extends BasePage> extends BasePage<TopMenu> {
    public CartModal cartModal;

    private final By catalogButton = By.id("fat-menu");
    private final By catalogListOnDropDown = By.className("menu-wrapper_state_animated");
    private final By categoryMenuList = By.className("menu-categories");
    private final By booksTitle = By.xpath("//*[text()='Книги']");
    private final By cartButton = By.cssSelector("li.header-actions__item--cart button");
    private final By cartButtonCounter = By.cssSelector("span.counter");

    public T clickCatalogButton() {
        driver.findElement(catalogButton).click();
        return (T) this;
    }

    public T waitCatalogAppeared() {
        new WebDriverWait(driver, SELENIUM_TIMEOUT_1s)
                .until(ExpectedConditions.visibilityOfElementLocated(catalogListOnDropDown));
        return (T) this;
    }

    public T hoverCategory(Category cat) {
        WebElement catTitleElem = driver.findElement(categoryMenuList).findElement(By.xpath("//*[text()='" + cat.getTitle() + "']"));
        super.hover(catTitleElem);
        new WebDriverWait(driver, SELENIUM_TIMEOUT_1s)
                .until(ExpectedConditions.attributeContains(catTitleElem, "class", "menu-categories__link_state_hovered"));
        return (T) this;
    }

    public T clickBooks() {
        driver.findElement(catalogListOnDropDown).findElement(booksTitle).click();
        return (T) this;
    }

    public CartModal clickCartButton() {
        driver.findElement(cartButton).click();
        return this.cartModal;
    }

    public int getCartButtonCounterValue() {
        return Integer.parseInt(driver.findElement(cartButtonCounter).getAttribute("innerText"));
    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class CartModal extends ModalWindow<CartModal> {
        private final By continueShoppingButton = By.className("cart-footer__continue");
        private final By productsTitle = By.cssSelector("li.cart-list__item a.cart-product__title");
        private final String productActionButton = "//li[@class='cart-list__item']//a[contains(@title,'%s')]/../..//button[contains(@class,'context-menu__toggle')]";
        private final By actionMenu = By.className("context-menu__list");
        private final By deleteButton = By.xpath("//button[contains(.,'Удалить')]");
        private final By loader = By.className("preloader_with_donut");
        private final By cartDummyImg = By.className("cart-dummy__illustration");
        private final By cartDummyText = By.className("cart-dummy__heading");

        public CartModal(WebDriver webDriver) {
            driver = webDriver;
            PageFactory.initElements(driver, this);
        }

        @Override
        public boolean isModalShown() {
            return super.isModalShown() &&
                    driver.findElement(By.xpath(String.format(modalHeader, "Корзина"))).isDisplayed();
        }

        public CartModal clickContinueButton() {
            driver.findElement(continueShoppingButton).click();
            return this;
        }

        public HashSet<String> getCartProductTitlesSet() {
            HashSet<String> result = new HashSet();
            driver.findElements(productsTitle)
                    .forEach(elem -> result.add(elem.getText().trim()));
            return result;
        }

        public CartModal clickActionButtonByProductTitle(String productTitle) {
            driver.findElement(By.xpath(String.format(productActionButton, productTitle))).click();
            return this;
        }

        public CartModal clickDeleteButtonOnActionMenu() {
            driver.findElement(actionMenu).findElement(deleteButton).click();
            return this;
        }

        public CartModal waitActionMenuOpened() {
            new WebDriverWait(driver, SELENIUM_TIMEOUT_1s)
                    .until(ExpectedConditions.visibilityOfElementLocated(actionMenu));
            return this;
        }

        public CartModal waitLoaderDisappear() {
            new WebDriverWait(driver, SELENIUM_TIMEOUT_10s)
                    .until(ExpectedConditions.invisibilityOfElementLocated(loader));
            return this;
        }

        public boolean isCartEmpty() {
            return !driver.findElements(cartDummyImg).isEmpty()
                    && !driver.findElements(cartDummyText).isEmpty();
        }
    }
}
