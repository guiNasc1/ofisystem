package com.ofisystem;

import com.ofisystem.util.JPAutil;
import jakarta.persistence.EntityManager;

public class testeConexaoBanco {

    public static void main(String[] args) {
        System.out.println("Testando conexão com o banco...");

        try {
            EntityManager em = JPAutil.getEntityManager();

            if (em.isOpen()) {
                System.out.println("✅ Conexão bem-sucedida! Banco conectado.");
            }

            em.close();
            JPAutil.close();

        } catch (Exception e) {
            System.out.println("❌ Erro ao conectar com o banco!");
            System.out.println("Causa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}