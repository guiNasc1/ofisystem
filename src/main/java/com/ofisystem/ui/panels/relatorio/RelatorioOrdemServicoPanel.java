package com.ofisystem.ui.panels.relatorio;

import Enums.StatusOS;
import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RelatorioOrdemServicoPanel extends AbstractRelatorioPanel {

    private static final OrdemServicoController CONTROLLER = new OrdemServicoController();

    private JComboBox<Object> cbStatus;
    private JTextField txtCliente;

    @Override
    protected String titulo() {
        return "Relatório de Ordens de Serviço";
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        cbStatus = new JComboBox<>();
        cbStatus.addItem("Todos");
        for (StatusOS s : StatusOS.values()) {
            cbStatus.addItem(s);
        }
        txtCliente = criarCampo(100);
        adicionarPeriodo(painel, gbc, 0, "Abertura");
        adicionarCampo(painel, gbc, 1, 0, "Status:", cbStatus);
        adicionarCampo(painel, gbc, 1, 1, "Cliente:", txtCliente);
        return 2;
    }

    @Override
    protected Supplier<Resultado> consulta() {
        LocalDate de = dataDe();
        LocalDate ate = dataAte();
        StatusOS status = cbStatus.getSelectedItem() instanceof StatusOS s ? s : null;
        String cliente = txtCliente.getText();
        String filtroPeriodo = descricaoPeriodo("Abertura");
        String filtroStatus = "Status: " + cbStatus.getSelectedItem() + (cliente.isBlank() ? "" : "   |   Cliente contém: " + cliente);

        return () -> {
            List<OrdemServico> lista = CONTROLLER.filtrar(null, cliente, null, status, false, de, ate);
            List<Object[]> linhas = new ArrayList<>();
            Map<StatusOS, Integer> porStatus = new EnumMap<>(StatusOS.class);
            double total = 0;
            for (OrdemServico os : lista) {
                linhas.add(new Object[]{
                        os.getNumero(),
                        Formatador.data(os.getDataAbertura()),
                        Formatador.data(os.getDataConclusao()),
                        os.getCliente().getNome(),
                        os.isPossuiVeiculo() ? Formatador.texto(os.getVeiculoPlaca()) : "",
                        os.getStatus().getDescricao(),
                        Formatador.moeda(os.getValorServicos()),
                        Formatador.moeda(os.getValorPecas()),
                        Formatador.moeda(os.getDesconto()),
                        Formatador.moeda(os.getValorTotal())
                });
                porStatus.merge(os.getStatus(), 1, Integer::sum);
                if (os.getStatus() != StatusOS.CANCELADA) total += os.getValorTotal();
            }

            StringBuilder contagem = new StringBuilder();
            porStatus.forEach((s, qtd) -> contagem.append(contagem.length() == 0 ? "" : "  ·  ").append(s.getDescricao()).append(": ").append(qtd));

            Resultado r = new Resultado(new String[]{"Nº", "Abertura", "Conclusão", "Cliente", "Placa", "Status",
                    "Serviços", "Peças", "Desconto", "Total"}, linhas)
                    .filtro(filtroPeriodo)
                    .filtro(filtroStatus);
            if (contagem.length() > 0) r.resumo(contagem.toString());
            return r.resumo("Total (sem canceladas): " + Formatador.moeda(total));
        };
    }
}
