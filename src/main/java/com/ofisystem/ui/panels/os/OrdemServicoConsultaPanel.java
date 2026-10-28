package com.ofisystem.ui.panels.os;

import Enums.FormaPagamento;
import Enums.StatusOS;
import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.entidade.NotaFiscal;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractConsultaPanel;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RelatorioJasper;
import com.ofisystem.util.Sessao;
import net.sf.jasperreports.engine.JasperPrint;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class OrdemServicoConsultaPanel extends AbstractConsultaPanel<OrdemServico> {

    private static final OrdemServicoController CONTROLLER = new OrdemServicoController();
    private static final String EM_ABERTO = "Em aberto (não finalizadas)";

    private JTextField txtNumero;
    private JTextField txtCliente;
    private JTextField txtPlaca;
    private JComboBox<Object> cbStatus;
    private JFormattedTextField txtDe;
    private JFormattedTextField txtAte;

    @Override
    protected String titulo() {
        return "Ordens de Serviço";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Nº", "Abertura", "Cliente", "Veículo", "Status", "Previsão", "Total"};
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        txtNumero = criarCampo(10);
        txtCliente = criarCampo(100);
        txtPlaca = criarCampo(8);
        cbStatus = new JComboBox<>();
        cbStatus.addItem(EM_ABERTO);
        cbStatus.addItem("Todas");
        for (StatusOS s : StatusOS.values()) {
            cbStatus.addItem(s);
        }
        txtDe = criarCampoData();
        txtAte = criarCampoData();

        adicionarCampo(painel, gbc, 0, 0, "Número:", txtNumero);
        adicionarCampo(painel, gbc, 0, 1, "Cliente:", txtCliente);
        adicionarCampo(painel, gbc, 0, 2, "Placa:", txtPlaca);
        adicionarCampo(painel, gbc, 1, 0, "Status:", cbStatus);
        adicionarCampo(painel, gbc, 1, 1, "Abertura de:", txtDe);
        adicionarCampo(painel, gbc, 1, 2, "até:", txtAte);
        return 2;
    }

    @Override
    protected Supplier<List<OrdemServico>> consulta() {
        String numero = txtNumero.getText();
        String cliente = txtCliente.getText();
        String placa = txtPlaca.getText();
        Object selecionado = cbStatus.getSelectedItem();
        StatusOS status = selecionado instanceof StatusOS s ? s : null;
        boolean somenteAbertas = EM_ABERTO.equals(selecionado);
        LocalDate de = Formatador.parseData(txtDe.getText());
        LocalDate ate = Formatador.parseData(txtAte.getText());
        return () -> CONTROLLER.filtrar(numero, cliente, placa, status, somenteAbertas, de, ate);
    }

    @Override
    protected Object[] linha(OrdemServico os) {
        return new Object[]{
                os.getNumero(),
                Formatador.dataHora(os.getDataAbertura()),
                os.getCliente().getNome(),
                os.getVeiculoDescricao(),
                os.getStatus().getDescricao() + (atrasada(os) ? " (atrasada)" : ""),
                Formatador.data(os.getDataPrevisao()),
                Formatador.moeda(os.getValorTotal())
        };
    }

    @Override
    protected String resumo(List<OrdemServico> lista) {
        double total = lista.stream().filter(o -> o.getStatus() != StatusOS.CANCELADA)
                .mapToDouble(OrdemServico::getValorTotal).sum();
        long atrasadas = lista.stream().filter(this::atrasada).count();
        return lista.size() + " OS  |  " + atrasadas + " atrasada(s)  |  Valor (sem canceladas): " + Formatador.moeda(total);
    }

    @Override
    protected Tema.Tom corLinha(OrdemServico os) {
        return tomDa(os);
    }

    public static Tema.Tom tomDa(OrdemServico os) {
        if (os.getStatus() == StatusOS.CANCELADA) return Tema.Tom.INATIVO;
        if (os.getStatus() == StatusOS.CONCLUIDA) return Tema.Tom.SUCESSO;
        if (os.getStatus().isEditavel() && os.getDataPrevisao() != null
                && os.getDataPrevisao().isBefore(LocalDate.now())) return Tema.Tom.CRITICO;
        if (os.getStatus() == StatusOS.AGUARDANDO_PECA) return Tema.Tom.ATENCAO;
        return Tema.Tom.NEUTRO;
    }

    @Override
    protected void aposMontarTela() {
        larguraColuna(0, 70);
        larguraColuna(2, 200);
        larguraColuna(3, 180);
    }

    @Override
    protected void montarBotoes(JPanel painel) {
        adicionarBotao(painel, "Nova OS", COR_SUCESSO, () -> Navegador.ir(new OrdemServicoPanel()));
        adicionarBotao(painel, "Abrir", COR_INFO, this::abrir);
        adicionarBotao(painel, "Status", COR_PRIMARIA, this::alterarStatus);
        adicionarBotao(painel, "Concluir", COR_SUCESSO, this::concluir);
        adicionarBotao(painel, "Cancelar", COR_PERIGO, this::cancelar);
        adicionarBotao(painel, "Nota Fiscal", COR_ALERTA, this::notaFiscal);
        adicionarBotao(painel, "Imprimir", COR_NEUTRA, this::imprimir);
    }

    @Override
    protected void aoDuploClique(OrdemServico registro) {
        Navegador.ir(OrdemServicoPanel.editar(registro.getId()));
    }

    private boolean atrasada(OrdemServico os) {
        return os.getStatus().isEditavel() && os.getDataPrevisao() != null && os.getDataPrevisao().isBefore(LocalDate.now());
    }

    private void abrir() {
        OrdemServico os = selecionado();
        if (os != null) {
            Navegador.ir(OrdemServicoPanel.editar(os.getId()));
        }
    }

    private void alterarStatus() {
        OrdemServico os = selecionado();
        if (os == null) return;
        if (!os.getStatus().isEditavel()) {
            mostrarAviso("A OS " + os.getNumero() + " já está " + os.getStatus().getDescricao().toLowerCase() + ".");
            return;
        }
        StatusOS[] opcoes = {StatusOS.ABERTA, StatusOS.EM_ANDAMENTO, StatusOS.AGUARDANDO_PECA};
        StatusOS novo = (StatusOS) JOptionPane.showInputDialog(this, "Novo status da OS " + os.getNumero() + ":",
                "Alterar status", JOptionPane.PLAIN_MESSAGE, null, opcoes, os.getStatus());
        if (novo != null && novo != os.getStatus()) {
            executarAsync(() -> CONTROLLER.alterarStatus(os, novo), null, this::pesquisar);
        }
    }

    private void concluir() {
        OrdemServico os = selecionado();
        if (os == null) return;
        if (!os.getStatus().isEditavel()) {
            mostrarAviso("A OS " + os.getNumero() + " já está " + os.getStatus().getDescricao().toLowerCase() + ".");
            return;
        }

        JComboBox<FormaPagamento> cbForma = new JComboBox<>(FormaPagamento.values());
        JPanel painel = new JPanel(new GridLayout(0, 1, 4, 4));
        painel.add(new JLabel("Concluir a OS " + os.getNumero() + " de " + os.getCliente().getNome() + "?"));
        painel.add(new JLabel("Valor a receber: " + Formatador.moeda(os.getValorTotal())));
        painel.add(new JLabel("Forma de pagamento:"));
        painel.add(cbForma);

        if (JOptionPane.showConfirmDialog(this, painel, "Concluir OS", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        FormaPagamento forma = (FormaPagamento) cbForma.getSelectedItem();
        executarAsync(() -> CONTROLLER.concluir(os, forma), null, () -> {
            pesquisar();
            if (Sessao.isAdmin() && confirmar("OS " + os.getNumero() + " concluída!\nDeseja emitir a nota fiscal agora?")) {
                emitirNota(os);
            } else {
                mostrarSucesso("OS " + os.getNumero() + " concluída!");
            }
        });
    }

    private void cancelar() {
        OrdemServico os = selecionado();
        if (os == null) return;
        if (confirmar("Cancelar a OS " + os.getNumero() + "?\nAs peças lançadas voltarão para o estoque.")) {
            executarAsync(() -> CONTROLLER.cancelar(os), "OS " + os.getNumero() + " cancelada.", this::pesquisar);
        }
    }

    private void notaFiscal() {
        OrdemServico os = selecionado();
        if (os == null) return;
        if (os.getStatus() != StatusOS.CONCLUIDA) {
            mostrarAviso("A nota fiscal só pode ser emitida para OS concluída.");
            return;
        }
        executarAsync(() -> CONTROLLER.buscarNotaFiscal(os), nota -> {
            if (nota != null) {
                exibirNota(nota);
            } else if (!Sessao.isAdmin()) {
                mostrarAviso("Ainda não há nota fiscal para esta OS.\nSomente administradores podem emitir.");
            } else if (confirmar("Emitir nota fiscal para a OS " + os.getNumero() + "?")) {
                emitirNota(os);
            }
        });
    }

    private void emitirNota(OrdemServico os) {
        executarAsync(() -> CONTROLLER.emitirNotaFiscal(os), nota -> {
            mostrarSucesso("Nota fiscal Nº " + nota.getNumero() + " emitida!");
            exibirNota(nota);
        });
    }

    private void exibirNota(NotaFiscal nota) {
        executarAsync(() -> gerar(() -> ImpressaoOS.notaFiscal(nota)),
                print -> RelatorioJasper.exibir(print, "Nota Fiscal " + nota.getNumero()));
    }

    private void imprimir() {
        OrdemServico os = selecionado();
        if (os == null) return;
        executarAsync(() -> gerar(() -> ImpressaoOS.ordemServico(CONTROLLER.buscarCompleta(os.getId()))),
                print -> RelatorioJasper.exibir(print, "Ordem de Serviço " + os.getNumero()));
    }

    private JasperPrint gerar(Impressao impressao) {
        try {
            return impressao.gerar();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar a impressão: " + e.getMessage(), e);
        }
    }

    private interface Impressao {
        JasperPrint gerar() throws Exception;
    }
}
