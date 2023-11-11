package dev.inmo.krontab.predictor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.inmo.krontab.predictor.css.KrontabCommonStylesheet
import dev.inmo.krontab.predictor.css.KrontabDateTimeGridsStylesheet
import dev.inmo.krontab.predictor.css.KrontabPartsStylesheet
import dev.inmo.krontab.predictor.css.StandardBlockStylesheet
import dev.inmo.krontab.predictor.ui.main.MainViewModel
import dev.inmo.krontab.predictor.utils.RawElement
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.appendElement
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.url.URLSearchParams

private val urlSearchParams by lazy {
    URLSearchParams(window.location.search)
}
var krontabInUrl: String?
    get() = urlSearchParams.get("krontab")
    set(value) {
        value ?.also {
            urlSearchParams.apply { set("krontab", it) }.toString()
        } ?: urlSearchParams.apply { delete("krontab") }.toString()
        val url = window.location.toString().replace(window.location.search, "?$urlSearchParams")
        window.history.pushState(value, "Krontab $value", url)
    }

fun main() {
    MdOutlinedTextField() // just to activate material web components
    val viewModel = MainViewModel(krontabInUrl)
    renderComposable(document.body ?.appendElement("div", {}) ?: return) {
        Style(KrontabPartsStylesheet)
        Style(KrontabCommonStylesheet)
        Style(KrontabDateTimeGridsStylesheet)
        Style(StandardBlockStylesheet)
        DefaultBlock("Krontab parts") {
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
                        RawElement(
                            "md-outlined-text-field",
                            {
                                classes(KrontabPartsStylesheet.input)
                                attr("value", mutableState.value)
                                addEventListener("input") {
                                    mutableState.value = (it.target as HTMLElement).getAttribute("value") ?.trim() ?: return@addEventListener
                                    onChange(mutableState.value)
                                }
                                onFocusIn {
                                    focused.value = true
                                }
                                onFocusOut {
                                    focused.value = false
                                }
                            }
                        )
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
        }
        DefaultBlock("Krontab string") {
            Div({ classes(KrontabCommonStylesheet.container) }) {
                RawElement(
                "md-outlined-text-field",
                    {
                        attr("value", viewModel.krontabTemplateState.value)
                        attr("readOnly", "true")
                    }
                )
            }
        }
        DefaultBlock("Instructions") {
            RawElement("md-text-button")
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
