package com.example.deber01_pereasanyiago

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaArtistas: ListView
    private lateinit var artistas: MutableList<Artista>
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        listaArtistas = findViewById(R.id.listaArtistas)

        artistas = obtenerArtistas()

        // Adaptador con los nombres de los artistas
        val nombres = artistas.map { "${it.nombre} - ${it.edad} años (${it.numeroObras} obras)" }.toMutableList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listaArtistas.adapter = adapter

        findViewById<Button>(R.id.btnCrearArtista).setOnClickListener {
            val intent = Intent(this, AgregarArtistaActivity::class.java)
            startActivity(intent)
        }

        listaArtistas.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpcionesCRUD(artistas[position])
            true
        }
    }

    private fun obtenerArtistas(): MutableList<Artista> {
        val lista = mutableListOf<Artista>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Artista", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Artista(
                        id = cursor.getInt(0),
                        nombre = cursor.getString(1),
                        edad = cursor.getInt(2),
                        activo = cursor.getInt(3) == 1,
                        numeroObras = cursor.getInt(4),
                        promedioValorObras = cursor.getDouble(5)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    private fun mostrarOpcionesCRUD(artista: Artista) {
        val opciones = arrayOf("Editar", "Eliminar", "Ver Obras")
        AlertDialog.Builder(this)
            .setTitle("Opciones para ${artista.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> editarArtista(artista)
                    1 -> eliminarArtista(artista)
                    2 -> verObras(artista)
                }
            }
            .show()
    }

    private fun editarArtista(artista: Artista) {
        val intent = Intent(this, AgregarArtistaActivity::class.java)
        intent.putExtra("ARTISTA_ID", artista.id)
        startActivity(intent)
    }

    private fun eliminarArtista(artista: Artista) {
        val db = dbHelper.writableDatabase
        db.delete("Artista", "id = ?", arrayOf(artista.id.toString()))
        artistas.remove(artista)
        adapter.remove("${artista.nombre} - ${artista.edad} años (${artista.numeroObras} obras)")
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        cargarListaArtistas()
    }

    private fun cargarListaArtistas() {
        // Obtén la lista actualizada de artistas desde la base de datos
        artistas = obtenerArtistas()

        // Actualiza los datos del adaptador
        val nombres = artistas.map { "${it.nombre} - ${it.edad} años (${it.numeroObras} obras)" }.toMutableList()
        adapter.clear()
        adapter.addAll(nombres)
        adapter.notifyDataSetChanged()
    }

    private fun verObras(artista: Artista) {
        Log.d("MainActivity", "ID del artista seleccionado: ${artista.id}")

        val intent = Intent(this, ListaObrasActivity::class.java)
        intent.putExtra("ARTISTA_NOMBRE", artista.nombre)
        intent.putExtra("ARTISTA_ID", artista.id)
        startActivity(intent)
    }
}
