package models

import java.util.Date
//clase artista
data class Artista(
    val id: Int,
    var nombre: String,
    var edad: Int,
    var activo: Boolean,
    var numeroObras: Int,
    var promedioValorObras: Double,
    var obras: MutableList<Obra> = mutableListOf()
)

