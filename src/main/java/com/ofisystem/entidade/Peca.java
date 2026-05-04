package com.ofisystem.entidade;

import Enums.Status;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "peca")
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "peca_id")
    private Integer id;

    @Column(nullable = false, name = "peca_nome")
    private String nome;

    @Column(nullable = false, unique = true, name = "peca_codigo")
    private String codigo;

    @Column(nullable = false, name = "pecaValorUnitario")
    private Double valorUnitario;

    @OneToMany(mappedBy = "peca", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<itemPeca> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "pecaStatus")
    private Status status;

    @Override
    public String toString() {
        return "Peca{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", codigo='" + codigo + '\'' +
                ", valorUnitario=" + valorUnitario +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Peca peca = (Peca) o;
        return Objects.equals(getId(), peca.getId()) && Objects.equals(getNome(), peca.getNome()) && Objects.equals(getCodigo(), peca.getCodigo()) && Objects.equals(getValorUnitario(), peca.getValorUnitario()) && getStatus() == peca.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNome(), getCodigo(), getValorUnitario(), getStatus());
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public List<itemPeca> getItens() {
        return itens;
    }

    public void setItens(List<itemPeca> itens) {
        this.itens = itens;
    }
}
