package com.example.nutrilab.data.entity

data class SymptomLogEntity(
    val logId: String = "",
    val userId: String = "",
    val symptoms: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)