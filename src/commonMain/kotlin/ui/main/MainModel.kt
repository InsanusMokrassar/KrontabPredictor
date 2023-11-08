package dev.inmo.krontab.predictor.ui.main

import dev.inmo.krontab.KrontabTemplate

interface MainModel {
    suspend fun getLatestKnownKrontabTemplate(): KrontabTemplate
}