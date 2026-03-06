package com.example.nutrilab

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nutrilab.data.AppDatabase
import com.example.nutrilab.data.entity.SymptomLogEntity
import com.example.nutrilab.data.repository.SymptomRepository
import kotlinx.coroutines.launch

class SymptomActivity : AppCompatActivity() {

    private val selectedSymptoms = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom)

        val btnHeadache = findViewById<Button>(R.id.btnHeadache)
        val btnFatigue = findViewById<Button>(R.id.btnFatigue)
        val btnNausea = findViewById<Button>(R.id.btnNausea)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitSymptoms)

        fun toggle(symptom: String) {
            if (selectedSymptoms.contains(symptom)) selectedSymptoms.remove(symptom)
            else selectedSymptoms.add(symptom)
        }

        btnHeadache.setOnClickListener { toggle("Headache") }
        btnFatigue.setOnClickListener { toggle("Fatigue") }
        btnNausea.setOnClickListener { toggle("Nausea") }

        val db = AppDatabase.getInstance(this)
        
        btnSubmit.setOnClickListener {
            lifecycleScope.launch {
                val session = db.sessionDao().getActiveSession()
                val userId = session?.userId?.toString() ?: "anonymous"

                if (selectedSymptoms.isEmpty()) {
                    Toast.makeText(this@SymptomActivity, "Select at least one symptom", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val log = SymptomLogEntity(
                    userId = userId,
                    symptoms = selectedSymptoms.toList()
                )

                SymptomRepository.addSymptomLog(
                    log,
                    onSuccess = {
                        Toast.makeText(this@SymptomActivity, "Symptoms saved!", Toast.LENGTH_SHORT).show()
                        selectedSymptoms.clear()
                    },
                    onError = { e ->
                        Toast.makeText(this@SymptomActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}