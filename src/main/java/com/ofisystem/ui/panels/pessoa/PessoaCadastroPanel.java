package com.ofisystem.ui.panels.pessoa;

import Enums.TipoPessoa;
import com.ofisystem.controle.pessoa.PessoaController;
import com.ofisystem.entidade.Endereco;
import com.ofisystem.entidade.Pessoa;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.util.EnderecoDTO;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.ViaCepService;

import javax.swing.*;
import java.awt.*;

public class PessoaCadastroPanel extends AbstractPanel {

    private static final PessoaController CONTROLLER = new PessoaController();

    private JLabel lblTitulo;
    private JComboBox<TipoPessoa> cbTipo;
    private JLabel lblDocumento;
    private JTextField txtNome;
    private JTextField txtDocumento;
    private JTextField txtRgIe;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JCheckBox chkCliente;
    private JCheckBox chkFornecedor;
    private JCheckBox chkFuncionario;
    private JCheckBox chkAtivo;

    private JTextField txtCep;
    private JButton btBuscarCep;
    private JTextField txtRua;
    private JTextField txtNumero;
    private JTextField txtComplemento;
    private JTextField txtBairro;
    private JTextField txtCidade;
    private JTextField txtUf;
    private JTextArea txtObservacao;

    private Pessoa pessoa;

    public static PessoaCadastroPanel editar(Integer id) {
        PessoaCadastroPanel painel = new PessoaCadastroPanel();
        painel.executarAsync(() -> CONTROLLER.buscarCompleta(id), painel::preencher);
        return painel;
    }

    @Override
    protected void inicializarTela() {
        lblTitulo = criarTexto("Cadastrar Pessoa");
        add(lblTitulo, BorderLayout.NORTH);

        add(comRolagemVertical(criarFormulario()), BorderLayout.CENTER);

        JPanel rodape = criarRodape();
        adicionarBotao(rodape, "Voltar", COR_NEUTRA, () -> Navegador.ir(new PessoaConsultaPanel()));
        adicionarBotao(rodape, "Novo", COR_INFO, this::limpar);
        adicionarBotao(rodape, "Salvar", COR_SUCESSO, this::salvar);
        add(rodape, BorderLayout.SOUTH);
    }

    private JPanel criarFormulario() {
        JPanel painel = criarPainelFormulario();
        GridBagConstraints gbc = criarGbc();

        cbTipo = new JComboBox<>(TipoPessoa.values());
        txtNome = criarCampo(255);
        txtDocumento = criarCampo(18);
        txtRgIe = criarCampo(20);
        txtTelefone = criarCampo(15);
        txtEmail = criarCampo(255);
        txtNome.putClientProperty("JTextField.placeholderText", "Nome completo ou razão social");
        txtDocumento.putClientProperty("JTextField.placeholderText", "Somente números");
        txtTelefone.putClientProperty("JTextField.placeholderText", "(00) 00000-0000");
        txtEmail.putClientProperty("JTextField.placeholderText", "email@exemplo.com");

        chkCliente = new JCheckBox("Cliente", true);
        chkFornecedor = new JCheckBox("Fornecedor");
        chkFuncionario = new JCheckBox("Funcionário");
        chkAtivo = new JCheckBox("Ativo", true);
        JPanel papeis = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        papeis.setOpaque(false);
        for (JCheckBox c : new JCheckBox[]{chkCliente, chkFornecedor, chkFuncionario}) {
            c.setOpaque(false);
            papeis.add(c);
        }
        chkAtivo.setOpaque(false);

        adicionarCampo(painel, gbc, 0, 0, "Tipo:", cbTipo);
        adicionarCampo(painel, gbc, 0, 1, "CPF:", txtDocumento);
        lblDocumento = (JLabel) painel.getComponent(painel.getComponentCount() - 2);
        adicionarCampo(painel, gbc, 0, 2, "RG / IE:", txtRgIe);

        adicionarCampo(painel, gbc, 1, 0, "Nome:", txtNome, 2);
        adicionarCampo(painel, gbc, 1, 2, "Telefone:", txtTelefone);

        adicionarCampo(painel, gbc, 2, 0, "E-mail:", txtEmail, 2);
        adicionarCampo(painel, gbc, 2, 2, "Situação:", chkAtivo);

        adicionarCampo(painel, gbc, 3, 0, "Papéis:", papeis, 3);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel secao = criarLabel("Endereço", 16);
        secao.setFont(secao.getFont().deriveFont(Font.BOLD));
        secao.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        painel.add(secao, gbc);
        gbc.gridwidth = 1;

        txtCep = criarCampo(9);
        txtRua = criarCampo(255);
        txtNumero = criarCampo(20);
        txtComplemento = criarCampo(100);
        txtBairro = criarCampo(100);
        txtCidade = criarCampo(100);
        txtUf = criarCampo(2);
        txtCep.putClientProperty("JTextField.placeholderText", "00000-000");

        btBuscarCep = criarBotao("Buscar CEP", COR_ALERTA);
        JPanel cep = new JPanel(new BorderLayout(6, 0));
        cep.setOpaque(false);
        cep.add(txtCep, BorderLayout.CENTER);
        cep.add(btBuscarCep, BorderLayout.EAST);

        adicionarCampo(painel, gbc, 5, 0, "CEP:", cep);
        adicionarCampo(painel, gbc, 5, 1, "Rua:", txtRua, 2);
        adicionarCampo(painel, gbc, 6, 0, "Número:", txtNumero);
        adicionarCampo(painel, gbc, 6, 1, "Complemento:", txtComplemento);
        adicionarCampo(painel, gbc, 6, 2, "Bairro:", txtBairro);
        adicionarCampo(painel, gbc, 7, 0, "Cidade:", txtCidade, 2);
        adicionarCampo(painel, gbc, 7, 2, "UF:", txtUf);

        txtObservacao = criarAreaTexto(3);
        adicionarCampo(painel, gbc, 8, 0, "Observação:", comRolagem(txtObservacao), 3);

        cbTipo.addActionListener(e -> atualizarTipo());
        btBuscarCep.addActionListener(e -> buscarCep());
        txtCep.addActionListener(e -> buscarCep());
        return painel;
    }

