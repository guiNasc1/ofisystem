package com.ofisystem.dao;

import Enums.StatusOS;
import Enums.StatusVenda;
import com.ofisystem.util.JPAutil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RelatorioDAO {

    private static final String FILTRO_OS = " o.status = :statusOs AND o.dataConclusao >= :de AND o.dataConclusao < :ate";
    private static final String FILTRO_VENDA = " v.status = :statusVenda AND v.data >= :de AND v.data < :ate";

    public List<Object[]> vendasPorProduto(LocalDate de, LocalDate ate, boolean incluirOs, boolean incluirBalcao) {
        return somar(de, ate, incluirOs, incluirBalcao,
                "SELECT p.id, p.nome, SUM(i.quantidade), SUM(i.subtotal) FROM ItemPeca i JOIN i.peca p JOIN i.ordemServico o"
                        + " WHERE" + FILTRO_OS + " GROUP BY p.id, p.nome",
                "SELECT p.id, p.nome, SUM(i.quantidade), SUM(i.subtotal) FROM ItemVenda i JOIN i.peca p JOIN i.venda v"
                        + " WHERE" + FILTRO_VENDA + " GROUP BY p.id, p.nome",
                2);
    }

    public List<Object[]> vendasPorServico(LocalDate de, LocalDate ate, boolean incluirOs, boolean incluirBalcao) {
        return somar(de, ate, incluirOs, incluirBalcao,
                "SELECT i.descricao, SUM(i.quantidade), SUM(i.subtotal) FROM ItemOs i JOIN i.ordemServico o"
                        + " WHERE" + FILTRO_OS + " GROUP BY i.descricao",
                "SELECT i.descricao, SUM(i.quantidade), SUM(i.subtotal) FROM ItemVenda i JOIN i.venda v"
                        + " WHERE i.servico IS NOT NULL AND" + FILTRO_VENDA + " GROUP BY i.descricao",
                1);
    }

    public List<Object[]> vendasPorCliente(LocalDate de, LocalDate ate, boolean incluirOs, boolean incluirBalcao) {
        return somar(de, ate, incluirOs, incluirBalcao,
                "SELECT c.nome, COUNT(o), SUM(o.valorTotal) FROM OrdemServico o JOIN o.cliente c"
                        + " WHERE" + FILTRO_OS + " GROUP BY c.nome",
                "SELECT COALESCE(c.nome, 'Consumidor final'), COUNT(v), SUM(v.valorTotal) FROM Venda v LEFT JOIN v.cliente c"
                        + " WHERE" + FILTRO_VENDA + " GROUP BY COALESCE(c.nome, 'Consumidor final')",
                1);
    }

    private List<Object[]> somar(LocalDate de, LocalDate ate, boolean incluirOs, boolean incluirBalcao,
                                 String jpqlOs, String jpqlVenda, int colunasChave) {
        Map<String, Object[]> agrupado = new LinkedHashMap<>();
        EntityManager em = JPAutil.getEntityManager();
        try {
            if (incluirOs) {
                acumular(agrupado, em.createQuery(jpqlOs, Object[].class)
                        .setParameter("statusOs", StatusOS.CONCLUIDA)
                        .setParameter("de", de.atStartOfDay())
                        .setParameter("ate", ate.plusDays(1).atStartOfDay())
                        .getResultList(), colunasChave);
            }
            if (incluirBalcao) {
                acumular(agrupado, em.createQuery(jpqlVenda, Object[].class)
                        .setParameter("statusVenda", StatusVenda.FINALIZADA)
                        .setParameter("de", de.atStartOfDay())
                        .setParameter("ate", ate.plusDays(1).atStartOfDay())
                        .getResultList(), colunasChave);
            }
        } finally {
            em.close();
        }
        List<Object[]> linhas = new ArrayList<>(agrupado.values());
        linhas.sort((a, b) -> Double.compare(((Number) b[b.length - 1]).doubleValue(), ((Number) a[a.length - 1]).doubleValue()));
        return linhas;
    }

    private void acumular(Map<String, Object[]> agrupado, List<Object[]> resultado, int colunasChave) {
        for (Object[] r : resultado) {
            String chave = String.valueOf(r[0]);
            Object[] atual = agrupado.get(chave);
            if (atual == null) {
                agrupado.put(chave, r.clone());
                continue;
            }
            for (int i = colunasChave; i < r.length; i++) {
                if (r[i] instanceof Double || atual[i] instanceof Double) {
                    atual[i] = ((Number) atual[i]).doubleValue() + ((Number) r[i]).doubleValue();
                } else {
                    atual[i] = ((Number) atual[i]).longValue() + ((Number) r[i]).longValue();
                }
            }
        }
    }
}
