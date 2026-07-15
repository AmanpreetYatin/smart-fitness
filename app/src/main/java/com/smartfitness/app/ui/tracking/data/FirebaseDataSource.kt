package com.smartfitness.app.ui.tracking.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.smartfitness.app.ui.tracking.model.RiderLocation
import javax.inject.Inject

class FirebaseDataSource @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    fun updateLocation(riderId: String, location: RiderLocation) {
        db.collection("riders_location")
            .document(riderId)
            .set(location)
    }

    fun observeLocation(riderId: String, onChange: (RiderLocation) -> Unit) {
        listener = db.collection("riders_location")
            .document(riderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.toObject(RiderLocation::class.java)?.let(onChange)
            }
    }

    fun updateDestination(riderId: String, address: String, lat: Double, lng: Double) {
        db.collection("riders_location")
            .document(riderId)
            .update(
                mapOf(
                    "destinationAddress" to address,
                    "destinationLat" to lat,
                    "destinationLng" to lng
                )
            )
    }

    fun removeListener() {
        listener?.remove()
        listener = null
    }
}