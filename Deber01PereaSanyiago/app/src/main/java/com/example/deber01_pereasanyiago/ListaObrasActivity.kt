package com.example.deber01_pereasanyiago

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListaObrasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var obras: MutableList<Pair<Int, String>> // Incluye IDs
    private lateinit var nombreArtistaTextView: TextView
    private var artistaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_obras)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.listaObras)
        nombreArtistaTextView = findViewById(R.id.nombreArtistaTextView) // TextView para el nombre del artista
        val btnAgregarObra = findViewById<Button>(R.id.btnAgregarObra)

        artistaId = intent.getIntExtra("ARTISTA_ID", -1)

        if (artistaId == -1) {
            Toast.makeText(this, "Error: no se encontró el artista.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mostrarNombreArtista()
        cargarObras(artistaId)

        btnAgregarObra.setOnClickListener {
            val intent = Intent(this, AgregarObraActivity::class.java)
            intent.putExtra("ARTISTA_ID", artistaId)
            startActivity(intent)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpciones(obras[position].first, obras[position].second)
            true
        }
    }

    private fun mostrarNombreArtista() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM Artista WHERE id = ?", arrayOf(artistaId.toString()))
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            nombreArtistaTextView.text = nombre
        }
        cursor.close()
    }

    private fun cargarObras(artistaId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, titulo, valor FROM Obra WHERE artista_id = ?", arrayOf(artistaId.toString()))

        obras = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val titulo = cursor.getString(1)
                val valor = cursor.getDouble(2)
                obras.add(id to "$titulo - $valor$")
            } while (cursor.moveToNext())
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, obras.map { it.second })
        listView.adapter = adapter
    }

    private fun mostrarOpciones(obraId: Int, obraInfo: String) {
        val opciones = arrayOf("Editar", "Eliminar")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones para $obraInfo")
        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> editarObra(obraId)
                1 -> eliminarObra(obraId)
            }
        }
        builder.show()
    }

    private fun editarObra(obraId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT titulo, fechaCreacion, vendida, numeroPaginas, valor FROM Obra WHERE id = ?", arrayOf(obraId.toString()))

        if (cursor.moveToFirst()) {
            val titulo = cursor.getString(0)
            val fechaCreacion = cursor.getInt(1)
            val vendida = cursor.getInt(2) == 1
            val numeroPaginas = cursor.getInt(3)
            val valor = cursor.getDouble(4)

            cursor.close()

            val intent = Intent(this, AgregarObraActivity::class.java)
            intent.putExtra("OBRA_ID", obraId)
            intent.putExtra("TITULO", titulo)
            intent.putExtra("FECHA_CREACION", fechaCreacion)
            intent.putExtra("VENDIDA", vendida)
            intent.putExtra("NUMERO_PAGINAS", numeroPaginas)
            intent.putExtra("VALOR", valor)
            intent.putExtra("ARTISTA_ID", artistaId)
            startActivity(intent)
        } else {
            cursor.close()
            Toast.makeText(this, "Error al cargar los datos de la obra.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarObra(obraId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Obra")
        builder.setMessage("¿Estás seguro de que quieres eliminar esta obra?")
        builder.setPositiveButton("Eliminar") { _, _ ->
            val db = dbHelper.writableDatabase
            db.execSQL("DELETE FROM Obra WHERE id = ?", arrayOf(obraId))
            cargarObras(artistaId)
            Toast.makeText(this, "Obra eliminada correctamente", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        cargarObras(artistaId)
    }
}
