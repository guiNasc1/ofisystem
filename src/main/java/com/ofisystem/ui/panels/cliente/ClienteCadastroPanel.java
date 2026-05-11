package com.ofisystem.ui.panels.cliente;

import com.ofisystem.entidade.Cliente;
import com.ofisystem.entidade.Endereco;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.dao.cliente.ClienteDAO;
import com.ofisystem.util.EnderecoDTO;
import com.ofisystem.util.ViaCepService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class ClienteCadastroPanel extends AbstractPanel {

    private JTextField txtNome;
    private JFormattedTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;

    private JTextField txtCep;
    private JTextField txtRua;
    private JTextField txtBairro;
    private JTextField txtCidade;
    private JTextField txtEstado;
    private JTextField txtNumero;

    private JButton btBuscarCep;
    private JButton btSalvar;

    private ClienteDAO clienteDAO;

    public ClienteCadastroPanel() throws ParseException {
    }

    @Override
    protected void inicializarTela() {
        try {
            clienteDAO = new ClienteDAO();
            add(criarTexto("Cadastrar Cliente", 13), BorderLayout.NORTH);
            add(criarFormulario());
        }catch (ParseException e){
            mostrarErro("Erro ao inicializar tela: " + e.getMessage());
        }
    }

    private JPanel criarFormulario() throws ParseException {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(188, 188, 188, 255));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtNome = criarCampo(20, 255);
        txtEmail = criarCampo(20, 255);

        MaskFormatter maskCpf = new MaskFormatter("###.###.###-##");
        maskCpf.setPlaceholderCharacter('_');
        txtCpf = criarCampoFormatado(maskCpf, 20, 255);

        MaskFormatter maskTelefone = new MaskFormatter("(##)#####-####");
        maskTelefone.setPlaceholderCharacter('_');
        txtTelefone = criarCampoFormatado(maskTelefone, 20, 255);

        gbc.gridx = 1; gbc.gridy = 1;
        painel.add(criarLabel("Nome: ", 15), gbc);
        gbc.gridx = 2;
        painel.add(txtNome, gbc);

        gbc.gridx = 3;
        painel.add(criarLabel("CPF: ", 15), gbc);
        gbc.gridx = 4;
        painel.add(txtCpf, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        painel.add(criarLabel("Telefone: ", 15), gbc);
        gbc.gridx = 2;
        painel.add(txtTelefone, gbc);

        gbc.gridx = 3;
        painel.add(criarLabel("Email: ", 15), gbc);
        gbc.gridx = 4;
        painel.add(txtEmail, gbc);

        txtCep = criarCampo(20, 255);
        txtRua = criarCampo(20, 255);
        txtBairro = criarCampo(20, 255);
        txtCidade = criarCampo(20, 255);
        txtEstado = criarCampo(20, 255);
        txtNumero = criarCampo(20, 255);

        gbc.gridx = 1; gbc.gridy = 3;
        painel.add(criarLabel("CEP:", 15), gbc);
        gbc.gridx = 2; gbc.gridy = 3;
        painel.add(txtCep, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        btBuscarCep = criarBotao("Buscar", new Color(195, 171, 39));
        gbc.gridx = 4; gbc.gridy = 3;
        painel.add(btBuscarCep, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        painel.add(criarLabel("Rua:", 15), gbc);
        gbc.gridx = 2; gbc.gridy = 4;
        painel.add(txtRua, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        painel.add(criarLabel("Bairro:", 15), gbc);
        gbc.gridx = 2; gbc.gridy = 5;
        painel.add(txtBairro, gbc);

        gbc.gridx = 3; gbc.gridy = 5;
        painel.add(criarLabel("Cidade:", 15), gbc);
        gbc.gridx = 4; gbc.gridy = 5;
        painel.add(txtCidade, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        painel.add(criarLabel("Estado:", 15), gbc);
        gbc.gridx = 2; gbc.gridy = 6;
        painel.add(txtEstado, gbc);

        gbc.gridx = 3; gbc.gridy = 6;
        painel.add(criarLabel("Nº", 15), gbc);
        gbc.gridx = 4; gbc.gridy = 6;
        painel.add(txtNumero, gbc);

        btSalvar = criarBotao("Salvar", new Color(195, 171, 39));

        gbc.gridx = 1; gbc.gridy = 7;
        painel.add(btSalvar, gbc);

        btBuscarCep.addActionListener(e -> buscarCep());
        btSalvar.addActionListener(e -> salvar());

        return painel;
    }

    private void salvar(){


        Cliente cliente = new Cliente();

        cliente.setCliNome(txtNome.getText().trim());
        cliente.setCliCpf(txtCpf.getText().trim());
        cliente.setCliEmail(txtEmail.getText().trim());
        cliente.setCliTelefone(txtTelefone.getText().trim());

        Endereco endereco = new Endereco();

        endereco.setBairro(txtBairro.getText());
        endereco.setRua(txtRua.getText());
        endereco.setNumero(txtNumero.getText());
        endereco.setCidade(txtCidade.getText());
        endereco.setEstado(txtEstado.getText());

        endereco.setCliente(cliente);
        cliente.getEndereco().add(endereco);

        clienteDAO.salvar(cliente);
        mostrarSucesso("Cliente cadastrado com sucesso!");
        limpar();

    }

    public void limpar(){
        txtNome.setText("");
        txtCpf.setValue(null);
        txtTelefone.setText("");
        txtEmail.setText("");
        txtCep.setText("");
        txtRua.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        txtEstado.setText("");
        txtNumero.setText("");
        txtNome.requestFocus();
    }

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