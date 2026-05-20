package com.ofisystem.dao;

import com.ofisystem.entidade.Usuario;
import com.ofisystem.util.JPAutil;
import com.ofisystem.util.SenhaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class UsuarioDAO {

    public void salvar(Usuario usuario){
        EntityManager em = JPAutil.getEntityManager();
        try{
            usuario.setSenha(SenhaUtil.criptografar(usuario.getSenha()));

            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean existeUsuario(Usuario usuario){
        EntityManager em = JPAutil.getEntityManager();
        try{
            Long total = em.createQuery(
                    "SELECT COUNT(u) FROM Usuario u", Long.class).getSingleResult();
            return total > 0;
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorNome(String nome){
        EntityManager em = JPAutil.getEntityManager();
        try{
            return em.createQuery("SELECT u FROM Usuario u WHERE u.nome = :nome", Usuario.class)
                    .setParameter("nome", nome)
                    .getSingleResult();
        } catch (NoResultException e){
            return null;
        } finally {
            em.close();
        }
    }

    public Usuario autenticar(String nome, String senha){
        Usuario usuario = buscarPorNome(nome);

        if(usuario == null) return null;

        if(SenhaUtil.verificar(senha, usuario.getSenha())) return usuario;

        return null;
    }
}
