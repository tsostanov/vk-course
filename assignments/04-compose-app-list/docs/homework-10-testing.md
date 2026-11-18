# Задание 10 · Unit-тестирование

[← Каталог приложений](../README.md) · [Все задания](../../../README.md)

Цель — покрыть Repository, мапперы DTO ↔ Domain и Use Case минимум пятью unit-тестами на каждый пункт. Тесты находятся в `src/test/java/com/example/vkcourse/applist/` модуля `homework-04`.

## Что проверяется

| Пункт | Тестовый класс | Количество | Сценарии |
| --- | --- | ---: | --- |
| Repository | [AppRepositoryUnitTest](../src/test/java/com/example/vkcourse/applist/data/repository/AppRepositoryUnitTest.kt) | 8 | Поля и порядок каталога; пустой результат; свежие данные и количество обращений; id и все поля подробностей; ошибки и отмена обоих запросов |
| Мапперы | [CatalogAppMapperTest](../src/test/java/com/example/vkcourse/applist/data/mapper/CatalogAppMapperTest.kt) | 6 | Все поля в обоих направлениях; пустые строки; Unicode, пробелы и URL; преобразование туда и обратно с сохранением данных |
| Мапперы | [AppDetailsMapperTest](../src/test/java/com/example/vkcourse/applist/data/mapper/AppDetailsMapperTest.kt) | 7 | Все поля в обоих направлениях, включая `screenshotUrlList` ↔ `screenshotUrls`; пустые значения; порядок и повторы скриншотов; точность чисел при преобразовании туда и обратно |
| Use Case | [GetCatalogAppsUseCaseTest](../src/test/java/com/example/vkcourse/applist/domain/usecase/GetCatalogAppsUseCaseTest.kt) | 6 | Сохранение полей и порядка; пустой каталог; ровно один вызов репозитория; ошибка без повторного запроса; отмена; свежие данные при следующем вызове |
| Use Case | [LoadAppRatingsUseCaseTest](../src/test/java/com/example/vkcourse/applist/domain/usecase/LoadAppRatingsUseCaseTest.kt) | 8 | Выбор последовательного или параллельного метода; пустые id; порядок и повторы id; ошибки без переключения режима; отмена; повторный вызов с другими id и режимом |

Всего в этой группе **35 тестов**: Repository — **8**, мапперы — **13**, Use Case — **14**. Даже каждый маппер и каждый Use Case отдельно покрыт минимум пятью тестами.

В `AppRepositoryUnitTest` интерфейс `AppCatalogDataSource` заменён подставной реализацией с управляемыми результатами. Use Case получают `StubAppRepository`, который записывает вызовы и возвращает заданные значения или исключения. Неожиданный вызов неподготовленного метода завершает тест ошибкой. Ожидаемые модели в тестах репозитория заданы явно, без использования проверяемых мапперов для расчёта результата.

Мапперы проверяются напрямую на чистых Kotlin-моделях. Для suspend-функций используется `runTest` из `kotlinx-coroutines-test`, для утверждений — JUnit 4. Проверки исключений убеждаются, что исходная ошибка или отмена дошла до вызывающего кода. Новые зависимости не нужны.

В мапперы [CatalogAppMapper](../src/main/java/com/example/vkcourse/applist/data/mapper/CatalogAppMapper.kt) и [AppDetailsMapper](../src/main/java/com/example/vkcourse/applist/data/mapper/AppDetailsMapper.kt) добавлен `toDto`. Проверки обоих направлений сверяют все поля с явно заданными моделями; дополнительные проверки преобразования туда и обратно подтверждают отсутствие потери данных.

## Как запустить

Команды выполняются из корня репозитория. В Windows используется `gradlew.bat`, в macOS/Linux — `./gradlew`. Нужны Android SDK и JDK из настроек проекта, эмулятор не требуется.

Только 35 тестов из таблицы:

```powershell
.\gradlew.bat :homework-04:testDebugUnitTest --tests '*AppRepositoryUnitTest' --tests '*data.mapper.*Test' --tests '*domain.usecase.*Test'
```

Полный набор unit-тестов каталога, включая прежние проверки ViewModel, корутин и Retrofit:

```powershell
.\gradlew.bat :homework-04:testDebugUnitTest
```

Полный набор также запускает прежний замер с настоящими задержками, который занимает около 20 секунд. Retrofit проверяется локальным `MockWebServer`; обращения к учебному серверу для unit-тестов не нужны.

HTML-отчёт: `assignments/04-compose-app-list/build/reports/tests/testDebugUnitTest/index.html`. XML-отчёты по классам: `assignments/04-compose-app-list/build/test-results/testDebugUnitTest/`.

Проверка 18 ноября 2026 года: команда `:homework-04:testDebugUnitTest` завершилась с `BUILD SUCCESSFUL`. В полном наборе **56 тестов, 0 ошибок, 0 падений, 0 пропусков**; все 35 тестов из таблицы прошли.

## Самопроверка

- [x] Repository: минимум 5 изолированных тестов — есть 8.
- [x] Мапперы: минимум 5 тестов — есть 13, проверены оба направления.
- [x] Use Case: минимум 5 тестов — есть 14, покрыты оба Use Case.
- [x] Проверены успешные результаты, пустые данные и ошибки; отмена корутин проверена отдельно от ошибок.
- [x] Тесты задания 10 не используют Android UI, настоящий сервер и реальные задержки.
- [x] Запустить полный набор unit-тестов и убедиться, что нет ошибок и пропусков — все 56 прошли.
- [x] Опубликовать изменения в GitHub в отдельной ветке `homework-10-tests`.

Для сдачи: [ветка с решением задания 10](https://github.com/tsostanov/vk-course/tree/homework-10-tests).
