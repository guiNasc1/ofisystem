package com.ofisystem.model;

import java.util.Objects;

public class Servico {

    private Integer id;
    private String nome;
    private String descricao;
    private Double valor;
    private Integer duracaoMin;
    private boolean ativo;

    @Override
    public String toString() {
        return "Servico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", duracaoMin=" + duracaoMin +
                ", ativo=" + ativo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Servico servico = (Servico) o;
        return isAtivo() == servico.isAtivo() && Objects.equals(getId(), servico.getId()) && Objects.equals(getNome(), servico.getNome()) && Objects.equals(getDescricao(), servico.getDescricao()) && Objects.equals(getValor(), servico.getValor()) && Objects.equals(getDuracaoMin(), servico.getDuracaoMin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNome(), getDescricao(), getValor(), getDuracaoMin(), isAtivo());
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Integer getDuracaoMin() {
        return duracaoMin;
    }

    public void setDuracaoMin(Integer duracaoMin) {
        this.duracaoMin = duracaoMin;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
