package com.example.ecomonitor

import android.app.AlertDialog
import android.widget.EditText
import android.view.LayoutInflater
import android.graphics.Color
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.SimpleDateFormat
import java.util.*

class AirTempGraphicActivity : AppCompatActivity() {

    // Variables de rango personalizadas, iniciadas con valores por defecto
    private var rangoTempAire: ClosedFloatingPointRange<Float> = 20f..27f
    private var rangoCO2: ClosedFloatingPointRange<Float> = 400f..1000f
    private var rangoTempAgua: ClosedFloatingPointRange<Float> = 18f..24f
    private var rangoPH: ClosedFloatingPointRange<Float> = 4f..6f
    private var rangoHumedad: ClosedFloatingPointRange<Float> = 30f..60f
    private var rangoPartAire: ClosedFloatingPointRange<Float> = 0f..900f
    private var rangosoundIntensity: ClosedFloatingPointRange<Float> = 0f..100f
    private var rangolightIntensity: ClosedFloatingPointRange<Float> = 0f..100f

    private lateinit var bbddConnection: BBDDConnection
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var handler: Handler

    private lateinit var ValorSeleccionText: TextView
    private lateinit var horaSeleccionadaText: TextView
    private lateinit var graficaPulsada: TextView

    private lateinit var lineChartTempaire: LineChart
    private lateinit var lineChartTempagua: LineChart
    private lateinit var lineChartCO2: LineChart
    private lateinit var lineChartPH: LineChart
    private lateinit var lineChartHumidity: LineChart
    private lateinit var lineChartPartAire: LineChart
    private lateinit var lineChartsoundIntensity: LineChart
    private lateinit var lineChartlightIntensity: LineChart

    private lateinit var graficas: List<ConfigGrafica>
    private var mediciones: List<Medicion> = emptyList()

