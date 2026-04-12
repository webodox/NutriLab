package com.example.nutrilab

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//user goals/reason for joining the app -mati sawadogo
class MainGoals : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_goals)

        val selectedMainGoalButtons = mutableSetOf<Button>()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val mainGoalButtons = listOf(
            findViewById<Button>(R.id.button20),
            findViewById<Button>(R.id.button21),
            findViewById<Button>(R.id.button22),
            findViewById<Button>(R.id.button23),
            findViewById<Button>(R.id.button24),
            findViewById<Button>(R.id.button25),
            findViewById<Button>(R.id.button26),
            findViewById<Button>(R.id.button27)
        )

        //selecting the buttons
        mainGoalButtons.forEach { button ->
            button.setOnClickListener {
                if (selectedMainGoalButtons.contains(button)) {
                    selectedMainGoalButtons.remove(button)
                    button.setBackgroundResource(R.drawable.grid_goal)
                } else {
                    if (selectedMainGoalButtons.size < 3) {
                        selectedMainGoalButtons.add(button)
                        button.setBackgroundResource(R.drawable.selected_grid_goal)
                    } else {
                        Toast.makeText(
                            this,
                            "Limit of 3",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        //submit button
        findViewById<Button>(R.id.submitBtn).setOnClickListener {
            if (selectedMainGoalButtons.isEmpty()) {
                Toast.makeText(
                    this,
                    "Select at least 3 goals.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            //logging user goals into database under userId collection
            val db = FirebaseFirestore.getInstance()
            val createDate = com.google.firebase.Timestamp.now()

            selectedMainGoalButtons.forEach { button ->
                val goalName = button.text.toString()

                val goalType = when (goalName.lowercase()) {
                    "lose weight" -> "weight-loss"
                    "gain muscle" -> "weight-gain"
                    "lower sodium", "increase fiber" -> "nutrition"
                    "manage ibs", "lower cholesterol", "manage diabetes", "blood pressure" -> "health-condition"
                    else -> "general"
                }

                val goal = hashMapOf(
                    "goalName" to goalName,
                    "goalType" to goalType,
                    "createDate" to createDate
                )

                db.collection("users")
                    .document(userId)
                    .collection("goals")
                    .add(goal)
                    .addOnSuccessListener {
                        startActivity(Intent(this@MainGoals, DashboardActivity::class.java))
                    }
            }
            startActivity(Intent(this@MainGoals, DashboardActivity::class.java))
            finish()

        }
    }
}
