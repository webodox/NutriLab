package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        //back button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this@EditProfile, ProfileActivity::class.java))
        }

        val userId = FirebaseAuth.getInstance().currentUser?. uid ?:return
        val updateFirstName = findViewById<EditText>(R.id.updateFirstName)
        val updateLastName = findViewById<EditText>(R.id.updateLastName)
        val btnProfileUpdate = findViewById<Button>(R.id.btnProfileUpdate)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                updateFirstName.setText(document.getString("firstName"))
                updateLastName.setText(document.getString("lastName"))
            }

        btnProfileUpdate.setOnClickListener{
            val updated = hashMapOf(
                "firstName" to updateFirstName.text.toString().trim(),
                "lastName" to updateLastName.text.toString().trim()
            )
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update(updated as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@EditProfile,
                        "Profile Updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@EditProfile, ProfileActivity::class.java))
                    finish()
                }
        }

        }
        }