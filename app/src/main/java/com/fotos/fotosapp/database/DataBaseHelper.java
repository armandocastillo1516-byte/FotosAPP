package com.fotos.fotosapp.database;

import android.content.Context;
import android.database.Cursor; // Importación necesaria para la búsqueda
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ComunidadFotos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLA_PERSONAS = "personas";
    public static final String COL_ID = "id";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_CORREO = "correo";
    public static final String COL_FOTO = "foto_base64";
    public static final String COL_FECHA = "fecha_registro";

    private static final String CREAR_TABLA_PERSONAS = "CREATE TABLE " + TABLA_PERSONAS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NOMBRE + " TEXT, " +
            COL_CORREO + " TEXT, " +
            COL_FOTO + " TEXT, " +
            COL_FECHA + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_PERSONAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PERSONAS);
        onCreate(db);
    }

    // ==========================================
    // METODO DEL RETO EXTRA: ELIMINAR REGISTRO
    // ==========================================
    public boolean eliminarPersona(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Ejecuta el borrado físico usando tus constantes
        int resultado = db.delete(TABLA_PERSONAS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        // Retorna true si eliminó al menos una fila (operación exitosa)
        return resultado > 0;
    }

    // ==========================================
    // METODO DEL RETO EXTRA: BUSCAR POR NOMBRE
    // ==========================================
    public int buscarPosicionPorNombre(String nombreBuscar) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consultamos las columnas ID y Nombre usando tus constantes de forma ordenada
        String query = "SELECT " + COL_ID + ", " + COL_NOMBRE + " FROM " + TABLA_PERSONAS;
        Cursor cursor = db.rawQuery(query, null);

        int posicion = -1;
        int contador = 0;

        if (cursor.moveToFirst()) {
            do {
                String nombrePersona = cursor.getString(1); // Columna COL_NOMBRE

                // Compara si el nombre de la BD contiene el texto buscado (ignorando mayúsculas/minúsculas)
                if (nombrePersona != null && nombrePersona.toLowerCase().contains(nombreBuscar.toLowerCase())) {
                    posicion = contador; // Mapea el índice exacto de la lista local
                    break;
                }
                contador++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return posicion; // Retorna el índice encontrado, o -1 si no hay coincidencias
    }
}