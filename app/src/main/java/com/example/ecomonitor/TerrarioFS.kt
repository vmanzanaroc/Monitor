package com.example.ecomonitor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class TerrarioFS : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terrario_fs)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn: Button = findViewById(R.id.backFS)
        btn.setOnClickListener {
            val intent = Intent(this, ecoSys::class.java)
            startActivity(intent)
        }

        // Referencias a los TextViews
        val txtTempAireFS: TextView = findViewById(R.id.txtTempAireFS)
        val txtCO2AireFS: TextView = findViewById(R.id.txtCO2AireFS)
        val txtTempAguaFS: TextView = findViewById(R.id.txtTempAguaFS)
        val txtPHAguaFS: TextView = findViewById(R.id.txtPHAguaFS)

        // Obtener los datos de Firestore
        db.collection("mediciones").document("terrario").get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val temperaturaAire = document.getDouble("temperaturaAire")?.toString() ?: "Dato no disponible"
                    val calidadAire = document.getDouble("calidadAire")?.toString() ?: "Dato no disponible"
                    val temperaturaAgua = document.getDouble("temperaturaAgua")?.toString() ?: "Dato no disponible"
                    val phAgua = document.getDouble("phAgua")?.toString() ?: "Dato no disponible"

                    txtTempAireFS.text = temperaturaAire + "ºC"
                    txtCO2AireFS.text = calidadAire
                    txtTempAguaFS.text = temperaturaAgua + "ºC"
                    txtPHAguaFS.text = phAgua
                } else {
                    txtTempAireFS.text = "No se encontraron datos"
                    txtCO2AireFS.text = "No se encontraron datos"
                    txtTempAguaFS.text = "No se encontraron datos"
                    txtPHAguaFS.text = "No se encontraron datos"
                }
            }
            .addOnFailureListener { exception ->
                txtTempAireFS.text = "Error al obtener datos"
                txtCO2AireFS.text = "Error al obtener datos"
                txtTempAguaFS.text = "Error al obtener datos"
                txtPHAguaFS.text = "Error al obtener datos"
            }
    }
}



/*
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class TerrarioFS : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terrario_fs)
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

        // Referencias a los TextViews
        val txtTempAireFS: TextView = findViewById(R.id.txtTempAireFS)
        val txtCO2AireFS: TextView = findViewById(R.id.txtCO2AireFS)
        val txtTempAguaFS: TextView = findViewById(R.id.txtTempAguaFS)
        val txtPHAguaFS: TextView = findViewById(R.id.txtPHAguaFS)

        // Obtener los datos de Firestore
        db.collection("mediciones").document("terrario").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    txtTempAireFS.text = document.getString("temperaturaAire") ?: "Dato no disponible"
                    txtCO2AireFS.text = document.getString("calidadAire") ?: "Dato no disponible"
                    txtTempAguaFS.text = document.getString("temperaturaAgua") ?: "Dato no disponible"
                    txtPHAguaFS.text = document.getString("phAgua") ?: "Dato no disponible"
                } else {
                    txtTempAireFS.text = "No se encontraron datos"
                    txtCO2AireFS.text = "No se encontraron datos"
                    txtTempAguaFS.text = "No se encontraron datos"
                    txtPHAguaFS.text = "No se encontraron datos"
                }
            }
            .addOnFailureListener { exception ->
                txtTempAireFS.text = "Error al obtener datos"
                txtCO2AireFS.text = "Error al obtener datos"
                txtTempAguaFS.text = "Error al obtener datos"
                txtPHAguaFS.text = "Error al obtener datos"
            }
    }
}
 */