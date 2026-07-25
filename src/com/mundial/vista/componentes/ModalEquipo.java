package com.mundial.vista.componentes;

import com.mundial.dao.GrupoDAO;
import com.mundial.modelo.Equipo;
import com.mundial.modelo.Grupo;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.List;

public class ModalEquipo extends JDialog {

    private JTextField txtNombre;
    private JComboBox<Grupo> cbGrupo;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private Equipo equipo;
    private boolean confirmado = false;
    
    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public ModalEquipo(Window owner, String titulo, Equipo equipoAEditar) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        this.equipo = equipoAEditar;
        
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
        JPanel panelForm = new JPanel(new GridLayout(4, 1, 0, 5));
        panelForm.setOpaque(false);
        
        JLabel lblNombre = new JLabel("Nombre del Equipo (País)");
        lblNombre.setForeground(new Color(180, 190, 210));
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre = crearTextField();
        
        JLabel lblGrupo = new JLabel("Grupo asignado");
        lblGrupo.setForeground(new Color(180, 190, 210));
        lblGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbGrupo = crearComboBox();
        cargarGrupos();
        
        if (equipoAEditar != null) {
            txtNombre.setText(equipoAEditar.getNombreEquipo());
            seleccionarGrupo(equipoAEditar.getIdGrupo());
        }
        
        panelForm.add(lblNombre);
        panelForm.add(txtNombre);
        panelForm.add(lblGrupo);
        panelForm.add(cbGrupo);
        
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
    
    private JComboBox<Grupo> crearComboBox() {
        JComboBox<Grupo> cb = new JComboBox<>();
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
                JButton btn = new JButton("\u25BC"); // Triángulo hacia abajo
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
                    // Cuando pinta el valor seleccionado en la caja principal
                    label.setBackground(COLOR_PANEL);
                } else {
                    // Cuando pinta los items en el popup
                    label.setBackground(isSelected ? new Color(212, 175, 55, 50) : COLOR_PANEL);
                }
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
        
        return cb;
    }
    
    private void cargarGrupos() {
        GrupoDAO dao = new GrupoDAO();
        List<Grupo> grupos = dao.listar();
        for (Grupo g : grupos) {
            cbGrupo.addItem(g);
        }
    }
    
    private void seleccionarGrupo(int idGrupo) {
        for (int i = 0; i < cbGrupo.getItemCount(); i++) {
            if (cbGrupo.getItemAt(i).getIdGrupo() == idGrupo) {
                cbGrupo.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void validarYGuardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del equipo es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Grupo grupoSeleccionado = (Grupo) cbGrupo.getSelectedItem();
        if (grupoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un grupo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validación: Un grupo solo puede tener 4 equipos
        if (equipo == null || equipo.getIdGrupo() != grupoSeleccionado.getIdGrupo()) {
            com.mundial.dao.EquipoDAO equipoDAO = new com.mundial.dao.EquipoDAO();
            if (equipoDAO.contarEquiposPorGrupo(grupoSeleccionado.getIdGrupo()) >= 4) {
                JOptionPane.showMessageDialog(this, "El grupo seleccionado ya tiene el máximo de 4 equipos.", "Grupo Lleno", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        if (equipo == null) {
            equipo = new Equipo(txtNombre.getText().trim(), grupoSeleccionado.getIdGrupo());
        } else {
            equipo.setNombreEquipo(txtNombre.getText().trim());
            equipo.setIdGrupo(grupoSeleccionado.getIdGrupo());
        }
        
        confirmado = true;
        dispose();
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public Equipo getEquipo() {
        return equipo;
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
