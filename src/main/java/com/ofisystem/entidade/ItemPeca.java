package com.ofisystem.entidade;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "item_peca")
public class ItemPeca extends RegistroDoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_peca_id")
    private Integer id;

    @Column(nullable = false, name = "item_peca_quantidade")
    private Integer quantidade;

    @Column(nullable = false, name = "item_peca_valor_unitario")
    private Double valorUnitario;

    @Column(nullable = false, name = "item_peca_subtotal")
    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "peca_id", referencedColumnName = "peca_id", nullable = false)
    private Peca peca;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", referencedColumnName = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    public void calcularSubtotal() {
        subtotal = quantidade * valorUnitario;
    }

    @Override
    public String toString() {
        return "itemPeca{" +
                "id=" + id +
                ", quantidade=" + quantidade +
                ", valorUnitario=" + valorUnitario +
                ", subtotal=" + subtotal +
                ", peca=" + peca +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPeca itemPeca = (ItemPeca) o;
        return id != null && Objects.equals(id, itemPeca.id);
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

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }
}
