package com.ofisystem.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAutil {

    private static final EntityManagerFactory factory =
            criarFactory();

    private static EntityManagerFactory criarFactory() {
        Map<String, Object> propriedades = new HashMap<>();
        // Permite uma base isolada de testes, mantendo a configuração padrão do aplicativo.
        for (String chave : new String[]{"jakarta.persistence.jdbc.url", "jakarta.persistence.jdbc.user",
                "jakarta.persistence.jdbc.password", "jakarta.persistence.jdbc.driver",
                "hibernate.dialect", "hibernate.hbm2ddl.auto", "hibernate.show_sql"}) {
            String valor = System.getProperty(chave);
            if (valor != null) propriedades.put(chave, valor);
        }
        // Cada EntityManager passa a enxergar apenas os registros do usuário logado.
        propriedades.put("hibernate.tenant_identifier_resolver", new UsuarioLogadoResolver());
        return Persistence.createEntityManagerFactory("ofisystem", propriedades);
    }

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void close() {
        if(factory.isOpen()) {
            factory.close();
        }
    }

}
