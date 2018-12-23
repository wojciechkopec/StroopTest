package com.dkopec.stroop.strooptest.controller

import android.text.Html
import com.dkopec.stroop.strooptest.MainActivity
import com.dkopec.stroop.strooptest.R
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
                activity.displayDescription(activity.getString(R.string.text_trial_success))
                currentPhase = Phase.PRE_TO_ACTUAL;

            } else {
                activity.displayDescription(activity.getString(R.string.text_trial_failure))
                currentPhase = Phase.PRE_TO_DESC
            }
        } else {
            activity.displayDescription(activity.getString(R.string.text_test_finished))
        }
        activity.setConfirmButtonText(activity.getString(R.string.text_button_start))
    }

    private fun initilizeTest() {
        this.stroopTest = createTest(currentPhase == Phase.PRE_TEST)
        activity.displayDescription(null)
        onNextStroopState(stroopTest.currentState())
    }

    private fun createTest(isTest: Boolean): StroopTest {
        val dataSaver = StroopDataSaver(activity.application, subjectUUID, isTest);
        if (isTest) {
            return StroopTest(dataSaver, 10, 10, 5)
        } else {
            return StroopTest(dataSaver, 40, 40, 20)
        }
    }

    private fun setupTestMode(test: Boolean) {
        if (test) {
            currentPhase = Phase.PRE_TEST
        } else {
            currentPhase = Phase.ACTUAL_TEST
        }
        activity.colorButtons(false)
        activity.displayDescription(null)
        activity.setConfirmButtonText(null)
        initilizeTest()
    }

    private fun setupDescriptionMode(newSubject: Boolean) {
        currentPhase = Phase.DESCRIPTION
        activity.colorButtons(true)
        activity.displayDescription(Html.fromHtml(activity.getString(R.string.text_description)))
        activity.setConfirmButtonText(activity.getString(R.string.text_button_start))
        activity.setStimuliState(null)
        if (newSubject) {
            subjectUUID = UUID.randomUUID()
        }
    }
}