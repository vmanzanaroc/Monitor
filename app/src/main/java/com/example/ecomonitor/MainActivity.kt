package com.example.ecomonitor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val email = currentUser.email ?: ""
            //val userId = currentUser.uid
            val userId = "IVZ3Ty9mmUY3jMoPW8KWp0QyuaK2"

            // Suscribirse al topic de notificaciones personalizadas
            FirebaseMessaging.getInstance().subscribeToTopic("user_$email")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM", "✅ Suscrito al topic user_$email")
                    } else {
                        Log.e("FCM", "❌ Error al suscribirse al topic", task.exception)
                    }
                }

            // Pedir permiso notificaciones solo en Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_NOTIFICATION_PERMISSION
                    )
                } else {
                    // Permiso ya concedido, puedes proceder si quieres mostrar notificaciones
                    Log.d("MainActivity", "Permiso de notificaciones ya concedido")
                }
            }

            // Ir a EcoSysActivity enviando el email
            val intent = Intent(this, EcoSysActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
            return
        }

        // Usuario no logueado, mostrar layout principal con botón login
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn: Button = findViewById(R.id.button)
        btn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // Manejar respuesta a la solicitud de permiso
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Permiso de notificaciones concedido")
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
            } else {
                Log.w("MainActivity", "Permiso de notificaciones denegado")
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


//token: IVZ3Ty9mmUY3jMoPW8KWp0QyuaK2


//package com.example.ecomonitor
//
//import android.Manifest
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.messaging.FirebaseMessaging
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enableEdgeToEdge()
//
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        if (currentUser != null) {
//            val email = currentUser.email ?: ""
//
//            // Suscribirse al topic de notificaciones personalizadas usando userId o email limpio
//            val userId = currentUser.uid
//            FirebaseMessaging.getInstance().subscribeToTopic("user_$userId")
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d("FCM", "✅ Suscrito al topic user_$userId")
//                    } else {
//                        Log.e("FCM", "❌ Error al suscribirse al topic", task.exception)
//                    }
//                }
//
//            // Solicitar permiso notificaciones solo en Android 13+
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
//            }
//
//            // Ir a EcoSysActivity enviando el email
//            val intent = Intent(this, EcoSysActivity::class.java).apply {
//                putExtra("email", email)
//            }
//            startActivity(intent)
//            finish()
//            return
//        }
//
//        // Usuario no logueado, mostrar layout principal con botón login
//        setContentView(R.layout.activity_main)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        val btn: Button = findViewById(R.id.button)
//        btn.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//    }
//}


//package com.example.ecomonitor
//
//import android.Manifest
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.messaging.FirebaseMessaging
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enableEdgeToEdge()
//
//        // Verificar si el usuario ya ha iniciado sesión
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        if (currentUser != null) {
//            val userId = currentUser.uid
//
//            // Suscribirse al topic de notificaciones personalizadas
//            FirebaseMessaging.getInstance().subscribeToTopic("user_$userId")
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d("FCM", "✅ Suscrito al topic user_$userId")
//                    } else {
//                        Log.e("FCM", "❌ Error al suscribirse al topic", task.exception)
//                    }
//                }
//
//            // Solicitar permiso de notificaciones si es Android 13+
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
//            }
//
//            // Redirigir directamente a la pantalla principal (lista de terrarios)
//            val intent = Intent(this, EcoSysActivity::class.java)
//            startActivity(intent)
//            finish()
//            return
//        }
//
//        // Si no ha iniciado sesión, mostrar la vista de bienvenida
//        setContentView(R.layout.activity_main)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // Botón para ir al Login
//        val btn: Button = findViewById(R.id.button)
//        btn.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        }
//    }
//}


//package com.example.ecomonitor
//
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.widget.Button
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//
//import com.google.firebase.messaging.FirebaseMessaging
//import android.util.Log
//import com.google.firebase.auth.FirebaseAuth
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        val btn: Button = findViewById(R.id.button)
//        btn.setOnClickListener{
//            val intent: Intent = Intent(this,LoginActivity:: class.java)
//            startActivity(intent)
//        }
//
//
////        FirebaseMessaging.getInstance().subscribeToTopic("alertas")
////            .addOnCompleteListener { task ->
////                if (task.isSuccessful) {
////                    Log.d("FCM", "Suscripción exitosa al topic")
////                } else {
////                    Log.e("FCM", "Error en la suscripción", task.exception)
////                }
////            }
//
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        if (userId != null) {
//            FirebaseMessaging.getInstance().subscribeToTopic("user_$userId")
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d("FCM", "Suscrito correctamente al topic user_$userId")
//                    } else {
//                        Log.e("FCM", "Error al suscribirse al topic", task.exception)
//                    }
//                }
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
//        }
//
//    }
//}