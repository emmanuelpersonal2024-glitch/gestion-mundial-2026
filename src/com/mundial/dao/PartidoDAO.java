package com.mundial.dao;

import com.mundial.config.ConexionDB;
import com.mundial.modelo.Partido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartidoDAO {

    // CREATE
    public boolean insertar(Partido p) {
        String sql = "INSERT INTO partidos (id_fase, id_estadio, id_equipo_a, id_equipo_b, fecha_partido, hora_partido) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, p.getIdFase());
            pstmt.setInt(2, p.getIdEstadio());
            pstmt.setInt(3, p.getIdEquipoA());
            pstmt.setInt(4, p.getIdEquipoB());
            pstmt.setDate(5, Date.valueOf(p.getFechaPartido())); // Convierte String a Date de SQL
            pstmt.setTime(6, Time.valueOf(p.getHoraPartido()));  // Convierte String a Time de SQL

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Error al insertar partido: " + e.getMessage());
            return false;
        }
    }

    // READ (Con múltiples JOINs para la interfaz)
    public List<Partido> listar() {
        List<Partido> lista = new ArrayList<>();
        String sql = "SELECT p.*, f.nombre_fase, est.nombre_estadio, ea.nombre_equipo as eq_a, eb.nombre_equipo as eq_b " +
                "FROM partidos p " +
                "INNER JOIN fases f ON p.id_fase = f.id_fase " +
                "INNER JOIN estadios est ON p.id_estadio = est.id_estadio " +
                "LEFT JOIN equipos ea ON p.id_equipo_a = ea.id_equipo " +
                "LEFT JOIN equipos eb ON p.id_equipo_b = eb.id_equipo " +
                "ORDER BY p.fecha_partido ASC, p.hora_partido ASC";

        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Partido p = new Partido();
                p.setIdPartido(rs.getInt("id_partido"));
                p.setIdFase(rs.getInt("id_fase"));
                p.setIdEstadio(rs.getInt("id_estadio"));
                p.setIdEquipoA(rs.getInt("id_equipo_a"));
                p.setIdEquipoB(rs.getInt("id_equipo_b"));
                p.setGolesEquipoA(rs.getInt("goles_equipo_a_real"));
                p.setGolesEquipoB(rs.getInt("goles_equipo_b_real"));
                p.setFechaPartido(rs.getString("fecha_partido"));
                p.setHoraPartido(rs.getString("hora_partido"));
                p.setEstadoPartido(rs.getString("estado_partido"));

                // Nombres para mostrar en la JTable
                p.setNombreFase(rs.getString("nombre_fase"));
                p.setNombreEstadio(rs.getString("nombre_estadio"));
                p.setNombreEquipoA(rs.getString("eq_a"));
                p.setNombreEquipoB(rs.getString("eq_b"));

                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar partidos: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE (Actualizar goles y estado al finalizar un partido)
    public boolean actualizarResultado(int idPartido, int golesA, int golesB, String estado) {
        String sql = "UPDATE partidos SET goles_equipo_a_real = ?, goles_equipo_b_real = ?, estado_partido = ? WHERE id_partido = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, golesA);
            pstmt.setInt(2, golesB);
            pstmt.setString(3, estado);
            pstmt.setInt(4, idPartido);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar resultado: " + e.getMessage());
            return false;
        }
    }

    public String eliminar(int idPartido) {
        String sql = "DELETE FROM partidos WHERE id_partido = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPartido);
            int rows = pstmt.executeUpdate();
            return rows > 0 ? null : "No se encontró el registro.";
        } catch (SQLException e) {
            System.err.println("Error al eliminar partido: " + e.getMessage());
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState())) {
                return "No se puede eliminar porque este partido está relacionado con pronósticos u otras tablas.";
            }
            return "Error de base de datos: " + e.getMessage();
        }
    }

    public int obtenerIdPartidoFaseGrupos(int idFase, int idEquipoA, int idEquipoB) {
        String sql = "SELECT id_partido FROM partidos WHERE id_fase = ? AND ((id_equipo_a = ? AND id_equipo_b = ?) OR (id_equipo_a = ? AND id_equipo_b = ?))";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFase);
            pstmt.setInt(2, idEquipoA);
            pstmt.setInt(3, idEquipoB);
            pstmt.setInt(4, idEquipoB);
            pstmt.setInt(5, idEquipoA);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_partido");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar partido duplicado: " + e.getMessage());
        }
        return -1;
    }
}
