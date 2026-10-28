package com.ofisystem.ui.panels.estoque;

import Enums.TipoMovimentacao;
import com.ofisystem.controle.estoque.EstoqueController;
import com.ofisystem.entidade.MovimentacaoEstoque;
import com.ofisystem.ui.panels.AbstractConsultaPanel;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class MovimentacaoEstoquePanel extends AbstractConsultaPanel<MovimentacaoEstoque> {

    private static final EstoqueController CONTROLLER = new EstoqueController();

    private JTextField txtPeca;
    private JComboBox<Object> cbTipo;
    private JFormattedTextField txtDe;
    private JFormattedTextField txtAte;

    @Override
    protected String titulo() {
        return "Movimentação de Estoque";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Data", "Peça", "Tipo", "Qtd.", "Saldo", "Documento", "Motivo", "Usuário"};
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        txtPeca = criarCampo(100);
        cbTipo = new JComboBox<>(new Object[]{"Todos", TipoMovimentacao.ENTRADA, TipoMovimentacao.SAIDA});
        txtDe = criarCampoData();
        txtAte = criarCampoData();
        definirData(txtDe, LocalDate.now().minusDays(30));

        adicionarCampo(painel, gbc, 0, 0, "Peça:", txtPeca);
        adicionarCampo(painel, gbc, 0, 1, "Tipo:", cbTipo);
        adicionarCampo(painel, gbc, 1, 0, "Período de:", txtDe);
        adicionarCampo(painel, gbc, 1, 1, "até:", txtAte);
        return 2;
    }

    @Override
    protected void limparFiltros() {
        super.limparFiltros();
        definirData(txtDe, LocalDate.now().minusDays(30));
    }

    @Override
    protected Supplier<List<MovimentacaoEstoque>> consulta() {
        String peca = txtPeca.getText();
        TipoMovimentacao tipo = cbTipo.getSelectedItem() instanceof TipoMovimentacao t ? t : null;
        LocalDate de = Formatador.parseData(txtDe.getText());
        LocalDate ate = Formatador.parseData(txtAte.getText());
        return () -> CONTROLLER.filtrarMovimentacoes(peca, tipo, de, ate);
    }

    @Override
    protected Object[] linha(MovimentacaoEstoque m) {
        return new Object[]{
                Formatador.dataHora(m.getData()),
                m.getPeca().toString(),
                m.getTipo().getDescricao(),
                m.getQuantidade(),
                m.getSaldo(),
                m.getDocumento(),
                Formatador.texto(m.getObservacao()),
                Formatador.texto(m.getUsuario())
        };
    }

    @Override
    protected String colunaEtiqueta() {
        return "Tipo";
    }

    @Override
    protected Tema.Tom corLinha(MovimentacaoEstoque registro) {
        return registro.getTipo() == TipoMovimentacao.SAIDA ? Tema.Tom.ATENCAO : Tema.Tom.SUCESSO;
    }

    @Override
    protected void aposMontarTela() {
        larguraColuna(1, 220);
        larguraColuna(6, 220);
    }

    @Override
    protected void montarBotoes(JPanel painel) {
        adicionarBotao(painel, "Entrada", COR_SUCESSO, () -> novaMovimentacao(TipoMovimentacao.ENTRADA));
        adicionarBotao(painel, "Saída", COR_PERIGO, () -> novaMovimentacao(TipoMovimentacao.SAIDA));
    }

    private void novaMovimentacao(TipoMovimentacao tipo) {
        executarAsync(CONTROLLER::listarAtivos, estoques -> {
            if (estoques.isEmpty()) {
                mostrarAviso("Nenhuma peça ativa cadastrada.");
                return;
            }
            MovimentacaoForm form = new MovimentacaoForm(estoques, null, tipo);
            if (!form.exibir(this)) return;
            try {
                Integer quantidade = form.quantidade();
                executarAsync(() -> CONTROLLER.movimentar(form.peca(), form.tipo(), quantidade, form.observacao()),
                        "Movimentação registrada com sucesso!", this::pesquisar);
            } catch (IllegalArgumentException ex) {
                mostrarAviso(ex.getMessage());
            }
        });
    }
}
