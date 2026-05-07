package com.ofisystem.ui.panels.cliente;

import com.ofisystem.entidade.Cliente;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.dao.cliente.ClienteDAO;
import com.ofisystem.util.EnderecoDTO;
import com.ofisystem.util.ViaCepService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClienteCadastroPanel extends AbstractPanel {

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;

    private JTextField txtCep;
    private JTextField txtRua;
    private JTextField txtBairro;
    private JTextField txtCidade;
    private JTextField txtEstado;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private JButton btBuscarCep;
    private JButton btSalvar;
    private JButton btVoltar;

    private Cliente clienteSelecionado;

    private ClienteDAO clienteDAO;

    @Override
    protected void inicializarTela() {
        clienteDAO = new ClienteDAO();
        add(criarTexto("Cadastrar Cliente", 13), BorderLayout.NORTH);

        add(criarFormulario());
    }

    private JPanel criarFormulario(){
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(66, 66, 66));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtNome = criarCampo(10, 255);
        txtCpf = criarCampo(10, 255);

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(criarLabel("Nome: "), gbc);
        gbc.gridx = 1;
        painel.add(txtNome, gbc);
        gbc.gridx = 2;
        painel.add(criarLabel("CPF: "), gbc);
        gbc.gridx = 3;
        painel.add(txtCpf, gbc);

        txtTelefone = criarCampo(10, 255);
        txtEmail = criarCampo(10, 255);

        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(criarLabel("Telefone: "), gbc);
        gbc.gridx = 1;
        painel.add(txtTelefone, gbc);
        gbc.gridx = 2;
        painel.add(criarLabel("Email: "), gbc);
        gbc.gridx = 3;
        painel.add(txtEmail, gbc);

        txtCep = criarCampo(10, 255);
        txtCep.setPreferredSize(new Dimension(150, 32));

        btBuscarCep = criarBotao("Buscar", new Color(195, 171, 39));
        btBuscarCep.setPreferredSize(new Dimension(100, 32));

        JPanel painelCep = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painelCep.setBackground(new Color(66, 66, 66));
        painelCep.add(txtCep);
        painelCep.add(btBuscarCep);

        gbc.gridx = 0; gbc.gridy = 3;
        painel.add(criarLabel("CEP:"), gbc);
        gbc.gridx = 1;
        painel.add(painelCep, gbc);

        txtRua = criarCampo(10, 255);
        gbc.gridx = 0; gbc.gridy = 4;
        painel.add(criarLabel("Rua:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        painel.add(txtRua, gbc);
        gbc.gridwidth = 1;

        txtBairro = criarCampo(10, 255);
        txtCidade = criarCampo(10, 255);
        gbc.gridx = 0; gbc.gridy = 5;
        painel.add(criarLabel("Bairro:"), gbc);
        gbc.gridx = 1;
        painel.add(txtBairro, gbc);
        gbc.gridx = 2;
        painel.add(criarLabel("Cidade:"), gbc);
        gbc.gridx = 3;
        painel.add(txtCidade, gbc);

        txtEstado = criarCampo(10, 255);
        txtEstado.setEditable(false); // preenchido automaticamente
        gbc.gridx = 0; gbc.gridy = 6;
        painel.add(criarLabel("Estado:"), gbc);
        gbc.gridx = 1;
        painel.add(txtEstado, gbc);

        btBuscarCep.addActionListener(e -> buscarCep());
//        btSalvar.addActionListener(e -> salvar());

        return painel;
    }

//    private void salvar(){
//        Cliente cliente = new Cliente();
//
//    }

    private void buscarCep(){

        String cep = txtCep.getText().trim();

        if(cep.isEmpty()) {
            mostrarErro("Digite um CEP!");
            txtCep.requestFocus();
            return;
        }

        SwingWorker<EnderecoDTO, Void> worker = new SwingWorker<>() {

            @Override
            protected EnderecoDTO doInBackground() throws Exception {
                btBuscarCep.setEnabled(false);
                btBuscarCep.setText("Buscando...");
                return ViaCepService.buscarEndereco(cep);
            }

            @Override
            protected void done() {
                try {
                    EnderecoDTO endereco = get();

                    txtRua.setText(endereco.getLogradouro());
                    txtBairro.setText(endereco.getBairro());
                    txtCidade.setText(endereco.getLocalidade());
                    txtEstado.setText(endereco.getUf());

                } catch (Exception ex){
                    mostrarErro(ex.getCause() != null
                    ? ex.getCause().getMessage()
                            : "Erro ao buscar CEP!");
                } finally {
                    btBuscarCep.setEnabled(true);
                    btBuscarCep.setText("Buscar");
                }
            }

        };
        btBuscarCep.setEnabled(false);
        btBuscarCep.setText("Buscando...");
        worker.execute();

    }

}