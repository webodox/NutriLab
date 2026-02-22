package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nutrilab.data.AppDatabase
import com.example.nutrilab.data.repository.AuthRepository
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        txtWelcome.text = "Welcome!"

        val db = AppDatabase.getInstance(this)
        val repo = AuthRepository(db.userDao(), db.sessionDao())

        btnLogout.setOnClickListener {
            lifecycleScope.launch {
                repo.logout()
                startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}