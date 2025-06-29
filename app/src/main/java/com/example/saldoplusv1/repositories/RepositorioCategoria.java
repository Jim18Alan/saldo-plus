package com.example.saldoplusv1.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saldoplusv1.data.SQLiteHelper;
import com.example.saldoplusv1.models.Categoria;

import java.util.ArrayList;
import java.util.List;

public class RepositorioCategoria {
    private final SQLiteHelper helper;

    public RepositorioCategoria(Context context) {
        this.helper = new SQLiteHelper(context);
    }

    public RepositorioCategoria(SQLiteHelper helper) {
        this.helper = helper;
    }

    public List<Categoria> obtenerPorApartado(int apartadoId) {
        List<Categoria> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, nombre, icono, color, apartado_id FROM categorias WHERE apartado_id = ?", new String[]{String.valueOf(apartadoId)});
        while (c.moveToNext()) {
            lista.add(new Categoria(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getInt(4)
            ));
        }
        c.close();
        return lista;
    }

    public long insertar(Categoria cat) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre", cat.getNombre());
        cv.put("icono", cat.getIcono());
        cv.put("color", cat.getColor());
        cv.put("apartado_id", cat.getApartadoId());
        long id = db.insert("categorias", null, cv);
        cat.setId((int) id);
        return id;
    }

    public Categoria obtenerPorNombre(String nombre) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Categoria categoria = null;
        Cursor c = db.rawQuery("SELECT id, nombre, icono, color, apartado_id FROM categorias WHERE LOWER(nombre) = LOWER(?) LIMIT 1", new String[]{nombre});
        if (c.moveToFirst()) {
            categoria = new Categoria(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getInt(4)
            );
        }
        c.close();
        return categoria;
    }
}
