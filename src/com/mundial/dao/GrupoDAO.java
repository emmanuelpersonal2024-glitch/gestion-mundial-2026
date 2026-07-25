package com.mundial.dao;

import com.mundial.config.ConexionDB;
import com.mundial.modelo.Grupo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    public boolean insertar(Grupo grupo) {
        String sql = "INSERT INTO grupos (nombre_grupo) VALUES (?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, grupo.getNombreGrupo());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar grupo: " + e.getMessage());
            return false;
        }
    }

    public List<Grupo> listar() {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM grupos ORDER BY id_grupo ASC";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Grupo grupo = new Grupo(
                        rs.getInt("id_grupo"),
                        rs.getString("nombre_grupo")
                );
                lista.add(grupo);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar grupos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Grupo grupo) {
        String sql = "UPDATE grupos SET nombre_grupo = ? WHERE id_grupo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, grupo.getNombreGrupo());
            pstmt.setInt(2, grupo.getIdGrupo());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar grupo: " + e.getMessage());
            return false;
        }
    }

    public String eliminar(int idGrupo) {
        String sql = "DELETE FROM grupos WHERE id_grupo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);
            int rows = pstmt.executeUpdate();
            return rows > 0 ? null : "No se encontró el registro.";

        } catch (SQLException e) {
            System.err.println("Error al eliminar grupo: " + e.getMessage());
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                return "No se puede eliminar porque este grupo está relacionado con uno o más equipos.";
            }
            return "Error de base de datos: " + e.getMessage();
        }
    }
}