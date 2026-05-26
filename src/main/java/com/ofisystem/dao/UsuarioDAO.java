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

    public void atualizarNomeOficina(Usuario usuario) {
        EntityManager em = JPAutil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
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

    public String contarUsuario(){
        EntityManager em = JPAutil.getEntityManager();
        try{
            return em.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class).getSingleResult().toString();
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
