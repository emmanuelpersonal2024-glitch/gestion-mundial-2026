package com.mundial.dao;

import com.mundial.config.ConexionDB;
import com.mundial.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Método específico para el Login
    public Usuario autenticar(String username, String password) {
        Usuario usuarioValidado = null;
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND password_usuario = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Si hay coincidencia, armamos el objeto Usuario con los datos de la BD
                    usuarioValidado = new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre_usuario"),
                            rs.getString("password_usuario"),
                            rs.getString("rol_usuario")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de autenticación: " + e.getMessage());
        }

        return usuarioValidado; // Retorna null si las credenciales son incorrectas
    }

    // --- MÉTODOS CRUD PARA EL PANEL DE ADMINISTRADOR ---

    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre_usuario, password_usuario, rol_usuario) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getNombreUsuario());
            pstmt.setString(2, u.getPasswordUsuario());
            pstmt.setString(3, u.getRolUsuario());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id_usuario ASC";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("password_usuario"),
                        rs.getString("rol_usuario")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre_usuario = ?, password_usuario = ?, rol_usuario = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getNombreUsuario());
            pstmt.setString(2, u.getPasswordUsuario());
            pstmt.setString(3, u.getRolUsuario());
            pstmt.setInt(4, u.getIdUsuario());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public String eliminar(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            int rows = pstmt.executeUpdate();
            return rows > 0 ? null : "No se encontró el registro.";

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                return "No se puede eliminar porque este usuario está relacionado con pronósticos o puntos en el sistema.";
            }
            return "Error de base de datos: " + e.getMessage();
        }
    }
}
