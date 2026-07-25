package com.mundial.vista.componentes;

import com.mundial.modelo.Fase;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

public class ModalFase extends JDialog {

    private JTextField txtNombre;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private Fase fase;
    private boolean confirmado = false;
    
    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public ModalFase(Window owner, String titulo, Fase faseAEditar) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        this.fase = faseAEditar;
        
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setUndecorated(true);
        
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
        JPanel panelForm = new JPanel(new GridLayout(2, 1, 0, 5));
        panelForm.setOpaque(false);
        
        JLabel lblNombre = new JLabel("Nombre de la Fase (Ej. Octavos, Cuartos)");
        lblNombre.setForeground(new Color(180, 190, 210));
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre = crearTextField();
        
        if (faseAEditar != null) {
            txtNombre.setText(faseAEditar.getNombreFase());
        }
        
        panelForm.add(lblNombre);
        panelForm.add(txtNombre);
        
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
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la fase es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (fase == null) {
            fase = new Fase(0, txtNombre.getText().trim());
        } else {
            fase.setNombreFase(txtNombre.getText().trim());
        }
        
        confirmado = true;
        dispose();
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public Fase getFase() {
        return fase;
    }
    
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
