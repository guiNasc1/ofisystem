package com.ofisystem.ui.panels.cliente;

import com.ofisystem.entidade.Cliente;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.dao.cliente.ClienteDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.ParseException;

public class ClienteConsultaPanel extends AbstractPanel {

    private JTextField txtFiltroNome;
    private JTextField txtFiltroCpf;
    private JTextField txtFiltroTelefone;
    private JTextField txtFiltroCidade;

    private JPanel painelFiltroTabela;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private JButton btNovo;
    private JButton btEditar;
    private JButton btExcluir;

    private Cliente clienteSelecionado;
    private ClienteDAO clienteDAO;

    private Runnable aoClicarNovo;

    public ClienteConsultaPanel(Runnable aoClicarNovo) throws ParseException {
        this.aoClicarNovo = aoClicarNovo;
    }

    public ClienteConsultaPanel() throws ParseException {
    }

    @Override
    protected void inicializarTela() {
        clienteDAO = new ClienteDAO();
        setLayout(new BorderLayout());
        JPanel centro = new JPanel(new BorderLayout());

        centro.add(criarFiltro(), BorderLayout.NORTH);
        centro.add(criarTabela(), BorderLayout.CENTER);

        add(criarTexto("Consultar Cliente", 13), BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(criarFooter(), BorderLayout.SOUTH);

        carregarTabela(clienteDAO::listarTodosPaginado);
    }

    private JPanel criarFiltro() {

        JPanel painelGeral = new JPanel(new BorderLayout());
        painelGeral.setBackground(new Color(255, 0, 0, 255));

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBackground(new Color(188, 188, 188, 255));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtFiltroNome = criarCampo(15, 255);
        txtFiltroCpf = criarCampo(15, 255);
        txtFiltroTelefone = criarCampo(15, 255);
        txtFiltroCidade = criarCampo(15, 255);

        gbc.gridy = 0;
        painelCampos.add(criarLabel("Nome:", 13), gbc);
        gbc.gridx = 1;
        painelCampos.add(txtFiltroNome, gbc);

        gbc.gridx = 2;
        painelCampos.add(criarLabel("CPF:", 13), gbc);
        gbc.gridx = 3;
        painelCampos.add(txtFiltroCpf, gbc);

        gbc.gridx = 4;
        painelCampos.add(criarLabel("Telefone:", 13), gbc);
        gbc.gridx = 5;
        painelCampos.add(txtFiltroTelefone, gbc);

        gbc.gridx = 6;
        painelCampos.add(criarLabel("Cidade:", 13), gbc);
        gbc.gridx = 7;
        painelCampos.add(txtFiltroCidade, gbc);

        JButton btFiltrar = criarBotao("🔍 Filtrar", new Color(0, 123, 255));
        JButton btLimpar = criarBotao("🧹 Limpar", new Color(108, 117, 125));

        JPanel painelBotoesFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBotoesFiltro.setBackground(new Color(14, 198, 17));
        painelBotoesFiltro.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
        painelBotoesFiltro.add(btLimpar);
        painelBotoesFiltro.add(btFiltrar);

        painelGeral.add(painelCampos, BorderLayout.CENTER);

        btFiltrar.addActionListener(e -> filtrarCampos());
        btLimpar.addActionListener(e -> limparFiltros());

        return painelGeral;
    }

    private JScrollPane criarTabela() {
        String[] colunas = {"ID", "Nome", "CPF", "Telefone", "Email", "Cidade", "Estado"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setGridColor(new Color(220, 220, 220));

        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);

        // Ao selecionar linha — habilita botões editar e excluir
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                boolean temSeleção = linha != -1;
                btEditar.setEnabled(temSeleção);
                btExcluir.setEnabled(temSeleção);

                if (temSeleção) {
                    Integer id = (Integer) modeloTabela.getValueAt(linha, 0);
                    clienteSelecionado = clienteDAO.buscarPorID(id);
                }
            }
        });

        painelFiltroTabela = new JPanel(null);
        painelFiltroTabela.setBackground(new Color(255, 255, 255, 255));
        painelFiltroTabela.setPreferredSize(new Dimension(0, 34));

        JTextField[] campos = {
                new JTextField(), // ID
                txtFiltroNome,
                txtFiltroCpf,
                txtFiltroTelefone,
                new JTextField(), // Email
                txtFiltroCidade,
                new JTextField() // Estado
        };

        for (JTextField c : campos) {
            c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
            painelFiltroTabela.add(c);
        }

        Runnable sincronizar = () -> {
            int x = 0;
            for( int i = 0; i < tabela.getColumnCount(); i++ ) {
                int largura = tabela.getColumnModel().getColumn(i).getWidth();
                campos[i].setBounds(x + 2, 3, largura -4, 28);
                x += largura;
            }
            painelFiltroTabela.revalidate();
        };

        tabela.getColumnModel().addColumnModelListener(new javax.swing.event.TableColumnModelListener() {
            public void columnMarginChanged(javax.swing.event.ChangeEvent e) { sincronizar.run(); }
            public void columnAdded(javax.swing.event.TableColumnModelEvent e) {}
            public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {}
            public void columnMoved(javax.swing.event.TableColumnModelEvent e) { sincronizar.run(); }
            public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {}
        });

        // Atualiza ao redimensionar a janela
        tabela.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) { sincronizar.run(); }
        });

        // Dispara filtro ao digitar
        for (JTextField c : new JTextField[]{txtFiltroNome, txtFiltroCpf, txtFiltroTelefone, txtFiltroCidade}) {
            c.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarCampos(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarCampos(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            });
        }

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        scroll.setPreferredSize(new Dimension(0, 350));

        carregarTabelaAsync(clienteDAO::listarTodosPaginado);

        return scroll;

    }

    private JPanel criarFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        footer.setBackground(new Color(235, 235, 23));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));

        btNovo = criarBotao("Novo Cliente", new Color(40, 167, 69));
        btEditar = criarBotao("Editar", new Color(123, 173, 253));
        btExcluir = criarBotao("Excluir", new Color(189, 34, 34));

        btNovo.setPreferredSize(new Dimension(150, 38));
        btEditar.setPreferredSize(new Dimension(120, 38));
        btExcluir.setPreferredSize(new Dimension(120, 38));

        btEditar.setEnabled(false);
        btExcluir.setEnabled(false);

        btNovo.addActionListener(e -> aoClicarNovo.run());
        btEditar.addActionListener(e -> abrirEdicao());

        footer.add(btNovo);
        footer.add(btEditar);
        footer.add(btExcluir);

        return footer;
    }

    // - Ações -

    private void filtrarCampos() {
        String nome     = txtFiltroNome.getText().trim();
        String cpf      = txtFiltroCpf.getText().trim();
        String telefone = txtFiltroTelefone.getText().trim();
        String cidade   = txtFiltroCidade.getText().trim();

        List<Cliente> resultado = clienteDAO.filtrar(nome, cpf, telefone, cidade);
        carregarTabela(() -> clienteDAO.filtrar(nome, cpf, telefone, cidade));
    }

    private void limparFiltros() {
        txtFiltroNome.setText("");
        txtFiltroCpf.setText("");
        txtFiltroCidade.setText("");
        txtFiltroTelefone.setText("");
        carregarTabela(clienteDAO::listarTodosPaginado);
    }

    private void excluir() {
        if (clienteSelecionado == null) {
            return;
        }

        if (confirmar("Deseja excluir o cliente " + clienteSelecionado.getCliNome() + "?")) {
            clienteDAO.deletar(clienteSelecionado);
            mostrarSucesso("Cliente excluído com sucesso!");
            clienteSelecionado = null;
            btEditar.setEnabled(false);
            btExcluir.setEnabled(false);

        }
    }

    // - Dialog -

    private void abrirEdicao(){
        if(clienteSelecionado == null) return;

        JDialog dialog = new JDialog();
        dialog.setTitle("Editar Cliente — " + clienteSelecionado.getCliNome());
        dialog.setSize(450, 320);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNome     = new JTextField(clienteSelecionado.getCliNome(), 20);
        JTextField txtCpf      = new JTextField(clienteSelecionado.getCliCpf(), 15);
        JTextField txtTelefone = new JTextField(clienteSelecionado.getCliTelefone(), 15);
        JTextField txtEmail    = new JTextField(clienteSelecionado.getCliEmail(), 20);

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        painel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        painel.add(txtCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        painel.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        painel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        painel.add(txtEmail, gbc);

        // Footer do dialog
        JPanel footerDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btCancelar = new JButton("Cancelar");
        JButton btSalvar   = new JButton("💾  Salvar");
        btSalvar.setBackground(new Color(40, 167, 69));
        btSalvar.setForeground(Color.WHITE);
        btSalvar.setFocusPainted(false);

        btCancelar.addActionListener(e -> dialog.dispose());

        btSalvar.addActionListener(e -> {
            clienteSelecionado.setCliNome(txtNome.getText().trim());
            clienteSelecionado.setCliCpf(txtCpf.getText().trim());
            clienteSelecionado.setCliTelefone(txtTelefone.getText().trim());
            clienteSelecionado.setCliEmail(txtEmail.getText().trim());
            clienteDAO.atualizar(clienteSelecionado);
            mostrarSucesso("Cliente atualizado com sucesso!");
            dialog.dispose();
            carregarTabela(clienteDAO::listarTodosPaginado);
        });

        footerDialog.add(btCancelar);
        footerDialog.add(btSalvar);

        dialog.add(painel, BorderLayout.CENTER);
        dialog.add(footerDialog, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void carregarTabela(java.util.function.Supplier<List<Cliente>> supplier) {
        carregarTabelaAsync(supplier);
    }

    private void carregarTabelaAsync(java.util.function.Supplier<List<Cliente>> supplier){
        modeloTabela.setRowCount(0);
        modeloTabela.addRow(new Object[]{null, "Carregando...", "", "", "", "", "",});
        tabela.setEnabled(false);

        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                return supplier.get();
            }

            @Override
            protected void done(){
                try{
                    List<Cliente> clientes = get();
                    modeloTabela.setRowCount(0);

                    for(Cliente c : clientes) {
                        String cidade = c.getEndereco().isEmpty() ? "" : c.getEndereco().get(0).getCidade();
                        String estado = c.getEndereco().isEmpty() ? "" : c.getEndereco().get(0).getEstado();

                        modeloTabela.addRow(new Object[]{
                                c.getId(),
                                c.getCliNome(),
                                c.getCliCpf(),
                                c.getCliTelefone(),
                                c.getCliEmail(),
                                cidade,
                                estado
                        });
                    }
                } catch (Exception e) {
                    modeloTabela.setRowCount(0);
                    mostrarErro("Erro ao carregar clientes: " + e.getMessage());
                } finally {
                    tabela.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

}