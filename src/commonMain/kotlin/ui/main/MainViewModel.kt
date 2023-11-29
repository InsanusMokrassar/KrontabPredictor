package dev.inmo.krontab.predictor.ui.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import dev.inmo.krontab.KrontabTemplate
import dev.inmo.krontab.toSchedule
import dev.inmo.krontab.utils.asFlowWithoutDelays
import dev.inmo.micro_utils.common.applyDiff
import dev.inmo.micro_utils.coroutines.compose.asComposeState
import dev.inmo.micro_utils.coroutines.compose.asFlowState
import dev.inmo.micro_utils.coroutines.doInUI
import korlibs.time.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

class MainViewModel(
    initialStateOfKrontab: KrontabTemplate? = "15 * * * *"
) {
    data class ModifierInstruction(
        val modifier: String,
        val description: String,
        val sample: List<String>
    )
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val secondsState = mutableStateOf("*").asFlowState(scope)
    val minutesState = MutableStateFlow("*")
    val minutesUIState by lazy { minutesState.asComposeState(scope) }
    val hoursState = MutableStateFlow("*")
    val hoursUIState by lazy { hoursState.asComposeState(scope) }
    val daysState = MutableStateFlow("*")
    val daysUIState by lazy { daysState.asComposeState(scope) }
    val monthsState = MutableStateFlow("*")
    val monthsUIState by lazy { monthsState.asComposeState(scope) }
    val yearsState = MutableStateFlow("")
    val yearsUIState by lazy { yearsState.asComposeState(scope) }
    val timezoneState = MutableStateFlow("")
    val timezoneUIState by lazy { timezoneState.asComposeState(scope) }
    val weekDaysState = MutableStateFlow("")
    val weekDaysUIState by lazy { weekDaysState.asComposeState(scope) }
    private fun String.krontabPart(suffix: String = "") = takeIf { it.isNotEmpty() } ?.let { " ${it}$suffix" } ?: ""
    private val krontabTemplateStateFlow = merge(
        this.secondsState,
        minutesState,
        hoursState,
        daysState,
        monthsState,
        yearsState,
        timezoneState,
        weekDaysState
    ).map {
        "${this.secondsState.value} ${minutesState.value} ${hoursState.value} ${daysState.value} ${monthsState.value}${yearsState.value.krontabPart()}${timezoneState.value.krontabPart("o")}${weekDaysState.value.krontabPart("w")}"
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
    val modifierDescriptions = mutableStateListOf<ModifierInstruction>().apply {
        add(
            ModifierInstruction(
                modifier = ",",
                description = "Use to separate different time selectors",
                sample = listOf("10,20")
            )
        )
        add(
            ModifierInstruction(
                modifier = "/",
                description = "Use to set repeatable rule. Left side is the time when rule starts to work, right one is the divider of when time is working",
                sample = listOf(
                    "0/20",
                    "*/20",
                    "/20",
                )
            )
        )
        add(
            ModifierInstruction(
                modifier = "-",
                description = "Use to set the range of data. Sample will be equal to 5,6,7,8,9,10",
                sample = listOf(
                    "5-10",
                )
            )
        )
        add(
            ModifierInstruction(
                modifier = "F",
                description = "Used as the first value in section. For example, for seconds it is 0",
                sample = listOf(
                    "F",
                )
            )
        )
        add(
            ModifierInstruction(
                modifier = "L",
                description = "Used as the last value in section. For example, for month it will set section value to 31 which means \"latest day\"",
                sample = listOf(
                    "L",
                )
            )
        )
    }

    fun onSetKrontabState(krontabTemplate: KrontabTemplate) {
        val splitted = ArrayDeque(krontabTemplate.split(" "))

        runCatching {
            secondsState.value = splitted.removeFirst()
            minutesState.value = splitted.removeFirst()
            hoursState.value = splitted.removeFirst()
            daysState.value = splitted.removeFirst()
            monthsState.value = splitted.removeFirst()
            while (splitted.isNotEmpty()) {
                val it = splitted.removeFirst()
                when {
                    it.endsWith("o") -> {
                        timezoneState.value = it.dropLast(1)
                    }
                    it.endsWith("w") -> {
                        weekDaysState.value = it.dropLast(1)
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
