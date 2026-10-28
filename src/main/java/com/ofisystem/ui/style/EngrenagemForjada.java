package com.ofisystem.ui.style;

import com.formdev.flatlaf.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.*;

public class EngrenagemForjada extends JComponent {

    private static final int DENTES = 8;
    private static final int DURACAO = 1700;

    private float t = 1f;
    private Animator animador;
    private Runnable aoConcluir;
    private boolean concluido = true;
    private String titulo = "OfiSystem";
    private String subtitulo = "Gestão de oficina";
    private boolean somenteMarca;

    public EngrenagemForjada() {
        setOpaque(true);
    }

    public void setSomenteMarca(boolean somenteMarca) {
        this.somenteMarca = somenteMarca;
        setOpaque(!somenteMarca);
        repaint();
    }

    public void setTextos(String titulo, String subtitulo) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        repaint();
    }

    public void forjar(Runnable aoConcluir) {
        this.aoConcluir = aoConcluir;
        Movimento.parar(animador);
        if (!Animator.useAnimation()) {
            pular();
            return;
        }
        concluido = false;
        t = 0f;
        animador = new Animator(DURACAO, fracao -> {
            t = fracao;
            repaint();
        }, this::concluir);
        animador.setResolution(15);
        animador.start();
    }

    public void pular() {
        if (concluido) return;
        Movimento.parar(animador);
        t = 1f;
        repaint();
        concluir();
    }

    public boolean isConcluido() {
        return concluido;
    }

    private void concluir() {
        if (concluido) return;
        concluido = true;
        t = 1f;
        repaint();
        if (aoConcluir != null) aoConcluir.run();
    }

    private static float fase(float t, float inicio, float fim) {
        if (t <= inicio) return 0f;
        if (t >= fim) return 1f;
        return (t - inicio) / (fim - inicio);
    }

    private static float sair(float x) {
        return 1f - (float) Math.pow(1f - x, 3);
    }

    private static float encaixe(float x) {
        double c1 = 1.2;
        double c3 = c1 + 1;
        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.suave(g);
        int w = getWidth();
        int h = getHeight();

        double raio;
        double cx = w / 2.0;
        double cy;
        if (somenteMarca) {
            raio = Math.min(w, h) * 0.47;
            cy = h / 2.0;
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(0x1B2B3D), w, h, new Color(0x0B1219)));
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(255, 255, 255, 10));
            for (int x = 32; x < w; x += 32) g2.drawLine(x, 0, x, h);
            for (int y = 32; y < h; y += 32) g2.drawLine(0, y, w, y);

            raio = Math.min(w, h) * 0.22;
            cy = h * 0.44;

            float luz = sair(fase(t, 0.35f, 0.9f));
            g2.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), (float) (raio * 2.6),
                    new float[]{0f, 1f}, new Color[]{new Color(74, 121, 173, Math.round(70 * luz)), new Color(74, 121, 173, 0)}));
            g2.fillRect(0, 0, w, h);
        }

        float giro = encaixe(fase(t, 0.30f, 0.78f));
        double angulo = Math.toRadians(-38 * (1 - giro));
        AffineTransform base = g2.getTransform();
        g2.translate(cx, cy);
        g2.rotate(angulo);

        float corpo = sair(fase(t, 0.10f, 0.50f));
        if (corpo > 0) {
            Composite c = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, corpo));
            double escala = 0.7 + 0.3 * corpo;
            Area anel = anel(raio * 0.84 * escala, raio * 0.58 * escala);
            pintarAco(g2, anel, raio);
            g2.setComposite(c);
        }

        for (int i = 0; i < DENTES; i++) {
            float chegada = sair(fase(t, 0.02f + i * 0.045f, 0.42f + i * 0.045f));
            if (chegada <= 0) continue;
            double a = Math.PI * 2 / DENTES * i;
            double afastamento = raio * 1.4 * (1 - chegada);
            double giroDente = Math.toRadians(55) * (1 - chegada);
            Graphics2D gd = (Graphics2D) g2.create();
            gd.rotate(a);
            gd.translate(0, -afastamento);
            gd.rotate(giroDente);
            gd.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, chegada * 1.4f)));
            pintarAco(gd, dente(raio), raio);
            gd.dispose();
        }

        float disco = sair(fase(t, 0.55f, 0.85f));
        if (disco > 0) {
            double rd = raio * 0.56 * (0.6 + 0.4 * disco);
            Ellipse2D circulo = new Ellipse2D.Double(-rd, -rd, rd * 2, rd * 2);
            Composite c = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, disco));
            g2.setPaint(new GradientPaint((float) -rd, (float) -rd, new Color(0x4A79AD), (float) rd, (float) rd, new Color(0x1F3A5A)));
            g2.fill(circulo);
            g2.setColor(new Color(0x14263A));
            g2.setStroke(new BasicStroke((float) (raio * 0.035)));
            g2.draw(circulo);

            g2.rotate(-angulo);
            Font fonte = Tema.fonte(100, Font.BOLD);
            GlyphVector letra = fonte.createGlyphVector(g2.getFontRenderContext(), "S");
            Rectangle2D caixa = letra.getVisualBounds();
            double escalaLetra = rd * 1.25 / caixa.getHeight();
            AffineTransform at = AffineTransform.getScaleInstance(escalaLetra, escalaLetra);
            at.translate(-caixa.getCenterX(), -caixa.getCenterY());
            Shape s = at.createTransformedShape(letra.getOutline());
            g2.setPaint(new GradientPaint(0, (float) -rd, new Color(0xF2F5F8), 0, (float) rd, new Color(0xA7B2BE)));
            g2.fill(s);
            g2.setColor(new Color(0x16283B));
            g2.setStroke(new BasicStroke((float) (raio * 0.02)));
            g2.draw(s);
            g2.rotate(angulo);
            g2.setComposite(c);
        }

        float brilho = fase(t, 0.72f, 1f);
        if (brilho > 0 && brilho < 1) {
            Area peca = anel(raio * 0.84, 0);
            for (int i = 0; i < DENTES; i++) {
                Area d = new Area(dente(raio));
                d.transform(AffineTransform.getRotateInstance(Math.PI * 2 / DENTES * i));
                peca.add(d);
            }
            Shape antigo = g2.getClip();
            g2.clip(peca);
            double faixaX = -raio * 1.6 + raio * 3.2 * brilho;
            g2.setPaint(new GradientPaint((float) (faixaX - raio * 0.35), 0, new Color(255, 255, 255, 0),
                    (float) faixaX, 0, new Color(255, 255, 255, 150), true));
            g2.rotate(Math.toRadians(-24));
            g2.fill(new Rectangle2D.Double(faixaX - raio * 0.35, -raio * 2, raio * 0.7, raio * 4));
            g2.setClip(antigo);
        }

        g2.setTransform(base);

        if (somenteMarca) {
            g2.dispose();
            return;
        }

        float texto = sair(fase(t, 0.68f, 1f));
        if (texto > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, texto));
            int deslocar = Math.round((1 - texto) * 12);
            g2.setFont(Tema.fonte(Tema.TAM_TITULO, Font.BOLD));
            FontMetrics fm = g2.getFontMetrics();
            int yTitulo = (int) (cy + raio * 1.25) + fm.getAscent() + deslocar;
            g2.setColor(Color.WHITE);
            g2.drawString(titulo, (int) (cx - fm.stringWidth(titulo) / 2.0), yTitulo);
            g2.setFont(Tema.fonte(Tema.TAM_TEXTO));
            FontMetrics fm2 = g2.getFontMetrics();
            g2.setColor(Tema.trilhoSuave());
            g2.drawString(subtitulo, (int) (cx - fm2.stringWidth(subtitulo) / 2.0), yTitulo + fm2.getHeight() + 4);
        }

        if (!concluido) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.setFont(Tema.fonte(Tema.TAM_PEQUENO));
            g2.setColor(Tema.trilhoSuave());
            String dica = "Pressione qualquer tecla para pular";
            g2.drawString(dica, w - g2.getFontMetrics().stringWidth(dica) - 24, h - 24);
        }
        g2.dispose();
    }

    private Area anel(double externo, double interno) {
        Area a = new Area(new Ellipse2D.Double(-externo, -externo, externo * 2, externo * 2));
        if (interno > 0) {
            a.subtract(new Area(new Ellipse2D.Double(-interno, -interno, interno * 2, interno * 2)));
        }
        return a;
    }

    private Shape dente(double raio) {
        double base = raio * 0.80;
        double topo = raio * 1.04;
        double meiaBase = raio * 0.20;
        double meiaTopo = raio * 0.14;
        RoundRectangle2D arredondado = new RoundRectangle2D.Double(-meiaTopo, -topo, meiaTopo * 2, raio * 0.18, raio * 0.08, raio * 0.08);
        Path2D p = new Path2D.Double();
        p.moveTo(-meiaBase, -base);
        p.lineTo(-meiaTopo, -topo + raio * 0.06);
        p.lineTo(meiaTopo, -topo + raio * 0.06);
        p.lineTo(meiaBase, -base);
        p.closePath();
        Area dente = new Area(p);
        dente.add(new Area(arredondado));
        return dente;
    }

    private void pintarAco(Graphics2D g2, Shape forma, double raio) {
        g2.setPaint(new LinearGradientPaint(new Point2D.Double(-raio, -raio), new Point2D.Double(raio, raio),
                new float[]{0f, 0.45f, 0.7f, 1f},
                new Color[]{new Color(0xDCE3EA), new Color(0x9AA6B3), new Color(0xC3CCD6), new Color(0x6E7B89)}));
        g2.fill(forma);
        g2.setColor(new Color(0x3C4855));
        g2.setStroke(new BasicStroke((float) Math.max(1.2, raio * 0.018)));
        g2.draw(forma);
    }
}
