package com.ofisystem.entidade;

import Enums.Status;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "nota_fiscal")
public class NotaFiscal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nota_fiscal_id")
    private Integer id;

    @Column(nullable = false, name = "nota_fiscal_numero")
    private String numero;

    @Column(nullable = false, name = "nota_fiscal_Data_emissao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEmissao;

    @Column(nullable = false, name = "nota_fiscal_valor_total")
    private Double valorTotal;

    @Column(nullable = false, name = "nota_fiscal_impostos")
    private Double impostos;

    @Enumerated(EnumType.STRING)
    @Column(name = "nota_fiscal_status")
    private Status status;

    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemNota> itens = new ArrayList<>();

    @Override
    public String toString() {
        return "NotaFiscal{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", dataEmissao=" + dataEmissao +
                ", valorTotal=" + valorTotal +
                ", impostos=" + impostos +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NotaFiscal that = (NotaFiscal) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getNumero(), that.getNumero()) && Objects.equals(getDataEmissao(), that.getDataEmissao()) && Objects.equals(getValorTotal(), that.getValorTotal()) && Objects.equals(getImpostos(), that.getImpostos()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNumero(), getDataEmissao(), getValorTotal(), getImpostos(), getStatus());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Double getImpostos() {
        return impostos;
    }

    public void setImpostos(Double impostos) {
        this.impostos = impostos;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ItemNota> getItens() {
        return itens;
    }

    public void setItens(List<ItemNota> itens) {
        this.itens = itens;
    }
}
