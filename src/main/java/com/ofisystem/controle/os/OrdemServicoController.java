package com.ofisystem.controle.os;

import Enums.FormaPagamento;
import Enums.StatusOS;
import com.ofisystem.controle.AbstractController;
import com.ofisystem.dao.NotaFiscalDAO;
import com.ofisystem.dao.OrdemServicoDAO;
import com.ofisystem.entidade.ItemOs;
import com.ofisystem.entidade.ItemPeca;
import com.ofisystem.entidade.NotaFiscal;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.util.RegraNegocioException;
import com.ofisystem.util.Sessao;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

public class OrdemServicoController extends AbstractController<OrdemServico> {

    public static final double ALIQUOTA_IMPOSTO = 0.06;

    private final OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO();
    private final NotaFiscalDAO notaFiscalDAO = new NotaFiscalDAO();

    public OrdemServico salvar(OrdemServico os) {
        if (os.getCliente() == null) {
            throw new RegraNegocioException("Selecione o cliente.");
        }
        validarVeiculo(os);
        if (os.getDescricao() == null || os.getDescricao().isBlank()) {
            throw new RegraNegocioException("Descreva o defeito relatado.");
        }
        if (os.getDataPrevisao() != null && os.getDataPrevisao().isBefore(os.getDataAbertura().toLocalDate())) {
            throw new RegraNegocioException("A previsão de entrega não pode ser anterior à abertura.");
        }
        for (ItemOs item : os.getItensServico()) {
            validarItem(item.getDescricao(), item.getQuantidade(), item.getValorUnitario());
            item.calcularSubtotal();
        }
        for (ItemPeca item : os.getItensPeca()) {
            validarItem(item.getPeca() == null ? null : item.getPeca().getNome(), item.getQuantidade(), item.getValorUnitario());
            item.calcularSubtotal();
        }
        if (os.getDesconto() == null) {
            os.setDesconto(0.0);
        }
        if (os.getDesconto() < 0) {
            throw new RegraNegocioException("O desconto não pode ser negativo.");
        }
        os.recalcularTotais();
        if (os.getDesconto() > os.getValorServicos() + os.getValorPecas()) {
            throw new RegraNegocioException("O desconto não pode ser maior que o valor da OS.");
        }
        if (os.getResponsavel() == null) {
            os.setResponsavel(Sessao.getNome());
        }
        return ordemServicoDAO.salvarComEstoque(os, Sessao.getNome());
    }

    public void alterarStatus(OrdemServico os, StatusOS novoStatus) {
        if (novoStatus == StatusOS.CONCLUIDA || novoStatus == StatusOS.CANCELADA) {
            throw new RegraNegocioException("Use as opções Concluir ou Cancelar.");
        }
        ordemServicoDAO.alterarStatus(os.getId(), novoStatus);
    }

    public void concluir(OrdemServico os, FormaPagamento formaPagamento) {
        if (os.getValorTotal() == null || os.getValorTotal() <= 0) {
            throw new RegraNegocioException("Adicione serviços ou peças antes de concluir a OS.");
        }
        if (formaPagamento == null) {
            throw new RegraNegocioException("Selecione a forma de pagamento.");
        }
        ordemServicoDAO.concluir(os.getId(), formaPagamento);
    }

    private void validarVeiculo(OrdemServico os) {
        if (!os.isPossuiVeiculo()) {
            os.setVeiculoPlaca(null);
            os.setVeiculoMarca(null);
            os.setVeiculoModelo(null);
            os.setVeiculoAno(null);
            os.setKm(null);
            return;
        }
        String placa = os.getVeiculoPlaca() == null ? "" : os.getVeiculoPlaca().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        os.setVeiculoPlaca(placa.isEmpty() ? null : placa);
        os.setVeiculoMarca(os.getVeiculoMarca() == null || os.getVeiculoMarca().isBlank() ? null : os.getVeiculoMarca().trim());
        if (os.getVeiculoModelo() == null || os.getVeiculoModelo().isBlank()) {
            throw new RegraNegocioException("Informe o modelo do veículo.");
        }
        os.setVeiculoModelo(os.getVeiculoModelo().trim());
        if (os.getVeiculoAno() != null && (os.getVeiculoAno() < 1900 || os.getVeiculoAno() > Year.now().getValue() + 1)) {
            throw new RegraNegocioException("Ano do veículo inválido.");
        }
        if (os.getKm() != null && os.getKm() < 0) {
            throw new RegraNegocioException("A quilometragem não pode ser negativa.");
        }
    }

    public void cancelar(OrdemServico os) {
        ordemServicoDAO.cancelar(os.getId(), Sessao.getNome());
    }

    public NotaFiscal emitirNotaFiscal(OrdemServico os) {
        if (!Sessao.isAdmin()) {
            throw new RegraNegocioException("Somente administradores podem emitir nota fiscal.");
        }
        return notaFiscalDAO.emitir(os.getId(), ALIQUOTA_IMPOSTO);
    }

    public NotaFiscal buscarNotaFiscal(OrdemServico os) {
        return notaFiscalDAO.buscarPorOrdemServico(os.getId());
    }

    public List<OrdemServico> filtrar(String numero, String cliente, String placa, StatusOS status,
                                      boolean somenteAbertas, LocalDate de, LocalDate ate) {
        return ordemServicoDAO.filtrar(numero, cliente, placa, status, somenteAbertas, de, ate);
    }

    public List<OrdemServico> concluidasNoPeriodo(LocalDate de, LocalDate ate) {
        return ordemServicoDAO.concluidasNoPeriodo(de, ate);
    }

    public OrdemServico buscarCompleta(Integer id) {
        return ordemServicoDAO.buscarCompleta(id);
    }

    private void validarItem(String descricao, Integer quantidade, Double valor) {
        if (descricao == null || descricao.isBlank()) {
            throw new RegraNegocioException("Há item sem descrição na OS.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade do item \"" + descricao + "\" deve ser maior que zero.");
        }
        if (valor == null || valor < 0) {
            throw new RegraNegocioException("O valor do item \"" + descricao + "\" é inválido.");
        }
    }
}
