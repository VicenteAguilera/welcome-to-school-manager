package com.github.welcome_to_school_manager.helpers.models;

public class Alumno {

    private String numeroControl;
    private String nombre;
    private String carrera;
    private String telefono;
    private String status;

    public Alumno(String numeroControl, String nombre, String carrera, String telefono) {
        this.numeroControl = numeroControl;
        this.nombre = nombre;
        this.carrera = carrera;
        this.status = "";
    }
    public Alumno(String numeroControl, String nombre, String carrera, String telefono, String status) {
        this.numeroControl = numeroControl;
        this.nombre = nombre;
        this.carrera = carrera;
        this.status = status;
    }


    public String getNumeroControl() {
        return numeroControl;
    }

    public void setNumeroControl(String numeroControl) {
        this.numeroControl = numeroControl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
