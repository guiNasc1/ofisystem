package com.ofisystem.entidade;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "estoque")
public class Estoque implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estoque_id")
    private Long id;

    @Column(name = "estoque_nome", nullable = false)
    private String nome;

    @Column(name = "estoque_categoria")
    private String categoria;        // ex: "Filtros", "Pneus", "Freios"

    @Column(name = "estoque_quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "estoque_quantidade_minima")
    private Integer quantidadeMinima; // alerta de estoque baixo

    @Column(name = "estoque_preco_venda", nullable = false)
    private BigDecimal precoVenda;

    public Estoque() {
        this.quantidade = 0;
        this.quantidadeMinima = 1;
    }

    public boolean isEstoqueBaixo() {
        return this.quantidade <= this.quantidadeMinima;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Integer getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(Integer quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    @Override
    public String toString() { return nome + " (Qtd: " + quantidade + ")"; }
}