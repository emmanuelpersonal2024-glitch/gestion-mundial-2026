package com.mundial.modelo;

public class Fase {
    private int idFase;
    private String nombreFase;

    public Fase() {}

    public Fase(String nombreFase) {
        this.nombreFase = nombreFase;
    }

    public Fase(int idFase, String nombreFase) {
        this.idFase = idFase;
        this.nombreFase = nombreFase;
    }

    public int getIdFase() { return idFase; }
    public void setIdFase(int idFase) { this.idFase = idFase; }

    public String getNombreFase() { return nombreFase; }
    public void setNombreFase(String nombreFase) { this.nombreFase = nombreFase; }

    @Override
    public String toString() {
        return nombreFase;
    }
}