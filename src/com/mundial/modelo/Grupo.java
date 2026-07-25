package com.mundial.modelo;

public class Grupo {

    private int idGrupo;
    private String nombreGrupo;

    public Grupo() {
    }

    public Grupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public Grupo(int idGrupo, String nombreGrupo) {
        this.idGrupo = idGrupo;
        this.nombreGrupo = nombreGrupo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    // Vital para cuando usemos JComboBox más adelante
    @Override
    public String toString() {
        return nombreGrupo;
    }
}
