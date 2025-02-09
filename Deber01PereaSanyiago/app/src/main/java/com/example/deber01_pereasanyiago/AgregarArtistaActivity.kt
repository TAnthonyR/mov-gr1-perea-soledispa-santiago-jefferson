package com.example.deber01_pereasanyiago

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarArtistaActivity : AppCompatActivity() {

    private var obraId: Int? = null
    private lateinit var etTitulo: EditText
    private lateinit var etAnio: EditText
    private lateinit var etTecnica: EditText
    private lateinit var etDimensiones: EditText
    private lateinit var cbDisponible: CheckBox
    private lateinit var btnGuardar: Button
    private lateinit var btnVerArtista: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_obra)

        // Inicializar vistas
        etTitulo = findViewById(R.id.etTitulo)
        etAnio = findViewById(R.id.etAnio)
        etTecnica = findViewById(R.id.etTecnica)
        etDimensiones = findViewById(R.id.etDimensiones)
        cbDisponible = findViewById(R.id.cbDisponible)
        btnGuardar = findViewById(R.id.btnGuardarObra)
        btnVerArtista = findViewById(R.id.verArtista)

        dbHelper = DatabaseHelper(this)

        // Detectar si estamos en modo edición
        obraId = intent.getIntExtra("OBRA_ID", -1).takeIf { it != -1 }

        if (obraId != null) {
            // Modo edición: cargar datos
            cargarDatosObra(obraId!!)
            verificarCamposLlenos()
        }

        // Inicialmente ocultar el botón de "Ver Artista"
        btnVerArtista.visibility = Button.GONE

        // Agregar TextWatcher a los campos de texto
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                verificarCamposLlenos()
            }
        }

        // Asignar el TextWatcher a los campos de texto
        etTitulo.addTextChangedListener(textWatcher)
        etAnio.addTextChangedListener(textWatcher)
        etTecnica.addTextChangedListener(textWatcher)
        etDimensiones.addTextChangedListener(textWatcher)

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val anio = etAnio.text.toString().toIntOrNull()
            val tecnica = etTecnica.text.toString()
            val dimensiones = etDimensiones.text.toString()
            val disponible = cbDisponible.isChecked

            if (titulo.isNotEmpty() && anio != null && tecnica.isNotEmpty() && dimensiones.isNotEmpty()) {
                if (obraId != null) {
                    actualizarObra(obraId!!)
                } else {
                    agregarObra(titulo, anio, tecnica, dimensiones, disponible)
                }
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarDatosObra(id: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Obra WHERE id = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            etTitulo.setText(cursor.getString(cursor.getColumnIndexOrThrow("titulo")))
            etAnio.setText(cursor.getInt(cursor.getColumnIndexOrThrow("anio")).toString())
            etTecnica.setText(cursor.getString(cursor.getColumnIndexOrThrow("tecnica")))
            etDimensiones.setText(cursor.getString(cursor.getColumnIndexOrThrow("dimensiones")))
            cbDisponible.isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("disponible")) == 1
        }
        cursor.close()
    }

    private fun verificarCamposLlenos() {
        if (etTitulo.text.isNotEmpty() && etAnio.text.isNotEmpty() && etTecnica.text.isNotEmpty() && etDimensiones.text.isNotEmpty()) {
            btnVerArtista.visibility = Button.VISIBLE
        } else {
            btnVerArtista.visibility = Button.GONE
        }
    }

    private fun actualizarObra(id: Int) {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("titulo", etTitulo.text.toString())
            put("anio", etAnio.text.toString().toInt())
            put("tecnica", etTecnica.text.toString())
            put("dimensiones", etDimensiones.text.toString())
            put("disponible", if (cbDisponible.isChecked) 1 else 0)
        }

        val filasActualizadas = db.update("Obra", valores, "id = ?", arrayOf(id.toString()))
        if (filasActualizadas > 0) {
            Toast.makeText(this, "Obra actualizada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar la obra", Toast.LENGTH_SHORT).show()
        }
    }

    private fun agregarObra(titulo: String, anio: Int, tecnica: String, dimensiones: String, disponible: Boolean) {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("titulo", titulo)
            put("anio", anio)
            put("tecnica", tecnica)
            put("dimensiones", dimensiones)
            put("disponible", if (disponible) 1 else 0)
        }

        val resultado = db.insert("Obra", null, valores)
        if (resultado != -1L) {
            Toast.makeText(this, "Obra agregada exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al agregar la obra", Toast.LENGTH_SHORT).show()
        }
    }
}
