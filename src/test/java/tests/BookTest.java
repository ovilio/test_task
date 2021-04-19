package tests;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.BasePage.Category;
import pages.BasePage.Filter.Sorting;
import pages.BooksPage;
import pages.HomePage;
import pages.TopMenu;
import utils.Urls;

import static pages.BooksPage.BookCategory.SCIENTIFIC_AND_TECHNICAL;

@SuppressWarnings("FieldCanBeLocal")
public class BookTest extends BaseTest {
    private final String targetBookTitle = "Java: Эффективное программирование, 3-е издание - Джошуа Блох"; //https://rozetka.com.ua/search/?text=978-5-+6041394-4-8
    private final String targetBookPubHouse = "Диалектика";
    SoftAssert sa = new SoftAssert();

    /**
     * Implementation of the test scenario from ./README.MD
     */
    @Test
    public void bookTest() {
        driver.get(Urls.baseUrl); //1. Зайти на rozetka.com.ua
        HomePage homePage = new HomePage(driver)
                .waitPageLoaded();
        homePage.clickCatalogButton() //2. Нажать "Каталог товаров"
                .waitCatalogAppeared()
                .hoverCategory(Category.STATIONERY_AND_BOOKS) //3. Навести на "Канцтовары и книги"
                .clickBooks(); //4. Выбрать "Книги"

        BooksPage booksPage = new BooksPage(driver)
                .waitPageLoaded();
        booksPage.clickBookCategory(SCIENTIFIC_AND_TECHNICAL) //5. На появившейся странице выбрать "Научная и техническая"
                .waitPageLoaded(SCIENTIFIC_AND_TECHNICAL.getTitle());

        booksPage.filter.movePriceMinSliderTo(300) //6. В фильтре слева с помощью слайдера выбрать цену примерно от 300 до 1500 грн
                .movePriceMaxSliderTo(1500)
                .clickPriceOkButton()
                .checkPubHouse(targetBookPubHouse) //7. В фильтре слева выбрать издательство "Диалектика"
                .waitProductsLoaded()
                .setSortingValue(Sorting.POPULARITY) //8. Отсортировать по "Популярные"
                .waitProductsLoaded();

        sa.assertTrue(booksPage.isProductPresent(targetBookTitle)); //9. Убедиться что "Java: Эффективное программирование, 3-е издание - Джошуа Блох"  присутствует в выборке

        booksPage.clickProductBuyButton(targetBookTitle) //10. Нажать добавить в корзину для "Java: Эффективное программирование, 3-е издание - Джошуа Блох"
                .waitProductSelected(targetBookTitle)
                .clickProductBuyButton(targetBookTitle); //double click here for modal window appearing

        booksPage.cartModal = new TopMenu.CartModal(driver);
        booksPage.cartModal.waitForModalAppearing();
        sa.assertTrue(booksPage.cartModal.isModalShown()); //11. Убедиться, что появилось всплывающее окно "Корзина" с выбранной книгой
        sa.assertTrue(booksPage.cartModal.getCartProductTitlesSet().contains(targetBookTitle)); //11.2 убедиться, что в корзине есть нужная книга
        booksPage.cartModal.clickContinueButton() //12. Нажать "Продолжить покупки"
                .waitForModalDisappearing();

        sa.assertEquals(booksPage.getCartButtonCounterValue(), 1); //13. Убедиться что на иконке корзины в правом верхнем углу 1 товар

        booksPage.clickCartButton() //14. Перейти в корзину
                .waitForModalAppearing()
                .clickActionButtonByProductTitle(targetBookTitle) //15. Удалить товар из корзины (Удалить без сохранения)
                .waitActionMenuOpened()
                .clickDeleteButtonOnActionMenu()
                .waitLoaderDisappear();
        sa.assertTrue(booksPage.cartModal.isCartEmpty()); //16. Убедиться, что корзина пуста
        booksPage.cartModal.clickCrossButton(); //17. Закрыть модальное окно корзины

        sa.assertAll();
    }
}
