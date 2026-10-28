package com.ofisystem.ui;

import com.ofisystem.controle.usuario.UsuarioController;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.util.RegraNegocioException;
import com.ofisystem.ui.style.Botao;
import com.ofisystem.ui.style.Icones;
import com.ofisystem.ui.style.Superficie;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;

import javax.swing.*;
import java.awt.*;

public class CadastroUsuarioFrame {

    private JDialog dialog;
    private JTextField txtUsuario;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    private JLabel lblErro;
    private Botao btCadastrar;

    private final Runnable aoConcluir;
    private final UsuarioController usuarioController;

    public CadastroUsuarioFrame(JFrame parent, Runnable aoConcluir) {
        this.usuarioController = new UsuarioController();
        this.aoConcluir = aoConcluir;
        construirTela(parent);
    }

    public CadastroUsuarioFrame(JFrame parent) {
        this(parent, null);
    }

    public void exibir() {
        dialog.setVisible(true);
    }

    private void construirTela(JFrame parent) {
        dialog = new JDialog(parent, "Criar conta", true);
        dialog.setLayout(new BorderLayout());

        Superficie cartao = new Superficie(new GridBagLayout(), 24, 32);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(Texto.titulo("Criar conta"), gbc);
        gbc.insets = new Insets(0, 0, 24, 0);
        cartao.add(Texto.apoio("Cada pessoa da oficina deve ter o próprio usuário."), gbc);

        txtUsuario = campo(new JTextField(), Icones.Tipo.USUARIO, "Nome de usuário para entrar");
        txtEmail = campo(new JTextField(), null, "email@exemplo.com");
        txtSenha = (JPasswordField) campo(new JPasswordField(), Icones.Tipo.CADEADO, "Mínimo de 6 caracteres");
        txtConfirmarSenha = (JPasswordField) campo(new JPasswordField(), Icones.Tipo.CADEADO, "Repita a senha");
        txtSenha.putClientProperty("JPasswordField.showRevealButton", true);

        adicionar(cartao, gbc, "Usuário", txtUsuario);
        adicionar(cartao, gbc, "E-mail", txtEmail);
        adicionar(cartao, gbc, "Senha", txtSenha);
        adicionar(cartao, gbc, "Confirmar senha", txtConfirmarSenha);

        lblErro = Texto.erro();
        gbc.insets = new Insets(0, 0, 12, 0);
        cartao.add(lblErro, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        Botao btCancelar = new Botao("Cancelar", Botao.Estilo.DISCRETO);
        btCadastrar = new Botao("Criar conta", Botao.Estilo.PRINCIPAL);
        botoes.add(btCancelar);
        botoes.add(btCadastrar);
        gbc.insets = new Insets(0, 0, 0, 0);
        cartao.add(botoes, gbc);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        fundo.add(cartao, BorderLayout.CENTER);
        dialog.add(fundo, BorderLayout.CENTER);
        dialog.getRootPane().setDefaultButton(btCadastrar);

        btCancelar.addActionListener(e -> dialog.dispose());
        btCadastrar.addActionListener(e -> cadastrar());

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);
    }

    private JTextField campo(JTextField campo, Icones.Tipo icone, String exemplo) {
        campo.setPreferredSize(new Dimension(360, 40));
        campo.putClientProperty("JTextField.placeholderText", exemplo);
        if (icone != null) {
            campo.putClientProperty("JTextField.leadingIcon", Icones.de(icone, 18, Tema::tintaSuave));
        }
        return campo;
    }

    private void adicionar(JPanel painel, GridBagConstraints gbc, String rotulo, JComponent campo) {
        gbc.insets = new Insets(0, 0, 6, 0);
        painel.add(Texto.rotulo(rotulo), gbc);
        gbc.insets = new Insets(0, 0, 16, 0);
        painel.add(campo, gbc);
    }

    private void cadastrar() {
        String nome = txtUsuario.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String confirma = new String(txtConfirmarSenha.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            lblErro.setText("Preencha usuário, e-mail e senha.");
            return;
        }
        if (senha.length() < 6) {
            lblErro.setText("A senha precisa ter pelo menos 6 caracteres.");
            txtSenha.requestFocusInWindow();
            return;
        }
        if (!senha.equals(confirma)) {
            lblErro.setText("As senhas não são iguais.");
            txtConfirmarSenha.setText("");
            txtConfirmarSenha.requestFocusInWindow();
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);

        try {
            UsuarioController.validarCadastro(usuario);
        } catch (RegraNegocioException e) {
            lblErro.setText(e.getMessage());
            return;
        }

        lblErro.setText(" ");
        btCadastrar.setEnabled(false);
        btCadastrar.setText("Criando...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                usuarioController.cadastrar(usuario);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(dialog, "Conta criada com perfil "
                                    + usuario.getPerfil().getDescricao() + ". Agora é só entrar.", "Conta criada",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    if (aoConcluir != null) aoConcluir.run();
                } catch (Exception e) {
                    btCadastrar.setEnabled(true);
                    btCadastrar.setText("Criar conta");
                    Throwable causa = e;
                    while (causa.getCause() != null && !(causa instanceof RegraNegocioException)) {
                        causa = causa.getCause();
                    }
                    lblErro.setText(causa instanceof RegraNegocioException ? causa.getMessage()
                            : "Não foi possível criar a conta. Verifique a conexão.");
                }
            }
        }.execute();
    }
}
