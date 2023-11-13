package dev.inmo.krontab.predictor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.inmo.krontab.predictor.css.KrontabCommonStylesheet
import dev.inmo.krontab.predictor.css.KrontabDateTimeGridsStylesheet
import dev.inmo.krontab.predictor.css.KrontabPartsStylesheet
import dev.inmo.krontab.predictor.css.StandardBlockStylesheet
import dev.inmo.krontab.predictor.ui.main.MainViewModel
import dev.inmo.krontab.predictor.utils.MaterialElement
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.appendElement
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.url.URLSearchParams

private val urlSearchParams by lazy {
    URLSearchParams(window.location.search)
}
var krontabInUrl: String?
    get() = urlSearchParams.get("krontab")
    set(value) {
        while (urlSearchParams.has("krontab")) {
            urlSearchParams.delete("krontab")
        }
        value ?.also {
            urlSearchParams.set("krontab", it)
        }
        val url = window.location.toString().replace(window.location.search, "?$urlSearchParams")
        window.history.pushState(value, "Krontab $value", url)
    }

fun main() {
    val viewModel = MainViewModel(krontabInUrl)
    renderComposable(document.body ?.appendElement("div", {}) ?: return) {
        Style(KrontabPartsStylesheet)
        Style(KrontabCommonStylesheet)
        Style(KrontabDateTimeGridsStylesheet)
        Style(StandardBlockStylesheet)
        DefaultBlock("Krontab parts") {
            Div({ classes(KrontabPartsStylesheet.container) }) {
                @Composable
                fun DrawInput(title: String, state: String, numberTypeRange: IntRange? = null, onChange: (String) -> Unit) {
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
                            numberTypeRange ?.let {
                                InputType.Number
                            } ?: InputType.Text
                        ) {
                            numberTypeRange ?.let {
                                min(it.first.toString())
                                max(it.toString())
                            }
                            classes(KrontabPartsStylesheet.input)
                            value(mutableState.value)
                            onInput {
                                mutableState.value = it.target.value.trim()
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
                DrawInput("Seconds", state = viewModel.secondsUIState.value, onChange = { viewModel.secondsState.value = it })
                DrawInput("Minutes", state = viewModel.minutesUIState.value, onChange = { viewModel.minutesState.value = it })
                DrawInput("Hours", state = viewModel.hoursUIState.value, onChange = { viewModel.hoursState.value = it })
                DrawInput("Days", state = viewModel.daysUIState.value, onChange = { viewModel.daysState.value = it })
                DrawInput("Months", state = viewModel.monthsUIState.value, onChange = { viewModel.monthsState.value = it })
                DrawInput("Years", state = viewModel.yearsUIState.value, onChange = { viewModel.yearsState.value = it })
                DrawInput("Timezone", state = viewModel.timezoneUIState.value, numberTypeRange = 0 until (60 * 24), onChange = { viewModel.timezoneState.value = it })
                DrawInput("Week\u00a0Day", state = viewModel.weekDaysUIState.value, onChange = { viewModel.weekDaysState.value = it })
            }
        }
        DefaultBlock("Krontab string") {
            Div({ classes(KrontabCommonStylesheet.container) }) {
                Input(
                    InputType.Text
                ) {
                    value(viewModel.krontabTemplateState.value)
                    classes(KrontabCommonStylesheet.input)
                    readOnly()
                }
            }
        }
        DefaultBlock("Instructions") {
            MaterialElement("md-text-button")
        }
        DefaultBlock("Output date/times") {
            Div({ classes(KrontabDateTimeGridsStylesheet.container) }) {
                viewModel.schedule.forEach {
                    Label {
                        Text(DateTimeFormatter.local.format(it.local))
                    }
                }
            }
        }

        krontabInUrl = viewModel.krontabTemplateState.value
    }
}
