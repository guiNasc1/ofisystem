package com.ofisystem.entidades;

import Enums.Status;

import java.util.Date;
import java.util.Objects;

public class OrdemServico {

    private Integer id;
    private String numero;
    private Date dataAbertura;
    private Date dataConclusao;
    private Status status;
    private String descricao;
    private Double valorTotal;
    private String cliente;

    @Override
    public String toString() {
        return "OrdemServico{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", dataAbertura=" + dataAbertura +
                ", dataConclusao=" + dataConclusao +
                ", status=" + status +
                ", descricao='" + descricao + '\'' +
                ", valorTotal=" + valorTotal +
                ", cliente='" + cliente + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrdemServico that = (OrdemServico) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getNumero(), that.getNumero()) && Objects.equals(getDataAbertura(), that.getDataAbertura()) && Objects.equals(getDataConclusao(), that.getDataConclusao()) && getStatus() == that.getStatus() && Objects.equals(getDescricao(), that.getDescricao()) && Objects.equals(getValorTotal(), that.getValorTotal()) && Objects.equals(getCliente(), that.getCliente());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNumero(), getDataAbertura(), getDataConclusao(), getStatus(), getDescricao(), getValorTotal(), getCliente());
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

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}
