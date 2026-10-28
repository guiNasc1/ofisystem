package com.ofisystem.ui;

import javax.swing.*;
import java.util.function.Consumer;

public final class Navegador {

    private static Consumer<JPanel> destino;

    private Navegador() {
    }

    public static void registrar(Consumer<JPanel> novoDestino) {
        destino = novoDestino;
    }

    public static void ir(JPanel painel) {
        if (destino != null) {
            destino.accept(painel);
        }
    }
}
