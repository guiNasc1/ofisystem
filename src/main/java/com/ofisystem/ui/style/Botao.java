package com.ofisystem.ui.style;

import com.formdev.flatlaf.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Botao extends JButton {

    public enum Estilo { PRINCIPAL, SECUNDARIO, CONTORNO, DISCRETO, ATENCAO, PERIGO, TRILHO }

    public static final int ALTURA = 40;

    private final Estilo estilo;
    private float hover;
    private boolean pressionado;
    private Animator animador;

    public Botao(String texto, Estilo estilo) {
        this(texto, estilo, null);
    }

    public Botao(String texto, Estilo estilo, Icones.Tipo icone) {
        super(texto);
        this.estilo = estilo;
        setFont(Tema.fonte(Tema.TAM_TEXTO, Font.BOLD));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(estilo == Estilo.TRILHO ? LEFT : CENTER);
        setIconTextGap(8);
        setMargin(new Insets(0, 16, 0, 16));
        if (icone != null) {
            setIcon(Icones.de(icone, 18, this::corTexto));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animarHover(1f);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pressionado = false;
                animarHover(0f);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressionado = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressionado = false;
                repaint();
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    private void animarHover(float alvo) {
        Movimento.parar(animador);
        float inicio = hover;
        animador = Movimento.animar(alvo > inicio ? 120 : 180, Movimento.SAIDA_SUAVE, t -> {
            hover = inicio + (alvo - inicio) * t;
            repaint();
        }, null);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (getText() == null || getText().isEmpty()) {
            return new Dimension(ALTURA, ALTURA);
        }
        int largura = estilo == Estilo.TRILHO ? d.width : Math.max(96, d.width + 8);
        return new Dimension(largura, ALTURA);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Color corTexto() {
        if (!isEnabled()) return Tema.tintaSuave();
        return switch (estilo) {
            case PRINCIPAL -> Color.WHITE;
            case SECUNDARIO -> Tema.texto(Tema.Tom.INFO);
            case CONTORNO, DISCRETO -> Tema.tinta();
            case ATENCAO -> Tema.texto(Tema.Tom.ATENCAO);
            case PERIGO -> Tema.critico();
            case TRILHO -> Movimento.misturar(Tema.trilhoSuave(), Color.WHITE, hover);
        };
    }

    private Color corFundo() {
        return switch (estilo) {
            case PRINCIPAL -> Movimento.misturar(Tema.acoForte(), Tema.acoForteHover(), hover);
            case SECUNDARIO -> Movimento.misturar(Tema.fundo(Tema.Tom.INFO), Tema.aco(), hover * 0.18f);
            case CONTORNO -> Movimento.misturar(Tema.vinil(), Tema.vinil2(), hover);
            case DISCRETO -> transparente(Tema.vinil2(), hover);
            case ATENCAO -> Movimento.misturar(Tema.vinil(), Tema.fundo(Tema.Tom.ATENCAO), hover);
            case PERIGO -> Movimento.misturar(Tema.vinil(), Tema.fundo(Tema.Tom.CRITICO), hover);
            case TRILHO -> new Color(255, 255, 255, Math.round(hover * 22));
        };
    }

    private Color transparente(Color c, float alfa) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.round(alfa * 255));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.suave(g);
        int w = getWidth();
        int h = getHeight();
        int deslocamento = pressionado && isEnabled() ? 1 : 0;

        g2.setColor(isEnabled() || estilo == Estilo.TRILHO || estilo == Estilo.DISCRETO ? corFundo() : Tema.vinil2());
        g2.fillRoundRect(0, deslocamento, w, h - 1, Tema.RAIO_CONTROLE, Tema.RAIO_CONTROLE);

        Color contorno = !isEnabled() && estilo != Estilo.TRILHO && estilo != Estilo.DISCRETO ? Tema.borda() : switch (estilo) {
            case CONTORNO -> Tema.borda();
            case ATENCAO -> Movimento.misturar(Tema.borda(), Tema.atencao(), 0.6f);
            case PERIGO -> Movimento.misturar(Tema.borda(), Tema.critico(), 0.5f);
            default -> null;
        };
        if (contorno != null) {
            g2.setColor(contorno);
            g2.drawRoundRect(0, deslocamento, w - 1, h - 2, Tema.RAIO_CONTROLE, Tema.RAIO_CONTROLE);
        }

        if (isFocusOwner() && estilo != Estilo.TRILHO) {
            g2.setColor(Tema.foco());
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, Tema.RAIO_CONTROLE, Tema.RAIO_CONTROLE);
        }

        g2.dispose();
        Graphics conteudo = g.create(0, deslocamento, w, h);
        super.paintComponent(conteudo);
        conteudo.dispose();
    }

    @Override
    public Color getForeground() {
        return estilo == null ? super.getForeground() : corTexto();
    }
}
