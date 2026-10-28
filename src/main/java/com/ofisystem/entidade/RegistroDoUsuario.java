package com.ofisystem.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.TenantId;

import java.io.Serializable;

/**
 * Base dos registros que pertencem a um usuário. O Hibernate grava o dono em
 * {@code usuario_id} ao inserir e acrescenta {@code usuario_id = :logado} a toda
 * consulta, de modo que cada conta enxerga somente os próprios dados.
 */
@MappedSuperclass
public abstract class RegistroDoUsuario implements Serializable {

    @TenantId
    @Column(name = "usuario_id", updatable = false)
    private Integer usuarioId;

    public Integer getUsuarioId() {
        return usuarioId;
    }
}
