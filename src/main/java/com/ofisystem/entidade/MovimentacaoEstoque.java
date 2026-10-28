package com.ofisystem.entidade;

import Enums.TipoMovimentacao;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoque implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mov_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "mov_tipo")
    private TipoMovimentacao tipo;

    @Column(nullable = false, name = "mov_quantidade")
    private Integer quantidade;

    @Column(nullable = false, name = "mov_saldo")
    private Integer saldo;

    @Column(nullable = false, name = "mov_data")
    private LocalDateTime data = LocalDateTime.now();

    @Column(name = "mov_observacao")
    private String observacao;

    @Column(name = "mov_usuario")
    private String usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    private Venda venda;

    public String getDocumento() {
        if (ordemServico != null) return "OS " + ordemServico.getNumero();
        if (venda != null) return "Venda " + venda.getNumero();
        return "";
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }
}
