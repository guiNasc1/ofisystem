package com.ofisystem.ui.style;

import com.formdev.flatlaf.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ItemTrilho extends JButton {

    public enum Tipo { PRINCIPAL, SECAO, SUBITEM }

    private final Tipo tipo;
    private final Icones.Tipo icone;
    private boolean selecionado;
    private boolean aberto;
    private float hover;
    private float giro;
    private Animator animadorHover;
    private Animator animadorGiro;

    public ItemTrilho(String texto, Tipo tipo, Icones.Tipo icone) {
        super(texto);
        this.tipo = tipo;
        this.icone = icone;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setHorizontalAlignment(LEFT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Tema.fonte(Tema.TAM_TEXTO, tipo == Tipo.SUBITEM ? Font.PLAIN : Font.BOLD));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        int altura = tipo == Tipo.SUBITEM ? 36 : 40;
        setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));
        setPreferredSize(new Dimension(200, altura));
        setMinimumSize(new Dimension(0, altura));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animarHover(1f);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animarHover(0f);
            }
        });
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
        repaint();
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setAberto(boolean aberto) {
        if (this.aberto == aberto) return;
        this.aberto = aberto;
        Movimento.parar(animadorGiro);
        float inicio = giro;
        float alvo = aberto ? 1f : 0f;
        animadorGiro = Movimento.animar(200, Movimento.SAIDA_SUAVE, t -> {
            giro = inicio + (alvo - inicio) * t;
            repaint();
        }, null);
    }

    private void animarHover(float alvo) {
        Movimento.parar(animadorHover);
        float inicio = hover;
        animadorHover = Movimento.animar(alvo > inicio ? 100 : 180, Movimento.SAIDA_SUAVE, t -> {
            hover = inicio + (alvo - inicio) * t;
            repaint();
        }, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.suave(g);
        int w = getWidth();
        int h = getHeight();
        int margem = 12;

        if (selecionado) {
            g2.setColor(Tema.isEscuro() ? new Color(0x2B4A6F) : Color.WHITE);
            g2.fillRoundRect(margem, 2, w - margem * 2, h - 4, Tema.RAIO_CONTROLE, Tema.RAIO_CONTROLE);
        } else if (hover > 0f) {
            g2.setColor(new Color(255, 255, 255, Math.round(hover * 20)));
            g2.fillRoundRect(margem, 2, w - margem * 2, h - 4, Tema.RAIO_CONTROLE, Tema.RAIO_CONTROLE);
        }

        if (isFocusOwner()) {
            g2.setColor(new Color(0xA9C6E6));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(margem + 1, 3, w - margem * 2 - 2, h - 6, Tema.RAIO_CONTROLE, Tema.RAIO_CONTROLE);
        }

        Color corTexto;
        if (selecionado) {
            corTexto = Tema.isEscuro() ? Color.WHITE : new Color(0x1F3A5A);
        } else if (tipo == Tipo.SUBITEM) {
            corTexto = Movimento.misturar(Tema.trilhoSuave(), Color.WHITE, hover);
        } else {
            corTexto = Movimento.misturar(Tema.trilhoTexto(), Color.WHITE, hover);
        }

        int x = tipo == Tipo.SUBITEM ? 52 : 24;
        if (icone != null) {
            Graphics2D gi = (Graphics2D) g2.create();
            gi.translate(24, (h - 18) / 2.0);
            gi.scale(18 / 24.0, 18 / 24.0);
            gi.setColor(corTexto);
            gi.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Icones.desenhar(gi, icone);
            gi.dispose();
            x = 52;
        }

        g2.setFont(getFont());
        g2.setColor(corTexto);
        FontMetrics fm = g2.getFontMetrics();
        int baseTexto = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, baseTexto);

        if (tipo == Tipo.SECAO) {
            Graphics2D gc = (Graphics2D) g2.create();
            gc.translate(w - 24 - 8, h / 2.0);
            gc.rotate(Math.toRadians(-90 + 90 * giro));
            gc.translate(-8, -8);
            gc.scale(16 / 24.0, 16 / 24.0);
            gc.setColor(Tema.trilhoSuave());
            gc.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Icones.desenhar(gc, Icones.Tipo.CHEVRON);
            gc.dispose();
        }
        g2.dispose();
    }
}