    private var email: String? = null
    private lateinit var terrarioId: String


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.g_activity_historico)

        bbddConnection = BBDDConnection()
        sharedPreferences = getSharedPreferences("eco_sys_prefs", MODE_PRIVATE)

        //Se cargan los datos guardados en sharePreferences para poder operar con los datos guardados por el usuario y que no se borren al cerrar la app
        rangoTempAire = sharedPreferences.getFloat("rango_temp_aire_min", 20f)..sharedPreferences.getFloat("rango_temp_aire_max", 27f)
        rangoCO2 = sharedPreferences.getFloat("rango_co2_min", 400f)..sharedPreferences.getFloat("rango_co2_max", 1000f)
        rangoTempAgua = sharedPreferences.getFloat("rango_temp_agua_min", 18f)..sharedPreferences.getFloat("rango_temp_agua_max", 24f)
        rangoPH = sharedPreferences.getFloat("rango_ph_min", 4f)..sharedPreferences.getFloat("rango_ph_max", 6f)
        rangoHumedad = sharedPreferences.getFloat("rango_humedad_min", 30f)..sharedPreferences.getFloat("rango_humedad_max", 60f)
        rangoPartAire = sharedPreferences.getFloat("rango_part_aire_min", 0f)..sharedPreferences.getFloat("rango_part_aire_max", 900f)
        rangosoundIntensity = sharedPreferences.getFloat("rango_ruido_min", 30f)..sharedPreferences.getFloat("rango_ruido_max", 60f)
        rangolightIntensity = sharedPreferences.getFloat("rango_luminosidad_min", 0f)..sharedPreferences.getFloat("rango_luminosidad_max", 900f)

        handler = Handler()

        val bundle = intent.extras
        email = bundle?.getString("email")
        terrarioId = bundle?.getString("terrarioId") ?: return

        //val spinner: Spinner = findViewById(R.id.spinnerOpcionesGrafica)

        lineChartTempagua = findViewById(R.id.TempAguaGrafico)
        lineChartTempaire = findViewById(R.id.TempAireGrafico)
        lineChartCO2 = findViewById(R.id.CO2Grafico)
        lineChartPH = findViewById(R.id.PHGrafico)
        lineChartHumidity = findViewById(R.id.HumidityGrafico)
        lineChartPartAire = findViewById(R.id.PartAireGrafico)
        lineChartsoundIntensity = findViewById(R.id.RuidoGrafico)
        lineChartlightIntensity = findViewById(R.id.LuminosidadGrafico)

        ValorSeleccionText = findViewById(R.id.textViewValorSeleccionado)
        horaSeleccionadaText = findViewById(R.id.textViewHoraSeleccionada)
        graficaPulsada = findViewById(R.id.whenGraphickClicked)

        //lineChartTempaire.axisRight.isEnabled = false
        //lineChartTempaire.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //lineChartTempaire.description.text = "Temperatura del aire - Últimas 24h"

        findViewById<Button>(R.id.backEcoSys).setOnClickListener {
            val intent= Intent(this, HistoricoActivity::class.java).apply {
                putExtra("email", email)
                putExtra("terrarioId", terrarioId)
            }
            startActivity(intent)
            finish()
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

                findViewById<MaterialButton>(R.id.btn_tempAire).visibility = if (tieneTempAire) View.VISIBLE else View.GONE
                findViewById<MaterialButton>(R.id.btn_co2).visibility = if (tieneCO2) View.VISIBLE else View.GONE
                findViewById<MaterialButton>(R.id.btn_tempAgua).visibility = if (tieneTempAgua) View.VISIBLE else View.GONE
                findViewById<MaterialButton>(R.id.btn_PH).visibility = if (tienePH) View.VISIBLE else View.GONE
                findViewById<MaterialButton>(R.id.btn_humidity).visibility = if (tieneHumedad) View.VISIBLE else View.GONE
                findViewById<MaterialButton>(R.id.btn_partAire).visibility = if (tienePartAire) View.VISIBLE else View.GONE
                findViewById<MaterialButton>(R.id.btn_soundIntensity).visibility = if (tieneSonoro) View.VISIBLE else View.GONE
                findViewById<MaterialButton>(R.id.btn_ligthIntensity).visibility = if (tieneLuminico) View.VISIBLE else View.GONE

            }, {}, 1000)
        }

        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)

        toggleGroup.check(R.id.btn_tempAire)
        //lineChartTempaire.visibility = View.VISIBLE

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            // Ocultar todos los gráficos
            lineChartTempaire.visibility = View.GONE
            lineChartCO2.visibility = View.GONE
            lineChartTempagua.visibility = View.GONE
            lineChartPH.visibility = View.GONE
            lineChartHumidity.visibility = View.GONE
            lineChartPartAire.visibility = View.GONE
            lineChartsoundIntensity.visibility = View.GONE
            lineChartlightIntensity.visibility = View.GONE

            // Mostrar solo el seleccionado
            when (checkedId) {
                R.id.btn_tempAire -> lineChartTempaire.visibility = View.VISIBLE
                R.id.btn_co2 -> lineChartCO2.visibility = View.VISIBLE
                R.id.btn_tempAgua -> lineChartTempagua.visibility = View.VISIBLE
                R.id.btn_PH -> lineChartPH.visibility = View.VISIBLE
                R.id.btn_humidity -> lineChartHumidity.visibility = View.VISIBLE
                R.id.btn_partAire -> lineChartPartAire.visibility = View.VISIBLE
                R.id.btn_soundIntensity -> lineChartsoundIntensity.visibility = View.VISIBLE
                R.id.btn_ligthIntensity -> lineChartlightIntensity.visibility = View.VISIBLE
            }
        }

        val infoGraficaButton = findViewById<ImageButton>(R.id.infoButtonGrafico)


        infoGraficaButton.setOnClickListener {
            // Obtener el botón seleccionado actualmente en el toggle group
            val checkedButtonId = toggleGroup.checkedButtonId
            if (checkedButtonId != View.NO_ID) {
                showInfoDialogForChart(checkedButtonId)
            } else {
                Toast.makeText(this, "Seleccione un gráfico primero", Toast.LENGTH_SHORT).show()
            }
        }

        // Ejemplo: supongamos que ya tienes tus charts vinculados
        graficas = listOf(
            ConfigGrafica(
                chart = lineChartTempaire,
                campoY = { it.tempAire?.toFloat() },
                descripcion = "Temperatura del aire",
                unidad = "°C",
                rango = 20f..27f
            ),
            ConfigGrafica(
                chart = lineChartTempagua,
                campoY = { it.tempAgua?.toFloat() },
                descripcion = "Temperatura del agua",
                unidad = "°C",
                rango = 18f..23f
            ),
            ConfigGrafica(
                chart = lineChartCO2,
                campoY = { it.co2?.toFloat() },
                descripcion = "Dióxido de carbono CO2",
                unidad = "ppm",
                rango = 400f..1000f
            ),
            ConfigGrafica(
                chart = lineChartPH,
                campoY = { it.ph?.toFloat() },
                descripcion = "PH del agua",
                unidad = "ph",
                rango = 6f..8f
            ),
            ConfigGrafica(
                chart = lineChartPartAire,
                campoY = { it.partAire?.toFloat() },
                descripcion = "Partículas en el aire",
                unidad = "ppb",
                rango = 0f..100f
            ),
            ConfigGrafica(
                chart = lineChartHumidity,
                campoY = { it.humidity?.toFloat() },
                descripcion = "Humedad del aire",
                unidad = "%",
                rango = 30f..60f
            ),
            ConfigGrafica(
                chart = lineChartsoundIntensity,
                campoY = { it.soundIntensity?.toFloat() },
                descripcion = "Intensidad sonora",
                unidad = "%",
                rango = 0f..100f
            ),
            ConfigGrafica(
                chart = lineChartlightIntensity,
                campoY = { it.lightIntensity?.toFloat() },
                descripcion = "Intensidad lumínica",
                unidad = "%",
                rango = 0f..100f
            )
        )

        val infoButton = findViewById<ImageButton>(R.id.configButton)
        infoButton.setOnClickListener {
            val graficaVisible = graficas.find { it.chart.visibility == View.VISIBLE }
            if (graficaVisible != null) {
                abrirDialogoCambiarRangos(graficaVisible.chart, graficaVisible.descripcion)
            } else {
                Toast.makeText(this, "No hay gráfica visible para configurar el rango", Toast.LENGTH_SHORT).show()
            }
        }

        //probarGraficaConPrediccion()
        cargarRangosDesdeBBDD()
        startAutoUpdate()
    }

    fun showInfoDialogForChart(chartId: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_info_grafico)
        dialog.setCancelable(true)

        val titleView = dialog.findViewById<TextView>(R.id.textTitle)
        val contentView = dialog.findViewById<TextView>(R.id.textContent)
        val closeButton = dialog.findViewById<ImageButton>(R.id.closeButton)

        // Cambia el contenido según el gráfico visible (chartId)
        when(chartId) {
            R.id.btn_tempAire -> {
                titleView.text = "Temperatura del aire"
                contentView.text = "Es la medida de la temperatura del ambiente dentro del terrario. Mantenerla en un rango adecuado es crucial para la salud y bienestar de las especies que habitan el terrario, ya que afecta su metabolismo, actividad y supervivencia."
            }
            R.id.btn_co2 -> {
                titleView.text = "Concentración de CO₂"
                contentView.text = "Representa la concentración de CO₂ en el aire dentro del terrario, medida en partes por millón (ppm). Valores elevados pueden indicar mala ventilación, lo que afecta negativamente a las plantas y animales."
            }
            R.id.btn_tempAgua -> {
                titleView.text = "Temperatura del agua"
                contentView.text = "Indica la temperatura del agua dentro del terrario. Es fundamental para mantener las condiciones ideales para especies acuáticas o semiacuáticas, ya que influye en su metabolismo y reproducción."
            }
            R.id.btn_humidity -> {
                titleView.text = "Humedad en el aire"
                contentView.text = "Mide el porcentaje de vapor de agua en el aire. Un nivel correcto es esencial para mantener el confort, evitar enfermedades y favorecer procesos biológicos en plantas y animales."
            }
            R.id.btn_PH -> {
                titleView.text = "Nivel de PH del agua"
                contentView.text = "Mide la acidez o alcalinidad del agua, en una escala de 0 a 14. Mantener un pH adecuado es vital para la salud de organismos acuáticos y para la estabilidad química del ambiente."
            }
            R.id.btn_partAire -> {
                titleView.text = "Particulas en el aire"
                contentView.text = "Indica la concentración de partículas suspendidas en el aire (como polvo o polen). Altos niveles pueden afectar la respiración y salud de los organismos dentro del terrario."
            }
            R.id.btn_ligthIntensity -> {
                titleView.text = "Intensidad lumínica"
                contentView.text = "Mide la cantidad de luz presente, en porcentaje o lux. La iluminación adecuada es necesaria para la fotosíntesis de las plantas y para el ciclo natural de vida de los organismos."
            }
            R.id.btn_soundIntensity -> {
                titleView.text = "Nivel de ruido"
                contentView.text = "Mide el nivel de ruido dentro del terrario. Excesivo ruido puede generar estrés en los animales y afectar su comportamiento y salud."
            }
            // Agrega más casos para otros botones...
            else -> {
                titleView.text = "Información"
                contentView.text = "Datos adicionales sobre el gráfico seleccionado."
            }
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun startAutoUpdate() {

        handler.postDelayed(object : Runnable {
            override fun run() {

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

                        val predTempAire = EcoSysActivity.prediccionesPorTerrarioYVariable[terrarioId]?.get("tempAire")
                        val predCO2 = EcoSysActivity.prediccionesPorTerrarioYVariable[terrarioId]?.get("co2")
                        val predTempAgua = EcoSysActivity.prediccionesPorTerrarioYVariable[terrarioId]?.get("tempAgua")
                        val predPH = EcoSysActivity.prediccionesPorTerrarioYVariable[terrarioId]?.get("ph")
                        val predHumedad = EcoSysActivity.prediccionesPorTerrarioYVariable[terrarioId]?.get("humidity")
                        val predPartAire = EcoSysActivity.prediccionesPorTerrarioYVariable[terrarioId]?.get("partAire")
                        val predSonoro = EcoSysActivity.prediccionesPorTerrarioYVariable[terrarioId]?.get("soundIntensity")
                        val predLuminico = EcoSysActivity.prediccionesPorTerrarioYVariable[terrarioId]?.get("lightIntensity")


                        // Mostrar solo las gráficas disponibles
                        if (tieneTempAire) mostrarGraficaGenerica(
                            mediciones,
                            campoY = { it.tempAire?.toFloat() },
                            rangoVerde = rangoTempAire,
                            chart = lineChartTempaire,
                            descripcion = "Temperatura del aire",
                            unidad = "°C",
                            prediccion = predTempAire
                        )
                        if (tieneCO2) mostrarGraficaGenerica(
                            mediciones,
                            campoY = { it.co2?.toFloat() },
                            rangoVerde = rangoCO2,
                            chart = lineChartCO2,
                            descripcion = "Dióxido de carbono CO2",
                            unidad = "ppm",
                            prediccion = predCO2
                        )
                        if (tieneTempAgua) mostrarGraficaGenerica(
                            mediciones,
                            campoY = { it.tempAgua?.toFloat() },
                            rangoVerde = rangoTempAgua,
                            chart = lineChartTempagua,
                            descripcion = "Temperatura del agua",
                            unidad = "°C",
                            prediccion = predTempAgua
                        )
                        if (tienePH) mostrarGraficaGenerica(
                            mediciones,
                            campoY = { it.ph?.toFloat() },
                            rangoVerde = rangoPH,
                            chart = lineChartPH,
                            descripcion = "PH del agua",
                            unidad = "ph",
                            prediccion = predPH
                        )
                        if (tieneHumedad) mostrarGraficaGenerica(
                            mediciones,
                            campoY = { it.humidity?.toFloat() },
                            rangoVerde = rangoHumedad,
                            chart = lineChartHumidity,
                            descripcion = "Humedad del aire",
                            unidad = "%",
                            prediccion = predHumedad
                        )
                        if (tienePartAire) mostrarGraficaGenerica(
                            mediciones,
                            campoY = { it.partAire?.toFloat() },
                            rangoVerde = rangoPartAire,
                            chart = lineChartPartAire,
                            descripcion = "Particulas en el aire",
                            unidad = "ppb",
                            prediccion = predPartAire
                        )
                        if (tieneSonoro) mostrarGraficaGenerica(
                            mediciones,
                            campoY = { it.soundIntensity?.toFloat() },
                            rangoVerde = rangosoundIntensity,
                            chart = lineChartsoundIntensity,
                            descripcion = "Intensidad sonora",
                            unidad = "%",
                            prediccion = predSonoro
                        )
                        if (tieneLuminico) mostrarGraficaGenerica(
                            mediciones,
                            campoY = { it.lightIntensity?.toFloat() },
                            rangoVerde = rangolightIntensity,
                            chart = lineChartlightIntensity,
                            descripcion = "Intensidad lumínica",
                            unidad = "%",
                            prediccion = predLuminico
                        )
                    }, {}, 1000)
                }

                handler.postDelayed(this, 2000)
            }
        }, 0)
    }

    private fun mostrarGraficaSegunDescripcion(descripcion: String) {
        when (descripcion) {
            "Temperatura del aire" -> mostrarGraficaGenerica(
                mediciones,
                campoY = { it.tempAire?.toFloat() },
                rangoVerde = rangoTempAire,
                chart = lineChartTempaire,
                descripcion = "Temperatura del aire",
                unidad = "°C"
            )
            "Dióxido de carbono CO2" -> mostrarGraficaGenerica(
                mediciones,
                campoY = { it.co2?.toFloat() },
                rangoVerde = rangoCO2,
                chart = lineChartCO2,
                descripcion = "Dióxido de carbono CO2",
                unidad = "ppm"
            )
            "Temperatura del agua" -> mostrarGraficaGenerica(
                mediciones,
                campoY = { it.tempAgua?.toFloat() },
                rangoVerde = rangoTempAgua,
                chart = lineChartTempagua,
                descripcion = "Temperatura del agua",
                unidad = "°C"
            )
            "PH del agua" -> mostrarGraficaGenerica(
                mediciones,
                campoY = { it.ph?.toFloat() },
                rangoVerde = rangoPH,
                chart = lineChartPH,
                descripcion = "PH del agua",
                unidad = "ph"
            )
            "Humedad del aire" -> mostrarGraficaGenerica(
                mediciones,
                campoY = { it.humidity?.toFloat() },
                rangoVerde = rangoHumedad,
                chart = lineChartHumidity,
                descripcion = "Humedad del aire",
                unidad = "%"
            )
            "Partículas en el aire" -> mostrarGraficaGenerica(
                mediciones,
                campoY = { it.partAire?.toFloat() },
                rangoVerde = rangoPartAire,
                chart = lineChartPartAire,
                descripcion = "Partículas en el aire",
                unidad = "ppb"                                                                      //"µg/m³"
            )
            "Intensidad sonora" -> mostrarGraficaGenerica(
                mediciones,
                campoY = { it.soundIntensity?.toFloat() },
                rangoVerde = rangosoundIntensity,
                chart = lineChartsoundIntensity,
                descripcion = "Intensidad sonora",
                unidad = "%"
            )
            "Intensidad lumínica" -> mostrarGraficaGenerica(
                mediciones,
                campoY = { it.lightIntensity?.toFloat() },
                rangoVerde = rangolightIntensity,
                chart = lineChartlightIntensity,
                descripcion = "Intensidad lumínica",
                unidad = "%"                                                                      //"µg/m³"
            )
        }
    }

    private fun cargarRangosDesdeBBDD() {
        val graficas = listOf(
            "Temperatura del aire",
            "Dióxido de carbono CO2",
            "Temperatura del agua",
            "PH del agua",
            "Humedad del aire",
            "Partículas en el aire",
            "Intensidad sonora",
            "Intensidad lumínica"
        )

        for (grafica in graficas) {

            email?.let {
                bbddConnection.obtenerRangoGrafica(it, terrarioId, grafica) { rango ->
                    rango?.let {
                        when (grafica) {
                            "Temperatura del aire" -> {
                                rangoTempAire = it.first..it.second
                                Log.d("RangosBBDD", "Temperatura del aire: ${rangoTempAire.start} - ${rangoTempAire.endInclusive}")
                            }
                            "Dióxido de carbono CO2" -> {
                                rangoCO2 = it.first..it.second
                                Log.d("RangosBBDD", "CO2: ${rangoCO2.start} - ${rangoCO2.endInclusive}")
                            }
                            "Temperatura del agua" -> {
                                rangoTempAgua = it.first..it.second
                                Log.d("RangosBBDD", "Temperatura del agua: ${rangoTempAgua.start} - ${rangoTempAgua.endInclusive}")
                            }
                            "PH del agua" -> {
                                rangoPH = it.first..it.second
                                Log.d("RangosBBDD", "PH: ${rangoPH.start} - ${rangoPH.endInclusive}")
                            }
                            "Humedad del aire" -> {
                                rangoHumedad = it.first..it.second
                                Log.d("RangosBBDD", "Humedad: ${rangoHumedad.start} - ${rangoHumedad.endInclusive}")
                            }
                            "Partículas en el aire" -> {
                                rangoPartAire = it.first..it.second
                                Log.d("RangosBBDD", "Partículas en el aire: ${rangoPartAire.start} - ${rangoPartAire.endInclusive}")
                            }
                            "Intensidad sonora" -> {
                                rangosoundIntensity = it.first..it.second
                                Log.d("RangosBBDD", "Intensidad sonora: ${rangosoundIntensity.start} - ${rangosoundIntensity.endInclusive}")
                            }
                            "Intensidad lumínica" -> {
                                rangolightIntensity = it.first..it.second
                                Log.d("RangosBBDD", "Intensidad lumínica: ${rangolightIntensity.start} - ${rangolightIntensity.endInclusive}")
                            }

                            else -> {}
                        }
                        // Aquí podrías actualizar la gráfica si está visible, por ejemplo:
                        // if (spinnerGraficas.selectedItem == grafica) mostrarGraficaSegunDescripcion(grafica)
                    } ?: Log.w("RangosBBDD", "No se recibió rango para la gráfica $grafica")
                }
            }
        }
    }

    private fun abrirDialogoCambiarRangos(chart: LineChart, descripcionGrafica: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_rango, null)
        val editMin = dialogView.findViewById<EditText>(R.id.editTextMin)
        val editMax = dialogView.findViewById<EditText>(R.id.editTextMax)

        // Poner los valores actuales en el diálogo
        when (descripcionGrafica) {
            "Temperatura del aire" -> {
                editMin.setText(rangoTempAire.start.toString())
                editMax.setText(rangoTempAire.endInclusive.toString())
            }
            "Dióxido de carbono CO2" -> {
                editMin.setText(rangoCO2.start.toString())
                editMax.setText(rangoCO2.endInclusive.toString())
            }
            "Temperatura del agua" -> {
                editMin.setText(rangoTempAgua.start.toString())
                editMax.setText(rangoTempAgua.endInclusive.toString())
            }
            "PH del agua" -> {
                editMin.setText(rangoPH.start.toString())
                editMax.setText(rangoPH.endInclusive.toString())
            }
            "Humedad del aire" -> {
                editMin.setText(rangoHumedad.start.toString())
                editMax.setText(rangoHumedad.endInclusive.toString())
            }
            "Partículas en el aire" -> {
                editMin.setText(rangoPartAire.start.toString())
                editMax.setText(rangoPartAire.endInclusive.toString())
            }
            "Intensidad sonora" -> {
                editMin.setText(rangosoundIntensity.start.toString())
                editMax.setText(rangosoundIntensity.endInclusive.toString())
            }
            "Intensidad lumínica" -> {
                editMin.setText(rangolightIntensity.start.toString())
                editMax.setText(rangolightIntensity.endInclusive.toString())
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Cambiar rango para $descripcionGrafica")
            .setView(dialogView)
            .setPositiveButton("Aplicar") { dialog, _ ->
                val minStr = editMin.text.toString()
                val maxStr = editMax.text.toString()
                val nuevoMin = minStr.toFloatOrNull()
                val nuevoMax = maxStr.toFloatOrNull()

                if (nuevoMin != null && nuevoMax != null && nuevoMin < nuevoMax) {
                    val prefsEditor = sharedPreferences.edit()
                    when (descripcionGrafica) {
                        "Temperatura del aire" -> {
                            rangoTempAire = nuevoMin..nuevoMax
                            prefsEditor.putFloat("rango_temp_aire_min", nuevoMin)
                            prefsEditor.putFloat("rango_temp_aire_max", nuevoMax)
                        }
                        "Dióxido de carbono CO2" -> {
                            rangoCO2 = nuevoMin..nuevoMax
                            prefsEditor.putFloat("rango_co2_min", nuevoMin)
                            prefsEditor.putFloat("rango_co2_max", nuevoMax)
                        }
                        "Temperatura del agua" -> {
                            rangoTempAgua = nuevoMin..nuevoMax
                            prefsEditor.putFloat("rango_temp_agua_min", nuevoMin)
                            prefsEditor.putFloat("rango_temp_agua_max", nuevoMax)
                        }
                        "PH del agua" -> {
                            rangoPH = nuevoMin..nuevoMax
                            prefsEditor.putFloat("rango_ph_min", nuevoMin)
                            prefsEditor.putFloat("rango_ph_max", nuevoMax)
                        }
                        "Humedad del aire" -> {
                            rangoHumedad = nuevoMin..nuevoMax
                            prefsEditor.putFloat("rango_humedad_min", nuevoMin)
                            prefsEditor.putFloat("rango_humedad_max", nuevoMax)
                        }
                        "Partículas en el aire" -> {
                            rangoPartAire = nuevoMin..nuevoMax
                            prefsEditor.putFloat("rango_part_aire_min", nuevoMin)
                            prefsEditor.putFloat("rango_part_aire_max", nuevoMax)
                        }
                        "Intensidad sonora" -> {
                            rangosoundIntensity = nuevoMin..nuevoMax
                            prefsEditor.putFloat("rango_ruido_min", nuevoMin)
                            prefsEditor.putFloat("rango_ruido_max", nuevoMax)
                        }
                        "Intensidad lumínica" -> {
                            rangolightIntensity = nuevoMin..nuevoMax
                            prefsEditor.putFloat("rango_luminosidad_min", nuevoMin)
                            prefsEditor.putFloat("rango_luminosidad_max", nuevoMax)
                        }
                    }

                    // Guardar en Firebase
                    email?.let {
                        bbddConnection.guardarRangoGrafica(it, terrarioId, descripcionGrafica, nuevoMin, nuevoMax) { success ->
                            if (success) {
                                Log.d("RangosBBDD", "Rango guardado correctamente")
                            } else {
                                Log.d("RangosBBDD", "Error guardando en base de datos")
                                Toast.makeText(this, "Error guardando en base de datos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    prefsEditor.apply()
                    mostrarGraficaSegunDescripcion(descripcionGrafica) // refrescar gráfica si quieres
                } else {
                    Toast.makeText(this, "Rango inválido. El mínimo debe ser menor que el máximo.", Toast.LENGTH_SHORT).show()
                }
            }

            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarGraficaGenerica(
        mediciones: List<Medicion>,
        campoY: (Medicion) -> Float?,
        rangoVerde: ClosedFloatingPointRange<Float>,
        chart: LineChart,
        descripcion: String,
        unidad: String,
        prediccion: List<Double>? = null  // <-- NUEVO
    ) {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

        val bloques = mutableMapOf<Long, Pair<Medicion, String>>()

        for (medicion in mediciones) {
            val dateStr = medicion.dateTime ?: continue
            val date = try { formatoEntrada.parse(dateStr) } catch (e: Exception) { null } ?: continue

            val calendar = Calendar.getInstance().apply { time = date }
            // Agrupamos por bloques de 5 minutos (por fecha y hora)
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - (calendar.get(Calendar.MINUTE) % 5))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val bloque = calendar.timeInMillis // Usamos el timestamp como clave única


            if (!bloques.containsKey(bloque)) {
                val etiqueta = formatoSalida.format(calendar.time)
                bloques[bloque] = Pair(medicion, etiqueta)
            }
        }

        val bloquesOrdenados = bloques.toSortedMap()
        val entries = mutableListOf<Entry>()
        val etiquetasX = mutableListOf<String>()
        val circleColors = mutableListOf<Int>()
        var index = 0f

        for ((_, pair) in bloquesOrdenados) {
            val medicion = pair.first
            val etiqueta = pair.second
            val valorY = campoY(medicion) ?: continue

            entries.add(Entry(index, valorY))
            etiquetasX.add(etiqueta)

            val color = if (valorY in rangoVerde) Color.parseColor("#4CAF50") else Color.RED
            circleColors.add(color)

            index++
        }

        val dataSet = LineDataSet(entries, descripcion).apply {
            color = Color.parseColor("#2196F3")
            lineWidth = 2f
            setCircleRadius(2f)
            setDrawCircles(true)
            setCircleColors(circleColors)
            setDrawValues(false)
            setDrawCircleHole(false)
        }

        // -------------------- NUEVO: crear curva de predicción --------------------
        val entriesPrediccion = mutableListOf<Entry>()
        if (!prediccion.isNullOrEmpty()) {
            for ((i, valor) in prediccion.withIndex()) {
                // Empieza justo después del último índice real
                val x = index + i
                entriesPrediccion.add(Entry(x, valor.toFloat()))
            }
        }

        val dataSetPrediccion = LineDataSet(entriesPrediccion, "Predicción").apply {
            color = Color.RED
            lineWidth = 2f
            enableDashedLine(10f, 10f, 0f)
            setCircleRadius(4f)
            setDrawCircles(true)
            setDrawCircleHole(false)
            setCircleColor(Color.parseColor("#FFA500"))
            setDrawValues(false)
        }

        // -------------------- NUEVO: combinar ambos datasets --------------------
        val lineData = LineData()
        lineData.addDataSet(dataSet) // datos reales
        if (entriesPrediccion.isNotEmpty()) {
            lineData.addDataSet(dataSetPrediccion) // curva de predicción
        }
        chart.data = lineData

        chart.apply {
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(etiquetasX)
            xAxis.labelRotationAngle = -45f
            xAxis.textSize = 14f
            this.description.text = descripcion
            description.textSize = 14f
            invalidate()
        }

//        chart.setTouchEnabled(true)
//        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
//            override fun onValueSelected(e: Entry?, h: Highlight?) {
//                ValorSeleccionText.text = "Valor: ${e?.y ?: "?"} $unidad"
//                val index = e?.x?.toInt() ?: -1
//                horaSeleccionadaText.text = if (index in etiquetasX.indices) {
//                    "Hora: ${etiquetasX[index]}"
//                } else {
//                    "Hora: -"
//                }
//                //graficaPulsada.visibility = View.GONE
//            }
//
//            override fun onNothingSelected() {
//                ValorSeleccionText.text = "Valor:"
//                horaSeleccionadaText.text = "Hora:"
//                //graficaPulsada.visibility = View.VISIBLE
//            }
//        })
        chart.setTouchEnabled(true)
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val valorFormateado = if (e != null) String.format("%.2f", e.y) else "?"
                ValorSeleccionText.text = "Valor: $valorFormateado $unidad"

                val index = e?.x?.toInt() ?: -1
                horaSeleccionadaText.text = if (index in etiquetasX.indices) {
                    "Hora: ${etiquetasX[index]}"
                } else {
                    "Hora: -"
                }
                //graficaPulsada.visibility = View.GONE
            }

            override fun onNothingSelected() {
                ValorSeleccionText.text = "Valor:"
                horaSeleccionadaText.text = "Hora:"
                //graficaPulsada.visibility = View.VISIBLE
            }
        })

    }

//    private fun probarGraficaConPrediccion() {
//        val mediciones = listOf(
//            Medicion(tempAire = 20.0, dateTime = "2025-06-21 10:00:00"),
//            Medicion(tempAire = 20.5, dateTime = "2025-06-21 10:05:00"),
//            Medicion(tempAire = 21.0, dateTime = "2025-06-21 10:10:00"),
//            Medicion(tempAire = 21.3, dateTime = "2025-06-21 10:15:00"),
//        )
//        val rangoTempAire = 19.0f..22.0f
//        val prediccion = listOf(21.5, 21.8, 22.0, 22.2)
//
//        mostrarGraficaGenerica(
//            mediciones = mediciones,
//            campoY = { it.tempAire?.toFloat() },
//            rangoVerde = rangoTempAire,
//            chart = lineChartTempaire,
//            descripcion = "Temperatura del aire",
//            unidad = "°C",
//            prediccion = prediccion
//        )
//    }



    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}