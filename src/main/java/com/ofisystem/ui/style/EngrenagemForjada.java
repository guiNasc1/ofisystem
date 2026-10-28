package com.ofisystem.ui.style;

import com.formdev.flatlaf.util.Animator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EngrenagemForjada extends JComponent {

    private static final int DENTES = 8;
    private static final int DURACAO = 1700;

    private static final double CENTRO_X = 334.5;
    private static final double CENTRO_Y = 337.5;
    private static final double RAIO_DISCO = 216;
    private static final double RAIO_ANEL = 284;
    private static final double RAIO_DENTE = 340;

    private static BufferedImage logo;
    private static BufferedImage disco;
    private static BufferedImage anel;
    private static BufferedImage[] dentes;
    private static final Map<Integer, BufferedImage> MARCAS = new HashMap<>();

    private float t = 1f;
    private Animator animador;
    private Runnable aoConcluir;
    private boolean concluido = true;
    private String titulo = "OfiSystem";
    private String subtitulo = "Gestão de oficina";
    private boolean somenteMarca;

    public EngrenagemForjada() {
        setOpaque(true);
        carregarPecas();
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
        if (!Animator.useAnimation() || logo == null) {
            concluido = false;
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

    private static synchronized void carregarPecas() {
        if (logo != null) return;
        try (InputStream in = EngrenagemForjada.class.getResourceAsStream("/images/logo.png")) {
            if (in == null) return;
            BufferedImage original = ImageIO.read(in);
            logo = new BufferedImage(670, 676, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = logo.createGraphics();
            g.drawImage(original, -188, -36, null);
            g.dispose();
        } catch (Exception e) {
            System.err.println("Logo não carregado: " + e.getMessage());
            return;
        }

        int w = logo.getWidth();
        int h = logo.getHeight();
        disco = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        anel = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dentes = new BufferedImage[DENTES];
        for (int i = 0; i < DENTES; i++) {
            dentes[i] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = logo.getRGB(x, y);
                int alfa = argb >>> 24;
                if (alfa == 0) continue;
                double dx = x - CENTRO_X;
                double dy = y - CENTRO_Y;
                double r = Math.hypot(dx, dy);
                double pesoExterno = suave(r, RAIO_DISCO - 2, RAIO_DISCO + 2);
                double pesoDente = suave(r, RAIO_ANEL - 2, RAIO_ANEL + 2);
                pintar(disco, x, y, argb, 1 - pesoExterno);
                pintar(anel, x, y, argb, pesoExterno * (1 - pesoDente));
                if (pesoDente > 0) {
                    double angulo = Math.toDegrees(Math.atan2(dy, dx));
                    int setor = (int) Math.floorMod(Math.round(angulo / 45.0), DENTES);
                    pintar(dentes[setor], x, y, argb, pesoDente);
                }
            }
        }
    }

    private static double suave(double r, double inicio, double fim) {
        if (r <= inicio) return 0;
        if (r >= fim) return 1;
        double x = (r - inicio) / (fim - inicio);
        return x * x * (3 - 2 * x);
    }

    private static void pintar(BufferedImage destino, int x, int y, int argb, double peso) {
        if (peso <= 0) return;
        int alfa = (int) Math.round((argb >>> 24) * Math.min(1, peso));
        destino.setRGB(x, y, (alfa << 24) | (argb & 0xFFFFFF));
    }

    public static Image marca(int tamanho) {
        carregarPecas();
        if (logo == null) return null;
        return MARCAS.computeIfAbsent(tamanho, EngrenagemForjada::reduzir);
    }

    private static BufferedImage reduzir(int tamanho) {
        BufferedImage atual = logo;
        int largura = logo.getWidth();
        int altura = logo.getHeight();
        while (largura / 2 >= tamanho) {
            largura /= 2;
            altura /= 2;
            atual = redimensionar(atual, largura, altura);
        }
        double escala = (double) tamanho / Math.max(logo.getWidth(), logo.getHeight());
        return redimensionar(atual, (int) Math.round(logo.getWidth() * escala), (int) Math.round(logo.getHeight() * escala));
    }

    private static BufferedImage redimensionar(BufferedImage origem, int largura, int altura) {
        BufferedImage destino = new BufferedImage(Math.max(1, largura), Math.max(1, altura), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(origem, 0, 0, destino.getWidth(), destino.getHeight(), null);
        g.dispose();
        return destino;
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
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        int w = getWidth();
        int h = getHeight();

        if (somenteMarca) {
            Image marca = marca(Math.min(w, h));
            if (marca != null) {
                g2.drawImage(marca, (w - marca.getWidth(null)) / 2, (h - marca.getHeight(null)) / 2, null);
            }
            g2.dispose();
            return;
        }

        g2.setPaint(new GradientPaint(0, 0, new Color(0x1B2B3D), w, h, new Color(0x0B1219)));
        g2.fillRect(0, 0, w, h);
        g2.setColor(new Color(255, 255, 255, 10));
        for (int x = 32; x < w; x += 32) g2.drawLine(x, 0, x, h);
        for (int y = 32; y < h; y += 32) g2.drawLine(0, y, w, y);

        double raio = Math.min(w, h) * 0.23;
        double cx = w / 2.0;
        double cy = h * 0.44;
        double escala = raio / RAIO_DENTE;

        float luz = sair(fase(t, 0.35f, 0.9f));
        g2.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), (float) (raio * 2.6),
                new float[]{0f, 1f}, new Color[]{new Color(74, 121, 173, Math.round(70 * luz)), new Color(74, 121, 173, 0)}));
        g2.fillRect(0, 0, w, h);

        if (logo != null) {
            pintarPeca(g2, cx, cy, escala, raio);
        }

        float texto = sair(fase(t, 0.68f, 1f));
        if (texto > 0) {
            Graphics2D gt = (Graphics2D) g2.create();
            gt.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, texto));
            int deslocar = Math.round((1 - texto) * 12);
            gt.setFont(Tema.fonte(Tema.TAM_TITULO, Font.BOLD));
            FontMetrics fm = gt.getFontMetrics();
            int yTitulo = (int) (cy + raio * 1.25) + fm.getAscent() + deslocar;
            gt.setColor(Color.WHITE);
            gt.drawString(titulo, (int) (cx - fm.stringWidth(titulo) / 2.0), yTitulo);
            gt.setFont(Tema.fonte(Tema.TAM_TEXTO));
            FontMetrics fm2 = gt.getFontMetrics();
            gt.setColor(Tema.trilhoSuave());
            gt.drawString(subtitulo, (int) (cx - fm2.stringWidth(subtitulo) / 2.0), yTitulo + fm2.getHeight() + 4);
            gt.dispose();
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

    private void pintarPeca(Graphics2D g2, double cx, double cy, double escala, double raio) {
        float travado = fase(t, 0.84f, 0.92f);
        double angulo = Math.toRadians(-38 * (1 - encaixe(fase(t, 0.30f, 0.78f))));

        if (travado < 1f) {
            Graphics2D gp = (Graphics2D) g2.create();
            gp.translate(cx, cy);
            gp.rotate(angulo);

            float corpo = sair(fase(t, 0.10f, 0.50f));
            if (corpo > 0) {
                desenhar(gp, anel, escala * (0.7 + 0.3 * corpo), corpo * (1f - travado), 0, 0);
            }
            for (int i = 0; i < DENTES; i++) {
                float chegada = sair(fase(t, 0.02f + i * 0.045f, 0.42f + i * 0.045f));
                if (chegada <= 0) continue;
                double direcao = Math.toRadians(45 * i);
                double afastamento = raio * 1.4 * (1 - chegada);
                Graphics2D gd = (Graphics2D) gp.create();
                gd.translate(Math.cos(direcao) * afastamento, Math.sin(direcao) * afastamento);
                gd.rotate(Math.toRadians(55) * (1 - chegada));
                desenhar(gd, dentes[i], escala, Math.min(1f, chegada * 1.4f) * (1f - travado), 0, 0);
                gd.dispose();
            }
            float disc = sair(fase(t, 0.55f, 0.85f));
            if (disc > 0) {
                desenhar(gp, disco, escala * (0.6 + 0.4 * disc), disc * (1f - travado), 0, 0);
            }
            gp.dispose();
        }

        if (travado > 0f) {
            Graphics2D gl = (Graphics2D) g2.create();
            gl.translate(cx, cy);
            desenhar(gl, logo, escala, 1f, 0, 0);
            gl.dispose();
        }

        float brilho = fase(t, 0.72f, 1f);
        if (brilho > 0 && brilho < 1) {
            int lado = (int) Math.ceil(Math.max(logo.getWidth(), logo.getHeight()) * escala);
            BufferedImage camada = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gb = camada.createGraphics();
            gb.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gb.translate(lado / 2.0, lado / 2.0);
            gb.rotate(angulo);
            gb.scale(escala, escala);
            gb.drawImage(logo, (int) -CENTRO_X, (int) -CENTRO_Y, null);
            gb.setTransform(new java.awt.geom.AffineTransform());
            gb.setComposite(AlphaComposite.SrcIn);
            double largura = lado * 0.22;
            double faixa = -largura + (lado + largura * 2) * brilho;
            gb.rotate(Math.toRadians(-24), lado / 2.0, lado / 2.0);
            gb.setPaint(new LinearGradientPaint(
                    new Point2D.Double(faixa - largura, 0), new Point2D.Double(faixa + largura, 0),
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(255, 255, 255, 0), new Color(255, 255, 255, 170), new Color(255, 255, 255, 0)},
                    MultipleGradientPaint.CycleMethod.NO_CYCLE));
            gb.fillRect(-lado, -lado, lado * 3, lado * 3);
            gb.dispose();
            g2.drawImage(camada, (int) Math.round(cx - lado / 2.0), (int) Math.round(cy - lado / 2.0), null);
        }
    }

    private void desenhar(Graphics2D g, BufferedImage imagem, double escala, float alfa, double dx, double dy) {
        if (alfa <= 0f) return;
        Graphics2D gi = (Graphics2D) g.create();
        gi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, alfa)));
        gi.translate(dx, dy);
        gi.scale(escala, escala);
        gi.drawImage(imagem, (int) Math.round(-CENTRO_X), (int) Math.round(-CENTRO_Y), null);
        gi.dispose();
    }
}
