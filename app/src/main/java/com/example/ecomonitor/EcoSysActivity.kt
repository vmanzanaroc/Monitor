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
import com.google.firebase.database.DatabaseReference


class EcoSysActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bbddConnection: BBDDConnection
    private lateinit var userRef: DatabaseReference

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


        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                logOut()
            }
        })

        loadProfileData(email ?: "")

    }
    private fun setup(email:String){
        title = "Inicio"
        val user: TextView = findViewById(R.id.username)
        user.text = email

        val logOutBtn: TextView = findViewById(R.id.logOutButton)
        logOutBtn.setOnClickListener {
            logOut()
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
            bbddConnection.changeNameUsers(email, newUsername) { success ->
                if (success) {
                    val user: TextView = findViewById(R.id.username)
                    user.text = newUsername
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
                    val ecosysName: TextView = findViewById(R.id.ecosysName)
                    ecosysName.text = newEcosysName
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

    private fun loadProfileData(email: String) {
        val user: TextView = findViewById(R.id.username)
        val ecosysName: TextView = findViewById(R.id.ecosysName)

        bbddConnection.readProfileData(email) { username, ecosystemName ->
            username?.let { user.text = it }
            ecosystemName?.let { ecosysName.text = it }
        }
    }

    fun logOut(){
        FirebaseAuth.getInstance().signOut()
        val intent: Intent = Intent(this, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent)
    }
}

    /*
    fun changeUserName(view: View, email: String) {
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

                bbddConnection.changeNameUsers(email, newUsername) { success ->
                    if (success) {
                        Toast.makeText(this, "Nombre de usuario actualizado con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show()
                    }
                }

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

     */


    /*
    fun changeEcosysName(view: View, email: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar Nombre de Usuario")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { dialog, which ->
            val newEcosysname = input.text.toString().trim()
            if (newEcosysname.isNotEmpty()) {
                // Actualizar el TextView con el nuevo nombre de usuario
                findViewById<TextView>(R.id.ecosysName).text = newEcosysname

                bbddConnection.changeEcosysName(email, newEcosysname) { success ->
                    if (success) {
                        Toast.makeText(this, "Nombre de terrario actualizado con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al actualizar el nombre de terrario", Toast.LENGTH_SHORT).show()
                    }
                }

                // Guardar el nuevo nombre de usuario en SharedPreferences
                sharedPreferences.edit().putString("ecosysname", newEcosysname).apply()
            } else {
                Toast.makeText(this, "Debe ingresar un nombre de terrario válido", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }
     */

    /*
    fun getuserName(email: String, onDataChange: (Medicion?) -> Unit) {
        userRoot(email, "profile")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName_ = snapshot.getValue(Medicion::class.java)
                onDataChange(userName_)

            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
            }
        })
    }
    */

