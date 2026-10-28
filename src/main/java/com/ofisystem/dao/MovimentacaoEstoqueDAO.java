package com.ofisystem.dao;

import Enums.TipoMovimentacao;
import com.ofisystem.entidade.MovimentacaoEstoque;

import java.time.LocalDate;
import java.util.List;

public class MovimentacaoEstoqueDAO extends AbstractFacade<MovimentacaoEstoque> {

    public MovimentacaoEstoqueDAO() {
        super(MovimentacaoEstoque.class);
    }

    public List<MovimentacaoEstoque> filtrar(String peca, TipoMovimentacao tipo, LocalDate de, LocalDate ate) {
        return consultar(em -> new Consulta(
                "SELECT m FROM MovimentacaoEstoque m JOIN FETCH m.peca p LEFT JOIN FETCH m.ordemServico LEFT JOIN FETCH m.venda")
                .contem("CONCAT(CAST(p.id AS String), ' ', p.nome)", "peca", peca)
                .igual("m.tipo", "tipo", tipo)
                .maiorIgual("m.data", "de", de == null ? null : de.atStartOfDay())
                .menor("m.data", "ate", ate == null ? null : ate.plusDays(1).atStartOfDay())
                .listar(em, "ORDER BY m.data DESC, m.id DESC", MovimentacaoEstoque.class));
    }
}
