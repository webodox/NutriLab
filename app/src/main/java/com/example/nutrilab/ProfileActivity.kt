package com.example.nutrilab

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        //bottom navigation bar: dashboard
        val btnHome= findViewById<ImageView>(R.id.homebutton)
        btnHome.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, DashboardActivity::class.java))
        }

        //bottom navigation bar: profile
        val btnProfile = findViewById<ImageView>(R.id.profilebutton)
        btnProfile.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, ProfileActivity::class.java))
        }

        val btnUpdateProfile = findViewById<MaterialButton>(R.id.button8)
        btnUpdateProfile.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, EditProfile::class.java))
        }

        //user first name on profile
        val firstNameText = findViewById<TextView>(R.id.firstNameText)
        val userId = FirebaseAuth.getInstance().currentUser?. uid ?:return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val firstName = document.getString("firstName") ?: ""
                firstNameText.text = firstName
            }
    }
}