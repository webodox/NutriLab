package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

//feedback from users -mati sawadogo
class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this@FeedbackActivity, DashboardActivity::class.java))
        }

        val subjectInput = findViewById<EditText>(R.id.subjectInput)
        val feedbackInput = findViewById<EditText>(R.id.feedbackInput)
        val btnSubmit = findViewById<Button>(R.id.btnFeedbackLog)
        val userId = FirebaseAuth.getInstance().currentUser?. uid ?:return

        btnSubmit.setOnClickListener {
            val subject = subjectInput.text.toString().trim()
            val feedback = feedbackInput.text.toString().trim()
            val wordCount = feedback.split("\\s".toRegex()).filter { it.isNotEmpty() }.size

            if (wordCount > 200) {
                Toast.makeText(this, "200 word limit.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //create collection to store feedback
            val feedbackLogs = hashMapOf(
                "userId" to userId,
                "subject" to subject,
                "description" to feedback,
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            //save feedback to database
            FirebaseFirestore.getInstance()
                .collection("feedback")
                .add(feedbackLogs)
                .addOnSuccessListener {
                    Toast.makeText(this, "Feedback sent!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@FeedbackActivity, DashboardActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message ?: "Feedback failed to submit.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}