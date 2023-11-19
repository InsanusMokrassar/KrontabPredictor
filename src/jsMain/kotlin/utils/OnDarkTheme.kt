package dev.inmo.krontab.predictor.utils

import org.jetbrains.compose.web.css.GenericStyleSheetBuilder
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.media

fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.onDarkTheme(rulesBuild: GenericStyleSheetBuilder<TBuilder>.() -> Unit) {
    media("prefers-color-scheme", StylePropertyValue("dark"), rulesBuild)
}
