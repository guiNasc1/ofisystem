package com.ofisystem.controle.estoque;

import Enums.TipoMovimentacao;
import com.ofisystem.controle.AbstractController;
import com.ofisystem.dao.EstoqueDAO;
import com.ofisystem.dao.MovimentacaoEstoqueDAO;
import com.ofisystem.dao.PecaDAO;
import com.ofisystem.entidade.Estoque;
import com.ofisystem.entidade.MovimentacaoEstoque;
import com.ofisystem.entidade.Peca;
import com.ofisystem.util.RegraNegocioException;
import com.ofisystem.util.Sessao;

import java.time.LocalDate;
import java.util.List;

public class EstoqueController extends AbstractController<Estoque> {

    private final EstoqueDAO estoqueDAO = new EstoqueDAO();
    private final PecaDAO pecaDAO = new PecaDAO();
    private final MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();

    public Estoque salvarPeca(Peca peca, Integer quantidadeMinima, Integer quantidadeInicial) {
        if (peca.getNome() == null || peca.getNome().isBlank()) {
            throw new RegraNegocioException("Informe o nome da peça.");
        }
        if (peca.getValorUnitario() == null || peca.getValorUnitario() < 0) {
            throw new RegraNegocioException("Informe um valor unitário válido.");
        }
        if (quantidadeMinima != null && quantidadeMinima < 0) {
            throw new RegraNegocioException("A quantidade mínima não pode ser negativa.");
        }
        if (quantidadeInicial != null && quantidadeInicial < 0) {
            throw new RegraNegocioException("A quantidade inicial não pode ser negativa.");
        }
        if (pecaDAO.existeNome(peca.getNome(), peca.getId())) {
            throw new RegraNegocioException("Já existe uma peça com o nome " + peca.getNome().trim() + ".");
        }
        return estoqueDAO.salvarPeca(peca, quantidadeMinima == null ? 0 : quantidadeMinima, quantidadeInicial, Sessao.getNome());
    }

    public void excluirPeca(Peca peca) {
        if (pecaDAO.usadaEmOrdemServicoOuVenda(peca.getId())) {
            throw new RegraNegocioException("Esta peça já foi usada em ordens de serviço ou vendas."
                    + "\nEdite o cadastro e altere o status para Inativo.");
        }
        estoqueDAO.excluirPeca(peca.getId());
    }

    public MovimentacaoEstoque movimentar(Peca peca, TipoMovimentacao tipo, Integer quantidade, String observacao) {
        if (peca == null) {
            throw new RegraNegocioException("Selecione a peça.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("Informe uma quantidade maior que zero.");
        }
        if (observacao == null || observacao.isBlank()) {
            throw new RegraNegocioException("Informe o motivo da movimentação.");
        }
        return estoqueDAO.movimentar(peca.getId(), tipo, quantidade, observacao.trim(), Sessao.getNome());
    }

    public List<Estoque> filtrar(String texto, String categoria, boolean apenasBaixo, boolean apenasAtivas) {
        return estoqueDAO.filtrar(texto, categoria, apenasBaixo, apenasAtivas);
    }

    public List<Estoque> listarAtivos() {
        return estoqueDAO.listarAtivos();
    }

    public Estoque buscarPorPeca(Integer pecaId) {
        return estoqueDAO.buscarPorPeca(pecaId);
    }

    public List<String> listarCategorias() {
        return pecaDAO.listarCategorias();
    }

    public List<MovimentacaoEstoque> filtrarMovimentacoes(String peca, TipoMovimentacao tipo, LocalDate de, LocalDate ate) {
        return movimentacaoDAO.filtrar(peca, tipo, de, ate);
    }
}
