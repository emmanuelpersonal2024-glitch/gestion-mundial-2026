package com.mundial.vista;

import com.mundial.dao.FaseDAO;
import com.mundial.modelo.Fase;
import com.mundial.vista.componentes.ModalFase;
import com.mundial.vista.componentes.TableActionCellEditor;
import com.mundial.vista.componentes.TableActionCellRender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class VentanaFases extends JPanel {

    private JTable tablaFases;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private FaseDAO dao;
    
    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public VentanaFases() {
        dao = new FaseDAO();
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(crearHeader(), BorderLayout.NORTH);
        
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
        
        JLabel lblTitulo = new JLabel("Gestión de Fases");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Administra las etapas del torneo (Fase de Grupos, Octavos de Final, Cuartos...).");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(180, 190, 210));
        
        panelTextos.add(lblTitulo);
        panelTextos.add(lblSub);
        
        JButton btnAgregar = new JButton(" Añadir Fase ");
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
        
        JTextField txtBuscar = new JTextField("Buscar por ID o Nombre...");
        txtBuscar.setBackground(COLOR_FONDO);
        txtBuscar.setForeground(Color.GRAY);
        txtBuscar.setCaretColor(COLOR_ACENTO);
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().equals("Buscar por ID o Nombre...")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setForeground(Color.GRAY);
                    txtBuscar.setText("Buscar por ID o Nombre...");
                }
            }
        });
        
        JButton btnLimpiar = new JButton("Limpiar Filtros");
        btnLimpiar.setBackground(new Color(60, 70, 90));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtBuscar.getText();
                if (text.equals("Buscar por ID o Nombre...")) return;
                
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setForeground(Color.GRAY);
            txtBuscar.setText("Buscar por ID o Nombre...");
            sorter.setRowFilter(null);
            tablaFases.requestFocus(); 
        });
        
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(btnLimpiar, BorderLayout.EAST);
        
        return panelBusqueda;
    }
    
    private JScrollPane crearTabla() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Acciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; 
            }
        };

        tablaFases = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaFases.setRowSorter(sorter);
        
        // Estilos premium para la tabla
        tablaFases.setRowHeight(40);
        tablaFases.setBackground(COLOR_PANEL);
        tablaFases.setForeground(Color.WHITE);
        tablaFases.setShowVerticalLines(false);
        tablaFases.setShowHorizontalLines(true);
        tablaFases.setGridColor(new Color(40, 50, 70));
        tablaFases.setSelectionBackground(new Color(212, 175, 55, 50));
        tablaFases.setSelectionForeground(Color.WHITE);
        tablaFases.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tablaFases.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaFases.getTableHeader().setBackground(new Color(15, 20, 35));
        tablaFases.getTableHeader().setForeground(COLOR_ACENTO);
        tablaFases.getTableHeader().setPreferredSize(new Dimension(100, 40));
        tablaFases.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACENTO));
        
        // Configurar botones de accion
        tablaFases.getColumnModel().getColumn(2).setCellRenderer(new TableActionCellRender());
        tablaFases.getColumnModel().getColumn(2).setCellEditor(new TableActionCellEditor(new TableActionCellEditor.TableActionListener() {
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
        tablaFases.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaFases.getColumnModel().getColumn(0).setMaxWidth(80);
        tablaFases.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaFases.getColumnModel().getColumn(2).setMaxWidth(90);

        JScrollPane scroll = new JScrollPane(tablaFases);
        scroll.getViewport().setBackground(COLOR_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        // Asignar el ScrollBarUI personalizado
        scroll.getVerticalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        
        return scroll;
    }
    
    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<Fase> lista = dao.listar();
        for (Fase f : lista) {
            modeloTabla.addRow(new Object[]{f.getIdFase(), f.getNombreFase(), ""});
        }
    }

    private void mostrarModalAgregar() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalFase modal = new ModalFase(parent, "Agregar Nueva Fase", null);
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Fase nueva = modal.getFase();
            if (dao.insertar(nueva)) {
                JOptionPane.showMessageDialog(this, "Fase agregada correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarModalEditar(int row) {
        int id = Integer.parseInt(tablaFases.getValueAt(row, 0).toString());
        String nombre = tablaFases.getValueAt(row, 1).toString();
        
        Fase actual = new Fase(id, nombre);
        
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalFase modal = new ModalFase(parent, "Editar Fase", actual);
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Fase editada = modal.getFase();
            if (dao.actualizar(editada)) {
                JOptionPane.showMessageDialog(this, "Fase modificada correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarEliminar(int row) {
        int id = Integer.parseInt(tablaFases.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de eliminar esta fase (ID: " + id + ")?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String error = dao.eliminar(id);
            if (error == null) {
                JOptionPane.showMessageDialog(this, "Fase eliminada correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, error, "Error de eliminación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
