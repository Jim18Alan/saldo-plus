package com.example.saldoplusv1.models;

/**
 * REPRESENTA: Un movimiento específico de tipo Ingreso.
 * RELACIÓN DE HERENCIA: Esta clase 'extiende' (hereda) de la clase Movimiento.
 * Esto significa que un Ingreso "ES UN" Movimiento y automáticamente
 * posee todos sus atributos (id, monto, etc.) y métodos.
 */
public class Ingreso extends Movimiento {

    /**
     * CONSTRUCTOR: Se utiliza para crear (instanciar) un nuevo objeto de tipo Ingreso.
     * USO DE 'super()': La primera línea 'super(...)' es una llamada obligatoria al constructor de la clase padre (Movimiento).
     * Le pasa los valores para que la superclase inicialice sus propios atributos.
     *
     * @param id          Parámetro para el identificador único del ingreso.
     * @param monto       Parámetro para la cantidad monetaria del ingreso.
     * @param fecha       Parámetro para la fecha en que se recibió el ingreso.
     * @param descripcion Parámetro para el texto descriptivo del ingreso (ej: "Salario Quincenal").
     */
    public Ingreso(long id, double monto, String fecha, String descripcion) {
        // INVOCACIÓN AL CONSTRUCTOR PADRE: Se llama al constructor de Movimiento para inicializar los atributos heredados.
        super(id, monto, fecha, descripcion);
    }

    /**
     * MÉTODO SOBRESCRITO: Implementación del método abstracto 'getTipo()' heredado de Movimiento.
     * USO DE '@Override': Esta anotación le indica al compilador que estamos sobrescribiendo un método
     * de la clase padre. Ayuda a prevenir errores.
     * PROPOSITO: Define que cualquier objeto de esta clase es de tipo "Ingreso".
     * @return La cadena de texto fija "Ingreso".
     */
    @Override
    public String getTipo() {
        return "Ingreso";
    }
}