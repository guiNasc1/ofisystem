package com.ofisystem.ui.panels.cliente;

import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.dao.cliente.ClienteDAO;
import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class ClienteConsultaPanel extends AbstractPanel {

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtEndereco;
    private ClienteDAO clienteDAO;

    public ClienteConsultaPanel() throws ParseException {
    }

    @Override
    protected void inicializarTela() {
        clienteDAO = new ClienteDAO();
        add(criarTexto("Consultar Cliente", 13), BorderLayout.NORTH);
    }

}