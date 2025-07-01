package com.example.ecomonitor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Pruebas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pruebas)

        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val selectedText = findViewById<MaterialButton>(checkedId).text
                Toast.makeText(this, "Seleccionado: $selectedText", Toast.LENGTH_SHORT).show()
            }
        }

        var isFabOpen = false

        val fabMain = findViewById<FloatingActionButton>(R.id.fab_main)
        val fab1 = findViewById<FloatingActionButton>(R.id.fab_option1)
        val fab2 = findViewById<FloatingActionButton>(R.id.fab_option2)
        val fab3 = findViewById<FloatingActionButton>(R.id.fab_option3)
        val fab4 = findViewById<FloatingActionButton>(R.id.fab_option4)
        val fab5 = findViewById<FloatingActionButton>(R.id.fab_option5)
        val fab6 = findViewById<FloatingActionButton>(R.id.fab_option6)

        fabMain.setOnClickListener {
            isFabOpen = !isFabOpen
            if (isFabOpen) {
                fab1.show()
                fab2.show()
                fab3.show()
                fab4.show()
                fab5.show()
                fab6.show()
            } else {
                fab1.hide()
                fab2.hide()
                fab3.hide()
                fab4.hide()
                fab5.hide()
                fab6.hide()
            }
        }
    }
}
