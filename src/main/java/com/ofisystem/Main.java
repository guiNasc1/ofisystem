package com.ofisystem;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.ofisystem.ui.LoginFrame;
import com.ofisystem.ui.MainFrame;
import com.ofisystem.util.JPAutil;

import javax.swing.*;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) {

        FlatIntelliJLaf.setup();

        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().exibir();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(JPAutil::close));

    }

}
