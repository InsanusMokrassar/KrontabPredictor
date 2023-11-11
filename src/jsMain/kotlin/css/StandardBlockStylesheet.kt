package dev.inmo.krontab.predictor.css

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto

object StandardBlockStylesheet : StyleSheet() {
    val container by style {
        margin(16.px, auto.unsafeCast<CSSNumeric>())
        width(KrontabPartsStylesheet.containerWidth)
        textAlign("center")
    }
}