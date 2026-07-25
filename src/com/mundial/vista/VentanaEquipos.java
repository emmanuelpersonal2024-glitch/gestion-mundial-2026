package com.mundial.vista;

import com.mundial.dao.EquipoDAO;
import com.mundial.modelo.Equipo;
import com.mundial.vista.componentes.ModalEquipo;
import com.mundial.vista.componentes.TableActionCellEditor;
import com.mundial.vista.componentes.TableActionCellRender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class VentanaEquipos extends JPanel {

    private JTable tablaEquipos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private EquipoDAO dao;
    private JComboBox<String> cbFiltroGrupo;
    
    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public VentanaEquipos() {
        dao = new EquipoDAO();
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(crearHeader(), BorderLayout.NORTH);
        
        cbFiltroGrupo = crearComboBoxPremium();
        cbFiltroGrupo.addItem("Seleccione Grupo");

        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.setOpaque(false);
        panelCentro.add(crearBarraBusqueda(), BorderLayout.NORTH);
        panelCentro.add(crearTabla(), BorderLayout.CENTER);
        
        add(panelCentro, BorderLayout.CENTER);

        cargarDatosCombobox();
        cargarDatosTabla();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                cargarDatosCombobox();
                cargarDatosTabla();
            }
        });
    }

    private void cargarDatosCombobox() {
        String seleccionPrevia = null;
        if (cbFiltroGrupo.getSelectedIndex() > 0) {
            seleccionPrevia = (String) cbFiltroGrupo.getSelectedItem();
        }
        
        cbFiltroGrupo.removeAllItems();
        cbFiltroGrupo.addItem("Seleccione Grupo");
        
        com.mundial.dao.GrupoDAO grupoDAO = new com.mundial.dao.GrupoDAO();
        java.util.List<com.mundial.modelo.Grupo> grupos = grupoDAO.listar();
        for (com.mundial.modelo.Grupo g : grupos) {
            cbFiltroGrupo.addItem(g.getNombreGrupo());
        }
        
        if (seleccionPrevia != null) {
            cbFiltroGrupo.setSelectedItem(seleccionPrevia);
        }
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Gestión de Equipos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Administra las selecciones nacionales y asígnales un grupo.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(180, 190, 210));
        
        panelTextos.add(lblTitulo);
        panelTextos.add(lblSub);
        
        JButton btnAgregar = new JButton(" Añadir Equipo ");
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
        
        JTextField txtBuscar = new JTextField("Buscar por ID, País o Grupo...");
        txtBuscar.setBackground(COLOR_FONDO);
        txtBuscar.setForeground(Color.GRAY);
        txtBuscar.setCaretColor(COLOR_ACENTO);
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        Runnable aplicarFiltros = () -> {
            java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<>();
            String text = txtBuscar.getText();
            if (!text.equals("Buscar por ID, País o Grupo...") && text.trim().length() > 0) {
                filtros.add(RowFilter.regexFilter("(?i)" + text));
            }
            if (cbFiltroGrupo.getSelectedIndex() > 0) {
                String grupoSeleccionado = (String) cbFiltroGrupo.getSelectedItem();
                filtros.add(RowFilter.regexFilter("^" + grupoSeleccionado + "$", 2));
            }
            if (filtros.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filtros));
            }
        };

        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().equals("Buscar por ID, País o Grupo...")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setForeground(Color.GRAY);
                    txtBuscar.setText("Buscar por ID, País o Grupo...");
                }
            }
        });
        
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                aplicarFiltros.run();
            }
        });
        
        cbFiltroGrupo.addActionListener(e -> aplicarFiltros.run());
        
        JButton btnLimpiar = new JButton("Limpiar Filtros");
        btnLimpiar.setBackground(new Color(60, 70, 90));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setForeground(Color.GRAY);
            txtBuscar.setText("Buscar por ID, País o Grupo...");
            cbFiltroGrupo.setSelectedIndex(0);
            sorter.setRowFilter(null);
            tablaEquipos.requestFocus(); 
        });
        
        JPanel panelControles = new JPanel(new BorderLayout(15, 0));
        panelControles.setOpaque(false);
        panelControles.add(cbFiltroGrupo, BorderLayout.CENTER);
        panelControles.add(btnLimpiar, BorderLayout.EAST);
        
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(panelControles, BorderLayout.EAST);
        
        return panelBusqueda;
    }
    
    private JScrollPane crearTabla() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "País", "Grupo", "Acciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; 
            }
        };

        tablaEquipos = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaEquipos.setRowSorter(sorter);
        
        // Estilos premium para la tabla
        tablaEquipos.setRowHeight(40);
        tablaEquipos.setBackground(COLOR_PANEL);
        tablaEquipos.setForeground(Color.WHITE);
        tablaEquipos.setShowVerticalLines(false);
        tablaEquipos.setShowHorizontalLines(true);
        tablaEquipos.setGridColor(new Color(40, 50, 70));
        tablaEquipos.setSelectionBackground(new Color(212, 175, 55, 50));
        tablaEquipos.setSelectionForeground(Color.WHITE);
        tablaEquipos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tablaEquipos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaEquipos.getTableHeader().setBackground(new Color(15, 20, 35));
        tablaEquipos.getTableHeader().setForeground(COLOR_ACENTO);
        tablaEquipos.getTableHeader().setPreferredSize(new Dimension(100, 40));
        tablaEquipos.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACENTO));
        
        // Configurar botones de accion
        tablaEquipos.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRender());
        tablaEquipos.getColumnModel().getColumn(3).setCellEditor(new TableActionCellEditor(new TableActionCellEditor.TableActionListener() {
            @Override
            public void onEdit(int row) {
                mostrarModalEditar(row);
            }

            @Override
            public void onDelete(int row) {
                ejecutarEliminar(row);
            }
        }));
        
        // Ajustar anchos
        tablaEquipos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaEquipos.getColumnModel().getColumn(0).setMaxWidth(80);
        tablaEquipos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaEquipos.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaEquipos.getColumnModel().getColumn(3).setMaxWidth(90);

        JScrollPane scroll = new JScrollPane(tablaEquipos);
        scroll.getViewport().setBackground(COLOR_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        // Asignar el ScrollBarUI personalizado
        scroll.getVerticalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        
        return scroll;
    }
    
    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<Equipo> lista = dao.listar();
        for (Equipo e : lista) {
            // Guardamos el objeto completo internamente en alguna columna o usamos el ID para buscarlo.
            // Por simplicidad, extraemos los datos a mostrar.
            modeloTabla.addRow(new Object[]{e.getIdEquipo(), e.getNombreEquipo(), e.getNombreGrupo(), ""});
        }
    }

    private void mostrarModalAgregar() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalEquipo modal = new ModalEquipo(parent, "Agregar Nuevo Equipo", null);
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Equipo nuevo = modal.getEquipo();
            if (dao.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Equipo agregado correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarModalEditar(int row) {
        int id = Integer.parseInt(tablaEquipos.getValueAt(row, 0).toString());
        String nombre = tablaEquipos.getValueAt(row, 1).toString();
        // Obtener el ID del grupo requiere volver a traer el objeto real. 
        // Lo más seguro es buscarlo en la DB, pero podemos iterar la lista actual.
        List<Equipo> todos = dao.listar();
        Equipo actual = null;
        for (Equipo e : todos) {
            if (e.getIdEquipo() == id) {
                actual = e;
                break;
            }
        }
        
        if (actual == null) return;
        
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalEquipo modal = new ModalEquipo(parent, "Editar Equipo", actual);
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Equipo editado = modal.getEquipo();
            if (dao.actualizar(editado)) {
                JOptionPane.showMessageDialog(this, "Equipo modificado correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarEliminar(int row) {
        int id = Integer.parseInt(tablaEquipos.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de eliminar este equipo (ID: " + id + ")?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String error = dao.eliminar(id);
            if (error == null) {
                JOptionPane.showMessageDialog(this, "Equipo eliminado correctamente.");
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
                JButton btn = new JButton("\u25BC"); // Triángulo hacia abajo
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
