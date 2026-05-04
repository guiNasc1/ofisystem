package com.ofisystem.ui.panels.cliente;

import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.dao.cliente.ClienteDAO;
import javax.swing.*;
import java.awt.*;

public class ClienteCadastroPanel extends AbstractPanel {

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtEndereco;
    private ClienteDAO clienteDAO;

    @Override
    protected void inicializarTela() {
        clienteDAO = new ClienteDAO();
        add(criarTexto("Cadastrar Cliente", 13), BorderLayout.NORTH);
    }

}