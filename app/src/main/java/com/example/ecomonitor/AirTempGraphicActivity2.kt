package com.example.ecomonitor

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.SimpleDateFormat
import java.util.*

class AirTempGraphicActivity2 : AppCompatActivity() {

    // Rango personalizados, cargados y guardados en SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    private var rangoTempAire = 20f..27f
    private var rangoCO2 = 400f..1000f
    private var rangoTempAgua = 18f..24f
    private var rangoPH = 4f..6f
    private var rangoHumedad = 30f..60f
    private var rangoPartAire = 0f..900f
    private var rangoSoundIntensity = 0f..100f
    private var rangoLightIntensity = 0f..100f

    private lateinit var bbddConnection: BBDDConnection
    private lateinit var handler: Handler

    private lateinit var lineChartTempAire: LineChart
    private lateinit var lineChartTempAgua: LineChart
    private lateinit var lineChartCO2: LineChart
    private lateinit var lineChartPH: LineChart
    private lateinit var lineChartHumidity: LineChart
    private lateinit var lineChartPartAire: LineChart
    private lateinit var lineChartSoundIntensity: LineChart
    private lateinit var lineChartLightIntensity: LineChart

    private lateinit var graficas: List<ConfigGrafica>
    private var mediciones: List<Medicion> = emptyList()

