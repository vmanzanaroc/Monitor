package com.example.ecomonitor

import com.github.mikephil.charting.charts.LineChart

data class ConfigGrafica(
    val chart: LineChart,
    val campoY: (Medicion) -> Float?,
    val descripcion: String,
    val unidad: String,
    var rango: ClosedFloatingPointRange<Float>
)
