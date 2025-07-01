package com.example.ecomonitor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import android.widget.ImageView


class TerrarioActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var bbddConnection: BBDDConnection

    private lateinit var terrarioId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terrario)

        // Formatear cada valor con 2 decimales antes de mostrarlo
        val tempAirePred = String.format("%.2f", intent.getDoubleExtra("pred_tempAire", 0.0))
        val tempAguaPred = String.format("%.2f", intent.getDoubleExtra("pred_tempAgua", 0.0))
        val co2Pred = String.format("%.2f", intent.getDoubleExtra("pred_co2", 0.0))
        val phPred = String.format("%.2f", intent.getDoubleExtra("pred_ph", 0.0))
        val humidityPred = String.format("%.2f", intent.getDoubleExtra("pred_humidity", 0.0))
        val partAirePred = String.format("%.2f", intent.getDoubleExtra("pred_partAire", 0.0))
        val soundIntensityPred = String.format("%.2f", intent.getDoubleExtra("pred_soundIntensity", 0.0))
        val lightIntensityPred = String.format("%.2f", intent.getDoubleExtra("pred_lightIntensity", 0.0))


        bbddConnection = BBDDConnection()

        val bundle = intent.extras
        val email = bundle?.getString("email")
        terrarioId = bundle?.getString("terrarioId") ?: return
        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)


//        email?.let {
//            bbddConnection.readFromDinamic(it) { medicion: Medicion? ->
//                medicion?.let {
//                    try {
//                        updateUI(medicion)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                } ?: run {
//                    println("No se recibió ningún valor")
//                }
//            }
//        }

