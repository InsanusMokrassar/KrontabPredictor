package dev.inmo.krontab.predictor.css

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto

object KrontabCommonStylesheet : StyleSheet() {
    val containerWidth = 600.px
    val darkThemeTextColor = Color.black
    val darkThemeBackgroundColor = Color("#ffffffcc")
    val container by style {
        margin(16.px, auto.unsafeCast<CSSNumeric>())
        width(KrontabCommonStylesheet.containerWidth)
        display(DisplayStyle.Grid)
        gridTemplateColumns("1fr")

        child(self, selector("*")) style {
            textAlign("center")
        }
    }
    val input by style {
        minHeight(36.px)
        fontSize(1.5.em)

        color(darkThemeTextColor)
        backgroundColor(darkThemeBackgroundColor)
    }
}