package com.mundial.modelo;

public class FilaRanking {
    private int posicion;
    private int idUsuario;
    private String nombreUsuario;
    private int puntosTotales;

    public FilaRanking() {
    }

    public FilaRanking(int posicion, int idUsuario, String nombreUsuario, int puntosTotales) {
        this.posicion = posicion;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.puntosTotales = puntosTotales;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(int puntosTotales) {
        this.puntosTotales = puntosTotales;
    }
}
