package com.ofisystem.ui.panels.estoque;

import Enums.Status;
import com.ofisystem.controle.estoque.EstoqueController;
import com.ofisystem.entidade.Estoque;
import com.ofisystem.entidade.Peca;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PecaCadastroPanel extends AbstractPanel {

    private static final EstoqueController CONTROLLER = new EstoqueController();

    private JLabel lblTitulo;
    private JTextField txtNome;
    private JComboBox<String> cbCategoria;
    private JTextField txtValor;
    private JComboBox<Status> cbStatus;
    private JTextField txtMinimo;
    private JTextField txtQuantidade;
    private JLabel lblQuantidade;

    private Peca peca;

    public static PecaCadastroPanel editar(Integer pecaId) {
        PecaCadastroPanel painel = new PecaCadastroPanel();
        painel.executarAsync(() -> CONTROLLER.buscarPorPeca(pecaId), painel::preencher);
        return painel;
    }

    @Override
    protected void inicializarTela() {
        lblTitulo = criarTexto("Cadastrar Peça");
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painel = criarPainelFormulario();
        GridBagConstraints gbc = criarGbc();

        txtNome = criarCampo(255);
        cbCategoria = new JComboBox<>();
        cbCategoria.setEditable(true);
        txtValor = criarCampo(15);
        cbStatus = new JComboBox<>(new Status[]{Status.ATIVO, Status.INATIVO});
        cbStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, value == null ? "" : ((Status) value).getDescricao(),
                        index, isSelected, cellHasFocus);
            }
        });
        txtMinimo = criarCampo(6);
        txtQuantidade = criarCampo(6);
        txtValor.putClientProperty("JTextField.placeholderText", "0,00");
        txtMinimo.setText("1");
        txtQuantidade.setText("0");

        adicionarCampo(painel, gbc, 0, 0, "Nome:", txtNome, 3);
        adicionarCampo(painel, gbc, 1, 0, "Categoria:", cbCategoria);
        adicionarCampo(painel, gbc, 1, 1, "Valor unit. (R$):", txtValor);
        adicionarCampo(painel, gbc, 1, 2, "Status:", cbStatus);
        adicionarCampo(painel, gbc, 2, 0, "Estoque mínimo:", txtMinimo);
        adicionarCampo(painel, gbc, 2, 1, "Qtd. inicial:", txtQuantidade);
        lblQuantidade = (JLabel) painel.getComponent(painel.getComponentCount() - 2);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setOpaque(false);
        conteudo.add(painel, BorderLayout.NORTH);
        add(conteudo, BorderLayout.CENTER);

        JPanel rodape = criarRodape();
        adicionarBotao(rodape, "Voltar", COR_NEUTRA, () -> Navegador.ir(new EstoqueConsultaPanel()));
        adicionarBotao(rodape, "Novo", COR_INFO, this::limpar);
        adicionarBotao(rodape, "Salvar", COR_SUCESSO, this::salvar);
        add(rodape, BorderLayout.SOUTH);

        executarAsync(CONTROLLER::listarCategorias, this::carregarCategorias);
    }

    private void carregarCategorias(List<String> categorias) {
        Object atual = cbCategoria.getSelectedItem();
        cbCategoria.setModel(new DefaultComboBoxModel<>(categorias.toArray(new String[0])));
        cbCategoria.setSelectedItem(atual);
    }

    private void preencher(Estoque estoque) {
        if (estoque == null) {
            mostrarErro("Peça não encontrada.");
            return;
        }
        peca = estoque.getPeca();
        lblTitulo.setText("Editar Peça — ID " + peca.getId());
        txtNome.setText(peca.getNome());
        cbCategoria.setSelectedItem(peca.getCategoria());
        txtValor.setText(Formatador.decimal(peca.getValorUnitario()));
        cbStatus.setSelectedItem(peca.getStatus());
        txtMinimo.setText(Formatador.texto(estoque.getQuantidadeMinima()));
        lblQuantidade.setText("Qtd. em estoque:");
        txtQuantidade.setText(Formatador.texto(estoque.getQuantidade()));
        txtQuantidade.setEditable(false);
        txtQuantidade.setToolTipText("Para alterar a quantidade use Estoque > Movimentação.");
    }

    private void salvar() {
        Peca p = peca != null ? peca : new Peca();
        Integer minimo;
        Integer inicial;
        try {
            p.setValorUnitario(Formatador.parseValor(txtValor.getText()));
            minimo = Formatador.parseInteiro(txtMinimo.getText());
            inicial = peca == null ? Formatador.parseInteiro(txtQuantidade.getText()) : null;
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
            return;
        }
        p.setNome(txtNome.getText().trim());
        Object categoria = cbCategoria.getSelectedItem();
        p.setCategoria(categoria == null || categoria.toString().isBlank() ? null : categoria.toString().trim());
        p.setStatus((Status) cbStatus.getSelectedItem());

        boolean edicao = peca != null;
        executarAsync(() -> CONTROLLER.salvarPeca(p, minimo, inicial), salvo -> {
            mostrarSucesso(edicao ? "Peça atualizada com sucesso!"
                    : "Peça cadastrada com sucesso! ID: " + salvo.getPeca().getId());
            if (edicao) {
                Navegador.ir(new EstoqueConsultaPanel());
            } else {
                limpar();
                executarAsync(CONTROLLER::listarCategorias, this::carregarCategorias);
            }
        });
    }

    private void limpar() {
        peca = null;
        lblTitulo.setText("Cadastrar Peça");
        txtNome.setText("");
        cbCategoria.setSelectedItem(null);
        txtValor.setText("");
        cbStatus.setSelectedItem(Status.ATIVO);
        txtMinimo.setText("1");
        lblQuantidade.setText("Qtd. inicial:");
        txtQuantidade.setText("0");
        txtQuantidade.setEditable(true);
        txtQuantidade.setToolTipText(null);
        txtNome.requestFocus();
    }
}
