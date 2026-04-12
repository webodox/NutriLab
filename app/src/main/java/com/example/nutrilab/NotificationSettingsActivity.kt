package com.example.nutrilab

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var mealToggle: Switch
    private lateinit var waterToggle: Switch
    private lateinit var symptomToggle: Switch
    private lateinit var mealTimeButton: Button
    private lateinit var waterTimeButton: Button
    private lateinit var symptomTimeButton: Button
    private lateinit var saveButton: Button
    private lateinit var mealPresetSpinner: Spinner
    private lateinit var waterPresetSpinner: Spinner
    private lateinit var symptomPresetSpinner: Spinner

    private var mealHour = 8
    private var mealMinute = 0
    private var waterHour = 12
    private var waterMinute = 0
    private var symptomHour = 20
    private var symptomMinute = 0

    private val presetTimes = arrayOf("Custom time", "8:00 AM", "10:00 AM", "12:00 PM", "2:00 PM", "6:00 PM", "8:00 PM")
    private val presetHours = intArrayOf(-1, 8, 10, 12, 14, 18, 20)
    private val presetMinutes = intArrayOf(-1, 0, 0, 0, 0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        mealToggle = findViewById(R.id.mealToggle)
        waterToggle = findViewById(R.id.waterToggle)
        symptomToggle = findViewById(R.id.symptomToggle)
        mealTimeButton = findViewById(R.id.mealTimeButton)
        waterTimeButton = findViewById(R.id.waterTimeButton)
        symptomTimeButton = findViewById(R.id.symptomTimeButton)
        saveButton = findViewById(R.id.saveNotifButton)
        mealPresetSpinner = findViewById(R.id.mealPresetSpinner)
        waterPresetSpinner = findViewById(R.id.waterPresetSpinner)
        symptomPresetSpinner = findViewById(R.id.symptomPresetSpinner)

        //back button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this@NotificationSettingsActivity, DashboardActivity::class.java))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, presetTimes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mealPresetSpinner.adapter = adapter
        waterPresetSpinner.adapter = adapter
        symptomPresetSpinner.adapter = adapter

        mealPresetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
                if (presetHours[pos] != -1) {
                    mealHour = presetHours[pos]
                    mealMinute = presetMinutes[pos]
                    mealTimeButton.text = "Meal: ${formatTime(mealHour, mealMinute)}"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        waterPresetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
                if (presetHours[pos] != -1) {
                    waterHour = presetHours[pos]
                    waterMinute = presetMinutes[pos]
                    waterTimeButton.text = "Water: ${formatTime(waterHour, waterMinute)}"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        symptomPresetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
                if (presetHours[pos] != -1) {
                    symptomHour = presetHours[pos]
                    symptomMinute = presetMinutes[pos]
                    symptomTimeButton.text = "Symptoms: ${formatTime(symptomHour, symptomMinute)}"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        mealTimeButton.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                mealHour = hour
                mealMinute = minute
                mealTimeButton.text = "Meal: ${formatTime(hour, minute)}"
            }, mealHour, mealMinute, false).show()
        }

        waterTimeButton.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                waterHour = hour
                waterMinute = minute
                waterTimeButton.text = "Water: ${formatTime(hour, minute)}"
            }, waterHour, waterMinute, false).show()
        }

        symptomTimeButton.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                symptomHour = hour
                symptomMinute = minute
                symptomTimeButton.text = "Symptoms: ${formatTime(hour, minute)}"
            }, symptomHour, symptomMinute, false).show()
        }

        saveButton.setOnClickListener {
            if (mealToggle.isChecked) scheduleNotification(1, mealHour, mealMinute, "Meal Reminder", "Time to log your meal!")
            if (waterToggle.isChecked) scheduleNotification(2, waterHour, waterMinute, "Water Reminder", "Don't forget to log your water intake!")
            if (symptomToggle.isChecked) scheduleNotification(3, symptomHour, symptomMinute, "Symptom Reminder", "Time to log your symptoms!")
            Toast.makeText(this, "Reminders saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleNotification(id: Int, hour: Int, minute: Int, title: String, message: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("notifId", id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val m = String.format("%02d", minute)
        return "$h:$m $amPm"
    }
}