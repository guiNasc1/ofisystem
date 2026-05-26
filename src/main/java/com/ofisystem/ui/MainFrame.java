package com.ofisystem.ui;

import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Cliente;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.ui.panels.cliente.ClienteCadastroPanel;
import com.ofisystem.ui.panels.cliente.ClienteConsultaPanel;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame {

    private JFrame frame;
    private JPanel painelCentral;
    private JLabel lblNomeOficina;

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
        frame.add(crirarMenuTopBar(), BorderLayout.NORTH);
        frame.add(criarMenuLateral(), BorderLayout.WEST);

        // Painel central — só esse muda
        painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(new Color(245, 245, 245));
        frame.add(painelCentral, BorderLayout.CENTER);

        if(Sessao.getUsuariologado().getNomeOficina() == null
        || Sessao.getUsuariologado().getNomeOficina().isEmpty()) {
            mostrarTelaBoasVindas();
        } else {
            mostrarBemVindo();
        }
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
        painelCentral.removeAll();

        JLabel label = new JLabel("Bem-vindo ao sistema da oficina! 🏍️");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        painelCentral.add(label, BorderLayout.CENTER);

        painelCentral.revalidate();
        painelCentral.repaint();
    }

    private void fecharTodosMenus() {
        for (int i = 0; i < todosOsMenus.size(); i++) {
            todosOsMenus.get(i).setVisible(false);
            todosBotoesTitulo.get(i).setText(todosBotoesTitulo.get(i).getText().replace("  ▼", "  ▶")
            );
            todosBotoesTitulo.get(i).setForeground(new Color(245, 245, 245));
        }
    }

    private JPanel crirarMenuTopBar() {
        JPanel topbar = new JPanel();
        topbar.setLayout(new BorderLayout());
        topbar.setPreferredSize(new Dimension(0, 60));
        topbar.setBackground(new Color(53, 69, 99));
        topbar.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));

        // = Lado Esquerdo =

        ImageIcon logo = new ImageIcon(getClass()
                .getResource("/images/logo.png"));

        Image imgRedimensionada = logo.getImage().getScaledInstance(65,45,Image.SCALE_SMOOTH);
        JLabel logoIcon = new JLabel(new ImageIcon(imgRedimensionada), SwingConstants.CENTER);
        logoIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        esquerda.setOpaque(false);

        lblNomeOficina = new JLabel();
        lblNomeOficina.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNomeOficina.setForeground(new Color(245, 245, 245));

        String nomeSessao = Sessao.getUsuariologado().getNomeOficina();
        lblNomeOficina.setText(nomeSessao != null && !nomeSessao.isEmpty()? "Oficina - " + nomeSessao : "Nenhuma oficina vinculada");

        esquerda.add(logoIcon);
        esquerda.add(lblNomeOficina);
        topbar.add(esquerda, BorderLayout.WEST);

        // = Lado Direito =

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);

        JLabel lblDireita = new JLabel("⭐ " + Sessao.getUsuariologado().getNome());
        lblDireita.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDireita.setForeground(new Color(200, 200, 200));
        lblDireita.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));


        direita.add(lblDireita);
        topbar.add(direita, BorderLayout.EAST);

        return topbar;
    }

    // ─── Menu lateral ────────────────────────────────────────────

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel(new BorderLayout());
        menu.setPreferredSize(new Dimension(250, 0));
        menu.setBackground(new Color(27, 37, 53));

        // Separador
        JPanel painelItens = new JPanel();
        painelItens.setLayout(new BoxLayout(painelItens, BoxLayout.Y_AXIS));
        painelItens.setBackground(new Color(33, 37, 41));

        menu.add(criarSeparador());
        menu.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Itens do menu com subitens
        painelItens.add(criarSeparador());

        painelItens.add(criarMenuExpansivel("Clientes", new String[]{
                "Cadastrar", "Consultar"}));

        painelItens.add(criarMenuExpansivel("Motos", new String[]{
                "Cadastrar", "Consultar"}));

        painelItens.add(criarMenuExpansivel("Ordens de Servico", new String[]{
                "Nova Ordem", "Consultar"}));

        painelItens.add(criarMenuExpansivel("Estoque", new String[]{
                "Cadastrar Peca", "Consultar", "Movimentacao"}));

        painelItens.add(criarMenuExpansivel("Financeiro", new String[]{
                "Lancamentos", "Relatorio"}));

        painelItens.add(criarMenuExpansivel("Manuais", new String[]{
                "Catalogo"}));


        menu.add(Box.createVerticalGlue()); // empurra tudo para cima

        JPanel painelRodape = new JPanel();
        painelRodape.setLayout(new BoxLayout(painelRodape, BoxLayout.Y_AXIS));
        painelRodape.setBackground(new Color(24, 32, 43));

        JButton btSair = new JButton("Sair");
        btSair.setMaximumSize(new Dimension(210,36));
        btSair.setAlignmentX(Component.CENTER_ALIGNMENT);
        btSair.setForeground(new Color(220, 53, 69));
        btSair.setBackground(new Color(24, 32, 43));
        btSair.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btSair.setBorderPainted(false);
        btSair.setFocusPainted(false);
        btSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btSair.addActionListener(e -> sair());

        JButton btTrocarUsuario = new JButton("Trocar Usuário");
        btTrocarUsuario.setMaximumSize(new Dimension(210,36));
        btTrocarUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        btTrocarUsuario.setForeground(new Color(255, 255, 255));
        btTrocarUsuario.setBackground(new Color(24, 32, 43));
        btTrocarUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btTrocarUsuario.setBorderPainted(false);
        btTrocarUsuario.setFocusPainted(false);
        btTrocarUsuario.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btTrocarUsuario.addActionListener(e -> trocarUsuario());

        painelRodape.add(btSair);
        painelRodape.add(btTrocarUsuario);

        menu.add(painelItens, BorderLayout.NORTH);
        menu.add(painelRodape,  BorderLayout.SOUTH);

        return menu;
    }

    private void atualizarTopBar(){
        String nome = Sessao.getUsuariologado().getNomeOficina();
        lblNomeOficina.setText(nome != null ? nome : "Oficina");
    }

    private void mostrarTelaBoasVindas() {
        // cobre o painel central com a tela de boas-vindas
        painelCentral.removeAll();
        painelCentral.add(new BoasVindasPanel(() -> {
            // ao concluir — remove a tela de boas-vindas e mostra o sistema
            atualizarTopBar();
            mostrarBemVindo();
        }), BorderLayout.CENTER);
        painelCentral.revalidate();
        painelCentral.repaint();
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
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
        container.setBackground(new Color(33, 37, 41));

        // Botão do título
        JButton btnTitulo = new JButton(titulo + "  ▶");
        btnTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnTitulo.setMinimumSize(new Dimension(0, 46));
        btnTitulo.setPreferredSize(new Dimension(0, 46));
        btnTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        btnTitulo.setForeground(new Color(200, 200, 200));
        btnTitulo.setBorder(BorderFactory.createEmptyBorder(10,16,10,0));
        btnTitulo.setBackground(new Color(15, 22, 37));
        btnTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnTitulo.setBorderPainted(false);
        btnTitulo.setFocusPainted(false);
        btnTitulo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Painel dos subitens — começa escondido
        JPanel painelSub = new JPanel();
        painelSub.setLayout(new BoxLayout(painelSub, BoxLayout.Y_AXIS));
        painelSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
        painelSub.setBackground(new Color(22, 25, 29));
        painelSub.setVisible(false);

        // Registra na lista global
        todosOsMenus.add(painelSub);
        todosBotoesTitulo.add(btnTitulo);

        // Cria cada subitem
        for (String subitem : subitens) {
            JButton btnSub = new JButton("•" + subitem);
            btnSub.setHorizontalAlignment(SwingConstants.LEFT);
            btnSub.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btnSub.setMinimumSize(new Dimension(0, 38));
            btnSub.setPreferredSize(new Dimension(0, 38));
            btnSub.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));
            btnSub.setForeground(new Color(160, 160, 160));
            btnSub.setBackground(new Color(22, 25, 29));
            btnSub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            btnSub.setBorderPainted(false);
            btnSub.setFocusPainted(false);
            btnSub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

            SwingUtilities.getAncestorOfClass(JPanel.class, container).revalidate();
            SwingUtilities.getAncestorOfClass(JPanel.class, container).repaint();
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
             case "Clientes" -> {
                 if (subitem.equals("Cadastrar")) navegarPara(new ClienteCadastroPanel());
                 if (subitem.equals("Consultar")) navegarPara(ClienteConsultaPanel.criar(() ->
                         navegarPara(new ClienteCadastroPanel())));
             }
         }
    }

    private JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(55, 60, 65));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }
}