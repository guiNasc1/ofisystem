package com.ofisystem.ui.panels.relatorio;

import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.ui.style.Texto;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RelatorioJasper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractRelatorioPanel extends AbstractPanel {

    private JTable tabela;
    private JLabel lblResumo;
    private JButton btImprimir;
    private Resultado ultimo;

    protected JFormattedTextField txtDe;
    protected JFormattedTextField txtAte;

    @Override
    protected void inicializarTela() {
        add(criarTexto(titulo()), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);

        JPanel filtros = criarPainelFormulario();
        GridBagConstraints gbc = criarGbc();
        txtDe = criarCampoData();
        txtAte = criarCampoData();
        definirData(txtDe, LocalDate.now().withDayOfMonth(1));
        definirData(txtAte, LocalDate.now());
        int linha = montarFiltros(filtros, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        adicionarBotao(botoes, "Gerar", COR_PRIMARIA, this::gerar);
        btImprimir = adicionarBotao(botoes, "Imprimir / PDF", COR_SUCESSO, this::imprimir);
        btImprimir.setEnabled(false);
        gbc.gridy = linha;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        filtros.add(botoes, gbc);
        centro.add(filtros, BorderLayout.NORTH);

        tabela = criarTabela(criarModelo());
        tabela.putClientProperty(TEXTO_VAZIO, "Defina os filtros e clique em Gerar para ver a prévia.");
        centro.add(comRolagem(tabela), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        lblResumo = Texto.apoio("Nenhum relatório gerado ainda.");
        JPanel rodape = criarRodape();
        ((FlowLayout) rodape.getLayout()).setAlignment(FlowLayout.LEFT);
        rodape.add(lblResumo);
        add(rodape, BorderLayout.SOUTH);
    }

    protected abstract String titulo();

    protected abstract int montarFiltros(JPanel painel, GridBagConstraints gbc);

    protected abstract Supplier<Resultado> consulta();

    protected void adicionarPeriodo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo) {
        adicionarCampo(painel, gbc, linha, 0, rotulo + " de:", txtDe);
        adicionarCampo(painel, gbc, linha, 1, "até:", txtAte);
    }

    protected LocalDate dataDe() {
        return Formatador.parseData(txtDe.getText());
    }

    protected LocalDate dataAte() {
        return Formatador.parseData(txtAte.getText());
    }

    protected LocalDate[] periodoObrigatorio() {
        LocalDate de = dataDe();
        LocalDate ate = dataAte();
        if (de == null || ate == null) {
            throw new IllegalArgumentException("Informe o período completo.");
        }
        if (de.isAfter(ate)) {
            throw new IllegalArgumentException("A data inicial é maior que a final.");
        }
        return new LocalDate[]{de, ate};
    }

    protected String descricaoPeriodo(String rotulo) {
        LocalDate de = dataDe();
        LocalDate ate = dataAte();
        if (de == null && ate == null) return rotulo + ": todo o período";
        return rotulo + ": " + (de == null ? "início" : Formatador.data(de)) + " a " + (ate == null ? "hoje" : Formatador.data(ate));
    }

    private void gerar() {
        Supplier<Resultado> tarefa;
        try {
            tarefa = consulta();
        } catch (RuntimeException e) {
            tratarErro(e);
            return;
        }
        lblResumo.setText("Gerando...");
        executarAsync(tarefa, resultado -> {
            ultimo = resultado;
            DefaultTableModel modelo = criarModelo(resultado.colunas);
            for (Object[] linha : resultado.linhas) {
                modelo.addRow(linha);
            }
            tabela.setModel(modelo);
            tabela.putClientProperty(TEXTO_VAZIO, "Nenhum registro no período e filtros escolhidos.");
            lblResumo.setText(resultado.linhas.size() + " registro(s)   ·   " + String.join("   ·   ", resultado.resumo));
            btImprimir.setEnabled(true);
        });
    }

    private void imprimir() {
        if (ultimo == null) return;
        Resultado resultado = ultimo;
        executarAsync(() -> {
            try {
                return RelatorioJasper.gerar(titulo(), resultado.filtros, resultado.colunas, resultado.linhas, resultado.resumo);
            } catch (Exception e) {
                throw new RuntimeException("Falha ao gerar o relatório: " + e.getMessage(), e);
            }
        }, print -> RelatorioJasper.exibir(print, titulo()));
    }

    public static class Resultado {
        final String[] colunas;
        final List<Object[]> linhas;
        final List<String> filtros = new ArrayList<>();
        final List<String> resumo = new ArrayList<>();

        public Resultado(String[] colunas, List<Object[]> linhas) {
            this.colunas = colunas;
            this.linhas = linhas;
        }

        public Resultado filtro(String texto) {
            filtros.add(texto);
            return this;
        }

        public Resultado resumo(String texto) {
            resumo.add(texto);
            return this;
        }
    }
}
