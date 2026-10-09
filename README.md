# VKCourse — практическое задание №3

Две настоящие Activity с интерфейсом на Kotlin и Jetpack Compose демонстрируют явный Intent, неявный Intent и системную отправку текста. `MainActivity` остаётся стартовой, `SecondActivity` доступна только внутри приложения.

## Запуск

1. Откройте проект в Android Studio и выполните Gradle Sync. Нужны Android SDK Platform 37 и устройство/эмулятор с Android 7.0 (API 24) или новее.
2. Используйте JVM, соответствующую `gradle/gradle-daemon-jvm.properties` (Java 25; Android Studio/Gradle могут загрузить её автоматически). Укажите установленный SDK в локальном `local.properties`.
3. Выберите модуль `app`, устройство и нажмите Run. Либо в PowerShell выполните:

   ```powershell
   .\gradlew.bat :app:assembleDebug
   & "$env:LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe" install -r app/build/outputs/apk/debug/app-debug.apk
   & "$env:LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe" shell am start -n com.example.vkcourse/.MainActivity
   ```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

Проект сохраняет AGP 9.3.3, Gradle 9.5.0 и текущие настройки SDK. Kotlin 2.2.10 уже встроен в AGP; отдельно подключён `org.jetbrains.kotlin.plugin.compose` 2.2.10, включён `buildFeatures.compose`, зависимости Compose согласованы через BOM 2025.08.00, интеграция с Activity — `activity-compose` 1.10.1. Подключение соответствует [встроенному Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin) и [Compose Compiler plugin](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler).

Для проверки на Android 17 используются Espresso 3.7.0 и AndroidX Test JUnit 1.3.0. Исходный Espresso 3.5.1 несовместим с системным API ввода этого эмулятора; обновление тестовых библиотек исправляет обращение к удалённому методу `InputManager.getInstance` ([release notes](https://developer.android.com/jetpack/androidx/releases/test)).

## Ручная проверка

| Действие | Ожидаемый результат |
| --- | --- |
| Ввести `Привет, Android!` → «Открыть вторую Activity» | Открывается отдельная `SecondActivity`, отображается точный текст из extras. |
| Вернуться системной кнопкой/жестом «Назад» или кнопкой на втором экране | Возвращается первый экран, ввод сохранён. |
| Ввести `+7 (999) 123-45-67` → «Позвонить другу» | Открывается приложение набора номера с `+79991234567`. Звонок сам не начинается. Не нажимайте кнопку звонка при проверке. |
| Повторить с `8 999 123 45 67` и `(999)123-45-67` | Форматирование удаляется, номер подставляется в набор номера. |
| Ввести текст → «Поделиться текстом» | Открывается системный chooser «Поделиться через…» с доступными приложениями; передаются `ACTION_SEND`, `EXTRA_TEXT` и `text/plain`. Заголовок и оформление могут зависеть от Android. Отменить отправку можно через «Назад». |
| Оставить поле пустым, затем ввести только пробелы; проверить все три кнопки | Ошибка в интерфейсе, внешнее приложение/вторая Activity не открываются. |
| Ввести `abc`, `++79991234567`, `(9991234567`, `123#`, `12` → набор номера | Сообщение о некорректном номере, Intent не отправляется. |
| Повернуть устройство после ввода и ошибки | Ввод и сообщение сохраняются, содержимое доступно с прокруткой и открытой клавиатурой. |

Для номера принимаются 3–15 цифр (включая короткие номера), необязательный плюс только в начале, пробельные символы, парные ненестированные скобки с цифрами и дефисы. Это проверка формата, а не подтверждение существования номера. USSD, URI, добавочные номера и буквы отклоняются. Текст для второго экрана и отправки сохраняется без обрезания; пустые и пробельные строки отклоняются.

Отсутствие приложения набора номера обрабатывается через `ActivityNotFoundException`, а наличие обработчика `ACTION_SEND` проверяется перед chooser. Для этой проверки в манифесте объявлен `<queries>`; разрешение на звонки не требуется. `SecurityException` также превращается в сообщение. Отсутствующий/пустой extra во второй Activity показывает ошибку и кнопку возврата. Ветка отсутствующего обработчика воспроизводится в инструментальных тестах подстановкой запуска Intent, без отключения системных приложений. Запуск второй Activity без extras проверяется тестом `SecondActivityTest` (через ADB она намеренно недоступна, поскольку `exported=false`).

## Автоматические проверки

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
# Требуется запущенный эмулятор или подключённое устройство:
.\gradlew.bat :app:connectedDebugAndroidTest
```

Тесты проверяют текст и номера, ошибки ввода, реальный переход между Activity и возврат, пересоздание Activity, контракт dial Intent и chooser, отсутствие extras и ошибки запуска. В тестах контрактов dial/chooser Intent захватывается через подстановку функции запуска после нажатия кнопки Compose. Проверка открытия настоящих системных приложений выполняется отдельно. Espresso Intents записывает реальные переходы внутри приложения; перехват внешних Activity на Android 17 приводит к потере фокуса тестового окна и здесь не используется.

Отчёты: `app/build/reports/tests/testDebugUnitTest/index.html`, `app/build/reports/androidTests/connected/debug/index.html`, `app/build/reports/lint-results-debug.html`.

Фактические результаты проверки этой версии перечислены в описании Pull Request.
