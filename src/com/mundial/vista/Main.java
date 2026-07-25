package com.mundial.vista;

import javax.swing.SwingUtilities;
import com.mundial.vista.componentes.GestorEstilos;

public class Main {
    public static void main(String[] args) {
        GestorEstilos.aplicarTemaPremium();
        SwingUtilities.invokeLater(() -> {
            VentanaLogin login = new VentanaLogin();
            login.setVisible(true);
        });
    }
}