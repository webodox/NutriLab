package com.example.nutrilab.data.repository

import com.example.nutrilab.FirestoreService
import com.example.nutrilab.data.entity.SymptomLogEntity
import com.google.firebase.firestore.FieldValue

object SymptomRepository {

    fun addSymptomLog(
        log: SymptomLogEntity,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        FirestoreService.db.collection("symptom_logs")
            .add(
                hashMapOf(
                    "userId" to log.userId,
                    "symptoms" to log.symptoms,
                    "timestamp" to FieldValue.serverTimestamp() // better than phone time
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }
}