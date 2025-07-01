package com.example.ecomonitor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoricoActivity : AppCompatActivity() {

    private lateinit var bbddConnection: BBDDConnection
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var handler: Handler
    private lateinit var updateRunnable: Runnable

    private lateinit var temperaturaAire: LinearLayout
    private lateinit var humedadAire: LinearLayout
    private lateinit var co2Aire: LinearLayout
    private lateinit var particulasAire: LinearLayout
    private lateinit var phAgua: LinearLayout
    private lateinit var temperaturaAgua: LinearLayout
    private lateinit var ruido: LinearLayout
    private lateinit var luminosidad: LinearLayout

    private lateinit var checkBox: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var checkBox4: CheckBox
    private lateinit var checkBox5: CheckBox
    private lateinit var checkBox6: CheckBox
    private lateinit var checkBox7: CheckBox
    private lateinit var checkBox8: CheckBox

    private lateinit var dateLayout: LinearLayout

    private var email: String? = null
    private var terrarioId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        bbddConnection = BBDDConnection()
        handler = Handler(Looper.getMainLooper())
        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)

        email = intent.extras?.getString("email")
        terrarioId = intent.extras?.getString("terrarioId") ?: return

        temperaturaAire = findViewById(R.id.temperaturaAire)
        humedadAire = findViewById(R.id.humedadAire)
        co2Aire = findViewById(R.id.co2Aire)
        particulasAire = findViewById(R.id.particulasAire)
        phAgua = findViewById(R.id.phAgua)
        temperaturaAgua = findViewById(R.id.temperaturaAgua)
        ruido = findViewById(R.id.ruido)
        luminosidad = findViewById(R.id.luminosidad)


        checkBox = findViewById(R.id.bttTempAire1)
        checkBox2 = findViewById(R.id.bttCO21)
        checkBox3 = findViewById(R.id.bttHumedad1)
        checkBox4 = findViewById(R.id.bttParticulas1)
        checkBox5 = findViewById(R.id.bttPHAgua1)
        checkBox6 = findViewById(R.id.bttTempAgua1)
        checkBox7 = findViewById(R.id.bttRuido1)
        checkBox8 = findViewById(R.id.bttLuminosidad1)

        dateLayout = findViewById(R.id.dateLayout)

        restaurarEstadoCheckboxes()

//        listOf(
//            checkBox, checkBox2, checkBox3, checkBox4,
//            checkBox5, checkBox6, checkBox7, checkBox8
//        ).forEach { it.isChecked = false }
//
//        //COMO MIS TERRARIOS NO TIENEN NI RUIDO NI LUMINOSIDAD, ESTOS CHECKBOX ESTAN A TRUE AUNQUE DESACTIVE TODOS, POR ESO NO DESAPARECE LA FECHA
//        checkBox7.isChecked = false
//        checkBox8.isChecked = false

        dateLayout = findViewById(R.id.dateLayout)

        setCheckboxListeners()

        listOf(
            temperaturaAire, humedadAire, co2Aire, particulasAire,
            phAgua, temperaturaAgua, ruido, luminosidad
        ).forEach { it.visibility = View.GONE }

        updateDateLayoutVisibility()
        setupButtons()
        setupAutoUpdate()

//        updateDateLayoutVisibility()
    }

