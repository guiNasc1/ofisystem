package com.ofisystem.ui;

import com.ofisystem.Main;
import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.ui.style.CardPanel;
import com.ofisystem.ui.style.PainelFundo;
import com.ofisystem.ui.style.PlaceHolderTextField;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

public class LoginFrame {

    private JFrame frame;
    private PlaceHolderTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCadastrar;;

    private UsuarioDAO usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        construirTela();
    }

    public void exibir(){
        frame.setVisible(true);
    }

    private void construirTela(){
        frame = new  JFrame("Ofisystem - Login");
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        frame.add(criarPainelLogin(), BorderLayout.CENTER);
    }

    private JPanel criarPainelLogin(){
        JPanel painelLogin = new JPanel(new GridBagLayout());
        painelLogin.setBackground(new Color(245, 245, 245));

        // Painel com fundo
        PainelFundo fundo = new PainelFundo("/images/background.jpg");
        fundo.setLayout(new GridBagLayout());

        //Card central
        CardPanel cardPanel = new CardPanel(20, 0.88f);
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(77, 98, 128), 2, true));
        cardPanel.setPreferredSize(new Dimension(340, 540));
        cardPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Logo
        ImageIcon logo = new ImageIcon(getClass()
                .getResource("/images/logo.png"));

        Image imgRedimensionada = logo.getImage().getScaledInstance(150,110,Image.SCALE_SMOOTH);
        JLabel logoIcon = new JLabel(new ImageIcon(imgRedimensionada), SwingConstants.CENTER);
        cardPanel.add(logoIcon, gbc);

        // Titulo
        JLabel titulo = new JLabel("Ofisystem", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoes UI", Font.BOLD, 26));
        titulo.setForeground(new Color(177, 178, 202));
        cardPanel.add(titulo, gbc);

        // Subtitulo
        JLabel subtitulo = new JLabel("Sistema de Gestão de Oficina", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subtitulo.setForeground(new Color(127, 128, 147));
        gbc.insets = new Insets(0,24,24,24);
        cardPanel.add(subtitulo, gbc);

        // Texto usuário
        gbc.insets = new Insets(6,24,4,24);
        JLabel usuariojlabel = new JLabel("Usuário:");
        usuariojlabel.setForeground(new Color(177, 178, 202));
        usuariojlabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardPanel.add(usuariojlabel, gbc);

        // Campo usuário
        txtUsuario = new PlaceHolderTextField("Insira o usuário");
        txtUsuario.setFont(new Font("Segoes UI", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(300, 40));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        cardPanel.add(txtUsuario, gbc);

        // Texto senha
        gbc.insets = new Insets(12, 24, 4, 24);
        JLabel senhajlabel = new JLabel("Senha:");
        senhajlabel.setForeground(new Color(177, 178, 202));
        senhajlabel.setFont(new Font("Segoes UI", Font.BOLD, 16));
        cardPanel.add(senhajlabel, gbc);

        // Campo senha
        gbc.insets = new Insets(6,24,4,24);
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoes UI", Font.PLAIN, 14));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBackground(new Color(255, 255, 255, 25));
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(4,8,4,8)));
        txtPassword.putClientProperty("JTextField.placeholderText", "Insira a senha");
        cardPanel.add(txtPassword, gbc);

        // Botão entrar
        gbc.insets = new Insets(20,24,6,24);
        gbc.weightx = 1.0;
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoes UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(52, 79, 99));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorder(BorderFactory.createMatteBorder(0,0,4,0,new Color(34, 34, 34, 255)));
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(280, 45));
        btnLogin.setMinimumSize(new Dimension(280, 50));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cardPanel.add(btnLogin, gbc);

        // Botão cadastrar
        gbc.insets = new Insets(6,24,4,24);
        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setFont(new Font("Segoes UI", Font.BOLD, 16));
        btnCadastrar.setBackground(new Color(255, 255, 255, 0));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setPreferredSize(new Dimension(280, 45));
        btnCadastrar.setMinimumSize(new Dimension(280, 50));
        btnCadastrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCadastrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCadastrar.setBackground(new Color(52, 79, 99, 165));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCadastrar.setBackground(new Color(52, 79, 99, 0));
            }
        });
        cardPanel.add(btnCadastrar, gbc);

        gbc.weightx = 0;

        JLabel qtdUsuario = new JLabel("Quantidade de usuários cadastrado: " + usuarioDAO.contarUsuario(), SwingConstants.CENTER);
        qtdUsuario.setFont(new Font("Segoes UI", Font.PLAIN, 15));
        qtdUsuario.setForeground(new Color(145, 147, 158));
        cardPanel.add(qtdUsuario, gbc);

        // Ações
        btnLogin.addActionListener(e -> login());
        btnCadastrar.addActionListener(e -> abrirCadastro());

        // Pressionar Enter
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_ENTER) login();
            }
        });

        fundo.add(cardPanel);

        return fundo;
    }

    private void login() {

        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtPassword.getPassword());

        if (usuario.isEmpty() && senha.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Insira o usuário e a senha    !", "Aviso!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Insira o usuário!", "Aviso!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (senha.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Insira a senha!", "Aviso!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        txtUsuario.setEnabled(false);
        txtPassword.setEnabled(false);
        btnLogin.setEnabled(false);
        btnCadastrar.setEnabled(false);
        btnLogin.setText("Carregando...");

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {

            @Override
            protected Usuario doInBackground() {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                return usuarioDAO.autenticar(usuario, senha);
            }

            @Override
            protected void done() {
                try {
                    Usuario u = get();

                    if (u != null) {
                        Sessao.iniciar(u);
                        frame.dispose();
                        SwingUtilities.invokeLater(() -> new MainFrame().exibirFrame());
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Usuário ou senha incorretos!", "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Erro ao autenticar: " + ex.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Reabilita tudo se não trocou de usuário
                    txtUsuario.setEnabled(true);
                    txtPassword.setEnabled(true);
                    btnLogin.setEnabled(true);
                    btnCadastrar.setEnabled(true);
                    btnLogin.setText("Entrar");
                }
            }
        };
        worker.execute();
    }

    private void abrirCadastro() {
        new CadastroUsuarioFrame(frame).exibir();
    }

}
