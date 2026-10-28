package com.ofisystem.ui.style;

import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public final class Movimento {

    public static final CubicBezierEasing SAIDA_SUAVE = new CubicBezierEasing(0.16f, 1f, 0.3f, 1f);
    public static final CubicBezierEasing ENTRADA = new CubicBezierEasing(0.4f, 0f, 1f, 1f);

    private Movimento() {
    }

    public static Animator animar(int duracao, CubicBezierEasing curva, Consumer<Float> passo, Runnable fim) {
        if (!Animator.useAnimation()) {
            passo.accept(1f);
            if (fim != null) fim.run();
            return null;
        }
        Animator animador = new Animator(duracao, passo::accept, fim);
        animador.setInterpolator(curva);
        animador.setResolution(15);
        animador.start();
        return animador;
    }

    public static void parar(Animator animador) {
        if (animador != null && animador.isRunning()) {
            animador.cancel();
        }
    }

    public static Color misturar(Color de, Color para, float t) {
        return new Color(
                Math.round(de.getRed() + (para.getRed() - de.getRed()) * t),
                Math.round(de.getGreen() + (para.getGreen() - de.getGreen()) * t),
                Math.round(de.getBlue() + (para.getBlue() - de.getBlue()) * t));
    }

    public static BufferedImage capturar(Component componente) {
        int largura = componente.getWidth();
        int altura = componente.getHeight();
        if (largura <= 0 || altura <= 0) return null;
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        componente.paint(g2);
        g2.dispose();
        return imagem;
    }

    public static void transicaoCor(AbstractButton botao, Color fundo, Color texto, int duracao) {
        parar((Animator) botao.getClientProperty(Movimento.class));
        Color fundoInicial = botao.getBackground();
        Color textoInicial = botao.getForeground();
        botao.putClientProperty(Movimento.class, animar(duracao, SAIDA_SUAVE, t -> {
            botao.setBackground(misturar(fundoInicial, fundo, t));
            if (texto != null) botao.setForeground(misturar(textoInicial, texto, t));
        }, null));
    }

    public static class CamadaDesvanecer extends JComponent {

        private final BufferedImage imagem;
        private float opacidade = 1f;

        public CamadaDesvanecer(BufferedImage imagem) {
            this.imagem = imagem;
        }

        public void setOpacidade(float opacidade) {
            this.opacidade = Math.max(0f, Math.min(1f, opacidade));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (imagem == null || opacidade <= 0f) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidade));
            g2.drawImage(imagem, 0, 0, null);
            g2.dispose();
        }
    }
}
