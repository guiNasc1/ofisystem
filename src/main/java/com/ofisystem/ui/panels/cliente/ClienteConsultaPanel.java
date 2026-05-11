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
        add(criarTexto("Consultar Cliente", 13), BorderLayout.NORTH);
        add(criarFiltro());
    }

    private JPanel criarFiltro(){

        JPanel painelGeral = new JPanel(new BorderLayout());
        painelGeral.setBackground(new Color(188, 188, 188, 255));

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBackground(new Color(188, 188, 188, 255));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 24, 10,24));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtFiltroNome = criarCampo(15, 255);
        txtFiltroCpf = criarCampo(15, 255);
        txtFiltroTelefone = criarCampo(15, 255);
        txtFiltroCidade = criarCampo(15, 255);

        gbc.gridx = 0; gbc.gridy = 0;
        painelCampos.add(criarLabel("Nome:", 13), gbc);
        gbc.gridx = 1;
        painelCampos.add(txtFiltroNome, gbc);

        gbc.gridx = 2;
        painelCampos.add(criarLabel("CPF:", 13), gbc);
        gbc.gridx = 3;
        painelCampos.add(txtFiltroCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painelCampos.add(criarLabel("Telefone:", 13), gbc);
        gbc.gridx = 1;
        painelCampos.add(txtFiltroTelefone, gbc);

        gbc.gridx = 2;
        painelCampos.add(criarLabel("Cidade:", 13), gbc);
        gbc.gridx = 3;
        painelCampos.add(txtFiltroCidade, gbc);

        JButton btFiltrar = criarBotao("🔍 Filtrar", new Color(0, 123, 255));
        JButton btLimpar  = criarBotao("🧹 Limpar",  new Color(108, 117, 125));

        JPanel painelBotoesFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBotoesFiltro.setBackground(new Color(245, 245, 245));
        painelBotoesFiltro.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
        painelBotoesFiltro.add(btLimpar);
        painelBotoesFiltro.add(btFiltrar);

        btFiltrar.addActionListener(e -> filtrar());
        btLimpar.addActionListener(e -> limparFiltros());

        return painelGeral;
    }

    private JScrollPane criarTabela() {
        String[] colunas = {"ID", "Nome", "CPF", "Telefone", "Email", "Cidade", "Estado"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column){
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
                    clienteSelecionado = clienteDAO.buscarPorId(id);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        scroll.setPreferredSize(new Dimension(0, 350));
        return scroll;

    }

    private JPanel criarLista() {

        JPanel lista = new JPanel(new GridBagLayout());
        lista.setBackground(new Color(188, 188, 188, 255));
        lista.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;



        return lista;
    }

    private void filtrar(){
        String nome = txtFiltroNome.getText().trim();
        String cpf = txtFiltroCpf.getText().trim();
        String telefone = txtFiltroTelefone.getText().trim();
        String cidade = txtFiltroCidade.getText().trim();

        List<Cliente> resultado = clienteDAO.filtrar();
        carregarTabela(resultado);
    }

    private void limparFiltros(){
        txtFiltroNome.setText("");
        txtFiltroCpf.setText("");
        txtFiltroCidade.setText("");
        txtFiltroTelefone.setText("");
        carregarTabela(clienteDAO.listarTodos());
    }

    private void carregarTabela(List<Cliente> clientes){
        modeloTabela.setRowCount(0);
    }

}