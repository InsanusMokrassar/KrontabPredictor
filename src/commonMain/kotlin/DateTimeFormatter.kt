package dev.inmo.krontab.predictor

import korlibs.time.DateFormat

object DateTimeFormatter {
    val default = DateFormat(
        "dd/MM/yyyy HH:mm:ss z"
    )
}