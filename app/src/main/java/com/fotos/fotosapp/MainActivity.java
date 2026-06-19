package com.fotos.fotosapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.fotos.fotosapp.dao.PersonaDAO;
import com.fotos.fotosapp.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo;
    private ImageView ivVistaPrevia;
    private Button btnTomarFoto, btnGuardar, btnVerLista;

    private Bitmap fotoCapturada = null;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forzar a la aplicación a usar siempre el modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        // Vincular UI
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        ivVistaPrevia = findViewById(R.id.ivVistaPrevia);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVerLista = findViewById(R.id.btnVerLista);

        // Configurar receptor de captura de imagen
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        fotoCapturada = (Bitmap) extras.get("data");
                        ivVistaPrevia.setImageBitmap(fotoCapturada);
                    } else {
                        Toast.makeText(this, "Captura de foto cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Listener del botón de la cámara
        btnTomarFoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(this, "No hay sensor de cámara disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para almacenar información en SQLite
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();

            if (nombre.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Debe completar todos los datos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fotoCapturada == null) {
                Toast.makeText(this, "Debe tomar una fotografía obligatoriamente", Toast.LENGTH_SHORT).show();
                return;
            }

            // Conversión y compresión a Base64
            String fotoBase64 = ImageUtils.bitmapToBase64(fotoCapturada);

            // Inserción en SQLite
            PersonaDAO dao = new PersonaDAO(this);
            dao.open();
            long res = dao.insertarPersona(nombre, correo, fotoBase64);
            dao.close();

            if (res > 0) {
                Toast.makeText(this, "Persona registrada exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
            } else {
                Toast.makeText(this, "Error crítico al guardar", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para ir a la siguiente pantalla de consulta
        btnVerLista.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ConsultaActivity.class);
            startActivity(i);
        });
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etCorreo.setText("");
        ivVistaPrevia.setImageResource(android.R.drawable.ic_menu_camera);
        fotoCapturada = null;
    }
}