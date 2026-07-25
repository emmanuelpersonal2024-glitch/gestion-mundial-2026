package com.mundial.dao;

import com.mundial.config.ConexionDB;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PuntoDAO {

    public int obtenerPuntosUsuario(int idUsuario) {
        String sql = "SELECT puntos_totales FROM puntos WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("puntos_totales");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener puntos: " + e.getMessage());
        }
        return 0;
    }

    public void calcularYActualizarPuntosGlobales() {
        Map<Integer, Integer> puntosPorUsuario = new HashMap<>();

        String sqlSelect = "SELECT p.id_usuario, p.goles_equipo_a_pronostico, p.goles_equipo_b_pronostico, " +
                "part.goles_equipo_a_real, part.goles_equipo_b_real " +
                "FROM pronosticos p " +
                "INNER JOIN partidos part ON p.id_partido = part.id_partido " +
                "WHERE part.estado_partido = 'FINALIZADO'";

        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlSelect)) {

            while (rs.next()) {
                int idUsuario = rs.getInt("id_usuario");
                int pronosticoA = rs.getInt("goles_equipo_a_pronostico");
                int pronosticoB = rs.getInt("goles_equipo_b_pronostico");
                int realA = rs.getInt("goles_equipo_a_real");
                int realB = rs.getInt("goles_equipo_b_real");

                int puntosGanados = 0;

                if (pronosticoA == realA && pronosticoB == realB) {
                    puntosGanados = 3;
                } else if ((pronosticoA > pronosticoB && realA > realB) ||
                        (pronosticoA < pronosticoB && realA < realB) ||
                        (pronosticoA == pronosticoB && realA == realB)) {
                    puntosGanados = 1;
                }

                puntosPorUsuario.put(idUsuario, puntosPorUsuario.getOrDefault(idUsuario, 0) + puntosGanados);
            }

            String sqlInsertUpdate = "INSERT INTO puntos (id_usuario, puntos_totales) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE puntos_totales = ?";

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlInsertUpdate)) {
                for (Map.Entry<Integer, Integer> entry : puntosPorUsuario.entrySet()) {
                    pstmtUpdate.setInt(1, entry.getKey());
                    pstmtUpdate.setInt(2, entry.getValue());
                    pstmtUpdate.setInt(3, entry.getValue());
                    pstmtUpdate.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular puntos globales: " + e.getMessage());
        }
    }

    public java.util.List<com.mundial.modelo.FilaRanking> obtenerRankingUsuarios() {
        java.util.List<com.mundial.modelo.FilaRanking> ranking = new java.util.ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre_usuario, COALESCE(p.puntos_totales, 0) AS puntos_totales " +
                     "FROM usuarios u " +
                     "LEFT JOIN puntos p ON u.id_usuario = p.id_usuario " +
                     "WHERE u.rol_usuario != 'ADMIN' " +
                     "ORDER BY puntos_totales DESC, u.nombre_usuario ASC";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            int posicion = 1;
            int puntosAnteriores = -1;
            int rankActual = 1;

            while (rs.next()) {
                int puntos = rs.getInt("puntos_totales");
                if (puntos != puntosAnteriores && puntosAnteriores != -1) {
                    rankActual = posicion;
                }
                
                com.mundial.modelo.FilaRanking fila = new com.mundial.modelo.FilaRanking();
                fila.setPosicion(rankActual);
                fila.setIdUsuario(rs.getInt("id_usuario"));
                fila.setNombreUsuario(rs.getString("nombre_usuario"));
                fila.setPuntosTotales(puntos);
                
                ranking.add(fila);
                
                puntosAnteriores = puntos;
                posicion++;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ranking: " + e.getMessage());
        }
        return ranking;
    }
}
