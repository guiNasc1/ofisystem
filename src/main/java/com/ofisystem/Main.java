package com.ofisystem;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.ofisystem.ui.MainFrame;
import com.ofisystem.util.JPAutil;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        FlatIntelliJLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new MainFrame().exibirFrame();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(JPAutil::close));

    }

}
