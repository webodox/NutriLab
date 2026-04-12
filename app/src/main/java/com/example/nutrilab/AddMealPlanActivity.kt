package com.example.nutrilab

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import android.widget.*

//user created meal plan - mati sawadogo
class AddMealPlanActivity : AppCompatActivity() {

    private lateinit var foodsRecyclerView: RecyclerView
    private lateinit var spinnerMealType: Spinner
    private val foods = mutableListOf<MealPlanFoodItem>()
    private val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snacks")
    private var startDate: Date = Date()
    private var endDate: Date = Date()

    private var planId: String? = null
    private var editingIndex: Int = -1
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal_plan)

        planId = intent.getStringExtra("planId")
        foodsRecyclerView = findViewById(R.id.foodsRecyclerView)
        spinnerMealType = findViewById(R.id.spinnerMealType)

        val editFoodName = findViewById<EditText>(R.id.editFoodName)
        val editFoodGrams = findViewById<EditText>(R.id.editFoodGrams)
        val btnAddFood = findViewById<Button>(R.id.btnAddFood)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnStartDate = findViewById<Button>(R.id.btnStartDate)
        val btnEndDate = findViewById<Button>(R.id.btnEndDate)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        setupSpinner()

        //editing displayed contents of current meal plan
        val foodAdapter = MealPlanFoodAdapter(foods) { index ->
            val item = foods[index]

            editFoodName.setText(item.name)
            editFoodGrams.setText(item.grams.toString())
            editingIndex = index
        }

        foodsRecyclerView.layoutManager = LinearLayoutManager(this)
        foodsRecyclerView.adapter = foodAdapter

        if(planId != null){
            loadMealPlan(planId!!, foodAdapter, btnStartDate, btnEndDate)
            }

        //saving updated meal plan edits to database
        btnAddFood.setOnClickListener {
            val name = editFoodName.text.toString()
            val gramsText = editFoodGrams.text.toString()

            if (name.isEmpty() || gramsText.isEmpty()) {
                Toast.makeText(this, "Enter items.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val grams = gramsText.toIntOrNull()
            if (grams == null) {
                Toast.makeText(this, "Invalid grams.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editingIndex >= 0) {
                foods[editingIndex] = MealPlanFoodItem(name, grams)
                editingIndex = -1
            } else {
                foods.add(MealPlanFoodItem(name, grams))
            }
            foodAdapter.notifyDataSetChanged()

            editFoodName.text.clear()
            editFoodGrams.text.clear()
        }

        //date duration of meal plan
        btnStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)

                    startDate = calendar.time
                    btnStartDate.text = "${month + 1}/$day/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)

                    endDate = calendar.time
                    btnEndDate.text = "${month + 1}/$day/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //submit button
        btnSubmit.setOnClickListener {
            if(foods.isEmpty()){
                Toast.makeText(this, "Add at least one food.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveMealPlan(spinnerMealType.selectedItem.toString())
        }

        //back button
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinner() {
        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                mealTypes
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMealType.adapter = adapter
    }

    //saving the meal plan to database
    private fun saveMealPlan(mealType: String) {
        val userId = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "userId" to userId,
            "mealType" to mealType,
            "foods" to foods,
            "startDate" to startDate,
            "endDate" to endDate
        )
        if (planId != null){
            db.collection("mealPlanLog")
                .document(planId!!)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Meal plan updated.", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } else {
            db.collection("mealPlanLog")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Meal Plan Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    //display meal plan contents for user for editing
    private fun loadMealPlan(id: String, adapter: MealPlanFoodAdapter, btnStartDate: Button, btnEndDate: Button) {
        db.collection("mealPlanLog")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                val mealType = doc.getString("mealType") ?: ""
                val foodList = doc.get("foods") as? List<Map<String, Any>> ?: emptyList()

                val index = mealTypes.indexOf(mealType)
                if(index >= 0) spinnerMealType.setSelection(index)

                foods.clear()
                foods.addAll(
                    foodList.map {
                        MealPlanFoodItem(
                            name = it["name"] as String,
                            grams = (it["grams"] as Long).toInt()
                        )
                    }
                )
                adapter.notifyDataSetChanged()

                //display dates for updates/edits
                val start = doc.getDate("startDate")
                val end = doc.getDate("endDate")

                if (start != null) {
                    startDate = start
                    val cal = Calendar.getInstance()
                    cal.time = start
                    btnStartDate.text = "${cal.get(Calendar.MONTH)+1}/${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(
                        Calendar.YEAR)}"
                }

                if (end != null) {
                    endDate = end
                    val cal = Calendar.getInstance()
                    cal.time = end
                    btnEndDate.text = "${cal.get(Calendar.MONTH)+1}/${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(
                        Calendar.YEAR)}"
                }
            }
    }
}
//adapter for displaying meal plan contents
class MealPlanFoodAdapter(private val foods: List<MealPlanFoodItem>, private val onItemClick: (Int) -> Unit):
    RecyclerView.Adapter<MealPlanFoodAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFood: TextView = view.findViewById(R.id.txtFoodItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal_plan_food, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = foods.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = foods[position]
        holder.txtFood.text = "• ${item.name} - ${item.grams}g"

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }
}


