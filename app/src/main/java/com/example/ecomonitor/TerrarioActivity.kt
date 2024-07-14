package com.example.ecomonitor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException

class TerrarioActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var handler: Handler
    private lateinit var bbddConnection: BBDDConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terrario)

        bbddConnection = BBDDConnection()

        val bundle = intent.extras
        val email = bundle?.getString("email")
        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)


        email?.let {
            bbddConnection.readFromDinamic(it) { medicion: Medicion? ->
                medicion?.let {
                    try {
                        updateUI(medicion)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } ?: run {
                    println("No se recibió ningún valor")
                }
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backbtn: Button = findViewById(R.id.backEcoSys)
        backbtn.setOnClickListener {
            val intent = Intent(this, EcoSysActivity::class.java)
            startActivity(intent)
            finish()
        }

        handler = Handler(Looper.getMainLooper())

        startUpdatingValues()
    }

    private fun startUpdatingValues() {
        handler.post(object : Runnable {
            override fun run() {
                GlobalScope.launch(Dispatchers.IO) {
                }
            }
        })
    }

    private fun updateUI(medicion:Medicion) {
        runOnUiThread {
            findViewById<TextView>(R.id.txtCO2Aire).text = medicion.co2.toString()
            findViewById<TextView>(R.id.txtTempAire).text = medicion.tempAire.toString()
            findViewById<TextView>(R.id.txtTempAgua).text = medicion.tempAgua.toString()
            findViewById<TextView>(R.id.txtPHAgua).text = medicion.ph.toString()
            findViewById<TextView>(R.id.txtHumAire).text = medicion.humidity.toString()
            findViewById<TextView>(R.id.txtPartAire).text = medicion.partAire.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
