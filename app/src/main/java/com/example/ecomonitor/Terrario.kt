package com.example.ecomonitor


/*
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement


class Terrario : AppCompatActivity() {

    private lateinit var handler: Handler
    private val interval = 3000L // 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terrario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn: Button = findViewById(R.id.backT)
        btn.setOnClickListener{

            val intent: Intent = Intent(this,ecoSys:: class.java)
            startActivity(intent)
        }

        handler = Handler(Looper.getMainLooper())

        // Iniciar la actualización periódica de los valores
        startUpdatingValues()
    }

    private fun startUpdatingValues() {
        handler.post(object : Runnable {
            override fun run() {
                updateValues()
                handler.postDelayed(this, interval)
            }
        })
    }

    private fun updateValues() {
        val connection = MySQLConnection.getConnection()
        println(connection)
        if (connection != null) {
            try {
                val statement: Statement = connection.createStatement()
                val resultSet: ResultSet = statement.executeQuery("SELECT * FROM mediciones_usuario1 LIMIT 1")
                if (resultSet.next()) {
                    val co2Aire = resultSet.getDouble("CO2Aire")
                    val tempAire = resultSet.getDouble("tempAire")
                    val tempAgua = resultSet.getDouble("tempAgua")
                    val phAgua = resultSet.getDouble("PHAgua")

                    // Actualizar los valores en tu interfaz de usuario
                    // Por ejemplo, puedes mostrarlos en TextViews
                    updateTextViewValue(co2Aire, tempAire, tempAgua, phAgua)
                }
                resultSet.close()
                statement.close()
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        } else {
            println("No se pudo establecer la conexión a la base de datos.")
        }
    }



    private fun updateTextViewValue(co2Aire: Double, tempAire: Double, tempAgua: Double, phAgua: Double) {
        // Actualiza los valores en tus TextViews
        // Supongamos que tienes TextViews con ids "txtCO2Aire", "txtTempAire", "txtTempAgua", "txtPHAgua"
        findViewById<TextView>(R.id.txtCO2Aire).text = "$co2Aire"
        findViewById<TextView>(R.id.txtTempAire).text = "$tempAire"
        findViewById<TextView>(R.id.txtTempAgua).text = "$tempAgua"
        findViewById<TextView>(R.id.txtPHAgua).text = "$phAgua"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
*/
import android.content.Intent
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
import java.sql.SQLException
import java.sql.Statement

class Terrario : AppCompatActivity() {

    private lateinit var handler: Handler
    private val interval = 3000L // 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terrario)
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

        handler = Handler(Looper.getMainLooper())

        // Iniciar la actualización periódica de los valores
        startUpdatingValues()
    }

    private fun startUpdatingValues() {
        handler.post(object : Runnable {
            override fun run() {
                // Ejecutar la actualización de valores en un hilo secundario
                GlobalScope.launch(Dispatchers.IO) {
                    updateValues()
                }
                handler.postDelayed(this, interval)
            }
        })
    }

    private suspend fun updateValues() {
        val connection = MySQLConnection.getConnection()
        println(connection)
        if (connection != null) {
            try {
                val statement: Statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM mediciones_usuario1 LIMIT 1")
                if (resultSet.next()) {
                    val co2Aire = resultSet.getDouble("CO2Aire")
                    val tempAire = resultSet.getDouble("tempAire")
                    val tempAgua = resultSet.getDouble("tempAgua")
                    val phAgua = resultSet.getDouble("PHAgua")

                    // Actualizar los valores en tu interfaz de usuario
                    // Por ejemplo, puedes mostrarlos en TextViews
                    updateTextViewValue(co2Aire, tempAire, tempAgua, phAgua)
                }
                resultSet.close()
                statement.close()
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        } else {
            println("No se pudo establecer la conexión a la base de datos.")
        }
    }

    private fun updateTextViewValue(co2Aire: Double, tempAire: Double, tempAgua: Double, phAgua: Double) {
        // Actualiza los valores en tus TextViews
        // Supongamos que tienes TextViews con ids "txtCO2Aire", "txtTempAire", "txtTempAgua", "txtPHAgua"
        findViewById<TextView>(R.id.txtCO2Aire).text = "$co2Aire"
        findViewById<TextView>(R.id.txtTempAire).text = "$tempAire"
        findViewById<TextView>(R.id.txtTempAgua).text = "$tempAgua"
        findViewById<TextView>(R.id.txtPHAgua).text = "$phAgua"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