    private var email: String? = null
    private lateinit var terrarioId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.g_activity_historico)

        bbddConnection = BBDDConnection()
        sharedPreferences = getSharedPreferences("eco_sys_prefs", MODE_PRIVATE)

        // Cargar rangos guardados
        rangoTempAire = sharedPreferences.getFloat("rango_temp_aire_min", 20f)..sharedPreferences.getFloat("rango_temp_aire_max", 27f)
        rangoCO2 = sharedPreferences.getFloat("rango_co2_min", 400f)..sharedPreferences.getFloat("rango_co2_max", 1000f)
        rangoTempAgua = sharedPreferences.getFloat("rango_temp_agua_min", 18f)..sharedPreferences.getFloat("rango_temp_agua_max", 24f)
        rangoPH = sharedPreferences.getFloat("rango_ph_min", 4f)..sharedPreferences.getFloat("rango_ph_max", 6f)
        rangoHumedad = sharedPreferences.getFloat("rango_humedad_min", 30f)..sharedPreferences.getFloat("rango_humedad_max", 60f)
        rangoPartAire = sharedPreferences.getFloat("rango_part_aire_min", 0f)..sharedPreferences.getFloat("rango_part_aire_max", 900f)
        rangoSoundIntensity = sharedPreferences.getFloat("rango_ruido_min", 0f)..sharedPreferences.getFloat("rango_ruido_max", 100f)
        rangoLightIntensity = sharedPreferences.getFloat("rango_luminosidad_min", 0f)..sharedPreferences.getFloat("rango_luminosidad_max", 100f)

        handler = Handler()

        val bundle = intent.extras
        email = bundle?.getString("email")
        terrarioId = bundle?.getString("terrarioId") ?: return

        // Referenciar gráficas
        lineChartTempAgua = findViewById(R.id.TempAguaGrafico)
        lineChartTempAire = findViewById(R.id.TempAireGrafico)
        lineChartCO2 = findViewById(R.id.CO2Grafico)
        lineChartPH = findViewById(R.id.PHGrafico)
        lineChartHumidity = findViewById(R.id.HumidityGrafico)
        lineChartPartAire = findViewById(R.id.PartAireGrafico)
        lineChartSoundIntensity = findViewById(R.id.RuidoGrafico)
        lineChartLightIntensity = findViewById(R.id.LuminosidadGrafico)

        // Botón para volver atrás
        findViewById<View>(R.id.backEcoSys).setOnClickListener {
            val intent = Intent(this, HistoricoActivity::class.java).apply {
                putExtra("email", email)
                putExtra("terrarioId", terrarioId)
            }
            startActivity(intent)
            finish()
        }

        // Mostrar botones solo si hay datos
        email?.let { emailKey ->
            bbddConnection.readFromStatic(emailKey, terrarioId, { listaMediciones ->
                mediciones = listaMediciones

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

                // Mostrar gráfica por defecto
                findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup).check(R.id.btn_tempAire)

                actualizarGraficas()
            }, {}, 1000)
        }

        // Configuración de grupo toggle botones
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            ocultarTodasGraficas()

            when (checkedId) {
                R.id.btn_tempAire -> lineChartTempAire.visibility = View.VISIBLE
                R.id.btn_co2 -> lineChartCO2.visibility = View.VISIBLE
                R.id.btn_tempAgua -> lineChartTempAgua.visibility = View.VISIBLE
                R.id.btn_PH -> lineChartPH.visibility = View.VISIBLE
                R.id.btn_humidity -> lineChartHumidity.visibility = View.VISIBLE
                R.id.btn_partAire -> lineChartPartAire.visibility = View.VISIBLE
                R.id.btn_soundIntensity -> lineChartSoundIntensity.visibility = View.VISIBLE
                R.id.btn_ligthIntensity -> lineChartLightIntensity.visibility = View.VISIBLE
            }

            actualizarGraficas()
        }

        // Configuración de graficas con su lógica de extracción datos y rango inicial
        graficas = listOf(
            ConfigGrafica(lineChartTempAire, { it.tempAire?.toFloat() }, "Temperatura del aire", "°C", rangoTempAire),
            ConfigGrafica(lineChartTempAgua, { it.tempAgua?.toFloat() }, "Temperatura del agua", "°C", rangoTempAgua),
            ConfigGrafica(lineChartCO2, { it.co2?.toFloat() }, "Dióxido de carbono CO2", "ppm", rangoCO2),
            ConfigGrafica(lineChartPH, { it.ph?.toFloat() }, "PH del agua", "pH", rangoPH),
            ConfigGrafica(lineChartPartAire, { it.partAire?.toFloat() }, "Partículas en el aire", "ppb", rangoPartAire),
            ConfigGrafica(lineChartHumidity, { it.humidity?.toFloat() }, "Humedad del aire", "%", rangoHumedad),
            ConfigGrafica(lineChartSoundIntensity, { it.soundIntensity?.toFloat() }, "Intensidad sonora", "%", rangoSoundIntensity),
            ConfigGrafica(lineChartLightIntensity, { it.lightIntensity?.toFloat() }, "Intensidad lumínica", "%", rangoLightIntensity)
        )

        // Botón para abrir diálogo de configuración
        findViewById<ImageButton>(R.id.configButton).setOnClickListener {
            val graficaVisible = graficas.find { it.chart.visibility == View.VISIBLE }
            if (graficaVisible != null) {
                abrirDialogoCambiarRangos(graficaVisible)
            } else {
                Toast.makeText(this, "No hay gráfica visible para configurar el rango", Toast.LENGTH_SHORT).show()
            }
        }

        startAutoUpdate()
    }

    private fun ocultarTodasGraficas() {
        lineChartTempAire.visibility = View.GONE
        lineChartCO2.visibility = View.GONE
        lineChartTempAgua.visibility = View.GONE
        lineChartPH.visibility = View.GONE
        lineChartHumidity.visibility = View.GONE
        lineChartPartAire.visibility = View.GONE
        lineChartSoundIntensity.visibility = View.GONE
        lineChartLightIntensity.visibility = View.GONE
    }

    private fun actualizarGraficas() {
        val graficaVisible = graficas.find { it.chart.visibility == View.VISIBLE } ?: return

        val entries = mediciones.mapIndexedNotNull { index, medicion ->
            val yValue = graficaVisible.campoY(medicion) ?: return@mapIndexedNotNull null
            Entry(index.toFloat(), yValue)
        }

        val dataSet = LineDataSet(entries, "${graficaVisible.descripcion} (${graficaVisible.unidad})")
        dataSet.color = android.graphics.Color.BLUE
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 2f

        val lineData = LineData(dataSet)
        graficaVisible.chart.data = lineData

        // Configurar ejes y rango
        val yAxis = graficaVisible.chart.axisLeft
        yAxis.axisMinimum = graficaVisible.rango.start
        yAxis.axisMaximum = graficaVisible.rango.endInclusive

        graficaVisible.chart.axisRight.isEnabled = false

        graficaVisible.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        graficaVisible.chart.xAxis.setDrawGridLines(false)
        graficaVisible.chart.xAxis.granularity = 1f
        graficaVisible.chart.xAxis.valueFormatter = IndexAxisValueFormatter(
            mediciones.map { SimpleDateFormat("HH:mm", Locale.getDefault()).format(it.dateTime) }
        )

        graficaVisible.chart.legend.form = Legend.LegendForm.LINE
        graficaVisible.chart.description.text = graficaVisible.descripcion
        graficaVisible.chart.invalidate()
    }

    private fun abrirDialogoCambiarRangos(grafica: ConfigGrafica) {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_rango, null)

        val etMin = dialogView.findViewById<EditText>(R.id.editTextMin)
        val etMax = dialogView.findViewById<EditText>(R.id.editTextMax)

        etMin.setText(grafica.rango.start.toString())
        etMax.setText(grafica.rango.endInclusive.toString())

        AlertDialog.Builder(this)
            .setTitle("Cambiar rango - ${grafica.descripcion}")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoMin = etMin.text.toString().toFloatOrNull()
                val nuevoMax = etMax.text.toString().toFloatOrNull()

                if (nuevoMin == null || nuevoMax == null || nuevoMin >= nuevoMax) {
                    Toast.makeText(this, "Rango inválido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Guardar el nuevo rango
                guardarRangoEnPrefs(grafica.descripcion, nuevoMin, nuevoMax)

                // Actualizar el rango en el objeto
                grafica.rango = nuevoMin..nuevoMax

                // Refrescar gráfica
                actualizarGraficas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarRangoEnPrefs(descripcion: String, min: Float, max: Float) {
        val editor = sharedPreferences.edit()
        when (descripcion) {
            "Temperatura del aire" -> {
                editor.putFloat("rango_temp_aire_min", min)
                editor.putFloat("rango_temp_aire_max", max)
                rangoTempAire = min..max
            }
            "Dióxido de carbono CO2" -> {
                editor.putFloat("rango_co2_min", min)
                editor.putFloat("rango_co2_max", max)
                rangoCO2 = min..max
            }
            "Temperatura del agua" -> {
                editor.putFloat("rango_temp_agua_min", min)
                editor.putFloat("rango_temp_agua_max", max)
                rangoTempAgua = min..max
            }
            "PH del agua" -> {
                editor.putFloat("rango_ph_min", min)
                editor.putFloat("rango_ph_max", max)
                rangoPH = min..max
            }
            "Humedad del aire" -> {
                editor.putFloat("rango_humedad_min", min)
                editor.putFloat("rango_humedad_max", max)
                rangoHumedad = min..max
            }
            "Partículas en el aire" -> {
                editor.putFloat("rango_part_aire_min", min)
                editor.putFloat("rango_part_aire_max", max)
                rangoPartAire = min..max
            }
            "Intensidad sonora" -> {
                editor.putFloat("rango_ruido_min", min)
                editor.putFloat("rango_ruido_max", max)
                rangoSoundIntensity = min..max
            }
            "Intensidad lumínica" -> {
                editor.putFloat("rango_luminosidad_min", min)
                editor.putFloat("rango_luminosidad_max", max)
                rangoLightIntensity = min..max
            }
        }
        editor.apply()
    }

    private fun startAutoUpdate() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Simular actualización de datos: vuelve a leer desde BBDD y actualiza gráfico visible
                email?.let { emailKey ->
                    bbddConnection.readFromStatic(emailKey, terrarioId, { listaMediciones ->
                        mediciones = listaMediciones
                        actualizarGraficas()
                    }, {}, 1000)
                }
                handler.postDelayed(this, 60_000) // cada 60 segundos
            }
        }, 0)
    }

    // Clase para configurar cada gráfica
    data class ConfigGrafica(
        val chart: LineChart,
        val campoY: (Medicion) -> Float?,
        val descripcion: String,
        val unidad: String,
        var rango: ClosedFloatingPointRange<Float>
    )
}
