package com.dkopec.stroop.strooptest.model

data class StroopState(val stimulus: Stimulus, val color: Int) {

    fun type(): String {
        if (stimulus == Stimulus.NEUTRAL) {
            return "NEUTRAL"
        } else {
            return if (stimulus.color == color) "MATCHING" else "NOT_MATCHING"
        }
    }
}
