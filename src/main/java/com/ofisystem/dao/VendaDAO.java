package com.ofisystem.dao;

import Enums.StatusVenda;
import Enums.TipoMovimentacao;
import com.ofisystem.entidade.ItemVenda;
import com.ofisystem.entidade.Venda;
import com.ofisystem.util.RegraNegocioException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.util.List;

public class VendaDAO extends AbstractFacade<Venda> {

    public VendaDAO() {
        super(Venda.class);
    }

    public List<Venda> filtrar(String numero, String cliente, StatusVenda status, LocalDate de, LocalDate ate) {
        return consultar(em -> new Consulta(
                "SELECT DISTINCT v FROM Venda v LEFT JOIN FETCH v.cliente c LEFT JOIN FETCH v.pagamentos")
                .contem("v.numero", "numero", numero)
                .contem("COALESCE(c.nome, 'Consumidor final')", "cliente", cliente)
                .igual("v.status", "status", status)
                .maiorIgual("v.data", "de", de == null ? null : de.atStartOfDay())
                .menor("v.data", "ate", ate == null ? null : ate.plusDays(1).atStartOfDay())
                .listar(em, "ORDER BY v.data DESC", Venda.class));
    }

    public Venda buscarCompleta(Integer id) {
        return consultar(em -> carregarCompleta(em, id));
    }

    static Venda carregarCompleta(EntityManager em, Integer id) {
        Venda venda = em.createQuery(
                        "SELECT v FROM Venda v LEFT JOIN FETCH v.itens WHERE v.id = :id", Venda.class)
                .setParameter("id", id)
                .getResultStream().findFirst().orElse(null);
        if (venda != null) {
            em.createQuery("SELECT v FROM Venda v LEFT JOIN FETCH v.pagamentos WHERE v.id = :id", Venda.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
        return venda;
    }

    public Venda finalizar(Venda venda, String usuario) {
        try {
            return emTransacao(em -> {
                String ultimo = em.createQuery("SELECT MAX(v.numero) FROM Venda v", String.class).getSingleResult();
                venda.setNumero(String.format("%06d", ultimo == null ? 1 : Integer.parseInt(ultimo) + 1));
                venda.setUsuario(usuario);

                Venda salva = em.merge(venda);
                for (ItemVenda item : salva.getItens()) {
                    if (item.isProduto()) {
                        EstoqueDAO.movimentar(em, item.getPeca().getId(), TipoMovimentacao.SAIDA, item.getQuantidade(),
                                "Venda " + salva.getNumero(), usuario, null, salva);
                    }
                }
                return salva;
            });
        } catch (RuntimeException e) {
            venda.setNumero(null);
            throw e;
        }
    }

    public void cancelar(Integer id, String usuario) {
        emTransacao(em -> {
            Venda venda = em.find(Venda.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (venda == null) {
                throw new RegraNegocioException("Venda não encontrada.");
            }
            if (venda.getStatus() == StatusVenda.CANCELADA) {
                throw new RegraNegocioException("A venda " + venda.getNumero() + " já está cancelada.");
            }
            for (ItemVenda item : carregarCompleta(em, id).getItens()) {
                if (item.isProduto()) {
                    EstoqueDAO.movimentar(em, item.getPeca().getId(), TipoMovimentacao.ENTRADA, item.getQuantidade(),
                            "Cancelamento da venda " + venda.getNumero(), usuario, null, venda);
                }
            }
            venda.setStatus(StatusVenda.CANCELADA);
            return null;
        });
    }
}
