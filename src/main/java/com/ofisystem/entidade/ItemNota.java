package com.ofisystem.entidade;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "item_nota")
public class ItemNota implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "item_nota_Id")
    private Integer id;

    @Column(nullable = false, name = "item_nota_descricao")
    private String descricao;

    @Column(nullable = false, name = "item_nota_quantidade")
    private Integer quantidade;

    @Column(nullable = false, name = "item_nota_valor_unitrio")
    private Double valorUnitario;

    @Column(nullable = false, name = "item_nota-subtotal")
    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "nota_fiscal_id", referencedColumnName = "nota_fiscal_id", nullable = false)
    private NotaFiscal notaFiscal;

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

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscal notaFiscal) {
        this.notaFiscal = notaFiscal;
    }
}
