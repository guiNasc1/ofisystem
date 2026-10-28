package com.ofisystem;

import com.ofisystem.ui.LoginFrame;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.util.JPAutil;

import javax.swing.*;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) {

        Tema.iniciar();

        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().exibir();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(JPAutil::close));

    }

}
