package com.ofisystem.ui.panels;

import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractConsultaPanel<T> extends AbstractPanel {

    protected JTable tabela;
    protected DefaultTableModel modelo;
    protected JLabel lblResumo;
    private JPanel painelFiltros;
    private List<T> registros;
    private boolean ignorarEventosFiltro;
    private int pesquisaAtual;

    @Override
    protected void inicializarTela() {
        registros = new ArrayList<>();
        add(criarTexto(titulo()), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);

        painelFiltros = criarPainelFormulario();
        GridBagConstraints gbc = criarGbc();
        int proximaLinha = montarFiltros(painelFiltros, gbc);
        adicionarBotoesFiltro(gbc, proximaLinha);
        registrarEventosFiltro(painelFiltros);
        centro.add(painelFiltros, BorderLayout.NORTH);

        modelo = criarModelo(colunas());
        tabela = criarTabela(modelo);
        tabela.setDefaultRenderer(Object.class, new RenderizadorLinha());
        tabela.putClientProperty(TEXTO_VAZIO, "Carregando...");
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() >= 0) {
                    aoDuploClique(registros.get(tabela.convertRowIndexToModel(tabela.getSelectedRow())));
                }
            }
        });
        centro.add(comRolagem(tabela), BorderLayout.CENTER);
        lblResumo = Texto.apoio(" ");
        lblResumo.setBorder(BorderFactory.createEmptyBorder(0, MARGEM_LATERAL + 4, 12, MARGEM_LATERAL));
        centro.add(lblResumo, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        JPanel rodape = criarRodape();
        montarBotoes(rodape);
        add(rodape, BorderLayout.SOUTH);

        aposMontarTela();
        pesquisar();
    }

    protected abstract String titulo();

    protected abstract String[] colunas();

    protected abstract int montarFiltros(JPanel painel, GridBagConstraints gbc);

    protected abstract Supplier<List<T>> consulta();

    protected abstract Object[] linha(T registro);

    protected abstract void montarBotoes(JPanel painel);

    protected void aposMontarTela() {
    }

    protected void aoDuploClique(T registro) {
    }

    protected Tema.Tom corLinha(T registro) {
        return Tema.Tom.NEUTRO;
    }

    protected String colunaEtiqueta() {
        return "Status";
    }

    protected String resumo(List<T> lista) {
        return lista.size() + " registro(s) encontrado(s)";
    }

    protected void limparFiltros() {
        limparCampos(painelFiltros);
    }

    protected void pesquisar() {
        Supplier<List<T>> tarefa;
        try {
            tarefa = consulta();
        } catch (RuntimeException e) {
            tratarErro(e);
            return;
        }
        int numero = ++pesquisaAtual;
        lblResumo.setText("Carregando...");
        if (modelo.getRowCount() == 0) {
            tabela.putClientProperty(TEXTO_VAZIO, "Carregando...");
            tabela.repaint();
        }
        executarAsync(tarefa, lista -> {
            if (numero != pesquisaAtual) return;
            registros = lista;
            modelo.setRowCount(0);
            for (T registro : lista) {
                modelo.addRow(linha(registro));
            }
            tabela.putClientProperty(TEXTO_VAZIO, "Nada encontrado com estes filtros. Ajuste a busca ou clique em Limpar.");
            tabela.repaint();
            lblResumo.setText(resumo(lista));
        });
    }

    protected T selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            mostrarAviso("Selecione um registro na tabela.");
            return null;
        }
        return registros.get(tabela.convertRowIndexToModel(linha));
    }

    protected List<T> getRegistros() {
        return registros;
    }

    protected void larguraColuna(int coluna, int largura) {
        tabela.getColumnModel().getColumn(coluna).setPreferredWidth(largura);
    }

    private void adicionarBotoesFiltro(GridBagConstraints gbc, int linha) {
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        adicionarBotao(botoes, "Limpar", COR_NEUTRA, () -> {
            ignorarEventosFiltro = true;
            try {
                limparFiltros();
            } finally {
                ignorarEventosFiltro = false;
            }
            pesquisar();
        });
        adicionarBotao(botoes, "Pesquisar", COR_PRIMARIA, this::pesquisar);

        gbc.gridy = linha;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        painelFiltros.add(botoes, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
    }

    private void registrarEventosFiltro(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTextField campo) {
                campo.addActionListener(e -> pesquisar());
            } else if (c instanceof JComboBox<?> combo) {
                combo.addActionListener(e -> {
                    if (!ignorarEventosFiltro) pesquisar();
                });
            } else if (c instanceof JCheckBox check) {
                check.addActionListener(e -> {
                    if (!ignorarEventosFiltro) pesquisar();
                });
            } else if (c instanceof JPanel painel) {
                registrarEventosFiltro(painel);
            }
        }
    }

    private void limparCampos(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JFormattedTextField campo) {
                campo.setValue(null);
            } else if (c instanceof JTextField campo) {
                campo.setText("");
            } else if (c instanceof JComboBox<?> combo && combo.getItemCount() > 0) {
                combo.setSelectedIndex(0);
            } else if (c instanceof JCheckBox check) {
                check.setSelected(false);
            } else if (c instanceof JPanel painel) {
                limparCampos(painel);
            }
        }
    }

    private class RenderizadorLinha extends CelulaPadrao {

        private Tema.Tom tomEtiqueta;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int linhaModelo = table.convertRowIndexToModel(row);
            Tema.Tom tom = linhaModelo < registros.size() ? corLinha(registros.get(linhaModelo)) : Tema.Tom.NEUTRO;
            boolean coluna = colunaEtiqueta().equalsIgnoreCase(table.getColumnName(column));
            tomEtiqueta = coluna && value != null && !value.toString().isBlank() ? tom : null;
            if (!isSelected) {
                setBackground(Tema.vinil());
                setForeground(tom == Tema.Tom.INATIVO ? Tema.tintaSuave() : Tema.tinta());
            }
            if (tomEtiqueta != null) {
                setForeground(tomEtiqueta == Tema.Tom.NEUTRO ? Tema.texto(Tema.Tom.INFO) : Tema.texto(tomEtiqueta));
                setFont(Tema.fonte(Tema.TAM_PEQUENO, Font.BOLD));
                setText(value.toString().toUpperCase());
            } else {
                setFont(table.getFont());
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (tomEtiqueta != null) {
                Graphics2D g2 = Tema.suave(g);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                FontMetrics fm = getFontMetrics(getFont());
                int largura = Math.min(getWidth() - 16, fm.stringWidth(getText()) + 16);
                int altura = 22;
                g2.setColor(Tema.fundo(tomEtiqueta == Tema.Tom.NEUTRO ? Tema.Tom.INFO : tomEtiqueta));
                g2.fillRoundRect(8, (getHeight() - altura) / 2, largura, altura, 6, 6);
                g2.setColor(getForeground());
                g2.setFont(getFont());
                g2.drawString(getText(), 16, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
                return;
            }
            super.paintComponent(g);
        }
    }
}
