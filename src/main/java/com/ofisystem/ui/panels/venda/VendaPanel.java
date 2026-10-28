package com.ofisystem.ui.panels.venda;

import Enums.FormaPagamento;
import com.ofisystem.controle.estoque.EstoqueController;
import com.ofisystem.controle.pessoa.PessoaController;
import com.ofisystem.controle.servico.ServicoController;
import com.ofisystem.controle.venda.VendaController;
import com.ofisystem.entidade.Estoque;
import com.ofisystem.entidade.ItemVenda;
import com.ofisystem.entidade.PagamentoVenda;
import com.ofisystem.entidade.Pessoa;
import com.ofisystem.entidade.Servico;
import com.ofisystem.entidade.Venda;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RelatorioJasper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class VendaPanel extends AbstractPanel {

    private static final VendaController CONTROLLER = new VendaController();
    private static final PessoaController PESSOA_CONTROLLER = new PessoaController();
    private static final ServicoController SERVICO_CONTROLLER = new ServicoController();
    private static final EstoqueController ESTOQUE_CONTROLLER = new EstoqueController();

    private JComboBox<Pessoa> cbCliente;
    private JTextField txtObservacao;

    private JComboBox<Estoque> cbProduto;
    private JTextField txtQtdProduto;
    private JTextField txtValorProduto;
    private JComboBox<Servico> cbServico;
    private JTextField txtQtdServico;
    private JTextField txtValorServico;
    private DefaultTableModel modeloItens;
    private JTable tabelaItens;

    private JLabel lblSubtotal;
    private JTextField txtDesconto;
    private JLabel lblTotal;
    private JComboBox<FormaPagamento> cbForma;
    private JTextField txtValorPagamento;
    private DefaultTableModel modeloPagamentos;
    private JTable tabelaPagamentos;
    private JLabel lblPago;
    private JLabel lblRestante;
    private JButton btFinalizar;

    private Venda venda;
    private boolean faltando;

    @Override
    protected void inicializarTela() {
        venda = new Venda();
        add(criarTexto("Venda de Balcão"), BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setOpaque(false);
        conteudo.add(criarDadosVenda());
        conteudo.add(criarItens());

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(comRolagemVertical(conteudo), BorderLayout.CENTER);
        centro.add(criarPagamento(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        JPanel rodape = criarRodape();
        adicionarBotao(rodape, "Consultar vendas", COR_NEUTRA, () -> Navegador.ir(new VendaConsultaPanel()));
        adicionarBotao(rodape, "Limpar", COR_INFO, this::novaVenda);
        btFinalizar = adicionarBotao(rodape, "Finalizar venda", COR_SUCESSO, this::finalizar);
        add(rodape, BorderLayout.SOUTH);

        carregarCadastros();
    }

    private JPanel criarDadosVenda() {
        JPanel painel = criarPainelFormulario();
        GridBagConstraints gbc = criarGbc();
        cbCliente = new JComboBox<>();
        cbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, value == null ? "Consumidor final" : value,
                        index, isSelected, cellHasFocus);
            }
        });
        txtObservacao = criarCampo(500);
        adicionarCampo(painel, gbc, 0, 0, "Cliente:", cbCliente);
        adicionarCampo(painel, gbc, 1, 0, "Observação:", txtObservacao);
        return painel;
    }

    private JPanel criarItens() {
        cbProduto = new JComboBox<>();
        txtQtdProduto = criarCampo(4, 4);
        txtValorProduto = criarCampo(7, 15);
        txtQtdProduto.setText("1");
        cbProduto.addActionListener(e -> {
            Estoque est = (Estoque) cbProduto.getSelectedItem();
            if (est != null) txtValorProduto.setText(Formatador.decimal(est.getPeca().getValorUnitario()));
        });

        cbServico = new JComboBox<>();
        txtQtdServico = criarCampo(4, 4);
        txtValorServico = criarCampo(7, 15);
        txtQtdServico.setText("1");
        cbServico.addActionListener(e -> {
            Servico s = (Servico) cbServico.getSelectedItem();
            if (s != null) txtValorServico.setText(Formatador.decimal(s.getValor()));
        });

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(Tema.fonte(Tema.TAM_TEXTO, Font.BOLD));
        abas.addTab("Produto", criarBarraItem(cbProduto, txtQtdProduto, txtValorProduto, this::adicionarProduto));
        abas.addTab("Serviço", criarBarraItem(cbServico, txtQtdServico, txtValorServico, this::adicionarServico));

        modeloItens = criarModelo("Tipo", "Descrição", "Qtd.", "Valor unit.", "Subtotal");
        tabelaItens = criarTabela(modeloItens);
        tabelaItens.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabelaItens.getColumnModel().getColumn(1).setPreferredWidth(240);
        JScrollPane scroll = new JScrollPane(tabelaItens);
        scroll.setPreferredSize(new Dimension(400, 232));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        acoes.setOpaque(false);
        adicionarBotao(acoes, "Remover item", COR_PERIGO, this::removerItem);

        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 24, 8, 12));
        painel.add(abas, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(acoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarBarraItem(JComboBox<?> combo, JTextField qtd, JTextField valor, Runnable adicionar) {
        JPanel barra = new JPanel(new GridBagLayout());
        barra.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        GridBagConstraints gbc = criarGbc();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        combo.setPreferredSize(new Dimension(160, ALTURA_CAMPO));
        barra.add(combo, gbc);
        gbc.weightx = 0;
        gbc.gridx++;
        barra.add(criarLabel("Qtd.:", 13), gbc);
        gbc.gridx++;
        barra.add(qtd, gbc);
        gbc.gridx++;
        barra.add(criarLabel("R$:", 13), gbc);
        gbc.gridx++;
        barra.add(valor, gbc);
        gbc.gridx++;
        barra.add(adicionarBotao(new JPanel(), "Adicionar", COR_SUCESSO, adicionar), gbc);
        qtd.addActionListener(e -> adicionar.run());
        valor.addActionListener(e -> adicionar.run());
        return barra;
    }

    private JPanel criarPagamento() {
        JPanel painel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Tema.vinil());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Tema.borda());
                g.fillRect(0, 0, 1, getHeight());
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(296, super.getPreferredSize().height);
            }
        };
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = criarGbc();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 2;

        lblSubtotal = criarLabel("Subtotal: R$ 0,00", 15);
        txtDesconto = criarCampo(8, 15);
        txtDesconto.setText("0,00");
        lblTotal = new Texto("TOTAL: R$ 0,00", Tema.TAM_TITULO, Font.BOLD, Tema::aco);
        cbForma = new JComboBox<>(FormaPagamento.values());
        txtValorPagamento = criarCampo(8, 15);
        for (JComponent controle : new JComponent[]{txtDesconto, cbForma, txtValorPagamento}) {
            controle.setPreferredSize(new Dimension(96, ALTURA_CAMPO));
            controle.setMinimumSize(new Dimension(80, ALTURA_CAMPO));
        }
        modeloPagamentos = criarModelo("Forma", "Valor");
        tabelaPagamentos = criarTabela(modeloPagamentos);
        JScrollPane scroll = new JScrollPane(tabelaPagamentos);
        scroll.setPreferredSize(new Dimension(200, 112));
        lblPago = criarLabel("Pago: R$ 0,00", 15);
        lblRestante = new Texto("Adicione itens à venda", Tema.TAM_TEXTO, Font.BOLD,
                () -> venda == null || venda.getItens().isEmpty() ? Tema.tintaSuave() : faltando ? Tema.critico() : Tema.sucesso());

        int y = 0;
        gbc.gridy = y++;
        painel.add(titulo("Pagamento"), gbc);
        gbc.gridy = y++;
        painel.add(lblSubtotal, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = y++;
        gbc.weightx = 0;
        painel.add(criarLabel("Desconto (R$):", 14), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(txtDesconto, gbc);
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        gbc.gridy = y++;
        painel.add(lblTotal, gbc);
        gbc.gridy = y++;
        painel.add(new JSeparator(), gbc);
        gbc.gridy = y++;
        painel.add(cbForma, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = y++;
        painel.add(txtValorPagamento, gbc);
        gbc.gridx = 1;
        painel.add(adicionarBotao(new JPanel(), "Incluir", COR_PRIMARIA, this::adicionarPagamento), gbc);
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        painel.add(scroll, gbc);
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = y++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(adicionarBotao(new JPanel(), "Remover", COR_PERIGO, this::removerPagamento), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = y++;
        painel.add(lblPago, gbc);
        gbc.gridy = y;
        painel.add(lblRestante, gbc);

        txtDesconto.addActionListener(e -> atualizarTotais());
        txtDesconto.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                atualizarTotais();
            }
        });
        txtValorPagamento.addActionListener(e -> adicionarPagamento());
        return painel;
    }

    private JLabel titulo(String texto) {
        return Texto.rotulo(texto);
    }

    private void carregarCadastros() {
        executarAsync(() -> new Cadastros(PESSOA_CONTROLLER.listarClientesAtivos(),
                ESTOQUE_CONTROLLER.listarAtivos(), SERVICO_CONTROLLER.listarAtivos()), cadastros -> {
            DefaultComboBoxModel<Pessoa> clientes = new DefaultComboBoxModel<>();
            clientes.addElement(null);
            cadastros.clientes.forEach(clientes::addElement);
            cbCliente.setModel(clientes);
            cbCliente.setSelectedItem(null);

            cbProduto.setModel(new DefaultComboBoxModel<>(cadastros.estoques.toArray(new Estoque[0])));
            cbProduto.setSelectedIndex(-1);
            txtValorProduto.setText("");
            cbServico.setModel(new DefaultComboBoxModel<>(cadastros.servicos.toArray(new Servico[0])));
            cbServico.setSelectedIndex(-1);
            txtValorServico.setText("");
        });
    }

    private void adicionarProduto() {
        Estoque estoque = (Estoque) cbProduto.getSelectedItem();
        if (estoque == null) {
            mostrarAviso("Selecione um produto.");
            return;
        }
        try {
            int quantidade = quantidadeValida(txtQtdProduto);
            double valor = valorValido(txtValorProduto);
            Integer pecaId = estoque.getPeca().getId();
            int noCarrinho = venda.getItens().stream()
                    .filter(i -> i.isProduto() && i.getPeca().getId().equals(pecaId))
                    .mapToInt(ItemVenda::getQuantidade).sum();
            if (quantidade + noCarrinho > estoque.getQuantidade()) {
                mostrarAviso("Estoque insuficiente para " + estoque.getPeca() + ".\nDisponível: "
                        + (estoque.getQuantidade() - noCarrinho));
                return;
            }

            ItemVenda existente = venda.getItens().stream()
                    .filter(i -> i.isProduto() && i.getPeca().getId().equals(pecaId) && Objects.equals(i.getValorUnitario(), valor))
                    .findFirst().orElse(null);
            if (existente != null) {
                existente.setQuantidade(existente.getQuantidade() + quantidade);
                existente.calcularSubtotal();
            } else {
                ItemVenda item = new ItemVenda();
                item.setPeca(estoque.getPeca());
                item.setDescricao(estoque.getPeca().getNome());
                item.setQuantidade(quantidade);
                item.setValorUnitario(valor);
                item.calcularSubtotal();
                venda.adicionarItem(item);
            }
            txtQtdProduto.setText("1");
            atualizarItens();
            cbProduto.requestFocus();
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void adicionarServico() {
        Servico servico = (Servico) cbServico.getSelectedItem();
        if (servico == null) {
            mostrarAviso("Selecione um serviço.");
            return;
        }
        try {
            ItemVenda item = new ItemVenda();
            item.setServico(servico);
            item.setDescricao(servico.getNome());
            item.setQuantidade(quantidadeValida(txtQtdServico));
            item.setValorUnitario(valorValido(txtValorServico));
            item.calcularSubtotal();
            venda.adicionarItem(item);
            txtQtdServico.setText("1");
            atualizarItens();
            cbServico.requestFocus();
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void removerItem() {
        int linha = tabelaItens.getSelectedRow();
        if (linha < 0) {
            mostrarAviso("Selecione um item na tabela.");
            return;
        }
        venda.getItens().remove(linha);
        atualizarItens();
    }

    private void adicionarPagamento() {
        atualizarTotais();
        try {
            Double valor = Formatador.parseValor(txtValorPagamento.getText());
            if (valor == null || valor <= 0) {
                mostrarAviso("Informe o valor do pagamento.");
                return;
            }
            venda.adicionarPagamento(new PagamentoVenda((FormaPagamento) cbForma.getSelectedItem(), valor));
            atualizarPagamentos();
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void removerPagamento() {
        int linha = tabelaPagamentos.getSelectedRow();
        if (linha < 0) {
            mostrarAviso("Selecione um pagamento na tabela.");
            return;
        }
        venda.getPagamentos().remove(linha);
        atualizarPagamentos();
    }

    private int quantidadeValida(JTextField campo) {
        Integer quantidade = Formatador.parseInteiro(campo.getText());
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Informe uma quantidade maior que zero.");
        }
        return quantidade;
    }

    private double valorValido(JTextField campo) {
        Double valor = Formatador.parseValor(campo.getText());
        if (valor == null || valor < 0) {
            throw new IllegalArgumentException("Informe um valor válido.");
        }
        return valor;
    }

    private void atualizarItens() {
        modeloItens.setRowCount(0);
        for (ItemVenda item : venda.getItens()) {
            modeloItens.addRow(new Object[]{item.isProduto() ? "Produto" : "Serviço", item.getDescricao(),
                    item.getQuantidade(), Formatador.moeda(item.getValorUnitario()), Formatador.moeda(item.getSubtotal())});
        }
        atualizarTotais();
    }

    private void atualizarPagamentos() {
        modeloPagamentos.setRowCount(0);
        for (PagamentoVenda p : venda.getPagamentos()) {
            modeloPagamentos.addRow(new Object[]{p.getForma().getDescricao(), Formatador.moeda(p.getValor())});
        }
        atualizarTotais();
    }

    private void atualizarTotais() {
        try {
            Double desconto = Formatador.parseValor(txtDesconto.getText());
            venda.setDesconto(desconto == null ? 0.0 : desconto);
        } catch (IllegalArgumentException e) {
            venda.setDesconto(0.0);
            txtDesconto.setText("0,00");
        }
        venda.recalcularTotais();
        double falta = venda.getValorTotal() - venda.getValorPago();

        lblSubtotal.setText("Subtotal: " + Formatador.moeda(venda.getValorProdutos() + venda.getValorServicos()));
        lblTotal.setText("TOTAL: " + Formatador.moeda(venda.getValorTotal()));
        lblPago.setText("Pago: " + Formatador.moeda(venda.getValorPago()));
        faltando = falta > 0.004;
        if (venda.getItens().isEmpty()) {
            lblRestante.setText("Adicione itens à venda");
            txtValorPagamento.setText("");
        } else if (faltando) {
            lblRestante.setText("Falta: " + Formatador.moeda(falta));
            txtValorPagamento.setText(Formatador.decimal(falta));
        } else {
            lblRestante.setText("Troco: " + Formatador.moeda(venda.getTroco()));
            txtValorPagamento.setText("");
        }
        lblRestante.repaint();
    }

    private void finalizar() {
        atualizarTotais();
        venda.setCliente((Pessoa) cbCliente.getSelectedItem());
        venda.setObservacao(txtObservacao.getText().isBlank() ? null : txtObservacao.getText().trim());

        btFinalizar.setEnabled(false);
        executarAsync(() -> {
            try {
                return CONTROLLER.finalizar(venda);
            } finally {
                SwingUtilities.invokeLater(() -> btFinalizar.setEnabled(true));
            }
        }, salva -> {
            String troco = salva.getTroco() > 0 ? "\nTroco: " + Formatador.moeda(salva.getTroco()) : "";
            if (confirmar("Venda Nº " + salva.getNumero() + " finalizada!" + troco + "\n\nDeseja imprimir o comprovante?")) {
                imprimir(salva.getId());
            }
            novaVenda();
            carregarCadastros();
        });
    }

    private void imprimir(Integer id) {
        executarAsync(() -> {
            try {
                return ImpressaoVenda.comprovante(CONTROLLER.buscarCompleta(id));
            } catch (Exception e) {
                throw new RuntimeException("Falha ao gerar o comprovante: " + e.getMessage(), e);
            }
        }, print -> RelatorioJasper.exibir(print, "Comprovante de venda"));
    }

    private void novaVenda() {
        venda = new Venda();
        cbCliente.setSelectedItem(null);
        txtObservacao.setText("");
        txtDesconto.setText("0,00");
        txtQtdProduto.setText("1");
        txtQtdServico.setText("1");
        cbProduto.setSelectedIndex(-1);
        cbServico.setSelectedIndex(-1);
        txtValorProduto.setText("");
        txtValorServico.setText("");
        cbForma.setSelectedIndex(0);
        atualizarItens();
        atualizarPagamentos();
    }

    private record Cadastros(List<Pessoa> clientes, List<Estoque> estoques, List<Servico> servicos) {
    }
}
