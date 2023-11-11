package dev.inmo.krontab.predictor

import androidx.compose.runtime.Composable
import dev.inmo.krontab.predictor.css.KrontabDateTimeGridsStylesheet
import dev.inmo.krontab.predictor.css.StandardBlockStylesheet
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text

@Composable
fun DefaultBlock(title: String, block: @Composable () -> Unit) {
    Div({ classes(StandardBlockStylesheet.container) }) {
        H4 {
            Text(title)
        }
        block()
    }
}