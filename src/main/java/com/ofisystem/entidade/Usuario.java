package com.ofisystem.entidade;

import Enums.Perfil;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer id;

    @Column(nullable = false, unique = true, name = "usuario_nome")
    private String nome;

    @Column(nullable = false, unique = true, name = "usuario_email")
    private String email;

    @Column(nullable = false, name = "usuario_senha")
    private String senha;

    @Column(name = "usuario_oficina")
    private String nomeOficina;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "usuario_perfil")
    private Perfil perfil = Perfil.USUARIO;

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(getId(), usuario.getId()) && Objects.equals(getNome(), usuario.getNome()) && Objects.equals(getEmail(), usuario.getEmail()) && getPerfil() == usuario.getPerfil();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNome(), getEmail(), getPerfil());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getNomeOficina() {
        return nomeOficina;
    }

    public void setNomeOficina(String nomeOficina) {
        this.nomeOficina = nomeOficina;
    }
}
