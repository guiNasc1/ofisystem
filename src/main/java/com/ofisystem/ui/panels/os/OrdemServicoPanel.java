package com.ofisystem.ui.panels.os;

import com.ofisystem.controle.estoque.EstoqueController;
import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.controle.pessoa.PessoaController;
import com.ofisystem.controle.servico.ServicoController;
import com.ofisystem.entidade.Estoque;
import com.ofisystem.entidade.ItemOs;
import com.ofisystem.entidade.ItemPeca;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.entidade.Pessoa;
import com.ofisystem.entidade.Servico;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.ui.style.Etiqueta;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RelatorioJasper;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrdemServicoPanel extends AbstractPanel {

    private static final OrdemServicoController CONTROLLER = new OrdemServicoController();
    private static final PessoaController PESSOA_CONTROLLER = new PessoaController();
    private static final ServicoController SERVICO_CONTROLLER = new ServicoController();
    private static final EstoqueController ESTOQUE_CONTROLLER = new EstoqueController();

    private JLabel lblTitulo;
    private Etiqueta lblStatus;
    private JLabel lblAbertura;
    private JLabel lblResponsavel;
    private JComboBox<Pessoa> cbCliente;
    private JCheckBox chkVeiculo;
    private JPanel painelVeiculo;
    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtAno;
    private JTextField txtKm;
    private JFormattedTextField txtPrevisao;
    private JTextArea txtDefeito;
    private JTextArea txtDiagnostico;

    private JComboBox<Servico> cbServico;
    private JTextField txtQtdServico;
    private JTextField txtValorServico;
    private DefaultTableModel modeloServicos;
    private JTable tabelaServicos;

    private JComboBox<Estoque> cbPeca;
    private JTextField txtQtdPeca;
    private JTextField txtValorPeca;
    private DefaultTableModel modeloPecas;
    private JTable tabelaPecas;

    private JLabel lblTotalServicos;
    private JLabel lblTotalPecas;
    private JTextField txtDesconto;
    private JLabel lblTotal;
    private JButton btSalvar;

    private OrdemServico os;
    private Map<Integer, Integer> quantidadeOriginal;

    public OrdemServicoPanel() {
        this(null);
    }

    private OrdemServicoPanel(Integer id) {
        carregar(id);
    }

    public static OrdemServicoPanel editar(Integer id) {
        return new OrdemServicoPanel(id);
    }

    @Override
    protected void inicializarTela() {
        JPanel dadosGerais = criarDadosGerais();
        lblTitulo = criarTexto("Nova Ordem de Serviço");
        add(criarCabecalho(lblTitulo, criarResumoOs()), BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setOpaque(false);
        conteudo.add(dadosGerais);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(Tema.fonte(Tema.TAM_TEXTO, Font.BOLD));
        abas.addTab("Serviços", criarAbaServicos());
        abas.addTab("Peças", criarAbaPecas());
        abas.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));
        abas.setPreferredSize(new Dimension(600, 260));
        conteudo.add(abas);

        add(comRolagemVertical(conteudo), BorderLayout.CENTER);

        JPanel rodape = criarRodape();
        adicionarBotao(rodape, "Voltar", COR_NEUTRA, () -> Navegador.ir(new OrdemServicoConsultaPanel()));
        btSalvar = adicionarBotao(rodape, "Salvar OS", COR_SUCESSO, this::salvar);

        JPanel base = new JPanel(new BorderLayout());
        base.setOpaque(false);
        base.add(criarTotais(), BorderLayout.NORTH);
        base.add(rodape, BorderLayout.SOUTH);
        add(base, BorderLayout.SOUTH);
    }

    private JPanel criarDadosGerais() {
        JPanel painel = criarPainelFormulario();
        GridBagConstraints gbc = criarGbc();

        lblStatus = new Etiqueta("Aberta", Tema.Tom.NEUTRO);
        lblAbertura = criarLabel(Formatador.dataHora(java.time.LocalDateTime.now()), 14);
        lblResponsavel = criarLabel(Sessao.getNome(), 14);
        cbCliente = new JComboBox<>();
        txtPrevisao = criarCampoData();
        txtDefeito = criarAreaTexto(3);
        txtDiagnostico = criarAreaTexto(2);
        chkVeiculo = new JCheckBox("Possui veículo vinculado");
        chkVeiculo.setOpaque(false);
        chkVeiculo.setFont(Tema.fonte(Tema.TAM_TEXTO, Font.BOLD));

        adicionarCampo(painel, gbc, 1, 0, "Cliente:", cbCliente, 2);
        adicionarCampoCompacto(painel, gbc, 1, 2, "Previsão entrega:", txtPrevisao);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        painel.add(chkVeiculo, gbc);
        gbc.gridy = 3;
        painel.add(criarDadosVeiculo(), gbc);
        gbc.gridwidth = 1;

        adicionarCampo(painel, gbc, 4, 0, "Defeito relatado:", comRolagem(txtDefeito), 3);
        adicionarCampo(painel, gbc, 5, 0, "Diagnóstico / obs.:", comRolagem(txtDiagnostico), 3);

        chkVeiculo.addActionListener(e -> atualizarVeiculo());
        return painel;
    }

    private JPanel criarDadosVeiculo() {
        painelVeiculo = new JPanel(new GridBagLayout());
        painelVeiculo.setOpaque(false);
        GridBagConstraints gbc = criarGbc();
        gbc.insets = new Insets(5, 0, 5, 6);

        txtPlaca = criarCampo(10);
        txtMarca = criarCampo(60);
        txtModelo = criarCampo(100);
        txtAno = criarCampo(4);
        txtKm = criarCampo(8);
        txtPlaca.putClientProperty("JTextField.placeholderText", "ABC1D23");
        txtMarca.putClientProperty("JTextField.placeholderText", "Honda, Fiat, Scania...");

        adicionarCampo(painelVeiculo, gbc, 0, 0, "Placa:", txtPlaca);
        adicionarCampo(painelVeiculo, gbc, 0, 1, "Marca:", txtMarca);
        adicionarCampo(painelVeiculo, gbc, 0, 2, "Modelo:", txtModelo);
        adicionarCampo(painelVeiculo, gbc, 1, 0, "Ano:", txtAno);
        adicionarCampo(painelVeiculo, gbc, 1, 1, "KM:", txtKm);
        painelVeiculo.setVisible(false);
        return painelVeiculo;
    }

    private void atualizarVeiculo() {
        painelVeiculo.setVisible(chkVeiculo.isSelected());
        painelVeiculo.revalidate();
        if (chkVeiculo.isSelected() && chkVeiculo.isEnabled()) {
            txtPlaca.requestFocus();
        }
    }

    private JPanel criarResumoOs() {
        JPanel resumo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        resumo.setOpaque(false);
        resumo.add(lblStatus);
        resumo.add(Box.createHorizontalStrut(24));
        resumo.add(criarLabel("Abertura: ", 14));
        resumo.add(lblAbertura);
        resumo.add(Box.createHorizontalStrut(24));
        resumo.add(criarLabel("Responsável: ", 14));
        resumo.add(lblResponsavel);

        JPanel alinhado = new JPanel(new GridBagLayout());
        alinhado.setOpaque(false);
        alinhado.add(resumo);
        return alinhado;
    }

    private JPanel criarAbaServicos() {
        cbServico = new JComboBox<>();
        txtQtdServico = criarCampo(4, 4);
        txtValorServico = criarCampo(8, 15);
        txtQtdServico.setText("1");
        cbServico.addActionListener(e -> {
            Servico s = (Servico) cbServico.getSelectedItem();
            if (s != null) txtValorServico.setText(Formatador.decimal(s.getValor()));
        });

        modeloServicos = criarModelo("Serviço", "Qtd.", "Valor unit.", "Subtotal");
        tabelaServicos = criarTabela(modeloServicos);

        return criarAba(cbServico, txtQtdServico, txtValorServico, tabelaServicos,
                this::adicionarServico, this::removerServico);
    }

    private JPanel criarAbaPecas() {
        cbPeca = new JComboBox<>();
        txtQtdPeca = criarCampo(4, 4);
        txtValorPeca = criarCampo(8, 15);
        txtQtdPeca.setText("1");
        cbPeca.addActionListener(e -> {
            Estoque est = (Estoque) cbPeca.getSelectedItem();
            if (est != null) txtValorPeca.setText(Formatador.decimal(est.getPeca().getValorUnitario()));
        });

        modeloPecas = criarModelo("ID", "Peça", "Qtd.", "Valor unit.", "Subtotal");
        tabelaPecas = criarTabela(modeloPecas);

        return criarAba(cbPeca, txtQtdPeca, txtValorPeca, tabelaPecas,
                this::adicionarPeca, this::removerPeca);
    }

    private JPanel criarAba(JComboBox<?> combo, JTextField qtd, JTextField valor, JTable tabela,
                            Runnable adicionar, Runnable remover) {
        JPanel aba = new JPanel(new BorderLayout(0, 6));
        aba.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel barra = new JPanel(new GridBagLayout());
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
        barra.add(criarLabel("Valor (R$):", 13), gbc);
        gbc.gridx++;
        barra.add(valor, gbc);
        gbc.gridx++;
        barra.add(adicionarBotao(new JPanel(), "Adicionar", COR_SUCESSO, adicionar), gbc);
        gbc.gridx++;
        barra.add(adicionarBotao(new JPanel(), "Remover", COR_PERIGO, remover), gbc);

        qtd.addActionListener(e -> adicionar.run());
        valor.addActionListener(e -> adicionar.run());

        aba.add(barra, BorderLayout.NORTH);
        aba.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return aba;
    }

    private JPanel criarTotais() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 8));
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(4, 24, 4, 24));

        lblTotalServicos = criarLabel("Serviços: R$ 0,00", 15);
        lblTotalPecas = criarLabel("Peças: R$ 0,00", 15);
        txtDesconto = criarCampo(8, 15);
        txtDesconto.setText("0,00");
        lblTotal = new Texto("TOTAL: R$ 0,00", Tema.TAM_TITULO, Font.BOLD, Tema::aco);

        txtDesconto.addActionListener(e -> atualizarTotais());
        txtDesconto.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                atualizarTotais();
            }
        });

        painel.add(lblTotalServicos);
        painel.add(lblTotalPecas);
        painel.add(criarLabel("Desconto (R$):", 15));
        painel.add(txtDesconto);
        painel.add(lblTotal);
        return painel;
    }

    private void carregar(Integer id) {
        executarAsync(() -> new Dados(
                PESSOA_CONTROLLER.listarClientesAtivos(),
                SERVICO_CONTROLLER.listarAtivos(),
                ESTOQUE_CONTROLLER.listarAtivos(),
                id == null ? null : CONTROLLER.buscarCompleta(id)), this::preencher);
    }

    private void preencher(Dados dados) {
        cbServico.setModel(new DefaultComboBoxModel<>(dados.servicos.toArray(new Servico[0])));
        cbServico.setSelectedIndex(-1);
        txtValorServico.setText("");
        cbPeca.setModel(new DefaultComboBoxModel<>(dados.estoques.toArray(new Estoque[0])));
        cbPeca.setSelectedIndex(-1);
        txtValorPeca.setText("");

        DefaultComboBoxModel<Pessoa> clientes = new DefaultComboBoxModel<>(dados.clientes.toArray(new Pessoa[0]));
        os = dados.ordemServico != null ? dados.ordemServico : new OrdemServico();
        if (os.getCliente() != null && clientes.getIndexOf(os.getCliente()) < 0) {
            clientes.addElement(os.getCliente());
        }
        cbCliente.setModel(clientes);
        cbCliente.setSelectedItem(os.getCliente());

        quantidadeOriginal = new HashMap<>();
        for (ItemPeca item : os.getItensPeca()) {
            quantidadeOriginal.merge(item.getPeca().getId(), item.getQuantidade(), Integer::sum);
        }

        if (os.getId() != null) {
            lblTitulo.setText("Ordem de Serviço Nº " + os.getNumero());
            lblStatus.setDados(os.getStatus().getDescricao(), OrdemServicoConsultaPanel.tomDa(os));
            lblAbertura.setText(Formatador.dataHora(os.getDataAbertura()));
            lblResponsavel.setText(Formatador.texto(os.getResponsavel()));
            chkVeiculo.setSelected(os.isPossuiVeiculo());
            txtPlaca.setText(os.getVeiculoPlaca());
            txtMarca.setText(os.getVeiculoMarca());
            txtModelo.setText(os.getVeiculoModelo());
            txtAno.setText(Formatador.texto(os.getVeiculoAno()));
            txtKm.setText(Formatador.texto(os.getKm()));
            atualizarVeiculo();
            definirData(txtPrevisao, os.getDataPrevisao());
            txtDefeito.setText(os.getDescricao());
            txtDiagnostico.setText(os.getDiagnostico());
            txtDesconto.setText(Formatador.decimal(os.getDesconto()));

            if (!os.getStatus().isEditavel()) {
                bloquearEdicao();
            }
        }
        atualizarTabelas();
    }

    private void bloquearEdicao() {
        btSalvar.setEnabled(false);
        lblStatus.setDados(os.getStatus().getDescricao() + " · somente leitura", OrdemServicoConsultaPanel.tomDa(os));
        for (JComponent c : new JComponent[]{cbCliente, chkVeiculo, txtPlaca, txtMarca, txtModelo, txtAno, txtKm, txtPrevisao, txtDesconto,
                cbServico, txtQtdServico, txtValorServico, cbPeca, txtQtdPeca, txtValorPeca}) {
            c.setEnabled(false);
        }
        txtDefeito.setEditable(false);
        txtDiagnostico.setEditable(false);
    }

    private void adicionarServico() {
        Servico servico = (Servico) cbServico.getSelectedItem();
        if (servico == null) {
            mostrarAviso("Selecione um serviço.");
            return;
        }
        try {
            Integer quantidade = quantidadeValida(txtQtdServico);
            Double valor = valorValido(txtValorServico);
            ItemOs item = new ItemOs();
            item.setServico(servico);
            item.setDescricao(servico.getNome());
            item.setQuantidade(quantidade);
            item.setValorUnitario(valor);
            item.calcularSubtotal();
            os.adicionarServico(item);
            atualizarTabelas();
            txtQtdServico.setText("1");
            cbServico.requestFocus();
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void removerServico() {
        int linha = tabelaServicos.getSelectedRow();
        if (linha < 0) {
            mostrarAviso("Selecione um serviço na tabela.");
            return;
        }
        os.getItensServico().remove(linha);
        atualizarTabelas();
    }

    private void adicionarPeca() {
        Estoque estoque = (Estoque) cbPeca.getSelectedItem();
        if (estoque == null) {
            mostrarAviso("Selecione uma peça.");
            return;
        }
        try {
            Integer quantidade = quantidadeValida(txtQtdPeca);
            Double valor = valorValido(txtValorPeca);
            Integer pecaId = estoque.getPeca().getId();

            ItemPeca existente = os.getItensPeca().stream()
                    .filter(i -> i.getPeca().getId().equals(pecaId) && Objects.equals(i.getValorUnitario(), valor))
                    .findFirst().orElse(null);
            int jaNaOs = os.getItensPeca().stream()
                    .filter(i -> i.getPeca().getId().equals(pecaId)).mapToInt(ItemPeca::getQuantidade).sum();
            int disponivel = estoque.getQuantidade() + quantidadeOriginal.getOrDefault(pecaId, 0) - jaNaOs;
            if (quantidade > disponivel) {
                mostrarAviso("Estoque insuficiente para " + estoque.getPeca() + ".\nDisponível: " + disponivel);
                return;
            }

            if (existente != null) {
                existente.setQuantidade(existente.getQuantidade() + quantidade);
                existente.calcularSubtotal();
            } else {
                ItemPeca item = new ItemPeca();
                item.setPeca(estoque.getPeca());
                item.setQuantidade(quantidade);
                item.setValorUnitario(valor);
                item.calcularSubtotal();
                os.adicionarPeca(item);
            }
            atualizarTabelas();
            txtQtdPeca.setText("1");
            cbPeca.requestFocus();
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void removerPeca() {
        int linha = tabelaPecas.getSelectedRow();
        if (linha < 0) {
            mostrarAviso("Selecione uma peça na tabela.");
            return;
        }
        os.getItensPeca().remove(linha);
        atualizarTabelas();
    }

    private Integer quantidadeValida(JTextField campo) {
        Integer quantidade = Formatador.parseInteiro(campo.getText());
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Informe uma quantidade maior que zero.");
        }
        return quantidade;
    }

    private Double valorValido(JTextField campo) {
        Double valor = Formatador.parseValor(campo.getText());
        if (valor == null || valor < 0) {
            throw new IllegalArgumentException("Informe um valor válido.");
        }
        return valor;
    }

    private void atualizarTabelas() {
        modeloServicos.setRowCount(0);
        for (ItemOs item : os.getItensServico()) {
            modeloServicos.addRow(new Object[]{item.getDescricao(), item.getQuantidade(),
                    Formatador.moeda(item.getValorUnitario()), Formatador.moeda(item.getSubtotal())});
        }
        modeloPecas.setRowCount(0);
        for (ItemPeca item : os.getItensPeca()) {
            modeloPecas.addRow(new Object[]{item.getPeca().getId(), item.getPeca().getNome(), item.getQuantidade(),
                    Formatador.moeda(item.getValorUnitario()), Formatador.moeda(item.getSubtotal())});
        }
        atualizarTotais();
    }

    private void atualizarTotais() {
        if (os == null) return;
        try {
            Double desconto = Formatador.parseValor(txtDesconto.getText());
            os.setDesconto(desconto == null ? 0.0 : desconto);
        } catch (IllegalArgumentException e) {
            os.setDesconto(0.0);
            txtDesconto.setText("0,00");
        }
        os.recalcularTotais();
        lblTotalServicos.setText("Serviços: " + Formatador.moeda(os.getValorServicos()));
        lblTotalPecas.setText("Peças: " + Formatador.moeda(os.getValorPecas()));
        lblTotal.setText("TOTAL: " + Formatador.moeda(os.getValorTotal()));
    }

    private void salvar() {
        if (os == null) return;
        try {
            os.setKm(Formatador.parseInteiro(txtKm.getText()));
            os.setVeiculoAno(Formatador.parseInteiro(txtAno.getText()));
            os.setDataPrevisao(Formatador.parseData(txtPrevisao.getText()));
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
            return;
        }
        atualizarTotais();
        os.setCliente((Pessoa) cbCliente.getSelectedItem());
        os.setPossuiVeiculo(chkVeiculo.isSelected());
        os.setVeiculoPlaca(txtPlaca.getText());
        os.setVeiculoMarca(txtMarca.getText());
        os.setVeiculoModelo(txtModelo.getText());
        os.setDescricao(txtDefeito.getText().trim());
        os.setDiagnostico(txtDiagnostico.getText().isBlank() ? null : txtDiagnostico.getText().trim());

        btSalvar.setEnabled(false);
        executarAsync(() -> {
            try {
                return CONTROLLER.salvar(os);
            } finally {
                SwingUtilities.invokeLater(() -> btSalvar.setEnabled(true));
            }
        }, salva -> {
            if (confirmar("OS Nº " + salva.getNumero() + " salva com sucesso!\nDeseja imprimir a ordem de serviço?")) {
                imprimir(salva.getId());
            }
            Navegador.ir(new OrdemServicoConsultaPanel());
        });
    }

    private void imprimir(Integer id) {
        executarAsync(() -> {
            try {
                return ImpressaoOS.ordemServico(CONTROLLER.buscarCompleta(id));
            } catch (Exception e) {
                throw new RuntimeException("Falha ao gerar a impressão: " + e.getMessage(), e);
            }
        }, print -> RelatorioJasper.exibir(print, "Ordem de Serviço"));
    }

    private record Dados(List<Pessoa> clientes, List<Servico> servicos,
                         List<Estoque> estoques, OrdemServico ordemServico) {
    }
}
