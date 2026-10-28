package com.ofisystem.entidade;

import Enums.FormaPagamento;
import Enums.StatusOS;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "ordem_servico", uniqueConstraints = @UniqueConstraint(
        name = "uk_ordem_servico_usuario_numero", columnNames = {"usuario_id", "ordem_servico_numero"}))
public class OrdemServico extends RegistroDoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "ordem_servico_id")
    private Integer id;

    @Column(nullable = false, name = "ordem_servico_numero")
    private String numero;

    @Column(nullable = false, name = "ordem_servico_data_abertura")
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "ordem_servico_data_previsao")
    private LocalDate dataPrevisao;

    @Column(name = "ordem_servico_data_conclusao")
    private LocalDateTime dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "ordem_servico_status")
    private StatusOS status = StatusOS.ABERTA;

    @Column(nullable = false, length = 1000, name = "ordem_servico_descricao")
    private String descricao;

    @Column(length = 1000, name = "ordem_servico_diagnostico")
    private String diagnostico;

    @Column(name = "ordem_servico_km")
    private Integer km;

    @Column(nullable = false, name = "ordem_servico_valor_servicos")
    private Double valorServicos = 0.0;

    @Column(nullable = false, name = "ordem_servico_valor_pecas")
    private Double valorPecas = 0.0;

    @Column(nullable = false, name = "ordem_servico_desconto")
    private Double desconto = 0.0;

    @Column(nullable = false, name = "ordem_servico_valor_total")
    private Double valorTotal = 0.0;

    @Column(name = "ordem_servico_responsavel")
    private String responsavel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa cliente;

    @Column(nullable = false, name = "ordem_servico_possui_veiculo")
    private boolean possuiVeiculo;

    @Column(name = "ordem_servico_veiculo_placa")
    private String veiculoPlaca;

    @Column(name = "ordem_servico_veiculo_marca")
    private String veiculoMarca;

    @Column(name = "ordem_servico_veiculo_modelo")
    private String veiculoModelo;

    @Column(name = "ordem_servico_veiculo_ano")
    private Integer veiculoAno;

    @Enumerated(EnumType.STRING)
    @Column(name = "ordem_servico_forma_pagamento")
    private FormaPagamento formaPagamento;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOs> itensServico = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPeca> itensPeca = new ArrayList<>();

    public void adicionarServico(ItemOs item) {
        item.setOrdemServico(this);
        itensServico.add(item);
    }

    public void adicionarPeca(ItemPeca item) {
        item.setOrdemServico(this);
        itensPeca.add(item);
    }

    public void recalcularTotais() {
        valorServicos = itensServico.stream().mapToDouble(ItemOs::getSubtotal).sum();
        valorPecas = itensPeca.stream().mapToDouble(ItemPeca::getSubtotal).sum();
        valorTotal = Math.max(0, valorServicos + valorPecas - (desconto == null ? 0 : desconto));
    }

    public String getVeiculoDescricao() {
        if (!possuiVeiculo) return "";
        StringJoiner partes = new StringJoiner(" ");
        if (veiculoPlaca != null && !veiculoPlaca.isBlank()) partes.add(veiculoPlaca + " -");
        if (veiculoMarca != null && !veiculoMarca.isBlank()) partes.add(veiculoMarca);
        if (veiculoModelo != null) partes.add(veiculoModelo);
        if (veiculoAno != null) partes.add("(" + veiculoAno + ")");
        return partes.toString();
    }

    @Override
    public String toString() {
        return "OS " + numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdemServico that = (OrdemServico) o;
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

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDate getDataPrevisao() {
        return dataPrevisao;
    }

    public void setDataPrevisao(LocalDate dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public StatusOS getStatus() {
        return status;
    }

    public void setStatus(StatusOS status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public Double getValorServicos() {
        return valorServicos;
    }

    public void setValorServicos(Double valorServicos) {
        this.valorServicos = valorServicos;
    }

    public Double getValorPecas() {
        return valorPecas;
    }

    public void setValorPecas(Double valorPecas) {
        this.valorPecas = valorPecas;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Pessoa getCliente() {
        return cliente;
    }

    public void setCliente(Pessoa cliente) {
        this.cliente = cliente;
    }

    public boolean isPossuiVeiculo() {
        return possuiVeiculo;
    }

    public void setPossuiVeiculo(boolean possuiVeiculo) {
        this.possuiVeiculo = possuiVeiculo;
    }

    public String getVeiculoPlaca() {
        return veiculoPlaca;
    }

    public void setVeiculoPlaca(String veiculoPlaca) {
        this.veiculoPlaca = veiculoPlaca;
    }

    public String getVeiculoMarca() {
        return veiculoMarca;
    }

    public void setVeiculoMarca(String veiculoMarca) {
        this.veiculoMarca = veiculoMarca;
    }

    public String getVeiculoModelo() {
        return veiculoModelo;
    }

    public void setVeiculoModelo(String veiculoModelo) {
        this.veiculoModelo = veiculoModelo;
    }

    public Integer getVeiculoAno() {
        return veiculoAno;
    }

    public void setVeiculoAno(Integer veiculoAno) {
        this.veiculoAno = veiculoAno;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<ItemOs> getItensServico() {
        return itensServico;
    }

    public void setItensServico(List<ItemOs> itensServico) {
        this.itensServico = itensServico;
    }

    public List<ItemPeca> getItensPeca() {
        return itensPeca;
    }

    public void setItensPeca(List<ItemPeca> itensPeca) {
        this.itensPeca = itensPeca;
    }
}
