package dev.inmo.krontab.predictor.ui.main

import androidx.compose.runtime.*
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

class MainViewModel {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val secondsState = MutableStateFlow("*")
    val secondsUIState = secondsState.asComposeState(scope)
    val minutesState = MutableStateFlow("*")
    val minutesUIState = minutesState.asComposeState(scope)
    val hoursState = MutableStateFlow("*")
    val hoursUIState = hoursState.asComposeState(scope)
    val daysState = MutableStateFlow("*")
    val daysUIState = daysState.asComposeState(scope)
    val monthsState = MutableStateFlow("*")
    val monthsUIState = monthsState.asComposeState(scope)
    private val krontabTemplateStateFlow = merge(
        secondsState,
        minutesState,
        hoursState,
        daysState,
        monthsState
    ).map {
        "${secondsState.value} ${minutesState.value} ${hoursState.value} ${daysState.value} ${monthsState.value}"
    }.stateIn(scope, SharingStarted.Eagerly, "* * * * *")

    val scheduleItemsState = MutableStateFlow(10)
    val scheduleUIItemsState = scheduleItemsState.asComposeState(scope)

    private val _schedule = mutableStateListOf<DateTime>()
    val schedule: List<DateTime>
        get() = _schedule
    private var job: Job? = null
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
}
