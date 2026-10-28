package com.ofisystem.dao;

import com.ofisystem.entidade.Servico;

import java.util.List;

public class ServicoDAO extends AbstractFacade<Servico> {

    public ServicoDAO() {
        super(Servico.class);
    }

    public List<Servico> filtrar(String nome, Boolean ativo) {
        return consultar(em -> new Consulta("SELECT s FROM Servico s")
                .contem("s.nome", "nome", nome)
                .igual("s.ativo", "ativo", ativo)
                .listar(em, "ORDER BY s.nome", Servico.class));
    }

    public List<Servico> listarAtivos() {
        return filtrar(null, true);
    }

    public boolean existeNome(String nome, Integer ignorarId) {
        return consultar(em -> new Consulta("SELECT COUNT(s) FROM Servico s")
                .se(true, "LOWER(s.nome) = :nome", "nome", nome.trim().toLowerCase())
                .se(ignorarId != null, "s.id <> :id", "id", ignorarId)
                .listar(em, "", Long.class).get(0) > 0);
    }

    public boolean possuiOrdemServico(Integer id) {
        return consultar(em -> em.createQuery(
                        "SELECT COUNT(i) FROM ItemOs i WHERE i.servico.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0);
    }
}
