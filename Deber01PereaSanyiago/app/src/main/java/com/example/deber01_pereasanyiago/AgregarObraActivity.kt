package com.example.deber01_pereasanyiago

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarObraActivity : AppCompatActivity() {

    private var artistaId: Int = -1
    private var obraId: Int = -1
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvArtistaInfo: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_obra)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        tvArtistaInfo = findViewById(R.id.tvArtistaInfo)

        val etTitulo = findViewById<EditText>(R.id.etTituloObra)
        val etFechaCreacion = findViewById<EditText>(R.id.etFechaCreacionObra)
        val etNumeroPaginas = findViewById<EditText>(R.id.etNumeroPaginasObra)
        val etValor = findViewById<EditText>(R.id.etValorObra)
        val cbVendida = findViewById<CheckBox>(R.id.cbVendida)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarObra)

        // Obtener el ID del artista y de la obra
        artistaId = intent.getIntExtra("ARTISTA_ID", -1)
        obraId = intent.getIntExtra("OBRA_ID", -1)

        if (artistaId == -1) {
            Toast.makeText(this, "Error: no se encontró el artista.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mostrarNombreArtista()

        // Si es edición, cargar datos de la obra
        if (obraId != -1) {
            cargarDatosObra(obraId, etTitulo, etFechaCreacion, etNumeroPaginas, etValor, cbVendida)
        }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val fechaCreacion = etFechaCreacion.text.toString().toIntOrNull()
            val numeroPaginas = etNumeroPaginas.text.toString().toIntOrNull()
            val valor = etValor.text.toString().toDoubleOrNull()
            val vendida = cbVendida.isChecked

            if (titulo.isNotEmpty() && fechaCreacion != null && numeroPaginas != null && valor != null) {
                val db = dbHelper.writableDatabase
                val valores = ContentValues().apply {
                    put("titulo", titulo)
                    put("fechaCreacion", fechaCreacion)
                    put("numeroPaginas", numeroPaginas)
                    put("valor", valor)
                    put("vendida", if (vendida) 1 else 0)
                    put("artista_id", artistaId)
                }

                if (obraId == -1) {
                    // Agregar nueva obra
                    val resultado = db.insert("Obra", null, valores)
                    if (resultado != -1L) {
                        Toast.makeText(this, "Obra agregada correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al guardar la obra.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Editar obra existente
                    val resultado = db.update("Obra", valores, "id = ?", arrayOf(obraId.toString()))
                    if (resultado > 0) {
                        Toast.makeText(this, "Obra editada correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al editar la obra.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarNombreArtista() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM Artista WHERE id = ?", arrayOf(artistaId.toString()))
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            tvArtistaInfo.text = nombre
        }
        cursor.close()
    }

    private fun cargarDatosObra(
        obraId: Int,
        etTitulo: EditText,
        etFechaCreacion: EditText,
        etNumeroPaginas: EditText,
        etValor: EditText,
        cbVendida: CheckBox
    ) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT titulo, fechaCreacion, numeroPaginas, valor, vendida FROM Obra WHERE id = ?",
            arrayOf(obraId.toString())
        )

        if (cursor.moveToFirst()) {
            etTitulo.setText(cursor.getString(0))
            etFechaCreacion.setText(cursor.getInt(1).toString())
            etNumeroPaginas.setText(cursor.getInt(2).toString())
            etValor.setText(cursor.getDouble(3).toString())
            cbVendida.isChecked = cursor.getInt(4) == 1
        }
        cursor.close()
    }
}
