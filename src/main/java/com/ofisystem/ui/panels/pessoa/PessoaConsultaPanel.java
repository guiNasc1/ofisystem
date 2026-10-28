package com.ofisystem.ui.panels.pessoa;

import com.ofisystem.controle.pessoa.PessoaController;
import com.ofisystem.entidade.Endereco;
import com.ofisystem.entidade.Pessoa;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractConsultaPanel;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class PessoaConsultaPanel extends AbstractConsultaPanel<Pessoa> {

    private static final PessoaController CONTROLLER = new PessoaController();

    private JTextField txtNome;
    private JTextField txtDocumento;
    private JTextField txtTelefone;
    private JTextField txtCidade;
    private JComboBox<String> cbPapel;
    private JComboBox<String> cbSituacao;

    @Override
    protected String titulo() {
        return "Consultar Pessoas";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Nome", "Tipo", "CPF/CNPJ", "Telefone", "E-mail", "Cidade/UF", "Papéis", "Situação"};
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        txtNome = criarCampo(100);
        txtDocumento = criarCampo(18);
        txtTelefone = criarCampo(15);
        txtCidade = criarCampo(100);
        cbPapel = new JComboBox<>(new String[]{"Todos", "Cliente", "Fornecedor", "Funcionário"});
        cbSituacao = new JComboBox<>(new String[]{"Ativos", "Inativos", "Todos"});

        adicionarCampo(painel, gbc, 0, 0, "Nome:", txtNome);
        adicionarCampo(painel, gbc, 0, 1, "CPF/CNPJ:", txtDocumento);
        adicionarCampo(painel, gbc, 0, 2, "Telefone:", txtTelefone);
        adicionarCampo(painel, gbc, 1, 0, "Cidade:", txtCidade);
        adicionarCampo(painel, gbc, 1, 1, "Papel:", cbPapel);
        adicionarCampo(painel, gbc, 1, 2, "Situação:", cbSituacao);
        return 2;
    }

    @Override
    protected Supplier<List<Pessoa>> consulta() {
        String nome = txtNome.getText();
        String documento = txtDocumento.getText();
        String telefone = txtTelefone.getText();
        String cidade = txtCidade.getText();
        String papel = (String) cbPapel.getSelectedItem();
        Boolean ativo = switch (cbSituacao.getSelectedIndex()) {
            case 0 -> true;
            case 1 -> false;
            default -> null;
        };
        return () -> CONTROLLER.filtrar(nome, documento, telefone, cidade, papel, ativo);
    }

    @Override
    protected Object[] linha(Pessoa p) {
        Endereco e = p.getEnderecoPrincipal();
        String cidade = e == null || e.getCidade() == null ? "" : e.getCidade() + (e.getEstado() == null ? "" : "/" + e.getEstado());
        return new Object[]{
                p.getNome(),
                p.getTipo().getDescricao(),
                Formatador.documento(p.getDocumento()),
                Formatador.telefone(p.getTelefone()),
                Formatador.texto(p.getEmail()),
                cidade,
                p.getPapeis(),
                p.isAtivo() ? "Ativo" : "Inativo"
        };
    }

    @Override
    protected void aposMontarTela() {
        larguraColuna(0, 220);
        larguraColuna(4, 180);
    }

    @Override
    protected void montarBotoes(JPanel painel) {
        adicionarBotao(painel, "Nova Pessoa", COR_SUCESSO, () -> Navegador.ir(new PessoaCadastroPanel()));
        adicionarBotao(painel, "Editar", COR_INFO, this::editar);
        adicionarBotao(painel, "Excluir", COR_PERIGO, this::excluir);
    }

    @Override
    protected void aoDuploClique(Pessoa registro) {
        Navegador.ir(PessoaCadastroPanel.editar(registro.getId()));
    }

    @Override
    protected String colunaEtiqueta() {
        return "Situação";
    }

    @Override
    protected Tema.Tom corLinha(Pessoa registro) {
        return registro.isAtivo() ? Tema.Tom.SUCESSO : Tema.Tom.INATIVO;
    }

    private void editar() {
        Pessoa p = selecionado();
        if (p != null) {
            Navegador.ir(PessoaCadastroPanel.editar(p.getId()));
        }
    }

    private void excluir() {
        Pessoa p = selecionado();
        if (p != null && confirmar("Deseja excluir " + p.getNome() + "?")) {
            executarAsync(() -> CONTROLLER.excluir(p), "Pessoa excluída com sucesso!", this::pesquisar);
        }
    }
}
