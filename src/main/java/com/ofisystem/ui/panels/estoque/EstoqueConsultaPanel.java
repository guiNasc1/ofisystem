package com.ofisystem.ui.panels.estoque;

import Enums.Status;
import Enums.TipoMovimentacao;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.controle.estoque.EstoqueController;
import com.ofisystem.entidade.Estoque;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractConsultaPanel;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class EstoqueConsultaPanel extends AbstractConsultaPanel<Estoque> {

    private static final EstoqueController CONTROLLER = new EstoqueController();

    private JTextField txtPeca;
    private JTextField txtCategoria;
    private JCheckBox chkBaixo;
    private JCheckBox chkInativas;

    @Override
    protected String titulo() {
        return "Consultar Estoque";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"ID", "Peça", "Categoria", "Valor unit.", "Qtd.", "Mínimo", "Situação", "Cadastro"};
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        txtPeca = criarCampo(100);
        txtCategoria = criarCampo(60);
        chkBaixo = new JCheckBox("Somente abaixo do mínimo");
        chkInativas = new JCheckBox("Incluir inativas");
        chkBaixo.setOpaque(false);
        chkInativas.setOpaque(false);
        JPanel opcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        opcoes.setOpaque(false);
        opcoes.add(chkBaixo);
        opcoes.add(chkInativas);

        adicionarCampo(painel, gbc, 0, 0, "ID/Nome:", txtPeca);
        adicionarCampo(painel, gbc, 0, 1, "Categoria:", txtCategoria);
        adicionarCampo(painel, gbc, 0, 2, "Opções:", opcoes);
        return 1;
    }

    @Override
    protected Supplier<List<Estoque>> consulta() {
        String peca = txtPeca.getText();
        String categoria = txtCategoria.getText();
        boolean baixo = chkBaixo.isSelected();
        boolean apenasAtivas = !chkInativas.isSelected();
        return () -> CONTROLLER.filtrar(peca, categoria, baixo, apenasAtivas);
    }

    @Override
    protected Object[] linha(Estoque e) {
        return new Object[]{
                e.getPeca().getId(),
                e.getPeca().getNome(),
                Formatador.texto(e.getPeca().getCategoria()),
                Formatador.moeda(e.getPeca().getValorUnitario()),
                e.getQuantidade(),
                e.getQuantidadeMinima(),
                e.isEstoqueBaixo() ? "Abaixo do mínimo" : "OK",
                e.getPeca().getStatus().getDescricao()
        };
    }

    @Override
    protected String resumo(List<Estoque> lista) {
        long baixos = lista.stream().filter(Estoque::isEstoqueBaixo).count();
        double valorTotal = lista.stream().mapToDouble(e -> e.getQuantidade() * e.getPeca().getValorUnitario()).sum();
        return lista.size() + " peça(s)  |  " + baixos + " abaixo do mínimo  |  Valor em estoque: " + Formatador.moeda(valorTotal);
    }

    @Override
    protected String colunaEtiqueta() {
        return "Situação";
    }

    @Override
    protected Tema.Tom corLinha(Estoque registro) {
        if (registro.getPeca().getStatus() != Status.ATIVO) return Tema.Tom.INATIVO;
        return registro.isEstoqueBaixo() ? Tema.Tom.CRITICO : Tema.Tom.SUCESSO;
    }

    @Override
    protected void montarBotoes(JPanel painel) {
        adicionarBotao(painel, "Nova Peça", COR_SUCESSO, () -> Navegador.ir(new PecaCadastroPanel()));
        adicionarBotao(painel, "Editar", COR_INFO, this::editar);
        adicionarBotao(painel, "Movimentar", COR_ALERTA, this::movimentar);
        adicionarBotao(painel, "Excluir", COR_PERIGO, this::excluir);
    }

    @Override
    protected void aoDuploClique(Estoque registro) {
        Navegador.ir(PecaCadastroPanel.editar(registro.getPeca().getId()));
    }

    private void editar() {
        Estoque e = selecionado();
        if (e != null) {
            Navegador.ir(PecaCadastroPanel.editar(e.getPeca().getId()));
        }
    }

    private void movimentar() {
        Estoque e = selecionado();
        if (e == null) return;
        MovimentacaoForm form = new MovimentacaoForm(List.of(e), e, TipoMovimentacao.ENTRADA);
        if (!form.exibir(this)) return;
        try {
            Integer quantidade = form.quantidade();
            executarAsync(() -> CONTROLLER.movimentar(form.peca(), form.tipo(), quantidade, form.observacao()),
                    "Movimentação registrada com sucesso!", this::pesquisar);
        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        }
    }

    private void excluir() {
        Estoque e = selecionado();
        if (e != null && confirmar("Deseja excluir a peça " + e.getPeca() + "?\nO histórico de movimentações dela também será apagado.")) {
            executarAsync(() -> CONTROLLER.excluirPeca(e.getPeca()), "Peça excluída com sucesso!", this::pesquisar);
        }
    }
}
