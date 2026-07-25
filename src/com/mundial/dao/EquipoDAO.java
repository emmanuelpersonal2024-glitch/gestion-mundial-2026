package com.mundial.dao;

import com.mundial.config.ConexionDB;
import com.mundial.modelo.Equipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    // CREATE
    public boolean insertar(Equipo equipo) {
        String sql = "INSERT INTO equipos (nombre_equipo, id_grupo) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipo.getNombreEquipo());
            pstmt.setInt(2, equipo.getIdGrupo());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar equipo: " + e.getMessage());
            return false;
        }
    }

    public int contarEquiposPorGrupo(int idGrupo) {
        String sql = "SELECT COUNT(*) FROM equipos WHERE id_grupo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idGrupo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar equipos por grupo: " + e.getMessage());
        }
        return 0;
    }

    // READ (Con INNER JOIN para traer la letra del grupo)
    public List<Equipo> listar() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT e.id_equipo, e.nombre_equipo, e.id_grupo, g.nombre_grupo " +
                "FROM equipos e " +
                "INNER JOIN grupos g ON e.id_grupo = g.id_grupo " +
                "ORDER BY e.id_equipo ASC";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Equipo equipo = new Equipo(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre_equipo"),
                        rs.getInt("id_grupo"),
                        rs.getString("nombre_grupo")
                );
                lista.add(equipo);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE
    public boolean actualizar(Equipo equipo) {
        String sql = "UPDATE equipos SET nombre_equipo = ?, id_grupo = ? WHERE id_equipo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipo.getNombreEquipo());
            pstmt.setInt(2, equipo.getIdGrupo());
            pstmt.setInt(3, equipo.getIdEquipo());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar equipo: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public String eliminar(int idEquipo) {
        String sql = "DELETE FROM equipos WHERE id_equipo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEquipo);
            int rows = pstmt.executeUpdate();
            return rows > 0 ? null : "No se encontró el registro.";

        } catch (SQLException e) {
            System.err.println("Error al eliminar equipo: " + e.getMessage());
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                return "No se puede eliminar porque este equipo está relacionado con partidos u otras tablas.";
            }
            return "Error de base de datos: " + e.getMessage();
        }
    }
}
