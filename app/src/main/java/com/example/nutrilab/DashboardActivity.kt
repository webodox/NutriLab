package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nutrilab.data.AppDatabase
import com.example.nutrilab.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)


        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnSymptoms = findViewById<Button>(R.id.btnSymptoms)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnHome= findViewById<ImageView>(R.id.homebutton)
        val btnProfile = findViewById<ImageView>(R.id.profilebutton)

        txtWelcome.text = "Welcome!"

        btnSymptoms.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, SymptomActivity::class.java))
        }


        btnSearch.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, SearchActivity::class.java))
        }

        //bottom navigation bar: dashboard
        btnHome.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, DashboardActivity::class.java))
        }

        //bottom navigation bar: profile
        btnProfile.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, ProfileActivity::class.java))
        }

        /*val db = AppDatabase.getInstance(this)
        val db = AppDatabase.getInstance(this)
        val repo = AuthRepository(db.userDao(), db.sessionDao())

        btnLogout.setOnClickListener {
            lifecycleScope.launch {
                repo.logout()
                startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
                finish()
            }
        }*/

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
            finish()
        }
    }
}