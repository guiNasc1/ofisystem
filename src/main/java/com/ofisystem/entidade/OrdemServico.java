package com.ofisystem.entidade;

import Enums.Status;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "item_nota")
public class OrdemServico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "ordem_servico_id")
    private Integer id;

    @Column(nullable = false, name = "ordem_servico_numero")
    private String numero;

    @Column(nullable = false, name = "ordem_servico_data_abertura")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAbertura;

    @Column(nullable = false, name = "ordem_servico_data_conclusao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(name = "ordem_servico_status")
    private Status status;

    @Column(nullable = false, name = "ordem_servico_descricao")
    private String descricao;

    @Column(nullable = false, name = "ordem_servico_valor_total")
    private Double valorTotal;

    @Column(nullable = false, name = "ordem_servico_cliente")
    private String cliente;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOs> itens = new ArrayList<>();

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

    public List<ItemOs> getItens() {
        return itens;
    }

    public void setItens(List<ItemOs> itens) {
        this.itens = itens;
    }
}
