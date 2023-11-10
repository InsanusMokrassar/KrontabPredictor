package dev.inmo.krontab.predictor.ui.main

import androidx.compose.runtime.*
import dev.inmo.krontab.KrontabTemplate
import dev.inmo.krontab.toSchedule
import dev.inmo.krontab.utils.asFlowWithoutDelays
import dev.inmo.micro_utils.common.applyDiff
import dev.inmo.micro_utils.coroutines.compose.asComposeState
import dev.inmo.micro_utils.coroutines.doInUI
import korlibs.time.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

class MainViewModel(
    initialStateOfKrontab: KrontabTemplate? = null
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val secondsState = MutableStateFlow("*")
    val secondsUIState by lazy { secondsState.asComposeState(scope) }
    val minutesState = MutableStateFlow("*")
    val minutesUIState by lazy { minutesState.asComposeState(scope) }
    val hoursState = MutableStateFlow("*")
    val hoursUIState by lazy { hoursState.asComposeState(scope) }
    val daysState = MutableStateFlow("*")
    val daysUIState by lazy { daysState.asComposeState(scope) }
    val monthsState = MutableStateFlow("*")
    val monthsUIState by lazy { monthsState.asComposeState(scope) }
    val yearsState = MutableStateFlow("")
    val yearsUIState by lazy { monthsState.asComposeState(scope) }
    val timezoneState = MutableStateFlow("")
    val timezoneUIState by lazy { monthsState.asComposeState(scope) }
    val weekDaysState = MutableStateFlow("")
    val weekDaysUIState by lazy { monthsState.asComposeState(scope) }
    private fun String.krontabPart(suffix: String = "") = takeIf { it.isNotEmpty() } ?.let { " ${it}$suffix" } ?: ""
    private val krontabTemplateStateFlow = merge(
        secondsState,
        minutesState,
        hoursState,
        daysState,
        monthsState,
        yearsState,
        timezoneState,
        weekDaysState
    ).map {
        "${secondsState.value} ${minutesState.value} ${hoursState.value} ${daysState.value} ${monthsState.value}${yearsState.value.krontabPart()}${timezoneState.value.krontabPart("o")}${weekDaysState.value.krontabPart("w")}"
    }.stateIn(scope, SharingStarted.Eagerly, "* * * * *")
    val krontabTemplateState = krontabTemplateStateFlow.asComposeState(scope)

    val scheduleItemsState = MutableStateFlow(10)
    val scheduleUIItemsState = scheduleItemsState.asComposeState(scope)

    val presets = listOf(
        "Every second" to "* * * * *"
    )

    private val _schedule = mutableStateListOf<DateTime>()
    val schedule: List<DateTime>
        get() = _schedule
    private val scheduleRecalculatorMapper = merge(
        krontabTemplateStateFlow
    ).map {
        runCatching {
            val now = DateTime.now()
            val krontab = krontabTemplateStateFlow.value.toSchedule().asFlowWithoutDelays(now)
            val newSchedule = krontab.take(scheduleItemsState.value).toList()
            doInUI {
                _schedule.applyDiff(newSchedule)
            }
        }.onFailure {
            it.printStackTrace()
        }
    }
    private val scheduleRecalculatorMapperJob = scheduleRecalculatorMapper.launchIn(scope)

    fun onSetKrontabState(krontabTemplate: KrontabTemplate) {
        val splitted = krontabTemplate.split(" ")

        runCatching {
            secondsState.value = splitted[0]
            minutesState.value = splitted[1]
            hoursState.value = splitted[2]
            daysState.value = splitted[3]
            monthsState.value = splitted[4]
            splitted.drop(5).forEach {
                when {
                    it.endsWith("o") -> {
                        timezoneState.value = it.drop(1)
                    }
                    it.endsWith("w") -> {
                        weekDaysState.value = it.drop(1)
                    }
                    else -> {
                        yearsState.value = it
                    }
                }
            }
        }
    }

    init {
        initialStateOfKrontab ?.let {
            onSetKrontabState(initialStateOfKrontab)
        }
    }
}
