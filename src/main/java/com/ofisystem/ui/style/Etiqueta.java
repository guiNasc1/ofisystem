package com.ofisystem.ui.style;

import javax.swing.*;
import java.awt.*;

public class Etiqueta extends JLabel {

    private Tema.Tom tom;

    public Etiqueta(String texto, Tema.Tom tom) {
        super(texto == null ? "" : texto.toUpperCase());
        this.tom = tom;
        setFont(Tema.fonte(Tema.TAM_PEQUENO, Font.BOLD));
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        setOpaque(false);
    }

    public void setDados(String texto, Tema.Tom tom) {
        this.tom = tom;
        setText(texto == null ? "" : texto.toUpperCase());
        repaint();
    }

    private Tema.Tom tomVisivel() {
        return tom == Tema.Tom.NEUTRO ? Tema.Tom.INFO : tom;
    }

    @Override
    public Color getForeground() {
        return tom == null ? super.getForeground() : Tema.texto(tomVisivel());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.suave(g);
        g2.setColor(Tema.fundo(tomVisivel()));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
        g2.dispose();
        super.paintComponent(g);
    }
}
