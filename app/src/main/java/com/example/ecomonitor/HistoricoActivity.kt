package com.example.ecomonitor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoricoActivity : AppCompatActivity() {

    private lateinit var bbddConnection: BBDDConnection
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var handler: Handler

    private lateinit var temperaturaAire: LinearLayout
    private lateinit var humedadAire: LinearLayout
    private lateinit var co2Aire: LinearLayout
    private lateinit var particulasAire: LinearLayout
    private lateinit var phAgua: LinearLayout
    private lateinit var temperaturaAgua: LinearLayout
    private lateinit var checkBox: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var checkBox4: CheckBox
    private lateinit var checkBox5: CheckBox
    private lateinit var checkBox6: CheckBox

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)
        // Inicializa BBDDConnection
        bbddConnection = BBDDConnection()

        val bundle = intent.extras
        val email = bundle?.getString("email")
        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)
        

        email?.let {
            bbddConnection.readFromStatic(it, { mediciones ->
                updateUI(mediciones)
                println("Prueba 1")
                println(mediciones.component1().tempAire)
                println(mediciones.component2().tempAire)
                println(mediciones.component3().tempAire)
                println(mediciones.component4().tempAire)
                println(mediciones.component5().tempAire)
            }, {
            }, 5)
        }


        temperaturaAire = findViewById(R.id.temperaturaAire)
        humedadAire = findViewById(R.id.humedadAire)
        co2Aire = findViewById(R.id.co2Aire)
        particulasAire = findViewById(R.id.particulasAire)
        phAgua = findViewById(R.id.phAgua)
        temperaturaAgua = findViewById(R.id.temperaturaAgua)

        checkBox = findViewById(R.id.bttTempAire)
        checkBox2 = findViewById(R.id.bttCO2)
        checkBox3 = findViewById(R.id.bttHumedad)
        checkBox4 = findViewById(R.id.bttParticulas)
        checkBox5 = findViewById(R.id.bttPHAgua)
        checkBox6 = findViewById(R.id.bttTempAgua)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                temperaturaAire.visibility = View.VISIBLE
            } else {
                temperaturaAire.visibility = View.GONE
            }
        }

        checkBox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                humedadAire.visibility = View.VISIBLE
            } else {
                humedadAire.visibility = View.GONE
            }
        }

        checkBox3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                co2Aire.visibility = View.VISIBLE
            } else {
                co2Aire.visibility = View.GONE
            }
        }

        checkBox4.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                particulasAire.visibility = View.VISIBLE
            } else {
                particulasAire.visibility = View.GONE
            }
        }

        checkBox5.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                phAgua.visibility = View.VISIBLE
            } else {
                phAgua.visibility = View.GONE
            }
        }

        checkBox6.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                temperaturaAgua.visibility = View.VISIBLE
            } else {
                temperaturaAgua.visibility = View.GONE
            }
        }

        val backbtn: Button = findViewById(R.id.backEcoSys)
        backbtn.setOnClickListener {
            val intent = Intent(this, EcoSysActivity::class.java)
            startActivity(intent)
            finish()
        }

        val clearbtn: Button = findViewById(R.id.clearData)
        clearbtn.setOnClickListener {
            temperaturaAire.visibility = View.GONE
            temperaturaAgua.visibility = View.GONE
            phAgua.visibility = View.GONE
            humedadAire.visibility = View.GONE
            co2Aire.visibility = View.GONE
            particulasAire.visibility = View.GONE

            checkBox.isChecked = false
            checkBox2.isChecked = false
            checkBox3.isChecked = false
            checkBox4.isChecked = false
            checkBox5.isChecked = false
            checkBox6.isChecked = false
        }


    }

    private fun updateUI(mediciones: List<Medicion>) {
        if (mediciones.isNotEmpty()) {
            // Actualizar TextViews con los últimos 5 datos

            findViewById<TextView>(R.id.textTA1).text = mediciones.getOrNull(4)?.tempAire.toString()
            findViewById<TextView>(R.id.textTA2).text = mediciones.getOrNull(3)?.tempAire.toString()
            findViewById<TextView>(R.id.textTA3).text = mediciones.getOrNull(2)?.tempAire.toString()
            findViewById<TextView>(R.id.textTA4).text = mediciones.getOrNull(1)?.tempAire.toString()
            findViewById<TextView>(R.id.textTA5).text = mediciones.getOrNull(0)?.tempAire.toString()

            findViewById<TextView>(R.id.textHA1).text = mediciones.getOrNull(4)?.humidity.toString()
            findViewById<TextView>(R.id.textHA2).text = mediciones.getOrNull(3)?.humidity.toString()
            findViewById<TextView>(R.id.textHA3).text = mediciones.getOrNull(2)?.humidity.toString()
            findViewById<TextView>(R.id.textHA4).text = mediciones.getOrNull(1)?.humidity.toString()
            findViewById<TextView>(R.id.textHA5).text = mediciones.getOrNull(0)?.humidity.toString()

            findViewById<TextView>(R.id.textCA1).text = mediciones.getOrNull(4)?.co2.toString()
            findViewById<TextView>(R.id.textCA2).text = mediciones.getOrNull(3)?.co2.toString()
            findViewById<TextView>(R.id.textCA3).text = mediciones.getOrNull(2)?.co2.toString()
            findViewById<TextView>(R.id.textCA4).text = mediciones.getOrNull(1)?.co2.toString()
            findViewById<TextView>(R.id.textCA5).text = mediciones.getOrNull(0)?.co2.toString()

            findViewById<TextView>(R.id.textPA1).text = mediciones.getOrNull(4)?.partAire.toString()
            findViewById<TextView>(R.id.textPA2).text = mediciones.getOrNull(3)?.partAire.toString()
            findViewById<TextView>(R.id.textPA3).text = mediciones.getOrNull(2)?.partAire.toString()
            findViewById<TextView>(R.id.textPA4).text = mediciones.getOrNull(1)?.partAire.toString()
            findViewById<TextView>(R.id.textPA5).text = mediciones.getOrNull(0)?.partAire.toString()

            findViewById<TextView>(R.id.textTH1).text = mediciones.getOrNull(4)?.tempAgua.toString()
            findViewById<TextView>(R.id.textTH2).text = mediciones.getOrNull(3)?.tempAgua.toString()
            findViewById<TextView>(R.id.textTH3).text = mediciones.getOrNull(2)?.tempAgua.toString()
            findViewById<TextView>(R.id.textTH4).text = mediciones.getOrNull(1)?.tempAgua.toString()
            findViewById<TextView>(R.id.textTH5).text = mediciones.getOrNull(0)?.tempAgua.toString()

            findViewById<TextView>(R.id.textPH1).text = mediciones.getOrNull(4)?.ph.toString()
            findViewById<TextView>(R.id.textPH2).text = mediciones.getOrNull(3)?.ph.toString()
            findViewById<TextView>(R.id.textPH3).text = mediciones.getOrNull(2)?.ph.toString()
            findViewById<TextView>(R.id.textPH4).text = mediciones.getOrNull(1)?.ph.toString()
            findViewById<TextView>(R.id.textPH5).text = mediciones.getOrNull(0)?.ph.toString()
        }
    }
}
