package com.ofisystem.dao;

import com.ofisystem.util.JPAutil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractFacade<T> implements Serializable {

    private final Class<T> classe;

    protected AbstractFacade(Class<T> classe) {
        this.classe = classe;
    }

    public T salvar(T entidade) {
        return emTransacao(em -> em.merge(entidade));
    }

    public void excluir(Object id) {
        emTransacao(em -> {
            T entidade = em.find(classe, id);
            if (entidade != null) {
                em.remove(entidade);
            }
            return null;
        });
    }

    public T buscarPorId(Object id) {
        return consultar(em -> em.find(classe, id));
    }

    public List<T> listarTodos() {
        return consultar(em -> em.createQuery("SELECT e FROM " + classe.getSimpleName() + " e", classe).getResultList());
    }

    protected <R> R consultar(Function<EntityManager, R> acao) {
        EntityManager em = JPAutil.getEntityManager();
        try {
            return acao.apply(em);
        } finally {
            em.close();
        }
    }

    protected <R> R emTransacao(Function<EntityManager, R> acao) {
        EntityManager em = JPAutil.getEntityManager();
        try {
            em.getTransaction().begin();
            R resultado = acao.apply(em);
            em.getTransaction().commit();
            return resultado;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    protected static class Consulta {

        private final StringBuilder jpql;
        private final Map<String, Object> parametros = new HashMap<>();

        public Consulta(String base) {
            this.jpql = new StringBuilder(base).append(" WHERE 1=1");
        }

        public Consulta igual(String campo, String parametro, Object valor) {
            if (valor != null && !(valor instanceof String s && s.isBlank())) {
                jpql.append(" AND ").append(campo).append(" = :").append(parametro);
                parametros.put(parametro, valor);
            }
            return this;
        }

        public Consulta contem(String campo, String parametro, String valor) {
            if (valor != null && !valor.isBlank()) {
                jpql.append(" AND LOWER(").append(campo).append(") LIKE :").append(parametro);
                parametros.put(parametro, "%" + valor.trim().toLowerCase() + "%");
            }
            return this;
        }

        public Consulta maiorIgual(String campo, String parametro, Object valor) {
            if (valor != null) {
                jpql.append(" AND ").append(campo).append(" >= :").append(parametro);
                parametros.put(parametro, valor);
            }
            return this;
        }

        public Consulta menor(String campo, String parametro, Object valor) {
            if (valor != null) {
                jpql.append(" AND ").append(campo).append(" < :").append(parametro);
                parametros.put(parametro, valor);
            }
            return this;
        }

        public Consulta se(boolean condicao, String clausula) {
            if (condicao) {
                jpql.append(" AND ").append(clausula);
            }
            return this;
        }

        public Consulta se(boolean condicao, String clausula, String parametro, Object valor) {
            if (condicao) {
                jpql.append(" AND ").append(clausula);
                parametros.put(parametro, valor);
            }
            return this;
        }

        public <R> List<R> listar(EntityManager em, String ordenacao, Class<R> tipo) {
            TypedQuery<R> query = em.createQuery(jpql + " " + ordenacao, tipo);
            parametros.forEach(query::setParameter);
            return query.getResultList();
        }
    }
}
