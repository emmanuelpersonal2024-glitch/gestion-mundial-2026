package com.mundial.vista;

import com.mundial.dao.PronosticoDAO;
import com.mundial.dao.PuntoDAO;
import com.mundial.dao.UsuarioDAO;
import com.mundial.modelo.Pronostico;
import com.mundial.modelo.Usuario;
import com.mundial.vista.componentes.CustomScrollBarUI;
import com.mundial.vista.componentes.ModalPronostico;
import com.mundial.vista.componentes.TableActionCellEditor;
import com.mundial.vista.componentes.TableActionCellRender;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class VentanaAuditoriaPronosticos extends JPanel {

    // Comunes
    private JLabel lblPuntosTotales;
    private JTable tablaAuditoria;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Pronostico> historialActual = new ArrayList<>();

    // Solo ADMIN
    private JComboBox<Usuario> cbUsuarios;
    private JPanel panelTablas;
    private CardLayout cardLayoutTablas;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private PronosticoDAO pronosticoDAO = new PronosticoDAO();
    private PuntoDAO puntoDAO = new PuntoDAO();

    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    private Usuario usuarioFijo;

    public VentanaAuditoriaPronosticos() {
        this(null);
    }

    public VentanaAuditoriaPronosticos(Usuario usuarioFijo) {
        this.usuarioFijo = usuarioFijo;
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(crearHeader(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.setOpaque(false);
        
        if (usuarioFijo == null) {
            panelCentro.add(crearBarraFiltroAdmin(), BorderLayout.NORTH);
        } else {
            panelCentro.add(crearBarraBusquedaUser(), BorderLayout.NORTH);
        }
        
        cardLayoutTablas = new CardLayout();
        panelTablas = new JPanel(cardLayoutTablas);
        panelTablas.setOpaque(false);
        panelTablas.add(crearTabla(), "Pronosticos");
        
        if (usuarioFijo == null) {
            panelTablas.add(new VentanaRanking(null, false), "Ranking");
        }
        
        panelCentro.add(panelTablas, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        if (usuarioFijo == null) {
            cargarComboboxUsuarios();
        } else {
            cargarDatosUsuarioFijo();
        }
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (usuarioFijo == null) {
                    cargarComboboxUsuarios();
                } else {
                    cargarDatosUsuarioFijo();
                }
            }
        });
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setOpaque(false);

        JLabel lblTitulo = new JLabel(usuarioFijo == null ? "Auditoría de Jugadores" : "Mis Pronósticos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(usuarioFijo == null ? "Revisa el historial de pronósticos y el puntaje total de cada usuario." : "Registra tus predicciones y revisa los puntos obtenidos en cada partido.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(180, 190, 210));

        panelTextos.add(lblTitulo);
        panelTextos.add(lblSub);

        header.add(panelTextos, BorderLayout.CENTER);

        // Si es USER, agregar boton de Añadir Pronostico
        if (usuarioFijo != null) {
            JButton btnAgregar = new JButton(" Añadir Pronóstico ");
            java.net.URL iconUrl = getClass().getResource("/com/mundial/recursos/iconos/agregar.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btnAgregar.setIcon(new ImageIcon(img));
            }
            btnAgregar.setBackground(new Color(40, 167, 69)); // Verde
            btnAgregar.setForeground(Color.WHITE);
            btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnAgregar.setFocusPainted(false);
            btnAgregar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAgregar.addActionListener(e -> mostrarModalAgregar());
            
            JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBtn.setOpaque(false);
            panelBtn.add(btnAgregar);
            
            header.add(panelBtn, BorderLayout.EAST);
        }

        return header;
    }

    private void mostrarModalAgregar() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalPronostico modal = new ModalPronostico(parent, usuarioFijo.getIdUsuario());
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Pronostico p = modal.getPronostico();
            new Thread(() -> {
                boolean exito = pronosticoDAO.guardarOActualizar(p);
                SwingUtilities.invokeLater(() -> {
                    if (exito) {
                        JOptionPane.showMessageDialog(this, "¡Pronóstico registrado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatosUsuarioFijo();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al guardar el pronóstico.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start();
        }
    }

    private JPanel crearBarraFiltroAdmin() {
        JPanel panelBusqueda = new JPanel(new BorderLayout(15, 0));
        panelBusqueda.setBackground(COLOR_PANEL);
        // Reducido el padding vertical a 10px para coincidir con otras vistas
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelIzquierdo.setOpaque(false);

        JLabel lblSeleccione = new JLabel("Jugador:");
        lblSeleccione.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeleccione.setForeground(Color.WHITE);

        cbUsuarios = crearComboBoxPremiumUsuarios();
        cbUsuarios.setPreferredSize(new Dimension(250, 35));

        cbUsuarios.addActionListener(e -> cargarDatosUsuario());

        panelIzquierdo.add(lblSeleccione);
        panelIzquierdo.add(cbUsuarios);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelDerecho.setOpaque(false);

        lblPuntosTotales = new JLabel("Puntos Totales: -- pts");
        lblPuntosTotales.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPuntosTotales.setForeground(new Color(40, 167, 69)); // Verde

        panelDerecho.add(lblPuntosTotales);

        panelBusqueda.add(panelIzquierdo, BorderLayout.WEST);
        panelBusqueda.add(panelDerecho, BorderLayout.EAST);

        return panelBusqueda;
    }
    
    private JPanel crearBarraBusquedaUser() {
        JPanel panelBusqueda = new JPanel(new BorderLayout(15, 0));
        panelBusqueda.setBackground(COLOR_PANEL);
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JTextField txtBuscar = new JTextField("Buscar por partido o estado...");
        txtBuscar.setBackground(COLOR_FONDO);
        txtBuscar.setForeground(Color.GRAY);
        txtBuscar.setCaretColor(COLOR_ACENTO);
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().equals("Buscar por partido o estado...")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setForeground(Color.GRAY);
                    txtBuscar.setText("Buscar por partido o estado...");
                }
            }
        });
        
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtBuscar.getText();
                if (text.equals("Buscar por partido o estado...")) return;
                
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelDerecho.setOpaque(false);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(60, 70, 90));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setForeground(Color.GRAY);
            txtBuscar.setText("Buscar por partido o estado...");
            sorter.setRowFilter(null);
            tablaAuditoria.requestFocus(); 
        });

        lblPuntosTotales = new JLabel("Puntos Totales: -- pts");
        lblPuntosTotales.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPuntosTotales.setForeground(new Color(40, 167, 69)); // Verde

        panelDerecho.add(btnLimpiar);
        panelDerecho.add(lblPuntosTotales);

        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(panelDerecho, BorderLayout.EAST);
        
        return panelBusqueda;
    }

    private JComboBox<Usuario> crearComboBoxPremiumUsuarios() {
        JComboBox<Usuario> cb = new JComboBox<>();
        cb.setBackground(COLOR_FONDO);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setFocusable(false);
        cb.setBorder(BorderFactory.createCompoundBorder(
                new VentanaAuditoriaPronosticos.RoundedBorder(COLOR_PANEL.brighter(), 1, 8),
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

    private JPanel crearTabla() {
        Object[] columnNames = (usuarioFijo != null) 
                ? new Object[]{"Fecha y Hora", "Partido", "Estado", "Predicción", "Resultado Real", "Puntos", "Acciones"}
                : new Object[]{"Fecha y Hora", "Partido", "Estado", "Predicción", "Resultado Real", "Puntos"};

        modeloTabla = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return (usuarioFijo != null && column == 6);
            }
        };

        tablaAuditoria = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaAuditoria.setRowSorter(sorter);

        // Estilos premium
        tablaAuditoria.setRowHeight(40);
        tablaAuditoria.setBackground(COLOR_PANEL);
        tablaAuditoria.setForeground(Color.WHITE);
        tablaAuditoria.setShowVerticalLines(false);
        tablaAuditoria.setShowHorizontalLines(true);
        tablaAuditoria.setGridColor(new Color(40, 50, 70));
        tablaAuditoria.setSelectionBackground(new Color(212, 175, 55, 50));
        tablaAuditoria.setSelectionForeground(Color.WHITE);
        tablaAuditoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tablaAuditoria.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaAuditoria.getTableHeader().setBackground(new Color(15, 20, 35));
        tablaAuditoria.getTableHeader().setForeground(COLOR_ACENTO);
        tablaAuditoria.getTableHeader().setPreferredSize(new Dimension(100, 40));
        tablaAuditoria.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACENTO));

        // Configurar renderizador especial para Estado del Partido (columna 2)
        tablaAuditoria.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
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
                
                return panel;
            }
        });

        // Configurar renderizador especial para Puntos (columna 5)
        tablaAuditoria.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                if ("3 pts".equals(value)) {
                    lbl.setForeground(new Color(40, 167, 69)); // Verde (Acertó Todo)
                } else if ("1 pts".equals(value)) {
                    lbl.setForeground(new Color(255, 193, 7)); // Amarillo (Acertó Ganador)
                } else if ("0 pts".equals(value)) {
                    lbl.setForeground(new Color(220, 53, 69)); // Rojo (No acertó)
                } else {
                    lbl.setForeground(Color.GRAY); // Pendiente
                }
                return lbl;
            }
        });

        // Ajustar anchos
        tablaAuditoria.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Fecha y Hora (Expande)
        tablaAuditoria.getColumnModel().getColumn(0).setPreferredWidth(220); 
        tablaAuditoria.getColumnModel().getColumn(0).setMinWidth(150);
        
        // Partido (Expande)
        tablaAuditoria.getColumnModel().getColumn(1).setPreferredWidth(160); 
        tablaAuditoria.getColumnModel().getColumn(1).setMinWidth(120);
        
        // Métricas (Fijas)
        tablaAuditoria.getColumnModel().getColumn(2).setPreferredWidth(100);  // Estado
        tablaAuditoria.getColumnModel().getColumn(2).setMaxWidth(120);
        tablaAuditoria.getColumnModel().getColumn(3).setPreferredWidth(100);  // Prediccion
        tablaAuditoria.getColumnModel().getColumn(3).setMaxWidth(120);
        tablaAuditoria.getColumnModel().getColumn(4).setPreferredWidth(100);  // Resultado Real
        tablaAuditoria.getColumnModel().getColumn(4).setMaxWidth(120);
        tablaAuditoria.getColumnModel().getColumn(5).setPreferredWidth(100);  // Puntos
        tablaAuditoria.getColumnModel().getColumn(5).setMaxWidth(120);
        
        if (usuarioFijo != null) {
            tablaAuditoria.getColumnModel().getColumn(6).setPreferredWidth(90);
            tablaAuditoria.getColumnModel().getColumn(6).setMaxWidth(90);
            
            TableActionCellEditor.TableActionListener actionEvent = new TableActionCellEditor.TableActionListener() {
                @Override
                public void onEdit(int row) {
                    editarPronostico(row);
                }

                @Override
                public void onDelete(int row) {
                    eliminarPronostico(row);
                }
            };
            tablaAuditoria.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
            tablaAuditoria.getColumnModel().getColumn(6).setCellEditor(new TableActionCellEditor(actionEvent));
        }

        JScrollPane scroll = new JScrollPane(tablaAuditoria);
        scroll.getViewport().setBackground(COLOR_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        scroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

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

    private void editarPronostico(int row) {
        if (row < 0 || row >= historialActual.size()) return;
        int modelRow = tablaAuditoria.convertRowIndexToModel(row);
        Pronostico p = historialActual.get(modelRow);
        
        if (!validarTiempoPermitido(p)) {
            JOptionPane.showMessageDialog(this, "El tiempo para modificar el pronóstico de este partido ha expirado.", "Bloqueado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalPronostico modal = new ModalPronostico(parent, usuarioFijo.getIdUsuario(), p);
        modal.setVisible(true);

        if (modal.isConfirmado()) {
            Pronostico actualizado = modal.getPronostico();
            new Thread(() -> {
                boolean exito = pronosticoDAO.guardarOActualizar(actualizado);
                SwingUtilities.invokeLater(() -> {
                    if (exito) {
                        JOptionPane.showMessageDialog(this, "Pronóstico actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarDatosUsuarioFijo();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start();
        }
    }

    private void eliminarPronostico(int row) {
        if (row < 0 || row >= historialActual.size()) return;
        int modelRow = tablaAuditoria.convertRowIndexToModel(row);
        Pronostico p = historialActual.get(modelRow);

        if (!validarTiempoPermitido(p)) {
            JOptionPane.showMessageDialog(this, "El tiempo para modificar el pronóstico de este partido ha expirado.", "Bloqueado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de eliminar tu pronóstico para este partido?\nEsta acción no se puede deshacer.",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                String error = pronosticoDAO.eliminar(p.getIdPronostico());
                if (error == null) {
                    JOptionPane.showMessageDialog(this, "Pronóstico eliminado correctamente.");
                    cargarDatosUsuarioFijo();
                } else {
                    JOptionPane.showMessageDialog(this, error, "Error de eliminación", JOptionPane.ERROR_MESSAGE);
                }
            }
    }

    private boolean validarTiempoPermitido(Pronostico p) {
        if (p.getFechaPartido() == null || p.getHoraPartido() == null) return false;
        try {
            String horaStr = p.getHoraPartido();
            if (horaStr.length() == 5) horaStr += ":00";
            String fechaHoraStr = p.getFechaPartido() + " " + horaStr;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime fechaPartido = LocalDateTime.parse(fechaHoraStr, formatter);
            
            long minutosRestantes = ChronoUnit.MINUTES.between(LocalDateTime.now(), fechaPartido);
            return minutosRestantes > 10;
        } catch (Exception e) {
            return false;
        }
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

    private void cargarComboboxUsuarios() {
        new Thread(() -> {
            List<Usuario> usuarios = usuarioDAO.listar();
            SwingUtilities.invokeLater(() -> {
                cbUsuarios.removeAllItems();
                
                // Opción "Todos" para el ranking
                Usuario todos = new Usuario();
                todos.setIdUsuario(-1);
                todos.setNombreUsuario("Todos (Ranking)");
                cbUsuarios.addItem(todos);
                
                for (Usuario u : usuarios) {
                    if ("USER".equals(u.getRolUsuario())) {
                        cbUsuarios.addItem(u);
                    }
                }
            });
        }).start();
    }

    private void cargarDatosUsuarioFijo() {
        if (usuarioFijo == null) return;
        new Thread(() -> {
            int puntos = puntoDAO.obtenerPuntosUsuario(usuarioFijo.getIdUsuario());
            List<Pronostico> historial = pronosticoDAO.listarPorUsuario(usuarioFijo.getIdUsuario());
            actualizarTabla(puntos, historial);
        }).start();
    }

    private void cargarDatosUsuario() {
        Usuario seleccionado = (Usuario) cbUsuarios.getSelectedItem();
        if (seleccionado == null) return;

        if (seleccionado.getIdUsuario() == -1) {
            if (cardLayoutTablas != null && panelTablas != null) {
                cardLayoutTablas.show(panelTablas, "Ranking");
            }
            lblPuntosTotales.setVisible(false);
            return;
        }

        if (cardLayoutTablas != null && panelTablas != null) {
            cardLayoutTablas.show(panelTablas, "Pronosticos");
        }
        lblPuntosTotales.setVisible(true);

        new Thread(() -> {
            int puntos = puntoDAO.obtenerPuntosUsuario(seleccionado.getIdUsuario());
            List<Pronostico> historial = pronosticoDAO.listarPorUsuario(seleccionado.getIdUsuario());
            actualizarTabla(puntos, historial);
        }).start();
    }

    private void actualizarTabla(int puntosDB, List<Pronostico> historial) {
        this.historialActual = historial;
        SwingUtilities.invokeLater(() -> {
            modeloTabla.setRowCount(0);
            
            int totalCalculado = 0;

            for (Pronostico p : historial) {
                String partidoStr = p.getNombreEquipoA() + " vs " + p.getNombreEquipoB();
                String prediccionStr = p.getGolesEquipoA() + " - " + p.getGolesEquipoB();
                String estadoStr = (p.getEstadoPartido() != null) ? p.getEstadoPartido() : "Programado";
                String resultadoRealStr = "-";
                String puntosGanadosStr = "-";

                if ("Finalizado".equalsIgnoreCase(estadoStr)) {
                    resultadoRealStr = p.getGolesRealesEquipoA() + " - " + p.getGolesRealesEquipoB();

                    int pronA = p.getGolesEquipoA();
                    int pronB = p.getGolesEquipoB();
                    int realA = p.getGolesRealesEquipoA();
                    int realB = p.getGolesRealesEquipoB();

                    if (pronA == realA && pronB == realB) {
                        puntosGanadosStr = "3 pts";
                        totalCalculado += 3;
                    } else if ((pronA > pronB && realA > realB) || 
                               (pronA < pronB && realA < realB) || 
                               (pronA == pronB && realA == realB)) {
                        puntosGanadosStr = "1 pts";
                        totalCalculado += 1;
                    } else {
                        puntosGanadosStr = "0 pts";
                    }
                } else if ("En Juego".equalsIgnoreCase(estadoStr)) {
                    resultadoRealStr = p.getGolesRealesEquipoA() + " - " + p.getGolesRealesEquipoB();
                    puntosGanadosStr = "-";
                } else {
                    resultadoRealStr = "-";
                    puntosGanadosStr = "-";
                }

                String horaStr = p.getHoraPartido();
                if (horaStr != null && horaStr.length() >= 5) {
                    horaStr = horaStr.substring(0, 5); // Remueve los segundos, ej: 15:00
                } else {
                    horaStr = "";
                }
                String fh = p.getFechaPartido() + " " + horaStr;

                if (usuarioFijo != null) {
                    modeloTabla.addRow(new Object[]{fh, partidoStr, estadoStr, prediccionStr, resultadoRealStr, puntosGanadosStr, ""});
                } else {
                    modeloTabla.addRow(new Object[]{fh, partidoStr, estadoStr, prediccionStr, resultadoRealStr, puntosGanadosStr});
                }
            }
            
            lblPuntosTotales.setText("Puntos Totales: " + totalCalculado + " pts");
        });
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