package com.smartfitness.app.ui.tracking.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.smartfitness.app.ui.tracking.model.RiderLocation
import javax.inject.Inject

class FirebaseDataSource @Inject constructor() {

    private val db = FirebaseDatabase.getInstance().reference

    fun updateLocation(riderId: String, location: RiderLocation) {
        db.child("riders_location")
            .child(riderId)
            .setValue(location)
    }

    fun observeLocation(riderId: String, onChange: (RiderLocation) -> Unit) {
        db.child("riders_location")
            .child(riderId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(RiderLocation::class.java)
                    data?.let(onChange)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}