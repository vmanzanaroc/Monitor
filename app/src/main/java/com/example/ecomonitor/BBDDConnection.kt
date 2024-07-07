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

    private fun userRoot(email: String, child: String) {
        val username = email.substringBefore("@")
        userRef = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference(username)
            .child(child)
    }
}

/*
import android.util.Log
import com.google.firebase.database.*

class FirebaseDatabaseConnection {

    private val TAG = "FirebaseDatabaseConn"
    private lateinit var database: FirebaseDatabase

    init {
        database = FirebaseDatabase.getInstance("https://tu-proyecto.firebaseio.com") // Reemplaza con tu URL de Firebase
    }

    fun readFromStatic(username: String) {
        val staticRef = database.reference.child(username).child("static")

        staticRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val co2 = childSnapshot.child("co2").getValue(Double::class.java)
                    val tempAire = childSnapshot.child("tempAire").getValue(Double::class.java)
                    val partAire = childSnapshot.child("partAire").getValue(Double::class.java)
                    val humidity = childSnapshot.child("humidity").getValue(Double::class.java)
                    val tempAgua = childSnapshot.child("tempAgua").getValue(Double::class.java)
                    val ph = childSnapshot.child("ph").getValue(Double::class.java)

                    Log.d(TAG, "CO2: $co2, Temp Aire: $tempAire, Part Aire: $partAire, Humidity: $humidity, Temp Agua: $tempAgua, pH: $ph")

                    // Aquí puedes manejar los valores, por ejemplo, asignarlos a variables globales
                    // o actualizar la interfaz de usuario
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Error al leer datos estáticos", error.toException())
            }
        })
    }
}
 */