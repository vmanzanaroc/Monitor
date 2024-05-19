package com.example.ecomonitor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TerrarioFB : AppCompatActivity() {

    // Referencia a la base de datos de Firebase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terrario_fb)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn: Button = findViewById(R.id.backFS)
        btn.setOnClickListener{
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Inicializar la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().reference

        // Obtener las referencias a los TextViews
        val txtTempAire: TextView = findViewById(R.id.txtTempAireFB)
        val txtCO2Aire: TextView = findViewById(R.id.txtCO2AireFB)
        val txtTempAgua: TextView = findViewById(R.id.txtTempAguaFB)
        val txtPHAgua: TextView = findViewById(R.id.txtPHAguaFB)

        // Escuchar cambios en la base de datos de Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtener el último conjunto de datos (el último elemento del JSON)
                val ultimoDato = dataSnapshot.children.lastOrNull()?.getValue(Dato::class.java)

                // Verificar si hay datos y mostrarlos en los TextViews
                if (ultimoDato != null) {
                    txtTempAire.text = "Temperatura aire: ${ultimoDato.temperaturaAire} °C"
                    txtCO2Aire.text = "CO2 en el aire: ${ultimoDato.calidadAire} ppm"
                    txtTempAgua.text = "Temperatura del agua: ${ultimoDato.temperaturaAgua} °C"
                    txtPHAgua.text = "PH del agua: ${ultimoDato.phAgua}"
                } else {
                    // Manejar el caso en que no haya datos disponibles
                    txtTempAire.text = "No disponible"
                    txtCO2Aire.text = "No disponible"
                    txtTempAgua.text = "No disponible"
                    txtPHAgua.text = "No disponible"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de la base de datos
                // Por ejemplo, puedes mostrar un mensaje de error en caso de fallo en la conexión
            }
        })
    }
}

// Clase de modelo para representar un conjunto de datos
data class Dato(
    val calidadAire: Double = 0.0,
    val phAgua: Double = 0.0,
    val temperaturaAgua: Double = 0.0,
    val temperaturaAire: Double = 0.0
)



/*
class TerrarioFB : AppCompatActivity() {

    // Referencia a la base de datos de Firebase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terrario_fb)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn: Button = findViewById(R.id.backFB)
        btn.setOnClickListener{
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Inicializar la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance("https://ecomonitor-2024-default-rtdb.europe-west1.firebasedatabase.app/").reference

        // Obtener las referencias a los TextViews
        val txtTempAire: TextView = findViewById(R.id.txtTempAireFB)
        val txtCO2Aire: TextView = findViewById(R.id.txtCO2AireFB)
        val txtTempAgua: TextView = findViewById(R.id.txtTempAguaFB)
        val txtPHAgua: TextView = findViewById(R.id.txtPHAguaFB)

        // Escuchar cambios en la base de datos de Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtener los valores de la base de datos
                val temperaturaAire = dataSnapshot.child("temperaturaAire").getValue(String::class.java)
                val co2Aire = dataSnapshot.child("calidadAire").getValue(String::class.java)
                val temperaturaAgua = dataSnapshot.child("temperaturaAgua").getValue(String::class.java)
                val phAgua = dataSnapshot.child("phAgua").getValue(String::class.java)

                // Mostrar los valores en los TextViews
                txtTempAire.text = temperaturaAire ?: "No disponible"
                txtCO2Aire.text = co2Aire ?: "No disponible"
                txtTempAgua.text = temperaturaAgua ?: "No disponible"
                txtPHAgua.text = phAgua ?: "No disponible"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de la base de datos
                // Por ejemplo, puedes mostrar un mensaje de error en caso de fallo en la conexión
            }
        })
    }
}

 */
