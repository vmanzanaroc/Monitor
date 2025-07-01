package com.example.ecomonitor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONArray
import org.json.JSONObject


class EcoSysActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bbddConnection: BBDDConnection

    //eSTO ES PARTE DE LA PREDICCION CON ARIMA
    companion object {
        // Mapa con histórico por terrario y variable
        val historicoPorTerrarioYVariable: MutableMap<String, MutableMap<String, List<Double>>> = mutableMapOf()

        // Mapa con predicciones por terrario y variable
        val prediccionesPorTerrarioYVariable: MutableMap<String, MutableMap<String, List<Double>>> = mutableMapOf()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ecosys)

        bbddConnection = BBDDConnection()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = intent.extras?.getString("email")?.substringBefore("@") ?: ""

        setup(email)
        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                logOut()
            }
        })

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            //val userId = currentUser.uid
            val userId = "IVZ3Ty9mmUY3jMoPW8KWp0QyuaK2"
            // Suscribirse al topic de notificaciones personalizadas
            FirebaseMessaging.getInstance().subscribeToTopic("user_$email")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM", "✅ Suscrito al topic user_$email")
                    } else {
                        Log.e("FCM", "❌ Error al suscribirse al topic", task.exception)
                    }
                }
        }

        loadProfileData(email)
    }

    private fun setup(email: String) {
        title = "Inicio"
        val user: TextView = findViewById(R.id.username)

        bbddConnection.readProfileData(email) { username, _ ->
            runOnUiThread {
                user.text = username
            }
        }

        findViewById<ImageButton>(R.id.configureUsernameBtn).setOnClickListener {
            showUsernameDialog(email)
        }

        findViewById<TextView>(R.id.logOutButton).setOnClickListener {
            logOut()
        }

        mostrarTerrarios(email)
    }

    private fun showUsernameDialog(email: String) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle("Cambiar nombre de usuario")
            .setView(input)
            .setPositiveButton("OK") { dialog, _ ->
                val newUsername = input.text.toString()
                bbddConnection.updateUserName(email, newUsername) { success ->
                    runOnUiThread {
                        if (success) {
                            loadProfileData(email)
                            Toast.makeText(this, "Nombre de usuario actualizado con éxito", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun loadProfileData(email: String) {
        val user: TextView = findViewById(R.id.username)
        bbddConnection.readProfileData(email) { username, _ ->
            runOnUiThread {
                user.text = username
            }
        }
    }
    private fun mostrarTerrarios(email: String) {
        val linearLayoutTerrarios = findViewById<LinearLayout>(R.id.linearLayoutTerrarios)
        linearLayoutTerrarios.removeAllViews()

        bbddConnection.getTerrarios(email) { terrarios ->
            runOnUiThread {
                val inflater = layoutInflater
                for (terrario in terrarios) {
                    // Inflate el layout para cada terrario
                    val terrarioView = inflater.inflate(R.layout.item_terrario, linearLayoutTerrarios, false)

                    // Referencias a views internas
                    val tvNombre = terrarioView.findViewById<TextView>(R.id.ecosysName)
                    val btnConfig = terrarioView.findViewById<ImageButton>(R.id.configureEcosystemNameBtn)
                    val btnVariablesActuales = terrarioView.findViewById<Button>(R.id.btnVariablesActuales)
                    val btnHistorico = terrarioView.findViewById<Button>(R.id.btnHistorico)

                    // Set nombre terrario
                    tvNombre.text = terrario.nombre


                    //----------------------------------------------------------------------------------------------------------------------------------------------------
                    //----------------------------------------------------------------------------------------------------------------------------------------------------
                    //----------------------------------------------------------------------------------------------------------------------------------------------------

                    // Nuevo bloque: calcular predicciones para cada variable disponible de este terrario
                    bbddConnection.readFromStatic(email, terrario.id, { mediciones ->

                        val historicos = mutableMapOf<String, List<Double>>()
                        val predicciones = mutableMapOf<String, List<Double>>()

                        // ---- TEMP AIRE ----
                        if (mediciones.any { it.tempAire != null }) {
                            val lista = mediciones.filter { it.tempAire != null }.map { it.tempAire!! }
                            historicos["tempAire"] = lista
                            if (lista.size >= 50) {
                                pedirPrediccion(lista, 3) { pred ->
                                    predicciones["tempAire"] = pred
                                }
                            }
                        }

                        // ---- CO2 ----
                        if (mediciones.any { it.co2 != null }) {
                            val lista = mediciones.filter { it.co2 != null }.map { it.co2!! }
                            historicos["co2"] = lista
                            if (lista.size >= 50) {
                                pedirPrediccion(lista, 3) { pred ->
                                    predicciones["co2"] = pred
                                }
                            }
                        }

                        // ---- TEMP AGUA ----
                        if (mediciones.any { it.tempAgua != null }) {
                            val lista = mediciones.filter { it.tempAgua != null }.map { it.tempAgua!! }
                            historicos["tempAgua"] = lista
                            if (lista.size >= 50) {
                                pedirPrediccion(lista, 3) { pred ->
                                    predicciones["tempAgua"] = pred
                                }
                            }
                        }

                        // ---- PH ----
                        if (mediciones.any { it.ph != null }) {
                            val lista = mediciones.filter { it.ph != null }.map { it.ph!! }
                            historicos["ph"] = lista
                            if (lista.size >= 50) {
                                pedirPrediccion(lista, 3) { pred ->
                                    predicciones["ph"] = pred
                                }
                            }
                        }

                        // ---- HUMIDITY ----
                        if (mediciones.any { it.humidity != null }) {
                            val lista = mediciones.filter { it.humidity != null }.map { it.humidity!! }
                            historicos["humidity"] = lista
                            if (lista.size >= 50) {
                                pedirPrediccion(lista, 3) { pred ->
                                    predicciones["humidity"] = pred
                                }
                            }
                        }

                        // ---- PARTICULAS AIRE ----
                        if (mediciones.any { it.partAire != null }) {
                            val lista = mediciones.filter { it.partAire != null }.map { it.partAire!! }
                            historicos["partAire"] = lista
                            if (lista.size >= 50) {
                                pedirPrediccion(lista, 3) { pred ->
                                    predicciones["partAire"] = pred
                                }
                            }
                        }

                        // ---- RUIDO ----
                        if (mediciones.any { it.soundIntensity != null }) {
                            val lista = mediciones.filter { it.soundIntensity != null }.map { it.soundIntensity!! }
                            historicos["soundIntensity"] = lista
                            if (lista.size >= 50) {
                                pedirPrediccion(lista, 3) { pred ->
                                    predicciones["soundIntensity"] = pred
                                }
                            }
                        }

                        // ---- LUMINOSIDAD ----
                        if (mediciones.any { it.lightIntensity != null }) {
                            val lista = mediciones.filter { it.lightIntensity != null }.map { it.lightIntensity!! }
                            historicos["lightIntensity"] = lista
                            if (lista.size >= 50) {
                                pedirPrediccion(lista, 3) { pred ->
                                    predicciones["lightIntensity"] = pred
                                }
                            }
                        }

                        // Guardar ambos mapas en las variables globales
                        historicoPorTerrarioYVariable[terrario.id] = historicos
                        prediccionesPorTerrarioYVariable[terrario.id] = predicciones

                    }, {}, 100)

                    //----------------------------------------------------------------------------------------------------------------------------------------------------
                    //----------------------------------------------------------------------------------------------------------------------------------------------------
                    //----------------------------------------------------------------------------------------------------------------------------------------------------

                    // Listener botón configuración para cambiar nombre en BD
                    btnConfig.setOnClickListener {
                        mostrarDialogCambioNombreTerrario(email, terrario.id, tvNombre)
                    }

                    // Listener botón Variables actuales (cambiar a TerrarioActivity)
//                    btnVariablesActuales.setOnClickListener {
//                        val intent = Intent(this, TerrarioActivity::class.java).apply {
//                            putExtra("email", email)
//                            putExtra("terrarioId", terrario.id)
//                            putExtra("prediccion_inicial", primerValorPrediccion)
//                        }
//                        startActivity(intent)
//                    }

                    btnVariablesActuales.setOnClickListener {
                        //Acceder a todas las predicciones de este terrario
                        val prediccionesTerrario = prediccionesPorTerrarioYVariable[terrario.id]

                        //Variables que te interesan
                        val variables = listOf(
                            "tempAire",
                            "tempAgua",
                            "co2",
                            "ph",
                            "humidity",
                            "partAire",
                            "soundIntensity",
                            "lightIntensity"
                        )

                        //Crear Intent
                        val intent = Intent(this, TerrarioActivity::class.java).apply {
                            putExtra("email", email)
                            putExtra("terrarioId", terrario.id)

                            //Añadir cada primer valor de predicción como extra
                            for (variable in variables) {
                                val prediccion = prediccionesTerrario?.get(variable) ?: emptyList()
                                val primerValor = prediccion.firstOrNull() ?: 0.0
                                putExtra("pred_$variable", primerValor)
                            }
                        }

                        startActivity(intent)
                    }


                    // Listener botón Histórico (cambiar a HistoricoActivity)
                    btnHistorico.setOnClickListener {
                        val intent = Intent(this, HistoricoActivity::class.java).apply {
                            putExtra("email", email)
                            putExtra("terrarioId", terrario.id)
                        }
                        startActivity(intent)
                    }

                    // Añadir vista al LinearLayout padre
                    linearLayoutTerrarios.addView(terrarioView)
                }
            }
        }
    }

    private fun mostrarDialogCambioNombreTerrario(email: String, terrarioId: String, tvNombre: TextView) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle("Cambiar nombre del terrario")
            .setView(input)
            .setPositiveButton("OK") { dialog, _ ->
                val nuevoNombre = input.text.toString()
                bbddConnection.updateTerrarioName(email, terrarioId, nuevoNombre) { success ->
                    runOnUiThread {
                        if (success) {
                            tvNombre.text = nuevoNombre
                            Toast.makeText(this, "Nombre actualizado con éxito", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al actualizar nombre", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun pedirPrediccion(
        listaValores: List<Double>,
        steps: Int,
        callback: (List<Double>) -> Unit
    ) {
        val url = "https://europe-west1-ecomonitor-2024.cloudfunctions.net/predictSimple"

        val jsonBody = JSONObject().apply {
            put("values", JSONArray(listaValores))
            put("steps", steps)
        }

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            { response ->
                val predArray = response.getJSONArray("prediction")
                val predList = (0 until predArray.length()).map { predArray.getDouble(it) }
                callback(predList)
            },
            { error ->
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

}