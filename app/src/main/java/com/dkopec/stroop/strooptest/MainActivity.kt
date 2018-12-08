package com.dkopec.stroop.strooptest

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            7 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }



    fun setStimuliState(state: StroopState) {
        stimuliTextView.text = state.stimulus.displayName
        stimuliTextView.setTextColor(state.color)
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

    fun updateResultsView() {
        resultView.text = stroopTest.getStateDescription()
    }

    fun displayDescription(content: String) {
        stimuliTextView.setTextColor(Color.WHITE)
        stimuliTextView.text = content
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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 7)

            }
        }
    }
}
