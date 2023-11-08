package dev.inmo.krontab.predictor.ui.main

import androidx.compose.runtime.*
import dev.inmo.krontab.KrontabTemplate
import dev.inmo.krontab.nextOrNow
import dev.inmo.krontab.toSchedule
import dev.inmo.krontab.utils.asFlowWithoutDelays
import korlibs.time.DateTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MainViewModel {
    val secondsState = mutableStateOf("*")
    val minutesState = mutableStateOf("*")
    val hoursState = mutableStateOf("*")
    val daysState = mutableStateOf("*")
    val monthsState = mutableStateOf("*")
    private val _krontabTemplateState = mutableStateOf<KrontabTemplate>("")
    val krontabTemplateState: State<KrontabTemplate> = derivedStateOf {
        "${secondsState.value} ${minutesState.value} ${hoursState.value} ${daysState.value} ${monthsState.value} "
    }

    val scheduleItems = mutableStateOf(10)

    private val _schedule = mutableStateOf<List<DateTime>>(emptyList())
    val schedule: State<List<DateTime>>
        get() = _schedule
    private val scheduleRecalculator = derivedStateOf {
        val scope = rememberCoroutineScope()
        var job: Job? = remember { null }
        job ?.cancel()
        val count = scheduleItems.value
        job = scope.launch {
            runCatching {
                val now = DateTime.now()
                val krontab = krontabTemplateState.value.toSchedule().asFlowWithoutDelays(now)
                _schedule.value = krontab.take(count).toList()
            }
        }
    }
}
