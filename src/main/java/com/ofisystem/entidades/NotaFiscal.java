package com.ofisystem.entidades;

import Enums.Status;

import java.util.Date;
import java.util.Objects;

public class NotaFiscal {

    private Integer id;
    private String numero;
    private Date dataEmissao;
    private Double valorTotal;
    private Double impostos;
    private Status status;

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
}
