package com.ofisystem.ui;

import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Usuario;

import javax.swing.*;
import java.awt.*;

public class CadastroUsuarioFrame {

    private JDialog dialog;
    private JTextField txtUsuario;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    private UsuarioDAO usuarioDAO;

    private GradientPaint gradientPaint;

    public CadastroUsuarioFrame(JFrame parent) {
        usuarioDAO = new UsuarioDAO();
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

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(245, 245, 245));
        painel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 4, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel titulo = new JLabel("Criar conta", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.insets = new Insets(0, 0, 20, 0);
        painel.add(titulo, gbc);

        // Usuário
        gbc.insets = new Insets(6, 0, 4, 0);
        painel.add(criarLabel("Usuário"), gbc);
        txtUsuario = new JTextField();
        txtUsuario.setPreferredSize(new Dimension(300, 38));
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Insira o usuário");
        painel.add(txtUsuario, gbc);

        // Email
        gbc.insets = new Insets(6, 0, 4, 0);
        painel.add(criarLabel("Email:"), gbc);
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(300, 38));
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmail.setBackground(new Color(255, 255, 255, 25));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(4,8,4,8)));
        txtEmail.putClientProperty("JTextField.placeholderText", "Insira o email");
        painel.add(txtEmail, gbc);

        // Senha
        painel.add(criarLabel("Senha"), gbc);
        txtSenha = new JPasswordField();
        txtSenha.setPreferredSize(new Dimension(300, 38));
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSenha.putClientProperty("JTextField.placeholderText", "Insira a senha");
        painel.add(txtSenha, gbc);

        // Confirmar senha
        painel.add(criarLabel("Confirmar senha"), gbc);
        txtConfirmarSenha = new JPasswordField();
        txtConfirmarSenha.setPreferredSize(new Dimension(300, 38));
        txtConfirmarSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtConfirmarSenha.putClientProperty("JTextField.placeholderText", "Confirme a senha");
        painel.add(txtConfirmarSenha, gbc);

        // Botão cadastrar
        gbc.insets = new Insets(20, 0, 0, 0);
        JButton btCadastrar = new JButton("Criar conta");
        btCadastrar.setBackground(new Color(33, 37, 41));
        btCadastrar.setForeground(Color.WHITE);
        btCadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btCadastrar.setFocusPainted(false);
        btCadastrar.setBorderPainted(false);
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
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }
}