package com.example.saldoplusv1.models;

import java.util.Date;

/**
 * Clase base para representar una transacción financiera genérica.
 */
public abstract class Transaccion {
    protected long id;
    protected double monto;
    protected Date fecha;
    protected String descripcion;
    protected Categoria categoria; // Categoría dinámica
    protected Apartado apartado;   // Apartado dinámico

    public Transaccion(long id, double monto, Date fecha, String descripcion,
                       Categoria categoria, Apartado apartado) {
        this.id = id;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.apartado = apartado;
    }

    // Getters
    public long getId() { return id; }
    public double getMonto() { return monto; }
    public Date getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public Categoria getCategoria() { return categoria; }
    public Apartado getApartado() { return apartado; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setMonto(double monto) { this.monto = monto; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public void setApartado(Apartado apartado) { this.apartado = apartado; }

    /**
     * Devuelve el tipo de impacto financiero de esta transacción.
     */
    public ImpactoFinanciero getImpacto() {
        return apartado.getImpacto(); // Asume que el impacto está en el apartado
    }

    public Integer getIdApartado() {
        return apartado.getId();
    }
}
