// Общие плагины курса. Каждое задание подключает их в своём модуле.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
}
