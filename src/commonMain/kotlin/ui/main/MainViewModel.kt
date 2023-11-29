package dev.inmo.krontab.predictor.ui.main

import androidx.compose.runtime.mutableStateListOf
import dev.inmo.krontab.KrontabTemplate
import dev.inmo.krontab.toSchedule
import dev.inmo.krontab.utils.asFlowWithoutDelays
import dev.inmo.micro_utils.common.applyDiff
import dev.inmo.micro_utils.coroutines.compose.asComposeState
import dev.inmo.micro_utils.coroutines.compose.FlowState
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
    val secondsState = FlowState("*")
    val minutesState = FlowState("*")
    val hoursState = FlowState("*")
    val daysState = FlowState("*")
    val monthsState = FlowState("*")
    val yearsState = FlowState("")
    val timezoneState = FlowState("")
    val weekDaysState = FlowState("")
    private fun String.krontabPart(suffix: String = "") = takeIf { it.isNotEmpty() } ?.let { " ${it}$suffix" } ?: ""
    private val krontabTemplateStateFlow = merge(
        this.secondsState,
        this.minutesState,
        this.hoursState,
        this.daysState,
        this.monthsState,
        this.yearsState,
        this.timezoneState,
        this.weekDaysState
    ).map {
        "${secondsState.value} ${this.minutesState.value} ${this.hoursState.value} ${this.daysState.value} ${this.monthsState.value}${this.yearsState.value.krontabPart()}${this.timezoneState.value.krontabPart("o")}${this.weekDaysState.value.krontabPart("w")}"
    }.stateIn(scope, SharingStarted.Eagerly, initialStateOfKrontab ?: "* * * * *")
    val krontabTemplateState = krontabTemplateStateFlow.asComposeState(scope)

    val scheduleItemsState = FlowState(10)

    val presets = listOf(
        "Every second" to "* * * * *",
        "Every minute" to "0 * * * *",
        "Every hour" to "0 0 * * *",
        "Every day" to "0 0 0 * *",
        "Each 15-th minutes" to "0 0/15 * * *",
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
            this.minutesState.value = splitted.removeFirst()
            this.hoursState.value = splitted.removeFirst()
            this.daysState.value = splitted.removeFirst()
            this.monthsState.value = splitted.removeFirst()
            while (splitted.isNotEmpty()) {
                val it = splitted.removeFirst()
                when {
                    it.endsWith("o") -> {
                        this.timezoneState.value = it.dropLast(1)
                    }
                    it.endsWith("w") -> {
                        this.weekDaysState.value = it.dropLast(1)
                    }
                    else -> {
                        this.yearsState.value = it
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
