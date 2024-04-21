package com.example.ecomonitor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn: Button = findViewById(R.id.button2)
        btn.setOnClickListener{
            val intent:Intent = Intent(this,MainActivity:: class.java)
            startActivity(intent)
        }
        val btn1: Button = findViewById(R.id.button3)
        btn1.setOnClickListener{
            val intent1:Intent = Intent(this,ecoSys:: class.java)
            startActivity(intent1)
        }
        val btn2: Button = findViewById(R.id.button1)
        btn2.setOnClickListener{
            val intent2:Intent = Intent(this,Register:: class.java)
            startActivity(intent2)
        }
    }
}