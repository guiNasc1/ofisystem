package com.ofisystem.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAutil {

    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("ofisystem");

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void close() {
        if(factory.isOpen()) {
            factory.close();
        }
    }

}
