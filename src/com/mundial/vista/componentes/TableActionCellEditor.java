package com.mundial.vista.componentes;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TableActionCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JPanel panel;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTable table;
    private int currentRow;
    
    // Interfaz para delegar la acción
    public interface TableActionListener {
        void onEdit(int row);
        void onDelete(int row);
    }
    
    private TableActionListener event;

    public TableActionCellEditor(TableActionListener event) {
        this.event = event;
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));

        btnEditar = new JButton();
        btnEditar.setIcon(loadIcon("/com/mundial/recursos/iconos/editar.png"));
        btnEditar.setBackground(new Color(212, 175, 55));
        btnEditar.setFocusPainted(false);
        btnEditar.setBorderPainted(false);
        btnEditar.setPreferredSize(new Dimension(30, 24));
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.setToolTipText("Editar");

        btnEliminar = new JButton();
        btnEliminar.setIcon(loadIcon("/com/mundial/recursos/iconos/eliminar.png"));
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setPreferredSize(new Dimension(30, 24));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setToolTipText("Eliminar");

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                if (TableActionCellEditor.this.event != null) {
                    TableActionCellEditor.this.event.onEdit(currentRow);
                }
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                if (TableActionCellEditor.this.event != null) {
                    TableActionCellEditor.this.event.onDelete(currentRow);
                }
            }
        });

        panel.add(btnEditar);
        panel.add(btnEliminar);
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
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.currentRow = row;
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
