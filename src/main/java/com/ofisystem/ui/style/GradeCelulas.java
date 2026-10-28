package com.ofisystem.ui.style;

import java.awt.*;

public class GradeCelulas implements LayoutManager {

    private final int larguraMinima;
    private final int espaco;

    public GradeCelulas(int larguraMinima, int espaco) {
        this.larguraMinima = larguraMinima;
        this.espaco = espaco;
    }

    private int colunas(Container pai) {
        Insets in = pai.getInsets();
        int disponivel = pai.getWidth() - in.left - in.right;
        if (disponivel <= 0) return 4;
        return Math.max(1, (disponivel + espaco) / (larguraMinima + espaco));
    }

    @Override
    public Dimension preferredLayoutSize(Container pai) {
        Insets in = pai.getInsets();
        int colunas = colunas(pai);
        int n = pai.getComponentCount();
        int altura = 0;
        for (int inicio = 0; inicio < n; inicio += colunas) {
            int linha = 0;
            for (int i = inicio; i < Math.min(n, inicio + colunas); i++) {
                linha = Math.max(linha, pai.getComponent(i).getPreferredSize().height);
            }
            altura += linha + (inicio > 0 ? espaco : 0);
        }
        int largura = colunas * larguraMinima + (colunas - 1) * espaco;
        return new Dimension(largura + in.left + in.right, altura + in.top + in.bottom);
    }

    @Override
    public Dimension minimumLayoutSize(Container pai) {
        return new Dimension(larguraMinima, 0);
    }

    @Override
    public void layoutContainer(Container pai) {
        Insets in = pai.getInsets();
        int colunas = colunas(pai);
        int disponivel = pai.getWidth() - in.left - in.right;
        int largura = (disponivel - (colunas - 1) * espaco) / colunas;
        int n = pai.getComponentCount();
        int y = in.top;
        for (int inicio = 0; inicio < n; inicio += colunas) {
            int altura = 0;
            for (int i = inicio; i < Math.min(n, inicio + colunas); i++) {
                altura = Math.max(altura, pai.getComponent(i).getPreferredSize().height);
            }
            for (int i = inicio; i < Math.min(n, inicio + colunas); i++) {
                int coluna = i - inicio;
                pai.getComponent(i).setBounds(in.left + coluna * (largura + espaco), y, largura, altura);
            }
            y += altura + espaco;
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }
}
