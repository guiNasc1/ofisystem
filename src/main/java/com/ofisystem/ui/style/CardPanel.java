package com.ofisystem.ui.style;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CardPanel extends JPanel {

    private int raio;
    private float opacidade;

    public CardPanel(int raio, float opacidade) {
        this.raio = raio;
        this.opacidade = opacidade;
        setOpaque(false);
        setLayout(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradiente = new GradientPaint(
                0, 0,                          // canto superior esquerdo
                new Color(45, 59, 80),
                getWidth(), getHeight(),       // canto inferior direito
                new Color(24, 32, 43
                ));

        // Fundo semitransparente arredondado
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, opacidade));
        g2.setPaint(gradiente);
        g2.fill(new RoundRectangle2D.Double( 0, 0, getWidth(), getHeight(), raio, raio));
        g2.dispose();
        super.paintComponent(g);
    }

}
