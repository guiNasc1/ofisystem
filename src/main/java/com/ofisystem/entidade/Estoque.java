package com.ofisystem.entidade;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "estoque")
public class Estoque implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estoque_id")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "peca_id", nullable = false, unique = true)
    private Peca peca;

    @Column(name = "estoque_quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "estoque_quantidade_minima")
    private Integer quantidadeMinima;

    public Estoque() {
        this.quantidade = 0;
        this.quantidadeMinima = 1;
    }

    public boolean isEstoqueBaixo() {
        return quantidadeMinima != null && this.quantidade <= this.quantidadeMinima;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Integer getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(Integer quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }

    @Override
    public String toString() { return peca + " (Disp: " + quantidade + ")"; }
}
