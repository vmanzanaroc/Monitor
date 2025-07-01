package com.example.ecomonitor

data class Medicion (
    var tempAire: Double? = null,
    var co2: Double? = null,
    var tempAgua: Double? = null,
    var ph: Double? = null,
    var partAire: Double? = null,
    var humidity: Double? = null,
    var soundIntensity: Double? = null,
    var lightIntensity: Double? = null,
    var dateTime: String? = null
)