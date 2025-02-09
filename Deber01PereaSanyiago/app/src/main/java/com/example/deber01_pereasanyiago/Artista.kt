package com.example.deber01_pereasanyiago

class Artista (
    val id: Int,
    var nombre: String,
    var edad: Int,
    var activo: Boolean,
    var numeroObras: Int,
    var promedioValorObras: Double,
    var obras: MutableList<Obra> = mutableListOf()

)


