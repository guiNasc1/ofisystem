package com.ofisystem.ui.panels.relatorio;

import Enums.TipoMovimentacao;
import com.ofisystem.controle.estoque.EstoqueController;
import com.ofisystem.entidade.Estoque;
import com.ofisystem.entidade.MovimentacaoEstoque;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RelatorioEstoquePanel extends AbstractRelatorioPanel {

    private static final EstoqueController CONTROLLER = new EstoqueController();
    private static final String[] TIPOS = {"Posição atual do estoque", "Peças abaixo do mínimo", "Movimentações no período"};

    private JComboBox<String> cbTipo;
    private JTextField txtCategoria;

    @Override
    protected String titulo() {
        return "Relatório de Estoque";
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        cbTipo = new JComboBox<>(TIPOS);
        txtCategoria = criarCampo(60);
        adicionarCampo(painel, gbc, 0, 0, "Relatório:", cbTipo);
        adicionarCampo(painel, gbc, 0, 1, "Categoria / peça:", txtCategoria);
        adicionarPeriodo(painel, gbc, 1, "Movimentações");
        return 2;
    }

    @Override
    protected Supplier<Resultado> consulta() {
        int tipo = cbTipo.getSelectedIndex();
        String texto = txtCategoria.getText();
        String filtroTipo = TIPOS[tipo] + (texto.isBlank() ? "" : "   |   Filtro: " + texto);

        if (tipo == 2) {
            LocalDate[] periodo = periodoObrigatorio();
            String filtroPeriodo = descricaoPeriodo("Período");
            return () -> movimentacoes(texto, periodo[0], periodo[1]).filtro(filtroTipo).filtro(filtroPeriodo);
        }
        return () -> posicao(texto, tipo == 1).filtro(filtroTipo);
    }

    private Resultado posicao(String categoria, boolean apenasBaixo) {
        List<Estoque> lista = CONTROLLER.filtrar(null, categoria, apenasBaixo, true);
        List<Object[]> linhas = new ArrayList<>();
        double valorTotal = 0;
        long itens = 0;
        for (Estoque e : lista) {
            double valor = e.getQuantidade() * e.getPeca().getValorUnitario();
            linhas.add(new Object[]{e.getPeca().getId(), e.getPeca().getNome(), Formatador.texto(e.getPeca().getCategoria()),
                    e.getQuantidade(), e.getQuantidadeMinima(), Formatador.moeda(e.getPeca().getValorUnitario()),
                    Formatador.moeda(valor), e.isEstoqueBaixo() ? "BAIXO" : "OK"});
            valorTotal += valor;
            itens += e.getQuantidade();
        }
        return new Resultado(new String[]{"ID", "Peça", "Categoria", "Qtd.", "Mínimo", "Valor unit.", "Valor em estoque", "Situação"}, linhas)
                .resumo(itens + " unidade(s) em estoque  ·  Valor total: " + Formatador.moeda(valorTotal));
    }

    private Resultado movimentacoes(String peca, LocalDate de, LocalDate ate) {
        List<MovimentacaoEstoque> lista = CONTROLLER.filtrarMovimentacoes(peca, null, de, ate);
        List<Object[]> linhas = new ArrayList<>();
        long entradas = 0, saidas = 0;
        for (MovimentacaoEstoque m : lista) {
            linhas.add(new Object[]{Formatador.dataHora(m.getData()), m.getPeca().toString(), m.getTipo().getDescricao(),
                    m.getQuantidade(), m.getSaldo(), m.getDocumento(),
                    Formatador.texto(m.getObservacao()), Formatador.texto(m.getUsuario())});
            if (m.getTipo() == TipoMovimentacao.ENTRADA) entradas += m.getQuantidade();
            else saidas += m.getQuantidade();
        }
        return new Resultado(new String[]{"Data", "Peça", "Tipo", "Qtd.", "Saldo", "Documento", "Motivo", "Usuário"}, linhas)
                .resumo("Entradas: " + entradas + " un.  ·  Saídas: " + saidas + " un.");
    }
}
