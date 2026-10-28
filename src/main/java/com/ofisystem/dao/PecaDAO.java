package com.ofisystem.dao;

import com.ofisystem.entidade.Peca;

import java.util.List;

public class PecaDAO extends AbstractFacade<Peca> {

    public PecaDAO() {
        super(Peca.class);
    }

    public boolean existeNome(String nome, Integer ignorarId) {
        return consultar(em -> new Consulta("SELECT COUNT(p) FROM Peca p")
                .se(true, "LOWER(p.nome) = :nome", "nome", nome.trim().toLowerCase())
                .se(ignorarId != null, "p.id <> :id", "id", ignorarId)
                .listar(em, "", Long.class).get(0) > 0);
    }

    public boolean usadaEmOrdemServicoOuVenda(Integer id) {
        return consultar(em -> em.createQuery(
                        "SELECT COUNT(i) FROM ItemPeca i WHERE i.peca.id = :id", Long.class)
                .setParameter("id", id).getSingleResult()
                + em.createQuery("SELECT COUNT(i) FROM ItemVenda i WHERE i.peca.id = :id", Long.class)
                .setParameter("id", id).getSingleResult() > 0);
    }

    public List<String> listarCategorias() {
        return consultar(em -> em.createQuery(
                        "SELECT DISTINCT p.categoria FROM Peca p WHERE p.categoria IS NOT NULL ORDER BY p.categoria", String.class)
                .getResultList());
    }
}
