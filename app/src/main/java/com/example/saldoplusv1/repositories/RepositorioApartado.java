package com.example.saldoplusv1.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saldoplusv1.data.SQLiteHelper;
import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.ImpactoFinanciero;

import java.util.ArrayList;
import java.util.List;

public class RepositorioApartado {
    private final SQLiteHelper helper;

    public RepositorioApartado(Context context) {
        this.helper = new SQLiteHelper(context);
    }

    public RepositorioApartado(SQLiteHelper helper) {
        this.helper = helper;
    }

    public List<Apartado> obtenerTodos() {
        List<Apartado> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, nombre, icono, color, impacto FROM apartados", null);
        while (c.moveToNext()) {
            lista.add(new Apartado(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    ImpactoFinanciero.valueOf(c.getString(4))
            ));
        }
        c.close();
        return lista;
    }

    public long insertar(Apartado ap) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre", ap.getNombre());
        cv.put("icono", ap.getIcono());
        cv.put("color", ap.getColor());
        cv.put("impacto", ap.getImpacto().name());
        long id = db.insert("apartados", null, cv);
        ap.setId((int) id);
        return id;
    }

    public Apartado obtenerPorId(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Apartado apartado = null;
        Cursor c = db.rawQuery("SELECT nombre, icono, color, impacto FROM apartados WHERE id = ?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            apartado = new Apartado(id, c.getString(0), c.getString(1), c.getString(2), ImpactoFinanciero.valueOf(c.getString(3)));
        }
        c.close();
        return apartado;
    }

    public Apartado obtenerPorNombre(String nombre) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Apartado ap = null;
        Cursor c = db.rawQuery("SELECT id, nombre, icono, color, impacto FROM apartados WHERE LOWER(nombre) = LOWER(?) LIMIT 1", new String[]{nombre});
        if (c.moveToFirst()) {
            ap = new Apartado(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    ImpactoFinanciero.valueOf(c.getString(4))
            );
        }
        c.close();
        return ap;
    }
}
