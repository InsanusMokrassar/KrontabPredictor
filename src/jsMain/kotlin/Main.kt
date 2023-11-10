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
            @Composable
            fun DrawState(title: String, state: String, onChange: (String) -> Unit) {
                val mutableState = remember { mutableStateOf(state) }
                val focused = remember { mutableStateOf(false) }
                Div({
                    classes(KrontabPartsStylesheet.element)
                    if (focused.value) {
                        classes(KrontabPartsStylesheet.elementFocused)
                    }
                }) {
                    Div({ classes(KrontabPartsStylesheet.labelContainer) }) {
                        Label { Text(title) }
                    }
                    Input(
                        InputType.Text,
                    ) {
                        classes(KrontabPartsStylesheet.input)
                        value(mutableState.value)
                        onInput {
                            mutableState.value = it.value.trim()
                            onChange(mutableState.value)
                        }
                        onFocusIn {
                            focused.value = true
                        }
                        onFocusOut {
                            focused.value = false
                        }
                    }
                }

            }
            DrawState("Seconds", state = viewModel.secondsUIState.value, onChange = { viewModel.secondsState.value = it })
            DrawState("Minutes", state = viewModel.minutesUIState.value, onChange = { viewModel.minutesState.value = it })
            DrawState("Hours", state = viewModel.hoursUIState.value, onChange = { viewModel.hoursState.value = it })
            DrawState("Days", state = viewModel.daysUIState.value, onChange = { viewModel.daysState.value = it })
            DrawState("Months", state = viewModel.monthsUIState.value, onChange = { viewModel.monthsState.value = it })
            DrawState("Years", state = viewModel.yearsUIState.value, onChange = { viewModel.yearsState.value = it })
            DrawState("Timezone", state = viewModel.timezoneUIState.value, onChange = { viewModel.timezoneState.value = it })
            DrawState("Week\u00a0Day", state = viewModel.weekDaysUIState.value, onChange = { viewModel.weekDaysState.value = it })
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
