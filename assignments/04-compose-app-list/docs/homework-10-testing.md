# Задание 10 · Unit-тесты

[← Каталог приложений](../README.md) · [Все задания](../../../README.md)

В этом задании покрыл тестами репозиторий, мапперы и Use Case каталога приложений. Для тестов использовал JUnit 4, а для suspend-функций — `runTest` из `kotlinx-coroutines-test`.

## Что проверяю

В [AppRepositoryUnitTest](../src/test/java/com/example/vkcourse/applist/data/repository/AppRepositoryUnitTest.kt) проверяю получение каталога и подробностей приложения: поля, порядок элементов, пустой ответ, передачу id, ошибки и отмену запросов. Также проверяю, что при повторном обращении приходят свежие данные. Вместо сетевого источника подставляю свою реализацию `AppCatalogDataSource`. Всего здесь 8 тестов.

Для мапперов добавил проверки в обе стороны: DTO → Domain и Domain → DTO. Для обратного преобразования написал `toDto`. Проверяю все поля, пустые строки, списки скриншотов и сохранение данных после преобразования туда и обратно. В [CatalogAppMapperTest](../src/test/java/com/example/vkcourse/applist/data/mapper/CatalogAppMapperTest.kt) получилось 6 тестов, в [AppDetailsMapperTest](../src/test/java/com/example/vkcourse/applist/data/mapper/AppDetailsMapperTest.kt) — 7.

В [GetCatalogAppsUseCaseTest](../src/test/java/com/example/vkcourse/applist/domain/usecase/GetCatalogAppsUseCaseTest.kt) проверяю получение списка, пустой результат, обращения к репозиторию, ошибки и отмену. В [LoadAppRatingsUseCaseTest](../src/test/java/com/example/vkcourse/applist/domain/usecase/LoadAppRatingsUseCaseTest.kt) — выбор последовательной или параллельной загрузки, передачу id и обработку ошибок. Репозиторий в этих тестах подменяю через `StubAppRepository`. Для двух Use Case получилось 6 и 8 тестов.

## Запуск

Из корня проекта:

```powershell
.\gradlew.bat :homework-04:testDebugUnitTest
```

Эмулятор и подключение к учебному серверу не нужны. Полный запуск включает замер загрузки рейтингов из задания 9 с настоящими задержками, поэтому он занимает ещё около 20 секунд.

Чтобы запустить только тесты репозитория, мапперов и Use Case из этого задания:

```powershell
.\gradlew.bat :homework-04:testDebugUnitTest --tests '*AppRepositoryUnitTest' --tests '*data.mapper.*Test' --tests '*domain.usecase.*Test'
```

При проверке 18 ноября 2026 года прошли все 56 unit-тестов каталога, включая 35 тестов по трём пунктам этого задания.

Отчёт: `assignments/04-compose-app-list/build/reports/tests/testDebugUnitTest/index.html`.
