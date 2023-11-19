package dev.inmo.krontab.predictor.css

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto

object KrontabInstructionsStylesheet : StyleSheet() {
    val container by style {
        width(100.percent)
        margin(16.px, auto.unsafeCast<CSSNumeric>())
        alignItems(AlignItems.Stretch)
        child(self, selector("*")) style {
            border {
                this.color = Color.darkgray
                this.style = LineStyle.Solid
            }
            borderWidth(right = 0.px, top = 0.px, left = 0.px, bottom = 1.px)
        }
        lastChild style {
            borderWidth(left = 0.px, top = 0.px, right = 0.px, bottom = 0.px)
        }
    }

    val containerItem by style {
        display(DisplayStyle.Grid)
        width(100.percent)
        gridTemplateColumns("1fr 3fr 1fr")
        gridTemplateRows("1fr")
        alignItems("center")
        textAlign("center")
        gap(8.px)

        child(self, selector("*")) style {
            textAlign("center")
            border {
                this.color = Color.darkgray
                this.style = LineStyle.Solid
            }
            borderWidth(right = 1.px, top = 0.px, left = 1.px, bottom = 0.px)
            padding(8.px)
        }
        firstChild style {
            textAlign("right")
            borderWidth(right = 0.px, top = 0.px, left = 0.px, bottom = 0.px)
        }
        lastChild style {
            textAlign("left")
            borderWidth(left = 0.px, top = 0.px, right = 0.px, bottom = 0.px)
        }
    }
}