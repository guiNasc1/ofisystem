package com.ofisystem.entidade;

import Enums.FormaPagamento;
import Enums.StatusVenda;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "venda")
public class Venda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venda_id")
    private Integer id;

    @Column(nullable = false, unique = true, name = "venda_numero")
    private String numero;

    @Column(nullable = false, name = "venda_data")
    private LocalDateTime data = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "venda_status")
    private StatusVenda status = StatusVenda.FINALIZADA;

    @Column(nullable = false, name = "venda_valor_produtos")
    private Double valorProdutos = 0.0;

    @Column(nullable = false, name = "venda_valor_servicos")
    private Double valorServicos = 0.0;

    @Column(nullable = false, name = "venda_desconto")
    private Double desconto = 0.0;

    @Column(nullable = false, name = "venda_valor_total")
    private Double valorTotal = 0.0;

    @Column(nullable = false, name = "venda_troco")
    private Double troco = 0.0;

    @Column(length = 500, name = "venda_observacao")
    private String observacao;

    @Column(name = "venda_usuario")
    private String usuario;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagamentoVenda> pagamentos = new ArrayList<>();

    public void adicionarItem(ItemVenda item) {
        item.setVenda(this);
        itens.add(item);
    }

    public void adicionarPagamento(PagamentoVenda pagamento) {
        pagamento.setVenda(this);
        pagamentos.add(pagamento);
    }

    public void recalcularTotais() {
        valorProdutos = itens.stream().filter(ItemVenda::isProduto).mapToDouble(ItemVenda::getSubtotal).sum();
        valorServicos = itens.stream().filter(i -> !i.isProduto()).mapToDouble(ItemVenda::getSubtotal).sum();
        valorTotal = Math.max(0, valorProdutos + valorServicos - (desconto == null ? 0 : desconto));
        troco = Math.max(0, getValorPago() - valorTotal);
    }

    public double getValorPago() {
        return pagamentos.stream().mapToDouble(PagamentoVenda::getValor).sum();
    }

    public double getValorEmDinheiro() {
        return pagamentos.stream().filter(p -> p.getForma() == FormaPagamento.DINHEIRO)
                .mapToDouble(PagamentoVenda::getValor).sum();
    }

    public String getFormasPagamento() {
        return pagamentos.stream().map(p -> p.getForma().getDescricao()).distinct()
                .reduce((a, b) -> a + ", " + b).orElse("");
    }

    @Override
    public String toString() {
        return "Venda " + numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venda venda = (Venda) o;
        return id != null && Objects.equals(id, venda.id);
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

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Pessoa getCliente() {
        return cliente;
    }

    public void setCliente(Pessoa cliente) {
        this.cliente = cliente;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }

    public Double getValorProdutos() {
        return valorProdutos;
    }

    public void setValorProdutos(Double valorProdutos) {
        this.valorProdutos = valorProdutos;
    }

    public Double getValorServicos() {
        return valorServicos;
    }

    public void setValorServicos(Double valorServicos) {
        this.valorServicos = valorServicos;
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

    public Double getTroco() {
        return troco;
    }

    public void setTroco(Double troco) {
        this.troco = troco;
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

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    public List<PagamentoVenda> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoVenda> pagamentos) {
        this.pagamentos = pagamentos;
    }
}
