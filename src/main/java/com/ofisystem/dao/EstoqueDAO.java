package com.ofisystem.dao;

import Enums.Status;
import Enums.TipoMovimentacao;
import com.ofisystem.entidade.Estoque;
import com.ofisystem.entidade.MovimentacaoEstoque;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.entidade.Peca;
import com.ofisystem.entidade.Venda;
import com.ofisystem.util.RegraNegocioException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.util.List;

public class EstoqueDAO extends AbstractFacade<Estoque> {

    public EstoqueDAO() {
        super(Estoque.class);
    }

    public List<Estoque> filtrar(String texto, String categoria, boolean apenasBaixo, boolean apenasAtivas) {
        return consultar(em -> new Consulta("SELECT e FROM Estoque e JOIN FETCH e.peca p")
                .contem("CONCAT(CAST(p.id AS String), ' ', p.nome)", "texto", texto)
                .contem("p.categoria", "categoria", categoria)
                .se(apenasBaixo, "e.quantidade <= e.quantidadeMinima")
                .se(apenasAtivas, "p.status = :status", "status", Status.ATIVO)
                .listar(em, "ORDER BY p.nome", Estoque.class));
    }

    public List<Estoque> listarAtivos() {
        return filtrar(null, null, false, true);
    }

    public Estoque buscarPorPeca(Integer pecaId) {
        return consultar(em -> em.createQuery(
                        "SELECT e FROM Estoque e JOIN FETCH e.peca p WHERE p.id = :id", Estoque.class)
                .setParameter("id", pecaId)
                .getResultStream().findFirst().orElse(null));
    }

    public Estoque salvarPeca(Peca peca, Integer quantidadeMinima, Integer quantidadeInicial, String usuario) {
        return emTransacao(em -> {
            if (peca.getId() == null) {
                Peca nova = em.merge(peca);
                Estoque estoque = new Estoque();
                estoque.setPeca(nova);
                estoque.setQuantidadeMinima(quantidadeMinima);
                em.persist(estoque);
                if (quantidadeInicial != null && quantidadeInicial > 0) {
                    movimentar(em, nova.getId(), TipoMovimentacao.ENTRADA, quantidadeInicial,
                            "Estoque inicial", usuario, null, null);
                }
                return estoque;
            }
            Peca gerenciada = em.merge(peca);
            Estoque estoque = travarEstoque(em, gerenciada.getId());
            estoque.setQuantidadeMinima(quantidadeMinima);
            return estoque;
        });
    }

    public void excluirPeca(Integer pecaId) {
        emTransacao(em -> {
            em.createQuery("DELETE FROM MovimentacaoEstoque m WHERE m.peca.id = :id").setParameter("id", pecaId).executeUpdate();
            em.createQuery("DELETE FROM Estoque e WHERE e.peca.id = :id").setParameter("id", pecaId).executeUpdate();
            em.createQuery("DELETE FROM Peca p WHERE p.id = :id").setParameter("id", pecaId).executeUpdate();
            return null;
        });
    }

    public MovimentacaoEstoque movimentar(Integer pecaId, TipoMovimentacao tipo, int quantidade,
                                          String observacao, String usuario) {
        return emTransacao(em -> movimentar(em, pecaId, tipo, quantidade, observacao, usuario, null, null));
    }

    static MovimentacaoEstoque movimentar(EntityManager em, Integer pecaId, TipoMovimentacao tipo, int quantidade,
                                          String observacao, String usuario, OrdemServico ordemServico, Venda venda) {
        Estoque estoque = travarEstoque(em, pecaId);
        int novoSaldo = tipo == TipoMovimentacao.ENTRADA
                ? estoque.getQuantidade() + quantidade
                : estoque.getQuantidade() - quantidade;

        if (novoSaldo < 0) {
            throw new RegraNegocioException("Estoque insuficiente para a peça " + estoque.getPeca()
                    + ".\nDisponível: " + estoque.getQuantidade() + " | Solicitado: " + quantidade);
        }

        estoque.setQuantidade(novoSaldo);

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setPeca(estoque.getPeca());
        mov.setTipo(tipo);
        mov.setQuantidade(quantidade);
        mov.setSaldo(novoSaldo);
        mov.setObservacao(observacao);
        mov.setUsuario(usuario);
        mov.setOrdemServico(ordemServico);
        mov.setVenda(venda);
        em.persist(mov);
        return mov;
    }

    private static Estoque travarEstoque(EntityManager em, Integer pecaId) {
        return em.createQuery("SELECT e FROM Estoque e WHERE e.peca.id = :id", Estoque.class)
                .setParameter("id", pecaId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream().findFirst()
                .orElseThrow(() -> new RegraNegocioException("Peça sem registro de estoque."));
    }
}
