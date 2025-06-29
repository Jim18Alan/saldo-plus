package com.example.saldoplusv1.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREF_NAME = "user_prefs3";
    private static final String KEY_NOMBRE = "nombre_usuario";

    private final SharedPreferences prefs;

    public UserPreferences(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Guardar el nombre
    public void guardarNombre(String nombre) {
        prefs.edit().putString(KEY_NOMBRE, nombre).apply();
    }

    // Leer el nombre
    public String obtenerNombre() {
        return prefs.getString(KEY_NOMBRE, "");
    }

    public boolean isRegistered() {
        return prefs.getString(KEY_NOMBRE, null) != null;
    }

    // Limpiar el nombre (opcional)
    public void borrarNombre() {
        prefs.edit().remove(KEY_NOMBRE).apply();
    }
}
