package com.mundial.dao;

import com.mundial.config.ConexionDB;
import com.mundial.modelo.Pronostico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PronosticoDAO {

    // Guarda el pronóstico, y si ya existe para ese usuario y partido, lo actualiza.
    public boolean guardarOActualizar(Pronostico p) {
        String sql = "INSERT INTO pronosticos (id_usuario, id_partido, goles_equipo_a_pronostico, goles_equipo_b_pronostico) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE goles_equipo_a_pronostico = ?, goles_equipo_b_pronostico = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Valores para el INSERT
            pstmt.setInt(1, p.getIdUsuario());
            pstmt.setInt(2, p.getIdPartido());
            pstmt.setInt(3, p.getGolesEquipoA());
            pstmt.setInt(4, p.getGolesEquipoB());

            // Valores para el UPDATE (si hay duplicado)
            pstmt.setInt(5, p.getGolesEquipoA());
            pstmt.setInt(6, p.getGolesEquipoB());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar pronóstico: " + e.getMessage());
            return false;
        }
    }

    // Elimina un pronóstico por su ID
    public String eliminar(int idPronostico) {
        String sql = "DELETE FROM pronosticos WHERE id_pronostico = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPronostico);
            int rows = pstmt.executeUpdate();
            return rows > 0 ? null : "No se encontró el registro.";

        } catch (SQLException e) {
            System.err.println("Error al eliminar pronóstico: " + e.getMessage());
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                return "No se puede eliminar porque este pronóstico está relacionado con otras tablas del sistema.";
            }
            return "Error de base de datos: " + e.getMessage();
        }
    }

    // Lista solo los pronósticos del usuario que inició sesión
    public List<Pronostico> listarPorUsuario(int idUsuario) {
        List<Pronostico> lista = new ArrayList<>();
        String sql = "SELECT p.id_pronostico, p.id_usuario, p.id_partido, " +
                "p.goles_equipo_a_pronostico, p.goles_equipo_b_pronostico, " +
                "ea.nombre_equipo AS eq_a, eb.nombre_equipo AS eq_b, part.fecha_partido, part.hora_partido, " +
                "part.estado_partido, part.goles_equipo_a_real, part.goles_equipo_b_real " +
                "FROM pronosticos p " +
                "INNER JOIN partidos part ON p.id_partido = part.id_partido " +
                "INNER JOIN equipos ea ON part.id_equipo_a = ea.id_equipo " +
                "INNER JOIN equipos eb ON part.id_equipo_b = eb.id_equipo " +
                "WHERE p.id_usuario = ? " +
                "ORDER BY part.fecha_partido ASC, part.hora_partido ASC";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Pronostico pro = new Pronostico();
                    pro.setIdPronostico(rs.getInt("id_pronostico"));
                    pro.setIdUsuario(rs.getInt("id_usuario"));
                    pro.setIdPartido(rs.getInt("id_partido"));
                    pro.setGolesEquipoA(rs.getInt("goles_equipo_a_pronostico"));
                    pro.setGolesEquipoB(rs.getInt("goles_equipo_b_pronostico"));
                    pro.setNombreEquipoA(rs.getString("eq_a"));
                    pro.setNombreEquipoB(rs.getString("eq_b"));
                    pro.setFechaPartido(rs.getString("fecha_partido"));
                    pro.setHoraPartido(rs.getString("hora_partido"));
                    pro.setEstadoPartido(rs.getString("estado_partido"));
                    pro.setGolesRealesEquipoA(rs.getInt("goles_equipo_a_real"));
                    pro.setGolesRealesEquipoB(rs.getInt("goles_equipo_b_real"));
                    lista.add(pro);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pronósticos: " + e.getMessage());
        }
        return lista;
    }
}
