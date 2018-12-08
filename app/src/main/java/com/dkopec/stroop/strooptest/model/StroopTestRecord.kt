package com.dkopec.stroop.strooptest.model

data class StroopTestRecord(val presentedState: StroopState, val chosenStimulus: Stimulus, val time: Long) {

    fun isCorrect(): Boolean {
        return presentedState.color == chosenStimulus.color
    }
}