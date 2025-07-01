package com.example.ecomonitor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "🔔 onMessageReceived ejecutado")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "EcoMonitor - Alerta"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["mensaje"]
            ?: "Hay una alerta en tu terrario"

        Log.d("FCM", "📨 Notificación recibida - Título: $title - Cuerpo: $body")

        mostrarNotificacion(title, body)
    }

    private fun mostrarNotificacion(title: String, body: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "alert_channel"

        // Crear canal para Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas EcoMonitor",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de alertas ambientales del sistema EcoMonitor"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Usa un ícono válido
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

//    override fun onNewToken(token: String) {
//        Log.d("FCM", "🆕 Nuevo token generado: $token")
//        // Puedes guardar o subir este token si manejas notificaciones individuales
//    }
}


//package com.example.ecomonitor
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d("FCM", "🔔 onMessageReceived ejecutado")
//
//        // Log para ver si trae notification
//        remoteMessage.notification?.let {
//            Log.d("FCM", "Mensaje con notification: ${it.title} - ${it.body}")
//        }
//
//        // Log para ver si trae data
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d("FCM", "Mensaje con data: ${remoteMessage.data}")
//        }
//
//        // Preparar datos para mostrar notificación
//        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Alerta EcoMonitor"
//        val body = remoteMessage.notification?.body ?: remoteMessage.data["mensaje"] ?: "Hay una alerta en tu terrario"
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Crear el canal de notificación para Android 8+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "alert_channel"
//            val channelName = "Alertas"
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            channel.description = "Canal para alertas del sistema EcoMonitor"
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val builder = NotificationCompat.Builder(this, "alert_channel")
//            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate que exista este icono
//            .setContentTitle(title)
//            .setContentText(body)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//
//        notificationManager.notify(0, builder.build())
//    }
//
//    override fun onNewToken(token: String) {
//        Log.d("FCM", "Nuevo token: $token")
//        // Aquí puedes enviar el token al servidor si deseas vincularlo con un usuario
//    }
//}


//package com.example.ecomonitor
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        remoteMessage.notification?.let {
//            //--------------------------------------------------------------------------------------
//            //Prueba si onMessageReceived() está funcionando
//            //--------------------------------------------------------------------------------------
//            Log.d("FCM", "🔔 onMessageReceived ejecutado")
//
//            if (remoteMessage.notification != null) {
//                Log.d("FCM", "Mensaje con notification: ${remoteMessage.notification?.title} - ${remoteMessage.notification?.body}")
//            }
//
//            if (remoteMessage.data.isNotEmpty()) {
//                Log.d("FCM", "Mensaje con data: ${remoteMessage.data}")
//            }
//            //--------------------------------------------------------------------------------------
//
//            Log.d("FCM", "Mensaje recibido: ${it.title} - ${it.body}")
//
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            // Crear el canal de notificación si es Android 8 o superior
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channelId = "alert_channel"
//                val channelName = "Alertas"
//                val channel = NotificationChannel(
//                    channelId,
//                    channelName,
//                    NotificationManager.IMPORTANCE_HIGH
//                )
//                channel.description = "Canal para alertas del sistema EcoMonitor"
//                notificationManager.createNotificationChannel(channel)
//            }
//
//            // Construir la notificación
//            val builder = NotificationCompat.Builder(this, "alert_channel")
//                .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener este ícono en res/drawable <<----------------------------------
//                .setContentTitle(it.title)
//                .setContentText(it.body)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//
//            // Mostrar la notificación
//            notificationManager.notify(0, builder.build())
//        }
//    }
//
//    override fun onNewToken(token: String) {
//        Log.d("FCM", "Nuevo token: $token")
//        // Aquí puedes enviar el token al servidor si deseas vincularlo con un usuario
//    }
//}


//package com.example.ecomonitor
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import android.content.Context
//import android.util.Log
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        remoteMessage.notification?.let {
//            Log.d("FCM", "Mensaje recibido: ${it.title} - ${it.body}")
//
//            val builder = NotificationCompat.Builder(this, "alert_channel")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle(it.title)
//                .setContentText(it.body)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel("alert_channel", "Alertas", NotificationManager.IMPORTANCE_HIGH)
//                manager.createNotificationChannel(channel)
//            }
//
//            manager.notify(0, builder.build())
//        }
//    }
//
//    override fun onNewToken(token: String) {
//        Log.d("FCM", "Nuevo token: $token")
//        // Aquí podrías enviar el token a tu base de datos si deseas vincularlo con un usuario.
//    }
//}
