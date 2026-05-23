package com.ofisystem.ui.style;

import javax.swing.*;
import java.awt.*;

public class PlaceHolderTextField extends JTextField {

    private String placeholder;

    public PlaceHolderTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setFont(new Font("Segoes UI", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        setBackground(new Color(255, 255, 255, 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundo do campo
        g2.setColor(new Color(255, 255, 255, 25));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Borda
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        g2.dispose();
        super.paintComponent(g);

        // Placeholder
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setColor(new Color(255, 255, 255, 120));
            g3.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            FontMetrics fm = g3.getFontMetrics();
            g3.drawString(placeholder, 12,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g3.dispose();
        }
    }

}
