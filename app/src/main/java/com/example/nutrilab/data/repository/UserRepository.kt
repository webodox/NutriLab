package com.example.nutrilab.data.repository

import com.example.nutrilab.FirestoreService
import com.example.nutrilab.data.entity.UserProfileEntity

object UserProfileRepository {

    fun saveProfile(profile: UserProfileEntity, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        FirestoreService.db.collection("users")
            .document(profile.userId)
            .set(profile)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }
}