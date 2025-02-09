package com.example.deber01_pereasanyiago

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context, "artistasDB", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla Artista
        db?.execSQL(
            """
                CREATE TABLE Artista (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    edad INTEGER NOT NULL,
                    activo INTEGER NOT NULL,
                    numeroObras INTEGER NOT NULL,
                    promedioValorObras REAL NOT NULL
                )
            """
        )

        // Crear tabla Obra
        db?.execSQL(
            """
                CREATE TABLE Obra (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    fechaCreacion INTEGER NOT NULL,
                    vendida INTEGER NOT NULL,
                    numeroPaginas INTEGER NOT NULL,
                    valor REAL NOT NULL,
                    artista_id INTEGER NOT NULL,
                    FOREIGN KEY (artista_id) REFERENCES Artista(id)
                )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Obra")
        db?.execSQL("DROP TABLE IF EXISTS Artista")
        onCreate(db)
    }

    fun agregarArtista(nombre: String, edad: Int, activo: Boolean, numeroObras: Int, promedioValorObras: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("edad", edad)
            put("activo", if (activo) 1 else 0)
            put("numeroObras", numeroObras)
            put("promedioValorObras", promedioValorObras)
        }

        val resultado = db.insert("Artista", null, values)
        db.close()
        return resultado
    }

    fun obtenerTodosLosArtistas(): List<Artista> {
        val lista = mutableListOf<Artista>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Artista", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"))
                val activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
                val numeroObras = cursor.getInt(cursor.getColumnIndexOrThrow("numeroObras"))
                val promedioValorObras = cursor.getDouble(cursor.getColumnIndexOrThrow("promedioValorObras"))

                lista.add(Artista(id, nombre, edad, activo, numeroObras, promedioValorObras))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun agregarObra(titulo: String, fechaCreacion: Int, vendida: Boolean, numeroPaginas: Int, valor: Double, artistaId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("titulo", titulo)
            put("fechaCreacion", fechaCreacion)
            put("vendida", if (vendida) 1 else 0)
            put("numeroPaginas", numeroPaginas)
            put("valor", valor)
            put("artista_id", artistaId)
        }

        val resultado = db.insert("Obra", null, values)
        db.close()
        return resultado
    }
}
