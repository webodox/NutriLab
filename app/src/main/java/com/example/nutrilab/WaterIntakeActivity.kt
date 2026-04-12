package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WaterIntakeActivity : AppCompatActivity() {

    private lateinit var waterAmountInput: EditText
    private lateinit var submitButton: Button
    private lateinit var currentIntakeText: TextView
    private lateinit var waterProgressBar: ProgressBar
    private lateinit var progressPercentText: TextView
    private lateinit var goalStatusText: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dailyGoal = 64.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_intake)

        waterAmountInput = findViewById(R.id.waterAmountInput)
        submitButton = findViewById(R.id.submitWaterButton)
        currentIntakeText = findViewById(R.id.currentIntakeText)
        waterProgressBar = findViewById(R.id.waterProgressBar)
        progressPercentText = findViewById(R.id.progressPercentText)
        goalStatusText = findViewById(R.id.goalStatusText)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this@WaterIntakeActivity, DashboardActivity::class.java))
        }

        loadTodayIntake()

        submitButton.setOnClickListener {
            val amountStr = waterAmountInput.text.toString().trim()

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid
            if (userId == null) {
                Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val waterLog = hashMapOf(
                "userId" to userId,
                "amount" to amount,
                "unit" to "oz",
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            db.collection("waterLogs")
                .add(waterLog)
                .addOnSuccessListener {
                    Toast.makeText(this, "Water intake logged!", Toast.LENGTH_SHORT).show()
                    waterAmountInput.setText("")
                    loadTodayIntake()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to log: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadTodayIntake() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("waterLogs")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                var total = 0.0
                for (doc in documents) {
                    total += doc.getDouble("amount") ?: 0.0
                }
                currentIntakeText.text = "${total} oz"
                waterProgressBar.progress = total.toInt().coerceAtMost(64)
                val percent = ((total / dailyGoal) * 100).toInt().coerceAtMost(100)
                progressPercentText.text = "$percent%"
                goalStatusText.text = when {
                    total >= dailyGoal -> "Goal reached! Great job staying hydrated!"
                    total >= dailyGoal * 0.75 -> "Almost there! Keep drinking!"
                    total >= dailyGoal * 0.5 -> "Halfway there! Keep going!"
                    else -> "Keep going! Stay hydrated."
                }
            }
    }
}