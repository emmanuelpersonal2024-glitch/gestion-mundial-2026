package com.mundial.modelo;

public class Equipo {
    private int idEquipo;
    private String nombreEquipo;
    private int idGrupo;
    private String nombreGrupo; // Atributo auxiliar para la vista

    public Equipo() {
    }

    // Constructor para insertar (sin ID, con llave foránea)
    public Equipo(String nombreEquipo, int idGrupo) {
        this.nombreEquipo = nombreEquipo;
        this.idGrupo = idGrupo;
    }

    // Constructor completo (usado al leer de la BD)
    public Equipo(int idEquipo, String nombreEquipo, int idGrupo, String nombreGrupo) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.idGrupo = idGrupo;
        this.nombreGrupo = nombreGrupo;
    }

    // Getters y Setters
    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
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

    @Override
    public String toString() {
        return nombreEquipo;
    }
}