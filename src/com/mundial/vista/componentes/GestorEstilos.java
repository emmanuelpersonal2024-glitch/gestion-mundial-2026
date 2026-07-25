package com.mundial.vista.componentes;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public class GestorEstilos {
    
    public static void aplicarTemaPremium() {
        // Colores más suaves y contrastantes para los diálogos
        Color colorFondoDialogo = new Color(44, 57, 80); 
        Color colorTexto = new Color(240, 245, 250);
        Color colorBoton = new Color(212, 175, 55); // Dorado del proyecto
        
        UIManager.put("OptionPane.background", colorFondoDialogo);
        UIManager.put("Panel.background", colorFondoDialogo);
        UIManager.put("OptionPane.messageForeground", colorTexto);
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 15));
        
        UIManager.put("Button.background", colorBoton);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        
        // Colores para componentes deshabilitados (ComboBox, TextField, etc.)
        Color colorDeshabilitado = new Color(20, 26, 46); // El mismo COLOR_PANEL
        UIManager.put("ComboBox.disabledBackground", colorDeshabilitado);
        UIManager.put("ComboBox.disabledForeground", Color.GRAY);
        UIManager.put("TextField.inactiveBackground", colorDeshabilitado);
        UIManager.put("TextField.inactiveForeground", Color.GRAY);
        UIManager.put("TextField.disabledBackground", colorDeshabilitado);
        UIManager.put("TextField.disabledForeground", Color.GRAY);
        
        // Si hay ScrollPanes por defecto
        UIManager.put("ScrollPane.background", colorFondoDialogo);
        UIManager.put("Viewport.background", colorFondoDialogo);
    }
}
