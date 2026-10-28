package com.ofisystem.ui.panels.os;

import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.entidade.ItemNota;
import com.ofisystem.entidade.ItemOs;
import com.ofisystem.entidade.ItemPeca;
import com.ofisystem.entidade.NotaFiscal;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.entidade.Pessoa;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RelatorioJasper;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.util.ArrayList;
import java.util.List;

final class ImpressaoOS {

    private ImpressaoOS() {
    }

    static JasperPrint ordemServico(OrdemServico os) throws JRException {
        List<String> cabecalho = new ArrayList<>(dadosCliente(os.getCliente()));
        if (os.isPossuiVeiculo()) {
            cabecalho.add("Veículo: " + os.getVeiculoDescricao() + (os.getKm() == null ? "" : "   |   KM: " + os.getKm()));
        }
        cabecalho.add("Abertura: " + Formatador.dataHora(os.getDataAbertura())
                + "   |   Previsão: " + (os.getDataPrevisao() == null ? "-" : Formatador.data(os.getDataPrevisao()))
                + "   |   Status: " + os.getStatus().getDescricao()
                + (os.getDataConclusao() == null ? "" : "   |   Finalizada em: " + Formatador.dataHora(os.getDataConclusao()))
                + (os.getFormaPagamento() == null ? "" : "   |   Pagamento: " + os.getFormaPagamento().getDescricao()));
        cabecalho.add("Defeito relatado: " + resumir(os.getDescricao()));
        if (os.getDiagnostico() != null && !os.getDiagnostico().isBlank()) {
            cabecalho.add("Diagnóstico: " + resumir(os.getDiagnostico()));
        }

        List<Object[]> linhas = new ArrayList<>();
        for (ItemOs item : os.getItensServico()) {
            linhas.add(new Object[]{"Serviço", item.getDescricao(), item.getQuantidade(),
                    Formatador.moeda(item.getValorUnitario()), Formatador.moeda(item.getSubtotal())});
        }
        for (ItemPeca item : os.getItensPeca()) {
            linhas.add(new Object[]{"Peça", item.getPeca().toString(), item.getQuantidade(),
                    Formatador.moeda(item.getValorUnitario()), Formatador.moeda(item.getSubtotal())});
        }

        List<String> resumo = new ArrayList<>();
        resumo.add("Serviços: " + Formatador.moeda(os.getValorServicos()) + "     Peças: " + Formatador.moeda(os.getValorPecas()));
        if (os.getDesconto() != null && os.getDesconto() > 0) {
            resumo.add("Desconto: " + Formatador.moeda(os.getDesconto()));
        }
        resumo.add("TOTAL: " + Formatador.moeda(os.getValorTotal()));
        resumo.add(" ");
        resumo.add("_______________________________________          Assinatura do cliente");

        return RelatorioJasper.gerar("Ordem de Serviço Nº " + os.getNumero(), cabecalho,
                new String[]{"Tipo", "Descrição", "Qtd.", "Valor unit.", "Subtotal"}, linhas, resumo);
    }

    static JasperPrint notaFiscal(NotaFiscal nota) throws JRException {
        OrdemServico os = nota.getOrdemServico();
        List<String> cabecalho = new ArrayList<>();
        cabecalho.add("Emissão: " + Formatador.dataHora(nota.getDataEmissao())
                + "   |   Referente à OS " + os.getNumero()
                + "   |   Situação: " + nota.getStatus().getDescricao());
        cabecalho.addAll(dadosCliente(os.getCliente()));

        List<Object[]> linhas = new ArrayList<>();
        for (ItemNota item : nota.getItens()) {
            linhas.add(new Object[]{item.getDescricao(), item.getQuantidade(),
                    Formatador.moeda(item.getValorUnitario()), Formatador.moeda(item.getSubtotal())});
        }

        List<String> resumo = new ArrayList<>();
        if (os.getDesconto() != null && os.getDesconto() > 0) {
            resumo.add("Desconto: " + Formatador.moeda(os.getDesconto()));
        }
        resumo.add("Simulação didática de tributos ("
                + Formatador.decimal(OrdemServicoController.ALIQUOTA_IMPOSTO * 100) + "%): "
                + Formatador.moeda(nota.getImpostos()));
        resumo.add("VALOR TOTAL DO DOCUMENTO: " + Formatador.moeda(nota.getValorTotal()));
        resumo.add(" ");
        resumo.add("Documento de controle interno — não substitui a NF-e/NFS-e.");

        return RelatorioJasper.gerar("Documento interno Nº " + nota.getNumero(), cabecalho,
                new String[]{"Descrição", "Qtd.", "Valor unit.", "Subtotal"}, linhas, resumo);
    }

    private static List<String> dadosCliente(Pessoa cliente) {
        List<String> linhas = new ArrayList<>();
        linhas.add("Cliente: " + cliente.getNome() + "   |   " + (cliente.getDocumento().length() == 11 ? "CPF: " : "CNPJ: ")
                + Formatador.documento(cliente.getDocumento()));
        String contato = Formatador.telefone(cliente.getTelefone());
        if (cliente.getEmail() != null) {
            contato += (contato.isEmpty() ? "" : "   |   ") + cliente.getEmail();
        }
        if (!contato.isEmpty()) {
            linhas.add("Contato: " + contato);
        }
        return linhas;
    }

    private static String resumir(String texto) {
        String limpo = texto == null ? "" : texto.replaceAll("\\s+", " ").trim();
        return limpo.length() > 120 ? limpo.substring(0, 117) + "..." : limpo;
    }
}
