package com.ofisystem.util;

import com.ofisystem.entidade.Usuario;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/** Informa ao Hibernate de quem é cada sessão: o usuário logado em {@link Sessao}. */
public class UsuarioLogadoResolver implements CurrentTenantIdentifierResolver<Integer> {

    /** Sem login (ou conta ainda não gravada) a sessão não casa com nenhum registro real. */
    static final Integer SEM_USUARIO = -1;

    @Override
    public Integer resolveCurrentTenantIdentifier() {
        Usuario usuario = Sessao.getUsuariologado();
        return usuario != null && usuario.getId() != null ? usuario.getId() : SEM_USUARIO;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
