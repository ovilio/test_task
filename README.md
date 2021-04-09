# Java Automation QA test task 

### Notes
* Репорты создаются в папке ./test-output/${timestamp}, открывать через index.html 
* Скриншот делается в случае падения теста.
* Заменил целевую книгу "Java: ефективне програмування, 3-е видання - Д. Бліх. Діалектика рус (978-5-6041394-4-8)" на "Java: Эффективное программирование, 3-е издание - Джошуа Блох"
* На всякий случай прикладываю запись запуска, работы теста и просмотра репорта - [dropbox](https://www.dropbox.com/s/dtrv35ods2qauoo/java_AQA_Dodoka_test_task.webm?dl=0).

### Что нужно сделать:

* Автоматизировать на Java с использованием TestNG
* Добавить report, приемлемый для клиента
* Проект должен быть написан в стиле https://www.tutorialspoint.com/design_pattern/factory_pattern.htm с соблюдением принципов ООП
* Проект должен собираться Maven
* Код присылать опубликованный на GitHub
* Быть готовым объяснить всё, что сделано
 
Плюсом будет:
1. ~Сделать графический репорт с шагами и скриншотами.~
2. Использование PageObject

Тестовый сценарий:
1. Зайти на rozetka.com.ua
2. Нажать "Каталог товаров"
3. Навести на "Канцтовары и книги"
4. Выбрать "Книги"
5. На появившейся странице выбрать "Научная и техническая"
6. В фильтре слева с помощью слайдера выбрать цену примерно от 300 до 1500 грн
7. В фильтре слева выбрать издательство "Диалектика"
8. Отсортировать по "Популярные"
9. Убедиться что "Java: ефективне програмування, 3-е видання - Д. Бліх. Діалектика рус (978-5-
6041394-4-8)" присутствует в выборке
10. Нажать добавить в корзину для "Java: ефективне програмування, 3-е видання - Д. Бліх.
Діалектика рус (978-5-6041394-4-8)"
11. Убедиться, что появилось всплывающее окно "Корзина" с выбранной книгой
12. Нажать "Продолжить покупки"
13. Убедиться что на иконке корзины в правом верхнем углу 1 товар
14. Перейти в корзину
15. Удалить товар из корзины (Удалить без сохранения)
16. Убедиться, что корзина пуста
17. Закрыть модальное окно корзины

Если вдруг эта книга пропадёт из продажи, можно использовать любую другую.


