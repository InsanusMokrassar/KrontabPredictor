package dev.inmo.krontab.predictor.css

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto

object KrontabDateTimeGridsStylesheet : StyleSheet() {
    val container by style {
        display(DisplayStyle.Grid)
        margin(16.px, auto.unsafeCast<CSSNumeric>())
        gridTemplateColumns("1fr 1fr")
        width(KrontabPartsStylesheet.containerWidth)

        child(self, selector("*")) style {
            textAlign("center")
            minWidth(0.px)
        }
    }
    val dateTime by style {
        minHeight(36.px)
        fontSize(1.5.em)
    }
}