package com.ofisystem.ui.panels.relatorio;

import Enums.StatusVenda;
import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.controle.venda.VendaController;
import com.ofisystem.dao.RelatorioDAO;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.entidade.Venda;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class RelatorioVendasPanel extends AbstractRelatorioPanel {

    private static final OrdemServicoController OS_CONTROLLER = new OrdemServicoController();
    private static final VendaController VENDA_CONTROLLER = new VendaController();
    private static final RelatorioDAO RELATORIO_DAO = new RelatorioDAO();
    private static final String[] ORIGENS = {"Balcão + Ordens de serviço", "Somente venda de balcão", "Somente ordens de serviço"};
    private static final String[] AGRUPAMENTOS = {"Por documento", "Por produto", "Por serviço", "Por cliente"};

    private JComboBox<String> cbOrigem;
    private JComboBox<String> cbAgrupamento;

    @Override
    protected String titulo() {
        return "Relatório de Vendas";
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        cbOrigem = new JComboBox<>(ORIGENS);
        cbAgrupamento = new JComboBox<>(AGRUPAMENTOS);
        adicionarPeriodo(painel, gbc, 0, "Período");
        adicionarCampo(painel, gbc, 1, 0, "Origem:", cbOrigem);
        adicionarCampo(painel, gbc, 1, 1, "Agrupar:", cbAgrupamento);
        return 2;
    }

    @Override
    protected Supplier<Resultado> consulta() {
        LocalDate[] periodo = periodoObrigatorio();
        int origem = cbOrigem.getSelectedIndex();
        boolean balcao = origem != 2;
        boolean os = origem != 1;
        int agrupamento = cbAgrupamento.getSelectedIndex();
        String filtroPeriodo = descricaoPeriodo("Período (OS pela data de conclusão)");
        String filtroOrigem = "Origem: " + ORIGENS[origem] + "   |   Agrupamento: " + AGRUPAMENTOS[agrupamento];

        return () -> {
            Resultado r = switch (agrupamento) {
                case 1 -> porProduto(periodo[0], periodo[1], os, balcao);
                case 2 -> porServico(periodo[0], periodo[1], os, balcao);
                case 3 -> porCliente(periodo[0], periodo[1], os, balcao);
                default -> porDocumento(periodo[0], periodo[1], os, balcao);
            };
            return r.filtro(filtroPeriodo).filtro(filtroOrigem);
        };
    }

    private Resultado porDocumento(LocalDate de, LocalDate ate, boolean incluirOs, boolean incluirBalcao) {
        List<Documento> documentos = new ArrayList<>();
        if (incluirOs) {
            for (OrdemServico o : OS_CONTROLLER.concluidasNoPeriodo(de, ate)) {
                documentos.add(new Documento("OS", o.getNumero(), o.getDataConclusao(), o.getCliente().getNome(),
                        o.getValorPecas(), o.getValorServicos(), o.getDesconto(), o.getValorTotal()));
            }
        }
        if (incluirBalcao) {
            for (Venda v : VENDA_CONTROLLER.filtrar(null, null, StatusVenda.FINALIZADA, de, ate)) {
                documentos.add(new Documento("Balcão", v.getNumero(), v.getData(),
                        v.getCliente() == null ? "Consumidor final" : v.getCliente().getNome(),
                        v.getValorProdutos(), v.getValorServicos(), v.getDesconto(), v.getValorTotal()));
            }
        }
        documentos.sort(Comparator.comparing(Documento::data));

        List<Object[]> linhas = new ArrayList<>();
        double produtos = 0, servicos = 0, descontos = 0, total = 0;
        for (Documento d : documentos) {
            linhas.add(new Object[]{d.origem, d.numero, Formatador.data(d.data), d.cliente, Formatador.moeda(d.produtos),
                    Formatador.moeda(d.servicos), Formatador.moeda(d.desconto), Formatador.moeda(d.total)});
            produtos += d.produtos;
            servicos += d.servicos;
            descontos += d.desconto;
            total += d.total;
        }
        return new Resultado(new String[]{"Origem", "Nº", "Data", "Cliente", "Produtos", "Serviços", "Desconto", "Total"}, linhas)
                .resumo("Produtos: " + Formatador.moeda(produtos) + "  ·  Serviços: " + Formatador.moeda(servicos)
                        + "  ·  Descontos: " + Formatador.moeda(descontos))
                .resumo("Faturamento: " + Formatador.moeda(total)
                        + "  ·  Ticket médio: " + Formatador.moeda(documentos.isEmpty() ? 0 : total / documentos.size()));
    }

    private Resultado porProduto(LocalDate de, LocalDate ate, boolean os, boolean balcao) {
        List<Object[]> linhas = new ArrayList<>();
        double total = 0;
        long quantidade = 0;
        for (Object[] r : RELATORIO_DAO.vendasPorProduto(de, ate, os, balcao)) {
            double valor = ((Number) r[3]).doubleValue();
            linhas.add(new Object[]{r[0], r[1], r[2], Formatador.moeda(valor)});
            total += valor;
            quantidade += ((Number) r[2]).longValue();
        }
        return new Resultado(new String[]{"ID", "Produto", "Quantidade", "Valor total"}, linhas)
                .resumo(quantidade + " unidade(s) vendida(s)  ·  Total em produtos: " + Formatador.moeda(total));
    }

    private Resultado porServico(LocalDate de, LocalDate ate, boolean os, boolean balcao) {
        List<Object[]> linhas = new ArrayList<>();
        double total = 0;
        for (Object[] r : RELATORIO_DAO.vendasPorServico(de, ate, os, balcao)) {
            double valor = ((Number) r[2]).doubleValue();
            linhas.add(new Object[]{r[0], r[1], Formatador.moeda(valor)});
            total += valor;
        }
        return new Resultado(new String[]{"Serviço", "Quantidade", "Valor total"}, linhas)
                .resumo("Total em serviços: " + Formatador.moeda(total));
    }

    private Resultado porCliente(LocalDate de, LocalDate ate, boolean os, boolean balcao) {
        List<Object[]> linhas = new ArrayList<>();
        double total = 0;
        for (Object[] r : RELATORIO_DAO.vendasPorCliente(de, ate, os, balcao)) {
            double valor = ((Number) r[2]).doubleValue();
            long qtd = ((Number) r[1]).longValue();
            linhas.add(new Object[]{r[0], qtd, Formatador.moeda(valor), Formatador.moeda(valor / qtd)});
            total += valor;
        }
        return new Resultado(new String[]{"Cliente", "Qtd. vendas", "Valor total", "Ticket médio"}, linhas)
                .resumo("Faturamento: " + Formatador.moeda(total));
    }

    private record Documento(String origem, String numero, LocalDateTime data, String cliente,
                             double produtos, double servicos, double desconto, double total) {
    }
}