    private void atualizarTipo() {
        boolean fisica = cbTipo.getSelectedItem() == TipoPessoa.FISICA;
        lblDocumento.setText(fisica ? "CPF:" : "CNPJ:");
    }

    private void preencher(Pessoa p) {
        if (p == null) {
            mostrarErro("Pessoa não encontrada.");
            return;
        }
        pessoa = p;
        lblTitulo.setText("Editar Pessoa — " + p.getNome());
        cbTipo.setSelectedItem(p.getTipo());
        txtNome.setText(p.getNome());
        txtDocumento.setText(Formatador.documento(p.getDocumento()));
        txtRgIe.setText(p.getRgIe());
        txtTelefone.setText(Formatador.telefone(p.getTelefone()));
        txtEmail.setText(p.getEmail());
        chkCliente.setSelected(p.isCliente());
        chkFornecedor.setSelected(p.isFornecedor());
        chkFuncionario.setSelected(p.isFuncionario());
        chkAtivo.setSelected(p.isAtivo());
        txtObservacao.setText(p.getObservacao());

        Endereco e = p.getEnderecoPrincipal();
        if (e != null) {
            txtCep.setText(Formatador.cep(e.getCep()));
            txtRua.setText(e.getRua());
            txtNumero.setText(e.getNumero());
            txtComplemento.setText(e.getComplemento());
            txtBairro.setText(e.getBairro());
            txtCidade.setText(e.getCidade());
            txtUf.setText(e.getEstado());
        }
    }

    private void salvar() {
        Pessoa p = pessoa != null ? pessoa : new Pessoa();
        p.setTipo((TipoPessoa) cbTipo.getSelectedItem());
        p.setNome(txtNome.getText().trim());
        p.setDocumento(txtDocumento.getText());
        p.setRgIe(vazioComoNulo(txtRgIe.getText()));
        p.setTelefone(txtTelefone.getText());
        p.setEmail(vazioComoNulo(txtEmail.getText()));
        p.setCliente(chkCliente.isSelected());
        p.setFornecedor(chkFornecedor.isSelected());
        p.setFuncionario(chkFuncionario.isSelected());
        p.setAtivo(chkAtivo.isSelected());
        p.setObservacao(vazioComoNulo(txtObservacao.getText()));

        boolean temEndereco = !(txtCep.getText().isBlank() && txtRua.getText().isBlank()
                && txtCidade.getText().isBlank() && txtBairro.getText().isBlank());
        if (temEndereco) {
            Endereco e = p.getEnderecoPrincipal() != null ? p.getEnderecoPrincipal() : new Endereco();
            e.setCep(txtCep.getText());
            e.setRua(vazioComoNulo(txtRua.getText()));
            e.setNumero(vazioComoNulo(txtNumero.getText()));
            e.setComplemento(vazioComoNulo(txtComplemento.getText()));
            e.setBairro(vazioComoNulo(txtBairro.getText()));
            e.setCidade(vazioComoNulo(txtCidade.getText()));
            e.setEstado(vazioComoNulo(txtUf.getText().toUpperCase()));
            p.definirEndereco(e);
        } else {
            p.definirEndereco(null);
        }

        boolean edicao = pessoa != null;
        executarAsync(() -> CONTROLLER.salvar(p), salva -> {
            mostrarSucesso(edicao ? "Cadastro atualizado com sucesso!" : "Pessoa cadastrada com sucesso!");
            if (edicao) {
                Navegador.ir(new PessoaConsultaPanel());
            } else {
                limpar();
            }
        });
    }

    private void limpar() {
        pessoa = null;
        lblTitulo.setText("Cadastrar Pessoa");
        cbTipo.setSelectedItem(TipoPessoa.FISICA);
        for (JTextField campo : new JTextField[]{txtNome, txtDocumento, txtRgIe, txtTelefone, txtEmail,
                txtCep, txtRua, txtNumero, txtComplemento, txtBairro, txtCidade, txtUf}) {
            campo.setText("");
        }
        txtObservacao.setText("");
        chkCliente.setSelected(true);
        chkFornecedor.setSelected(false);
        chkFuncionario.setSelected(false);
        chkAtivo.setSelected(true);
        txtNome.requestFocus();
    }

    private void buscarCep() {
        String cep = txtCep.getText().trim();
        if (cep.isEmpty()) {
            mostrarAviso("Digite um CEP!");
            txtCep.requestFocus();
            return;
        }
        btBuscarCep.setEnabled(false);
        btBuscarCep.setText("Buscando...");
        executarAsync(() -> {
            try {
                return ViaCepService.buscarEndereco(cep);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("Não foi possível consultar o CEP. Verifique sua conexão.");
            } finally {
                SwingUtilities.invokeLater(() -> {
                    btBuscarCep.setEnabled(true);
                    btBuscarCep.setText("Buscar CEP");
                });
            }
        }, (EnderecoDTO endereco) -> {
            txtCep.setText(Formatador.cep(cep));
            txtRua.setText(endereco.getLogradouro());
            txtBairro.setText(endereco.getBairro());
            txtCidade.setText(endereco.getLocalidade());
            txtUf.setText(endereco.getUf());
            txtNumero.requestFocus();
        });
    }

    private String vazioComoNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
