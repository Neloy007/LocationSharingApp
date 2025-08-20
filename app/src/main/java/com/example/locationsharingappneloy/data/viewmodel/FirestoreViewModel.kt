package com.example.locationsharingappneloy.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.locationsharingappneloy.model.User
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    // Save or create a user document
    fun saveUser(userId: String, displayName: String, email: String, location: String) {
        val user = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "location" to location
        )
        usersCollection.document(userId).set(user)
    }

    // Get all users
    fun getAllUsers(callback: (List<User>) -> Unit) {
        usersCollection.get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<User>()
                for (document in result) {
                    val userId = document.id
                    val displayName = document.getString("displayName") ?: ""
                    val email = document.getString("email") ?: ""
                    val location = document.getString("location") ?: ""
                    userList.add(User(userId, displayName, email, location))
                }
                callback(userList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    // Update user's name and location
    fun updateUser(userId: String, displayName: String, location: String) {
        val userMap = mapOf(
            "displayName" to displayName,
            "location" to location
        )
        usersCollection.document(userId).update(userMap)
    }

    // Update only location
    fun updateUserLocation(userId: String, location: String) {
        if (userId.isEmpty()) return
        usersCollection.document(userId).update("location", location)
    }

    // Get a single user
    fun getUser(userId: String, callback: (User?) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    // Get a single user's location
    fun getUserLocation(userId: String, callback: (String) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { doc ->
                callback(doc.getString("location") ?: "")
            }
            .addOnFailureListener {
                callback("")
            }
    }
}
