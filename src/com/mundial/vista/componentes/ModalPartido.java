package com.mundial.vista.componentes;

import com.mundial.dao.EquipoDAO;
import com.mundial.dao.EstadioDAO;
import com.mundial.dao.FaseDAO;
import com.mundial.modelo.Equipo;
import com.mundial.modelo.Estadio;
import com.mundial.modelo.Fase;
import com.mundial.modelo.Partido;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;

public class ModalPartido extends JDialog {

    // Modo CREACIÓN
    private JComboBox<Fase> cbFase;
    private JComboBox<Estadio> cbEstadio;
    private JComboBox<Equipo> cbEquipoA;
    private JComboBox<Equipo> cbEquipoB;
    private JSpinner spinFecha;
    private JSpinner spinHora;

    // Modo EDICIÓN RESULTADO
    private JTextField txtGolesA;
    private JTextField txtGolesB;
    private JPanel panelGolesA;
    private JPanel panelGolesB;
    private JComboBox<String> cbEstado;

    private JButton btnGuardar;
    private JButton btnCancelar;

    private Partido partido;
    private boolean confirmado = false;
    private boolean esCreacion;

    private static final Color COLOR_FONDO = new Color(10, 14, 26);
    private static final Color COLOR_PANEL = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public ModalPartido(Window owner, String titulo, Partido partidoAEditar) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        this.partido = partidoAEditar;
        this.esCreacion = (partidoAEditar == null);

        setSize(450, esCreacion ? 650 : 450);
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
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(false);
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        if (esCreacion) {
            construirFormularioCreacion(panelForm);
        } else {
            construirFormularioEdicion(panelForm);
        }

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
        
        if (!esCreacion && "Finalizado".equalsIgnoreCase(partido.getEstadoPartido())) {
            btnGuardar.setEnabled(false);
        }

        panelFooter.add(btnCancelar);
        panelFooter.add(btnGuardar);

