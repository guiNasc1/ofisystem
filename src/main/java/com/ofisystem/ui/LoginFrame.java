package com.ofisystem.ui;

import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.ui.panels.AbstractPanel;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class LoginFrame extends AbstractPanel {

    private JFrame frame;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCadastrar;
    private UsuarioDAO usuarioDAO;

    public LoginFrame() throws ParseException {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void inicializarTela(){
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Logo
        JLabel logo = new JLabel("🏍️", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 52));
        painelLogin.add(logo, gbc);

        // Titulo
        JLabel titulo = criarTexto("Ofisystem");
        titulo.setFont(new Font("Segoes UI", Font.BOLD, 26));
        titulo.setForeground(new Color(33, 37, 41));
        painelLogin.add(titulo, gbc);

        // Subtitulo
        JLabel subtitulo = new JLabel("Sistema de Gestão de Oficina", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(Color.GRAY);
        gbc.insets = new Insets(0,24,24,24);
        painelLogin.add(subtitulo, gbc);

        // Texto usuário
        gbc.insets = new Insets(6,24,4,24);
        JLabel usuariojlabel = criarTexto("Usuário:");
        usuariojlabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        painelLogin.add(usuariojlabel, gbc);

        // Campo usuário
        JTextField txtUsuario = criarCampo(10, 255);
        txtUsuario.setFont(new Font("Segoes UI", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(300, 40));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        painelLogin.add(txtUsuario, gbc);

        // Texto senha
        gbc.insets = new Insets(12, 24, 4, 24);
        JLabel senhajlabel = criarTexto("Senha:");
        senhajlabel.setFont(new Font("Segoes UI", Font.PLAIN, 16));
        painelLogin.add(senhajlabel, gbc);

        // Campo senha
        gbc.insets = new Insets(6,24,4,24);
        JPasswordField txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Segoes UI", Font.PLAIN, 14));
        txtSenha.setPreferredSize(new Dimension(300, 40));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(4,8,4,8)));
        painelLogin.add(txtSenha, gbc);

        //Botão entrar
        gbc.insets = new Insets(20,24,6,24);
        JButton btLogin = criarBotao("Login", Color.BLUE);
        btLogin.setFont(new Font("Segoes UI", Font.BOLD, 10));
        btLogin.setBackground(new Color(33, 37, 41));
        btLogin.setForeground(Color.WHITE);
        btLogin.setFocusPainted(false);
        btLogin.setBorderPainted(false);
        btLogin.setPreferredSize(new Dimension(300, 42));
        btLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelLogin.add(btLogin, gbc);

        return painelLogin;
    }

}
