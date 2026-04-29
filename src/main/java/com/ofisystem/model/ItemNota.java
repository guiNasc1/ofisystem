package com.ofisystem.model;

import java.util.Objects;

public class ItemNota {

    private Integer id;
    private String descricao;
    private Integer quantidade;
    private Double valorUnitario;
    private Double subtotal;

    @Override
    public String toString() {
        return "ItemNota{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", quantidade=" + quantidade +
                ", valorUnitario=" + valorUnitario +
                ", subtotal=" + subtotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemNota itemNota = (ItemNota) o;
        return Objects.equals(getId(), itemNota.getId()) && Objects.equals(getDescricao(), itemNota.getDescricao()) && Objects.equals(getQuantidade(), itemNota.getQuantidade()) && Objects.equals(getValorUnitario(), itemNota.getValorUnitario()) && Objects.equals(getSubtotal(), itemNota.getSubtotal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDescricao(), getQuantidade(), getValorUnitario(), getSubtotal());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
