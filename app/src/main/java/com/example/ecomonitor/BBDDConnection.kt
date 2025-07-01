package com.example.ecomonitor

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BBDDConnection {

    private lateinit var userRef: DatabaseReference

    fun readFromDinamic(email: String, terrarioId: String, onDataChange: (Medicion?) -> Unit){
        val databaseRef = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("$email/terrarios/$terrarioId/dinamic")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicion = snapshot.getValue(Medicion::class.java)
                onDataChange(medicion)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
            }
        })
    }

    fun readFromStatic(email: String, terrarioId: String, onDataChange: (List<Medicion>) -> Unit, onNoData: () -> Unit, limit: Int ) {
        val databaseRef = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("$email/terrarios/$terrarioId/static")

        databaseRef.limitToLast(limit).addListenerForSingleValueEvent(object : ValueEventListener {
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

    fun updateTerrarioName(email: String, terrarioId: String, newName: String, onComplete: (Boolean) -> Unit) {
        //val username = email.substringBefore("@")
        val terrarioRef = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("$email/terrarios/$terrarioId/name")
        terrarioRef.setValue(newName)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun updateUserName(email: String, newUsername: String, onComplete: (Boolean) -> Unit) {
        val username = email.substringBefore("@")
        val userNameRef = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(username)
            .child("profile")
            .child("userName")

        userNameRef.setValue(newUsername)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
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

    private fun userRoot(email: String, child: String) {
        val username = email.substringBefore("@")
        userRef = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(username)
            .child(child)
    }

    fun getTerrarios(email: String, callback: (List<TerrarioData>) -> Unit) {
        val username = email.substringBefore("@")
        val ref = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(username)
            .child("terrarios")

        ref.get().addOnSuccessListener { snapshot ->
            val terrarios = mutableListOf<TerrarioData>()

            for (terrarioSnapshot in snapshot.children) {
                val id = terrarioSnapshot.key ?: continue
                val name = terrarioSnapshot.child("name").getValue(String::class.java) ?: "Sin nombre"

                terrarios.add(TerrarioData(id, name))
            }

            callback(terrarios)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }

    fun obtenerAlertasActivas(
        email: String,
        terrarioId: String,
        onResult: (Map<String, Boolean>) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val username = email.substringBefore("@")
        val ref = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(username)
            .child("terrarios")
            .child(terrarioId)
            .child("alertasActivas")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableMapOf<String, Boolean>()
                for (child in snapshot.children) {
                    val key = child.key ?: continue
                    val value = child.getValue(Boolean::class.java) ?: false
                    result[key] = value
                }
                onResult(result)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    fun obtenerRangoGrafica(email: String,terrarioId: String, nombreGrafica: String, onComplete: (Pair<Float, Float>?) -> Unit) {
        val username = email.substringBefore("@")
        val ref = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(username)
            .child("terrarios")
            .child(terrarioId)
            .child("rangos")
            .child(nombreGrafica)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var min: Float? = null
                var max: Float? = null

                for (child in snapshot.children) {
                    val key = child.key ?: continue
                    val value = child.getValue(Float::class.java)

                    if (value != null) {
                        if (key.startsWith("min")) {
                            min = value
                        } else if (key.startsWith("max")) {
                            max = value
                        }
                    }
                }

                if (min != null && max != null) {
                    onComplete(Pair(min, max))
                } else {
                    onComplete(null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                onComplete(null)
            }
        })
    }

    fun guardarRangoGrafica(email: String,terrarioId: String, nombreGrafica: String, min: Float, max: Float, onComplete: (Boolean) -> Unit) {
        val username = email.substringBefore("@")
        val ref = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference(username)
            .child("terrarios")
            .child(terrarioId)
            .child("rangos")
            .child(nombreGrafica)

        val datos = mapOf(
            "min" to min,
            "max" to max
        )

        ref.setValue(datos).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun obtenerPrediccion(mediciones: List<Medicion>): List<Double> {
        // Ejemplo simple: tomar los últimos valores y "predecir" aumentando un poco
        val ultimosValores = mediciones.takeLast(3).mapNotNull { it.tempAire }

        // Si no hay datos, retornamos una lista vacía
        if (ultimosValores.isEmpty()) return emptyList()

        // Generar predicciones simples: último valor + un incremento pequeño constante
        val incremento = 0.3
        val predicciones = mutableListOf<Double>()
        var valorActual = ultimosValores.last()

        for (i in 1..4) { // predicción para 4 puntos futuros
            valorActual += incremento
            predicciones.add(valorActual)
        }
        return predicciones
    }

    data class TerrarioData(
        val id: String,
        val nombre: String,
    )

}
