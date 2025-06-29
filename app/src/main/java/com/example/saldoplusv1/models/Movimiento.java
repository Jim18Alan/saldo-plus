package com.example.saldoplusv1.models;

import java.util.Date;

/**
 * Clase intermedia entre Transaccion y cualquier tipo más específico (si agregas metas, etc.)
 */
public class Movimiento extends Transaccion {

    public Movimiento(long id, double monto, Date fecha, String descripcion,
                      Categoria categoria, Apartado apartado) {
        super(id, monto, fecha, descripcion, categoria, apartado);
    }

    // Constructor con fecha actual
    public Movimiento(long id, double monto, String descripcion,
                      Categoria categoria, Apartado apartado) {
        super(id, monto, new Date(), descripcion, categoria, apartado);
    }

    // Métodos de utilidad para saber su impacto
    public boolean esIngreso() {
        return getImpacto() == ImpactoFinanciero.INGRESO;
    }

    public boolean esGasto() {
        return getImpacto() == ImpactoFinanciero.GASTO;
    }

    public boolean esNeutro() {
        return getImpacto() == ImpactoFinanciero.NEUTRO;
    }
}
