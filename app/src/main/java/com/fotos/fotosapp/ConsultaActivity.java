package com.fotos.fotosapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.fotos.fotosapp.dao.PersonaDAO;
import com.fotos.fotosapp.models.Persona;
import com.fotos.fotosapp.utils.ImageUtils;

import java.util.List;

public class ConsultaActivity extends AppCompatActivity {

    private ImageView ivFotoConsultada;
    private TextView tvNombreConsultado, tvCorreoConsultado, tvFechaConsultada;
    private Button btnAnterior, btnSiguiente, btnVolver;

    private List<Persona> listaPersonas;
    private int indiceActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Forzar a la aplicación a usar siempre el modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_consulta);

        // Vincular los componentes visuales del XML
        ivFotoConsultada = findViewById(R.id.ivFotoConsultada);
        tvNombreConsultado = findViewById(R.id.tvNombreConsultado);
        tvCorreoConsultado = findViewById(R.id.tvCorreoConsultado);
        tvFechaConsultada = findViewById(R.id.tvFechaConsultada);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnVolver = findViewById(R.id.btnVolver);

        // Instanciar el DAO y cargar la lista desde SQLite
        PersonaDAO dao = new PersonaDAO(this);
        dao.open();
        listaPersonas = dao.obtenerTodasLasPersonas();
        dao.close();

        // Validar si existen registros almacenados en la tabla
        if (listaPersonas != null && !listaPersonas.isEmpty()) {
            mostrarRegistro(indiceActual);
        } else {
            Toast.makeText(this, "No se encontraron registros en SQLite", Toast.LENGTH_LONG).show();
            tvNombreConsultado.setText("Nombre: Sin registros");
        }

        // Evento para retroceder al registro previo
        btnAnterior.setOnClickListener(v -> {
            if (listaPersonas == null || listaPersonas.isEmpty()) return;
            if (indiceActual > 0) {
                indiceActual--;
                mostrarRegistro(indiceActual);
            } else {
                Toast.makeText(this, "Primer registro alcanzado", Toast.LENGTH_SHORT).show();
            }
        });

        // Evento para avanzar al siguiente registro
        btnSiguiente.setOnClickListener(v -> {
            if (listaPersonas == null || listaPersonas.isEmpty()) return;
            if (indiceActual < listaPersonas.size() - 1) {
                indiceActual++;
                mostrarRegistro(indiceActual);
            } else {
                Toast.makeText(this, "Último registro alcanzado", Toast.LENGTH_SHORT).show();
            }
        });

        // Evento para finalizar la actividad y regresar
        btnVolver.setOnClickListener(v -> finish());
    }

    private void mostrarRegistro(int posicion) {
        Persona persona = listaPersonas.get(posicion);

        // Setear los datos de texto planos
        tvNombreConsultado.setText("Nombre: " + persona.getNombre());
        tvCorreoConsultado.setText("Correo: " + persona.getCorreo());
        tvFechaConsultada.setText("Fecha Reg: " + persona.getFechaRegistro());

        // Decodificación: Pasar de Base64 de SQLite a Bitmap en pantalla
        if (persona.getFotoBase64() != null && !persona.getFotoBase64().isEmpty()) {
            Bitmap bitmap = ImageUtils.base64ToBitmap(persona.getFotoBase64());
            ivFotoConsultada.setImageBitmap(bitmap);
        } else {
            ivFotoConsultada.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}