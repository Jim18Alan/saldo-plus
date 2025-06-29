// La declaración 'package' indica que esta clase pertenece al paquete 'data', encargado de la lógica de datos.
package com.example.saldoplusv1.data;

// Importaciones de librerías necesarias de Android para el manejo de la base de datos SQLite.
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Importación para el manejo de logs (mensajes de depuración).

// Importaciones de las clases de nuestro modelo de datos.
import com.example.saldoplusv1.models.Gasto;
import com.example.saldoplusv1.models.Ingreso;
import com.example.saldoplusv1.models.Movimiento;

// Importaciones de librerías de Java para manejar listas.
import java.util.ArrayList;
import java.util.List;

/**
 * REPRESENTA: El manejador principal de la base de datos SQLite de la aplicación.
 * PROPOSITO: Encapsular toda la lógica de creación, actualización y acceso a la base de datos.
 * HERENCIA: Extiende de SQLiteOpenHelper, la clase base de Android para gestionar el ciclo de vida de una BD.
 */
public class DBHelper extends SQLiteOpenHelper {

    // --- CONSTANTES DE LA BASE DE DATOS ---
    // PROPOSITO: Usar constantes evita errores de tipeo y facilita futuras modificaciones.

    /**
     * CONSTANTE: Nombre del archivo de la base de datos que se creará en el dispositivo.
     */
    private static final String DATABASE_NAME = "SaldoPlus.db";

    /**
     * CONSTANTE: Versión de la base de datos. Si cambiamos el esquema (la estructura de las tablas),
     * este número DEBE incrementarse para que el método onUpgrade() se ejecute.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * CONSTANTE: Nombre de la única tabla que tendremos en nuestra base de datos.
     */
    private static final String TABLE_MOVIMIENTOS = "movimientos";

    // --- NOMBRES DE LAS COLUMNAS DE LA TABLA "movimientos" ---

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MONTO = "monto";
    private static final String COLUMN_FECHA = "fecha";
    private static final String COLUMN_DESCRIPCION = "descripcion";
    private static final String COLUMN_TIPO = "tipo"; // Columna CLAVE para saber si es "Ingreso" o "Gasto".
    private static final String COLUMN_CATEGORIA = "categoria"; // Será NULL para los ingresos.

    /**
     * CONSTRUCTOR: Se utiliza para crear una instancia del DBHelper.
     *
     * @param context El contexto de la aplicación (normalmente una Activity o el Application Context),
     * necesario para que Android sepa dónde crear la base de datos.
     */
    public DBHelper(Context context) {
        // INVOCACIÓN AL CONSTRUCTOR PADRE: Se llama al constructor de SQLiteOpenHelper.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * MÉTODO SOBRESCRITO: Se ejecuta automáticamente UNA SOLA VEZ cuando la base de datos se crea por primera vez.
     * PROPOSITO: Aquí es donde definimos la estructura de nuestras tablas.
     *
     * @param db La instancia de la base de datos que se acaba de crear.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SENTENCIA SQL: Se define la consulta para crear la tabla "movimientos".
        // DOCUMENTACIÓN DE LA ESTRUCTURA SQL:
        // CREATE TABLE movimientos (
        //   id INTEGER PRIMARY KEY AUTOINCREMENT, -> Clave primaria autoincremental.
        //   monto REAL NOT NULL,                 -> Monto no puede ser nulo. REAL es para decimales.
        //   fecha TEXT NOT NULL,                   -> Fecha como texto.
        //   descripcion TEXT,                      -> Descripción (puede ser nulo).
        //   tipo TEXT NOT NULL,                    -> Tipo ("Ingreso" o "Gasto").
        //   categoria TEXT                         -> Categoría (puede ser nulo, para los ingresos).
        // );
        String CREATE_TABLE = "CREATE TABLE " + TABLE_MOVIMIENTOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MONTO + " REAL NOT NULL,"
                + COLUMN_FECHA + " TEXT NOT NULL,"
                + COLUMN_DESCRIPCION + " TEXT,"
                + COLUMN_TIPO + " TEXT NOT NULL,"
                + COLUMN_CATEGORIA + " TEXT" + ")";

        // EJECUCIÓN DEL SQL: Se ejecuta la sentencia para crear la tabla.
        db.execSQL(CREATE_TABLE);
    }

