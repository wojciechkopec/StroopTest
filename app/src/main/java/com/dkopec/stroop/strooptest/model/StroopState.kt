package com.dkopec.stroop.strooptest.model

data class StroopState(val stimulus: Stimulus, val color: Int) {

    fun type(): String {
        if (stimulus == Stimulus.NEUTRAL) {
            return "N"
        } else {
            return if (stimulus.color == color) "C" else "I"
        }
    }
}