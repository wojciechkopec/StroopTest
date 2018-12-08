package com.dkopec.stroop.strooptest.controller

import com.dkopec.stroop.strooptest.MainActivity
import com.dkopec.stroop.strooptest.model.Stimulus
import com.dkopec.stroop.strooptest.model.StroopState
import com.dkopec.stroop.strooptest.model.StroopTest
import java.util.*

class StroopTestController {

    enum class Phase {
        DESCRIPTION, PRE_TEST, ACTUAL_TEST, PRE_TO_ACTUAL, PRE_TO_DESC;
    }

    private val correctnessThreshold = 70;

    private val activity: MainActivity

    private var currentPhase: Phase

    private var stroopTest: StroopTest

    private var subjectUUID = UUID.randomUUID()

    constructor(activity: MainActivity) {
        this.activity = activity
        this.currentPhase = Phase.DESCRIPTION
        this.stroopTest = createTest(false)
        initialize()
    }

    fun initialize() {
        this.currentPhase = Phase.DESCRIPTION
        setupDescriptionMode(true)
    }

    fun onStimulusSelected(stimulus: Stimulus) {
        if (currentPhase != Phase.ACTUAL_TEST && currentPhase != Phase.PRE_TEST) {
            return
        }
        stroopTest.stimuliChosen(stimulus)
        activity.updateResultsView()
        onNextStroopState(stroopTest.currentState())
    }

    fun onConfirmButtonClicked() {
        when (currentPhase) {
            Phase.ACTUAL_TEST -> setupDescriptionMode(true)
            Phase.DESCRIPTION -> setupTestMode(true)
            Phase.PRE_TO_ACTUAL -> setupTestMode(false)
            Phase.PRE_TO_DESC -> setupDescriptionMode(false)
        }
    }

    fun getStateDescription() =
        "${stroopTest.getCurrentSteps()}/${stroopTest.getLength()} ${stroopTest.getCorrectness()}% $currentPhase"

    private fun onNextStroopState(state: StroopState) {
        if (stroopTest.isFinished()) {
            onTestFinished()
            return
        }
        activity.setStimuliState(state)
    }

    private fun onTestFinished() {
        if (currentPhase == Phase.PRE_TEST) {
            if (stroopTest.getCorrectness() >= correctnessThreshold) {
                activity.displayDescription("Test pomyślny, przejdź do właściwego")
                activity.setConfirmButtonText("Dalej")
                currentPhase = Phase.PRE_TO_ACTUAL;

            } else {
                activity.displayDescription("Test niepomyslny, wróć")
                activity.setConfirmButtonText("Wróć")
                currentPhase = Phase.PRE_TO_DESC
            }
            return;
        } else {
            activity.displayDescription("Koniec, dzięki")
            activity.setConfirmButtonText("Od początku")
        }
    }

    private fun initilizeTest() {
        this.stroopTest = createTest(currentPhase == Phase.PRE_TEST)
        onNextStroopState(stroopTest.currentState())
    }

    private fun createTest(isTest: Boolean): StroopTest {
        val dataSaver = StroopDataSaver(activity.application, subjectUUID, isTest);
        return StroopTest(dataSaver, 10, 10, 5)
    }

    private fun setupTestMode(test: Boolean) {
        if (test) {
            currentPhase = Phase.PRE_TEST
        } else {
            currentPhase = Phase.ACTUAL_TEST
        }
        activity.colorButtons(false)
        activity.displayDescription("")
        activity.setConfirmButtonText(null)
        initilizeTest()
    }

    private fun setupDescriptionMode(newSubject: Boolean) {
        currentPhase = Phase.DESCRIPTION
        activity.colorButtons(true)
        activity.displayDescription("Lorem ipsum...")
        activity.setConfirmButtonText("Start!")
        if (newSubject) {
            subjectUUID = UUID.randomUUID()
        }
    }
}