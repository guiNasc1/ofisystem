package com.ofisystem.ui;

import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.ui.style.CardPanel;
import com.ofisystem.ui.style.PainelFundo;

import javax.swing.*;
import java.awt.*;

public class CadastroUsuarioFrame {

    private JDialog dialog;
    private JTextField txtUsuario;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;

    private Runnable aoConcluir;
    private UsuarioDAO usuarioDAO;

    private GradientPaint gradientPaint;

    public CadastroUsuarioFrame(JFrame parent, Runnable aoConcluir) {
        this.usuarioDAO = new UsuarioDAO();
        this.aoConcluir = aoConcluir;
        construirTela(parent);
    }

    public CadastroUsuarioFrame(JFrame parent) {
        this.usuarioDAO = new UsuarioDAO();
        construirTela(parent);
    }

    public void exibir() {
        dialog.setVisible(true);
    }

    private void construirTela(JFrame parent) {
        dialog = new JDialog(parent, "Criar conta", true);
        dialog.setSize(560, 500);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        CardPanel painel = new CardPanel(0,0.88f);
        painel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 20, 4, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        ImageIcon logo = new ImageIcon(getClass()
                .getResource("/images/logofundo.png"));

        Image imgRedimensionada = logo.getImage().getScaledInstance(150,110,Image.SCALE_SMOOTH);
        JLabel logoIcon = new JLabel(new ImageIcon(imgRedimensionada), SwingConstants.CENTER);
        painel.add(logoIcon, gbc);
        gbc.gridwidth = 1;

        JLabel titulo = new JLabel("Criar conta", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        painel.add(titulo, gbc);
        gbc.gridwidth = 1;

        // Usuário
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(6, 20, 4, 10);
        JLabel usuariojlabel = new JLabel("Usuário:");
        usuariojlabel.setForeground(new Color(213, 214, 255));
        usuariojlabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painel.add(usuariojlabel, gbc);

        // Usuário campo
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(6, 0, 4, 20);
        txtUsuario = new JTextField();
        txtUsuario.setPreferredSize(new Dimension(300, 38));
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Insira o usuário");
        txtUsuario.setForeground(Color.WHITE);
        painel.add(txtUsuario, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(6, 20, 4, 10);
        JLabel emailJLabel = new JLabel("Email:");
        emailJLabel.setForeground(new Color(177, 178, 202));
        emailJLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painel.add(emailJLabel, gbc);

        // Email campo
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.insets = new Insets(6, 0, 4, 20);
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(300, 38));
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmail.setBackground(new Color(255, 255, 255, 25));
        txtEmail.setForeground(Color.WHITE);
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(4,8,4,8)));
        txtEmail.putClientProperty("JTextField.placeholderText", "Insira o email");
        painel.add(txtEmail, gbc);

        // Senha
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.insets = new Insets(6, 20, 4, 10);
        JLabel senhaJLabel = new JLabel("Senha:");
        senhaJLabel.setForeground(new Color(177, 178, 202));
        senhaJLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painel.add(senhaJLabel, gbc);

        // Senha campo
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.insets = new Insets(6, 0, 4, 20);
        txtSenha = new JPasswordField();
        txtSenha.setPreferredSize(new Dimension(300, 38));
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSenha.putClientProperty("JTextField.placeholderText", "Insira a senha");
        txtSenha.setForeground(Color.WHITE);
        painel.add(txtSenha, gbc);

        // Confirmar senha
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.insets = new Insets(6, 10, 4, 20);
        JLabel confirmarSenhaJLabel = new JLabel("Confirmar Senha:");
        confirmarSenhaJLabel.setForeground(new Color(177, 178, 202));
        confirmarSenhaJLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        painel.add(confirmarSenhaJLabel, gbc);

        // Confirmar senha campo
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.insets = new Insets(6, 0, 4, 20);
        txtConfirmarSenha = new JPasswordField();
        txtConfirmarSenha.setPreferredSize(new Dimension(300, 38));
        txtConfirmarSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtConfirmarSenha.putClientProperty("JTextField.placeholderText", "Confirme a senha");
        txtConfirmarSenha.setForeground(Color.WHITE);
        painel.add(txtConfirmarSenha, gbc);

        // Botão cadastrar
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 20, 20);
        JButton btCadastrar = new JButton("Criar conta");
        btCadastrar.setBackground(new Color(33, 37, 41));
        btCadastrar.setForeground(Color.WHITE);
        btCadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btCadastrar.setFocusPainted(false);
        btCadastrar.setForeground(Color.WHITE);
        btCadastrar.setPreferredSize(new Dimension(300, 42));
        btCadastrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btCadastrar.addActionListener(e -> cadastrar());
        painel.add(btCadastrar, gbc);

        dialog.add(painel, BorderLayout.CENTER);
    }

    private void cadastrar() {
        String nome = txtUsuario.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String confirma = new String(txtConfirmarSenha.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                    "Preencha todos os campos!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(dialog,
                    "A senha deve ter pelo menos 6 caracteres!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!senha.equals(confirma)) {
            JOptionPane.showMessageDialog(dialog,
                    "As senhas não coincidem!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            txtConfirmarSenha.setText("");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);

        usuarioDAO.salvar(usuario);

        JOptionPane.showMessageDialog(dialog,
                "Conta criada com sucesso! Faça login.", "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);

        dialog.dispose();

        if(aoConcluir != null){
            aoConcluir.run();
        }
    }
}