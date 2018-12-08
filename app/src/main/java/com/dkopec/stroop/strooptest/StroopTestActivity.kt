package com.dkopec.stroop.strooptest

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.dkopec.stroop.strooptest.controller.StroopTestController
import com.dkopec.stroop.strooptest.model.Stimulus
import com.dkopec.stroop.strooptest.model.StroopState
import kotlinx.android.synthetic.main.activity_stroop_test.*
import kotlinx.android.synthetic.main.app_bar_stroop_test.*
import kotlinx.android.synthetic.main.content_stroop_test.*

class StroopTestActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var stroopTest: StroopTestController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stroop_test)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        stroopTest = StroopTestController(this)
        upButton.setOnClickListener { onButtonSelected(it) }
        downButton.setOnClickListener { onButtonSelected(it) }
        leftButton.setOnClickListener { onButtonSelected(it) }
        rightButton.setOnClickListener { onButtonSelected(it) }
        confirmButton.setOnClickListener { stroopTest.onConfirmButtonClicked() }
//        colorButtons(true)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.stroop_test, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
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
        stimuliTextView.setTextColor(Color.BLACK)
        stimuliTextView.text = content
    }

    fun setConfirmButtonText(content: String?) {
        if(content == null){
            confirmButton.text = ""
            confirmButton.isEnabled = false
        }
        confirmButton.text = content
        confirmButton.isEnabled = true
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
}
