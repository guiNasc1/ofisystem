package com.ofisystem.entidade;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "item_os")
public class ItemOs implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "item_os_id")
    private Integer id;

    @Column(nullable = false, name = "item_os_descricao")
    private String descricao;

    @Column(nullable = false, name = "item_os_quantidade")
    private Integer quantidade;

    @Column(nullable = false, name = "item_os_valor_unitario")
    private Double valorUnitario;

    @Column(nullable = false, name = "item_os_subtotal")
    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    @Override
    public String toString() {
        return "ItemOs{" +
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
        ItemOs itemOs = (ItemOs) o;
        return Objects.equals(getId(), itemOs.getId()) && Objects.equals(getDescricao(), itemOs.getDescricao()) && Objects.equals(getQuantidade(), itemOs.getQuantidade()) && Objects.equals(getValorUnitario(), itemOs.getValorUnitario()) && Objects.equals(getSubtotal(), itemOs.getSubtotal());
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

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }
}
