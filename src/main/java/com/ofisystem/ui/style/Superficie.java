package com.ofisystem.ui.style;

import javax.swing.*;
import java.awt.*;

public class Superficie extends JPanel {

    protected int elevacao = 3;
    private Insets margem = new Insets(0, 0, 0, 0);
    private int vertical;
    private int horizontal;

    public Superficie(LayoutManager layout) {
        this(layout, 16, 24);
    }

    public Superficie(LayoutManager layout, int vertical, int horizontal) {
        super(layout);
        this.vertical = vertical;
        this.horizontal = horizontal;
        setOpaque(false);
        atualizarBorda();
    }

    public Superficie comMargem(int topo, int lados, int base) {
        margem = new Insets(topo, lados, base, lados);
        atualizarBorda();
        return this;
    }

    private void atualizarBorda() {
        setBorder(BorderFactory.createEmptyBorder(margem.top + vertical, margem.left + horizontal,
                margem.bottom + vertical + elevacao * 2, margem.right + horizontal));
    }

    protected Color corFundo() {
        return Tema.vinil();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.suave(g);
        int x = margem.left + 1;
        int y = margem.top + 1;
        int w = getWidth() - margem.left - margem.right - 2;
        int h = getHeight() - margem.top - margem.bottom - elevacao * 2 - 2;
        Tema.sombra(g2, x, y, w, h, Tema.RAIO, elevacao);
        g2.setColor(corFundo());
        g2.fillRoundRect(x, y, w, h, Tema.RAIO, Tema.RAIO);
        g2.setColor(Tema.borda());
        g2.drawRoundRect(x, y, w - 1, h - 1, Tema.RAIO, Tema.RAIO);
        g2.dispose();
    }
}
