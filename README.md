# VK Education · Android

Мои практические задания по Android-разработке на Kotlin. Здесь собраны приложения, которые дополняются по ходу курса: от работы с Activity и Intent до Compose, архитектуры и внедрения зависимостей.

В репозитории два приложения: задание 3 и каталог приложений для заданий 4–8. У них общие настройки Gradle, но каждый модуль можно собрать и запустить отдельно.

В **задании 8** каталог загружается с учебного сервера через Retrofit. Добавлены сетевые иконки, сообщение об ошибке и повтор загрузки. Отдельный запрос подробностей по id подготовлен в репозитории; экран пока использует данные каталога.

## Задания

| № | Тема | Что реализовано | Папка / описание | Модуль |
| --- | --- | --- | --- | --- |
| 3 | Activity и Intent | Два экрана, передача текста, набор номера и отправка текста | [03-intents](assignments/03-intents/) | `homework-03` |
| 4.1 | Создание UI в Android. Часть 1 | Каталог приложений на Compose: тулбар, иконки, описания и категории | [04-compose-app-list](assignments/04-compose-app-list/) | `homework-04` |
| 4.2 | Создание UI в Android. Часть 2 | Карточка приложения с витриной из 9 приложений, постраничным Pager и навигацией на Compose | [04-compose-app-list](assignments/04-compose-app-list/) | `homework-04` |
| 5 | Presentation pattern | MVVM на `ViewModel` и `StateFlow`; snackbar по нажатию на тулбар без повторного показа после пересоздания экрана | [04-compose-app-list](assignments/04-compose-app-list/) | `homework-04` |
| 6 | Слоистая архитектура | Разделение на data, domain и presentation; получение данных через репозиторий и Use Case | [04-compose-app-list](assignments/04-compose-app-list/) | `homework-04` |
| 7 | Внедрение зависимостей | Hilt создаёт зависимости и передаёт Use Case в ViewModel | [04-compose-app-list](assignments/04-compose-app-list/#задание-7-внедрение-зависимостей) | `homework-04` |
| 8 | Работа с сетью | Retrofit: загрузка каталога с сервера и отдельный запрос подробностей по id | [04-compose-app-list](assignments/04-compose-app-list/#задание-8-загрузка-с-сервера) | `homework-04` |

Подробности реализации, скриншоты и команды проверки — в README каждого приложения.

## Структура

```text
assignments/
├── 03-intents/             # Задание 3 — Activity и Intent
│   ├── src/
│   ├── build.gradle.kts
│   └── README.md
└── 04-compose-app-list/    # Задания 4–8 — каталог, архитектура, DI и сеть
    ├── src/
    ├── docs/              # Скриншоты интерфейса
    ├── build.gradle.kts
    └── README.md
gradle/                    # Wrapper, JVM и версии зависимостей
build.gradle.kts           # Общие плагины
settings.gradle.kts        # Подключение модулей заданий
```

## Запуск в Android Studio

1. Клонировать репозиторий и открыть **корневую папку репозитория**, в которой находится `settings.gradle.kts`.
2. Установить Android SDK Platform 37. Путь к SDK Android Studio сохранит в локальном `local.properties`.
3. Выполнить Gradle Sync. Для сборки используется **полный JDK 25**, указанный в `gradle/gradle-daemon-jvm.properties`. Gradle берёт его из кеша или скачивает автоматически.
4. Выбрать модуль `homework-03` или `homework-04` в конфигурации запуска и нажать Run.

Для запуска подойдёт эмулятор или устройство с **Android 7.0 (API 24)** или новее. Оба приложения можно установить одновременно.

## Сборка и проверки

Команды выполняются из корня репозитория. В Windows используется `gradlew.bat`, в macOS/Linux — `./gradlew`.

```powershell
# Собрать оба приложения
.\gradlew.bat :homework-03:assembleDebug :homework-04:assembleDebug

# Проверить unit-тесты и lint обоих приложений
.\gradlew.bat :homework-03:testDebugUnitTest :homework-04:testDebugUnitTest :homework-03:lintDebug :homework-04:lintDebug

# UI-тесты — нужен запущенный эмулятор или подключённое устройство
.\gradlew.bat :homework-03:connectedDebugAndroidTest :homework-04:connectedDebugAndroidTest
```

APK и отчёты создаются в `build/` внутри папки соответствующего задания. Точные пути и отдельные команды запуска приведены в его README.

## Материалы

- [Репозиторий с примерами курса](https://github.com/maltsevmicyou/VkEducation)
- [Jetpack Compose: списки](https://developer.android.com/develop/ui/compose/lists)
- [Jetpack Compose: тулбар](https://developer.android.com/develop/ui/compose/components/app-bars)
