package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nutrilab.data.AppDatabase
import com.example.nutrilab.data.repository.AuthRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoRegister = findViewById<Button>(R.id.btnGoRegister)

        val db = AppDatabase.getInstance(this)
        val repo = AuthRepository(db.userDao(), db.sessionDao())

        // Auto-skip login if session exists
        lifecycleScope.launch {
            if (repo.hasActiveSession()) {
                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                finish()
            }
        }

        // LOGIN BUTTON
        btnLogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val pass = editPassword.text.toString()

            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = repo.login(email, pass)
                if (result.isSuccess) {
                    Toast.makeText(this@MainActivity, "Login success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        result.exceptionOrNull()?.message ?: "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // CREATE ACCOUNT BUTTON
        btnGoRegister.setOnClickListener {
            Toast.makeText(this, "Opening Register...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}