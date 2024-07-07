package com.example.ecomonitor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //Google Analytics
        val analytics : FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("LoginScreen", bundle)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val backbtn: Button = findViewById(R.id.back)
        backbtn.setOnClickListener{

            val intent:Intent = Intent(this, MainActivity:: class.java)
            startActivity(intent)
            finish()
         }

        val btn2: Button = findViewById(R.id.register)
        btn2.setOnClickListener{
            val intent2:Intent = Intent(this, RegisterActivity:: class.java)
            startActivity(intent2)
        }

        setup()
    }
    private fun setup() {
        title = "InicioSesión"

        val email: EditText = findViewById(R.id.emailEditText)
        val passwd: EditText = findViewById(R.id.editPassword)
        val btn2: Button = findViewById(R.id.register)
        btn2.setOnClickListener{
            if (email.text.isNotEmpty() && passwd.text.isNotEmpty()) {

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.text.toString(), passwd.text.toString()).addOnCompleteListener {

                        if (it.isSuccessful) {
                            showEcosystems(it.result?.user?.email ?: "")
                        }else {
                            showAlert()
                        }
                    }


            }
        }

        val btn1: Button = findViewById(R.id.login)
        btn1.setOnClickListener{
            if (email.text.isNotEmpty() && passwd.text.isNotEmpty()) {

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.text.toString(), passwd.text.toString()).addOnCompleteListener {

                        if (it.isSuccessful) {
                            showEcosystems(it.result?.user?.email ?: "")
                        }else {
                            showAlert()
                        }
                    }


            }
        }


    }

    private fun showAlert() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autenticar al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    private fun showEcosystems(email: String){
        val ecoSysIntent:Intent = Intent(this, EcoSysActivity:: class.java).apply {
            putExtra("email", email)
        }
        startActivity(ecoSysIntent)
        finish()
    }

}