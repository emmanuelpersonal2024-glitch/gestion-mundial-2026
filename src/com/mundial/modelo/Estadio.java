package com.mundial.modelo;

public class Estadio {

    private int idEstadio;
    private String nombreEstadio;
    private String ciudadEstadio;

    public Estadio() {
    }

    public Estadio(String nombreEstadio, String ciudadEstadio) {
        this.nombreEstadio = nombreEstadio;
        this.ciudadEstadio = ciudadEstadio;
    }

    public Estadio(int idEstadio, String nombreEstadio, String ciudadEstadio) {
        this.idEstadio = idEstadio;
        this.nombreEstadio = nombreEstadio;
        this.ciudadEstadio = ciudadEstadio;
    }

    public int getIdEstadio() {
        return idEstadio;
    }

    public void setIdEstadio(int idEstadio) {
        this.idEstadio = idEstadio;
    }

    public String getNombreEstadio() {
        return nombreEstadio;
    }

    public void setNombreEstadio(String nombreEstadio) {
        this.nombreEstadio = nombreEstadio;
    }

    public String getCiudadEstadio() {
        return ciudadEstadio;
    }

    public void setCiudadEstadio(String ciudadEstadio) {
        this.ciudadEstadio = ciudadEstadio;
    }

    @Override
    public String toString() {
        return nombreEstadio;
    }
}
