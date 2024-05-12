package com.example.ecomonitor


import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object MySQL_Connection {

    private const val URL = "jdbc:mysql://localhost:3306/Mediciones"
    private const val user = "root"
    private const val passwd = "root"

    fun getConnection(): Connection? {
        var connection: Connection? = null
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Mediciones" + "user=root&password=root")
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object MySQL_Connection {

    private const val URL = "jdbc:mysql://127.0.0.1:3306/Mediciones"
    private const val usuario = "root"
    private const val contra = "root"

    fun getConnection(callback: (Connection?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                Class.forName("com.mysql.cj.jdbc.Driver")
                connection = DriverManager.getConnection(URL, usuario, contra)
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(connection)
            }
        }
    }
}
 */