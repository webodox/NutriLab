package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//editing user information -mati sawadogo
class EditProfile : AppCompatActivity() {

    private lateinit var checkGluten: CheckBox
    private lateinit var checkDairy: CheckBox
    private lateinit var checkNuts: CheckBox
    private lateinit var checkShellfish: CheckBox
    private lateinit var checkEggs: CheckBox
    private lateinit var checkSoy: CheckBox
    private lateinit var checkVegetarian: CheckBox
    private lateinit var checkVegan: CheckBox
    private lateinit var userId: String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        //back button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this@EditProfile, ProfileActivity::class.java))
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updateFirstName = findViewById<EditText>(R.id.updateFirstName)
        val updateLastName = findViewById<EditText>(R.id.updateLastName)
        val btnProfileUpdate = findViewById<Button>(R.id.btnProfileUpdate)

        // allergy checkboxes
        checkGluten = findViewById(R.id.checkGluten)
        checkDairy = findViewById(R.id.checkDairy)
        checkNuts = findViewById(R.id.checkNuts)
        checkShellfish = findViewById(R.id.checkShellfish)
        checkEggs = findViewById(R.id.checkEggs)
        checkSoy = findViewById(R.id.checkSoy)
        checkVegetarian = findViewById(R.id.checkVegetarian)
        checkVegan = findViewById(R.id.checkVegan)

        //get updated information
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                updateFirstName.setText(document.getString("firstName"))
                updateLastName.setText(document.getString("lastName"))

                // load saved allergies and check the boxes
                val allergies = document.get("allergies") as? List<*> ?: emptyList<String>()
                checkGluten.isChecked = allergies.contains("Gluten Free")
                checkDairy.isChecked = allergies.contains("Dairy Free")
                checkNuts.isChecked = allergies.contains("Nut Allergy")
                checkShellfish.isChecked = allergies.contains("Shellfish Allergy")
                checkEggs.isChecked = allergies.contains("Egg Allergy")
                checkSoy.isChecked = allergies.contains("Soy Allergy")
                checkVegetarian.isChecked = allergies.contains("Vegetarian")
                checkVegan.isChecked = allergies.contains("Vegan")

                // set auto-save listeners after loading so they don't trigger on load
                setAllergyListeners()
            }

        //update database with name changes
        btnProfileUpdate.setOnClickListener {
            val updated = hashMapOf(
                "firstName" to updateFirstName.text.toString().trim(),
                "lastName" to updateLastName.text.toString().trim()
            )

            db.collection("users")
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

    // auto save allergies whenever a checkbox is tapped
    private fun setAllergyListeners() {
        val checkboxes = listOf(
            checkGluten, checkDairy, checkNuts,
            checkShellfish, checkEggs, checkSoy,
            checkVegetarian, checkVegan
        )

        checkboxes.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { _, _ ->
                saveAllergies()
            }
        }
    }

    // save current checkbox state to Firebase
    private fun saveAllergies() {
        val allergies = mutableListOf<String>()
        if (checkGluten.isChecked) allergies.add("Gluten Free")
        if (checkDairy.isChecked) allergies.add("Dairy Free")
        if (checkNuts.isChecked) allergies.add("Nut Allergy")
        if (checkShellfish.isChecked) allergies.add("Shellfish Allergy")
        if (checkEggs.isChecked) allergies.add("Egg Allergy")
        if (checkSoy.isChecked) allergies.add("Soy Allergy")
        if (checkVegetarian.isChecked) allergies.add("Vegetarian")
        if (checkVegan.isChecked) allergies.add("Vegan")

        db.collection("users")
            .document(userId)
            .update("allergies", allergies)
    }
}