package com.fotos.fotosapp.models;

public class Persona {
    private int id;
    private String nombre;
    private String correo;
    private String fotoBase64;
    private String fechaRegistro;

    public Persona() {}

    public Persona(int id, String nombre, String correo, String fotoBase64, String fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.fotoBase64 = fotoBase64;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getFotoBase64() { return fotoBase64; }
    public void setFotoBase64(String fotoBase64) { this.fotoBase64 = fotoBase64; }
    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
