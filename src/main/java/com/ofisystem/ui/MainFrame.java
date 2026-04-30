package com.ofisystem.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Oficina AutoMecânica — Sistema de Gestão");
        setSize(1100,700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centraliza na tela

        // Menu lateral
        JPanel menuLateral = criarMenuLateral();
        add(menuLateral, BorderLayout.WEST);

        // Painel de conteúdo central (começa vazio)
        JPanel conteudo = new JPanel();
        conteudo.setBackground(new Color(245, 245, 245));
        JLabel bemVindo = new JLabel("Bem-vindo ao sistema da oficina!");
        bemVindo.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        bemVindo.setHorizontalAlignment(SwingConstants.CENTER);
        conteudo.setLayout(new BorderLayout());
        conteudo.add(bemVindo, BorderLayout.CENTER);
        add(conteudo, BorderLayout.CENTER);
    }

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(200, 0));
        menu.setBackground(new Color(33, 37, 41));
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        // Título do menu
        JLabel titulo = new JLabel("OFICINA");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));
        menu.add(titulo);

        // Botões de navegação
        menu.add(criarBotaoMenu("👤  Clientes"));
        menu.add(criarBotaoMenu("🏍️  Motos"));
        menu.add(criarBotaoMenu("🔧  Ordens de Serviço"));
        menu.add(criarBotaoMenu("📦  Estoque"));
        menu.add(criarBotaoMenu("💰  Financeiro"));
        menu.add(Box.createVerticalGlue()); // empurra tudo para cima

        return menu;
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(200, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(33, 37, 41));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(55, 65, 75));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(33, 37, 41));
            }
        });

        return btn;
    }
}