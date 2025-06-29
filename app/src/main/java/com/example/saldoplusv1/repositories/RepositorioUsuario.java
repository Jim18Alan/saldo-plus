package com.example.saldoplusv1.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saldoplusv1.db.SQLiteHelper;
import com.example.saldoplusv1.models.Usuario;

public class RepositorioUsuario {
    private final SQLiteHelper helper;

    public RepositorioUsuario(Context context) {
        this.helper = new SQLiteHelper(context);
    }

    /**
     * Guarda un nuevo usuario solo si no hay uno ya registrado.
     */
    public void guardarUsuario(Usuario usuario) {
        if (!isRegistered()) {
            try (SQLiteDatabase db = helper.getWritableDatabase()) {
                ContentValues valores = new ContentValues();
                valores.put("nombre", usuario.getNombre());
                db.insert("usuarios", null, valores);
            }
        }
    }

    /**
     * Verifica si ya hay un usuario guardado.
     */
    public boolean isRegistered() {
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM usuarios", null)) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count > 0;
            }
        }
        return false;
    }

    /**
     * Obtiene el único usuario registrado.
     */
    public Usuario obtenerUsuario() {
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM usuarios LIMIT 1", null)) {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                return new Usuario(id, nombre);
            }
        }
        return null;
    }

    /**
     * Actualiza el único usuario existente.
     */
    public void actualizarUsuario(String nuevoNombre) {
        Usuario usuario = obtenerUsuario();
        if (usuario != null) {
            try (SQLiteDatabase db = helper.getWritableDatabase()) {
                ContentValues valores = new ContentValues();
                valores.put("nombre", nuevoNombre);
                db.update("usuarios", valores, "id = ?", new String[]{String.valueOf(usuario.getId())});
            }
        }
    }

    /**
     * Elimina el usuario (si algún día se requiere reiniciar).
     */
    public void eliminarUsuario() {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete("usuarios", null, null);
        }
    }

    public String obtenerNombre() {
        Usuario usuario = obtenerUsuario();
        if (usuario != null) {
            return usuario.getNombre();
        }
        return null;
    }
}
