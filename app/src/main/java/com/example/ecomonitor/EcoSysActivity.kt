package com.example.ecomonitor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class EcoSysActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bbddConnection: BBDDConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bbddConnection = BBDDConnection()

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
            showUsernameDialog(email ?: "")
        }

        val configureEcosysnameBtn: Button = findViewById(R.id.configureEcosystemNameBtn)
        configureEcosysnameBtn.setOnClickListener {
            showEcosystemNameDialog(email ?: "")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                logOut()
            }
        })

        // Load profile data and set TextViews
        loadProfileData(email ?: "")
    }

    private fun setup(email: String) {
        title = "Inicio"
        val user: TextView = findViewById(R.id.username)
        val ecosysName: TextView = findViewById(R.id.ecosysName)

        // Load initial data
        bbddConnection.readProfileData(email) { username, ecosystemName ->
            user.text = username
            ecosysName.text = ecosystemName
        }

        val logOutBtn: TextView = findViewById(R.id.logOutButton)
        logOutBtn.setOnClickListener {
            logOut()
        }
    }

    private fun loadProfileData(email: String) {
        val user: TextView = findViewById(R.id.username)
        val ecosysName: TextView = findViewById(R.id.ecosysName)

        bbddConnection.readProfileData(email) { username, ecosystemName ->
            user.text = username
            ecosysName.text = ecosystemName
        }
    }

    private fun showUsernameDialog(email: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar nombre de usuario")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val newUsername = input.text.toString()
            bbddConnection.updateUserName(email, newUsername) { success ->
                if (success) {
                    loadProfileData(email) // Refresh data
                    Toast.makeText(this, "Nombre de usuario actualizado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showEcosystemNameDialog(email: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar nombre de terrario")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val newEcosysName = input.text.toString()
            bbddConnection.changeEcosysName(email, newEcosysName) { success ->
                if (success) {
                    loadProfileData(email) // Refresh data
                    Toast.makeText(this, "Nombre de terrario actualizado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar el nombre de terrario", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent: Intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
