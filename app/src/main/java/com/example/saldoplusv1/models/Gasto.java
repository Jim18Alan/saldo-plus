package com.example.saldoplusv1.models;

/**
 * REPRESENTA: Un movimiento específico de tipo Gasto.
 * RELACIÓN DE HERENCIA: Esta clase también hereda de la clase Movimiento.
 * ESPECIALIZACIÓN: A diferencia de Ingreso, la clase Gasto añade un atributo propio ('categoria'),
 * demostrando cómo una subclase puede especializar y extender la funcionalidad de su superclase.
 */
public class Gasto extends Movimiento {

    /**
     * ATRIBUTO PROPIO: Almacena la categoría específica del gasto (ej: "Comida", "Transporte", "Ocio").
     * TIPO DE DATO: String.
     * ACCESO: 'private' para asegurar el encapsulamiento. Solo se puede acceder a él a través de su getter.
     */
    private String categoria;

    /**
     * CONSTRUCTOR: Se utiliza para crear (instanciar) un nuevo objeto de tipo Gasto.
     *
     * @param id          Parámetro para el identificador único del gasto.
     * @param monto       Parámetro para la cantidad monetaria del gasto.
     * @param fecha       Parámetro para la fecha en que se realizó el gasto.
     * @param descripcion Parámetro para el texto descriptivo del gasto.
     * @param categoria   Parámetro para la categoría a la que pertenece el gasto.
     */
    public Gasto(long id, double monto, String fecha, String descripcion, String categoria) {
        // INVOCACIÓN AL CONSTRUCTOR PADRE: Se llama al constructor de Movimiento para inicializar los atributos comunes.
        super(id, monto, fecha, descripcion);
        // INICIALIZACIÓN DEL ATRIBUTO PROPIO: Se asigna el valor del parámetro al atributo 'categoria' de esta clase.
        this.categoria = categoria;
    }

    /**
     * MÉTODO: Obtiene la categoría del gasto.
     * @return La categoría (String) del gasto.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * MÉTODO SOBRESCRITO: Implementación del método abstracto 'getTipo()' heredado de Movimiento.
     * PROPOSITO: Define que cualquier objeto de esta clase es de tipo "Gasto".
     * @return La cadena de texto fija "Gasto".
     */
    @Override
    public String getTipo() {
        return "Gasto";
    }
}