package com.ofisystem.dao;

import Enums.Perfil;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.util.JPAutil;
import com.ofisystem.util.SenhaUtil;
import com.ofisystem.util.RegraNegocioException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Locale;

public class UsuarioDAO {

    public void salvar(Usuario usuario){
        EntityManager em = JPAutil.getEntityManager();
        String senhaOriginal = usuario.getSenha();
        try{
            em.getTransaction().begin();
            Long repetidos = em.createQuery("SELECT COUNT(u) FROM Usuario u"
                            + " WHERE LOWER(TRIM(u.nome)) = :nome OR LOWER(TRIM(u.email)) = :email", Long.class)
                    .setParameter("nome", usuario.getNome().trim().toLowerCase(Locale.ROOT))
                    .setParameter("email", usuario.getEmail().trim().toLowerCase(Locale.ROOT))
                    .getSingleResult();
            if (repetidos > 0) {
                throw new RegraNegocioException("Já existe uma conta com esse usuário ou e-mail.");
            }
            Long total = em.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class).getSingleResult();
            // A primeira conta da base é sempre ADMIN; nas demais vale o perfil escolhido no cadastro.
            if (total == 0) {
                usuario.setPerfil(Perfil.ADMIN);
            } else if (usuario.getPerfil() == null) {
                usuario.setPerfil(Perfil.USUARIO);
            }
            usuario.setSenha(SenhaUtil.criptografar(senhaOriginal));
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e){
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            usuario.setSenha(senhaOriginal);
            usuario.setId(null);
            if (e instanceof org.hibernate.exception.ConstraintViolationException
                    || e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new RegraNegocioException("Já existe uma conta com esse usuário ou e-mail.");
            }
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
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorNome(String nome){
        EntityManager em = JPAutil.getEntityManager();
        try{
            return em.createQuery("SELECT u FROM Usuario u WHERE LOWER(TRIM(u.nome)) = :nome", Usuario.class)
                    .setParameter("nome", nome.trim().toLowerCase(Locale.ROOT))
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
