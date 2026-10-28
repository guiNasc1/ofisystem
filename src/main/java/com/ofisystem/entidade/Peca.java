package com.ofisystem.entidade;

import Enums.Status;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "peca")
public class Peca implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "peca_id")
    private Integer id;

    @Column(nullable = false, name = "peca_nome")
    private String nome;

    @Column(name = "peca_categoria")
    private String categoria;

    @Column(nullable = false, name = "peca_valor_unitario")
    private Double valorUnitario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "peca_status")
    private Status status = Status.ATIVO;

    @Override
    public String toString() {
        return id + " - " + nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peca peca = (Peca) o;
        return id != null && Objects.equals(id, peca.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
