package com.ofisystem.ui.panels;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPanel extends JPanel {

    private JFrame frame;
    private JPanel painelCentral;

    public AbstractPanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        inicializarTela();
    }

    protected abstract void inicializarTela();

    protected JFrame getFrame(String titulo, int largura, int altura, int larguraMinimaJanela, int alturaMinimaJanela){
        frame = new JFrame();
        frame.setTitle(titulo);
        frame.setSize(largura,altura);
        frame.setMinimumSize(new Dimension(larguraMinimaJanela, alturaMinimaJanela));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // centraliza na tela

        // Painel de conteúdo central (começa vazio)
        painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(new Color(245, 245, 245));
        frame.add(painelCentral);
        return frame;
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
