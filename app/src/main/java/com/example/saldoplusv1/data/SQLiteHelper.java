package com.example.saldoplusv1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.Categoria;
import com.example.saldoplusv1.models.ImpactoFinanciero;

import java.util.ArrayList;
import java.util.List;


// Clase para interactuar con la base de datos SQLite. SQLiteHelper → acceso crudo a la base de datos.
public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "guardar.db";
    private static final int DB_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla apartados con columna impacto
        db.execSQL("CREATE TABLE apartados (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "icono TEXT, " +
                "color TEXT, " +
                "impacto TEXT)");

        // Tabla categorias
        db.execSQL("CREATE TABLE categorias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "apartado_id INTEGER, " +
                "icono TEXT, " +
                "color TEXT, " +
                "FOREIGN KEY(apartado_id) REFERENCES apartados(id))");

        // Tabla transacciones
        db.execSQL("CREATE TABLE transacciones (" +
                "id INTEGER PRIMARY KEY, " +
                "monto REAL, " +
                "fecha INTEGER, " +
                "descripcion TEXT, " +
                "categoria_id INTEGER, " +
                "apartado_id INTEGER, " +
                "FOREIGN KEY(categoria_id) REFERENCES categorias(id), " +
                "FOREIGN KEY(apartado_id) REFERENCES apartados(id))");

        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL)");


        // Datos iniciales: apartados
        db.execSQL("INSERT INTO apartados (nombre, icono, color, impacto) VALUES " +
                "('Ingreso','ic_ingreso','#edf3ec','INGRESO')," +
                "('Gasto','ic_gasto','#fdebec','GASTO')");

        // Datos iniciales: categorías
        db.execSQL("INSERT INTO categorias (nombre, apartado_id, icono, color) VALUES " +
                "('Sueldo', 1, 'ic_sueldo', '#4CAF50')," +
                "('Comida', 2, 'ic_comida', '#F44336')," +
                "('Compras', 2, 'ic_compras', '#F44336')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transacciones");
        db.execSQL("DROP TABLE IF EXISTS categorias");
        db.execSQL("DROP TABLE IF EXISTS apartados");
        onCreate(db);
    }

    /**
     * Devuelve todos los apartados existentes en la BD.
     */
    public List<Apartado> obtenerTodosLosApartados() {
        List<Apartado> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, nombre, icono, color, impacto FROM apartados", null);
        while (c.moveToNext()) {
            int id      = c.getInt(0);
            String nom  = c.getString(1);
            String ico  = c.getString(2);
            String col  = c.getString(3);
            ImpactoFinanciero imp = ImpactoFinanciero.valueOf(c.getString(4));
            lista.add(new Apartado(id, nom, ico, col, imp));
        }
        c.close();
        return lista;
    }

    /**
     * Inserta un nuevo apartado y devuelve su ID.
     */
    public long insertarApartado(Apartado ap) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre", ap.getNombre());
        cv.put("icono", ap.getIcono());
        cv.put("color", ap.getColor());
        cv.put("impacto", ap.getImpacto().name());
        long id = db.insert("apartados", null, cv);
        // Opcional: asignar el ID al objeto
        ap.setId((int) id);
        return id;
    }

    /**
     * Devuelve todas las categorías que pertenecen al apartado dado.
     */
    public List<Categoria> obtenerCategoriasPorApartado(int apartadoId) {
        List<Categoria> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, nombre, icono, color, apartado_id FROM categorias WHERE apartado_id = ?",
                new String[]{ String.valueOf(apartadoId) }
        );
        while (c.moveToNext()) {
            int id       = c.getInt(0);
            String nom   = c.getString(1);
            String ico   = c.getString(2);
            String col   = c.getString(3);
            int apartId  = c.getInt(4);
            lista.add(new Categoria(id, nom, ico, col, apartId));
        }
        c.close();
        return lista;
    }

    /**
     * Inserta una nueva categoría y devuelve su ID.
     */
    public long insertarCategoria(Categoria cat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre",       cat.getNombre());
        cv.put("icono",        cat.getIcono());
        cv.put("color",        cat.getColor());
        cv.put("apartado_id",  cat.getApartadoId());
        long id = db.insert("categorias", null, cv);
        cat.setId((int)id);
        return id;
    }

    public Apartado obtenerApartadoPorId(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Apartado apartado = null;

        Cursor cursor = db.rawQuery("SELECT * FROM apartados WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String icono = cursor.getString(cursor.getColumnIndexOrThrow("icono"));
            String color = cursor.getString(cursor.getColumnIndexOrThrow("color"));
            String impactoStr = cursor.getString(cursor.getColumnIndexOrThrow("impacto")); // Asegúrate que sea tipo TEXT
            ImpactoFinanciero impacto = ImpactoFinanciero.valueOf(impactoStr); // Enum

            apartado = new Apartado(nombre, icono, color, impacto);
            apartado.setId(id);
        }

        if (cursor != null) cursor.close();
        db.close();
        return apartado;
    }

}