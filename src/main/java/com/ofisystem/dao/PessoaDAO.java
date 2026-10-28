package com.ofisystem.dao;

import com.ofisystem.entidade.Pessoa;
import com.ofisystem.util.Formatador;

import java.util.List;

public class PessoaDAO extends AbstractFacade<Pessoa> {

    public PessoaDAO() {
        super(Pessoa.class);
    }

    public List<Pessoa> filtrar(String nome, String documento, String telefone, String cidade,
                                String papel, Boolean ativo) {
        String doc = Formatador.somenteDigitos(documento);
        String fone = Formatador.somenteDigitos(telefone);
        return consultar(em -> new Consulta("SELECT DISTINCT p FROM Pessoa p LEFT JOIN FETCH p.enderecos")
                .contem("p.nome", "nome", nome)
                .contem("p.documento", "documento", doc)
                .contem("p.telefone", "fone", fone)
                .se(cidade != null && !cidade.isBlank(),
                        "EXISTS (SELECT 1 FROM Endereco e WHERE e.pessoa = p AND LOWER(e.cidade) LIKE :cidade)",
                        "cidade", "%" + (cidade == null ? "" : cidade.trim().toLowerCase()) + "%")
                .se("Cliente".equals(papel), "p.cliente = true")
                .se("Fornecedor".equals(papel), "p.fornecedor = true")
                .se("Funcionário".equals(papel), "p.funcionario = true")
                .igual("p.ativo", "ativo", ativo)
                .listar(em, "ORDER BY p.nome", Pessoa.class));
    }

    public Pessoa buscarCompleta(Integer id) {
        return consultar(em -> em.createQuery(
                        "SELECT p FROM Pessoa p LEFT JOIN FETCH p.enderecos WHERE p.id = :id", Pessoa.class)
                .setParameter("id", id)
                .getResultStream().findFirst().orElse(null));
    }

    public boolean existeDocumento(String documento, Integer ignorarId) {
        return consultar(em -> new Consulta("SELECT COUNT(p) FROM Pessoa p")
                .igual("p.documento", "documento", documento)
                .se(ignorarId != null, "p.id <> :id", "id", ignorarId)
                .listar(em, "", Long.class).get(0) > 0);
    }

    public List<Pessoa> listarClientesAtivos() {
        return consultar(em -> em.createQuery(
                        "SELECT p FROM Pessoa p WHERE p.cliente = true AND p.ativo = true ORDER BY p.nome", Pessoa.class)
                .getResultList());
    }

    public List<Pessoa> listarAtivas() {
        return consultar(em -> em.createQuery(
                        "SELECT p FROM Pessoa p WHERE p.ativo = true ORDER BY p.nome", Pessoa.class)
                .getResultList());
    }

    public boolean possuiMovimento(Integer id) {
        return consultar(em -> {
            for (String jpql : new String[]{
                    "SELECT COUNT(o) FROM OrdemServico o WHERE o.cliente.id = :id",
                    "SELECT COUNT(v) FROM Venda v WHERE v.cliente.id = :id"}) {
                if (em.createQuery(jpql, Long.class).setParameter("id", id).getSingleResult() > 0) {
                    return true;
                }
            }
            return false;
        });
    }
}
