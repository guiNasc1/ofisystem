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
    protected void inicializarTela() throws ParseException {

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

        JLabel titulo = criarTexto("Ofisystem", 16);
        titulo.setFont(new Font("Tahoma", Font.BOLD, 26);
    }

}
