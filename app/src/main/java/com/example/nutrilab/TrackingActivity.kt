package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

//tracking logs display with calendar -mati sawadogo
class TrackingActivity : AppCompatActivity() {
    private lateinit var calendarStrip: LinearLayout
    private lateinit var mealsRecyclerView: RecyclerView

    private lateinit var symptomsRecyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedDate: Date = Date()

    private var weekOffset = 0

    //calendar set up
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        calendarStrip = findViewById(R.id.calendarStrip)
        mealsRecyclerView = findViewById(R.id.mealsRecyclerView)
        mealsRecyclerView.layoutManager = LinearLayoutManager(this)
        mealsRecyclerView.adapter = MealLogAdapter(emptyList())
        symptomsRecyclerView = findViewById(R.id.symptomsRecyclerView)
        symptomsRecyclerView.layoutManager = LinearLayoutManager(this)
        symptomsRecyclerView.adapter = SymptomsLogAdapter(emptyList())

        setupCalendarStrip()
        loadMealsDate(selectedDate)

        //back button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this@TrackingActivity, DashboardActivity::class.java))
        }

        //previous week back button
        findViewById<ImageButton>(R.id.btnPrevWeek).setOnClickListener {
            weekOffset--
            setupCalendarStrip()
        }

        //next week back button
        findViewById<ImageButton>(R.id.btnNextWeek).setOnClickListener {
            weekOffset++
            setupCalendarStrip()
        }
    }

    //calendar weekly view
    private fun setupCalendarStrip() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

        val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

        calendarStrip.removeAllViews()

        val selectedCal = Calendar.getInstance().apply {time = selectedDate}

        for (i in 0..6) {
            val dayDate = calendar.time.clone() as Date
            val dayNum = calendar.get(Calendar.DAY_OF_MONTH)

            val dayView = LayoutInflater.from(this)
                .inflate(R.layout.calendar_day, calendarStrip, false)

            val tvLabel = dayView.findViewById<TextView>(R.id.tvDayLabel)
            val tvNum = dayView.findViewById<TextView>(R.id.tvDayNum)

            tvLabel.text = dayLabels[i]
            tvNum.text = dayNum.toString()

            //highlight day
            if (calendar.get(Calendar.DAY_OF_YEAR) == selectedCal.get(Calendar.DAY_OF_YEAR) && calendar.get(
                    Calendar.YEAR) == selectedCal.get(Calendar.YEAR)) {
                tvNum.setBackgroundResource(R.drawable.circle_selected)
                tvNum.setTextColor(resources.getColor(android.R.color.white, null))
            }

            dayView.setOnClickListener {
                selectedDate = dayDate
                setupCalendarStrip()
                loadMealsDate(selectedDate)
                loadSymptomsDate(selectedDate)
            }

            calendarStrip.addView(dayView)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    //display meals, date logged, and macronutrients
    private fun loadMealsDate(date: Date) {
        val userId = auth.currentUser?.uid

        val startOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        val endOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time

        //get meals logged
        db.collection("mealLogs")
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("timestamp", com.google.firebase.Timestamp(startOfDay))
            .whereLessThanOrEqualTo("timestamp", com.google.firebase.Timestamp(endOfDay))
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                val meals = documents.map { doc ->
                    MealLogItem(
                    name = (doc.get("foods") as? List<*>)?.joinToString(", ") ?: "Meal",
                    calories = doc.getLong("totalCalories")?.toInt() ?: 0,
                    carbs = doc.getLong("totalCarbs")?.toInt() ?: 0,
                    fat = doc.getLong("totalFat")?.toInt() ?: 0,
                    protein = doc.getLong("totalProtein")?.toInt() ?: 0,
                    timestamp = doc.getTimestamp("timestamp")?.toDate() ?: Date()
                    )
                }
                mealsRecyclerView.adapter = MealLogAdapter(meals)
            }
            .addOnFailureListener { e ->
                android.util.Log.d("MealLog", "Error: ${e.message}")
            }
    }

    //display symptoms
    private fun loadSymptomsDate(date: Date) {
        val userId = auth.currentUser?.uid

        val startOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        val endOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time

        //get symptoms logged
        db.collection("symptom_logs")
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("timestamp", com.google.firebase.Timestamp(startOfDay))
            .whereLessThanOrEqualTo("timestamp", com.google.firebase.Timestamp(endOfDay))
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                val symptoms = documents.map { doc ->
                    SymptomLogItem(
                        symptoms = doc.get("symptoms") as? List<String>? ?: emptyList(),
                        timestamp = doc.getTimestamp("timestamp")?.toDate() ?: Date()
                    )
                }
                symptomsRecyclerView.adapter = SymptomsLogAdapter(symptoms)
            }
            .addOnFailureListener { e ->
                android.util.Log.d("SymptomLog", "Error: ${e.message}")
            }

    }
}

//display logged meals and symptoms to view
data class MealLogItem(val name: String, val calories: Int, val carbs: Int, val fat: Int, val protein: Int, val timestamp: Date)
class MealLogAdapter(private val meals: List<MealLogItem>) :
        RecyclerView.Adapter<MealLogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvMealTime)
        val tvName: TextView = view.findViewById(R.id.tvMealName)
        val tvCalories : TextView = view.findViewById(R.id.tvMealCalories)
        val tvCarbs : TextView = view.findViewById(R.id.tvMealCarbs)
        val tvFat : TextView = view.findViewById(R.id.tvMealFat)
        val tvProtein : TextView = view.findViewById(R.id.tvMealProtein)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_meal_log, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = meals[position]
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        holder.tvTime.text = timeFormat.format(meal.timestamp)
        holder.tvName.text = meal.name
        holder.tvCalories.text = "Calories: ${meal.calories} kcal"
        holder.tvCarbs.text = "Carbs: ${meal.carbs} g"
        holder.tvFat.text = "Fat: ${meal.fat} g"
        holder.tvProtein.text = "Protein: ${meal.protein} g"
    }

    override fun getItemCount() = meals.size
}

data class SymptomLogItem(val symptoms: List<String>, val timestamp: Date)
class SymptomsLogAdapter(private val symptoms: List<SymptomLogItem>) :
    RecyclerView.Adapter<SymptomsLogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvSymptomTime)
        val tvNames: TextView = view.findViewById(R.id.tvSymptomNames)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_symptom_log, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symptom = symptoms[position]
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        holder.tvTime.text = timeFormat.format(symptom.timestamp)
        holder.tvNames.text = symptom.symptoms.joinToString("\n")
    }

    override fun getItemCount() = symptoms.size
}