    /**
     * MÉTODO SOBRESCRITO: Se ejecuta cuando incrementamos DATABASE_VERSION.
     * PROPOSITO: Sirve para migrar datos o reestructurar la BD en una actualización de la app.
     * Por ahora, la estrategia simple es eliminar la tabla vieja y crearla de nuevo.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Elimina la tabla si ya existía.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIMIENTOS);
        // Llama a onCreate() para crear la nueva versión de la tabla.
        onCreate(db);
    }

    /**
     * MÉTODO: Agrega un nuevo movimiento (sea Ingreso o Gasto) a la base de datos.
     * USO DE POLIMORFISMO: Acepta un objeto 'Movimiento', y dinámicamente determina
     * si es una instancia ('instanceof') de Ingreso o Gasto para guardarlo correctamente.
     *
     * @param movimiento El objeto (puede ser Ingreso o Gasto) a guardar.
     */
    public void agregarMovimiento(Movimiento movimiento) {
        // Se obtiene una referencia a la base de datos en modo escritura.
        SQLiteDatabase db = this.getWritableDatabase();

        // ContentValues es una clase de Android que permite definir un mapa de "columna -> valor"
        // para ser insertado en una fila de la base de datos.
        ContentValues values = new ContentValues();
        values.put(COLUMN_MONTO, movimiento.getMonto());
        values.put(COLUMN_FECHA, movimiento.getFecha());
        values.put(COLUMN_DESCRIPCION, movimiento.getDescripcion());
        values.put(COLUMN_TIPO, movimiento.getTipo()); // Llama al método polimórfico getTipo().

        // IF-ELSE: Se verifica el tipo de objeto para manejar atributos específicos.
        // Este es un punto clave de la aplicación de la herencia.
        if (movimiento instanceof Gasto) {
            // Si el movimiento es una instancia de Gasto, obtenemos su categoría.
            // Se hace un "casting" para poder acceder al método getCategoria().
            Gasto gasto = (Gasto) movimiento;
            values.put(COLUMN_CATEGORIA, gasto.getCategoria());
        } else {
            // Si es un Ingreso, la columna categoría se deja explícitamente como null.
            values.putNull(COLUMN_CATEGORIA);
        }

        // --- MANEJO DE ERRORES (RUBRICA) ---
        // El bloque try/catch/finally asegura que la app no se cierre inesperadamente
        // si ocurre un error durante la inserción y que la conexión a la BD siempre se cierre.
        try {
            // Inserción del nuevo registro en la tabla.
            // El método insert() devuelve el ID de la nueva fila, o -1 si hubo un error.
            long id = db.insert(TABLE_MOVIMIENTOS, null, values);
            if (id == -1) {
                // Si la inserción falla, se registra un mensaje de error en el Logcat.
                Log.e("DBHelper", "Error al insertar movimiento en la base de datos.");
            }
        } catch (SQLException e) {
            // Si ocurre una excepción SQL, se imprime en el Logcat para depuración.
            Log.e("DBHelper", "Error SQL durante la inserción: " + e.getMessage());
        } finally {
            // El bloque 'finally' SIEMPRE se ejecuta, haya o no un error.
            // Es el lugar ideal para asegurarse de cerrar la conexión a la base de datos.
            if (db != null && db.isOpen()) {
                db.close(); // Cierra la base de datos para liberar recursos.
            }
        }
    }

    /**
     * MÉTODO: Obtiene una lista de TODOS los movimientos de la base de datos.
     * USO DE POLIMORFISMO: Lee los registros y crea objetos Ingreso o Gasto según el valor
     * de la columna 'tipo', reconstruyendo la estructura de herencia.
     *
     * @return Una lista de objetos 'Movimiento'.
     */
    public List<Movimiento> getTodosLosMovimientos() {
        // Se crea una lista vacía que se llenará con los movimientos.
        List<Movimiento> listaMovimientos = new ArrayList<>();
        // Se define la consulta SELECT para obtener todos los registros, ordenados por ID descendente (los más nuevos primero).
        String selectQuery = "SELECT * FROM " + TABLE_MOVIMIENTOS + " ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null; // Se inicializa el cursor a null.

        // --- MANEJO DE ERRORES (RUBRICA) ---
        try {
            // Se ejecuta la consulta. El cursor apunta a los resultados.
            cursor = db.rawQuery(selectQuery, null);

            // BUCLE 'while': Se itera sobre cada fila del resultado mientras haya filas.
            while (cursor.moveToNext()) {
                // Se obtienen los datos de cada columna para la fila actual.
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                double monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO));

                // IF-ELSE: Se decide qué tipo de objeto crear basado en la columna 'tipo'.
                if ("Ingreso".equals(tipo)) {
                    // INSTANCIACIÓN DE OBJETO 'Ingreso'.
                    Ingreso ingreso = new Ingreso(id, monto, fecha, descripcion);
                    listaMovimientos.add(ingreso); // Se añade a la lista.
                } else if ("Gasto".equals(tipo)) {
                    // Si es Gasto, se obtiene también la categoría.
                    String categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA));
                    // INSTANCIACIÓN DE OBJETO 'Gasto'.
                    Gasto gasto = new Gasto(id, monto, fecha, descripcion, categoria);
                    listaMovimientos.add(gasto); // Se añade a la lista.
                }
            }
        } catch (SQLException e) {
            Log.e("DBHelper", "Error al leer movimientos: " + e.getMessage());
        } finally {
            // Se asegura de cerrar el cursor y la base de datos.
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        // Se retorna la lista (puede estar vacía si no hay registros o si hubo un error).
        return listaMovimientos;
    }

    // TODO: Más adelante agregaremos aquí los métodos para ACTUALIZAR y ELIMINAR movimientos.
    // Por ahora, con agregar y leer tenemos la base para continuar.
}