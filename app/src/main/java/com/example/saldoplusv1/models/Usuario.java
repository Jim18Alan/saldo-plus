package com.example.saldoplusv1.models;

public class Usuario {
    private long id;
    private String nombre;

    public Usuario(long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }

    public void setId(long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
