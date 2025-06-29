package com.example.saldoplusv1.models;

/**
 * Representa una categoría asociada a un apartado (como "Comida", "Sueldo", etc.)
 */
public class Categoria {
    private int id;
    private String nombre;
    private String icono;
    private String color;
    private int apartadoId;

    public Categoria(int id, String nombre, String icono, String color, int apartadoId) {
        this.id = id;
        this.nombre = nombre;
        this.icono = icono;
        this.color = color;
        this.apartadoId = apartadoId;
    }

    public Categoria(String nombre, String icono, String color, int apartadoId) {
        this.nombre = nombre;
        this.icono = icono;
        this.color = color;
        this.apartadoId = apartadoId;
        
    }

    // Getters
    public int getId() { return id; }

    public String getNombre() { return nombre; }

    public String getIcono() { return icono; }

    public String getColor() { return color; }

    public int getApartadoId() { return apartadoId; }

    // Setters
    public void setId(int id) { this.id = id; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public void setIcono(String icono) { this.icono = icono; }

    public void setColor(String color) { this.color = color; }

    public void setApartadoId(int apartadoId) { this.apartadoId = apartadoId; }
}