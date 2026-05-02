package com.ofisystem.ui;

import com.ofisystem.ui.panels.AbstractPanel;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame {

    private JFrame frame;
    private JPanel painelCentral;

    private List<JPanel> todosOsMenus = new ArrayList<>();
    private List<JButton> todosBotoesTitulo = new ArrayList<>();

    public MainFrame() {
        frame = new JFrame();
        frame.setTitle("Oficina AutoMecânica — Sistema de Gestão");
        frame.setSize(1100, 700);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Menu lateral fixo — nunca muda
        frame.add(criarMenuLateral(), BorderLayout.WEST);

        // Painel central — só esse muda
        painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(new Color(245, 245, 245));
        mostrarBemVindo();
        frame.add(painelCentral, BorderLayout.CENTER);
    }

    public void exibirFrame() {
        frame.setVisible(true);
    }

    // Troca o conteúdo do painel central
    private void navegarPara(AbstractPanel painel) {
        painelCentral.removeAll();
        painelCentral.add(painel, BorderLayout.CENTER);
        painelCentral.revalidate();
        painelCentral.repaint();
    }

    private void mostrarBemVindo() {
        JLabel label = new JLabel("Bem-vindo ao sistema da oficina! 🏍️");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        painelCentral.add(label, BorderLayout.CENTER);
    }

    private void fecharTodosMenus() {
        for (int i = 0; i < todosOsMenus.size(); i++) {
            todosOsMenus.get(i).setVisible(false);
            todosBotoesTitulo.get(i).setText(todosBotoesTitulo.get(i).getText().replace("  ▼", "  ▶")
            );
            todosBotoesTitulo.get(i).setForeground(new Color(245, 245, 245));
        }
    }

    // ─── Menu lateral ────────────────────────────────────────────

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(210, 0));
        menu.setBackground(new Color(33, 37, 41));
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        // Logo / título
        JLabel titulo = new JLabel("🏍️  OFICINA");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(28, 0, 28, 0));
        menu.add(titulo);

        // Separador
        menu.add(criarSeparador());

        // Itens do menu com subitens
        menu.add(criarMenuExpansivel("👤  Clientes", new String[]{
                "Cadastrar", "Consultar"
        }));

        menu.add(criarMenuExpansivel("🏍️  Motos", new String[]{
                "Cadastrar", "Consultar"
        }));

        menu.add(criarMenuExpansivel("🔧  Ordens de Serviço", new String[]{
                "Nova Ordem", "Consultar"
        }));

        menu.add(criarMenuExpansivel("📦  Estoque", new String[]{
                "Cadastrar Peça", "Consultar", "Movimentação"
        }));

        menu.add(criarMenuExpansivel("💰  Financeiro", new String[]{
                "Lançamentos", "Relatório"
        }));

        menu.add(criarMenuExpansivel("📖  Manuais", new String[]{
                "Catálogo"
        }));

        menu.add(Box.createVerticalGlue()); // empurra tudo para cima

        return menu;
    }

    private JPanel criarMenuExpansivel(String titulo, String[] subitens) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(33, 37, 41));
        container.setMaximumSize(new Dimension(210, 1000));

        // Botão principal
        JButton btnTitulo = new JButton(titulo + "  ▶");
        btnTitulo.setMaximumSize(new Dimension(210, 46));
        btnTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTitulo.setForeground(new Color(200, 200, 200));
        btnTitulo.setBackground(new Color(33, 37, 41));
        btnTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnTitulo.setBorderPainted(false);
        btnTitulo.setFocusPainted(false);
        btnTitulo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        btnTitulo.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        // Painel dos subitens — começa escondido
        JPanel painelSub = new JPanel();
        painelSub.setLayout(new BoxLayout(painelSub, BoxLayout.Y_AXIS));
        painelSub.setBackground(new Color(22, 25, 29));
        painelSub.setVisible(false);

        // Cria cada subitem
        for (String subitem : subitens) {
            JButton btnSub = new JButton("        • " + subitem);
            btnSub.setMaximumSize(new Dimension(210, 38));
            btnSub.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnSub.setForeground(new Color(160, 160, 160));
            btnSub.setBackground(new Color(22, 25, 29));
            btnSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnSub.setBorderPainted(false);
            btnSub.setFocusPainted(false);
            btnSub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnSub.setHorizontalAlignment(SwingConstants.LEFT);

            // Hover no subitem
            btnSub.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btnSub.setForeground(Color.WHITE);
                    btnSub.setBackground(new Color(35, 40, 46));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btnSub.setForeground(new Color(160, 160, 160));
                    btnSub.setBackground(new Color(22, 25, 29));
                }
            });

            // Ação do subitem — aqui você vai conectar os painéis reais
            btnSub.addActionListener(e -> onSubitemClicado(titulo, subitem));

            painelSub.add(btnSub);
        }

        // Clique no título — expande ou recolhe
        btnTitulo.addActionListener(e -> {
            boolean visivel = painelSub.isVisible();
            painelSub.setVisible(!visivel);
            btnTitulo.setText(titulo + (visivel ? "  ▶" : "  ▼"));
            btnTitulo.setForeground(visivel
                    ? new Color(200, 200, 200) : Color.WHITE);
            container.revalidate();
            container.repaint();
        });

        // Hover no título
        btnTitulo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnTitulo.setBackground(new Color(45, 50, 56));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnTitulo.setBackground(new Color(33, 37, 41));
            }
        });

        container.add(btnTitulo);
        container.add(painelSub);
        return container;
    }

    // Aqui você conecta cada subitem ao seu painel
    private void onSubitemClicado(String menu, String subitem) {
        // Exemplo — substitua os JOptionPane pelos painéis reais quando criar
        // switch (menu) {
        //     case "👤  Clientes" -> {
        //         if (subitem.equals("Cadastrar")) navegarPara(new ClienteCadastroPanel());
        //         if (subitem.equals("Consultar")) navegarPara(new ClienteConsultaPanel());
        //     }
        //     case "📦  Estoque" -> {
        //         if (subitem.equals("Cadastrar Peça")) navegarPara(new EstoquePanel());
        //     }
        // }

        // Por enquanto mostra o que foi clicado
        JOptionPane.showMessageDialog(frame,
                "Você clicou em: " + menu.trim() + " → " + subitem);
    }

    private JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(55, 60, 65));
        sep.setMaximumSize(new Dimension(210, 1));
        return sep;
    }
}