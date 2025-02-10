package com.example.deber01_pereasanyiago

import android.annotation.SuppressLint
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

class AgregarArtistaActivity : AppCompatActivity() {

    private var artistaId: Int? = null
    private lateinit var etNombre: EditText
    private lateinit var etEdad: EditText
    private lateinit var cbActivo: CheckBox
    private lateinit var etNumeroObras: EditText
    private lateinit var etPromedioValorObras: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnVerArtista: Button
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_artista)

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre)
        etEdad = findViewById(R.id.etEdad)
        cbActivo = findViewById(R.id.cbActivo)
        etNumeroObras = findViewById(R.id.etNumeroObras)
        etPromedioValorObras = findViewById(R.id.etPromedioValorObras)
        btnGuardar = findViewById(R.id.btnGuardarArtista)
        btnVerArtista = findViewById(R.id.verArtista)

        dbHelper = DatabaseHelper(this)

        // Detectar si estamos en modo edición
        artistaId = intent.getIntExtra("ARTISTA_ID", -1).takeIf { it != -1 }

        if (artistaId != null) {
            cargarDatosArtista(artistaId!!)
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val edad = etEdad.text.toString().toIntOrNull()
            val activo = cbActivo.isChecked
            val numeroObras = etNumeroObras.text.toString().toIntOrNull()
            val promedioValorObras = etPromedioValorObras.text.toString().toDoubleOrNull()

            if (nombre.isNotEmpty() && edad != null && numeroObras != null && promedioValorObras != null) {
                if (artistaId != null) {
                    actualizarArtista(artistaId!!, nombre, edad, activo, numeroObras, promedioValorObras)
                } else {
                    agregarArtista(nombre, edad, activo, numeroObras, promedioValorObras)
                }
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        val botonGoogleMaps = findViewById<Button>(R.id.verArtista)
        botonGoogleMaps.setOnClickListener {
            val intent = Intent(this,GGoogleMaps::class.java)
            startActivity(intent)
        }
    }

    private fun cargarDatosArtista(id: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Artista WHERE id = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            etNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")))
            etEdad.setText(cursor.getInt(cursor.getColumnIndexOrThrow("edad")).toString())
            cbActivo.isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
            etNumeroObras.setText(cursor.getInt(cursor.getColumnIndexOrThrow("numeroObras")).toString())
            etPromedioValorObras.setText(cursor.getDouble(cursor.getColumnIndexOrThrow("promedioValorObras")).toString())
        }
        cursor.close()
    }


    private fun actualizarArtista(id: Int, nombre: String, edad: Int, activo: Boolean, numeroObras: Int, promedioValorObras: Double) {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("edad", edad)
            put("activo", if (activo) 1 else 0)
            put("numeroObras", numeroObras)
            put("promedioValorObras", promedioValorObras)
        }

        val filasActualizadas = db.update("Artista", valores, "id = ?", arrayOf(id.toString()))
        if (filasActualizadas > 0) {
            Toast.makeText(this, "Artista actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el artista", Toast.LENGTH_SHORT).show()
        }
    }

    private fun agregarArtista(nombre: String, edad: Int, activo: Boolean, numeroObras: Int, promedioValorObras: Double) {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("edad", edad)
            put("activo", if (activo) 1 else 0)
            put("numeroObras", numeroObras)
            put("promedioValorObras", promedioValorObras)
        }

        val resultado = db.insert("Artista", null, valores)
        if (resultado != -1L) {
            Toast.makeText(this, "Artista agregado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al agregar el artista", Toast.LENGTH_SHORT).show()
        }
    }
}
