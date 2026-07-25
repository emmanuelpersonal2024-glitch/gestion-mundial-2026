package com.mundial.vista;

import com.mundial.dao.PuntoDAO;
import com.mundial.modelo.FilaRanking;
import com.mundial.modelo.Usuario;
import com.mundial.vista.componentes.CustomScrollBarUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaRanking extends JPanel {

    private JTable tablaRanking;
    private DefaultTableModel modeloTabla;
    private PuntoDAO puntoDAO;
    private Usuario usuarioActual;

    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public VentanaRanking(Usuario usuarioActual) {
        this(usuarioActual, true);
    }

    public VentanaRanking(Usuario usuarioActual, boolean mostrarHeader) {
        this.usuarioActual = usuarioActual;
        this.puntoDAO = new PuntoDAO();
        
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        if (mostrarHeader) {
            add(crearHeader(), BorderLayout.NORTH);
        }

        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.setOpaque(false);
        panelCentro.add(crearTabla(), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        cargarDatosRanking();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                cargarDatosRanking();
            }
        });
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Ranking Global");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Tabla de posiciones general con los puntos de todos los jugadores.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(180, 190, 210));

        panelTextos.add(lblTitulo);
        panelTextos.add(lblSub);

        header.add(panelTextos, BorderLayout.CENTER);

        return header;
    }

    private JPanel crearTabla() {
        String[] columnas = {"Pos", "Jugador", "Puntos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRanking = new JTable(modeloTabla);

        // Estilos premium
        tablaRanking.setRowHeight(40);
        tablaRanking.setBackground(COLOR_PANEL);
        tablaRanking.setForeground(Color.WHITE);
        tablaRanking.setShowVerticalLines(false);
        tablaRanking.setShowHorizontalLines(true);
        tablaRanking.setGridColor(new Color(40, 50, 70));
        tablaRanking.setSelectionBackground(new Color(212, 175, 55, 50));
        tablaRanking.setSelectionForeground(Color.WHITE);
        tablaRanking.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tablaRanking.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaRanking.getTableHeader().setBackground(new Color(15, 20, 35));
        tablaRanking.getTableHeader().setForeground(COLOR_ACENTO);
        tablaRanking.getTableHeader().setPreferredSize(new Dimension(100, 40));
        tablaRanking.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACENTO));

        // Renderizador para el resaltado del usuario logeado y centrado
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Centrado por defecto
                if (column == 0 || column == 2) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                }

                if (column == 2) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    lbl.setForeground(new Color(40, 167, 69)); // Verde para los puntos
                } else {
                    lbl.setForeground(Color.WHITE);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }

                // Resaltar la fila si es el usuario actual
                String nombreFila = table.getValueAt(row, 1).toString();
                if (usuarioActual != null && usuarioActual.getNombreUsuario().equals(nombreFila)) {
                    lbl.setBackground(new Color(212, 175, 55, 60)); // Dorado sutil para destacar
                    lbl.setOpaque(true);
                    if (column != 2) {
                        lbl.setForeground(COLOR_ACENTO); // Texto dorado
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    }
                } else {
                    lbl.setBackground(isSelected ? new Color(212, 175, 55, 50) : COLOR_PANEL);
                    lbl.setOpaque(true);
                }

                return lbl;
            }
        };

        for (int i = 0; i < tablaRanking.getColumnCount(); i++) {
            tablaRanking.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Ajustar anchos
        tablaRanking.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaRanking.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaRanking.getColumnModel().getColumn(0).setMaxWidth(100);
        tablaRanking.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaRanking.getColumnModel().getColumn(2).setMaxWidth(150);

        JScrollPane scroll = new JScrollPane(tablaRanking);
        scroll.getViewport().setBackground(COLOR_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        JPanel wrapperTabla = new JPanel(new BorderLayout());
        wrapperTabla.setOpaque(false);
        wrapperTabla.add(scroll, BorderLayout.CENTER);

        return wrapperTabla;
    }

    private void cargarDatosRanking() {
        new Thread(() -> {
            puntoDAO.calcularYActualizarPuntosGlobales(); // Actualizar puntos primero
            List<FilaRanking> ranking = puntoDAO.obtenerRankingUsuarios();

            SwingUtilities.invokeLater(() -> {
                modeloTabla.setRowCount(0);
                for (FilaRanking r : ranking) {
                    modeloTabla.addRow(new Object[]{
                            r.getPosicion(),
                            r.getNombreUsuario(),
                            r.getPuntosTotales()
                    });
                }
            });
        }).start();
    }
}
