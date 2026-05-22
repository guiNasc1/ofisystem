package com.ofisystem.ui;

import com.ofisystem.Main;
import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;

public class LoginFrame extends AbstractPanel {

    private JFrame frame;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCadastrar;
    private UsuarioDAO usuarioDAO;
    private JDialog dialog;

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
        txtUsuario = criarCampo(10, 255);
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
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoes UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(4,8,4,8)));
        painelLogin.add(txtPassword, gbc);

        // Botão entrar
        gbc.insets = new Insets(20,24,6,24);
        btnLogin = criarBotao("Login", Color.BLUE);
        btnLogin.setFont(new Font("Segoes UI", Font.BOLD, 10));
        btnLogin.setBackground(new Color(33, 37, 41));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(300, 42));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelLogin.add(btnLogin, gbc);

        // Botão cadastrar
        gbc.insets = new Insets(6,24,4,24);
        btnCadastrar = criarBotao("Cadastrar", Color.BLUE);
        btnCadastrar.setFont(new Font("Segoes UI", Font.BOLD, 10));
        btnCadastrar.setBackground(new Color(33, 37, 41));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setBorderPainted(false);
        btnCadastrar.setPreferredSize(new Dimension(300, 42));
        btnCadastrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelLogin.add(btnCadastrar, gbc);

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

        return painelLogin;
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
                        JOptionPane.showMessageDialog(dialog,
                                "Usuário ou senha incorretos!", "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
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
        // Verifica se já existe usuário cadastrado
        if (usuarioDAO.existeUsuario()) {
            JOptionPane.showMessageDialog(frame,
                    "Já existe um usuário cadastrado no sistema!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new CadastroUsuarioFrame(frame).exibir();
    }

}
