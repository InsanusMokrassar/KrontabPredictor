package dev.inmo.krontab.predictor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.inmo.krontab.predictor.css.KrontabCommonStylesheet
import dev.inmo.krontab.predictor.css.KrontabDateTimeGridsStylesheet
import dev.inmo.krontab.predictor.css.KrontabPartsStylesheet
import dev.inmo.krontab.predictor.ui.main.MainViewModel
import korlibs.time.format
import kotlinx.browser.document
import kotlinx.dom.appendElement
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    val viewModel = MainViewModel()
    renderComposable(document.body ?.appendElement("div", {}) ?: return) {
        Style(KrontabPartsStylesheet)
        Style(KrontabCommonStylesheet)
        Style(KrontabDateTimeGridsStylesheet)
        Div({ classes(KrontabPartsStylesheet.container) }) {
            Div { Text("Seconds") }
            Div { Text("Minutes") }
            Div { Text("Hours") }
            Div { Text("Days") }
            Div { Text("Months") }
            Div { Text("Years") }
            Div { Text("Timezone") }
            Div { Text("Week\u00a0day") }
        }
        Div({ classes(KrontabPartsStylesheet.container) }) {
            @Composable
            fun DrawState(state: String, onChange: (String) -> Unit) {
                val mutableState = remember { mutableStateOf(state) }
                Input(
                    InputType.Text,
                ) {
                    value(mutableState.value)
                    onInput {
                        mutableState.value = it.value.trim()
                        onChange(mutableState.value)
                    }
                }
            }
            DrawState(state = viewModel.secondsUIState.value, onChange = { viewModel.secondsState.value = it })
            DrawState(state = viewModel.minutesUIState.value, onChange = { viewModel.minutesState.value = it })
            DrawState(state = viewModel.hoursUIState.value, onChange = { viewModel.hoursState.value = it })
            DrawState(state = viewModel.daysUIState.value, onChange = { viewModel.daysState.value = it })
            DrawState(state = viewModel.monthsUIState.value, onChange = { viewModel.monthsState.value = it })
            DrawState(state = viewModel.yearsUIState.value, onChange = { viewModel.yearsState.value = it })
            DrawState(state = viewModel.timezoneUIState.value, onChange = { viewModel.timezoneState.value = it })
            DrawState(state = viewModel.weekDaysUIState.value, onChange = { viewModel.weekDaysState.value = it })
        }
        Div({ classes(KrontabCommonStylesheet.container) }) {
            Input(InputType.Text) {
                value(viewModel.krontabTemplateState.value)
            }
        }
        Div({ classes(KrontabDateTimeGridsStylesheet.container) }) {
            viewModel.schedule.forEach {
                Label {
                    Text(DateTimeFormatter.default.format(it.local))
                }
            }
        }
    }
}
