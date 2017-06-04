package ru.bokhonin.montanaktl

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mStartButton = findViewById(R.id.startButton) as Button
        mStartButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Service is STARTED", Toast.LENGTH_LONG).show()
            MontanaService_.setServiceAlarm(this@MainActivity, true)
            SetCurrentStatusService()
            SaveAllPreferences()
        }

        val mStopButton = findViewById(R.id.stopButton) as Button
        mStopButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Service is FINISHED", Toast.LENGTH_LONG).show()
            MontanaService_.setServiceAlarm(this@MainActivity, false)
            SetCurrentStatusService()
            SaveAllPreferences()
        }

        val mVibrationSwitch = findViewById(R.id.vibrationSwitch) as Switch
        mVibrationSwitch.setOnClickListener {
            SetStatusButtonStart()
            ChangeDescriptionButtonStart()
        }

        val mSoundSwitch  = findViewById(R.id.soundSwitch) as Switch
        mSoundSwitch.setOnClickListener {
            SetStatusButtonStart()
            ChangeDescriptionButtonStart()
        }

        SetCurrentStatusService()
        LoadPreferences()
        SaveAllPreferences()
        SetStatusButtonStart()
    }


    private fun SetStatusButtonStart() {
        val mVibrationSwitch = findViewById(R.id.vibrationSwitch) as Switch
        val mSoundSwitch = findViewById(R.id.soundSwitch) as Switch
        val mStartButton = findViewById(R.id.startButton) as Button

        mStartButton.isEnabled = !(!mVibrationSwitch.isChecked && !mSoundSwitch.isChecked)
    }

    private fun ChangeDescriptionButtonStart() {
        if (MontanaService_.isServiceAlarmOn(this@MainActivity)) {
            val mStartButton = findViewById(R.id.startButton) as Button
            mStartButton.text = "RESTART"
        }
    }

    private fun SaveAllPreferences() {
        val mVibrationSwitch = findViewById(R.id.vibrationSwitch) as Switch
        val mSoundSwitch = findViewById(R.id.soundSwitch) as Switch

        SavePreferences("mVibrationSwitch", mVibrationSwitch.isChecked)
        SavePreferences("mSoundSwitch", mSoundSwitch.isChecked)
    }

    private fun SavePreferences(key: String, value: Boolean) {
        val sharedPreferences = getSharedPreferences("montana_preferences", 0)
        val editor = sharedPreferences.edit()

        editor.putBoolean(key, value)
        editor.apply()
    }

    private fun LoadPreferences() {
        val sharedPreferences = getSharedPreferences("montana_preferences", 0)
        val mVibrationSwitch = findViewById(R.id.vibrationSwitch) as Switch
        val mSoundSwitch = findViewById(R.id.soundSwitch) as Switch

        mVibrationSwitch.isChecked = sharedPreferences.getBoolean("mVibrationSwitch", true)
        mSoundSwitch.isChecked = sharedPreferences.getBoolean("mSoundSwitch", false)
    }

    private fun SetCurrentStatusService() {
        val serviceStarted = MontanaService_.isServiceAlarmOn(this@MainActivity)

        val mCurrentStatusService = findViewById(R.id.currentStatusServiceTextView) as TextView

        val text = if (serviceStarted) "Service is started" else "Service is not started"
        mCurrentStatusService.text = text
    }


}




