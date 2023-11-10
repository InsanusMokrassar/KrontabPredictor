package dev.inmo.krontab.predictor.css

import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.width

object KrontabCommonStylesheet : StyleSheet() {
    val container by style {
        margin(16.px, auto.unsafeCast<CSSNumeric>())
        width(KrontabPartsStylesheet.containerWidth)
        display(DisplayStyle.Grid)
        gridTemplateColumns("1fr")

        child(self, selector("*")) style {
            textAlign("center")
        }
    }
}