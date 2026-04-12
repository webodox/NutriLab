package com.example.nutrilab

import android.content.ContentValues
import android.os.Bundle
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//user profile -mati sawadogo
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        //profile options
        val btnHome = findViewById<ImageView>(R.id.homebutton)
        btnHome.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, DashboardActivity::class.java))
        }

        val btnProfile = findViewById<ImageView>(R.id.profilebutton)
        btnProfile.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, ProfileActivity::class.java))
        }

        val btnUpdateProfile = findViewById<MaterialButton>(R.id.button8)
        btnUpdateProfile.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, EditProfile::class.java))
        }

        val btnMealPlan = findViewById<MaterialButton>(R.id.button7)
        btnMealPlan.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, MealPlanActivity::class.java))
        }
        val btnHelpSupport = findViewById<MaterialButton>(R.id.button9)
        btnHelpSupport.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, ChatbotActivity::class.java))
        }

        val btnExportData = findViewById<MaterialButton>(R.id.button6)
        btnExportData.setOnClickListener {
            exportUserData()
        }

        val btnLogOutProfile = findViewById<MaterialButton>(R.id.button10)
        btnLogOutProfile.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
            finish()
        }


        //first name display and achievements
        val firstNameText = findViewById<TextView>(R.id.firstNameText)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val firstName = document.getString("firstName") ?: ""
                firstNameText.text = firstName

                val badge = document.getString("badge") ?: "bronze"
                val badgeImage = when (badge) {
                    "silver" -> R.drawable.silv_badge
                    "gold" -> R.drawable.gold_badge
                    "platinum" -> R.drawable.plat_badge
                    "diamond" -> R.drawable.diam_badge
                    else -> R.drawable.bronz_badge
                }
                findViewById<ImageView>(R.id.achievementBadge).setImageResource(badgeImage)
            }
    }

    //export data
    private fun exportUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val repo = ExportRepository()

        repo.exportAllUserData(userId) { text ->
            val fileName = "nutrilab_export.txt"
            val textBytes = text.toByteArray()
            val resolver = contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            ) ?: run{ Toast.makeText(this, "Export failed to save to downloads.", Toast.LENGTH_SHORT).show()
                return@exportAllUserData }

            resolver.openOutputStream(uri)?.use { stream ->
                    stream.write(textBytes)
                }
            }
            Toast.makeText(this, "Export saved to downloads.", Toast.LENGTH_SHORT).show()
        }
    }



