package com.ofisystem.dao;

import Enums.StatusOS;
import com.ofisystem.entidade.ItemNota;
import com.ofisystem.entidade.ItemOs;
import com.ofisystem.entidade.ItemPeca;
import com.ofisystem.entidade.NotaFiscal;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.util.RegraNegocioException;

import java.time.LocalDateTime;

public class NotaFiscalDAO extends AbstractFacade<NotaFiscal> {

    public NotaFiscalDAO() {
        super(NotaFiscal.class);
    }

    public NotaFiscal buscarPorOrdemServico(Integer osId) {
        return consultar(em -> em.createQuery(
                        "SELECT n FROM NotaFiscal n LEFT JOIN FETCH n.itens WHERE n.ordemServico.id = :id", NotaFiscal.class)
                .setParameter("id", osId)
                .getResultStream().findFirst().orElse(null));
    }

    public NotaFiscal emitir(Integer osId, double aliquota) {
        return emTransacao(em -> {
            OrdemServico os = OrdemServicoDAO.carregarCompleta(em, osId);
            if (os == null || os.getStatus() != StatusOS.CONCLUIDA) {
                throw new RegraNegocioException("A nota fiscal só pode ser emitida para OS concluída.");
            }
            Long existentes = em.createQuery(
                            "SELECT COUNT(n) FROM NotaFiscal n WHERE n.ordemServico.id = :id", Long.class)
                    .setParameter("id", osId).getSingleResult();
            if (existentes > 0) {
                throw new RegraNegocioException("Já existe nota fiscal emitida para a OS " + os.getNumero() + ".");
            }

            String ultimo = em.createQuery("SELECT MAX(n.numero) FROM NotaFiscal n", String.class).getSingleResult();
            int proximo = ultimo == null ? 1 : Integer.parseInt(ultimo) + 1;

            NotaFiscal nota = new NotaFiscal();
            nota.setNumero(String.format("%06d", proximo));
            nota.setDataEmissao(LocalDateTime.now());
            nota.setOrdemServico(os);
            nota.setValorTotal(os.getValorTotal());
            nota.setImpostos(Math.round(os.getValorTotal() * aliquota * 100.0) / 100.0);

            for (ItemOs item : os.getItensServico()) {
                nota.adicionarItem(new ItemNota("Serviço: " + item.getDescricao(), item.getQuantidade(),
                        item.getValorUnitario(), item.getSubtotal()));
            }
            for (ItemPeca item : os.getItensPeca()) {
                nota.adicionarItem(new ItemNota("Peça: " + item.getPeca(), item.getQuantidade(),
                        item.getValorUnitario(), item.getSubtotal()));
            }
            if (os.getDesconto() != null && os.getDesconto() > 0) {
                nota.adicionarItem(new ItemNota("Desconto", 1, -os.getDesconto(), -os.getDesconto()));
            }

            em.persist(nota);
            return nota;
        });
    }
}
