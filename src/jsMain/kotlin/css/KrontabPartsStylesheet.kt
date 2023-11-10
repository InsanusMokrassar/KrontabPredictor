package dev.inmo.krontab.predictor.css

import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.width

object KrontabPartsStylesheet : StyleSheet() {
    val containerSize = 600.px
    val container by style {
        display(DisplayStyle.Grid)
        margin(0.px, auto.unsafeCast<CSSNumeric>())
        width(containerSize)
        gridTemplateColumns("1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr")
        gap(16.px)

        child(self, selector("*")) style {
            textAlign("center")
            minWidth(0.px)
        }
    }
}