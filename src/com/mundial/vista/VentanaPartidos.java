package com.mundial.vista;

import com.mundial.dao.PartidoDAO;
import com.mundial.modelo.Partido;
import com.mundial.vista.componentes.ModalPartido;
import com.mundial.vista.componentes.TableActionCellEditor;
import com.mundial.vista.componentes.TableActionCellRender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class VentanaPartidos extends JPanel {

    private JTable tablaPartidos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private PartidoDAO dao;
    private JComboBox<String> cbFiltroEstado;
    
    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public VentanaPartidos() {
        dao = new PartidoDAO();
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(crearHeader(), BorderLayout.NORTH);
        
        cbFiltroEstado = crearComboBoxPremium();
        cbFiltroEstado.addItem("Seleccione Estado");
        cbFiltroEstado.addItem("Programado");
        cbFiltroEstado.addItem("En Juego");
        cbFiltroEstado.addItem("Finalizado");

        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.setOpaque(false);
        panelCentro.add(crearBarraBusqueda(), BorderLayout.NORTH);
        panelCentro.add(crearTabla(), BorderLayout.CENTER);
        
        add(panelCentro, BorderLayout.CENTER);

        cargarDatosTabla();
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                cargarDatosTabla();
            }
        });
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Gestión de Partidos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Administra el calendario de juegos, actualiza resultados en vivo y registra estadísticas.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(180, 190, 210));
        
        panelTextos.add(lblTitulo);
        panelTextos.add(lblSub);
        
        JButton btnAgregar = new JButton(" Programar Partido ");
        java.net.URL iconUrl = getClass().getResource("/com/mundial/recursos/iconos/agregar.png");
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            btnAgregar.setIcon(new ImageIcon(img));
        }
        btnAgregar.setBackground(new Color(40, 167, 69)); // Verde elegante
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setFocusPainted(false);
        btnAgregar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.addActionListener(e -> mostrarModalAgregar());
        
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.setOpaque(false);
        panelBtn.add(btnAgregar);
        
        header.add(panelTextos, BorderLayout.CENTER);
        header.add(panelBtn, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel crearBarraBusqueda() {
        JPanel panelBusqueda = new JPanel(new BorderLayout(15, 0));
        panelBusqueda.setBackground(COLOR_PANEL);
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JTextField txtBuscar = new JTextField("Buscar por Equipo, Fase, Estadio o Fecha...");
        txtBuscar.setBackground(COLOR_FONDO);
        txtBuscar.setForeground(Color.GRAY);
        txtBuscar.setCaretColor(COLOR_ACENTO);
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        Runnable aplicarFiltros = () -> {
            java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<>();
            String text = txtBuscar.getText();
            if (!text.equals("Buscar por Equipo, Fase, Estadio o Fecha...") && text.trim().length() > 0) {
                filtros.add(RowFilter.regexFilter("(?i)" + text));
            }
            if (cbFiltroEstado.getSelectedIndex() > 0) {
                String estadoSeleccionado = (String) cbFiltroEstado.getSelectedItem();
                // Columna 6 es Estado
                filtros.add(RowFilter.regexFilter("^" + estadoSeleccionado + "$", 6));
            }
            if (filtros.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filtros));
            }
        };

        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().equals("Buscar por Equipo, Fase, Estadio o Fecha...")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setForeground(Color.GRAY);
                    txtBuscar.setText("Buscar por Equipo, Fase, Estadio o Fecha...");
                }
            }
        });
        
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                aplicarFiltros.run();
            }
        });
        
        cbFiltroEstado.addActionListener(e -> aplicarFiltros.run());
        
        JButton btnLimpiar = new JButton("Limpiar Filtros");
        btnLimpiar.setBackground(new Color(60, 70, 90));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setForeground(Color.GRAY);
            txtBuscar.setText("Buscar por Equipo, Fase, Estadio o Fecha...");
            cbFiltroEstado.setSelectedIndex(0);
            sorter.setRowFilter(null);
            tablaPartidos.requestFocus(); 
        });
        
        JPanel panelControles = new JPanel(new BorderLayout(15, 0));
        panelControles.setOpaque(false);
        panelControles.add(cbFiltroEstado, BorderLayout.CENTER);
        panelControles.add(btnLimpiar, BorderLayout.EAST);
        
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(panelControles, BorderLayout.EAST);
        
        return panelBusqueda;
    }
    
    private JPanel crearTabla() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Fase", "Local", "Res", "Visitante", "Fecha / Hora", "Estado", "Acciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; 
            }
        };

        tablaPartidos = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaPartidos.setRowSorter(sorter);
        
        // Estilos premium para la tabla
        tablaPartidos.setRowHeight(40);
        tablaPartidos.setBackground(COLOR_PANEL);
        tablaPartidos.setForeground(Color.WHITE);
        tablaPartidos.setShowVerticalLines(false);
        tablaPartidos.setShowHorizontalLines(true);
        tablaPartidos.setGridColor(new Color(40, 50, 70));
        tablaPartidos.setSelectionBackground(new Color(212, 175, 55, 50));
        tablaPartidos.setSelectionForeground(Color.WHITE);
        tablaPartidos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tablaPartidos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaPartidos.getTableHeader().setBackground(new Color(15, 20, 35));
        tablaPartidos.getTableHeader().setForeground(COLOR_ACENTO);
        tablaPartidos.getTableHeader().setPreferredSize(new Dimension(100, 40));
        tablaPartidos.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACENTO));
        
        // Configurar botones de accion
        tablaPartidos.getColumnModel().getColumn(7).setCellRenderer(new TableActionCellRender());
        tablaPartidos.getColumnModel().getColumn(7).setCellEditor(new TableActionCellEditor(new TableActionCellEditor.TableActionListener() {
            @Override
            public void onEdit(int row) {
                mostrarModalEditar(row);
            }

            @Override
            public void onDelete(int row) {
                ejecutarEliminar(row);
            }
        }));
        
        // Configurar renderer para el Estado (Punto de Color)
        tablaPartidos.getColumnModel().getColumn(6).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
                panel.setOpaque(true);
                panel.setBackground(isSelected ? new Color(212, 175, 55, 50) : COLOR_PANEL);
                
                String estado = (value != null) ? value.toString() : "Programado";
                Color dotColor;
                if (estado.equalsIgnoreCase("En Juego")) {
                    dotColor = new Color(255, 193, 7); // Amarillo
                } else if (estado.equalsIgnoreCase("Finalizado")) {
                    dotColor = new Color(40, 167, 69); // Verde
                } else {
                    dotColor = new Color(13, 110, 253); // Azul
                }
                
                JPanel dot = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(dotColor);
                        g2.fillOval(0, 0, 14, 14);
                    }
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(14, 14);
                    }
                };
                dot.setOpaque(false);
                
                panel.add(dot);
                
                // Centrado en la celda
                panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                return panel;
            }
        });
        
        // Ajustar anchos
        tablaPartidos.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaPartidos.getColumnModel().getColumn(0).setMaxWidth(60);
        tablaPartidos.getColumnModel().getColumn(1).setPreferredWidth(100); // Fase
        tablaPartidos.getColumnModel().getColumn(2).setPreferredWidth(130); // Local
        tablaPartidos.getColumnModel().getColumn(3).setPreferredWidth(60); // Res (Goles)
        tablaPartidos.getColumnModel().getColumn(3).setMaxWidth(80);
        tablaPartidos.getColumnModel().getColumn(4).setPreferredWidth(130); // Visitante
        tablaPartidos.getColumnModel().getColumn(5).setPreferredWidth(200); // Fecha / Hora
        tablaPartidos.getColumnModel().getColumn(6).setPreferredWidth(50); // Estado (Solo el punto)
        tablaPartidos.getColumnModel().getColumn(6).setMaxWidth(60);
        tablaPartidos.getColumnModel().getColumn(7).setPreferredWidth(90); // Acciones
        tablaPartidos.getColumnModel().getColumn(7).setMaxWidth(90);

        JScrollPane scroll = new JScrollPane(tablaPartidos);
        scroll.getViewport().setBackground(COLOR_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        // Asignar el ScrollBarUI personalizado
        scroll.getVerticalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        
        // --- Footer de Leyenda de Estados ---
        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelLeyenda.setOpaque(false);
        
        panelLeyenda.add(crearItemLeyenda("Programado", new Color(13, 110, 253))); // Azul
        panelLeyenda.add(crearItemLeyenda("En Juego", new Color(255, 193, 7)));   // Amarillo
        panelLeyenda.add(crearItemLeyenda("Finalizado", new Color(40, 167, 69))); // Verde
        
        JPanel wrapperTabla = new JPanel(new BorderLayout());
        wrapperTabla.setOpaque(false);
        wrapperTabla.add(scroll, BorderLayout.CENTER);
        wrapperTabla.add(panelLeyenda, BorderLayout.SOUTH);
        
        return wrapperTabla;
    }
    
    private JPanel crearItemLeyenda(String texto, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, 12, 12);
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(12, 12);
            }
        };
        dot.setOpaque(false);
        
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(new Color(180, 190, 210));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(dot);
        panel.add(lbl);
        return panel;
    }
    
    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<Partido> lista = dao.listar();
        for (Partido p : lista) {
            String res = p.getGolesEquipoA() + " - " + p.getGolesEquipoB();
            
            String horaStr = p.getHoraPartido();
            if (horaStr != null && horaStr.length() >= 5) {
                horaStr = horaStr.substring(0, 5); // Remueve los segundos, ej: 15:00
            }
            String fh = p.getFechaPartido() + " " + horaStr;
            
            String est = (p.getEstadoPartido() != null) ? p.getEstadoPartido() : "Programado";
            
            modeloTabla.addRow(new Object[]{
                p.getIdPartido(), 
                p.getNombreFase(), 
                p.getNombreEquipoA(), 
                res, 
                p.getNombreEquipoB(), 
                fh, 
                est, 
                ""
            });
        }
    }

    private void mostrarModalAgregar() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalPartido modal = new ModalPartido(parent, "Programar Partido", null);
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Partido nuevo = modal.getPartido();
            if (dao.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Partido programado correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarModalEditar(int row) {
        int id = Integer.parseInt(tablaPartidos.getValueAt(row, 0).toString());
        List<Partido> todos = dao.listar();
        Partido actual = null;
        for (Partido p : todos) {
            if (p.getIdPartido() == id) {
                actual = p;
                break;
            }
        }
        
        if (actual == null) return;
        
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalPartido modal = new ModalPartido(parent, "Actualizar Resultado", actual);
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Partido editado = modal.getPartido();
            if (dao.actualizarResultado(editado.getIdPartido(), editado.getGolesEquipoA(), editado.getGolesEquipoB(), editado.getEstadoPartido())) {
                JOptionPane.showMessageDialog(this, "Resultado actualizado correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarEliminar(int row) {
        int id = Integer.parseInt(tablaPartidos.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de eliminar este partido (ID: " + id + ")?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String error = dao.eliminar(id);
            if (error == null) {
                JOptionPane.showMessageDialog(this, "Partido eliminado correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, error, "Error de eliminación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JComboBox<String> crearComboBoxPremium() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setBackground(COLOR_FONDO);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setFocusable(false);
        cb.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_PANEL.brighter(), 1, 8),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        
        cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
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

    static class RoundedBorder extends javax.swing.border.AbstractBorder {
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