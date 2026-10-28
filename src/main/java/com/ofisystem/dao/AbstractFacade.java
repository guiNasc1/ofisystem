package com.ofisystem.dao;

import com.ofisystem.util.JPAutil;
import com.ofisystem.util.RegraNegocioException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
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
        return emTransacao(em -> {
            Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entidade);
            if (id != null && achar(em, classe, id, null) == null) {
                throw new RegraNegocioException("Registro não encontrado.");
            }
            return em.merge(entidade);
        });
    }

    public void excluir(Object id) {
        emTransacao(em -> {
            T entidade = achar(em, classe, id, null);
            if (entidade != null) {
                em.remove(entidade);
            }
            return null;
        });
    }

    public T buscarPorId(Object id) {
        return consultar(em -> achar(em, classe, id, null));
    }

    /**
     * Busca por chave respeitando o dono do registro. O {@code em.find} do Hibernate 6.4 não aplica o
     * filtro de usuário ({@code @TenantId}), então o acesso por id passa por uma consulta.
     */
    protected static <E> E achar(EntityManager em, Class<E> tipo, Object id, LockModeType bloqueio) {
        TypedQuery<E> query = em.createQuery(
                "SELECT e FROM " + tipo.getSimpleName() + " e WHERE e.id = :id", tipo).setParameter("id", id);
        if (bloqueio != null) {
            query.setLockMode(bloqueio);
        }
        return query.getResultStream().findFirst().orElse(null);
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
