package com.mundial.modelo;

public class Partido {
    private int idPartido;
    private int idFase;
    private int idEstadio;
    private int idEquipoA;
    private int idEquipoB;
    private int golesEquipoA; // Usamos 0 si es nulo
    private int golesEquipoB;
    private String fechaPartido; // Formato YYYY-MM-DD
    private String horaPartido;  // Formato HH:MM:SS
    private String estadoPartido;

    // Nombres para mostrar en la tabla visual (JOINs)
    private String nombreFase, nombreEstadio, nombreEquipoA, nombreEquipoB;

    public Partido() {
    }

    // Constructor para insertar un partido nuevo (sin goles ni estado, quedan por defecto)
    public Partido(int idFase, int idEstadio, int idEquipoA, int idEquipoB, String fechaPartido, String horaPartido) {
        this.idFase = idFase;
        this.idEstadio = idEstadio;
        this.idEquipoA = idEquipoA;
        this.idEquipoB = idEquipoB;
        this.fechaPartido = fechaPartido;
        this.horaPartido = horaPartido;
    }

    // Getters y Setters
    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public int getIdFase() {
        return idFase;
    }

    public void setIdFase(int idFase) {
        this.idFase = idFase;
    }

    public int getIdEstadio() {
        return idEstadio;
    }

    public void setIdEstadio(int idEstadio) {
        this.idEstadio = idEstadio;
    }

    public int getIdEquipoA() {
        return idEquipoA;
    }

    public void setIdEquipoA(int idEquipoA) {
        this.idEquipoA = idEquipoA;
    }

    public int getIdEquipoB() {
        return idEquipoB;
    }

    public void setIdEquipoB(int idEquipoB) {
        this.idEquipoB = idEquipoB;
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

    // Getters y Setters para los nombres descriptivos
    public String getNombreFase() {
        return nombreFase;
    }

    public void setNombreFase(String nombreFase) {
        this.nombreFase = nombreFase;
    }

    public String getNombreEstadio() {
        return nombreEstadio;
    }

    public void setNombreEstadio(String nombreEstadio) {
        this.nombreEstadio = nombreEstadio;
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
}