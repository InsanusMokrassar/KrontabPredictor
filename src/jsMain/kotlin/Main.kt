package dev.inmo.krontab.predictor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.inmo.krontab.predictor.css.*
import dev.inmo.krontab.predictor.ui.main.MainViewModel
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.appendElement
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.url.URLSearchParams

private val urlSearchParams by lazy {
    URLSearchParams(window.location.search)
}
private external fun encodeURIComponent(value: String): String
var krontabInUrl: String?
    get() = urlSearchParams.get("krontab")
    set(value) {
        val newSearchParams = "?krontab=${encodeURIComponent(value ?: "* * * * *")}"

        val url = when {
            window.location.search.isNotEmpty() -> {
                window.location.href.substring(0, window.location.href.indexOf('?'))
            }
            window.location.hash.isNotEmpty() -> {
                window.location.href.substring(0, window.location.href.indexOf('#'))
            }
            else -> window.location.href
        } + newSearchParams

        window.history.pushState(value, "Krontab $value", url)
    }

fun main() {
    val viewModel = MainViewModel(krontabInUrl)
    renderComposable(document.body ?.appendElement("div", {}) ?: return) {
        Style(KrontabPartsStylesheet)
        Style(KrontabCommonStylesheet)
        Style(KrontabDateTimeGridsStylesheet)
        Style(StandardBlockStylesheet)
        Style(KrontabInstructionsStylesheet)
        Style(KrontabPresetsStylesheet)
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
                DrawInput("Seconds", state = viewModel.secondsState.value, onChange = { viewModel.secondsState.value = it })
                DrawInput("Minutes", state = viewModel.minutesState.value, onChange = { viewModel.minutesState.value = it })
                DrawInput("Hours", state = viewModel.hoursState.value, onChange = { viewModel.hoursState.value = it })
                DrawInput("Days", state = viewModel.daysState.value, onChange = { viewModel.daysState.value = it })
                DrawInput("Months", state = viewModel.monthsState.value, onChange = { viewModel.monthsState.value = it })
                DrawInput("Years", state = viewModel.yearsState.value, onChange = { viewModel.yearsState.value = it })
                DrawInput("Timezone", state = viewModel.timezoneState.value, numberTypeRange = 0 until (60 * 24), onChange = { viewModel.timezoneState.value = it })
                DrawInput("Week\u00a0Day", state = viewModel.weekDaysState.value, onChange = { viewModel.weekDaysState.value = it })
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
        DefaultBlock("Output date/times") {
            Div({ classes(KrontabDateTimeGridsStylesheet.container) }) {
                viewModel.schedule.forEach {
                    Label(attrs = { classes(KrontabDateTimeGridsStylesheet.dateTime) }) {
                        Text(DateTimeFormatter.local.format(it.local))
                    }
                }
            }
        }
        DefaultBlock("Instructions") {
            Div({ classes(KrontabInstructionsStylesheet.container) }) {
                Div({ classes(KrontabInstructionsStylesheet.containerItem) }) {
                    B { Text("Modifier") }
                    B { Text("Description") }
                    B { Text("Sample") }
                }
                viewModel.modifierDescriptions.forEach {
                    Div({ classes(KrontabInstructionsStylesheet.containerItem) }) {
                        Label { Text(it.modifier) }
                        Label { Text(it.description) }
                        Label { Text(it.sample.joinToString(" ")) }
                    }
                }
            }
        }
        DefaultBlock("Presets") {
            Div({ classes(KrontabPresetsStylesheet.container) }) {
                Div({ classes(KrontabPresetsStylesheet.containerItem) }) {
                    B { Text("Preset") }
                    B { Text("Krontab") }
                }
                viewModel.presets.forEach {
                    Div({
                        classes(KrontabPresetsStylesheet.containerItem, KrontabPresetsStylesheet.containerItemSelectable)
                        onClick { _ ->
                            viewModel.onSetKrontabState(it.second)
                        }
                    }) {
                        Label { Text(it.first) }
                        Label { Text(it.second) }
                    }
                }
            }
        }

        krontabInUrl = viewModel.krontabTemplateState.value
    }
}
