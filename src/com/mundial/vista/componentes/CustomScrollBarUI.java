package com.mundial.vista.componentes;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomScrollBarUI extends BasicScrollBarUI {
    
    private final int width = 8;
    private final Color customTrackColor = new Color(20, 26, 46); // Igual que el panel
    private final Color customThumbColor = new Color(60, 70, 90); // Gris azulado oscuro
    private final Color thumbHoverColor = new Color(80, 95, 120);

    @Override
    protected void configureScrollBarColors() {
        this.trackColor = customTrackColor;
        this.thumbColor = customThumbColor;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isDragging || isThumbRollover()) {
            g2.setColor(thumbHoverColor);
        } else {
            g2.setColor(thumbColor);
        }

        int arc = 8;
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, arc, arc);
        g2.dispose();
    }
    
    @Override
    public Dimension getPreferredSize(JComponent c) {
        return (scrollbar.getOrientation() == JScrollBar.VERTICAL) 
            ? new Dimension(width, super.getPreferredSize(c).height)
            : new Dimension(super.getPreferredSize(c).width, width);
    }
}
