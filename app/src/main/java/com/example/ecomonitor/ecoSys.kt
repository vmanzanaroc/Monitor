package com.example.ecomonitor

import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context

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

        val btn1: Button = findViewById(R.id.button5)
        btn1.setOnClickListener{
            val intent:Intent = Intent(this,Terrario:: class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("eco_sys_prefs", Context.MODE_PRIVATE)

        val btn2: Button = findViewById(R.id.button6)
        btn2.setOnClickListener{
            val intent:Intent = Intent(this,LoginActivity:: class.java)
            startActivity(intent)
        }
        val btnAdd: Button = findViewById(R.id.button7)
        val linearLayout: LinearLayout = findViewById(R.id.linearLayout) // Asegúrate de tener un ID para tu LinearLayout en el XML

        btnAdd.setOnClickListener {

            /*
            // Crear un nuevo TextView
            val newTextView = TextView(this)
            newTextView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            newTextView.text = "Nuevo TextView"
            // Generar un ID único para el TextView
            newTextView.id = ViewCompat.generateViewId()
            */


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
            //linearLayout.addView(newTextView)

            // Guardar los identificadores de los botones y TextViews en SharedPreferences
            val buttonIdSet = sharedPreferences.getStringSet("button_ids", HashSet()) ?: HashSet()
            buttonIdSet.add(newButton.id.toString())
            //val textViewIdSet = sharedPreferences.getStringSet("textview_ids", HashSet()) ?: HashSet()
           // textViewIdSet.add(newTextView.id.toString())
            with(sharedPreferences.edit()) {
                putStringSet("button_ids", buttonIdSet)
                //putStringSet("textview_ids", textViewIdSet)
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
                //val lastTextViewId = textViewIdSet.last().toInt()

                // Eliminar el último botón y TextView del LinearLayout
                linearLayout.removeView(findViewById<Button>(lastButtonId))
                //linearLayout.removeView(findViewById<TextView>(lastTextViewId))

                // Eliminar el último ID de los conjuntos en SharedPreferences
                buttonIdSet.remove(lastButtonId.toString())
                //textViewIdSet.remove(lastTextViewId.toString())
                with(sharedPreferences.edit()) {
                    putStringSet("button_ids", buttonIdSet)
                    //putStringSet("textview_ids", textViewIdSet)
                    apply()
                }
            } else {
                // No hay botones para eliminar
                Toast.makeText(this, "No hay botones para eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

        /*val btnRemove: Button = findViewById(R.id.button8)

        btnRemove.setOnClickListener {
            val linearLayout: LinearLayout = findViewById(R.id.linearLayout)

            // Obtener el número de hijos en el LinearLayout
            val childCount = linearLayout.childCount

            // Verificar si hay al menos un hijo en el LinearLayout
            if (childCount > 0) {
                // Eliminar el último hijo del LinearLayout
                linearLayout.removeViewAt(childCount - 1)
                linearLayout.removeViewAt(childCount - 1) // Remover también el TextView asociado
            } else {
                // No hay ningún ecosistema para eliminar
                Toast.makeText(this, "No hay ecosistemas para eliminar", Toast.LENGTH_SHORT).show()
            }
        }*/

