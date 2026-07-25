package com.mundial.dao;

import com.mundial.config.ConexionDB;
import com.mundial.modelo.Estadio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadioDAO {

    public boolean insertar(Estadio estadio) {
        String sql = "INSERT INTO estadios (nombre_estadio, ciudad_estadio) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estadio.getNombreEstadio());
            pstmt.setString(2, estadio.getCiudadEstadio());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar estadio: " + e.getMessage());
            return false;
        }
    }

    public List<Estadio> listar() {
        List<Estadio> lista = new ArrayList<>();
        String sql = "SELECT * FROM estadios ORDER BY id_estadio ASC";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Estadio estadio = new Estadio(
                        rs.getInt("id_estadio"),
                        rs.getString("nombre_estadio"),
                        rs.getString("ciudad_estadio")
                );
                lista.add(estadio);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar estadios: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Estadio estadio) {
        String sql = "UPDATE estadios SET nombre_estadio = ?, ciudad_estadio = ? WHERE id_estadio = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estadio.getNombreEstadio());
            pstmt.setString(2, estadio.getCiudadEstadio());
            pstmt.setInt(3, estadio.getIdEstadio());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estadio: " + e.getMessage());
            return false;
        }
    }

    public String eliminar(int idEstadio) {
        String sql = "DELETE FROM estadios WHERE id_estadio = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEstadio);
            int rows = pstmt.executeUpdate();
            return rows > 0 ? null : "No se encontró el registro.";

        } catch (SQLException e) {
            System.err.println("Error al eliminar estadio: " + e.getMessage());
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                return "No se puede eliminar porque este estadio está relacionado con uno o más partidos.";
            }
            return "Error de base de datos: " + e.getMessage();
        }
    }
}
