package com.ofisystem.ui.style;

import com.formdev.flatlaf.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;

public class Adesivo extends JPanel {

    public static final int ALTURA_FAIXA = 32;

    private String rotulo;
    private String estado;
    private Tema.Tom tom;
    private float descolar;
    private float apagado;
    private Animator animador;
    private Runnable aoAbrir;
    private java.util.function.Consumer<Boolean> aoMudarFoco;
    private boolean focado;

    public void setAoMudarFoco(java.util.function.Consumer<Boolean> aoMudarFoco) {
        this.aoMudarFoco = aoMudarFoco;
    }

    public Adesivo(String rotulo, String estado, Tema.Tom tom, LayoutManager layout) {
        super(layout);
        this.rotulo = rotulo;
        this.estado = estado;
        this.tom = tom;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(ALTURA_FAIXA + 12, 16, 12 + Tema.FOLGA_SOMBRA + 4, 16));
        setFocusable(false);
    }

    public void setDados(String rotulo, String estado, Tema.Tom tom) {
        this.rotulo = rotulo;
        this.estado = estado;
        this.tom = tom;
        repaint();
    }

    public void setAoAbrir(Runnable aoAbrir) {
        this.aoAbrir = aoAbrir;
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animar(1f);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!contem(e)) animar(isFocusOwner() ? 1f : 0f);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (Adesivo.this.aoAbrir != null) Adesivo.this.aoAbrir.run();
            }
        };
        addMouseListener(mouse);
        for (Component filho : getComponents()) {
            filho.addMouseListener(mouse);
            if (filho instanceof JComponent jc) jc.setCursor(getCursor());
        }
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                animar(1f);
            }

            @Override
            public void focusLost(FocusEvent e) {
                animar(0f);
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) && Adesivo.this.aoAbrir != null) {
                    Adesivo.this.aoAbrir.run();
                }
            }
        });
    }

    private boolean contem(MouseEvent e) {
        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), this);
        return p.x >= 0 && p.y >= 0 && p.x < getWidth() && p.y < getHeight();
    }

    public float getDescolar() {
        return descolar;
    }

    public void setDescolar(float descolar) {
        this.descolar = Math.max(0f, Math.min(1f, descolar));
        repaint();
    }

    public void setApagado(float apagado) {
        this.apagado = apagado;
        repaint();
    }

    public float getApagado() {
        return apagado;
    }

    private void animar(float alvo) {
        boolean novoFoco = alvo > 0.5f;
        if (novoFoco != focado) {
            focado = novoFoco;
            if (aoMudarFoco != null) aoMudarFoco.accept(focado);
        }
        Movimento.parar(animador);
        float inicio = descolar;
        animador = Movimento.animar(alvo > inicio ? 220 : 160, Movimento.SAIDA_SUAVE, t -> {
            descolar = inicio + (alvo - inicio) * t;
            repaint();
            Container pai = getParent();
            if (pai != null) pai.repaint();
        }, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.suave(g);
        int w = getWidth() - 2;
        int elevacao = Math.round(3 + descolar * 9);
        int h = getHeight() - Tema.FOLGA_SOMBRA - 4;
        int y = Math.round(4 - descolar * 3);
        int dobra = Math.round(descolar * 22);
        int r = Tema.RAIO;

        Tema.sombra(g2, 1, y, w, h, r, elevacao);

        Shape corpo = corpoComDobra(1, y, w, h, r, dobra);
        g2.setColor(Tema.vinil());
        g2.fill(corpo);

        Shape antigo = g2.getClip();
        g2.clip(corpo);
        g2.setColor(Tema.faixa(tom));
        g2.fillRect(1, y, w, ALTURA_FAIXA);
        g2.setClip(antigo);

        g2.setColor(Tema.borda());
        g2.draw(corpo);

        g2.setFont(Tema.fonte(Tema.TAM_PEQUENO, Font.BOLD));
        FontMetrics fm = g2.getFontMetrics();
        int base = y + (ALTURA_FAIXA + fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(Tema.textoFaixa(tom));
        String esq = rotulo == null ? "" : rotulo.toUpperCase();
        String dir = estado == null ? "" : estado.toUpperCase();
        int larguraDir = fm.stringWidth(dir);
        int limiteEsq = w - 16 - larguraDir - 16 - dobra;
        g2.drawString(cortar(esq, fm, limiteEsq), 16, base);
        g2.drawString(dir, Math.max(16, w - 14 - larguraDir - dobra), base);

        if (dobra > 1) {
            Path2D verso = new Path2D.Double();
            verso.moveTo(1 + w - dobra, y);
            verso.lineTo(1 + w - dobra, y + dobra);
            verso.lineTo(1 + w, y + dobra);
            verso.closePath();
            g2.setPaint(new GradientPaint(1 + w - dobra, y, Movimento.misturar(Tema.vinil2(), Tema.borda(), 0.7f),
                    1 + w, y + dobra, Tema.vinil()));
            g2.fill(verso);
            g2.setColor(Tema.borda());
            g2.draw(verso);
        }

        if (apagado > 0f) {
            g2.setColor(new Color(Tema.chao().getRed(), Tema.chao().getGreen(), Tema.chao().getBlue(), Math.round(apagado * 140)));
            g2.fill(corpo);
        }

        if (isFocusOwner()) {
            g2.setColor(Tema.foco());
            g2.setStroke(new BasicStroke(2f));
            g2.draw(corpoComDobra(-1, y - 2, w + 4, h + 4, r + 4, dobra));
        }
        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(0, Math.round(-descolar * 3));
        super.paintChildren(g2);
        if (apagado > 0f) {
            Graphics2D g3 = Tema.suave(g2);
            g3.setColor(new Color(Tema.chao().getRed(), Tema.chao().getGreen(), Tema.chao().getBlue(), Math.round(apagado * 140)));
            g3.fillRect(0, ALTURA_FAIXA + 4, getWidth(), getHeight());
            g3.dispose();
        }
        g2.dispose();
    }

    private Shape corpoComDobra(int x, int y, int w, int h, int r, int dobra) {
        Path2D p = new Path2D.Double();
        p.moveTo(x + r, y);
        p.lineTo(x + w - Math.max(dobra, r), y);
        if (dobra > r) {
            p.lineTo(x + w, y + dobra);
        } else {
            p.quadTo(x + w, y, x + w, y + r);
        }
        p.lineTo(x + w, y + h - r);
        p.quadTo(x + w, y + h, x + w - r, y + h);
        p.lineTo(x + r, y + h);
        p.quadTo(x, y + h, x, y + h - r);
        p.lineTo(x, y + r);
        p.quadTo(x, y, x + r, y);
        p.closePath();
        return p;
    }

    private String cortar(String texto, FontMetrics fm, int limite) {
        if (fm.stringWidth(texto) <= limite) return texto;
        String reticencias = "…";
        for (int i = texto.length() - 1; i > 0; i--) {
            String parte = texto.substring(0, i) + reticencias;
            if (fm.stringWidth(parte) <= limite) return parte;
        }
        return "";
    }
}
