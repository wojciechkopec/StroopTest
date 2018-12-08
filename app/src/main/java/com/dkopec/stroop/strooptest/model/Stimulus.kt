package com.dkopec.stroop.strooptest.model

import android.graphics.Color

enum class Stimulus(val displayName: String, val color: Int) {
    RED("czerwony", Color.RED), GREEN("zielony", Color.GREEN), BLUE("niebieski", Color.BLUE), YELLOW(
        "żółty", Color.YELLOW
    ),
    NEUTRAL("xxx", Color.WHITE);

    companion object {
        fun getNotNeutral(): List<Stimulus> {
            return Stimulus.values().filter { it != NEUTRAL }
        }
    }
}