package com.example.locationsharingapp.data.repo


import com.example.locationsharingappneloy.model.UserLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LocationRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    private fun usersRef() = db.collection("users")

    suspend fun updateMyLocation(lat: Double, lng: Double, accuracy: Float?) {
        val uid = auth.currentUser?.uid ?: return
        val userDoc = usersRef().document(uid)
        val base = mapOf(
            "name" to (auth.currentUser?.displayName ?: "Unknown"),
            "photoUrl" to (auth.currentUser?.photoUrl?.toString())
        )
        val location = mapOf(
            "lat" to lat,
            "lng" to lng,
            "accuracy" to (accuracy ?: 0f),
            "updatedAt" to System.currentTimeMillis()
        )
        userDoc.set(base, com.google.firebase.firestore.SetOptions.merge()).await()
        userDoc.update("location", location).await()
        // Optional heartbeat
        userDoc.update("lastActive", FieldValue.serverTimestamp())
    }

    fun observeAllUsers() = callbackFlow<List<UserLocation>> {
        val reg = usersRef().addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap?.documents?.map { doc ->
                val loc = doc.get("location") as? Map<*, *>
                UserLocation(
                    uid = doc.id,
                    name = doc.getString("name") ?: "",
                    photoUrl = doc.getString("photoUrl"),
                    lat = (loc?.get("lat") as? Number)?.toDouble(),
                    lng = (loc?.get("lng") as? Number)?.toDouble(),
                    accuracy = (loc?.get("accuracy") as? Number)?.toFloat(),
                    updateAt = (loc?.get("updatedAt") as? Number)?.toLong()
                )
            }?.sortedByDescending { it.updateAt ?: 0L } ?: emptyList()
            trySend(list).isSuccess
        }
        awaitClose { reg.remove() }
    }
}
