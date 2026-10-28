package com.ofisystem.ui.panels.servico;

import com.ofisystem.controle.servico.ServicoController;
import com.ofisystem.entidade.Servico;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractConsultaPanel;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class ServicoConsultaPanel extends AbstractConsultaPanel<Servico> {

    private static final ServicoController CONTROLLER = new ServicoController();

    private JTextField txtNome;
    private JComboBox<String> cbSituacao;

    @Override
    protected String titulo() {
        return "Consultar Serviços";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Nome", "Descrição", "Valor", "Duração (min)", "Situação"};
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        txtNome = criarCampo(100);
        cbSituacao = new JComboBox<>(new String[]{"Ativos", "Inativos", "Todos"});
        adicionarCampo(painel, gbc, 0, 0, "Nome:", txtNome);
        adicionarCampo(painel, gbc, 0, 1, "Situação:", cbSituacao);
        return 1;
    }

    @Override
    protected Supplier<List<Servico>> consulta() {
        String nome = txtNome.getText();
        Boolean ativo = switch (cbSituacao.getSelectedIndex()) {
            case 0 -> true;
            case 1 -> false;
            default -> null;
        };
        return () -> CONTROLLER.filtrar(nome, ativo);
    }

    @Override
    protected Object[] linha(Servico s) {
        return new Object[]{
                s.getNome(),
                s.getDescricao(),
                Formatador.moeda(s.getValor()),
                Formatador.texto(s.getDuracaoMin()),
                s.isAtivo() ? "Ativo" : "Inativo"
        };
    }

    @Override
    protected void aposMontarTela() {
        larguraColuna(1, 320);
    }

    @Override
    protected void montarBotoes(JPanel painel) {
        adicionarBotao(painel, "Novo Serviço", COR_SUCESSO, () -> Navegador.ir(new ServicoCadastroPanel()));
        adicionarBotao(painel, "Editar", COR_INFO, this::editar);
        adicionarBotao(painel, "Excluir", COR_PERIGO, this::excluir);
    }

    @Override
    protected void aoDuploClique(Servico registro) {
        Navegador.ir(ServicoCadastroPanel.editar(registro.getId()));
    }

    @Override
    protected String colunaEtiqueta() {
        return "Situação";
    }

    @Override
    protected Tema.Tom corLinha(Servico registro) {
        return registro.isAtivo() ? Tema.Tom.SUCESSO : Tema.Tom.INATIVO;
    }

    private void editar() {
        Servico s = selecionado();
        if (s != null) {
            Navegador.ir(ServicoCadastroPanel.editar(s.getId()));
        }
    }

    private void excluir() {
        Servico s = selecionado();
        if (s != null && confirmar("Deseja excluir o serviço " + s.getNome() + "?")) {
            executarAsync(() -> CONTROLLER.excluir(s), "Serviço excluído com sucesso!", this::pesquisar);
        }
    }
}
