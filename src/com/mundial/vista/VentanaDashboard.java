package com.mundial.vista;

import com.mundial.dao.PuntoDAO;
import com.mundial.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VentanaDashboard extends JFrame {

    private Usuario usuarioActual;
    private static final Color COLOR_FONDO          = new Color(10, 14, 26);
    private static final Color COLOR_PANEL          = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO         = new Color(212, 175, 55);
    private static final Color COLOR_TEXTO_SUB      = new Color(180, 190, 210);

    private JPanel panelCentral;
    private CardLayout cardLayout;
    
    private JButton botonSeleccionado = null;
    private java.util.List<JButton> botonesMenu = new java.util.ArrayList<>();

    public VentanaDashboard(Usuario usuario) {
        this.usuarioActual = usuario;

        setTitle("Dashboard - Mundial 2026");
        setSize(1100, 700);
        setMinimumSize(new Dimension(1200, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        add(crearHeader(), BorderLayout.NORTH);
        add(crearSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.setBorder(new EmptyBorder(15, 15, 15, 15)); // Margen interior general

        // Paneles comunes
        panelCentral.add(crearPanelBienvenida(), "Bienvenida");
        panelCentral.add(new VentanaPosiciones(), "Posiciones");

        if ("ADMIN".equalsIgnoreCase(usuario.getRolUsuario())) {
            panelCentral.add(new VentanaEstadios(), "Estadios");
            panelCentral.add(new VentanaGrupos(), "Grupos");
            panelCentral.add(new VentanaFases(), "Fases");
            panelCentral.add(new VentanaEquipos(), "Equipos");
            panelCentral.add(new VentanaPartidos(), "Partidos");
            panelCentral.add(new VentanaUsuarios(), "Usuarios");
            panelCentral.add(new VentanaAuditoriaPronosticos(), "Auditoria");
        } else {
            panelCentral.add(new VentanaAuditoriaPronosticos(usuarioActual), "Pronosticos");
            panelCentral.add(new VentanaRanking(usuarioActual), "Ranking");
            panelCentral.add(new VentanaReglas(), "Reglas");
        }

        add(panelCentral, BorderLayout.CENTER);
        
        cardLayout.show(panelCentral, "Bienvenida");
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PANEL);
        // Header con margen simetrico: 15 arriba/abajo, 25 izquierda/derecha
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel lblBienvenida = new JLabel("BIENVENIDO: " + usuarioActual.getNombreUsuario().toUpperCase());
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBienvenida.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(lblBienvenida);



        header.add(leftPanel, BorderLayout.WEST);

        // Linea inferior dorada
        JPanel bordeInferior = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(COLOR_ACENTO);
                g.fillRect(0, 0, getWidth(), 2);
            }
        };
        bordeInferior.setPreferredSize(new Dimension(0, 2));
        header.add(bordeInferior, BorderLayout.SOUTH);

        return header;
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(COLOR_PANEL);
        sidebar.setPreferredSize(new Dimension(240, 0));
        // Margen alineado simétricamente con el borde izquierdo del header (25px)
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 20));

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBackground(COLOR_PANEL);

        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMenu.setForeground(COLOR_TEXTO_SUB);
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda para emparejar con bienvenida
        panelBotones.add(lblMenu);
        panelBotones.add(Box.createVerticalStrut(20));

        if ("ADMIN".equalsIgnoreCase(usuarioActual.getRolUsuario())) {
            panelBotones.add(crearBotonMenu("Estadios", "Estadios"));
            panelBotones.add(crearBotonMenu("Fases", "Fases"));
            panelBotones.add(crearBotonMenu("Grupos", "Grupos"));
            panelBotones.add(crearBotonMenu("Equipos", "Equipos"));
            panelBotones.add(crearBotonMenu("Partidos", "Partidos"));
            panelBotones.add(crearBotonMenu("Usuarios y Roles", "Usuarios"));
            panelBotones.add(crearBotonMenu("Auditoría Jugadores", "Auditoria"));
            panelBotones.add(crearBotonMenu("Tabla Posiciones", "Posiciones"));
        } else {
            panelBotones.add(crearBotonMenu("Mis Pronósticos", "Pronosticos"));
            panelBotones.add(crearBotonMenu("Tabla Posiciones", "Posiciones"));
            panelBotones.add(crearBotonMenu("Ranking Global", "Ranking"));
            
            panelBotones.add(Box.createVerticalStrut(20));
            
            JButton btnReglas = crearBotonMenu("Ver Reglas", "Reglas");
            panelBotones.add(btnReglas);
        }

        sidebar.add(panelBotones, BorderLayout.CENTER);

        // Footer Cerrar Sesion alineado a la izquierda
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.setBackground(COLOR_PANEL);
        
        JButton btnCerrarSesion = new JButton(" Cerrar Sesión") {
            @Override protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    g.setColor(new Color(220, 53, 69, 50)); // Red hover
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                super.paintComponent(g);
            }
        };
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnCerrarSesion.setForeground(new Color(255, 120, 130)); // Text red/pink tint
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setHorizontalAlignment(SwingConstants.LEFT);
        btnCerrarSesion.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCerrarSesion.setMaximumSize(new Dimension(200, 42));
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.addActionListener(e -> {
            this.dispose();
            new VentanaLogin().setVisible(true);
        });
        
        footer.add(btnCerrarSesion);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton crearBotonMenu(String texto, String panelName) {
        JButton btn = new JButton(" " + texto) {
            @Override protected void paintComponent(Graphics g) {
                if (this == botonSeleccionado) {
                    g.setColor(new Color(212, 175, 55, 30)); // Fondo dorado suave translúcido
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(255, 255, 255, 15));
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda para alinear con el titulo
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            cardLayout.show(panelCentral, panelName);
            botonSeleccionado = btn;
            for (JButton b : botonesMenu) {
                if (b == botonSeleccionado) {
                    b.setForeground(COLOR_ACENTO);
                    b.setFont(new Font("Segoe UI", Font.BOLD, 15));
                } else {
                    b.setForeground(Color.WHITE);
                    b.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                }
                b.repaint();
            }
        });
        
        botonesMenu.add(btn);
        return btn;
    }

    private JPanel crearPanelBienvenida() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COLOR_FONDO);
        String titulo = "ADMINISTRACIÓN".equalsIgnoreCase(usuarioActual.getRolUsuario()) ? "MUNDIAL 2026" : "BIENVENIDO AL SIMULADOR";
        JLabel lbl = new JLabel("<html><center><font size='6' color='#D4AF37'>" + titulo + "</font><br><br><font color='white'>Selecciona una opción del menú lateral.</font></center></html>");
        p.add(lbl);
        return p;
    }


}
