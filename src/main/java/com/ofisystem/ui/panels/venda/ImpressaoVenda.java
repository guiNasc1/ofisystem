package com.ofisystem.ui.panels.venda;

import Enums.FormaPagamento;
import com.ofisystem.entidade.ItemVenda;
import com.ofisystem.entidade.PagamentoVenda;
import com.ofisystem.entidade.Venda;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RelatorioJasper;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.util.ArrayList;
import java.util.List;

final class ImpressaoVenda {

    private ImpressaoVenda() {
    }

    static JasperPrint comprovante(Venda venda) throws JRException {
        List<String> cabecalho = new ArrayList<>();
        cabecalho.add("Data: " + Formatador.dataHora(venda.getData())
                + "   |   Situação: " + venda.getStatus().getDescricao()
                + "   |   Atendente: " + Formatador.texto(venda.getUsuario()));
        cabecalho.add("Cliente: " + (venda.getCliente() == null ? "Consumidor final"
                : venda.getCliente().getNome() + "   |   " + Formatador.documento(venda.getCliente().getDocumento())));
        if (venda.getObservacao() != null) {
            cabecalho.add("Obs.: " + venda.getObservacao());
        }

        List<Object[]> linhas = new ArrayList<>();
        for (ItemVenda item : venda.getItens()) {
            linhas.add(new Object[]{item.isProduto() ? "Produto" : "Serviço", item.getDescricao(), item.getQuantidade(),
                    Formatador.moeda(item.getValorUnitario()), Formatador.moeda(item.getSubtotal())});
        }

        List<String> resumo = new ArrayList<>();
        resumo.add("Produtos: " + Formatador.moeda(venda.getValorProdutos())
                + "     Serviços: " + Formatador.moeda(venda.getValorServicos()));
        if (venda.getDesconto() != null && venda.getDesconto() > 0) {
            resumo.add("Desconto: " + Formatador.moeda(venda.getDesconto()));
        }
        resumo.add("TOTAL: " + Formatador.moeda(venda.getValorTotal()));
        StringBuilder formas = new StringBuilder();
        for (PagamentoVenda p : venda.getPagamentos()) {
            formas.append(formas.length() == 0 ? "" : "   ").append(p.getForma().getDescricao()).append(": ")
                    .append(Formatador.moeda(p.getValor()));
        }
        resumo.add(formas.toString());
        if (venda.getTroco() > 0 && venda.getPagamentos().stream().anyMatch(p -> p.getForma() == FormaPagamento.DINHEIRO)) {
            resumo.add("Troco: " + Formatador.moeda(venda.getTroco()));
        }

        return RelatorioJasper.gerar("Comprovante de Venda Nº " + venda.getNumero(), cabecalho,
                new String[]{"Tipo", "Descrição", "Qtd.", "Valor unit.", "Subtotal"}, linhas, resumo);
    }
}
