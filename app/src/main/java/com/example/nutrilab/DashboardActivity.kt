package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnSymptoms = findViewById<Button>(R.id.btnSymptoms)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnHome = findViewById<ImageView>(R.id.homebutton)
        val btnProfile = findViewById<ImageView>(R.id.profilebutton)
        val btnMealTracking = findViewById<Button>(R.id.btnMealTracking)
        val btnChatbot = findViewById<Button>(R.id.btnChatbot)

        txtWelcome.text = "Welcome!"

        btnSymptoms.setOnClickListener {
            startActivity(Intent(this, SymptomActivity::class.java))
        }

        btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        btnMealTracking.setOnClickListener {
            startActivity(Intent(this, MealTrackingActivity::class.java))
        }

        btnChatbot.setOnClickListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        }

        btnHome.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}