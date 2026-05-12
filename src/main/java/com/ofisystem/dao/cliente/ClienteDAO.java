package com.ofisystem.dao.cliente;

import com.ofisystem.entidade.Cliente;
import com.ofisystem.util.JPAutil;
import jakarta.persistence.EntityManager;

import javax.swing.*;

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
        EntityManager
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
