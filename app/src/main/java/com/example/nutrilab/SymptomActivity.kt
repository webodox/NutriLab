package com.example.nutrilab

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrilab.data.entity.SymptomLogEntity
import com.example.nutrilab.data.repository.SymptomRepository

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

        btnSubmit.setOnClickListener {
            // TODO: replace with real logged-in userId from your session
            val userId = "testUser123"

            if (selectedSymptoms.isEmpty()) {
                Toast.makeText(this, "Select at least one symptom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val log = SymptomLogEntity(
                userId = userId,
                symptoms = selectedSymptoms.toList()
            )

            SymptomRepository.addSymptomLog(
                log,
                onSuccess = {
                    Toast.makeText(this, "Symptoms saved!", Toast.LENGTH_SHORT).show()
                    selectedSymptoms.clear()
                },
                onError = { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}