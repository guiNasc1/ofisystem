package com.ofisystem.ui.panels.estoque;

import Enums.TipoMovimentacao;
import com.ofisystem.entidade.Estoque;
import com.ofisystem.entidade.Peca;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class MovimentacaoForm {

    private final JComboBox<Estoque> cbPeca;
    private final JComboBox<TipoMovimentacao> cbTipo;
    private final JTextField txtQuantidade;
    private final JTextField txtObservacao;
    private final JPanel painel;

    MovimentacaoForm(List<Estoque> estoques, Estoque selecionado, TipoMovimentacao tipo) {
        cbPeca = new JComboBox<>(estoques.toArray(new Estoque[0]));
        if (selecionado != null) {
            cbPeca.setSelectedItem(selecionado);
            cbPeca.setEnabled(false);
        }
        cbTipo = new JComboBox<>(TipoMovimentacao.values());
        cbTipo.setSelectedItem(tipo);
        txtQuantidade = new JTextField(8);
        txtObservacao = new JTextField(30);
        txtObservacao.putClientProperty("JTextField.placeholderText", "Ex.: compra NF 123, ajuste de inventário, perda");

        painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        adicionar(0, "Peça:", cbPeca, gbc);
        adicionar(1, "Tipo:", cbTipo, gbc);
        adicionar(2, "Quantidade:", txtQuantidade, gbc);
        adicionar(3, "Motivo:", txtObservacao, gbc);
    }

    private void adicionar(int linha, String rotulo, JComponent campo, GridBagConstraints gbc) {
        gbc.gridy = linha;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campo, gbc);
    }

    boolean exibir(Component pai) {
        return JOptionPane.showConfirmDialog(pai, painel, "Movimentar estoque",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    Peca peca() {
        Estoque e = (Estoque) cbPeca.getSelectedItem();
        return e == null ? null : e.getPeca();
    }

    TipoMovimentacao tipo() {
        return (TipoMovimentacao) cbTipo.getSelectedItem();
    }

    Integer quantidade() {
        return Formatador.parseInteiro(txtQuantidade.getText());
    }

    String observacao() {
        return txtObservacao.getText();
    }
}
