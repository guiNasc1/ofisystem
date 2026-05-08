package com.ofisystem.dao.cliente;

import com.ofisystem.entidade.Cliente;
import com.ofisystem.util.JPAutil;
import jakarta.persistence.EntityManager;

public class ClienteDAO {

    public void salvar(Cliente cliente){

        EntityManager em = JPAutil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }

    }

}
