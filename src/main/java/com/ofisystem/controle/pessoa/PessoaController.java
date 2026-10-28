package com.ofisystem.controle.pessoa;

import Enums.TipoPessoa;
import com.ofisystem.controle.AbstractController;
import com.ofisystem.dao.PessoaDAO;
import com.ofisystem.entidade.Endereco;
import com.ofisystem.entidade.Pessoa;
import com.ofisystem.util.DocumentoUtil;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RegraNegocioException;

import java.util.List;

public class PessoaController extends AbstractController<Pessoa> {

    private final PessoaDAO pessoaDAO = new PessoaDAO();

    public Pessoa salvar(Pessoa pessoa) {
        validar(pessoa);
        return pessoaDAO.salvar(pessoa);
    }

    public void excluir(Pessoa pessoa) {
        if (pessoaDAO.possuiMovimento(pessoa.getId())) {
            throw new RegraNegocioException("Esta pessoa possui ordens de serviço ou vendas vinculadas."
                    + "\nEdite o cadastro e desmarque \"Ativo\" para inativá-la.");
        }
        pessoaDAO.excluir(pessoa.getId());
    }

    public List<Pessoa> filtrar(String nome, String documento, String telefone, String cidade, String papel, Boolean ativo) {
        return pessoaDAO.filtrar(nome, documento, telefone, cidade, papel, ativo);
    }

    public Pessoa buscarCompleta(Integer id) {
        return pessoaDAO.buscarCompleta(id);
    }

    public List<Pessoa> listarClientesAtivos() {
        return pessoaDAO.listarClientesAtivos();
    }

    public List<Pessoa> listarAtivas() {
        return pessoaDAO.listarAtivas();
    }

    private void validar(Pessoa pessoa) {
        if (pessoa.getNome() == null || pessoa.getNome().trim().length() < 3) {
            throw new RegraNegocioException("Informe o nome (mínimo 3 caracteres).");
        }

        String documento = Formatador.somenteDigitos(pessoa.getDocumento());
        pessoa.setDocumento(documento);
        if (pessoa.getTipo() == TipoPessoa.FISICA && !DocumentoUtil.cpfValido(documento)) {
            throw new RegraNegocioException("CPF inválido.");
        }
        if (pessoa.getTipo() == TipoPessoa.JURIDICA && !DocumentoUtil.cnpjValido(documento)) {
            throw new RegraNegocioException("CNPJ inválido.");
        }
        if (pessoaDAO.existeDocumento(documento, pessoa.getId())) {
            throw new RegraNegocioException("Já existe uma pessoa cadastrada com este "
                    + (pessoa.getTipo() == TipoPessoa.FISICA ? "CPF." : "CNPJ."));
        }

        if (!pessoa.isCliente() && !pessoa.isFornecedor() && !pessoa.isFuncionario()) {
            throw new RegraNegocioException("Marque pelo menos um papel: Cliente, Fornecedor ou Funcionário.");
        }

        String telefone = Formatador.somenteDigitos(pessoa.getTelefone());
        if (!telefone.isEmpty() && telefone.length() != 10 && telefone.length() != 11) {
            throw new RegraNegocioException("Telefone inválido. Informe DDD + número.");
        }
        pessoa.setTelefone(telefone.isEmpty() ? null : telefone);

        if (pessoa.getEmail() != null && !pessoa.getEmail().isBlank()
                && !pessoa.getEmail().trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new RegraNegocioException("E-mail inválido.");
        }

        Endereco endereco = pessoa.getEnderecoPrincipal();
        if (endereco != null) {
            String cep = Formatador.somenteDigitos(endereco.getCep());
            if (!cep.isEmpty() && cep.length() != 8) {
                throw new RegraNegocioException("CEP inválido.");
            }
            endereco.setCep(cep.isEmpty() ? null : cep);
            if (endereco.getEstado() != null && endereco.getEstado().length() > 2) {
                throw new RegraNegocioException("Informe a UF com 2 letras.");
            }
        }
    }
}
