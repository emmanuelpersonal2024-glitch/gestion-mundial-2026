package com.mundial.vista;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import com.mundial.dao.UsuarioDAO;
import com.mundial.modelo.Usuario;

public class VentanaLogin extends JFrame {

    private static final Color COLOR_FONDO          = new Color(10, 14, 26);
    private static final Color COLOR_PANEL          = new Color(20, 26, 46);
    private static final Color COLOR_ACENTO         = new Color(212, 175, 55);   // Dorado
    private static final Color COLOR_ACENTO2        = new Color(0, 168, 107);    // Verde esmeralda
    private static final Color COLOR_TEXTO_TITULO   = Color.WHITE;
    private static final Color COLOR_TEXTO_SUB      = new Color(180, 190, 210);
    private static final Color COLOR_BORDE_CARD     = new Color(212, 175, 55, 80);
    private static final Color COLOR_ERROR          = new Color(220, 80, 80);

    private JTextField  txtLoginUsuario;
    private JPasswordField txtLoginPassword;
    private JLabel      lblLoginError;

    private JTextField    txtRegUsuario;
    private JPasswordField txtRegPassword;
    private JPasswordField txtRegPasswordConf;
    private JLabel        lblRegError;
    private JLabel        lblRegExito;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private JPanel panelLogin;
    private JPanel panelRegistro;
    private JButton btnTabLogin;
    private JButton btnTabRegistro;

