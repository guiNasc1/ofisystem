package com.ofisystem.dao;

import com.ofisystem.entidade.Cliente;
import com.ofisystem.util.JPAutil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import javax.swing.*;
import java.util.List;

public class ClienteDAO {

    public void salvar(Cliente cliente){

        EntityManager em = JPAutil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
        } catch (Exception e) {
            em.getTransaction().rollback();
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar cliente!");
            throw e;
        } finally {
            em.close();
        }

    }

    public void atualizar(Cliente cliente){
        EntityManager em = JPAutil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cliente);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Cliente> filtrar(String nome, String cpf, String telefone, String cidade){
        EntityManager em = JPAutil.getEntityManager();

        try{

            StringBuilder jpql = new StringBuilder(
                    "SELECT c FROM Clientes c WHERE 1=1"
            );

            if (nome != null && !nome.isBlank()){
                jpql.append(" AND LOWER(c.nome) LIKE LOWER(:nome)");
            }

            if (cpf != null && !cpf.isBlank()) {
                jpql.append(" AND LOWER(c.cpf) LIKE LOWER(:cpf)");
            }

            if (telefone != null && !telefone.isBlank()) {
                jpql.append(" AND LOWER(c.telefone) LIKE LOWER(:telefone)");
            }

            if (cidade != null && !cidade.isBlank()) {
                jpql.append(" AND LOWER(c.cidade) LIKE LOWER(:cidade)");
            }

            TypedQuery<Cliente> query = em.createQuery(jpql.toString(), Cliente.class);

            if(nome != null && !nome.isBlank()){
                query.setParameter("nome", "%" + nome + "%");
            }

            if(cpf != null && !cpf.isBlank()){
                query.setParameter("cpf", "%" + cpf + "%");
            }

            if(telefone != null && !telefone.isBlank()){
                query.setParameter("telefone", "%" + telefone + "%");
            }

            if(cidade != null && !cidade.isBlank()){
                query.setParameter("cidade", "%" + cidade + "%");
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cliente> listarTodosPaginado(){
        EntityManager em = JPAutil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT c FROM Cliente c" +
                    " LEFT JOIN FETCH c.endereco" +
                    " ORDER BY c.cliNome", Cliente.class).getResultList();
            } finally {
                em.close();
            }
        }

    public void deletar(Cliente cliente){
        EntityManager em = JPAutil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.remove(em.merge(cliente));
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Cliente buscarPorID(Integer id){
        EntityManager em = JPAutil.getEntityManager();
        try{
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

}
