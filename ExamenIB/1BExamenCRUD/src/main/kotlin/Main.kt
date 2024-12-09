package main

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Boolean
import models.Artista
import models.Obra
import services.CRUDService
import java.util.*

fun main() {
    val crud = CRUDService()
    var artistas = crud.readArtistas().toMutableList()

    while (true) {
        println("\n=== Menú  ===")
        println("1. Crear un artista")
        println("2. Listar los artistas")
        println("3. Actualizar artista")
        println("4. Eliminar artista")
        println("5. Añadir obra a un artista")
        println("6. Eliminar obra de un artista")
        println("7. Listar obras de un artista")
        println("8. Actualizar obra de un artista")
        println("9. Salir")
        print("Selecciona una opción: ")

        when (readln().toIntOrNull()) {
            1 -> {
                println("Ingrese el nombre del artista:")
                val nombre = readln()
                println("Ingrese la edad del artista:")
                val edad = readln().toIntOrNull() ?: 0
                println("Ingrese la actividad de obras verdadero(v) o falso (f) :")
                val activo = readln().toBoolean()
                println("Ingrese el numero de obras:")
                val numeroObra = readln().toIntOrNull() ?: 0
                println("Ingrese el promedio de obras:")
                val promedio = readln().toDoubleOrNull() ?: 0.0

                val artista = Artista(
                    id = (artistas.maxOfOrNull { it.id } ?: 0) + 1,
                    nombre = nombre,
                    edad = edad,
                    activo = activo,
                    numeroObras = numeroObra,
                    promedioValorObras = promedio
                )
                crud.createArtista(artista)
                artistas.clear()
                artistas.addAll(crud.readArtistas())
                println("Artista creado exitosamente.")
            }

            2 -> {
                println("\n=== Lista de artistas ===")
                if (artistas.isEmpty()) {
                    println("No hay artistas registrados.")
                } else {
                    artistas.forEach { println(it) }
                }
            }

            3 -> {
                println("Ingrese el ID del artista a actualizar:")
                val id = readln().toIntOrNull()
                val artista = artistas.find { it.id == id }
                if (artista != null) {
                    println("Ingrese el nuevo nombre del artista:")
                    artista.nombre = readln()
                    println("Ingrese la edad del artista:")
                    artista.edad = readln().toInt()
                    println("Ingrese epromedio de obras:")
                    artista.promedioValorObras = readln().toDoubleOrNull() ?: artista.promedioValorObras
                    crud.updateArtista(id!!, artista)
                    artistas.clear()
                    artistas.addAll(crud.readArtistas())
                    println("Artista actualizado exitosamente.")
                } else {
                    println("Artista no encontrado.")
                }
            }

            4 -> {
                println("Ingrese el ID de la artista a eliminar:")
                val id = readln().toIntOrNull()
                if (id != null && artistas.any { it.id == id }) {
                    crud.deleteArtista(id)
                    artistas.clear()
                    artistas.addAll(crud.readArtistas())
                    println("Artista eliminado exitosamente.")
                } else {
                    println("ID inválido o artisto no encontrada.")
                }
            }

            5 -> {
                println("Ingrese el ID del artista:")
                val artistaId = readln().toIntOrNull()
                val artista = artistas.find { it.id == artistaId }
                if (artista != null) {
                    println("Ingrese el titulo de la obra:")
                    val titulo = readln()
                    println("Ingrese la fecha de creacion de la obra:")
                    val fecha = readln().toInt()
                    println("Ingrese disponibilidad de la obra:")
                    val vendida = readln().toBoolean()
                    println("Ingrese las paginas de la obra:")
                    val numeroPaginas = readln().toInt()
                    println("Ingrese el valor de la obra en $:")
                    val valor = readln().toDoubleOrNull() ?: 0.0

                    val obra = Obra(
                        id = (artista.obras.maxOfOrNull { it.id } ?: 0) + 1,
                        titulo = titulo,
                        fechaCreacion = fecha,
                        vendida = vendida,
                        numeroPaginas = numeroPaginas,
                        valor = valor
                    )
                    crud.addObraToArtista(artistaId!!, obra)
                    artistas.clear()
                    artistas.addAll(crud.readArtistas())
                    println("Obra añadida exitosamente.")
                } else {
                    println("Artista no encontrada.")
                }
            }

            6 -> {
                println("Ingrese el ID de la artista:")
                val artistaId = readln().toIntOrNull()
                val artista = artistas.find { it.id == artistaId }
                if (artista != null) {
                    println("Ingrese el ID de la Obra a eliminar:")
                    val obraId = readln().toIntOrNull()
                    if (obraId != null && artista.obras.any { it.id == obraId }) {
                        crud.removeObraFromArtista(artistaId!!, obraId)
                        artistas.clear()
                        artistas.addAll(crud.readArtistas())
                        println("Obra eliminado exitosamente.")
                    } else {
                        println("Obra no encontrada con el artista.")
                    }
                } else {
                    println("Artista no encontrada.")
                }
            }

            7 -> {
                println("Ingrese el ID del artista:")
                val artistaId = readln().toIntOrNull()
                val obras = crud.readObras(artistaId ?: -1)
                if (obras != null && obras.isNotEmpty()) {
                    println("\n=== Obras que tiene el artista ID: $artistaId ===")
                    obras.forEach { println(it) }
                } else {
                    println("No se encontraron obras o no existe.")
                }
            }

            8 -> {
                println("Ingrese el ID de la artista:")
                val artistaId = readln().toIntOrNull()
                val artista = artistas.find { it.id == artistaId }
                if (artista != null) {
                    println("Ingrese el ID de la obra a actualizar:")
                    val obraId = readln().toIntOrNull()
                    val obra = artista.obras.find { it.id == obraId }
                    if (obra != null) {
                        println("Ingrese el nuevo nombre del obra:")
                        obra.titulo = readln()
                        println("Ingrese la nueva fecha de la obra:")
                        obra.fechaCreacion = readln().toIntOrNull() ?: obra.fechaCreacion
                        println("Ingrese si esta o no vendida (v) o (f):")
                        obra.vendida = readln().toBooleanStrict()
                        println("Ingrese las paginas a actualizar:")
                        obra.numeroPaginas = readln().toInt()
                        println("Ingrese el nuevo valor de la obra en $:")
                        obra.valor = readln().toDoubleOrNull() ?: obra.valor
                        crud.updateObra(artistaId!!, obraId!!, obra)
                        artistas.clear()
                        artistas.addAll(crud.readArtistas())
                        println("Obra actualizado exitosamente.")
                    } else {
                        println("Obra no encontrado.")
                    }
                } else {
                    println("Artista no encontrada.")
                }
            }

            9 -> {
                println("Saliendo del programa...")
                return
            }

            else -> println("Opción inválida. Intente nuevamente.")
        }
    }
}
