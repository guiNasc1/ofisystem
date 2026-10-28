package com.ofisystem.controle.venda;

import Enums.FormaPagamento;
import Enums.StatusVenda;
import com.ofisystem.controle.AbstractController;
import com.ofisystem.dao.VendaDAO;
import com.ofisystem.entidade.ItemVenda;
import com.ofisystem.entidade.PagamentoVenda;
import com.ofisystem.entidade.Venda;
import com.ofisystem.util.RegraNegocioException;
import com.ofisystem.util.Sessao;

import java.time.LocalDate;
import java.util.List;

public class VendaController extends AbstractController<Venda> {

    private final VendaDAO vendaDAO = new VendaDAO();

    public Venda finalizar(Venda venda) {
        if (venda.getId() != null) {
            throw new RegraNegocioException("Esta venda já foi finalizada.");
        }
        if (venda.getItens().isEmpty()) {
            throw new RegraNegocioException("Adicione pelo menos um produto ou serviço.");
        }
        for (ItemVenda item : venda.getItens()) {
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new RegraNegocioException("A quantidade do item \"" + item.getDescricao() + "\" deve ser maior que zero.");
            }
            if (item.getValorUnitario() == null || item.getValorUnitario() < 0) {
                throw new RegraNegocioException("O valor do item \"" + item.getDescricao() + "\" é inválido.");
            }
            item.calcularSubtotal();
        }
        if (venda.getDesconto() == null) {
            venda.setDesconto(0.0);
        }
        if (venda.getDesconto() < 0) {
            throw new RegraNegocioException("O desconto não pode ser negativo.");
        }
        venda.recalcularTotais();
        if (venda.getDesconto() > venda.getValorProdutos() + venda.getValorServicos()) {
            throw new RegraNegocioException("O desconto não pode ser maior que o valor da venda.");
        }

        if (venda.getPagamentos().isEmpty()) {
            throw new RegraNegocioException("Informe a forma de pagamento.");
        }
        for (PagamentoVenda p : venda.getPagamentos()) {
            if (p.getValor() == null || p.getValor() <= 0) {
                throw new RegraNegocioException("Há pagamento com valor inválido.");
            }
        }
        double pago = arredondar(venda.getValorPago());
        double total = arredondar(venda.getValorTotal());
        if (pago < total) {
            throw new RegraNegocioException("Pagamento insuficiente. Falta " + String.format("R$ %.2f", total - pago) + ".");
        }
        if (pago > total && arredondar(pago - total) > arredondar(venda.getValorEmDinheiro())) {
            throw new RegraNegocioException("Só é possível dar troco sobre pagamento em dinheiro."
                    + "\nAjuste os valores de PIX/cartão para não ultrapassar o total.");
        }
        return vendaDAO.finalizar(venda, Sessao.getNome());
    }

    public void cancelar(Venda venda) {
        vendaDAO.cancelar(venda.getId(), Sessao.getNome());
    }

    public List<Venda> filtrar(String numero, String cliente, StatusVenda status, LocalDate de, LocalDate ate) {
        return vendaDAO.filtrar(numero, cliente, status, de, ate);
    }

    public Venda buscarCompleta(Integer id) {
        return vendaDAO.buscarCompleta(id);
    }

    public static double valorRecebido(Venda venda, FormaPagamento forma) {
        double valor = venda.getPagamentos().stream().filter(p -> p.getForma() == forma)
                .mapToDouble(PagamentoVenda::getValor).sum();
        return forma == FormaPagamento.DINHEIRO ? valor - venda.getTroco() : valor;
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
