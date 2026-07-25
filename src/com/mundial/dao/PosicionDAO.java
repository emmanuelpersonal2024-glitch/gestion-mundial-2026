package com.mundial.dao;

import com.mundial.config.ConexionDB;
import com.mundial.modelo.FilaPosicion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PosicionDAO {

    // Devuelve las posiciones filtradas por el ID de un grupo específico
    public List<FilaPosicion> obtenerTablaPorGrupo(int idGrupo) {
        List<FilaPosicion> lista = new ArrayList<>();

        String sql = "SELECT *, (GF - GC) AS GD FROM ( " +
                "    SELECT " +
                "        e.nombre_equipo, " +
                "        g.nombre_grupo, " +
                "        COUNT(p.id_partido) AS PJ, " +
                "        COALESCE(SUM(CASE WHEN (p.id_equipo_a = e.id_equipo AND p.goles_equipo_a_real > p.goles_equipo_b_real) OR (p.id_equipo_b = e.id_equipo AND p.goles_equipo_b_real > p.goles_equipo_a_real) THEN 1 ELSE 0 END), 0) AS PG, " +
                "        COALESCE(SUM(CASE WHEN p.id_partido IS NOT NULL AND p.goles_equipo_a_real = p.goles_equipo_b_real THEN 1 ELSE 0 END), 0) AS PE, " +
                "        COALESCE(SUM(CASE WHEN (p.id_equipo_a = e.id_equipo AND p.goles_equipo_a_real < p.goles_equipo_b_real) OR (p.id_equipo_b = e.id_equipo AND p.goles_equipo_b_real < p.goles_equipo_a_real) THEN 1 ELSE 0 END), 0) AS PP, " +
                "        COALESCE(SUM(CASE WHEN p.id_equipo_a = e.id_equipo THEN p.goles_equipo_a_real WHEN p.id_equipo_b = e.id_equipo THEN p.goles_equipo_b_real ELSE 0 END), 0) AS GF, " +
                "        COALESCE(SUM(CASE WHEN p.id_equipo_a = e.id_equipo THEN p.goles_equipo_b_real WHEN p.id_equipo_b = e.id_equipo THEN p.goles_equipo_a_real ELSE 0 END), 0) AS GC, " +
                "        COALESCE(SUM(CASE WHEN (p.id_equipo_a = e.id_equipo AND p.goles_equipo_a_real > p.goles_equipo_b_real) OR (p.id_equipo_b = e.id_equipo AND p.goles_equipo_b_real > p.goles_equipo_a_real) THEN 3 " +
                "                 WHEN p.id_partido IS NOT NULL AND p.goles_equipo_a_real = p.goles_equipo_b_real THEN 1 ELSE 0 END), 0) AS PTS " +
                "    FROM equipos e " +
                "    INNER JOIN grupos g ON e.id_grupo = g.id_grupo " +
                "    LEFT JOIN partidos p ON (e.id_equipo = p.id_equipo_a OR e.id_equipo = p.id_equipo_b) AND p.estado_partido = 'FINALIZADO' " +
                "    WHERE g.id_grupo = ? " +
                "    GROUP BY e.id_equipo, e.nombre_equipo, g.nombre_grupo " +
                ") AS tabla_calculada " +
                "ORDER BY PTS DESC, GD DESC, GF DESC";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    FilaPosicion fila = new FilaPosicion();
                    fila.setNombreEquipo(rs.getString("nombre_equipo"));
                    fila.setNombreGrupo(rs.getString("nombre_grupo"));

                    fila.setPj(rs.getInt("PJ")); // <-- LÍNEA CORREGIDA
                    fila.setPg(rs.getInt("PG"));
                    fila.setPe(rs.getInt("PE"));
                    fila.setPp(rs.getInt("PP"));
                    fila.setGf(rs.getInt("GF"));
                    fila.setGc(rs.getInt("GC"));
                    fila.setGd(rs.getInt("GF") - rs.getInt("GC"));
                    fila.setPts(rs.getInt("PTS"));

                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular tabla de posiciones: " + e.getMessage());
        }
        return lista;
    }
}
