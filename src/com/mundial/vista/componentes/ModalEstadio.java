package com.mundial.vista.componentes;

import com.mundial.modelo.Estadio;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModalEstadio extends JDialog {

    private JTextField txtNombre;
    private JTextField txtCiudad;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private Estadio estadio; // null si es agregar
    private boolean confirmado = false;
    
    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public ModalEstadio(Window owner, String titulo, Estadio estadioAEditar) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        this.estadio = estadioAEditar;
        
        setSize(400, 350);
        setLocationRelativeTo(owner);
        setUndecorated(true); // Custom look without OS borders
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_FONDO);
        contentPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // Header
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        panelHeader.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
        contentPane.add(panelHeader, BorderLayout.NORTH);
        
        // Body (Formulario)
        JPanel panelForm = new JPanel(new GridLayout(4, 1, 0, 5));
        panelForm.setOpaque(false);
        
        JLabel lblNombre = new JLabel("Nombre del Estadio");
        lblNombre.setForeground(new Color(180, 190, 210));
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre = crearTextField();
        
        JLabel lblCiudad = new JLabel("Ciudad");
        lblCiudad.setForeground(new Color(180, 190, 210));
        lblCiudad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCiudad = crearTextField();
        
        if (estadioAEditar != null) {
            txtNombre.setText(estadioAEditar.getNombreEstadio());
            txtCiudad.setText(estadioAEditar.getCiudadEstadio());
        }
        
        panelForm.add(lblNombre);
        panelForm.add(txtNombre);
        panelForm.add(lblCiudad);
        panelForm.add(txtCiudad);
        
        contentPane.add(panelForm, BorderLayout.CENTER);
        
        // Footer (Botones)
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelFooter.setOpaque(false);
        panelFooter.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(COLOR_PANEL);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(COLOR_ACENTO);
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> validarYGuardar());
        
        panelFooter.add(btnCancelar);
        panelFooter.add(btnGuardar);
        
        contentPane.add(panelFooter, BorderLayout.SOUTH);
        
        setContentPane(contentPane);
    }
    
    private JTextField crearTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(COLOR_PANEL);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_ACENTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_PANEL.brighter(), 1, 8),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }
    
    private void validarYGuardar() {
        if (txtNombre.getText().trim().isEmpty() || txtCiudad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (estadio == null) {
            estadio = new Estadio(0, txtNombre.getText().trim(), txtCiudad.getText().trim());
        } else {
            estadio.setNombreEstadio(txtNombre.getText().trim());
            estadio.setCiudadEstadio(txtCiudad.getText().trim());
        }
        
        confirmado = true;
        dispose();
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public Estadio getEstadio() {
        return estadio;
    }
    
    // Custom Border
    static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;
        RoundedBorder(Color c, int t, int r) { color = c; thickness = t; radius = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
    }
}