        contentPane.add(panelFooter, BorderLayout.SOUTH);
        setContentPane(contentPane);
    }

    private void construirFormularioCreacion(JPanel panelForm) {
        cbFase = crearComboBoxPremium();
        cbEstadio = crearComboBoxPremium();
        cbEquipoA = crearComboBoxPremium();
        cbEquipoB = crearComboBoxPremium();
        
        spinFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinFecha, "yyyy-MM-dd");
        spinFecha.setEditor(dateEditor);
        estilizarSpinner(spinFecha, dateEditor.getTextField());

        spinHora = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinHora, "HH:mm:ss");
        spinHora.setEditor(timeEditor);
        estilizarSpinner(spinHora, timeEditor.getTextField());

        // Cargar listas
        List<Fase> fases = new FaseDAO().listar();
        for (Fase f : fases) cbFase.addItem(f);

        List<Estadio> estadios = new EstadioDAO().listar();
        for (Estadio e : estadios) cbEstadio.addItem(e);

        List<Equipo> equipos = new EquipoDAO().listar();
        for (Equipo eq : equipos) {
            cbEquipoA.addItem(eq);
            cbEquipoB.addItem(eq);
        }

        agregarCampo(panelForm, "Fase del Torneo", cbFase);
        agregarCampo(panelForm, "Estadio", cbEstadio);
        agregarCampo(panelForm, "Equipo A (Local)", cbEquipoA);
        agregarCampo(panelForm, "Equipo B (Visitante)", cbEquipoB);
        agregarCampo(panelForm, "Fecha (Selector)", spinFecha);
        agregarCampo(panelForm, "Hora (Selector)", spinHora);
    }
    
    private void estilizarSpinner(JSpinner spinner, JFormattedTextField txt) {
        spinner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_PANEL.brighter(), 1, 8),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        spinner.setBackground(COLOR_PANEL);
        txt.setBackground(COLOR_PANEL);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(COLOR_ACENTO);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createEmptyBorder());
        
        // Estilizar flechitas
        for (Component c : spinner.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                b.setBackground(COLOR_PANEL.brighter());
                b.setForeground(Color.WHITE);
                b.setBorder(BorderFactory.createLineBorder(COLOR_PANEL));
                b.setFocusPainted(false);
            }
        }
    }
    
    private void estilizarSpinnerNumerico(JSpinner spinner) {
        spinner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_PANEL.brighter(), 1, 8),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        spinner.setBackground(COLOR_PANEL);

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        JFormattedTextField txt = editor.getTextField();
        txt.setBackground(COLOR_PANEL);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(COLOR_ACENTO);
        txt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txt.setBorder(BorderFactory.createEmptyBorder());
        txt.setHorizontalAlignment(SwingConstants.CENTER);
        txt.setEditable(false); // No permitir teclado

        // Estilizar flechitas
        for (Component c : spinner.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                b.setBackground(COLOR_PANEL.brighter());
                b.setForeground(Color.WHITE);
                b.setBorder(BorderFactory.createLineBorder(COLOR_PANEL));
                b.setFocusPainted(false);
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }
    }

    private void construirFormularioEdicion(JPanel panelForm) {
        JLabel lblInfo = new JLabel(partido.getNombreEquipoA() + " vs " + partido.getNombreEquipoB());
        lblInfo.setForeground(COLOR_ACENTO);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelForm.add(lblInfo);
        panelForm.add(Box.createVerticalStrut(20));

        cbEstado = crearComboBoxPremium();
        cbEstado.addItem("Programado");
        cbEstado.addItem("En Juego");
        cbEstado.addItem("Finalizado");

        if (partido.getEstadoPartido() != null) {
            cbEstado.setSelectedItem(partido.getEstadoPartido());
        }

        txtGolesA = crearTextField();
        txtGolesA.setHorizontalAlignment(SwingConstants.CENTER);
        aplicarFiltroGoles(txtGolesA);
        
        txtGolesB = crearTextField();
        txtGolesB.setHorizontalAlignment(SwingConstants.CENTER);
        aplicarFiltroGoles(txtGolesB);
        
        if (partido.getGolesEquipoA() > 0 || partido.getGolesEquipoB() > 0 || "Finalizado".equalsIgnoreCase(partido.getEstadoPartido())) {
            txtGolesA.setText(String.valueOf(partido.getGolesEquipoA()));
            txtGolesB.setText(String.valueOf(partido.getGolesEquipoB()));
        }

        cbEstado.addItemListener(e -> actualizarEstadoGoles());

        agregarCampo(panelForm, "Estado del Partido", cbEstado);
        
        panelGolesA = agregarCampo(panelForm, "Goles Equipo A", txtGolesA);
        panelGolesB = agregarCampo(panelForm, "Goles Equipo B", txtGolesB);
        
        actualizarEstadoGoles();
        
        if ("Finalizado".equalsIgnoreCase(partido.getEstadoPartido())) {
            cbEstado.setEnabled(false);
            txtGolesA.setEnabled(false);
            txtGolesB.setEnabled(false);
            
            // Para asegurar que el color de fuente se vea bien aunque esté deshabilitado en Metal/Basic UI
            txtGolesA.setDisabledTextColor(Color.GRAY);
            txtGolesB.setDisabledTextColor(Color.GRAY);
        }
    }
    
    private void actualizarEstadoGoles() {
        boolean finalizado = "Finalizado".equals(cbEstado.getSelectedItem());
        if (panelGolesA != null) panelGolesA.setVisible(finalizado);
        if (panelGolesB != null) panelGolesB.setVisible(finalizado);
    }

    private JPanel agregarCampo(JPanel panel, String labelText, Component comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(new Color(180, 190, 210));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        
        p.add(Box.createVerticalStrut(5));
        
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comp.setPreferredSize(new Dimension(250, 35));
        ((JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(comp);
        
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(p);
        return p;
    }
    
    private void aplicarFiltroGoles(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (esGolesValido(fb.getDocument().getText(0, fb.getDocument().getLength()), string, offset, 0)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (esGolesValido(fb.getDocument().getText(0, fb.getDocument().getLength()), text, offset, length)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean esGolesValido(String currentText, String newText, int offset, int length) {
                if (!newText.matches("\\d*")) return false; // Solo permite dígitos numéricos
                StringBuilder sb = new StringBuilder(currentText);
                sb.replace(offset, offset + length, newText);
                if (sb.length() == 0) return true; // Permitir que el campo quede vacío mientras se edita
                if (sb.length() > 2) return false; // Un número hasta 50 tiene máximo 2 dígitos
                try {
                    int val = Integer.parseInt(sb.toString());
                    return val >= 0 && val <= 50;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
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

    private <T> JComboBox<T> crearComboBoxPremium() {
        JComboBox<T> cb = new JComboBox<>();
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
        if (esCreacion) {
            Fase f = (Fase) cbFase.getSelectedItem();
            Estadio est = (Estadio) cbEstadio.getSelectedItem();
            Equipo ea = (Equipo) cbEquipoA.getSelectedItem();
            Equipo eb = (Equipo) cbEquipoB.getSelectedItem();

            if (f == null || est == null || ea == null || eb == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar todos los campos desplegables.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (ea.getIdEquipo() == eb.getIdEquipo()) {
                JOptionPane.showMessageDialog(this, "El Equipo A y el Equipo B no pueden ser el mismo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (f.getNombreFase().equalsIgnoreCase("Fase de Grupos")) {
                if (ea.getIdGrupo() != eb.getIdGrupo()) {
                    JOptionPane.showMessageDialog(this, "En la Fase de Grupos, los partidos deben ser entre equipos del mismo grupo.", "Grupos Diferentes", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                com.mundial.dao.PartidoDAO partidoDAO = new com.mundial.dao.PartidoDAO();
                int idExistente = partidoDAO.obtenerIdPartidoFaseGrupos(f.getIdFase(), ea.getIdEquipo(), eb.getIdEquipo());
                if (idExistente != -1) {
                    JOptionPane.showMessageDialog(this, "Este partido ya está programado en la Fase de Grupos.\nID del partido: " + idExistente, "Partido Duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            java.util.Date dFecha = (java.util.Date) spinFecha.getValue();
            java.util.Date dHora = (java.util.Date) spinHora.getValue();
            java.text.SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm:ss");
            
            String strFecha = sdfFecha.format(dFecha);
            String strHora = sdfHora.format(dHora);

            partido = new Partido(f.getIdFase(), est.getIdEstadio(), ea.getIdEquipo(), eb.getIdEquipo(), strFecha, strHora);

        } else {
            String estado = (String) cbEstado.getSelectedItem();
            int ga = 0;
            int gb = 0;
            
            if ("Finalizado".equals(estado)) {
                try {
                    ga = Integer.parseInt(txtGolesA.getText().trim());
                    gb = Integer.parseInt(txtGolesB.getText().trim());
                    
                    if (ga < 0 || ga > 50 || gb < 0 || gb > 50) {
                        JOptionPane.showMessageDialog(this, "Los goles deben ser números positivos entre 0 y 50.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar valores numéricos válidos para los goles.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            partido.setGolesEquipoA(ga);
            partido.setGolesEquipoB(gb);
            partido.setEstadoPartido(estado);
        }

        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Partido getPartido() {
        return partido;
    }

    public boolean isEsCreacion() {
        return esCreacion;
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
