package com.mundial.vista;

import javax.swing.*;
import java.awt.*;

public class VentanaReglas extends JPanel {

    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public VentanaReglas() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(crearHeader(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitulo = new JLabel("Reglamento del Simulador");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Conoce el sistema de puntuación oficial y las reglas para ganar.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(180, 190, 210));

        header.add(lblTitulo, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.CENTER);

        return header;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new GridLayout(3, 1, 0, 20));
        cuerpo.setOpaque(false);

        cuerpo.add(crearTarjetaRegla(
                "3 PUNTOS - ¡Pleno Exacto!",
                "Acertar el marcador exacto del partido.",
                "Por ejemplo: Pronosticaste 2 - 1, y el partido terminó exactamente 2 - 1.",
                new Color(40, 167, 69) // Verde
        ));

        cuerpo.add(crearTarjetaRegla(
                "1 PUNTO - Acertar Tendencia",
                "Acertar el ganador o el empate, pero sin atinar al marcador exacto.",
                "Por ejemplo: Pronosticaste victoria local 1 - 0, y el local ganó 3 - 0.",
                new Color(255, 193, 7) // Amarillo
        ));

        cuerpo.add(crearTarjetaRegla(
                "0 PUNTOS - Pronóstico Fallido",
                "No acertar ni el marcador exacto ni la tendencia del partido.",
                "Por ejemplo: Pronosticaste victoria local, pero el partido terminó en empate o ganó el visitante.",
                new Color(220, 53, 69) // Rojo
        ));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(cuerpo, BorderLayout.NORTH);

        return wrapper;
    }

    private JPanel crearTarjetaRegla(String titulo, String desc, String ejemplo, Color colorBorde) {
        JPanel tarjeta = new JPanel(new BorderLayout(10, 10));
        tarjeta.setBackground(COLOR_PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, colorBorde),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(colorBorde);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDesc.setForeground(Color.WHITE);

        JLabel lblEjemplo = new JLabel("<html><i>" + ejemplo + "</i></html>");
        lblEjemplo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblEjemplo.setForeground(new Color(150, 160, 180));

        JPanel textos = new JPanel(new GridLayout(2, 1, 5, 5));
        textos.setOpaque(false);
        textos.add(lblDesc);
        textos.add(lblEjemplo);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(textos, BorderLayout.CENTER);

        return tarjeta;
    }
}
