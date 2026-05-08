package com.ofisystem.ui.panels;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.text.ParseException;

public abstract class AbstractPanel extends JPanel {

    public AbstractPanel() throws ParseException {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        inicializarTela();
    }

    protected abstract void inicializarTela() throws ParseException;

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

    protected JTextField criarCampo(int largura, int maxCaracteres) {
        JTextField campo = new JTextField(largura);
        campo.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str != null && (getLength() + str.length()) <= maxCaracteres)
                    super.insertString(offs, str, a);
            }
        });
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        FontMetrics fm = campo.getFontMetrics(campo.getFont());
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 32));
        return campo;
    }

    protected JFormattedTextField criarCampoFormatado(MaskFormatter mask, int largura, int maxCaracteres){
        try {
            JFormattedTextField campo = new JFormattedTextField(mask);
            campo.setDocument(new PlainDocument() {
                @Override
                public void insertString(int offs, String str, AttributeSet a)
                        throws BadLocationException {
                    if (str != null && (getLength() + str.length()) <= maxCaracteres)
                        super.insertString(offs, str, a);
                }
            });
            campo.setColumns(largura);
            campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            campo.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
            FontMetrics fm = campo.getFontMetrics(campo.getFont());
            campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 32));

            return campo;
        }catch (Exception e) {
            return new JFormattedTextField();
        }
    }

    protected JLabel criarLabel(String texto, int tamanho) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, tamanho));
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
