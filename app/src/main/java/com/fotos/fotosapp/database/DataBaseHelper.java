package com.fotos.fotosapp.database;

import android.content.Context;
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
}
