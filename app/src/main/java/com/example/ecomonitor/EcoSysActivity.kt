package com.example.ecomonitor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class EcoSysActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ecosys)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = intent.extras
        val email = bundle?.getString("email")?.substringBefore("@")

        setup(email ?: "")
        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)

        val btn1: Button = findViewById(R.id.tortugas)
        btn1.setOnClickListener {
            val intent: Intent = Intent(this, TerrarioActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
        }

        val btn2: Button = findViewById(R.id.historico)
        btn2.setOnClickListener {
            val intent2: Intent = Intent(this, HistoricoActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent2)
        }

        val configureUsernameBtn: Button = findViewById(R.id.configureUsernameBtn)
        configureUsernameBtn.setOnClickListener {
            changeUserName(it)
        }

        val configureEcosysnameBtn: Button = findViewById(R.id.configureEcosysnameBtn)
        configureUsernameBtn.setOnClickListener {
            changeEcosysName(it)
        }
    }
    private fun setup(email:String){
        title = "Inicio"
        val user: TextView = findViewById(R.id.username)
        user.text = email

        val logOutBtn: TextView = findViewById(R.id.logOutButton)
        logOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun changeUserName(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar Nombre de Usuario")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { dialog, which ->
            val newUsername = input.text.toString().trim()
            if (newUsername.isNotEmpty()) {
                // Actualizar el TextView con el nuevo nombre de usuario
                findViewById<TextView>(R.id.username).text = newUsername
                // Guardar el nuevo nombre de usuario en SharedPreferences
                sharedPreferences.edit().putString("username", newUsername).apply()
            } else {
                Toast.makeText(this, "Debe ingresar un nombre de usuario válido", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    fun changeEcosysName(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar Nombre de Usuario")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { dialog, which ->
            val newUsername = input.text.toString().trim()
            if (newUsername.isNotEmpty()) {
                // Actualizar el TextView con el nuevo nombre de usuario
                findViewById<TextView>(R.id.username).text = newUsername
                // Guardar el nuevo nombre de usuario en SharedPreferences
                sharedPreferences.edit().putString("username", newUsername).apply()
            } else {
                Toast.makeText(this, "Debe ingresar un nombre de usuario válido", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }
}