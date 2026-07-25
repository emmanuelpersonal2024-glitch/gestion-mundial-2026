package com.mundial.vista.componentes;

import com.mundial.dao.PartidoDAO;
import com.mundial.modelo.Partido;
import com.mundial.modelo.Pronostico;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ModalPronostico extends JDialog {

    private boolean confirmado = false;
    private Pronostico pronostico;
    private int idUsuario;

    private JComboBox<PartidoItem> cbPartidos;
    private JTextField txtGolesA, txtGolesB;
    private PartidoDAO partidoDAO = new PartidoDAO();

    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public ModalPronostico(Window parent, int idUsuario) {
        this(parent, idUsuario, null);
    }

    public ModalPronostico(Window parent, int idUsuario, Pronostico pEditar) {
        super(parent, pEditar == null ? "Registrar Pronóstico" : "Editar Pronóstico", Dialog.ModalityType.APPLICATION_MODAL);
        this.idUsuario = idUsuario;
        
        setSize(450, 450);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_FONDO);
        contentPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        this.pronostico = pEditar;

        JLabel lblTitulo = new JLabel(pEditar == null ? "Registrar Pronóstico" : "Editar Pronóstico");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        panelHeader.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
        contentPane.add(panelHeader, BorderLayout.NORTH);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(false);
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Partido
        if (pEditar == null) {
            cbPartidos = crearComboBoxPremiumPartidos();
            agregarCampo(panelForm, "Seleccionar Partido", cbPartidos);
        } else {
            JLabel lblPartidoFijo = new JLabel(pEditar.getNombreEquipoA() + " vs " + pEditar.getNombreEquipoB());
            lblPartidoFijo.setForeground(COLOR_ACENTO);
            lblPartidoFijo.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblPartidoFijo.setHorizontalAlignment(SwingConstants.CENTER);
            agregarCampo(panelForm, "Partido", lblPartidoFijo);
        }

        // Goles Local
        int valA = (pEditar != null) ? pEditar.getGolesEquipoA() : 0;
        txtGolesA = crearTextField();
        txtGolesA.setHorizontalAlignment(SwingConstants.CENTER);
        aplicarFiltroGoles(txtGolesA);
        if (pEditar != null) txtGolesA.setText(String.valueOf(valA));
        agregarCampo(panelForm, "Goles Equipo Local", txtGolesA);
        
        // Goles Visitante
        int valB = (pEditar != null) ? pEditar.getGolesEquipoB() : 0;
        txtGolesB = crearTextField();
        txtGolesB.setHorizontalAlignment(SwingConstants.CENTER);
        aplicarFiltroGoles(txtGolesB);
        if (pEditar != null) txtGolesB.setText(String.valueOf(valB));
        agregarCampo(panelForm, "Goles Equipo Visitante", txtGolesB);

        contentPane.add(panelForm, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(COLOR_PANEL);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(COLOR_ACENTO);
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> validarYGuardar());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(contentPane);
        if (pEditar == null) {
            cargarPartidosPendientes();
        }
    }
    
    private void cargarPartidosPendientes() {
        new Thread(() -> {
            List<Partido> partidos = partidoDAO.listar();
            LocalDateTime ahora = LocalDateTime.now();
            SwingUtilities.invokeLater(() -> {
                cbPartidos.removeAllItems();
                for (Partido p : partidos) {
                    String estado = p.getEstadoPartido();
                    if (estado == null || estado.equalsIgnoreCase("Programado") || estado.equalsIgnoreCase("PENDIENTE") || estado.trim().isEmpty()) {
                        if (esPartidoValidoParaPronostico(p, ahora)) {
                            cbPartidos.addItem(new PartidoItem(p));
                        }
                    }
                }
            });
        }).start();
    }

    private boolean esPartidoValidoParaPronostico(Partido p, LocalDateTime ahora) {
        if (p.getFechaPartido() == null || p.getHoraPartido() == null) {
            return false;
        }
        try {
            String horaStr = p.getHoraPartido();
            if (horaStr.length() == 5) {
                horaStr += ":00";
            }
            String fechaHoraStr = p.getFechaPartido() + " " + horaStr;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime fechaPartido = LocalDateTime.parse(fechaHoraStr, formatter);
            
            long minutosRestantes = ChronoUnit.MINUTES.between(ahora, fechaPartido);
            return minutosRestantes > 10;
        } catch (DateTimeParseException e) {
            System.err.println("Error al parsear fecha del partido: " + e.getMessage());
            return false;
        }
    }

    private void validarYGuardar() {
        int idPartidoSeleccionado;
        
        if (pronostico == null) { // Modo creación
            PartidoItem itemSeleccionado = (PartidoItem) cbPartidos.getSelectedItem();
            if (itemSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un partido válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Partido p = itemSeleccionado.getPartido();
            if (!esPartidoValidoParaPronostico(p, LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "El tiempo para modificar el pronóstico de este partido ha expirado.", "Tiempo Expirado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            idPartidoSeleccionado = p.getIdPartido();
        } else { // Modo edición
            Partido pDummy = new Partido();
            pDummy.setFechaPartido(pronostico.getFechaPartido());
            pDummy.setHoraPartido(pronostico.getHoraPartido());
            if (!esPartidoValidoParaPronostico(pDummy, LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "El tiempo para modificar el pronóstico de este partido ha expirado.", "Tiempo Expirado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            idPartidoSeleccionado = pronostico.getIdPartido();
        }
        
        int gA = 0;
        int gB = 0;
        try {
            gA = Integer.parseInt(txtGolesA.getText().trim());
            gB = Integer.parseInt(txtGolesB.getText().trim());
            if (gA < 0 || gA > 50 || gB < 0 || gB > 50) {
                JOptionPane.showMessageDialog(this, "Los goles deben ser números positivos entre 0 y 50.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Debe ingresar valores numéricos válidos para los goles.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        pronostico = new Pronostico(idUsuario, idPartidoSeleccionado, gA, gB);
        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Pronostico getPronostico() {
        return pronostico;
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
                if (!newText.matches("\\d*")) return false; 
                StringBuilder sb = new StringBuilder(currentText);
                sb.replace(offset, offset + length, newText);
                if (sb.length() == 0) return true; 
                if (sb.length() > 2) return false; 
                try {
                    int val = Integer.parseInt(sb.toString());
                    return val >= 0 && val <= 50;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }

    private JComboBox<PartidoItem> crearComboBoxPremiumPartidos() {
        JComboBox<PartidoItem> cb = new JComboBox<>();
        cb.setBackground(COLOR_FONDO);
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
                btn.setBackground(COLOR_FONDO);
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(COLOR_FONDO);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            protected javax.swing.plaf.basic.ComboPopup createPopup() {
                javax.swing.plaf.basic.BasicComboPopup popup = new javax.swing.plaf.basic.BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        scroller.getVerticalScrollBar().setUI(new CustomScrollBarUI());
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
                    label.setBackground(COLOR_FONDO);
                } else {
                    label.setBackground(isSelected ? new Color(212, 175, 55, 50) : COLOR_FONDO);
                }
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
        return cb;
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
    
    private class PartidoItem {
        private Partido partido;
        public PartidoItem(Partido partido) { this.partido = partido; }
        public Partido getPartido() { return partido; }
        @Override
        public String toString() {
            String horaStr = partido.getHoraPartido();
            if (horaStr != null && horaStr.length() >= 5) {
                horaStr = horaStr.substring(0, 5);
            } else {
                horaStr = "";
            }
            return partido.getFechaPartido() + " " + horaStr + " | " + partido.getNombreEquipoA() + " vs " + partido.getNombreEquipoB();
        }
    }
}
