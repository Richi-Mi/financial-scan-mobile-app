package com.richi_mc.myapplication.data.api.dto

data class EstadoAnalisisGeneral(
    val resumen_ejecutivo: String?,
    val mensaje_motivacional: String?,
    // Usamos Map porque el backend separó títulos y explicaciones en distintos objetos
    val insights_clave: List<Map<String, Any>>?
)
