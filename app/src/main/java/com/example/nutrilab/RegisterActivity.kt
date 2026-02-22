package com.example.nutrilab

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nutrilab.data.AppDatabase
import com.example.nutrilab.data.entity.UserEntity
import com.example.nutrilab.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val editFirstName = findViewById<EditText>(R.id.editFirstName)
        val editLastName = findViewById<EditText>(R.id.editLastName)
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val btnCreate = findViewById<Button>(R.id.btnCreateAccount)

        val db = AppDatabase.getInstance(this)
        val repo = AuthRepository(db.userDao(), db.sessionDao())

        btnCreate.setOnClickListener {
            val first = editFirstName.text.toString().trim()
            val last = editLastName.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val pass = editPassword.text.toString()

            if (first.isBlank() || last.isBlank() || email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = UserEntity(
                    email = email,
                    password = pass,
                    firstName = first,
                    lastName = last
                )

                val result = repo.register(user)

                if (result.isSuccess) {
                    Toast.makeText(this@RegisterActivity, "Account created! Login now.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        result.exceptionOrNull()?.message ?: "Registration failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}