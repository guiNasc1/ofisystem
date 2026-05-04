package com.ofisystem.ui.panels;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPanel extends JPanel {

    public AbstractPanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        inicializarTela();
    }

    protected abstract void inicializarTela();

    protected JLabel criarTexto(String texto, int tamanho){
        JLabel txt = new JLabel(texto);
        txt.setHorizontalAlignment(JLabel.CENTER);
        txt.setFont(new Font("Segoe UI", Font.BOLD, tamanho));
        txt.setVerticalAlignment(JLabel.CENTER);
        return txt;
    }

    protected JButton criarBotao(String texto, Color cor){
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(new Color(255, 255, 255));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));

        return btn;
    }

    protected JTextField criarCampo(int colunas) {
        JTextField campo = new JTextField(colunas);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 32));
        return campo;
    }

    protected JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    protected void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this,
                mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this,
                mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    protected boolean confirmar(String mensagem) {
        return JOptionPane.showConfirmDialog(this,
                mensagem, "Confirmação",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

}
