package models

import java.util.Date

data class Obra(
    val id: Int,
    var titulo: String,
    var fechaCreacion: Int,
    var vendida: Boolean,
    var numeroPaginas: Int,
    var valor: Double
)
