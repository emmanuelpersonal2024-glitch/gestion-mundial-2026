package com.mundial.vista;

import com.mundial.dao.GrupoDAO;
import com.mundial.modelo.Grupo;
import com.mundial.vista.componentes.ModalGrupo;
import com.mundial.vista.componentes.TableActionCellEditor;
import com.mundial.vista.componentes.TableActionCellRender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class VentanaGrupos extends JPanel {

    private JTable tablaGrupos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private GrupoDAO dao;
    
    private static final Color COLOR_FONDO  = new Color(10, 14, 26);
    private static final Color COLOR_PANEL  = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO = new Color(212, 175, 55);

    public VentanaGrupos() {
        dao = new GrupoDAO();
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
        
        JLabel lblTitulo = new JLabel("Gestión de Grupos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Administra los grupos (A, B, C...) correspondientes a la fase de grupos del Mundial.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(180, 190, 210));
        
        panelTextos.add(lblTitulo);
        panelTextos.add(lblSub);
        
        JButton btnAgregar = new JButton(" Añadir Grupo ");
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
            tablaGrupos.requestFocus(); 
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

        tablaGrupos = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaGrupos.setRowSorter(sorter);
        
        // Estilos premium para la tabla
        tablaGrupos.setRowHeight(40);
        tablaGrupos.setBackground(COLOR_PANEL);
        tablaGrupos.setForeground(Color.WHITE);
        tablaGrupos.setShowVerticalLines(false);
        tablaGrupos.setShowHorizontalLines(true);
        tablaGrupos.setGridColor(new Color(40, 50, 70));
        tablaGrupos.setSelectionBackground(new Color(212, 175, 55, 50));
        tablaGrupos.setSelectionForeground(Color.WHITE);
        tablaGrupos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tablaGrupos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaGrupos.getTableHeader().setBackground(new Color(15, 20, 35));
        tablaGrupos.getTableHeader().setForeground(COLOR_ACENTO);
        tablaGrupos.getTableHeader().setPreferredSize(new Dimension(100, 40));
        tablaGrupos.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACENTO));
        
        // Configurar botones de accion
        tablaGrupos.getColumnModel().getColumn(2).setCellRenderer(new TableActionCellRender());
        tablaGrupos.getColumnModel().getColumn(2).setCellEditor(new TableActionCellEditor(new TableActionCellEditor.TableActionListener() {
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
        tablaGrupos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaGrupos.getColumnModel().getColumn(0).setMaxWidth(80);
        tablaGrupos.getColumnModel().getColumn(2).setPreferredWidth(90); // Acciones
        tablaGrupos.getColumnModel().getColumn(2).setMaxWidth(90);

        JScrollPane scroll = new JScrollPane(tablaGrupos);
        scroll.getViewport().setBackground(COLOR_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        // Asignar el ScrollBarUI personalizado
        scroll.getVerticalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new com.mundial.vista.componentes.CustomScrollBarUI());
        
        return scroll;
    }
    
    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<Grupo> lista = dao.listar();
        for (Grupo g : lista) {
            modeloTabla.addRow(new Object[]{g.getIdGrupo(), g.getNombreGrupo(), ""});
        }
    }

    private void mostrarModalAgregar() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalGrupo modal = new ModalGrupo(parent, "Agregar Nuevo Grupo", null);
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Grupo nuevo = modal.getGrupo();
            if (dao.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Grupo agregado correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarModalEditar(int row) {
        int id = Integer.parseInt(tablaGrupos.getValueAt(row, 0).toString());
        String nombre = tablaGrupos.getValueAt(row, 1).toString();
        
        Grupo actual = new Grupo(id, nombre);
        
        Window parent = SwingUtilities.getWindowAncestor(this);
        ModalGrupo modal = new ModalGrupo(parent, "Editar Grupo", actual);
        modal.setVisible(true);
        
        if (modal.isConfirmado()) {
            Grupo editado = modal.getGrupo();
            if (dao.actualizar(editado)) {
                JOptionPane.showMessageDialog(this, "Grupo modificado correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarEliminar(int row) {
        int id = Integer.parseInt(tablaGrupos.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de eliminar este grupo (ID: " + id + ")?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String error = dao.eliminar(id);
            if (error == null) {
                JOptionPane.showMessageDialog(this, "Grupo eliminado correctamente.");
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, error, "Error de eliminación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}