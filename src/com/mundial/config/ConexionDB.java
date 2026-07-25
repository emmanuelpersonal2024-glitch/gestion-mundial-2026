package com.mundial.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/mundial_db";
    private static final String USER = "root";
    private static final String PASS = ""; // contraseña

    // Ahora retorna una conexión NUEVA e independiente cada vez
    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la BD: " + e.getMessage());
            return null;
        }
    }
}