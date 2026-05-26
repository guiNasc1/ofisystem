package com.ofisystem.ui;

import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import java.awt.*;

public class BoasVindasPanel extends JPanel {

    private JTextField txtNomeOficina;  // campo para inserir o nome da oficina
    private Runnable aoConcluir;        // callback executado ao salvar
    private UsuarioDAO usuarioDAO;

    public BoasVindasPanel(Runnable aoConcluir) {
        this.aoConcluir = aoConcluir;
        this.usuarioDAO = new UsuarioDAO();
        construir();
    }

    private void construir() {
        setLayout(new GridBagLayout());                         // centraliza tudo
        setBackground(new Color(22, 28, 36));                   // fundo escuro

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 60, 8, 60);

        // emoji de boas-vindas
        JLabel emoji = new JLabel("🏍️", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        gbc.insets = new Insets(0, 60, 8, 60);
        add(emoji, gbc);

        // título
        JLabel titulo = new JLabel(
                "Bem-vindo, " + Sessao.getNome() + "!",
                SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);
        gbc.insets = new Insets(0, 60, 8, 60);
        add(titulo, gbc);

        // subtítulo
        JLabel sub = new JLabel(
                "Antes de começar, dê um nome para sua oficina.",
                SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(new Color(160, 160, 160));
        gbc.insets = new Insets(0, 60, 32, 60);
        add(sub, gbc);

        // label campo
        JLabel lblNome = new JLabel("Nome da oficina:");
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNome.setForeground(new Color(200, 200, 200));
        gbc.insets = new Insets(0, 200, 4, 200);
        add(lblNome, gbc);

        // campo
        txtNomeOficina = new JTextField();
        txtNomeOficina.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtNomeOficina.setPreferredSize(new Dimension(0, 48));
        txtNomeOficina.setBackground(new Color(35, 43, 54));
        txtNomeOficina.setForeground(Color.WHITE);
        txtNomeOficina.setCaretColor(Color.WHITE);
        txtNomeOficina.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 100, 130), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        txtNomeOficina.putClientProperty(
                "JTextField.placeholderText", "Ex: Oficina do João");
        gbc.insets = new Insets(0, 200, 20, 200);
        add(txtNomeOficina, gbc);

        // botão confirmar
        JButton btConfirmar = new JButton("Confirmar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint grad = new GradientPaint(
                        0, 0, new Color(52, 73, 102),
                        0, getHeight(), new Color(66, 90, 125));
                g2.setPaint(grad);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btConfirmar.setForeground(Color.WHITE);
        btConfirmar.setContentAreaFilled(false);
        btConfirmar.setBorderPainted(false);
        btConfirmar.setFocusPainted(false);
        btConfirmar.setOpaque(false);
        btConfirmar.setPreferredSize(new Dimension(0, 52));
        btConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(0, 200, 0, 200);
        add(btConfirmar, gbc);

        // ação do botão
        btConfirmar.addActionListener(e -> salvar());

        // Enter também confirma
        txtNomeOficina.addActionListener(e -> salvar());
    }

    private void salvar() {
        String nome = txtNomeOficina.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Digite o nome da oficina!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            txtNomeOficina.requestFocus();
            return;
        }

        // salva no banco
        Sessao.getUsuariologado().setNomeOficina(nome);
        usuarioDAO.atualizarNomeOficina(Sessao.getUsuariologado());

        // executa o callback — mostra o sistema normal
        if (aoConcluir != null) aoConcluir.run();
    }

}
