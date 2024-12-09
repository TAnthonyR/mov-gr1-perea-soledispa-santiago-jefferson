package services

import models.Artista
import models.Obra
import java.io.File
import java.text.SimpleDateFormat

class CRUDService {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val artistasFile = File("src/main/resources/artistas.txt")

    private var artistas: MutableList<Artista> = mutableListOf()

    init {
        println("Ruta del archivo de artistas.txt: ${artistasFile.absolutePath}")

        if (!artistasFile.exists()) {
            val inputStream = javaClass.classLoader.getResourceAsStream("artistas.txt")
            if (inputStream != null) {
                artistasFile.writeText(inputStream.bufferedReader().use { it.readText() })
                println("Archivo artistas.txt copiado desde resources.")
            } else {
                println("Error: No se encontró el archivo artistas.txt en resources.")
            }
        }
        loadFromFile()
    }

    private fun loadFromFile() {
        if (artistasFile.exists()) {
            println("Se esta cargando artistas desde el archivo...")
            artistasFile.readLines().forEach { line ->
                if (line.isNotBlank()) {
                    val parts = line.split("|")
                    if (parts.size >= 6) {
                        try {
                            val artistas = parts.drop(6).chunked(6).mapNotNull { artParts ->
                                try {
                                    artistas.add(
                                        Artista(
                                            id = artParts[0].toInt(),
                                            nombre = artParts[1],
                                            edad = artParts[2].toInt(),
                                            activo = artParts[3].toBooleanStrict(),
                                            numeroObras = artParts[4].toInt(),
                                            promedioValorObras = artParts[5].toDouble()
                                        )
                                    )
                                } catch (e: Exception) {
                                    println("Error al cargar la Obra: ${e.message}")
                                    null
                                }
                            }.toMutableList()
                            Obra(
                                id = parts[0].toInt(),
                                titulo = parts[1],
                                fechaCreacion = parts[2].toInt(),
                                vendida = parts[3].toBooleanStrict(),
                                numeroPaginas = parts[4].toInt(),
                                valor = parts[5].toDouble(),
                            )
                        } catch (e: Exception) {
                            println("Error al cargar el Artista: ${e.message}")
                        }
                    } else {
                        println("Línea mal formateada: $line")
                    }
                }
            }
            println("artistas cargados: ${artistas.size}")
        }
    }

    private fun saveToFile() {
        try {
            artistasFile.writeText("") // Limpiar el archivo antes de escribir
            artistas.forEach { artista ->
                val obrasText = artista.obras.joinToString("|") { obra ->
                    "${obra.id}|${obra.titulo}|${dateFormat.format(obra.fechaCreacion)}|${obra.vendida}|${obra.numeroPaginas}|${obra.valor}"
                }
                val artistaText = "${artista.id}|${artista.nombre}|${artista.edad}|${artista.activo}|${artista.numeroObras}|${artista.promedioValorObras}|${obrasText}\n"
                artistasFile.appendText(artistaText)
            }
            println("Datos guardados correctamente en el archivo.")
        } catch (e: Exception) {
            println("Error al guardar en el archivo: ${e.message}")
        }
    }

    // CRUD para artista
    fun createArtista(artista: Artista) {
        artistas.add(artista)
        saveToFile()
    }

    fun readArtistas(): List<Artista> = artistas

    fun updateArtista(id: Int, updatedArtista: Artista) {
        val index = artistas.indexOfFirst { it.id == id }
        if (index != -1) {
            artistas[index] = updatedArtista
            saveToFile()
        } else {
            println("artista no encontrado")
        }
    }

    fun deleteArtista(id: Int) {
        artistas.removeIf { it.id == id }
        saveToFile()
    }

    // CRUD para obras que tiene un artista
    fun addObraToArtista(artistaId: Int, obra: Obra) {
        val artista = artistas.find { it.id == artistaId }
        if (artista != null) {
            artista.obras.add(obra)
            artista.numeroObras = artista.obras.size
            saveToFile()
        } else {
            println("artista no encontrado")
        }
    }

    fun removeObraFromArtista(artistaId: Int, obraId: Int) {
        val artista = artistas.find { it.id == artistaId }
        if (artista != null) {
            artista.obras.removeIf { it.id == obraId }
            artista.numeroObras = artista.obras.size
            saveToFile()
        } else {
            println("artista no encontrado")
        }
    }

    // Listar obras de un artista
    fun readObras(artistaId: Int): List<Obra>? {
        val artista = artistas.find { it.id == artistaId }
        return artista?.obras
    }

    // Actualizar obra
    fun updateObra(artistaId: Int, obraId: Int, updatedobra: Obra) {
        val artista = artistas.find { it.id == artistaId }
        if (artista != null) {
            val index = artista.obras.indexOfFirst { it.id == obraId }
            if (index != -1) {
                artista.obras[index] = updatedobra
                saveToFile()
            } else {
                println("obra no encontrada")
            }
        } else {
            println("artista no encontrado")
        }
    }
}