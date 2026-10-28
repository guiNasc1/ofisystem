package com.ofisystem.ui.panels.servico;

import com.ofisystem.controle.servico.ServicoController;
import com.ofisystem.entidade.Servico;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;

public class ServicoCadastroPanel extends AbstractPanel {

    private static final ServicoController CONTROLLER = new ServicoController();

    private JLabel lblTitulo;
    private JTextField txtNome;
    private JTextField txtValor;
    private JTextField txtDuracao;
    private JCheckBox chkAtivo;
    private JTextArea txtDescricao;

    private Servico servico;

    public static ServicoCadastroPanel editar(Integer id) {
        ServicoCadastroPanel painel = new ServicoCadastroPanel();
        painel.executarAsync(() -> CONTROLLER.buscarPorId(id), painel::preencher);
        return painel;
    }

    @Override
    protected void inicializarTela() {
        lblTitulo = criarTexto("Cadastrar Serviço");
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painel = criarPainelFormulario();
        GridBagConstraints gbc = criarGbc();

        txtNome = criarCampo(255);
        txtValor = criarCampo(15);
        txtDuracao = criarCampo(6);
        chkAtivo = new JCheckBox("Ativo", true);
        chkAtivo.setOpaque(false);
        txtDescricao = criarAreaTexto(4);
        txtValor.putClientProperty("JTextField.placeholderText", "0,00");
        txtDuracao.putClientProperty("JTextField.placeholderText", "minutos");

        adicionarCampo(painel, gbc, 0, 0, "Nome:", txtNome, 2);
        adicionarCampo(painel, gbc, 0, 2, "Situação:", chkAtivo);
        adicionarCampo(painel, gbc, 1, 0, "Valor (R$):", txtValor);
        adicionarCampo(painel, gbc, 1, 1, "Duração (min):", txtDuracao);
        adicionarCampo(painel, gbc, 2, 0, "Descrição:", comRolagem(txtDescricao), 3);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setOpaque(false);
        conteudo.add(painel, BorderLayout.NORTH);
        add(conteudo, BorderLayout.CENTER);

        JPanel rodape = criarRodape();
        adicionarBotao(rodape, "Voltar", COR_NEUTRA, () -> Navegador.ir(new ServicoConsultaPanel()));
        adicionarBotao(rodape, "Novo", COR_INFO, this::limpar);
        adicionarBotao(rodape, "Salvar", COR_SUCESSO, this::salvar);
        add(rodape, BorderLayout.SOUTH);
    }

    private void preencher(Servico s) {
        if (s == null) {
            mostrarErro("Serviço não encontrado.");
            return;
        }
        servico = s;
        lblTitulo.setText("Editar Serviço — " + s.getNome());
        txtNome.setText(s.getNome());
        txtValor.setText(Formatador.decimal(s.getValor()));
        txtDuracao.setText(Formatador.texto(s.getDuracaoMin()));
        chkAtivo.setSelected(s.isAtivo());
        txtDescricao.setText(s.getDescricao());
    }

    private void salvar() {
        Servico s = servico != null ? servico : new Servico();
        try {
            s.setValor(Formatador.parseValor(txtValor.getText()));
            s.setDuracaoMin(Formatador.parseInteiro(txtDuracao.getText()));
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
            return;
        }
        s.setNome(txtNome.getText().trim());
        s.setDescricao(txtDescricao.getText().trim());
        s.setAtivo(chkAtivo.isSelected());

        boolean edicao = servico != null;
        executarAsync(() -> CONTROLLER.salvar(s), salvo -> {
            mostrarSucesso(edicao ? "Serviço atualizado com sucesso!" : "Serviço cadastrado com sucesso!");
            if (edicao) {
                Navegador.ir(new ServicoConsultaPanel());
            } else {
                limpar();
            }
        });
    }

    private void limpar() {
        servico = null;
        lblTitulo.setText("Cadastrar Serviço");
        txtNome.setText("");
        txtValor.setText("");
        txtDuracao.setText("");
        txtDescricao.setText("");
        chkAtivo.setSelected(true);
        txtNome.requestFocus();
    }
}
