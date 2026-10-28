package com.ofisystem.ui.panels.relatorio;

import Enums.FormaPagamento;
import Enums.StatusVenda;
import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.controle.venda.VendaController;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.entidade.Venda;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RelatorioFinanceiroPanel extends AbstractRelatorioPanel {

    private static final OrdemServicoController OS_CONTROLLER = new OrdemServicoController();
    private static final VendaController VENDA_CONTROLLER = new VendaController();
    private static final String[] TIPOS = {"Recebimentos detalhados", "Resumo por forma de pagamento"};

    private JComboBox<String> cbTipo;

    @Override
    protected String titulo() {
        return "Relatório Financeiro";
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        cbTipo = new JComboBox<>(TIPOS);
        adicionarCampo(painel, gbc, 0, 0, "Relatório:", cbTipo, 2);
        adicionarPeriodo(painel, gbc, 1, "Período");
        return 2;
    }

    @Override
    protected Supplier<Resultado> consulta() {
        int tipo = cbTipo.getSelectedIndex();
        LocalDate[] periodo = periodoObrigatorio();
        String filtroTipo = "Relatório: " + TIPOS[tipo];
        String filtroPeriodo = descricaoPeriodo("Recebimentos entre");

        return () -> {
            List<Recebimento> recebimentos = new ArrayList<>();
            for (OrdemServico o : OS_CONTROLLER.concluidasNoPeriodo(periodo[0], periodo[1])) {
                recebimentos.add(new Recebimento(o.getDataConclusao(), "OS", o.getNumero(), o.getCliente().getNome(),
                        o.getFormaPagamento(), o.getValorTotal()));
            }
            for (Venda v : VENDA_CONTROLLER.filtrar(null, null, StatusVenda.FINALIZADA, periodo[0], periodo[1])) {
                String cliente = v.getCliente() == null ? "Consumidor final" : v.getCliente().getNome();
                for (FormaPagamento forma : FormaPagamento.values()) {
                    double valor = VendaController.valorRecebido(v, forma);
                    if (valor > 0.004) {
                        recebimentos.add(new Recebimento(v.getData(), "Balcão", v.getNumero(), cliente, forma, valor));
                    }
                }
            }
            recebimentos.sort(Comparator.comparing(Recebimento::data));

            Map<FormaPagamento, Double> porForma = new EnumMap<>(FormaPagamento.class);
            double semForma = 0, total = 0;
            for (Recebimento r : recebimentos) {
                if (r.forma == null) semForma += r.valor;
                else porForma.merge(r.forma, r.valor, Double::sum);
                total += r.valor;
            }

            Resultado resultado;
            if (tipo == 1) {
                List<Object[]> linhas = new ArrayList<>();
                for (Map.Entry<FormaPagamento, Double> e : porForma.entrySet()) {
                    linhas.add(new Object[]{e.getKey().getDescricao(), Formatador.moeda(e.getValue()),
                            Formatador.decimal(total == 0 ? 0 : e.getValue() * 100 / total) + "%"});
                }
                if (semForma > 0) {
                    linhas.add(new Object[]{"Não informada", Formatador.moeda(semForma),
                            Formatador.decimal(semForma * 100 / total) + "%"});
                }
                resultado = new Resultado(new String[]{"Forma de pagamento", "Valor recebido", "Participação"}, linhas);
            } else {
                List<Object[]> linhas = new ArrayList<>();
                for (Recebimento r : recebimentos) {
                    linhas.add(new Object[]{Formatador.dataHora(r.data), r.origem, r.numero, r.cliente,
                            r.forma == null ? "Não informada" : r.forma.getDescricao(), Formatador.moeda(r.valor)});
                }
                resultado = new Resultado(new String[]{"Data", "Origem", "Nº", "Cliente", "Forma", "Valor"}, linhas);
            }

            StringBuilder formas = new StringBuilder();
            porForma.forEach((f, v) -> formas.append(formas.length() == 0 ? "" : "  ·  ")
                    .append(f.getDescricao()).append(": ").append(Formatador.moeda(v)));
            if (formas.length() > 0) resultado.resumo(formas.toString());
            return resultado.filtro(filtroTipo).filtro(filtroPeriodo)
                    .resumo("Total recebido: " + Formatador.moeda(total));
        };
    }

    private record Recebimento(LocalDateTime data, String origem, String numero, String cliente,
                               FormaPagamento forma, double valor) {
    }
}
