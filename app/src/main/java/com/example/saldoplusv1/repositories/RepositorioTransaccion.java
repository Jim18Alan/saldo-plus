package com.example.saldoplusv1.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saldoplusv1.data.SQLiteHelper;
import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.ImpactoFinanciero;
import com.example.saldoplusv1.models.Movimiento;
import com.example.saldoplusv1.models.Transaccion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RepositorioTransaccion {
    private final SQLiteHelper helper;

    public RepositorioTransaccion(Context context) {
        this.helper = new SQLiteHelper(context);
    }

    public RepositorioTransaccion(SQLiteHelper helper) {
        this.helper = helper;
    }


    public void agregar(Transaccion tx) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            ContentValues valores = new ContentValues();
            valores.put("monto", tx.getMonto());
            valores.put("fecha", tx.getFecha().getTime());
            valores.put("descripcion", tx.getDescripcion());
            valores.put("categoria_id", tx.getCategoria().getId());
            valores.put("apartado_id", tx.getApartado().getId());
            db.insertOrThrow("transacciones", null, valores);
        }
    }

    public void actualizar(Transaccion tx) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            ContentValues valores = new ContentValues();
            valores.put("monto", tx.getMonto());
            valores.put("fecha", tx.getFecha().getTime());
            valores.put("descripcion", tx.getDescripcion());
            valores.put("categoria_id", tx.getCategoria().getId());
            valores.put("apartado_id", tx.getApartado().getId());
            db.update("transacciones", valores, "id = ?", new String[]{String.valueOf(tx.getId())});
        }
    }

    public void eliminar(Transaccion tx) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete("transacciones", "id = ?", new String[]{String.valueOf(tx.getId())});
        }
    }

    public List<Transaccion> obtenerTodas() {
        return consultar("SELECT * FROM transacciones", null);
    }

    public List<Transaccion> obtenerPorTipo(boolean ingreso) {
        String tipo = ingreso ? "ingreso" : "gasto";
        return consultar(
                "SELECT * FROM transacciones t JOIN apartados a ON t.apartado_id = a.id WHERE LOWER(a.nombre) = ?",
                new String[]{tipo}
        );
    }

    public List<Transaccion> buscar(String palabra) {
        return consultar("SELECT * FROM transacciones WHERE descripcion LIKE ?", new String[]{"%" + palabra + "%"});
    }

    public List<Transaccion> filtrarPorRango(Date desde, Date hasta) {
        return consultar(
                "SELECT * FROM transacciones WHERE fecha BETWEEN ? AND ?",
                new String[]{String.valueOf(desde.getTime()), String.valueOf(hasta.getTime())}
        );
    }

    private List<Transaccion> consultar(String sql, String[] args) {
        List<Transaccion> lista = new ArrayList<>();
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor c = db.rawQuery(sql, args)) {

            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow("id"));
                double monto = c.getDouble(c.getColumnIndexOrThrow("monto"));
                Date fecha = new Date(c.getLong(c.getColumnIndexOrThrow("fecha")));
                String descripcion = c.getString(c.getColumnIndexOrThrow("descripcion"));
                int categoriaId = c.getInt(c.getColumnIndexOrThrow("categoria_id"));
                int apartadoId = c.getInt(c.getColumnIndexOrThrow("apartado_id"));

                Categoria categoria = cargarCategoria(db, categoriaId);
                Apartado apartado = cargarApartado(db, apartadoId);

                Transaccion tx = new Movimiento(id, monto, fecha, descripcion, categoria, apartado);
                lista.add(tx);
            }
        }
        return lista;
    }

    private Categoria cargarCategoria(SQLiteDatabase db, int id) {
        try (Cursor c = db.rawQuery(
                "SELECT nombre, icono, color, apartado_id FROM categorias WHERE id = ?",
                new String[]{String.valueOf(id)})) {
            if (c.moveToFirst()) {
                return new Categoria(id, c.getString(0), c.getString(1), c.getString(2), c.getInt(3));
            }
        }
        return null;
    }

    private Apartado cargarApartado(SQLiteDatabase db, int id) {
        try (Cursor c = db.rawQuery(
                "SELECT nombre, icono, color, impacto FROM apartados WHERE id = ?",
                new String[]{String.valueOf(id)})) {
            if (c.moveToFirst()) {
                return new Apartado(id, c.getString(0), c.getString(1), c.getString(2),
                        ImpactoFinanciero.valueOf(c.getString(3)));
            }
        }
        return null;
    }
}
