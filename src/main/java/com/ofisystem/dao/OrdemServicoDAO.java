package com.ofisystem.dao;

import Enums.FormaPagamento;
import Enums.StatusOS;
import Enums.TipoMovimentacao;
import com.ofisystem.entidade.ItemPeca;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.util.RegraNegocioException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrdemServicoDAO extends AbstractFacade<OrdemServico> {

    public OrdemServicoDAO() {
        super(OrdemServico.class);
    }

    public List<OrdemServico> filtrar(String numero, String cliente, String placa, StatusOS status,
                                      boolean somenteAbertas, LocalDate de, LocalDate ate) {
        return consultar(em -> new Consulta(
                "SELECT o FROM OrdemServico o JOIN FETCH o.cliente c")
                .contem("o.numero", "numero", numero)
                .contem("c.nome", "cliente", cliente)
                .contem("o.veiculoPlaca", "placa", placa == null ? null : placa.replace("-", ""))
                .igual("o.status", "status", status)
                .se(somenteAbertas, "o.status NOT IN (:finalizadas)", "finalizadas",
                        List.of(StatusOS.CONCLUIDA, StatusOS.CANCELADA))
                .maiorIgual("o.dataAbertura", "de", de == null ? null : de.atStartOfDay())
                .menor("o.dataAbertura", "ate", ate == null ? null : ate.plusDays(1).atStartOfDay())
                .listar(em, "ORDER BY o.dataAbertura DESC", OrdemServico.class));
    }

    public List<OrdemServico> concluidasNoPeriodo(LocalDate de, LocalDate ate) {
        return consultar(em -> new Consulta(
                "SELECT o FROM OrdemServico o JOIN FETCH o.cliente")
                .igual("o.status", "status", StatusOS.CONCLUIDA)
                .maiorIgual("o.dataConclusao", "de", de == null ? null : de.atStartOfDay())
                .menor("o.dataConclusao", "ate", ate == null ? null : ate.plusDays(1).atStartOfDay())
                .listar(em, "ORDER BY o.dataConclusao", OrdemServico.class));
    }

    public OrdemServico buscarCompleta(Integer id) {
        return consultar(em -> carregarCompleta(em, id));
    }

    static OrdemServico carregarCompleta(EntityManager em, Integer id) {
        OrdemServico os = em.createQuery(
                        "SELECT o FROM OrdemServico o LEFT JOIN FETCH o.itensServico WHERE o.id = :id", OrdemServico.class)
                .setParameter("id", id)
                .getResultStream().findFirst().orElse(null);
        if (os != null) {
            em.createQuery("SELECT o FROM OrdemServico o LEFT JOIN FETCH o.itensPeca WHERE o.id = :id", OrdemServico.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
        return os;
    }

    public OrdemServico salvarComEstoque(OrdemServico os, String usuario) {
        boolean nova = os.getId() == null;
        try {
            return gravarComEstoque(os, usuario);
        } catch (RuntimeException e) {
            if (nova) {
                os.setNumero(null);
            }
            throw e;
        }
    }

    private OrdemServico gravarComEstoque(OrdemServico os, String usuario) {
        return emTransacao(em -> {
            Map<Integer, Integer> anterior = new HashMap<>();
            if (os.getId() != null) {
                OrdemServico atual = achar(em, OrdemServico.class, os.getId(), LockModeType.PESSIMISTIC_WRITE);
                if (atual == null) {
                    throw new RegraNegocioException("Ordem de serviço não encontrada.");
                }
                if (!atual.getStatus().isEditavel()) {
                    throw new RegraNegocioException("A OS " + atual.getNumero() + " está "
                            + atual.getStatus().getDescricao().toLowerCase() + " e não pode ser alterada.");
                }
                em.createQuery("SELECT i.peca.id, SUM(i.quantidade) FROM ItemPeca i"
                                + " WHERE i.ordemServico.id = :id GROUP BY i.peca.id", Object[].class)
                        .setParameter("id", os.getId())
                        .getResultList()
                        .forEach(r -> anterior.put((Integer) r[0], ((Number) r[1]).intValue()));
            } else {
                os.setNumero(proximoNumero(em));
            }

            Map<Integer, Integer> novo = new HashMap<>();
            for (ItemPeca item : os.getItensPeca()) {
                novo.merge(item.getPeca().getId(), item.getQuantidade(), Integer::sum);
            }

            OrdemServico salva = em.merge(os);

            Set<Integer> pecas = new HashSet<>(anterior.keySet());
            pecas.addAll(novo.keySet());
            for (Integer pecaId : pecas) {
                int diferenca = novo.getOrDefault(pecaId, 0) - anterior.getOrDefault(pecaId, 0);
                if (diferenca > 0) {
                    EstoqueDAO.movimentar(em, pecaId, TipoMovimentacao.SAIDA, diferenca,
                            "Saída para OS " + salva.getNumero(), usuario, salva, null);
                } else if (diferenca < 0) {
                    EstoqueDAO.movimentar(em, pecaId, TipoMovimentacao.ENTRADA, -diferenca,
                            "Devolução da OS " + salva.getNumero(), usuario, salva, null);
                }
            }
            return salva;
        });
    }

    public void alterarStatus(Integer id, StatusOS novoStatus) {
        if (novoStatus == null || !novoStatus.isEditavel()) {
            throw new RegraNegocioException("Use as opções Concluir e receber ou Cancelar.");
        }
        emTransacao(em -> {
            OrdemServico os = travarEditavel(em, id);
            os.setStatus(novoStatus);
            return null;
        });
    }

    public void concluir(Integer id, FormaPagamento formaPagamento) {
        emTransacao(em -> {
            OrdemServico os = travarEditavel(em, id);
            if (formaPagamento == null) {
                throw new RegraNegocioException("Selecione a forma de pagamento.");
            }
            OrdemServico completa = carregarCompleta(em, id);
            completa.recalcularTotais();
            if (completa.getValorTotal() <= 0) {
                throw new RegraNegocioException("Adicione serviços ou peças com valor maior que zero antes de concluir a OS.");
            }
            os.setStatus(StatusOS.CONCLUIDA);
            os.setDataConclusao(LocalDateTime.now());
            os.setFormaPagamento(formaPagamento);
            return null;
        });
    }

    public void cancelar(Integer id, String usuario) {
        emTransacao(em -> {
            OrdemServico os = travarEditavel(em, id);
            for (ItemPeca item : carregarCompleta(em, id).getItensPeca()) {
                EstoqueDAO.movimentar(em, item.getPeca().getId(), TipoMovimentacao.ENTRADA, item.getQuantidade(),
                        "Cancelamento da OS " + os.getNumero(), usuario, os, null);
            }
            os.setStatus(StatusOS.CANCELADA);
            os.setDataConclusao(LocalDateTime.now());
            return null;
        });
    }

    private OrdemServico travarEditavel(EntityManager em, Integer id) {
        OrdemServico os = achar(em, OrdemServico.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (os == null) {
            throw new RegraNegocioException("Ordem de serviço não encontrada.");
        }
        if (!os.getStatus().isEditavel()) {
            throw new RegraNegocioException("A OS " + os.getNumero() + " já está "
                    + os.getStatus().getDescricao().toLowerCase() + ".");
        }
        return os;
    }

    private String proximoNumero(EntityManager em) {
        String ultimo = em.createQuery("SELECT MAX(o.numero) FROM OrdemServico o", String.class).getSingleResult();
        int proximo = ultimo == null ? 1 : Integer.parseInt(ultimo) + 1;
        return String.format("%06d", proximo);
    }
}
