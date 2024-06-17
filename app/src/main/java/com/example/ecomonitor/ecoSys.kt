package com.example.ecomonitor

import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class ecoSys : AppCompatActivity() {

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

        val btn1: Button = findViewById(R.id.tortugas)
        btn1.setOnClickListener {
            val intent: Intent = Intent(this, Terrario::class.java)
            startActivity(intent)
        }

        val bundle = intent.extras
        val email = bundle?.getString("email")

        setup(email ?: "")
        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)
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
        }
    }

        /*
        val btn2: Button = findViewById(R.id.logOutButton)
        btn2.setOnClickListener{
            val intent:Intent = Intent(this, LoginActivity:: class.java)
            startActivity(intent)
        }
         */


        /*
        val btnAdd: Button = findViewById(R.id.button7)
        val linearLayout: LinearLayout = findViewById(R.id.linearLayout) // Asegúrate de tener un ID para tu LinearLayout en el XML

        btnAdd.setOnClickListener {

            // Crear un nuevo botón
            val newButton = Button(this)
            newButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            newButton.text = "Nuevo Botón"
            // Generar un ID único para el botón
            newButton.id = ViewCompat.generateViewId()

            // Añadir el nuevo botón y el nuevo TextView al LinearLayout
            linearLayout.addView(newButton)

            // Guardar los identificadores de los botones y TextViews en SharedPreferences
            val buttonIdSet = sharedPreferences.getStringSet("button_ids", HashSet()) ?: HashSet()
            buttonIdSet.add(newButton.id.toString())

            with(sharedPreferences.edit()) {
                putStringSet("button_ids", buttonIdSet)
                apply()
            }
        }
        val btnRemove: Button = findViewById(R.id.button8)
        btnRemove.setOnClickListener {
            val buttonIdSet = sharedPreferences.getStringSet("button_ids", HashSet()) ?: HashSet()
            //val textViewIdSet = sharedPreferences.getStringSet("textview_ids", HashSet()) ?: HashSet()

            // Verificar si hay al menos un botón para eliminar
            if (buttonIdSet.isNotEmpty()) {
                val lastButtonId = buttonIdSet.last().toInt()

                // Eliminar el último botón y TextView del LinearLayout
                linearLayout.removeView(findViewById<Button>(lastButtonId))


                // Eliminar el último ID de los conjuntos en SharedPreferences
                buttonIdSet.remove(lastButtonId.toString())

                with(sharedPreferences.edit()) {
                    putStringSet("button_ids", buttonIdSet)
                    apply()
                }
            } else {
                // No hay botones para eliminar
                Toast.makeText(this, "No hay botones para eliminar", Toast.LENGTH_SHORT).show()
            }
        }

        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        setup(email ?: "")
    }
         */

}