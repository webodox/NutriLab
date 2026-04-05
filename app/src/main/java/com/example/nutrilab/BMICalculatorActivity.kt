package com.example.nutrilab

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class BMICalculatorActivity : AppCompatActivity() {

    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var activitySpinner: Spinner
    private lateinit var calculateButton: Button
    private lateinit var bmiResult: TextView
    private lateinit var bmiCategory: TextView
    private lateinit var calorieResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_calculator)

        heightInput = findViewById(R.id.heightInput)
        weightInput = findViewById(R.id.weightInput)
        ageInput = findViewById(R.id.ageInput)
        genderSpinner = findViewById(R.id.genderSpinner)
        activitySpinner = findViewById(R.id.activitySpinner)
        calculateButton = findViewById(R.id.calculateButton)
        bmiResult = findViewById(R.id.bmiResult)
        bmiCategory = findViewById(R.id.bmiCategory)
        calorieResult = findViewById(R.id.calorieResult)

        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            arrayOf("Male", "Female"))
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter

        val activityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            arrayOf("Sedentary (little/no exercise)",
                "Lightly active (1-3 days/week)",
                "Moderately active (3-5 days/week)",
                "Very active (6-7 days/week)",
                "Extra active (physical job)"))
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activitySpinner.adapter = activityAdapter

        calculateButton.setOnClickListener {
            val heightStr = heightInput.text.toString().trim()
            val weightStr = weightInput.text.toString().trim()
            val ageStr = ageInput.text.toString().trim()

            if (heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightCm = heightStr.toDoubleOrNull()
            val weightKg = weightStr.toDoubleOrNull()
            val age = ageStr.toIntOrNull()

            if (heightCm == null || weightKg == null || age == null || heightCm <= 0 || weightKg <= 0 || age <= 0) {
                Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightM = heightCm / 100.0
            val bmi = weightKg / (heightM * heightM)
            val bmiFormatted = String.format("%.1f", bmi)

            val category = when {
                bmi < 18.5 -> "Underweight"
                bmi < 25.0 -> "Normal weight"
                bmi < 30.0 -> "Overweight"
                else -> "Obese"
            }

            val isMale = genderSpinner.selectedItemPosition == 0
            val bmr = if (isMale) {
                88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age)
            } else {
                447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age)
            }

            val activityMultiplier = when (activitySpinner.selectedItemPosition) {
                0 -> 1.2
                1 -> 1.375
                2 -> 1.55
                3 -> 1.725
                4 -> 1.9
                else -> 1.2
            }

            val dailyCalories = (bmr * activityMultiplier).toInt()

            bmiResult.text = "Your BMI: $bmiFormatted"
            bmiCategory.text = "Category: $category"
            calorieResult.text = "Daily calorie needs: $dailyCalories kcal/day"
        }
    }
}