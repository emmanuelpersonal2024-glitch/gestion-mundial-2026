package com.mundial.vista;

import com.mundial.dao.GrupoDAO;
import com.mundial.dao.PosicionDAO;
import com.mundial.modelo.Grupo;
import com.mundial.modelo.FilaPosicion;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class VentanaPosiciones extends JPanel {

    private JComboBox<Grupo> cbGrupos;
    private JTable tablaPosiciones;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    private GrupoDAO grupoDAO = new GrupoDAO();
    private PosicionDAO posicionDAO = new PosicionDAO();

    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public VentanaPosiciones() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(crearHeader(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.setOpaque(false);
        panelCentro.add(crearBarraFiltro(), BorderLayout.NORTH);
        panelCentro.add(crearTabla(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        cargarComboGrupos();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                cargarComboGrupos();
            }
        });
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Tabla de Posiciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Consulta la clasificación de las selecciones y sus estadísticas detalladas por grupo.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(180, 190, 210));

        panelTextos.add(lblTitulo);
        panelTextos.add(lblSub);

        header.add(panelTextos, BorderLayout.CENTER);

        return header;
    }

    private JPanel crearBarraFiltro() {
        JPanel panelBusqueda = new JPanel(new BorderLayout(15, 0));
        panelBusqueda.setBackground(COLOR_PANEL);
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelIzquierdo.setOpaque(false);

        JLabel lblSeleccione = new JLabel("Seleccionar Grupo:");
        lblSeleccione.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeleccione.setForeground(Color.WHITE);

        cbGrupos = crearComboBoxPremium();
        cbGrupos.setPreferredSize(new Dimension(250, 35));
        
        // Cargar tabla automáticamente al seleccionar un grupo
        cbGrupos.addActionListener(e -> cargarTablaPosiciones());

        panelIzquierdo.add(lblSeleccione);
        panelIzquierdo.add(cbGrupos);

        panelBusqueda.add(panelIzquierdo, BorderLayout.WEST);

        return panelBusqueda;
    }

    private JComboBox<Grupo> crearComboBoxPremium() {
        JComboBox<Grupo> cb = new JComboBox<>();
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

    private JPanel crearTabla() {
        String[] columnas = {"Pos", "Selección", "Grupo", "PJ", "PG", "PE", "PP", "GF", "GC", "GD", "PTS"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPosiciones = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaPosiciones.setRowSorter(sorter);

        // Estilos premium
        tablaPosiciones.setRowHeight(40);
        tablaPosiciones.setBackground(COLOR_PANEL);
        tablaPosiciones.setForeground(Color.WHITE);
        tablaPosiciones.setShowVerticalLines(false);
        tablaPosiciones.setShowHorizontalLines(true);
        tablaPosiciones.setGridColor(new Color(40, 50, 70));
        tablaPosiciones.setSelectionBackground(new Color(212, 175, 55, 50));
        tablaPosiciones.setSelectionForeground(Color.WHITE);
        tablaPosiciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tablaPosiciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaPosiciones.getTableHeader().setBackground(new Color(15, 20, 35));
        tablaPosiciones.getTableHeader().setForeground(COLOR_ACENTO);
        tablaPosiciones.getTableHeader().setPreferredSize(new Dimension(100, 40));
        tablaPosiciones.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACENTO));

        // Renderizador para el PTS (destacarlo)
        tablaPosiciones.getColumnModel().getColumn(10).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lbl.setForeground(new Color(40, 167, 69)); // Verde para los puntos
                return lbl;
            }
        });

        // Configuración de centrado para casi todo menos el nombre
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        int[] colsCentro = {0, 2, 3, 4, 5, 6, 7, 8, 9};
        for(int c : colsCentro) {
            tablaPosiciones.getColumnModel().getColumn(c).setCellRenderer(centerRenderer);
        }

        // Ajustar anchos (Modo estricto para evitar problemas de layout)
        tablaPosiciones.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Fijos: 0 (Pos), 2 (Grp), 3-10 (Metricas)
        tablaPosiciones.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaPosiciones.getColumnModel().getColumn(0).setMaxWidth(60);
        
        tablaPosiciones.getColumnModel().getColumn(2).setPreferredWidth(60);
        tablaPosiciones.getColumnModel().getColumn(2).setMaxWidth(80);
        
        for (int i = 3; i <= 9; i++) {
            tablaPosiciones.getColumnModel().getColumn(i).setPreferredWidth(50);
            tablaPosiciones.getColumnModel().getColumn(i).setMaxWidth(70);
        }
        
        // PTS un poco más grande
        tablaPosiciones.getColumnModel().getColumn(10).setPreferredWidth(60);
        tablaPosiciones.getColumnModel().getColumn(10).setMaxWidth(80);
        
        // Seleccion (Expande)
        tablaPosiciones.getColumnModel().getColumn(1).setPreferredWidth(250); 
        tablaPosiciones.getColumnModel().getColumn(1).setMinWidth(150);

        JScrollPane scroll = new JScrollPane(tablaPosiciones);
        scroll.getViewport().setBackground(COLOR_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        scroll.getVerticalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());

        JPanel wrapperTabla = new JPanel(new BorderLayout());
        wrapperTabla.setOpaque(false);
        wrapperTabla.add(scroll, BorderLayout.CENTER);

        return wrapperTabla;
    }

    private void cargarComboGrupos() {
        new Thread(() -> {
            List<Grupo> grupos = grupoDAO.listar();
            SwingUtilities.invokeLater(() -> {
                cbGrupos.removeAllItems();
                for (Grupo g : grupos) {
                    cbGrupos.addItem(g);
                }
                if (cbGrupos.getItemCount() > 0) {
                    cargarTablaPosiciones();
                }
            });
        }).start();
    }

    private void cargarTablaPosiciones() {
        Grupo grupoSeleccionado = (Grupo) cbGrupos.getSelectedItem();
        if (grupoSeleccionado == null) return;

        new Thread(() -> {
            List<FilaPosicion> posiciones = posicionDAO.obtenerTablaPorGrupo(grupoSeleccionado.getIdGrupo());

            SwingUtilities.invokeLater(() -> {
                modeloTabla.setRowCount(0);
                int puesto = 1;
                for (FilaPosicion f : posiciones) {
                    modeloTabla.addRow(new Object[]{
                            puesto++,
                            f.getNombreEquipo(),
                            f.getNombreGrupo(),
                            f.getPj(),
                            f.getPg(),
                            f.getPe(),
                            f.getPp(),
                            f.getGf(),
                            f.getGc(),
                            (f.getGd() > 0 ? "+" : "") + f.getGd(),
                            f.getPts()
                    });
                }
            });
        }).start();
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
