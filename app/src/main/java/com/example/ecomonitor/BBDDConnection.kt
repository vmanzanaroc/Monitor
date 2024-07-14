package com.example.ecomonitor

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BBDDConnection {

    private lateinit var userRef: DatabaseReference

    fun readFromDinamic(email: String, onDataChange: (Medicion?) -> Unit) {
        userRoot(email, "dinamic")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicion = snapshot.getValue(Medicion::class.java)
                onDataChange(medicion)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
            }
        })
    }

    fun readFromStatic(email: String, onDataChange: (List<Medicion>) -> Unit, onNoData: () -> Unit, limit: Int) {
        userRoot(email, "static")
        userRef.limitToLast(limit).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val mediciones = mutableListOf<Medicion>()
                    for (childSnapshot in snapshot.children) {
                        val medicion = childSnapshot.getValue(Medicion::class.java)
                        medicion?.let { mediciones.add(it) }
                    }
                    onDataChange(mediciones)
                } else {
                    onNoData()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
            }
        })
    }

    fun changeNameUsers(email: String, newUsername: String, onComplete: (Boolean) -> Unit) {
        userRoot(email, "profile")
        userRef.child("userName").setValue(newUsername)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun changeEcosysName(email: String, newEcosysname: String, onComplete: (Boolean) -> Unit) {
        userRoot(email, "profile")
        userRef.child("terrario").setValue(newEcosysname)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun updateUserName(email: String, newUsername: String, onComplete: (Boolean) -> Unit) {
        userRoot(email, "profile")
        userRef.child("userName").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userRef.child("userName").setValue(newUsername)
                        .addOnCompleteListener { task ->
                            onComplete(task.isSuccessful)
                        }
                } else {
                    onComplete(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
                onComplete(false)
            }
        })
    }


    fun readProfileData(email: String, onDataChange: (String?, String?) -> Unit) {
        userRoot(email, "profile")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("userName").getValue(String::class.java)
                val ecosystemName = snapshot.child("terrario").getValue(String::class.java)
                onDataChange(username, ecosystemName)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Fallo al leer el valor: ${error.toException()}")
                onDataChange(null, null)
            }
        })
    }


    /*
    fun readProfileData(email: String, onDataLoaded: (String, String) -> Unit) {
        userRoot(email, "profile")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.child("userName").getValue(String::class.java) ?: ""
                    val ecosystemName = snapshot.child("terrario").getValue(String::class.java) ?: ""
                    onDataLoaded(username, ecosystemName)
                } else {
                    onDataLoaded("", "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
                onDataLoaded("", "")
            }
        })
    }
     */

    private fun userRoot(email: String, child: String) {
        val username = email.substringBefore("@")
        userRef = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(username)
            .child(child)
    }
}
