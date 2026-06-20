package com.fotos.fotosapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.fotos.fotosapp.database.DataBaseHelper; // Importamos el helper para usar el eliminar
import com.fotos.fotosapp.dao.PersonaDAO;
import com.fotos.fotosapp.models.Persona;
import com.fotos.fotosapp.utils.ImageUtils;

import java.util.List;

public class ConsultaActivity extends AppCompatActivity {

    private ImageView ivFotoConsultada;
    private TextView tvNombreConsultado, tvCorreoConsultado, tvFechaConsultada;
    private Button btnAnterior, btnSiguiente, btnVolver, btnEliminar, btnBuscar; // Agregado btnBuscar

    private List<Persona> listaPersonas;
    private int indiceActual = 0;
    private DataBaseHelper dbHelper; // Instancia global del Helper

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
        btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscar = findViewById(R.id.btnBuscar); // Inicializado el nuevo botón de búsqueda

        // Instanciar el helper de la base de datos
        dbHelper = new DataBaseHelper(this);

        // Instanciar el DAO y cargar la lista desde SQLite
        PersonaDAO dao = new PersonaDAO(this);
        dao.open();
        listaPersonas = dao.obtenerTodasLasPersonas();
        dao.close();

        // Validar si existen registros almacenados en la tabla
        if (listaPersonas != null && !listaPersonas.isEmpty()) {
            mostrarRegistro(indiceActual);
        } else {
            pantallaSinRegistros();
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

        // =======================================================
        // ACCIÓN DEL RETO EXTRA: ELIMINAR EL REGISTRO ACTUAL
        // =======================================================
        btnEliminar.setOnClickListener(v -> {
            if (listaPersonas == null || listaPersonas.isEmpty()) {
                Toast.makeText(this, "No hay registros para eliminar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el objeto de la persona que se visualiza en pantalla
            Persona personaActual = listaPersonas.get(indiceActual);

            // Eliminar físicamente en SQLite usando el ID autoincremental
            boolean borradoExitoso = dbHelper.eliminarPersona(personaActual.getId());

            if (borradoExitoso) {
                Toast.makeText(this, "Registro eliminado de SQLite", Toast.LENGTH_SHORT).show();

                // Remover de la lista local en memoria para refrescar la pantalla inmediatamente
                listaPersonas.remove(indiceActual);

                // Validar la navegación tras el borrado
                if (listaPersonas.isEmpty()) {
                    // Si ya no quedan personas en la tabla, limpiamos los componentes
                    pantallaSinRegistros();
                } else {
                    // Si eliminamos el último elemento de la lista, retrocedemos una posición
                    if (indiceActual >= listaPersonas.size()) {
                        indiceActual = listaPersonas.size() - 1;
                    }
                    // Refrescar los textos y la imagen con la nueva persona en la posición
                    mostrarRegistro(indiceActual);
                }
            } else {
                Toast.makeText(this, "Error al intentar borrar el registro", Toast.LENGTH_SHORT).show();
            }
        });

        // =======================================================
        // ACCIÓN DEL RETO EXTRA: BUSCAR REGISTRO POR NOMBRE CORREGIDO
        // =======================================================
        btnBuscar.setOnClickListener(v -> {
            if (listaPersonas == null || listaPersonas.isEmpty()) {
                Toast.makeText(this, "No hay registros cargados para realizar búsquedas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear un cuadro de entrada flotante para el cuadro de diálogo
            final android.widget.EditText inputBuscar = new android.widget.EditText(this);
            inputBuscar.setHint("Escribe el nombre aquí...");

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Buscar por Nombre")
                    .setMessage("Ingrese el nombre completo o parcial de la persona:")
                    .setView(inputBuscar)
                    .setPositiveButton("Buscar", (dialog, which) -> {
                        String textoBusqueda = inputBuscar.getText().toString().trim().toLowerCase();

                        if (!textoBusqueda.isEmpty()) {
                            int posicionEncontrada = -1;

                            // Buscamos directamente el índice de coincidencia en nuestra lista en memoria
                            for (int i = 0; i < listaPersonas.size(); i++) {
                                String nombrePersona = listaPersonas.get(i).getNombre();
                                if (nombrePersona != null && nombrePersona.toLowerCase().contains(textoBusqueda)) {
                                    posicionEncontrada = i;
                                    break; // Rompe el ciclo en la primera coincidencia
                                }
                            }

                            if (posicionEncontrada != -1) {
                                // Cambiar al índice hallado y renderizar los componentes con precisión
                                indiceActual = posicionEncontrada;
                                mostrarRegistro(indiceActual);
                                Toast.makeText(this, "Registro localizado con éxito", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "No se encontraron coincidencias en SQLite", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "Por favor, escribe un criterio de búsqueda", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
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

    // Método auxiliar para limpiar los controles visuales cuando la base de datos se queda vacía
    private void pantallaSinRegistros() {
        tvNombreConsultado.setText("Nombre: Sin registros");
        tvCorreoConsultado.setText("Correo: ");
        tvFechaConsultada.setText("Fecha Reg: ");
        ivFotoConsultada.setImageResource(android.R.drawable.ic_menu_gallery);
        Toast.makeText(this, "No se encontraron registros en SQLite", Toast.LENGTH_LONG).show();
    }
}