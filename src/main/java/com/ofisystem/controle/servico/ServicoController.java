package com.ofisystem.controle.servico;

import com.ofisystem.controle.AbstractController;
import com.ofisystem.dao.ServicoDAO;
import com.ofisystem.entidade.Servico;
import com.ofisystem.util.RegraNegocioException;

import java.util.List;

public class ServicoController extends AbstractController<Servico> {

    private final ServicoDAO servicoDAO = new ServicoDAO();

    public Servico salvar(Servico servico) {
        if (servico.getNome() == null || servico.getNome().isBlank()) {
            throw new RegraNegocioException("Informe o nome do serviço.");
        }
        if (servico.getDescricao() == null || servico.getDescricao().isBlank()) {
            servico.setDescricao(servico.getNome());
        }
        if (servico.getValor() == null || servico.getValor() < 0) {
            throw new RegraNegocioException("Informe um valor válido.");
        }
        if (servico.getDuracaoMin() == null || servico.getDuracaoMin() <= 0) {
            throw new RegraNegocioException("Informe a duração estimada em minutos.");
        }
        if (servicoDAO.existeNome(servico.getNome(), servico.getId())) {
            throw new RegraNegocioException("Já existe um serviço com este nome.");
        }
        return servicoDAO.salvar(servico);
    }

    public void excluir(Servico servico) {
        if (servicoDAO.possuiOrdemServico(servico.getId())) {
            throw new RegraNegocioException("Este serviço já foi usado em ordens de serviço."
                    + "\nEdite o cadastro e desmarque \"Ativo\" para inativá-lo.");
        }
        servicoDAO.excluir(servico.getId());
    }

    public List<Servico> filtrar(String nome, Boolean ativo) {
        return servicoDAO.filtrar(nome, ativo);
    }

    public List<Servico> listarAtivos() {
        return servicoDAO.listarAtivos();
    }

    public Servico buscarPorId(Integer id) {
        return servicoDAO.buscarPorId(id);
    }
}
