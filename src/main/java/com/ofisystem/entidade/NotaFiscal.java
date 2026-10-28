package com.ofisystem.entidade;

import Enums.StatusNota;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "nota_fiscal")
public class NotaFiscal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nota_fiscal_id")
    private Integer id;

    @Column(nullable = false, unique = true, name = "nota_fiscal_numero")
    private String numero;

    @Column(nullable = false, name = "nota_fiscal_data_emissao")
    private LocalDateTime dataEmissao;

    @Column(nullable = false, name = "nota_fiscal_valor_total")
    private Double valorTotal;

    @Column(nullable = false, name = "nota_fiscal_impostos")
    private Double impostos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "nota_fiscal_status")
    private StatusNota status = StatusNota.EMITIDA;

    @OneToOne(optional = false)
    @JoinColumn(name = "ordem_servico_id", nullable = false, unique = true)
    private OrdemServico ordemServico;

    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemNota> itens = new ArrayList<>();

    public void adicionarItem(ItemNota item) {
        item.setNotaFiscal(this);
        itens.add(item);
    }

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotaFiscal that = (NotaFiscal) o;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
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

    public StatusNota getStatus() {
        return status;
    }

    public void setStatus(StatusNota status) {
        this.status = status;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public List<ItemNota> getItens() {
        return itens;
    }

    public void setItens(List<ItemNota> itens) {
        this.itens = itens;
    }
}
