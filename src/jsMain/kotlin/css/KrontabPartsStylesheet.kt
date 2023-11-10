package dev.inmo.krontab.predictor.css

import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.GridAutoFlow
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.gridAutoFlow
import org.jetbrains.compose.web.css.gridColumn
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.width

object KrontabPartsStylesheet : StyleSheet() {
    val containerSize = 600.px
    val containerItemsGap = 16.px
    val containerItems = 9
    val container by style {
        display(DisplayStyle.Grid)
        margin(0.px, auto.unsafeCast<CSSNumeric>())
        width(containerSize)
        gridTemplateColumns(
            (0 until containerItems).joinToString(" ") { _ -> "1fr" }
        )
        gap(containerItemsGap)
    }

    val element by style {
        display(DisplayStyle.Grid)
        textAlign("center")
        gridTemplateColumns("1fr")
        gridColumn("span 1")
    }
    val elementFocused by style {
        gridColumn("span 2")
    }

    val input by style {
        minWidth(0.px)
        textAlign("center")
    }

    val labelContainer by style {
        minWidth(0.px)
        textAlign("center")
    }
}