package com.example.ecomonitor

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


        val btn: Button = findViewById(R.id.backT)
        btn.setOnClickListener{

            val intent:Intent = Intent(this, MainActivity:: class.java)
            startActivity(intent)
         }
        /*
        val btn1: Button = findViewById(R.id.login)
        btn1.setOnClickListener{
            //if (emailEditText.text.isNotEmpty() && editPassword.ext.isNotEmpty())
            val intent1:Intent = Intent(this, ecoSys:: class.java)
            startActivity(intent1)
        }
        */
        val btn2: Button = findViewById(R.id.register)
        btn2.setOnClickListener{
            val intent2:Intent = Intent(this, Register:: class.java)
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
        val ecoSysIntent:Intent = Intent(this, ecoSys:: class.java).apply {
            putExtra("email", email)
        }
        startActivity(ecoSysIntent)
    }

}