//        email?.let { emailNonNull ->
//            bbddConnection.readFromDinamic(emailNonNull, terrarioId) { medicion: Medicion? ->
//                medicion?.let {
//                    try {
//                        updateUI(medicion)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                } ?: run {
//                    println("No se recibió ningún valor")
//                }
//            }
//        }

        email?.let { emailKey ->
            bbddConnection.readFromDinamic(emailKey, terrarioId) { medicion: Medicion? ->
                medicion?.let {
                    try {
                        updateUI(medicion)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } ?: run {
                    println("No se recibió ningún valor para el terrario $terrarioId")
                }
            }
        }

        email?.let { emailKey ->
            bbddConnection.readFromStatic(emailKey, terrarioId, { mediciones ->

                val tieneTempAire = mediciones.any { it.tempAire != null }
                val tieneCO2 = mediciones.any { it.co2 != null }
                val tieneTempAgua = mediciones.any { it.tempAgua != null }
                val tienePH = mediciones.any { it.ph != null }
                val tieneHumedad = mediciones.any { it.humidity != null }
                val tienePartAire = mediciones.any { it.partAire != null }
                val tieneSonoro = mediciones.any { it.soundIntensity != null }
                val tieneLuminico = mediciones.any { it.lightIntensity != null }

                findViewById<LinearLayout>(R.id.LLTempAire).visibility = if (tieneTempAire) View.VISIBLE else View.GONE
                findViewById<LinearLayout>(R.id.LLCO2).visibility = if (tieneCO2) View.VISIBLE else View.GONE
                findViewById<LinearLayout>(R.id.LLTempAgua).visibility = if (tieneTempAgua) View.VISIBLE else View.GONE
                findViewById<LinearLayout>(R.id.LLPHAgua).visibility = if (tienePH) View.VISIBLE else View.GONE
                findViewById<LinearLayout>(R.id.LLHumidity).visibility = if (tieneHumedad) View.VISIBLE else View.GONE
                findViewById<LinearLayout>(R.id.LLPartAire).visibility = if (tienePartAire) View.VISIBLE else View.GONE
                findViewById<LinearLayout>(R.id.LLRuido).visibility = if (tieneSonoro) View.VISIBLE else View.GONE
                findViewById<LinearLayout>(R.id.LLLuminosidad).visibility = if (tieneLuminico) View.VISIBLE else View.GONE


                if (tieneTempAire) {
                    findViewById<TextView>(R.id.txtTempAirePred).text = tempAirePred.toString()
                }
                if (tieneCO2) {
                    findViewById<TextView>(R.id.txtCO2AirePred).text = co2Pred.toString()
                }
                if (tieneTempAgua) {
                    findViewById<TextView>(R.id.txtTempAguaPred).text = tempAguaPred.toString()
                }
                if (tienePH) {
                    findViewById<TextView>(R.id.txtPHAguaPred).text = phPred.toString()
                }
                if (tieneHumedad) {
                    findViewById<TextView>(R.id.txtHumAirePred).text = humidityPred.toString()
                }
                if (tienePartAire) {
                    findViewById<TextView>(R.id.txtPartAirePred).text = partAirePred.toString()
                }
                if (tieneSonoro) {
                    findViewById<TextView>(R.id.txtRuidoPred).text = soundIntensityPred.toString()
                }
                if (tieneLuminico) {
                    findViewById<TextView>(R.id.txtLuminosidadPred).text = lightIntensityPred.toString()
                }

            }, {}, 1000)
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

        startUpdatingValues()
        //startUpdatingPrediction()
    }

    private fun startUpdatingValues() {
        val updateInterval = 2000L // cada 2 segundos

        handler.post(object : Runnable {
            override fun run() {
                val email = intent.getStringExtra("email")
                if (email != null) {
                    bbddConnection.readFromDinamic(email, terrarioId) { medicion: Medicion? ->
                        medicion?.let {
                            try {
                                updateUI(it)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } ?: run {
                            println("No se recibió ningún valor para el terrario $terrarioId")
                        }
                    }
                }
                // volver a ejecutar este runnable después del intervalo
                handler.postDelayed(this, updateInterval)
            }
        })
    }

//    private fun startUpdatingPrediction() {
//        val updateInterval = 300_000L // 5 minutos en milisegundos
//
//        handler.post(object : Runnable {
//            override fun run() {
//                val email = intent.getStringExtra("email")
//                if (email != null) {
//                    bbddConnection.obtenerPrediccion(email, terrarioId,
//                        onResult = { prediccionMap ->
//                            runOnUiThread {
//                                prediccionMap["tempAire"]?.let {
//                                    findViewById<TextView>(R.id.txtTempAirePred).text = String.format("%.2f", it)
//                                }
//                                prediccionMap["co2"]?.let {
//                                    findViewById<TextView>(R.id.txtCO2AirePred).text = String.format("%.2f", it)
//                                }
//                                prediccionMap["tempAgua"]?.let {
//                                    findViewById<TextView>(R.id.txtTempAguaPred).text = String.format("%.2f", it)
//                                }
//                                prediccionMap["ph"]?.let {
//                                    findViewById<TextView>(R.id.txtPhPred).text = String.format("%.2f", it)
//                                }
//                                prediccionMap["partAire"]?.let {
//                                    findViewById<TextView>(R.id.txtPartAirePred).text = String.format("%.2f", it)
//                                }
//                                prediccionMap["humidity"]?.let {
//                                    findViewById<TextView>(R.id.txtHumidityPred).text = String.format("%.2f", it)
//                                }
//                                // Puedes agregar más variables si las tienes
//                            }
//                        },
//                        onError = { e ->
//                            println("❌ Error al obtener predicción: ${e.message}")
//                        }
//                    )
//                }
//                handler.postDelayed(this, updateInterval)
//            }
//        })
//    }


    private fun aplicarColoresSegunAlertas(alertas: Map<String, Boolean>) {
        val colores = mapOf(
            true to getColor(android.R.color.holo_red_dark),
            false to getColor(android.R.color.black)
        )

        val hayAlertaActiva = alertas.values.any { it }  // true si alguna alerta es true

        val alertImg = findViewById<ImageView>(R.id.alertImg)
        alertImg.visibility = if (hayAlertaActiva) View.VISIBLE else View.GONE

        alertas.forEach { (clave, enAlerta) ->
            val color = colores[enAlerta] ?: getColor(android.R.color.black)

            // Mapea la clave al ID del TextView correspondiente
            val idTextView = when (clave.lowercase()) {
                "tempAire".lowercase() -> R.id.txtTempAire
                "tempAgua".lowercase() -> R.id.txtTempAgua
                "co2" -> R.id.txtCO2Aire
                "ph" -> R.id.txtPHAgua
                "humidity" -> R.id.txtHumAire
                "partAire".lowercase() -> R.id.txtPartAire
                "soundIntensity".lowercase() -> R.id.txtRuido
                "lightIntensity".lowercase() -> R.id.txtLuminosidad
                else -> null
            }

            idTextView?.let {
                findViewById<TextView>(it).setTextColor(color)
            }
        }
    }

    private fun updateUI(medicion: Medicion) {
        runOnUiThread {
            findViewById<TextView>(R.id.txtCO2Aire).text = medicion.co2.toString()
            findViewById<TextView>(R.id.txtTempAire).text = medicion.tempAire.toString()
            findViewById<TextView>(R.id.txtTempAgua).text = medicion.tempAgua.toString()
            findViewById<TextView>(R.id.txtPHAgua).text = medicion.ph.toString()
            findViewById<TextView>(R.id.txtHumAire).text = medicion.humidity.toString()
            findViewById<TextView>(R.id.txtPartAire).text = medicion.partAire.toString()
            findViewById<TextView>(R.id.txtRuido).text = medicion.soundIntensity.toString()
            findViewById<TextView>(R.id.txtLuminosidad).text = medicion.lightIntensity.toString()
        }

        val bundle = intent.extras
        val email = bundle?.getString("email")
        email?.let {
            bbddConnection.obtenerAlertasActivas(email, terrarioId,
                onResult = { alertasMap ->
                    aplicarColoresSegunAlertas(alertasMap)
                },
                onError = { e ->
                    println("❌ Error al obtener alertas: ${e.message}")
                }
            )
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}