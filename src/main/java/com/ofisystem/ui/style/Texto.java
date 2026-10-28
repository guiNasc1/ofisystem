package com.ofisystem.ui.style;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class Texto extends JLabel {

    private final Supplier<Color> cor;

    public Texto(String texto, int tamanho, int estilo, Supplier<Color> cor) {
        super(texto);
        this.cor = cor;
        setFont(Tema.fonte(tamanho, estilo));
    }

    public static Texto titulo(String texto) {
        return new Texto(texto, Tema.TAM_TITULO, Font.BOLD, Tema::tinta);
    }

    public static Texto corpo(String texto) {
        return new Texto(texto, Tema.TAM_TEXTO, Font.PLAIN, Tema::tinta);
    }

    public static Texto apoio(String texto) {
        return new Texto(texto, Tema.TAM_TEXTO, Font.PLAIN, Tema::tintaSuave);
    }

    public static Texto rotulo(String texto) {
        return new Texto(texto == null ? "" : texto.toUpperCase(), Tema.TAM_PEQUENO, Font.BOLD, Tema::tintaSuave);
    }

    public static Texto erro() {
        return new Texto(" ", Tema.TAM_PEQUENO, Font.BOLD, Tema::critico);
    }

    @Override
    public Color getForeground() {
        return cor == null ? super.getForeground() : cor.get();
    }
}
