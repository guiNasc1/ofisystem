package com.ofisystem.ui;

import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Cliente;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.ui.panels.cliente.ClienteCadastroPanel;
import com.ofisystem.ui.panels.cliente.ClienteConsultaPanel;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
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
    private void navegarPara(JPanel painel) {
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

        JLabel lblUsuario = new JLabel("Usuário:" + Sessao.getNome());
        lblUsuario.setForeground(new Color(160,160,160));
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

        JButton btSair = new JButton("Sair");
        btSair.setMaximumSize(new Dimension(210,36));
        btSair.setAlignmentX(Component.CENTER_ALIGNMENT);
        btSair.setForeground(new Color(220, 53, 69));
        btSair.setBackground(new Color(33, 37, 41));
        btSair.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btSair.setBorderPainted(false);
        btSair.setFocusPainted(false);
        btSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btSair.addActionListener(e -> sair());

        JButton btTrocarUsuario = new JButton("Trocar Usuário");
        btTrocarUsuario.setMaximumSize(new Dimension(210,36));
        btTrocarUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        btTrocarUsuario.setForeground(new Color(255, 255, 255));
        btTrocarUsuario.setBackground(new Color(33, 37, 41));
        btTrocarUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btTrocarUsuario.setBorderPainted(false);
        btTrocarUsuario.setFocusPainted(false);
        btTrocarUsuario.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btTrocarUsuario.addActionListener(e -> trocarUsuario());

        menu.add(lblUsuario);
        menu.add(btSair);
        menu.add(btTrocarUsuario);

        return menu;
    }

    private void sair(){
        try{
        int resposta = JOptionPane.showConfirmDialog(frame,
                "Deseja sair do programa?", "Sair", JOptionPane.YES_NO_OPTION);
        if(resposta == JOptionPane.YES_OPTION) {
            Sessao.encerrar();
            frame.dispose();
            new LoginFrame().exibir();
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void trocarUsuario(){
        try{
            JDialog dialog = new JDialog();
            dialog.setSize(720, 560);
            dialog.setLocationRelativeTo(frame);
            dialog.setResizable(false);
            dialog.setLayout(new BorderLayout());

            JPanel painel = new JPanel(new GridBagLayout());
            painel.setBackground(new Color(245, 245, 245));
            painel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 0, 8, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = GridBagConstraints.REMAINDER;

            // Título
            JLabel titulo = new JLabel("Trocar Usuário", SwingConstants.CENTER);
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            gbc.insets = new Insets(0, 0, 20, 0);
            painel.add(titulo, gbc);

            // Campo usuário
            gbc.insets = new Insets(4, 0, 4, 0);
            JLabel lblUsuario = new JLabel("Usuário");
            lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
            painel.add(lblUsuario, gbc);

            JTextField txtUsuario = new JTextField();
            txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtUsuario.setPreferredSize(new Dimension(280, 36));
            txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            painel.add(txtUsuario, gbc);

            // Campo senha
            JLabel lblSenha = new JLabel("Senha");
            lblSenha.setFont(new Font("Segoe UI", Font.BOLD, 13));
            painel.add(lblSenha, gbc);

            JPasswordField txtSenha = new JPasswordField();
            txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtSenha.setPreferredSize(new Dimension(280, 36));
            txtSenha.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            painel.add(txtSenha, gbc);

            // Botões
            JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            painelBotoes.setBackground(new Color(245, 245, 245));

            JButton btCancelar = new JButton("Cancelar");
            btCancelar.setFocusPainted(false);

            JButton btEntrar = new JButton("Entrar");
            btEntrar.setBackground(new Color(33, 37, 41));
            btEntrar.setForeground(Color.WHITE);
            btEntrar.setFocusPainted(false);
            btEntrar.setBorderPainted(false);
            btEntrar.setPreferredSize(new Dimension(100, 34));

            painelBotoes.add(btCancelar);
            painelBotoes.add(btEntrar);

            gbc.insets = new Insets(16, 0, 0, 0);
            painel.add(painelBotoes, gbc);

            dialog.add(painel, BorderLayout.CENTER);

            // Ações
            btCancelar.addActionListener(e -> dialog.dispose());

            // Enter na senha também loga
            txtSenha.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        btEntrar.doClick();
                    }
                }
            });

            btEntrar.addActionListener(e -> {
                String usuario = txtUsuario.getText().trim();
                String senha   = new String(txtSenha.getPassword());

                if (usuario.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Preencha usuário e senha!", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Usuario u = usuarioDAO.autenticar(usuario, senha);

                if (u != null) {
                    Sessao.iniciar(u);          // troca a sessão para o novo usuário
                    dialog.dispose();           // fecha o dialog
                    frame.dispose();            // fecha o MainFrame atual

                    // Abre um novo MainFrame com o novo usuário
                    SwingUtilities.invokeLater(() -> new MainFrame().exibirFrame());
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Usuário ou senha incorretos!", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    txtSenha.setText("");
                    txtSenha.requestFocus();
                }
            });

            dialog.setVisible(true);
        } catch (Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarMenuExpansivel(String titulo, String[] subitens) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(33, 37, 41));
        container.setMaximumSize(new Dimension(210, 1000));

        // Botão do título
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

        // Registra na lista global
        todosOsMenus.add(painelSub);
        todosBotoesTitulo.add(btnTitulo);

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
            btnSub.addActionListener(e -> {
                    onSubitemClicado(titulo, subitem);
            });

            painelSub.add(btnSub);
        }

        // Clique no título — fecha todos e abre só esse
        btnTitulo.addActionListener(e -> {
            boolean estaAberto = painelSub.isVisible();

            // Fecha todos os menus
            fecharTodosMenus();

            // Se estava fechado, abre ele
            if (!estaAberto) {
                painelSub.setVisible(true);
                btnTitulo.setText(titulo + "  ▼");
                btnTitulo.setForeground(Color.WHITE);
            }

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
         switch (menu) {
             case "👤  Clientes" -> {
                 if (subitem.equals("Cadastrar")) navegarPara(new ClienteCadastroPanel());
                 if (subitem.equals("Consultar")) navegarPara(ClienteConsultaPanel.criar(() ->
                         navegarPara(new ClienteCadastroPanel())));
             }
         }
    }

    private JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(55, 60, 65));
        sep.setMaximumSize(new Dimension(210, 1));
        return sep;
    }
}