    public VentanaLogin() {
        setTitle("Mundial 2026 – Iniciar Sesión");
        setSize(480, 720);
        setMinimumSize(new Dimension(420, 660));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(new PanelFondo());
        setLayout(new BorderLayout());

        add(crearHeader(),  BorderLayout.NORTH);
        add(crearCuerpo(),  BorderLayout.CENTER);
        add(crearFooter(),  BorderLayout.SOUTH);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 26, 46),
                        getWidth(), 0, new Color(10, 14, 26));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(COLOR_ACENTO);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(40, getHeight() - 1, getWidth() - 40, getHeight() - 1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(14, 40, 12, 40));

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel lblIcono = new JLabel("⚽", SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("MUNDIAL 2026", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_ACENTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sistema de Pronósticos", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXTO_SUB);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        centro.add(lblIcono);
        centro.add(Box.createVerticalStrut(4));
        centro.add(lblTitulo);
        centro.add(Box.createVerticalStrut(2));
        centro.add(lblSub);
        header.add(centro, BorderLayout.CENTER);

        return header;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 0));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(12, 36, 8, 36));

        JPanel panelTabs = new JPanel(new GridLayout(1, 2, 6, 0));
        panelTabs.setOpaque(false);
        panelTabs.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        btnTabLogin    = crearTabButton("Iniciar Sesión",   true);
        btnTabRegistro = crearTabButton("Registrarse",      false);

        btnTabLogin.addActionListener(e -> mostrarTab(true));
        btnTabRegistro.addActionListener(e -> mostrarTab(false));

        panelTabs.add(btnTabLogin);
        panelTabs.add(btnTabRegistro);
        cuerpo.add(panelTabs, BorderLayout.NORTH);

        JPanel contenedor = new JPanel(new CardLayout());
        contenedor.setOpaque(false);

        panelLogin    = crearPanelLogin();
        panelRegistro = crearPanelRegistro();

        contenedor.add(panelLogin,    "LOGIN");
        contenedor.add(panelRegistro, "REGISTRO");
        cuerpo.add(contenedor, BorderLayout.CENTER);

        cuerpo.putClientProperty("contenedor", contenedor);
        return cuerpo;
    }

    private void mostrarTab(boolean esLogin) {
        btnTabLogin.putClientProperty("activo", esLogin);
        btnTabRegistro.putClientProperty("activo", !esLogin);
        btnTabLogin.repaint();
        btnTabRegistro.repaint();

        Container cuerpo = (Container) getContentPane().getComponent(1);
        for (Component c : cuerpo.getComponents()) {
            if (c instanceof JPanel p && p.getLayout() instanceof CardLayout cl) {
                cl.show(p, esLogin ? "LOGIN" : "REGISTRO");
                break;
            }
        }
        limpiarMensajes();
    }

    private JPanel crearPanelLogin() {
        JPanel card = crearCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE_CARD, 1, 16),
                BorderFactory.createEmptyBorder(22, 28, 22, 28)));

        JLabel lblCardTitulo = new JLabel("Bienvenido de vuelta");
        lblCardTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblCardTitulo.setForeground(COLOR_TEXTO_TITULO);
        lblCardTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCardSub = new JLabel("Ingresa tus credenciales para continuar");
        lblCardSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCardSub.setForeground(COLOR_TEXTO_SUB);
        lblCardSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblCardTitulo);
        card.add(Box.createVerticalStrut(3));
        card.add(lblCardSub);
        card.add(Box.createVerticalStrut(16));

        card.add(crearEtiqueta("Usuario"));
        card.add(Box.createVerticalStrut(5));
        txtLoginUsuario = crearCampoTexto("Tu nombre de usuario");
        card.add(txtLoginUsuario);
        card.add(Box.createVerticalStrut(12));

        card.add(crearEtiqueta("Contraseña"));
        card.add(Box.createVerticalStrut(5));
        txtLoginPassword = crearCampoPassword("Tu contraseña");
        card.add(txtLoginPassword);
        card.add(Box.createVerticalStrut(5));

        lblLoginError = new JLabel(" ");
        lblLoginError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoginError.setForeground(COLOR_ERROR);
        lblLoginError.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblLoginError);
        card.add(Box.createVerticalStrut(14));

        JButton btnIngresar = crearBotonPrimario("Ingresar");
        btnIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnIngresar.addActionListener(this::accionLogin);
        card.add(btnIngresar);

        ActionListener enterLogin = this::accionLogin;
        txtLoginUsuario.addActionListener(enterLogin);
        txtLoginPassword.addActionListener(enterLogin);

        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel crearPanelRegistro() {
        JPanel card = crearCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDE_CARD, 1, 16),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)));

        JLabel lblCardTitulo = new JLabel("Crear cuenta nueva");
        lblCardTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblCardTitulo.setForeground(COLOR_TEXTO_TITULO);
        lblCardTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCardSub = new JLabel("Regístrate para participar en los pronósticos");
        lblCardSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCardSub.setForeground(COLOR_TEXTO_SUB);
        lblCardSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblCardTitulo);
        card.add(Box.createVerticalStrut(3));
        card.add(lblCardSub);
        card.add(Box.createVerticalStrut(14));

        card.add(crearEtiqueta("Nombre de usuario"));
        card.add(Box.createVerticalStrut(4));
        txtRegUsuario = crearCampoTexto("Elige un nombre único");
        card.add(txtRegUsuario);
        card.add(Box.createVerticalStrut(10));

        card.add(crearEtiqueta("Contraseña"));
        card.add(Box.createVerticalStrut(4));
        txtRegPassword = crearCampoPassword("Mínimo 4 caracteres");
        card.add(txtRegPassword);
        card.add(Box.createVerticalStrut(10));

        card.add(crearEtiqueta("Confirmar contraseña"));
        card.add(Box.createVerticalStrut(4));
        txtRegPasswordConf = crearCampoPassword("Repite tu contraseña");
        card.add(txtRegPasswordConf);
        card.add(Box.createVerticalStrut(5));

        lblRegError = new JLabel(" ");
        lblRegError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRegError.setForeground(COLOR_ERROR);
        lblRegError.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblRegError);

        lblRegExito = new JLabel(" ");
        lblRegExito.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRegExito.setForeground(COLOR_ACENTO2);
        lblRegExito.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblRegExito);
        card.add(Box.createVerticalStrut(10));

        JButton btnRegistrar = crearBotonSecundario("Crear cuenta");
        btnRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnRegistrar.addActionListener(this::accionRegistrar);
        card.add(btnRegistrar);

        card.add(Box.createVerticalGlue());
        return card;
    }

    private void accionLogin(ActionEvent e) {
        String usuario  = txtLoginUsuario.getText().trim();
        String password = new String(txtLoginPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            lblLoginError.setText("Completa todos los campos.");
            return;
        }

        Component src = (Component) e.getSource();
        src.setEnabled(false);
        lblLoginError.setText("Verificando...");
        lblLoginError.setForeground(COLOR_TEXTO_SUB);

        new Thread(() -> {
            try {
                Usuario u = usuarioDAO.autenticar(usuario, password);

                SwingUtilities.invokeLater(() -> {
                    src.setEnabled(true);
                    if (u == null) {
                        lblLoginError.setText("Usuario o contraseña incorrectos.");
                        lblLoginError.setForeground(COLOR_ERROR);
                        txtLoginPassword.setText("");
                        txtLoginPassword.requestFocus();
                    } else {
                        redirigirSegunRol(u);
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    src.setEnabled(true);
                    lblLoginError.setText("Error de conexión: " + ex.getMessage());
                    lblLoginError.setForeground(COLOR_ERROR);
                });
            }
        }).start();
    }

    private void accionRegistrar(ActionEvent e) {
        String usuario   = txtRegUsuario.getText().trim();
        String pass      = new String(txtRegPassword.getPassword());
        String passConf  = new String(txtRegPasswordConf.getPassword());

        lblRegExito.setText(" ");
        lblRegError.setText(" ");

        if (usuario.isEmpty() || pass.isEmpty() || passConf.isEmpty()) {
            lblRegError.setText("Completa todos los campos.");
            return;
        }
        if (usuario.length() < 3) {
            lblRegError.setText("El usuario debe tener al menos 3 caracteres.");
            return;
        }
        if (pass.length() < 4) {
            lblRegError.setText("La contraseña debe tener al menos 4 caracteres.");
            return;
        }
        if (!pass.equals(passConf)) {
            lblRegError.setText("Las contraseñas no coinciden.");
            txtRegPasswordConf.setText("");
            txtRegPasswordConf.requestFocus();
            return;
        }

        Component src = (Component) e.getSource();
        src.setEnabled(false);

        new Thread(() -> {
            try {
                // Register with default role "USER"
                Usuario u = new Usuario(0, usuario, pass, "USER");
                boolean insertado = usuarioDAO.insertar(u);
                SwingUtilities.invokeLater(() -> {
                    src.setEnabled(true);
                    if (insertado) {
                        lblRegExito.setText("¡Cuenta creada! Ya puedes iniciar sesión.");
                        txtRegUsuario.setText("");
                        txtRegPassword.setText("");
                        txtRegPasswordConf.setText("");
                        new Timer(1500, ev -> mostrarTab(true)).start();
                    } else {
                        lblRegError.setText("Error al registrar, usuario duplicado o fallo db.");
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    src.setEnabled(true);
                    lblRegError.setText("Error al registrar: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void redirigirSegunRol(Usuario u) {
        dispose();
        new VentanaDashboard(u).setVisible(true);
    }

    private void limpiarMensajes() {
        if (lblLoginError  != null) { lblLoginError.setText(" ");  lblLoginError.setForeground(COLOR_ERROR); }
        if (lblRegError    != null) { lblRegError.setText(" "); }
        if (lblRegExito    != null) { lblRegExito.setText(" "); }
    }

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_ACENTO);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(40, 0, getWidth() - 40, 0);
                g2.dispose();
            }
        };
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 12, 0));
        JLabel lbl = new JLabel("USA · CANADA · MEXICO  |  FIFA World Cup 2026™");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(COLOR_TEXTO_SUB);
        footer.add(lbl);
        return footer;
    }

    private JPanel crearCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private JButton crearTabButton(String texto, boolean activo) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean estaActivo = Boolean.TRUE.equals(getClientProperty("activo"));
                if (estaActivo) {
                    g2.setColor(COLOR_ACENTO);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else {
                    g2.setColor(getModel().isRollover()
                            ? new Color(212, 175, 55, 30) : new Color(20, 26, 46));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(COLOR_BORDE_CARD);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("activo", activo);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(activo ? COLOR_FONDO : Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 38));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });
        btn.addPropertyChangeListener("activo", evt ->
            btn.setForeground(Boolean.TRUE.equals(evt.getNewValue()) ? COLOR_FONDO : Color.WHITE));
        return btn;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO_SUB);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_FONDO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isFocusOwner() ? COLOR_ACENTO : COLOR_BORDE_CARD);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 2f : 1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_ACENTO);
        tf.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setToolTipText(placeholder);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { tf.repaint(); }
            @Override public void focusLost(FocusEvent e)   { tf.repaint(); }
        });
        return tf;
    }

    private JPasswordField crearCampoPassword(String placeholder) {
        JPasswordField pf = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_FONDO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isFocusOwner() ? COLOR_ACENTO : COLOR_BORDE_CARD);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 2f : 1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pf.setOpaque(false);
        pf.setForeground(Color.WHITE);
        pf.setCaretColor(COLOR_ACENTO);
        pf.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setEchoChar('●');
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pf.setToolTipText(placeholder);
        pf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { pf.repaint(); }
            @Override public void focusLost(FocusEvent e)   { pf.repaint(); }
        });
        return pf;
    }

    private JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        getModel().isRollover() ? COLOR_ACENTO.darker() : COLOR_ACENTO,
                        getWidth(), 0,
                        getModel().isRollover() ? new Color(180, 140, 30) : new Color(200, 160, 40));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(new Color(10, 14, 26));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        getModel().isRollover() ? COLOR_ACENTO2.darker() : COLOR_ACENTO2,
                        getWidth(), 0,
                        getModel().isRollover() ? new Color(0, 130, 80) : new Color(0, 148, 95));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });
        return btn;
    }

    static class PanelFondo extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            GradientPaint gp = new GradientPaint(0, 0, COLOR_FONDO,
                    0, getHeight(), new Color(15, 20, 38));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(212, 175, 55, 18));
            g2.fillOval(-80, -80, 320, 320);
            g2.setColor(new Color(0, 168, 107, 12));
            g2.fillOval(getWidth() - 200, getHeight() - 200, 340, 340);
            g2.dispose();
        }
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
            g2.drawRoundRect(x + 1, y + 1, w - 2, h - 2, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
    }
}
