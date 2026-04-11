package com.example.nutrilab

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.view.*
import android.widget.*

class MealPlanActivity : AppCompatActivity() {
    private lateinit var mealPlansRecyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plan)

        mealPlansRecyclerView = findViewById(R.id.mealPlansRecyclerView)
        mealPlansRecyclerView.layoutManager= LinearLayoutManager(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnAddMealPlan)
            .setOnClickListener {
                startActivity(Intent(this, AddMealPlanActivity::class.java))
            }

        loadMealPlans()
    }

    override fun onResume() {
        super.onResume()
        loadMealPlans()
    }
    private fun loadMealPlans() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("mealPlanLog")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { docs->

                val plans = docs.map { doc ->
                    val foodList = doc.get("foods") as? List<Map<String, Any>> ?: emptyList()
                    val foods = foodList.map {
                        MealPlanFoodItem(
                            name = it["name"] as String,
                            grams = (it["grams"] as Long).toInt()
                        )
                    }


                    MealPlanItem(
                        id = doc.id,
                        mealType = doc.getString("mealType") ?: "",
                        foods = foods
                    )

                }
                mealPlansRecyclerView.adapter =
                    MealPlanAdapter(
                        plans,
                        onEdit = { plan ->
                            val intent = Intent(this, AddMealPlanActivity::class.java)
                            intent.putExtra("planId", plan.id)
                            startActivity(intent)
                        },
                        onDelete = { id ->
                            db.collection("mealPlanLog")
                                .document(id)
                                .delete()
                                .addOnSuccessListener { loadMealPlans() }
                        }

                    )
            }


    }

}

class MealPlanAdapter(private val plans: List<MealPlanItem>, private val onEdit: (MealPlanItem) -> Unit, private val onDelete: (String) -> Unit) :
    RecyclerView.Adapter<MealPlanAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMealType : TextView = view.findViewById(R.id.txtMealType)
        val txtFoods: TextView = view.findViewById(R.id.txtFoods)
        val btnEdit : Button = view.findViewById(R.id.btnEdit)
        val btnDelete : Button = view.findViewById(R.id.btnDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal_plan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = plans.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plan = plans[position]
        holder.txtMealType.text = plan.mealType

        val foodText = plan.foods.joinToString("\n"){
            "• ${it.name} - ${it.grams}g"
        }
        holder.txtFoods.text = foodText

        holder.btnEdit.setOnClickListener {
            onEdit(plan)
        }
        holder.btnDelete.setOnClickListener {
            onDelete(plan.id)
        }
    }
}

