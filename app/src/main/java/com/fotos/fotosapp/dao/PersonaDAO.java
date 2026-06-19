package com.fotos.fotosapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fotos.fotosapp.database.DataBaseHelper;
import com.fotos.fotosapp.database.DataBaseHelper;
import com.fotos.fotosapp.models.Persona;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public PersonaDAO(Context context) {
        dbHelper = new DataBaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertarPersona(String nombre, String correo, String fotoBase64) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COL_NOMBRE, nombre);
        values.put(DataBaseHelper.COL_CORREO, correo);
        values.put(DataBaseHelper.COL_FOTO, fotoBase64);
        return database.insert(DataBaseHelper.TABLA_PERSONAS, null, values);
    }

    public List<Persona> obtenerTodasLasPersonas() {
        List<Persona> lista = new ArrayList<>();
        Cursor cursor = database.query(DataBaseHelper.TABLA_PERSONAS, null, null, null, null, null, DataBaseHelper.COL_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Persona p = new Persona();
                p.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_ID)));
                p.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_NOMBRE)));
                p.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CORREO)));
                p.setFotoBase64(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_FOTO)));
                p.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_FECHA)));
                lista.add(p);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }
}