package com.ofisystem.ui.panels.relatorio;

import com.ofisystem.controle.pessoa.PessoaController;
import com.ofisystem.entidade.Endereco;
import com.ofisystem.entidade.Pessoa;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RelatorioPessoasPanel extends AbstractRelatorioPanel {

    private static final PessoaController CONTROLLER = new PessoaController();

    private JComboBox<String> cbPapel;
    private JComboBox<String> cbSituacao;
    private JTextField txtCidade;

    @Override
    protected String titulo() {
        return "Relatório de Pessoas";
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        cbPapel = new JComboBox<>(new String[]{"Todos", "Cliente", "Fornecedor", "Funcionário"});
        cbSituacao = new JComboBox<>(new String[]{"Ativos", "Inativos", "Todos"});
        txtCidade = criarCampo(100);
        adicionarCampo(painel, gbc, 0, 0, "Papel:", cbPapel);
        adicionarCampo(painel, gbc, 0, 1, "Situação:", cbSituacao);
        adicionarCampo(painel, gbc, 0, 2, "Cidade:", txtCidade);
        return 1;
    }

    @Override
    protected Supplier<Resultado> consulta() {
        String papel = (String) cbPapel.getSelectedItem();
        String cidade = txtCidade.getText();
        Boolean ativo = switch (cbSituacao.getSelectedIndex()) {
            case 0 -> true;
            case 1 -> false;
            default -> null;
        };
        String filtro = "Papel: " + papel + "   |   Situação: " + cbSituacao.getSelectedItem()
                + (cidade.isBlank() ? "" : "   |   Cidade contém: " + cidade);

        return () -> {
            List<Pessoa> lista = CONTROLLER.filtrar(null, null, null, cidade, papel, ativo);
            List<Object[]> linhas = new ArrayList<>();
            long fisicas = 0;
            for (Pessoa p : lista) {
                Endereco e = p.getEnderecoPrincipal();
                linhas.add(new Object[]{
                        p.getNome(),
                        Formatador.documento(p.getDocumento()),
                        Formatador.telefone(p.getTelefone()),
                        Formatador.texto(p.getEmail()),
                        e == null || e.getCidade() == null ? "" : e.getCidade() + (e.getEstado() == null ? "" : "/" + e.getEstado()),
                        p.getPapeis(),
                        Formatador.data(p.getDataCadastro())
                });
                if (p.getDocumento().length() == 11) fisicas++;
            }
            return new Resultado(new String[]{"Nome", "CPF/CNPJ", "Telefone", "E-mail", "Cidade/UF", "Papéis", "Cadastro"}, linhas)
                    .filtro(filtro)
                    .resumo(fisicas + " pessoa(s) física(s)  ·  " + (lista.size() - fisicas) + " jurídica(s)");
        };
    }
}
