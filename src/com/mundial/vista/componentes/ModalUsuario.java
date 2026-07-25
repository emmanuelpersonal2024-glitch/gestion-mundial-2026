package com.mundial.vista.componentes;

import com.mundial.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class ModalUsuario extends JDialog {

    private JTextField txtNombre;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRol;

    private JButton btnGuardar;
    private JButton btnCancelar;

    private Usuario usuario;
    private boolean confirmado = false;

    private static final Color COLOR_FONDO = new Color(10, 14, 26);
    private static final Color COLOR_PANEL = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public ModalUsuario(Window owner, String titulo, Usuario usuarioAEditar) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        this.usuario = usuarioAEditar;

        setSize(400, 350);
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
        JPanel panelForm = new JPanel(new GridLayout(6, 1, 0, 5));
        panelForm.setOpaque(false);

        txtNombre = crearTextField();
        txtPassword = crearPasswordField();
        cbRol = crearComboBoxPremium();
        cbRol.addItem("USER");
        cbRol.addItem("ADMIN");

        if (usuario != null) {
            txtNombre.setText(usuario.getNombreUsuario());
            txtPassword.setText(""); // No mostrar la contraseña al editar
            cbRol.setSelectedItem(usuario.getRolUsuario());
        }

        agregarCampo(panelForm, "Nombre de Usuario", txtNombre);
        agregarCampo(panelForm, "Contraseña" + (usuario != null ? " (Dejar en blanco para mantener)" : ""), txtPassword);
        agregarCampo(panelForm, "Rol del Sistema", cbRol);

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

    private void agregarCampo(JPanel panel, String labelText, Component comp) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(new Color(180, 190, 210));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lbl);
        panel.add(comp);
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

    private JPasswordField crearPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setBackground(COLOR_PANEL);
        pf.setForeground(Color.WHITE);
        pf.setCaretColor(COLOR_ACENTO);
        pf.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_PANEL.brighter(), 1, 8),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return pf;
    }

    private JComboBox<String> crearComboBoxPremium() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setBackground(COLOR_PANEL);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setFocusable(false);
        cb.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_PANEL.brighter(), 1, 8),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));

        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("\u25BC");
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                btn.setForeground(COLOR_ACENTO);
                btn.setBackground(COLOR_PANEL);
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(COLOR_PANEL);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            protected javax.swing.plaf.basic.ComboPopup createPopup() {
                javax.swing.plaf.basic.BasicComboPopup popup = new javax.swing.plaf.basic.BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        scroller.getVerticalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
                        scroller.setBorder(BorderFactory.createLineBorder(COLOR_ACENTO, 1));
                        return scroller;
                    }
                };
                popup.setBorder(BorderFactory.createEmptyBorder());
                return popup;
            }
        });

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                if (index == -1) {
                    label.setBackground(COLOR_PANEL);
                } else {
                    label.setBackground(isSelected ? new Color(212, 175, 55, 50) : COLOR_PANEL);
                }
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        return cb;
    }

    private void validarYGuardar() {
        String nombre = txtNombre.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String rol = (String) cbRol.getSelectedItem();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar el nombre de usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (usuario == null && pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una contraseña para el nuevo usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (usuario == null) {
            usuario = new Usuario(0, nombre, pass, rol);
        } else {
            usuario.setNombreUsuario(nombre);
            if (!pass.isEmpty()) {
                usuario.setPasswordUsuario(pass);
            }
            usuario.setRolUsuario(rol);
        }

        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Usuario getUsuario() {
        return usuario;
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
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius / 2, radius / 2, radius / 2, radius / 2); }
    }
}
