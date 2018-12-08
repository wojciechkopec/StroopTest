package com.dkopec.stroop.strooptest.model

import java.util.*

class StroopTest(
    private val matchingStimuli: Int = 10,
    private val notMatchingStimuli: Int = 10,
    private val neutralStimuli: Int = 5
) {

    private val stimuliList = createStimuli()

    private val recordedStates: MutableList<StroopTestRecord> = ArrayList()

    private var currentState = generateState()

    private var stateGeneratedTime = System.currentTimeMillis()

    private var failures = 0

    fun currentState(): StroopState {
        return currentState
    }

    fun stimuliChosen(stimulus: Stimulus) {
        val now = System.currentTimeMillis()
        val record = StroopTestRecord(currentState, stimulus, now - stateGeneratedTime)
        recordedStates.add(record)
        if (!record.isCorrect()) {
            failures++
        }
        stateGeneratedTime = now
        if (!isFinished()) {
            currentState = generateState()
        }
    }

    fun getLength() = matchingStimuli + notMatchingStimuli + neutralStimuli

    fun getCurrentSteps() = recordedStates.size

    fun getCorrectness() = if (recordedStates.isEmpty()) 100 else 100 - (100 * failures / getCurrentSteps())

    fun isFinished() = recordedStates.size == getLength()

    private fun generateState(): StroopState {
        return stimuliList[getCurrentSteps()]
    }

    private fun createStimuli(): MutableList<StroopState> {
        val result: MutableList<StroopState> = ArrayList()
        repeat(matchingStimuli) {
            result.add(generateState(true))
        }
        repeat(notMatchingStimuli) {
            result.add(generateState(false))
        }
        repeat(neutralStimuli) {
            result.add(generateNeutralState())
        }
        result.shuffle()
        return result
    }

    private fun generateState(matching: Boolean): StroopState {
        val random = Random()
        val stimuli = Stimulus.getNotNeutral()
        val stimulus = stimuli[random.nextInt(stimuli.size)]
        if (matching) {
            return StroopState(stimulus, stimulus.color)
        }
        val color = stimuli.filter { it != stimulus }[random.nextInt(stimuli.size - 1)].color
        return StroopState(stimulus, color)
    }

    private fun generateNeutralState(): StroopState {
        val stimuli = Stimulus.getNotNeutral()
        val random = Random()
        val color = stimuli[random.nextInt(stimuli.size)].color
        return StroopState(Stimulus.NEUTRAL, color)
    }
}