//    private fun setupAutoUpdate() {
//        updateRunnable = object : Runnable {
//            override fun run() {
//                email?.let { emailKey ->
//                    terrarioId?.let { terrarioKey ->
//                        bbddConnection.readFromStatic(emailKey, terrarioKey, { mediciones ->
//                            updateUI(mediciones)
//                            updateVisibility(mediciones)
//                        }, {}, 1000)
//                    }
//                }
//                handler.postDelayed(this, 5000) // cada 5 segundos
//            }
//        }
//    }

    //Coge los ultimos 5 datos basandose en la fecha
    private fun setupAutoUpdate() {
        updateRunnable = object : Runnable {
            override fun run() {
                email?.let { emailKey ->
                    terrarioId?.let { terrarioKey ->
                        bbddConnection.readFromStatic(emailKey, terrarioKey, { mediciones ->

                            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                            val ultimas5 = mediciones
                                .filter { it.dateTime != null }
                                .sortedByDescending {
                                    try {
                                        formato.parse(it.dateTime)
                                    } catch (e: Exception) {
                                        Date(0)
                                    }
                                }
                                .take(5)

                            updateUI(ultimas5)
                            updateVisibility(ultimas5)

                        }, {}, 1000)
                    }
                }
                handler.postDelayed(this, 5000)
            }
        }

    }

    private fun updateVisibility(mediciones: List<Medicion>) {
        val tieneTempAire = mediciones.any { it.tempAire != null }
        val tieneCO2 = mediciones.any { it.co2 != null }
        val tieneTempAgua = mediciones.any { it.tempAgua != null }
        val tienePH = mediciones.any { it.ph != null }
        val tieneHumedad = mediciones.any { it.humidity != null }
        val tienePartAire = mediciones.any { it.partAire != null }
        val tieneSonoro = mediciones.any { it.soundIntensity != null }
        val tieneLuminico = mediciones.any { it.lightIntensity != null }

        // Mostrar u ocultar CheckBoxes
        checkBox.visibility = if (tieneTempAire) View.VISIBLE else View.GONE
        checkBox2.visibility = if (tieneCO2) View.VISIBLE else View.GONE
        checkBox3.visibility = if (tieneHumedad) View.VISIBLE else View.GONE
        checkBox4.visibility = if (tienePartAire) View.VISIBLE else View.GONE
        checkBox5.visibility = if (tienePH) View.VISIBLE else View.GONE
        checkBox6.visibility = if (tieneTempAgua) View.VISIBLE else View.GONE
        checkBox7.visibility = if (tieneSonoro) View.VISIBLE else View.GONE
        checkBox8.visibility = if (tieneLuminico) View.VISIBLE else View.GONE

        // Mostrar u ocultar LinearLayouts SOLO si hay datos Y el CheckBox está activado
        temperaturaAire.visibility = if (tieneTempAire && checkBox.isChecked) View.VISIBLE else View.GONE
        co2Aire.visibility = if (tieneCO2 && checkBox2.isChecked) View.VISIBLE else View.GONE
        humedadAire.visibility = if (tieneHumedad && checkBox3.isChecked) View.VISIBLE else View.GONE
        particulasAire.visibility = if (tienePartAire && checkBox4.isChecked) View.VISIBLE else View.GONE
        phAgua.visibility = if (tienePH && checkBox5.isChecked) View.VISIBLE else View.GONE
        temperaturaAgua.visibility = if (tieneTempAgua && checkBox6.isChecked) View.VISIBLE else View.GONE
        ruido.visibility = if (tieneSonoro && checkBox7.isChecked) View.VISIBLE else View.GONE
        luminosidad.visibility = if (tieneLuminico && checkBox8.isChecked) View.VISIBLE else View.GONE

        updateDateLayoutVisibility()
    }


    private fun setCheckboxListeners() {
        checkBox.setOnCheckedChangeListener { _, isChecked -> temperaturaAire.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateDateLayoutVisibility()
//            checkBox.isChecked = false
        }
        checkBox2.setOnCheckedChangeListener { _, isChecked -> co2Aire.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateDateLayoutVisibility()
//            checkBox2.isChecked = false
        }
        checkBox3.setOnCheckedChangeListener { _, isChecked -> humedadAire.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateDateLayoutVisibility()
//            checkBox3.isChecked = false
        }
        checkBox4.setOnCheckedChangeListener { _, isChecked -> particulasAire.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateDateLayoutVisibility()
//            checkBox4.isChecked = false
        }
        checkBox5.setOnCheckedChangeListener { _, isChecked -> phAgua.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateDateLayoutVisibility()
//            checkBox5.isChecked = false
        }
        checkBox6.setOnCheckedChangeListener { _, isChecked -> temperaturaAgua.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateDateLayoutVisibility()
//            checkBox6.isChecked = false
        }
        checkBox7.setOnCheckedChangeListener { _, isChecked -> ruido.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateDateLayoutVisibility()
//            checkBox7.isChecked = false
        }
        checkBox8.setOnCheckedChangeListener { _, isChecked -> luminosidad.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateDateLayoutVisibility()
//            checkBox8.isChecked = false
        }
    }

    private fun updateDateLayoutVisibility() {
        val anyChecked = listOf(
            checkBox, checkBox2, checkBox3, checkBox4,
            checkBox5, checkBox6, checkBox7, checkBox8
        ).any { it.isChecked }

        dateLayout.visibility = if (anyChecked) View.VISIBLE else View.GONE
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.backEcoSys).setOnClickListener {
            startActivity(Intent(this, EcoSysActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.clearData).setOnClickListener {
            listOf(
                temperaturaAire, temperaturaAgua, phAgua, humedadAire,
                co2Aire, particulasAire, ruido, luminosidad
            ).forEach { it.visibility = View.GONE }

            listOf(
                checkBox, checkBox2, checkBox3, checkBox4,
                checkBox5, checkBox6, checkBox7, checkBox8
            ).forEach { it.isChecked = false }
        }

        findViewById<Button>(R.id.showAllData).setOnClickListener {
            listOf(
                temperaturaAire, temperaturaAgua, phAgua, humedadAire,
                co2Aire, particulasAire, ruido, luminosidad
            ).forEach { it.visibility = View.VISIBLE }

            listOf(
                checkBox, checkBox2, checkBox3, checkBox4,
                checkBox5, checkBox6, checkBox7, checkBox8
            ).forEach { it.isChecked = true }
        }

        findViewById<Button>(R.id.showList).setOnClickListener {
            val layout = findViewById<LinearLayout>(R.id.linearLayoutLista)
            val btn = it as Button
            if (layout.isVisible) {
                layout.visibility = View.GONE
                btn.text = "MOSTRAR LISTA"
                btn.backgroundTintList = getColorStateList(R.color.gris)
            } else {
                layout.visibility = View.VISIBLE
                btn.text = "OCULTAR LISTA"
                btn.backgroundTintList =getColorStateList(R.color.azul)
            }
        }

        findViewById<Button>(R.id.goGraphic).setOnClickListener {
            val intent = Intent(this, AirTempGraphicActivity::class.java).apply {
                putExtra("email", email)
                putExtra("terrarioId", terrarioId)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI(mediciones: List<Medicion>) {
        if (mediciones.isEmpty()) return

        fun TextView.setMedicion(valor: Any?) {
            text = valor?.toString() ?: "-"
        }

        fun TextView.setDate(valor: Any?) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

            text = try {
                val dateParsed = inputFormat.parse(valor.toString())
                if (dateParsed != null) outputFormat.format(dateParsed) else "-"
            } catch (e: Exception) {
                "-"
            }
        }


        fun updateFive(prefix: String, valores: List<Any?>) {
            for (i in 0..4) {
                val id = resources.getIdentifier("text$prefix${i + 1}", "id", packageName)
                findViewById<TextView>(id).setMedicion(valores.getOrNull(4 - i))
            }
        }
        fun updateFiveDates(prefix: String, valores: List<Any?>) {
            for (i in 0..4) {
                val id = resources.getIdentifier("$prefix${i + 1}", "id", packageName)
                findViewById<AppCompatTextView>(id).setDate(valores.getOrNull(4 - i))
            }
        }

        updateFiveDates("date", mediciones.map { it.dateTime })

        updateFive("TA", mediciones.map { it.tempAire })
        updateFive("HA", mediciones.map { it.humidity })
        updateFive("CA", mediciones.map { it.co2 })
        updateFive("PA", mediciones.map { it.partAire })
        updateFive("TH", mediciones.map { it.tempAgua })
        updateFive("PH", mediciones.map { it.ph })
        updateFive("Ruido", mediciones.map { it.soundIntensity })
        updateFive("Luz", mediciones.map { it.lightIntensity })
    }
    private fun guardarEstadoCheckboxes() {
        val prefs = getSharedPreferences("EstadoCheckboxes", MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putBoolean("checkBox1", checkBox.isChecked)
        editor.putBoolean("checkBox2", checkBox2.isChecked)
        editor.putBoolean("checkBox3", checkBox3.isChecked)
        editor.putBoolean("checkBox4", checkBox4.isChecked)
        editor.putBoolean("checkBox5", checkBox5.isChecked)
        editor.putBoolean("checkBox6", checkBox6.isChecked)
        editor.putBoolean("checkBox7", checkBox7.isChecked)
        editor.putBoolean("checkBox8", checkBox8.isChecked)

        editor.apply()
    }

    private fun restaurarEstadoCheckboxes() {
        val prefs = getSharedPreferences("EstadoCheckboxes", MODE_PRIVATE)

        checkBox.isChecked = prefs.getBoolean("checkBox1", false)
        checkBox2.isChecked = prefs.getBoolean("checkBox2", false)
        checkBox3.isChecked = prefs.getBoolean("checkBox3", false)
        checkBox4.isChecked = prefs.getBoolean("checkBox4", false)
        checkBox5.isChecked = prefs.getBoolean("checkBox5", false)
        checkBox6.isChecked = prefs.getBoolean("checkBox6", false)
        checkBox7.isChecked = prefs.getBoolean("checkBox7", false)
        checkBox8.isChecked = prefs.getBoolean("checkBox8", false)

        // Actualizar visibilidad según los estados cargados
        updateDateLayoutVisibility()
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        guardarEstadoCheckboxes()
        handler.removeCallbacks(updateRunnable)
    }
}


//CODIGO CON RANGOS DE TIEMPO PARA MOSTRAR HISTORIAL
//
//package com.example.ecomonitor
//
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.isVisible
//import java.text.SimpleDateFormat
//import java.util.*
//
//class HistoricoActivity : AppCompatActivity() {
//
//    private lateinit var bbddConnection: BBDDConnection
//    private lateinit var sharedPreferences: SharedPreferences
//    private lateinit var handler: Handler
//    private lateinit var updateRunnable: Runnable
//
//    private lateinit var temperaturaAire: LinearLayout
//    private lateinit var humedadAire: LinearLayout
//    private lateinit var co2Aire: LinearLayout
//    private lateinit var particulasAire: LinearLayout
//    private lateinit var phAgua: LinearLayout
//    private lateinit var temperaturaAgua: LinearLayout
//    private lateinit var ruido: LinearLayout
//    private lateinit var luminosidad: LinearLayout
//
//    private lateinit var checkBox: CheckBox
//    private lateinit var checkBox2: CheckBox
//    private lateinit var checkBox3: CheckBox
//    private lateinit var checkBox4: CheckBox
//    private lateinit var checkBox5: CheckBox
//    private lateinit var checkBox6: CheckBox
//    private lateinit var checkBox7: CheckBox
//    private lateinit var checkBox8: CheckBox
//
//    private var email: String? = null
//    private var terrarioId: String? = null
//
//    // Cambia este valor para ajustar intervalo en minutos (ej: 15 o 60)
//    private val intervaloMinutos = 15
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_historico)
//
//        bbddConnection = BBDDConnection()
//        handler = Handler(Looper.getMainLooper())
//        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)
//
//        email = intent.extras?.getString("email")
//        terrarioId = intent.extras?.getString("terrarioId") ?: return
//
//        temperaturaAire = findViewById(R.id.temperaturaAire)
//        humedadAire = findViewById(R.id.humedadAire)
//        co2Aire = findViewById(R.id.co2Aire)
//        particulasAire = findViewById(R.id.particulasAire)
//        phAgua = findViewById(R.id.phAgua)
//        temperaturaAgua = findViewById(R.id.temperaturaAgua)
//        ruido = findViewById(R.id.ruido)
//        luminosidad = findViewById(R.id.luminosidad)
//
//        checkBox = findViewById(R.id.bttTempAire1)
//        checkBox2 = findViewById(R.id.bttCO21)
//        checkBox3 = findViewById(R.id.bttHumedad1)
//        checkBox4 = findViewById(R.id.bttParticulas1)
//        checkBox5 = findViewById(R.id.bttPHAgua1)
//        checkBox6 = findViewById(R.id.bttTempAgua1)
//        checkBox7 = findViewById(R.id.bttRuido1)
//        checkBox8 = findViewById(R.id.bttLuminosidad1)
//
//        setCheckboxListeners()
//        setupButtons()
//        setupAutoUpdate()
//    }
//
//    private fun setupAutoUpdate() {
//        updateRunnable = object : Runnable {
//            override fun run() {
//                email?.let { emailKey ->
//                    terrarioId?.let { terrarioKey ->
//                        bbddConnection.readFromStatic(emailKey, terrarioKey, { mediciones ->
//                            val ultimasFiltradas = filtrarPorIntervalo(mediciones, intervaloMinutos)
//                            updateUI(ultimasFiltradas)
//                            updateVisibility(ultimasFiltradas)
//                        }, {}, 1000)
//                    }
//                }
//                handler.postDelayed(this, 5000) // cada 5 segundos
//            }
//        }
//    }
//
//    private fun filtrarPorIntervalo(
//        mediciones: List<Medicion>,
//        intervaloMinutos: Int
//    ): List<Medicion> {
//        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//
//        // Ordenar las mediciones por fecha descendente (más reciente primero)
//        val medicionesOrdenadas = mediciones
//            .filter { it.dateTime != null }
//            .sortedByDescending {
//                try {
//                    formato.parse(it.dateTime)
//                } catch (e: Exception) {
//                    Date(0)
//                }
//            }
//
//        if (medicionesOrdenadas.isEmpty()) return emptyList()
//
//        val resultado = mutableListOf<Medicion>()
//        var ultimoTiempo = formato.parse(medicionesOrdenadas[0].dateTime!!)!!.time
//        resultado.add(medicionesOrdenadas[0]) // siempre agregamos el dato más reciente
//
//        for (i in 1 until medicionesOrdenadas.size) {
//            val medicion = medicionesOrdenadas[i]
//            val tiempoActual = formato.parse(medicion.dateTime!!)!!.time
//            if ((ultimoTiempo - tiempoActual) >= intervaloMinutos * 60_000) {
//                resultado.add(medicion)
//                ultimoTiempo = tiempoActual
//            }
//        }
//
//        return resultado
//    }
//
//    private fun updateVisibility(mediciones: List<Medicion>) {
//        val tieneTempAire = mediciones.any { it.tempAire != null }
//        val tieneCO2 = mediciones.any { it.co2 != null }
//        val tieneTempAgua = mediciones.any { it.tempAgua != null }
//        val tienePH = mediciones.any { it.ph != null }
//        val tieneHumedad = mediciones.any { it.humidity != null }
//        val tienePartAire = mediciones.any { it.partAire != null }
//        val tieneSonoro = mediciones.any { it.soundIntensity != null }
//        val tieneLuminico = mediciones.any { it.lightIntensity != null }
//
//        // Mostrar/Ocultar checkboxes solo si hay datos
//        checkBox.visibility = if (tieneTempAire) View.VISIBLE else View.GONE
//        checkBox2.visibility = if (tieneCO2) View.VISIBLE else View.GONE
//        checkBox3.visibility = if (tieneHumedad) View.VISIBLE else View.GONE
//        checkBox4.visibility = if (tienePartAire) View.VISIBLE else View.GONE
//        checkBox5.visibility = if (tienePH) View.VISIBLE else View.GONE
//        checkBox6.visibility = if (tieneTempAgua) View.VISIBLE else View.GONE
//        checkBox7.visibility = if (tieneSonoro) View.VISIBLE else View.GONE
//        checkBox8.visibility = if (tieneLuminico) View.VISIBLE else View.GONE
//
//        // Mostrar/Ocultar layouts según checkbox y si hay datos
//        temperaturaAire.visibility = if (tieneTempAire && checkBox.isChecked) View.VISIBLE else View.GONE
//        co2Aire.visibility = if (tieneCO2 && checkBox2.isChecked) View.VISIBLE else View.GONE
//        humedadAire.visibility = if (tieneHumedad && checkBox3.isChecked) View.VISIBLE else View.GONE
//        particulasAire.visibility = if (tienePartAire && checkBox4.isChecked) View.VISIBLE else View.GONE
//        phAgua.visibility = if (tienePH && checkBox5.isChecked) View.VISIBLE else View.GONE
//        temperaturaAgua.visibility = if (tieneTempAgua && checkBox6.isChecked) View.VISIBLE else View.GONE
//        ruido.visibility = if (tieneSonoro && checkBox7.isChecked) View.VISIBLE else View.GONE
//        luminosidad.visibility = if (tieneLuminico && checkBox8.isChecked) View.VISIBLE else View.GONE
//    }
//
//    private fun setCheckboxListeners() {
//        checkBox.setOnCheckedChangeListener { _, isChecked ->
//            temperaturaAire.visibility = if (isChecked) View.VISIBLE else View.GONE
//        }
//        checkBox2.setOnCheckedChangeListener { _, isChecked ->
//            co2Aire.visibility = if (isChecked) View.VISIBLE else View.GONE
//        }
//        checkBox3.setOnCheckedChangeListener { _, isChecked ->
//            humedadAire.visibility = if (isChecked) View.VISIBLE else View.GONE
//        }
//        checkBox4.setOnCheckedChangeListener { _, isChecked ->
//            particulasAire.visibility = if (isChecked) View.VISIBLE else View.GONE
//        }
//        checkBox5.setOnCheckedChangeListener { _, isChecked ->
//            phAgua.visibility = if (isChecked) View.VISIBLE else View.GONE
//        }
//        checkBox6.setOnCheckedChangeListener { _, isChecked ->
//            temperaturaAgua.visibility = if (isChecked) View.VISIBLE else View.GONE
//        }
//        checkBox7.setOnCheckedChangeListener { _, isChecked ->
//            ruido.visibility = if (isChecked) View.VISIBLE else View.GONE
//        }
//        checkBox8.setOnCheckedChangeListener { _, isChecked ->
//            luminosidad.visibility = if (isChecked) View.VISIBLE else View.GONE
//        }
//    }
//
//    private fun setupButtons() {
//        findViewById<Button>(R.id.backEcoSys).setOnClickListener {
//            startActivity(Intent(this, EcoSysActivity::class.java))
//            finish()
//        }
//
//        findViewById<Button>(R.id.clearData).setOnClickListener {
//            listOf(
//                temperaturaAire, temperaturaAgua, phAgua, humedadAire,
//                co2Aire, particulasAire, ruido, luminosidad
//            ).forEach { it.visibility = View.GONE }
//
//            listOf(
//                checkBox, checkBox2, checkBox3, checkBox4,
//                checkBox5, checkBox6, checkBox7, checkBox8
//            ).forEach { it.isChecked = false }
//        }
//
//        findViewById<Button>(R.id.showList).setOnClickListener {
//            val layout = findViewById<LinearLayout>(R.id.linearLayoutLista)
//            val btn = it as Button
//            if (layout.isVisible) {
//                layout.visibility = View.GONE
//                btn.text = "MOSTRAR LISTA"
//            } else {
//                layout.visibility = View.VISIBLE
//                btn.text = "OCULTAR LISTA"
//            }
//        }
//
//        findViewById<Button>(R.id.goGraphic).setOnClickListener {
//            val intent = Intent(this, AirTempGraphicActivity::class.java).apply {
//                putExtra("email", email)
//                putExtra("terrarioId", terrarioId)
//            }
//            startActivity(intent)
//            finish()
//        }
//    }
//
//    private fun updateUI(mediciones: List<Medicion>) {
//        if (mediciones.isEmpty()) return
//
//        fun TextView.setMedicion(valor: Any?) {
//            text = valor?.toString() ?: "-"
//        }
//
//        fun updateFive(prefix: String, valores: List<Any?>) {
//            for (i in 0..4) {
//                val id = resources.getIdentifier("text$prefix${i + 1}", "id", packageName)
//                findViewById<TextView>(id).setMedicion(valores.getOrNull(4 - i))
//            }
//        }
//
//        updateFive("TA", mediciones.map { it.tempAire })
//        updateFive("HA", mediciones.map { it.humidity })
//        updateFive("CA", mediciones.map { it.co2 })
//        updateFive("PA", mediciones.map { it.partAire })
//        updateFive("TH", mediciones.map { it.tempAgua })
//        updateFive("PH", mediciones.map { it.ph })
//        updateFive("Ruido", mediciones.map { it.soundIntensity })
//        updateFive("Luz", mediciones.map { it.lightIntensity })
//    }
//
//    override fun onResume() {
//        super.onResume()
//        handler.post(updateRunnable)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        handler.removeCallbacks(updateRunnable)
//    }
//}

