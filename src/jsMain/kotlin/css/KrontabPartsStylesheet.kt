package dev.inmo.krontab.predictor.css

import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto

object KrontabPartsStylesheet : StyleSheet() {
    val containerSize = 600.px
    val containerItemsGap = 16.px
    val containerItems = 8
    val container by style {
        display(DisplayStyle.Grid)
        margin(0.px, auto.unsafeCast<CSSNumeric>())
        width(containerSize)
        gridTemplateColumns(
            (0 until containerItems).joinToString(" ") { _ -> "1fr" }
        )
        gap(containerItemsGap)
    }

    @OptIn(ExperimentalComposeWebApi::class)
    val element by style {
        display(DisplayStyle.Grid)
        textAlign("center")
        gridTemplateColumns("1fr")

        transitions {
            properties("transform") {
                duration(0.3.s)
                timingFunction(AnimationTimingFunction.EaseInOut)
            }
        }
    }
    @OptIn(ExperimentalComposeWebApi::class)
    val elementFocused by style {
        transform {
            scale(1.2, 1.2)
        }
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