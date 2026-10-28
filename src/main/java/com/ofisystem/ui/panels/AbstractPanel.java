package com.ofisystem.ui.panels;

import com.ofisystem.ui.style.Botao;
import com.ofisystem.ui.style.Superficie;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RegraNegocioException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractPanel extends JPanel {

    protected static final Botao.Estilo COR_SUCESSO = Botao.Estilo.PRINCIPAL;
    protected static final Botao.Estilo COR_PRIMARIA = Botao.Estilo.SECUNDARIO;
    protected static final Botao.Estilo COR_INFO = Botao.Estilo.CONTORNO;
    protected static final Botao.Estilo COR_NEUTRA = Botao.Estilo.DISCRETO;
    protected static final Botao.Estilo COR_ALERTA = Botao.Estilo.SECUNDARIO;
    protected static final Botao.Estilo COR_PERIGO = Botao.Estilo.PERIGO;

    protected static final int MARGEM_LATERAL = 24;
    protected static final int MARGEM_TOPO_TITULO = 24;
    protected static final int MARGEM_BASE_TITULO = 16;
    protected static final int ALTURA_CAMPO = 40;
    protected static final String TEXTO_VAZIO = "ofisystem.textoVazio";

    public AbstractPanel(){
        setLayout(new BorderLayout());
        inicializarTela();
    }

    protected abstract void inicializarTela();

    protected JLabel criarTexto(String texto){
        Texto txt = Texto.titulo(texto);
        txt.setHorizontalAlignment(JLabel.LEFT);
        txt.setBorder(BorderFactory.createEmptyBorder(MARGEM_TOPO_TITULO, MARGEM_LATERAL, MARGEM_BASE_TITULO, MARGEM_LATERAL));
        return txt;
    }

    protected JPanel criarCabecalho(JLabel titulo, JComponent lateral) {
        JPanel cabecalho = new JPanel(new BorderLayout(16, 0));
        cabecalho.setOpaque(false);
        cabecalho.setBorder(titulo.getBorder());
        titulo.setBorder(null);
        cabecalho.add(titulo, BorderLayout.WEST);
        if (lateral != null) cabecalho.add(lateral, BorderLayout.EAST);
        return cabecalho;
    }

    protected JButton criarBotao(String texto, Botao.Estilo estilo){
        return new Botao(texto, estilo);
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
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, ALTURA_CAMPO));
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
            campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, ALTURA_CAMPO));
            return campo;
        }catch (Exception e) {
            return new JFormattedTextField();
        }
    }

    protected JLabel criarLabel(String texto, int tamanho) {
        int degrau = tamanho <= 13 ? Tema.TAM_PEQUENO : tamanho <= 16 ? Tema.TAM_TEXTO : Tema.TAM_TITULO;
        return new Texto(texto, degrau, degrau == Tema.TAM_TITULO ? Font.BOLD : Font.PLAIN, Tema::tinta);
    }

    protected void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this,
                mensagem, "Pronto", JOptionPane.INFORMATION_MESSAGE);
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

    protected void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(this,
                mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    protected JPanel criarPainelFormulario() {
        return new Superficie(new GridBagLayout(), 16, 24).comMargem(0, MARGEM_LATERAL, 8);
    }

    protected GridBagConstraints criarGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    protected void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, int coluna,
                                  String rotulo, JComponent campo) {
        adicionarCampo(painel, gbc, linha, coluna, rotulo, campo, 1);
    }

    protected void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, int coluna,
                                  String rotulo, JComponent campo, int colunasOcupadas) {
        gbc.gridy = linha;
        gbc.gridx = coluna * 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        String limpo = rotulo == null ? "" : rotulo.trim();
        if (limpo.endsWith(":")) limpo = limpo.substring(0, limpo.length() - 1);
        painel.add(Texto.rotulo(limpo), gbc);

        if (campo instanceof JComboBox<?>) {
            campo.setPreferredSize(new Dimension(Math.max(campo.getPreferredSize().width, 160), ALTURA_CAMPO));
        }

        gbc.gridx = coluna * 2 + 1;
        gbc.gridwidth = colunasOcupadas * 2 - 1;
        gbc.weightx = 1;
        painel.add(campo, gbc);
        gbc.gridwidth = 1;
    }

    protected void adicionarCampoCompacto(JPanel painel, GridBagConstraints gbc, int linha, int coluna,
                                          String rotulo, JComponent campo) {
        adicionarCampo(painel, gbc, linha, coluna, rotulo, campo, 1);
        GridBagLayout grade = (GridBagLayout) painel.getLayout();
        GridBagConstraints restricao = grade.getConstraints(campo);
        restricao.weightx = 0;
        grade.setConstraints(campo, restricao);
    }

    protected void comAjuda(JComboBox<?> combo, String ajuda) {
        ListCellRenderer<? super Object> original = new DefaultListCellRenderer();
        combo.setRenderer((lista, valor, indice, selecionado, foco) -> {
            Component c = original.getListCellRendererComponent(lista, valor == null && indice < 0 ? ajuda : valor,
                    indice, selecionado, foco);
            if (valor == null && indice < 0) c.setForeground(Tema.tintaSuave());
            return c;
        });
    }

    protected JTextField criarCampo(int maxCaracteres) {
        return criarCampo(6, maxCaracteres);
    }

    protected JScrollPane comRolagemVertical(JComponent conteudo) {
        JPanel ajustado = new PainelRolavel();
        ajustado.add(conteudo, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(ajustado,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        return rolagem;
    }

    private static class PainelRolavel extends JPanel implements Scrollable {

        PainelRolavel() {
            super(new BorderLayout());
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    protected JFormattedTextField criarCampoData() {
        try {
            MaskFormatter mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            JFormattedTextField campo = new JFormattedTextField(mascara);
            campo.setFocusLostBehavior(JFormattedTextField.PERSIST);
            campo.setColumns(9);
            campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, ALTURA_CAMPO));
            return campo;
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }

    protected void definirData(JFormattedTextField campo, LocalDate data) {
        campo.setValue(data == null ? null : Formatador.data(data));
    }

    protected JTextArea criarAreaTexto(int linhas) {
        JTextArea area = new JTextArea(linhas, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return area;
    }

    protected JScrollPane comRolagem(JTextArea area) {
        return new JScrollPane(area);
    }

    protected DefaultTableModel criarModelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    protected JTable criarTabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Object vazio = getClientProperty(TEXTO_VAZIO);
                if (getRowCount() == 0 && vazio != null) {
                    Graphics2D g2 = Tema.suave(g);
                    g2.setFont(Tema.fonte(Tema.TAM_TEXTO));
                    g2.setColor(Tema.tintaSuave());
                    FontMetrics fm = g2.getFontMetrics();
                    String texto = vazio.toString();
                    int altura = getParent() != null ? getParent().getHeight() : getHeight();
                    g2.drawString(texto, Math.max(16, (getWidth() - fm.stringWidth(texto)) / 2), Math.max(48, altura / 3));
                    g2.dispose();
                }
            }
        };
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.setDefaultRenderer(Object.class, new CelulaPadrao());
        tabela.getTableHeader().setDefaultRenderer(new CabecalhoTabela(tabela.getTableHeader().getDefaultRenderer()));
        return tabela;
    }

    protected static boolean pareceNumero(Object valor) {
        if (valor instanceof Number) return true;
        if (valor == null) return false;
        String v = valor.toString().trim();
        return !v.isEmpty() && v.matches("^-?(R\\$[\\s\\u00A0]?)?-?[\\d.]+(,\\d+)?%?$") && !v.matches("^0\\d{3,}$");
    }

    protected static class CelulaPadrao extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setHorizontalAlignment(pareceNumero(value) ? RIGHT : LEFT);
            return this;
        }
    }

    private static class CabecalhoTabela implements TableCellRenderer {

        private final TableCellRenderer original;

        CabecalhoTabela(TableCellRenderer original) {
            this.original = original;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = original.getTableCellRendererComponent(table,
                    value == null ? "" : value.toString().toUpperCase(), isSelected, hasFocus, row, column);
            if (c instanceof JLabel l) {
                l.setFont(Tema.fonte(Tema.TAM_PEQUENO, Font.BOLD));
                l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                boolean numerica = false;
                if (table.getRowCount() > 0) {
                    numerica = pareceNumero(table.getValueAt(0, column));
                }
                l.setHorizontalAlignment(numerica ? SwingConstants.RIGHT : SwingConstants.LEFT);
            }
            return c;
        }
    }

    protected JComponent comRolagem(JTable tabela) {
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        Superficie cartao = new Superficie(new BorderLayout(), 0, 0);
        cartao.comMargem(8, MARGEM_LATERAL, 8);
        cartao.setBorder(BorderFactory.createCompoundBorder(cartao.getBorder(), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        cartao.add(scroll, BorderLayout.CENTER);
        return cartao;
    }

    protected JPanel criarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Tema.borda());
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        return rodape;
    }

    protected JButton adicionarBotao(JPanel painel, String texto, Botao.Estilo estilo, Runnable acao) {
        JButton botao = criarBotao(texto, estilo);
        botao.addActionListener(e -> acao.run());
        painel.add(botao);
        return botao;
    }

    protected <R> void executarAsync(Supplier<R> tarefa, Consumer<R> aoConcluir) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<R, Void>() {
            @Override
            protected R doInBackground() {
                return tarefa.get();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    R resultado = get();
                    if (aoConcluir != null) aoConcluir.accept(resultado);
                } catch (Exception e) {
                    tratarErro(e);
                }
            }
        }.execute();
    }

    protected void executarAsync(Runnable tarefa, String mensagemSucesso, Runnable aoConcluir) {
        executarAsync(() -> {
            tarefa.run();
            return Boolean.TRUE;
        }, ok -> {
            if (mensagemSucesso != null) mostrarSucesso(mensagemSucesso);
            if (aoConcluir != null) aoConcluir.run();
        });
    }

    protected void tratarErro(Throwable erro) {
        Throwable raiz = erro;
        for (Throwable atual = erro; atual != null; atual = atual.getCause()) {
            if (atual instanceof RegraNegocioException || atual instanceof IllegalArgumentException) {
                mostrarAviso(atual.getMessage());
                return;
            }
            raiz = atual;
        }

        erro.printStackTrace();
        String mensagem = raiz.getMessage() == null ? raiz.getClass().getSimpleName() : raiz.getMessage();
        if (mensagem.toLowerCase().contains("foreign key") || mensagem.toLowerCase().contains("violates")) {
            mostrarErro("Operação não permitida: o registro possui vínculos com outros cadastros.");
            return;
        }
        mostrarErro("Erro inesperado: " + mensagem);
    }

}
