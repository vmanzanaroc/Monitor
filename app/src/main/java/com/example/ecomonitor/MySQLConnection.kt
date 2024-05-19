package com.example.ecomonitor


import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object MySQLConnection {


    private const val URL = "jdbc:mysql://localhost:3306/Mediciones"
    private const val user = "root"
    private const val passwd = "root"


    fun getConnection(): Connection? {

        var connection: Connection? = null
        println("Probando conexion")
        try {
            Class.forName("com.mysql.jdbc.Driver")
            connection = DriverManager.getConnection(URL, user, passwd)
            println("Conexion establecida")
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return connection
    }
}

/*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


object MySQLConnection {

    private const val URL = "jdbc:mysql://127.0.0.1:3306/Mediciones"
    private const val user = "root"
    private const val passwd = "root"

    suspend fun getConnection(): Connection? = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        println("Probando conexion")
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            connection = DriverManager.getConnection(URL, user, passwd)
            println("Conexion establecida")
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        connection
    }
}*/
