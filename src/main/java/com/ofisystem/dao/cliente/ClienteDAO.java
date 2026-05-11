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

}
