package com.mundial.modelo;

public class Pronostico {
    private int idPronostico;
    private int idUsuario;
    private int idPartido;
    private int golesEquipoA;
    private int golesEquipoB;

    // Atributos auxiliares para mostrar en la interfaz (JOINs)
    private String nombreEquipoA;
    private String nombreEquipoB;
    private String fechaPartido;
    private String horaPartido;
    private String estadoPartido;
    private int golesRealesEquipoA;
    private int golesRealesEquipoB;

    public Pronostico() {
    }

    public Pronostico(int idUsuario, int idPartido, int golesEquipoA, int golesEquipoB) {
        this.idUsuario = idUsuario;
        this.idPartido = idPartido;
        this.golesEquipoA = golesEquipoA;
        this.golesEquipoB = golesEquipoB;
    }

    // Getters y Setters
    public int getIdPronostico() {
        return idPronostico;
    }

    public void setIdPronostico(int idPronostico) {
        this.idPronostico = idPronostico;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public int getGolesEquipoA() {
        return golesEquipoA;
    }

    public void setGolesEquipoA(int golesEquipoA) {
        this.golesEquipoA = golesEquipoA;
    }

    public int getGolesEquipoB() {
        return golesEquipoB;
    }

    public void setGolesEquipoB(int golesEquipoB) {
        this.golesEquipoB = golesEquipoB;
    }

    public String getNombreEquipoA() {
        return nombreEquipoA;
    }

    public void setNombreEquipoA(String nombreEquipoA) {
        this.nombreEquipoA = nombreEquipoA;
    }

    public String getNombreEquipoB() {
        return nombreEquipoB;
    }

    public void setNombreEquipoB(String nombreEquipoB) {
        this.nombreEquipoB = nombreEquipoB;
    }

    public String getFechaPartido() {
        return fechaPartido;
    }

    public void setFechaPartido(String fechaPartido) {
        this.fechaPartido = fechaPartido;
    }

    public String getHoraPartido() {
        return horaPartido;
    }

    public void setHoraPartido(String horaPartido) {
        this.horaPartido = horaPartido;
    }

    public String getEstadoPartido() {
        return estadoPartido;
    }

    public void setEstadoPartido(String estadoPartido) {
        this.estadoPartido = estadoPartido;
    }

    public int getGolesRealesEquipoA() {
        return golesRealesEquipoA;
    }

    public void setGolesRealesEquipoA(int golesRealesEquipoA) {
        this.golesRealesEquipoA = golesRealesEquipoA;
    }

    public int getGolesRealesEquipoB() {
        return golesRealesEquipoB;
    }

    public void setGolesRealesEquipoB(int golesRealesEquipoB) {
        this.golesRealesEquipoB = golesRealesEquipoB;
    }
}