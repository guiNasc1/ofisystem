package com.ofisystem.ui.style;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.function.Supplier;

public final class Icones {

    public enum Tipo {
        INICIO, OS, VENDA, PESSOAS, SERVICOS, ESTOQUE, RELATORIOS,
        SOL, LUA, SAIR, TROCAR, CHEVRON, MAIS, ALERTA, CADEADO, USUARIO
    }

    private Icones() {
    }

    public static Icon de(Tipo tipo, int tamanho, Supplier<Color> cor) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = Tema.suave(g);
                g2.translate(x, y);
                g2.scale(tamanho / 24.0, tamanho / 24.0);
                g2.setColor(cor.get());
                g2.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                desenhar(g2, tipo);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return tamanho;
            }

            @Override
            public int getIconHeight() {
                return tamanho;
            }
        };
    }

    public static void desenhar(Graphics2D g, Tipo tipo) {
        switch (tipo) {
            case INICIO -> {
                Path2D p = new Path2D.Double();
                p.moveTo(3, 11);
                p.lineTo(12, 3.5);
                p.lineTo(21, 11);
                g.draw(p);
                Path2D casa = new Path2D.Double();
                casa.moveTo(5.5, 9.5);
                casa.lineTo(5.5, 20.5);
                casa.lineTo(18.5, 20.5);
                casa.lineTo(18.5, 9.5);
                g.draw(casa);
                g.draw(new RoundRectangle2D.Double(10, 14, 4, 6.5, 1, 1));
            }
            case OS -> {
                g.draw(new RoundRectangle2D.Double(5, 4.5, 14, 17, 3, 3));
                g.draw(new RoundRectangle2D.Double(9, 2.5, 6, 4, 1.5, 1.5));
                g.draw(new Line2D.Double(8.5, 11, 15.5, 11));
                g.draw(new Line2D.Double(8.5, 14.5, 15.5, 14.5));
                g.draw(new Line2D.Double(8.5, 18, 12.5, 18));
            }
            case VENDA -> {
                Path2D sacola = new Path2D.Double();
                sacola.moveTo(4.5, 8);
                sacola.lineTo(19.5, 8);
                sacola.lineTo(18.5, 21);
                sacola.lineTo(5.5, 21);
                sacola.closePath();
                g.draw(sacola);
                g.draw(new Arc2D.Double(8.5, 3, 7, 9, 0, 180, Arc2D.OPEN));
            }
            case PESSOAS -> {
                g.draw(new Ellipse2D.Double(5.5, 4, 7, 7));
                g.draw(new Arc2D.Double(2.5, 13.5, 13, 12, 20, 140, Arc2D.OPEN));
                g.draw(new Arc2D.Double(14, 5, 6, 6, -90, 220, Arc2D.OPEN));
                g.draw(new Arc2D.Double(15, 14, 7.5, 10, 20, 85, Arc2D.OPEN));
            }
            case SERVICOS -> {
                Path2D chave = new Path2D.Double();
                chave.moveTo(14.5, 6);
                chave.curveTo(15.8, 3.2, 19.8, 2.8, 21, 5.5);
                chave.lineTo(18, 8.5);
                chave.lineTo(15.5, 6);
                chave.moveTo(14.5, 6);
                chave.curveTo(13.5, 8.5, 14.5, 10.5, 15.5, 11.2);
                g.draw(chave);
                g.draw(new Line2D.Double(14.5, 10.5, 5, 20));
                g.draw(new Line2D.Double(11.8, 9.5, 4, 17.3));
                g.draw(new Arc2D.Double(3.2, 16.2, 3.6, 3.6, 135, 180, Arc2D.OPEN));
                Path2D topo = new Path2D.Double();
                topo.moveTo(21, 5.5);
                topo.curveTo(22, 9, 18.5, 12.5, 15.2, 11);
                g.draw(topo);
            }
            case ESTOQUE -> {
                Path2D caixa = new Path2D.Double();
                caixa.moveTo(12, 2.8);
                caixa.lineTo(20.5, 7.2);
                caixa.lineTo(20.5, 16.8);
                caixa.lineTo(12, 21.2);
                caixa.lineTo(3.5, 16.8);
                caixa.lineTo(3.5, 7.2);
                caixa.closePath();
                g.draw(caixa);
                Path2D meio = new Path2D.Double();
                meio.moveTo(3.5, 7.2);
                meio.lineTo(12, 11.6);
                meio.lineTo(20.5, 7.2);
                meio.moveTo(12, 11.6);
                meio.lineTo(12, 21.2);
                g.draw(meio);
            }
            case RELATORIOS -> {
                g.draw(new Line2D.Double(4, 20.5, 20, 20.5));
                g.draw(new RoundRectangle2D.Double(5.5, 12, 3, 8.5, 1, 1));
                g.draw(new RoundRectangle2D.Double(10.5, 6.5, 3, 14, 1, 1));
                g.draw(new RoundRectangle2D.Double(15.5, 9.5, 3, 11, 1, 1));
            }
            case SOL -> {
                g.draw(new Ellipse2D.Double(8, 8, 8, 8));
                for (int i = 0; i < 8; i++) {
                    double a = Math.PI / 4 * i;
                    g.draw(new Line2D.Double(12 + Math.cos(a) * 7, 12 + Math.sin(a) * 7,
                            12 + Math.cos(a) * 9.5, 12 + Math.sin(a) * 9.5));
                }
            }
            case LUA -> {
                Area lua = new Area(new Ellipse2D.Double(4, 4, 16, 16));
                lua.subtract(new Area(new Ellipse2D.Double(9.5, 1.5, 14, 14)));
                g.draw(lua);
            }
            case SAIR -> {
                Path2D porta = new Path2D.Double();
                porta.moveTo(10, 3.5);
                porta.lineTo(5, 3.5);
                porta.lineTo(5, 20.5);
                porta.lineTo(10, 20.5);
                g.draw(porta);
                g.draw(new Line2D.Double(10, 12, 20.5, 12));
                Path2D seta = new Path2D.Double();
                seta.moveTo(16.5, 8);
                seta.lineTo(20.5, 12);
                seta.lineTo(16.5, 16);
                g.draw(seta);
            }
            case TROCAR -> {
                g.draw(new Line2D.Double(4, 8, 19, 8));
                Path2D s1 = new Path2D.Double();
                s1.moveTo(15.5, 4.5);
                s1.lineTo(19, 8);
                s1.lineTo(15.5, 11.5);
                g.draw(s1);
                g.draw(new Line2D.Double(20, 16, 5, 16));
                Path2D s2 = new Path2D.Double();
                s2.moveTo(8.5, 12.5);
                s2.lineTo(5, 16);
                s2.lineTo(8.5, 19.5);
                g.draw(s2);
            }
            case CHEVRON -> {
                Path2D c = new Path2D.Double();
                c.moveTo(7, 9.5);
                c.lineTo(12, 14.5);
                c.lineTo(17, 9.5);
                g.draw(c);
            }
            case MAIS -> {
                g.draw(new Line2D.Double(12, 5, 12, 19));
                g.draw(new Line2D.Double(5, 12, 19, 12));
            }
            case ALERTA -> {
                Path2D t = new Path2D.Double();
                t.moveTo(12, 3.5);
                t.lineTo(21.5, 20);
                t.lineTo(2.5, 20);
                t.closePath();
                g.draw(t);
                g.draw(new Line2D.Double(12, 9.5, 12, 14));
                g.fill(new Ellipse2D.Double(11, 16.3, 2, 2));
            }
            case CADEADO -> {
                g.draw(new RoundRectangle2D.Double(5, 10.5, 14, 10.5, 3, 3));
                g.draw(new Arc2D.Double(8, 3, 8, 11, 0, 180, Arc2D.OPEN));
                g.draw(new Line2D.Double(8, 8.5, 8, 10.5));
                g.draw(new Line2D.Double(16, 8.5, 16, 10.5));
            }
            case USUARIO -> {
                g.draw(new Ellipse2D.Double(8, 3.5, 8, 8));
                g.draw(new Arc2D.Double(4, 14, 16, 14, 15, 150, Arc2D.OPEN));
            }
        }
    }
}
