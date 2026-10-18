# VK Education · Android

Практические задания курса по Android-разработке. Kotlin, Jetpack Compose и работа с платформой — от Activity и Intent до интерфейсов приложений.

Каждое задание находится в своей папке и собирается в отдельное приложение. В корне лежат общие настройки Gradle и каталог версий библиотек.

## Задания

| № | Тема | Что реализовано | Папка / описание | Модуль |
| --- | --- | --- | --- | --- |
| 3 | Activity и Intent | Два экрана, передача текста, набор номера и отправка текста | [03-intents](assignments/03-intents/) | `homework-03` |
| 4 | Создание UI в Android. Часть 1 | Каталог приложений: тулбар, иконки, описания и категории на Compose | [04-compose-app-list](assignments/04-compose-app-list/) | `homework-04` |

В таблице перечислены задания, решения которых есть в репозитории. В каждой папке — исходники, тесты, инструкция запуска и чек-лист самопроверки.

## Структура

```text
assignments/
├── 03-intents/             # Задание 3 — Activity и Intent
│   ├── src/
│   ├── build.gradle.kts
│   └── README.md
└── 04-compose-app-list/    # Задание 4 — список приложений
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
3. Выполнить Gradle Sync. JVM для Gradle задана в `gradle/gradle-daemon-jvm.properties`: **полный JDK 25**, включая `jlink`. Gradle использует свой кеш JDK или скачивает его автоматически. Автопоиск Java в сторонних редакторах отключён, чтобы не выбрать урезанную среду без инструментов сборки.
4. Выбрать модуль `homework-03` или `homework-04` в конфигурации запуска Android App и нажать Run. Если сохранилась старая конфигурация `app`, заменить в ней модуль.

Нужен эмулятор или устройство с **Android 7.0 (API 24)** или новее. У заданий разные `applicationId`, поэтому оба приложения можно установить одновременно.

## Сборка и проверки

Команды выполняются из корня репозитория. В Windows используется `gradlew.bat`, в macOS/Linux — `./gradlew`.

```powershell
# Собрать оба приложения
.\gradlew.bat :homework-03:assembleDebug :homework-04:assembleDebug

# Проверить код и unit-тесты задания 3
.\gradlew.bat :homework-03:testDebugUnitTest :homework-03:lintDebug :homework-04:lintDebug

# UI-тесты — нужен запущенный эмулятор или подключённое устройство
.\gradlew.bat :homework-03:connectedDebugAndroidTest :homework-04:connectedDebugAndroidTest
```

APK и отчёты создаются в `build/` внутри папки соответствующего задания. Точные пути и отдельные команды запуска приведены в его README.

## Как добавить следующее задание

1. Создать папку `assignments/NN-topic` с Android-модулем и своим README.
2. Подключить модуль в `settings.gradle.kts` по образцу существующих заданий.
3. Указать отдельные `namespace` и `applicationId`, использовать общие версии из `gradle/libs.versions.toml`.
4. Добавить строку в таблицу заданий. Решение вести в отдельной ветке и сдавать через Pull Request в `main`.

## Материалы

- [Репозиторий с примерами курса](https://github.com/maltsevmicyou/VkEducation)
- [Jetpack Compose: списки](https://developer.android.com/develop/ui/compose/lists)
- [Jetpack Compose: тулбар](https://developer.android.com/develop/ui/compose/components/app-bars)
