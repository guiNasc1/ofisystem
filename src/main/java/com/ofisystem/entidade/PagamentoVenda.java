package com.ofisystem.entidade;

import Enums.FormaPagamento;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "pagamento_venda")
public class PagamentoVenda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pagamento_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "pagamento_forma")
    private FormaPagamento forma;

    @Column(nullable = false, name = "pagamento_valor")
    private Double valor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    public PagamentoVenda() {
    }

    public PagamentoVenda(FormaPagamento forma, Double valor) {
        this.forma = forma;
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagamentoVenda that = (PagamentoVenda) o;
        return id != null && Objects.equals(id, that.id);
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

    public FormaPagamento getForma() {
        return forma;
    }

    public void setForma(FormaPagamento forma) {
        this.forma = forma;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }
}
