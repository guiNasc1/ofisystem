package com.ofisystem.entidade;

import Enums.TipoPessoa;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "pessoa")
public class Pessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pessoa_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "pessoa_tipo")
    private TipoPessoa tipo = TipoPessoa.FISICA;

    @Column(nullable = false, name = "pessoa_nome")
    private String nome;

    @Column(nullable = false, unique = true, name = "pessoa_documento")
    private String documento;

    @Column(name = "pessoa_rg_ie")
    private String rgIe;

    @Column(name = "pessoa_telefone")
    private String telefone;

    @Column(name = "pessoa_email")
    private String email;

    @Column(nullable = false, name = "pessoa_cliente")
    private boolean cliente = true;

    @Column(nullable = false, name = "pessoa_fornecedor")
    private boolean fornecedor;

    @Column(nullable = false, name = "pessoa_funcionario")
    private boolean funcionario;

    @Column(nullable = false, name = "pessoa_ativo")
    private boolean ativo = true;

    @Column(length = 1000, name = "pessoa_observacao")
    private String observacao;

    @Column(nullable = false, name = "pessoa_data_cadastro")
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos = new ArrayList<>();

    public Endereco getEnderecoPrincipal() {
        return enderecos.isEmpty() ? null : enderecos.get(0);
    }

    public void definirEndereco(Endereco endereco) {
        enderecos.clear();
        if (endereco != null) {
            endereco.setPessoa(this);
            enderecos.add(endereco);
        }
    }

    public String getPapeis() {
        StringJoiner papeis = new StringJoiner(", ");
        if (cliente) papeis.add("Cliente");
        if (fornecedor) papeis.add("Fornecedor");
        if (funcionario) papeis.add("Funcionário");
        return papeis.toString();
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return id != null && Objects.equals(id, pessoa.id);
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

    public TipoPessoa getTipo() {
        return tipo;
    }

    public void setTipo(TipoPessoa tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getRgIe() {
        return rgIe;
    }

    public void setRgIe(String rgIe) {
        this.rgIe = rgIe;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCliente() {
        return cliente;
    }

    public void setCliente(boolean cliente) {
        this.cliente = cliente;
    }

    public boolean isFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(boolean fornecedor) {
        this.fornecedor = fornecedor;
    }

    public boolean isFuncionario() {
        return funcionario;
    }

    public void setFuncionario(boolean funcionario) {
        this.funcionario = funcionario;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }
}
