package com.ofisystem.ui.panels.venda;

import Enums.StatusVenda;
import com.ofisystem.controle.venda.VendaController;
import com.ofisystem.entidade.Venda;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractConsultaPanel;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.util.Formatador;
import com.ofisystem.util.RelatorioJasper;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class VendaConsultaPanel extends AbstractConsultaPanel<Venda> {

    private static final VendaController CONTROLLER = new VendaController();

    private JTextField txtNumero;
    private JTextField txtCliente;
    private JComboBox<Object> cbStatus;
    private JFormattedTextField txtDe;
    private JFormattedTextField txtAte;

    @Override
    protected String titulo() {
        return "Vendas";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Nº", "Data", "Cliente", "Pagamento", "Total", "Troco", "Situação"};
    }

    @Override
    protected int montarFiltros(JPanel painel, GridBagConstraints gbc) {
        txtNumero = criarCampo(10);
        txtCliente = criarCampo(100);
        cbStatus = new JComboBox<>(new Object[]{StatusVenda.FINALIZADA, StatusVenda.CANCELADA, "Todas"});
        txtDe = criarCampoData();
        txtAte = criarCampoData();
        definirData(txtDe, LocalDate.now());

        adicionarCampo(painel, gbc, 0, 0, "Número:", txtNumero);
        adicionarCampo(painel, gbc, 0, 1, "Cliente:", txtCliente);
        adicionarCampo(painel, gbc, 0, 2, "Situação:", cbStatus);
        adicionarCampo(painel, gbc, 1, 0, "Data de:", txtDe);
        adicionarCampo(painel, gbc, 1, 1, "até:", txtAte);
        return 2;
    }

    @Override
    protected void limparFiltros() {
        super.limparFiltros();
        definirData(txtDe, LocalDate.now());
    }

    @Override
    protected Supplier<List<Venda>> consulta() {
        String numero = txtNumero.getText();
        String cliente = txtCliente.getText();
        StatusVenda status = cbStatus.getSelectedItem() instanceof StatusVenda s ? s : null;
        LocalDate de = Formatador.parseData(txtDe.getText());
        LocalDate ate = Formatador.parseData(txtAte.getText());
        return () -> CONTROLLER.filtrar(numero, cliente, status, de, ate);
    }

    @Override
    protected Object[] linha(Venda v) {
        return new Object[]{
                v.getNumero(),
                Formatador.dataHora(v.getData()),
                v.getCliente() == null ? "Consumidor final" : v.getCliente().getNome(),
                v.getFormasPagamento(),
                Formatador.moeda(v.getValorTotal()),
                v.getTroco() > 0 ? Formatador.moeda(v.getTroco()) : "",
                v.getStatus().getDescricao()
        };
    }

    @Override
    protected String resumo(List<Venda> lista) {
        double total = lista.stream().filter(v -> v.getStatus() == StatusVenda.FINALIZADA)
                .mapToDouble(Venda::getValorTotal).sum();
        long finalizadas = lista.stream().filter(v -> v.getStatus() == StatusVenda.FINALIZADA).count();
        return lista.size() + " venda(s)  |  Total vendido: " + Formatador.moeda(total)
                + "  |  Ticket médio: " + Formatador.moeda(finalizadas == 0 ? 0 : total / finalizadas);
    }

    @Override
    protected String colunaEtiqueta() {
        return "Situação";
    }

    @Override
    protected Tema.Tom corLinha(Venda v) {
        return v.getStatus() == StatusVenda.CANCELADA ? Tema.Tom.INATIVO : Tema.Tom.SUCESSO;
    }

    @Override
    protected void aposMontarTela() {
        larguraColuna(0, 70);
        larguraColuna(2, 200);
        larguraColuna(3, 180);
    }

    @Override
    protected void montarBotoes(JPanel painel) {
        adicionarBotao(painel, "Nova venda", COR_SUCESSO, () -> Navegador.ir(new VendaPanel()));
        adicionarBotao(painel, "Comprovante", COR_INFO, this::imprimir);
        adicionarBotao(painel, "Cancelar venda", COR_PERIGO, this::cancelar);
    }

    @Override
    protected void aoDuploClique(Venda registro) {
        imprimir();
    }

    private void imprimir() {
        Venda v = selecionado();
        if (v == null) return;
        executarAsync(() -> {
            try {
                return ImpressaoVenda.comprovante(CONTROLLER.buscarCompleta(v.getId()));
            } catch (Exception e) {
                throw new RuntimeException("Falha ao gerar o comprovante: " + e.getMessage(), e);
            }
        }, print -> RelatorioJasper.exibir(print, "Venda " + v.getNumero()));
    }

    private void cancelar() {
        Venda v = selecionado();
        if (v == null) return;
        if (v.getStatus() == StatusVenda.CANCELADA) {
            mostrarAviso("Esta venda já está cancelada.");
            return;
        }
        if (confirmar("Cancelar a venda " + v.getNumero() + " de " + Formatador.moeda(v.getValorTotal()) + "?"
                + "\nOs produtos voltarão para o estoque.")) {
            executarAsync(() -> CONTROLLER.cancelar(v), "Venda " + v.getNumero() + " cancelada.", this::pesquisar);
        }
    }
}
