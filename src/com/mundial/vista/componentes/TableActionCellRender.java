package com.mundial.vista.componentes;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TableActionCellRender extends JPanel implements TableCellRenderer {

    private JButton btnEditar;
    private JButton btnEliminar;

    public TableActionCellRender() {
        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));

        btnEditar = new JButton();
        btnEditar.setIcon(loadIcon("/com/mundial/recursos/iconos/editar.png"));
        btnEditar.setBackground(new Color(212, 175, 55)); // Dorado
        btnEditar.setFocusPainted(false);
        btnEditar.setBorderPainted(false);
        btnEditar.setPreferredSize(new Dimension(30, 24));
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.setToolTipText("Editar");

        btnEliminar = new JButton();
        btnEliminar.setIcon(loadIcon("/com/mundial/recursos/iconos/eliminar.png"));
        btnEliminar.setBackground(new Color(220, 53, 69)); // Rojo
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setPreferredSize(new Dimension(30, 24));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setToolTipText("Eliminar");

        add(btnEditar);
        add(btnEliminar);
    }
    
    private ImageIcon loadIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        System.err.println("No se encontro el icono: " + path);
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        return this;
    }
}
