package com.example.saldoplusv1.models;

/**
 * REPRESENTA: La idea abstracta de un movimiento financiero.
 * PROPOSITO: Servir como clase base (superclase) para clases más específicas como Ingreso y Gasto.
 * ESTRUCTURA: Contiene todos los atributos y métodos comunes a cualquier tipo de transacción.
 * USO DE "ABSTRACT": Se declara como 'abstract' porque nunca crearemos un objeto de tipo "Movimiento" directamente.
 * Solo crearemos objetos de sus clases hijas (Ingreso, Gasto). Esto es un pilar de la POO.
 */
public abstract class Movimiento {

    /**
     * ATRIBUTO: Identificador único del movimiento en la base de datos.
     * TIPO DE DATO: long (entero largo) para soportar un gran número de registros.
     * ACCESO: 'protected' para que las clases hijas (Ingreso, Gasto) puedan acceder a él si fuera necesario.
     */
    protected long id;

    /**
     * ATRIBUTO: Cantidad monetaria del movimiento. Siempre se debe manejar como un valor positivo.
     * TIPO DE DATO: double (decimal de doble precisión) para manejar centavos.
     */
    protected double monto;

    /**
     * ATRIBUTO: Fecha en la que se registró el movimiento.
     * TIPO DE DATO: String (cadena de texto) en formato "YYYY-MM-DD" para facilitar su guardado y ordenamiento.
     */
    protected String fecha;

    /**
     * ATRIBUTO: Texto descriptivo del movimiento proporcionado por el usuario (ej: "Café en la cafetería").
     * TIPO DE DATO: String.
     */
    protected String descripcion;

    /**
     * CONSTRUCTOR: Se utiliza para inicializar los objetos de las clases que hereden de Movimiento.
     * PROPOSITO: Asignar los valores iniciales a los atributos de la instancia. Es invocado con 'super()' desde las clases hijas.
     *
     * @param id          Parámetro que recibe el identificador único del movimiento.
     * @param monto       Parámetro que recibe la cantidad monetaria de la transacción.
     * @param fecha       Parámetro que recibe la fecha de la transacción.
     * @param descripcion Parámetro que recibe el texto descriptivo para el movimiento.
     */
    public Movimiento(long id, double monto, String fecha, String descripcion) {
        // INSTANCIACIÓN DE ATRIBUTOS: Se asignan los valores recibidos por los parámetros a los atributos del objeto.
        this.id = id;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    // --- MÉTODOS GETTERS ---
    // PROPOSITO: Proveer acceso de solo lectura a los atributos del objeto desde otras clases,
    //            respetando el principio de encapsulamiento.

    /**
     * MÉTODO: Obtiene el identificador único del movimiento.
     * @return El ID (long) del movimiento.
     */
    public long getId() {
        return id;
    }

    /**
     * MÉTODO: Obtiene el monto del movimiento.
     * @return El monto (double) del movimiento.
     */
    public double getMonto() {
        return monto;
    }

    /**
     * MÉTODO: Obtiene la fecha del movimiento.
     * @return La fecha (String) del movimiento.
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * MÉTODO: Obtiene la descripción del movimiento.
     * @return La descripción (String) del movimiento.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * MÉTODO ABSTRACTO: Obliga a las clases hijas a definir su tipo específico.
     * PROPOSITO: Es un ejemplo de polimorfismo. Cada subclase debe proporcionar su propia implementación (override).
     * Esto nos permitirá saber si un objeto 'Movimiento' es en realidad un 'Ingreso' o un 'Gasto'.
     * @return Una cadena de texto (String) que representa el tipo de movimiento (ej: "Ingreso" o "Gasto").
     */
    public abstract String getTipo();
}