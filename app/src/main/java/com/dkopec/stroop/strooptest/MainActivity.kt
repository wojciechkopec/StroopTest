package com.dkopec.stroop.strooptest

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dkopec.stroop.strooptest.controller.StroopTestController
import com.dkopec.stroop.strooptest.model.Stimulus
import com.dkopec.stroop.strooptest.model.StroopState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var stroopTest: StroopTestController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stroopTest = StroopTestController(this)
        upButton.setOnClickListener { onButtonSelected(it) }
        downButton.setOnClickListener { onButtonSelected(it) }
        leftButton.setOnClickListener { onButtonSelected(it) }
        rightButton.setOnClickListener { onButtonSelected(it) }
        confirmButton.setOnClickListener { stroopTest.onConfirmButtonClicked() }
        confirmButton.setBackgroundColor(Color.WHITE)
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
    }

    fun setStimuliState(state: StroopState?) {
        if (state == null) {
            stimulusTextView.visibility = View.INVISIBLE
        } else {
            stimulusTextView.text = state.stimulus.displayName
            stimulusTextView.setTextColor(state.color)
            stimulusTextView.visibility = View.VISIBLE
        }
    }

    fun colorButtons(color: Boolean) {
        if (color) {
            upButton.setBackgroundColor(Color.BLUE)
            downButton.setBackgroundColor(Color.RED)
            leftButton.setBackgroundColor(Color.GREEN)
            rightButton.setBackgroundColor(Color.YELLOW)
        } else {
            arrayOf(upButton, downButton, leftButton, rightButton).forEach { it.setBackgroundColor(Color.WHITE) }
        }
    }

    fun displayDescription(content: CharSequence?) {
        descriptionTextView.setTextColor(Color.WHITE)
        descriptionTextView.text = content

        descriptionTextView.visibility = if (content != null) View.VISIBLE else View.INVISIBLE
        showControllers(content == null)
    }


    fun setConfirmButtonText(content: String?) {
        if (content == null) {
            confirmButton.text = ""
            confirmButton.visibility = View.INVISIBLE
        } else {
            confirmButton.text = content
            confirmButton.visibility = View.VISIBLE
        }
    }

    fun showControllers(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.INVISIBLE
//        buttonsLayout.visibility = visibility
        stimulusTextView.visibility = visibility
    }

    private fun onButtonSelected(button: View) {
        val stimulus = getButtonStimuli(button)
        stroopTest.onStimulusSelected(stimulus)

    }

    private fun getButtonStimuli(button: View): Stimulus {
        when (button) {
            upButton -> return Stimulus.BLUE
            downButton -> return Stimulus.RED
            leftButton -> return Stimulus.GREEN
            rightButton -> return Stimulus.YELLOW
            else -> throw RuntimeException("Unexpected button: $button")
        }
    }

    private fun requestPermissions() {
        if (checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 7)
            }
        }
    }
}
