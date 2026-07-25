package com.mundial.dao;

import com.mundial.config.ConexionDB;
import com.mundial.modelo.Fase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaseDAO {

    public boolean insertar(Fase fase) {
        String sql = "INSERT INTO fases (nombre_fase) VALUES (?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fase.getNombreFase());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar fase: " + e.getMessage());
            return false;
        }
    }

    public List<Fase> listar() {
        List<Fase> lista = new ArrayList<>();
        String sql = "SELECT * FROM fases ORDER BY id_fase ASC";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Fase(rs.getInt("id_fase"), rs.getString("nombre_fase")));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar fases: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Fase fase) {
        String sql = "UPDATE fases SET nombre_fase = ? WHERE id_fase = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fase.getNombreFase());
            pstmt.setInt(2, fase.getIdFase());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar fase: " + e.getMessage());
            return false;
        }
    }

    public String eliminar(int idFase) {
        String sql = "DELETE FROM fases WHERE id_fase = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFase);
            int rows = pstmt.executeUpdate();
            return rows > 0 ? null : "No se encontró el registro.";
        } catch (SQLException e) {
            System.err.println("Error al eliminar fase: " + e.getMessage());
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                return "No se puede eliminar porque esta fase está relacionada con uno o más partidos.";
            }
            return "Error de base de datos: " + e.getMessage();
        }
    }
}