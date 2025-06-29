package com.example.saldoplusv1.models;


/**
 * Representa una sección de transacción: Ingreso, Gasto, Ahorro, etc.
 * Ahora incluye su impacto financiero.
 */
public class Apartado {
    private int id;
    private String nombre;
    private String icono;
    private String color;
    private ImpactoFinanciero impacto;

    // Constructor completo (para lectura desde BD)
    public Apartado(int id, String nombre, String icono, String color, ImpactoFinanciero impacto) {
        this.id = id;
        this.nombre = nombre;
        this.icono = icono;
        this.color = color;
        this.impacto = impacto;
    }

    // Constructor para creación (aún no tiene ID)
    public Apartado(String nombre, String icono, String color, ImpactoFinanciero impacto) {
        this.nombre = nombre;
        this.icono = icono;
        this.color = color;
        this.impacto = impacto;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getIcono() { return icono; }
    public String getColor() { return color; }
    public ImpactoFinanciero getImpacto() { return impacto; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setIcono(String icono) { this.icono = icono; }
    public void setColor(String color) { this.color = color; }
    public void setImpacto(ImpactoFinanciero impacto) { this.impacto = impacto; }
}
