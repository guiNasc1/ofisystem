package com.ofisystem.ui.panels.inicio;

import Enums.FormaPagamento;
import Enums.StatusOS;
import Enums.StatusVenda;
import com.ofisystem.controle.estoque.EstoqueController;
import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.controle.venda.VendaController;
import com.ofisystem.entidade.OrdemServico;
import com.ofisystem.entidade.Venda;
import com.ofisystem.ui.Navegador;
import com.ofisystem.ui.panels.AbstractPanel;
import com.ofisystem.ui.panels.estoque.EstoqueConsultaPanel;
import com.ofisystem.ui.panels.os.OrdemServicoConsultaPanel;
import com.ofisystem.ui.panels.os.OrdemServicoPanel;
import com.ofisystem.ui.panels.venda.VendaPanel;
import com.ofisystem.ui.style.Adesivo;
import com.ofisystem.ui.style.Botao;
import com.ofisystem.ui.style.Etiqueta;
import com.ofisystem.ui.style.GradeCelulas;
import com.ofisystem.ui.style.Icones;
import com.ofisystem.ui.style.Movimento;
import com.ofisystem.ui.style.Superficie;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;
import com.ofisystem.util.Formatador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InicioPanel extends AbstractPanel {

    private static final OrdemServicoController OS_CONTROLLER = new OrdemServicoController();
    private static final VendaController VENDA_CONTROLLER = new VendaController();
    private static final EstoqueController ESTOQUE_CONTROLLER = new EstoqueController();
    private static final int LIMITE_ADESIVOS = 8;
    private static final int LARGURA_ADESIVO = 264;

    private Texto lblResumo;
    private JPanel grade;
    private JPanel listaMovimento;
    private JPanel acoesTopo;
    private JPanel topo;
    private JPanel listaCaixa;
    private JPanel listaAtencao;

    @Override
    protected void inicializarTela() {
        add(criarTopo(), BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(0, MARGEM_LATERAL, 24, MARGEM_LATERAL));

        conteudo.add(alinhado(Texto.rotulo("Na oficina agora")));
        conteudo.add(Box.createVerticalStrut(8));
        grade = new JPanel(new GradeCelulas(LARGURA_ADESIVO, 16));
        grade.setOpaque(false);
        grade.setAlignmentX(Component.LEFT_ALIGNMENT);
        grade.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                grade.revalidate();
            }
        });
        conteudo.add(grade);
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(criarRodapeDia());

        add(comRolagemVertical(conteudo), BorderLayout.CENTER);

        mostrarCarregando();
        carregar();
    }

    private JComponent alinhado(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private JPanel criarTopo() {
        LocalDate hoje = LocalDate.now();
        String dia = hoje.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"));
        dia = dia.replace("-feira", "");
        Texto titulo = Texto.titulo("Fila de " + dia + ", " + Formatador.data(hoje).substring(0, 5));
        lblResumo = Texto.apoio("Carregando a fila do dia...");

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(lblResumo);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        Botao venda = new Botao("Venda de balcão", Botao.Estilo.CONTORNO, Icones.Tipo.VENDA);
        venda.setToolTipText("Atalho: F3");
        venda.addActionListener(e -> Navegador.ir(new VendaPanel()));
        Botao novaOs = new Botao("Nova OS", Botao.Estilo.PRINCIPAL, Icones.Tipo.MAIS);
        novaOs.setToolTipText("Atalho: F2");
        novaOs.addActionListener(e -> Navegador.ir(new OrdemServicoPanel()));
        acoes.add(venda);
        acoes.add(Box.createHorizontalStrut(8));
        acoes.add(novaOs);

        topo = new JPanel(new BorderLayout(16, 16)) {
            @Override
            public void doLayout() {
                reorganizarTopo();
                super.doLayout();
            }
        };
        topo.setOpaque(false);
        topo.setBorder(BorderFactory.createEmptyBorder(MARGEM_TOPO_TITULO, MARGEM_LATERAL, 24, MARGEM_LATERAL + 3));
        topo.add(textos, BorderLayout.CENTER);
        acoesTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoesTopo.setOpaque(false);
        acoesTopo.add(acoes);
        topo.add(acoesTopo, BorderLayout.EAST);
        topo.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reorganizarTopo();
            }
        });
        return topo;
    }

    private void reorganizarTopo() {
        if (topo == null || acoesTopo == null || topo.getWidth() <= 0) return;
        String posicao = topo.getWidth() < 1000 ? BorderLayout.SOUTH : BorderLayout.EAST;
        BorderLayout layout = (BorderLayout) topo.getLayout();
        if (layout.getLayoutComponent(posicao) == acoesTopo) return;
        topo.remove(acoesTopo);
        topo.add(acoesTopo, posicao);
        topo.revalidate();
    }

    private JPanel criarRodapeDia() {
        listaMovimento = new JPanel();
        listaMovimento.setOpaque(false);
        listaMovimento.setLayout(new BoxLayout(listaMovimento, BoxLayout.Y_AXIS));
        listaMovimento.add(alinhado(Texto.apoio("Carregando...")));

        Superficie movimento = new Superficie(new BorderLayout(0, 12), 16, 16);
        movimento.add(Texto.rotulo("Movimento de hoje"), BorderLayout.NORTH);
        movimento.add(listaMovimento, BorderLayout.CENTER);

        listaCaixa = new JPanel();
        listaCaixa.setOpaque(false);
        listaCaixa.setLayout(new BoxLayout(listaCaixa, BoxLayout.Y_AXIS));
        Superficie caixa = new Superficie(new BorderLayout(0, 12), 16, 16);
        caixa.add(Texto.rotulo("Caixa de hoje"), BorderLayout.NORTH);
        caixa.add(listaCaixa, BorderLayout.CENTER);

        listaAtencao = new JPanel();
        listaAtencao.setOpaque(false);
        listaAtencao.setLayout(new BoxLayout(listaAtencao, BoxLayout.Y_AXIS));
        Superficie atencao = new Superficie(new BorderLayout(0, 12), 16, 16);
        atencao.add(Texto.rotulo("Precisa de atenção"), BorderLayout.NORTH);
        atencao.add(listaAtencao, BorderLayout.CENTER);

        JPanel linha = new JPanel(new GradeCelulas(272, 16));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.add(movimento);
        linha.add(caixa);
        linha.add(atencao);
        linha.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                linha.revalidate();
            }
        });
        return linha;
    }

    private void mostrarCarregando() {
        grade.removeAll();
        for (int i = 0; i < 4; i++) {
            Adesivo vazio = new Adesivo("", "Carregando", Tema.Tom.INATIVO, new BorderLayout());
            vazio.setPreferredSize(new Dimension(LARGURA_ADESIVO, 168));
            grade.add(vazio);
        }
        grade.revalidate();
    }

    private void carregar() {
        LocalDate hoje = LocalDate.now();
        executarAsync(() -> new Dados(
                OS_CONTROLLER.filtrar(null, null, null, null, true, null, null),
                OS_CONTROLLER.concluidasNoPeriodo(hoje, hoje),
                VENDA_CONTROLLER.filtrar(null, null, StatusVenda.FINALIZADA, hoje, hoje),
                ESTOQUE_CONTROLLER.filtrar(null, null, true, true).size()), this::preencher);
    }

    private void preencher(Dados dados) {
        List<OrdemServico> abertas = new ArrayList<>(dados.abertas);
        abertas.sort(Comparator.comparingInt(InicioPanel::prioridade)
                .thenComparing(os -> os.getDataPrevisao() == null ? LocalDate.MAX : os.getDataPrevisao()));

        long atrasadas = abertas.stream().filter(os -> OrdemServicoConsultaPanel.tomDa(os) == Tema.Tom.CRITICO).count();
        long aguardando = abertas.stream().filter(os -> os.getStatus() == StatusOS.AGUARDANDO_PECA).count();

        lblResumo.setText(abertas.isEmpty() ? "Nenhuma OS em aberto."
                : abertas.size() + " OS em aberto"
                + (atrasadas > 0 ? " · " + atrasadas + (atrasadas == 1 ? " atrasada" : " atrasadas") : "")
                + (aguardando > 0 ? " · " + aguardando + " aguardando peça" : ""));

        preencherGrade(abertas);
        preencherMovimento(dados);
        preencherCaixa(dados);
        preencherAtencao(atrasadas, aguardando, dados.estoqueBaixo);
        revalidate();
        repaint();
    }

    private static int prioridade(OrdemServico os) {
        return switch (OrdemServicoConsultaPanel.tomDa(os)) {
            case CRITICO -> 0;
            case ATENCAO -> 1;
            default -> os.getStatus() == StatusOS.EM_ANDAMENTO ? 2 : 3;
        };
    }

    private void preencherGrade(List<OrdemServico> abertas) {
        grade.removeAll();
        if (abertas.isEmpty()) {
            Superficie vazio = new Superficie(new BorderLayout(0, 8), 24, 24);
            vazio.add(Texto.corpo("Nenhuma OS em aberto agora."), BorderLayout.NORTH);
            vazio.add(Texto.apoio("<html>Quando um cliente chegar, abra a OS em Nova OS (F2). Ela fica aqui até ser concluída.</html>"),
                    BorderLayout.CENTER);
            vazio.setPreferredSize(new Dimension(LARGURA_ADESIVO, 128));
            grade.setLayout(new BorderLayout());
            grade.add(vazio, BorderLayout.CENTER);
            grade.revalidate();
            return;
        }
        grade.setLayout(new GradeCelulas(LARGURA_ADESIVO, 16));

        List<Adesivo> adesivos = new ArrayList<>();
        for (OrdemServico os : abertas.subList(0, Math.min(LIMITE_ADESIVOS, abertas.size()))) {
            Adesivo adesivo = criarAdesivo(os);
            adesivos.add(adesivo);
            grade.add(adesivo);
        }
        for (Adesivo a : adesivos) {
            a.setAoMudarFoco(focado -> destacar(adesivos, focado ? a : null));
        }
        if (abertas.size() > LIMITE_ADESIVOS) {
            Botao todas = new Botao("Ver todas as " + abertas.size() + " OS em aberto", Botao.Estilo.CONTORNO);
            todas.addActionListener(e -> Navegador.ir(new OrdemServicoConsultaPanel()));
            JPanel celula = new JPanel(new GridBagLayout());
            celula.setOpaque(false);
            celula.add(todas);
            celula.setPreferredSize(new Dimension(LARGURA_ADESIVO, 168));
            grade.add(celula);
        }
        grade.revalidate();
    }

    private void destacar(List<Adesivo> adesivos, Adesivo foco) {
        for (Adesivo a : adesivos) {
            float inicio = a.getApagado();
            float alvo = foco != null && a != foco ? 1f : 0f;
            if (inicio == alvo) continue;
            Movimento.animar(160, Movimento.SAIDA_SUAVE, t -> a.setApagado(inicio + (alvo - inicio) * t), null);
        }
    }

    private Adesivo criarAdesivo(OrdemServico os) {
        Tema.Tom tom = OrdemServicoConsultaPanel.tomDa(os);
        String estado = tom == Tema.Tom.CRITICO ? "Atrasada" : os.getStatus().getDescricao();
        Adesivo adesivo = new Adesivo("OS " + os.getNumero(), estado, tom, new GridBagLayout());
        adesivo.setPreferredSize(new Dimension(LARGURA_ADESIVO, 168));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridwidth = 2;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        g.gridy = 0;
        adesivo.add(Texto.rotulo(tom == Tema.Tom.CRITICO ? "Prometida para" : "Entrega prevista"), g);
        g.gridy = 1;
        adesivo.add(new Texto(textoPrevisao(os), Tema.TAM_TITULO, Font.BOLD,
                () -> tom == Tema.Tom.CRITICO ? Tema.critico() : Tema.tinta()), g);
        g.gridy = 2;
        g.insets = new Insets(8, 0, 0, 0);
        adesivo.add(new Texto(os.getCliente().getNome(), Tema.TAM_TEXTO, Font.BOLD, Tema::tinta), g);

        g.gridy = 3;
        g.gridwidth = 1;
        g.insets = new Insets(0, 0, 0, 8);
        adesivo.add(Texto.apoio(os.isPossuiVeiculo() ? os.getVeiculoDescricao() : "Sem veículo"), g);
        g.gridx = 1;
        g.weightx = 0;
        g.anchor = GridBagConstraints.EAST;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(0, 0, 0, 0);
        adesivo.add(new Texto(Formatador.moeda(os.getValorTotal()), Tema.TAM_TEXTO, Font.BOLD, Tema::tinta), g);

        adesivo.setToolTipText("Abrir OS " + os.getNumero());
        adesivo.setAoAbrir(() -> Navegador.ir(OrdemServicoPanel.editar(os.getId())));
        return adesivo;
    }

    private String textoPrevisao(OrdemServico os) {
        LocalDate previsao = os.getDataPrevisao();
        if (previsao == null) return "Sem data";
        LocalDate hoje = LocalDate.now();
        if (previsao.equals(hoje)) return "Hoje";
        if (previsao.equals(hoje.plusDays(1))) return "Amanhã";
        if (previsao.equals(hoje.minusDays(1))) return "Ontem";
        return Formatador.data(previsao).substring(0, 5);
    }

    private void preencherMovimento(Dados dados) {
        record Linha(LocalDateTime quando, String documento, String cliente, double valor) {
        }
        List<Linha> linhas = new ArrayList<>();
        for (Venda v : dados.vendasHoje) {
            linhas.add(new Linha(v.getData(), "Venda " + v.getNumero(),
                    v.getCliente() == null ? "Consumidor final" : v.getCliente().getNome(), v.getValorTotal()));
        }
        for (OrdemServico os : dados.concluidasHoje) {
            linhas.add(new Linha(os.getDataConclusao(), "OS " + os.getNumero(), os.getCliente().getNome(), os.getValorTotal()));
        }
        linhas.sort(Comparator.comparing(Linha::quando).reversed());
        listaMovimento.removeAll();
        if (linhas.isEmpty()) {
            listaMovimento.add(alinhado(Texto.apoio("Nenhuma venda ou OS concluída hoje.")));
        }
        for (Linha l : linhas.subList(0, Math.min(4, linhas.size()))) {
            JPanel item = new JPanel(new BorderLayout(8, 0));
            item.setOpaque(false);
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            JPanel textos = new JPanel(new GridLayout(2, 1));
            textos.setOpaque(false);
            textos.add(new Texto(l.documento(), Tema.TAM_TEXTO, Font.BOLD, Tema::tinta));
            textos.add(new Texto(Formatador.dataHora(l.quando()).substring(11) + " · " + l.cliente(),
                    Tema.TAM_PEQUENO, Font.PLAIN, Tema::tintaSuave));
            item.add(textos, BorderLayout.CENTER);
            item.add(Texto.corpo(Formatador.moeda(l.valor())), BorderLayout.EAST);
            listaMovimento.add(alinhado(item));
            listaMovimento.add(Box.createVerticalStrut(4));
        }
        if (linhas.size() > 4) {
            listaMovimento.add(alinhado(Texto.apoio("e mais " + (linhas.size() - 4) + " hoje")));
        }
        listaMovimento.revalidate();
    }

    private void preencherCaixa(Dados dados) {
        Map<FormaPagamento, Double> porForma = new EnumMap<>(FormaPagamento.class);
        for (Venda v : dados.vendasHoje) {
            for (FormaPagamento f : FormaPagamento.values()) {
                double valor = VendaController.valorRecebido(v, f);
                if (valor > 0.004) porForma.merge(f, valor, Double::sum);
            }
        }
        for (OrdemServico os : dados.concluidasHoje) {
            if (os.getFormaPagamento() != null) porForma.merge(os.getFormaPagamento(), os.getValorTotal(), Double::sum);
        }
        double total = porForma.values().stream().mapToDouble(Double::doubleValue).sum();

        listaCaixa.removeAll();
        Texto valorTotal = new Texto(Formatador.moeda(0.0), Tema.TAM_TITULO, Font.BOLD, Tema::aco);
        listaCaixa.add(alinhado(valorTotal));
        listaCaixa.add(Box.createVerticalStrut(8));
        if (porForma.isEmpty()) {
            listaCaixa.add(alinhado(Texto.apoio("Nada recebido hoje.")));
        }
        porForma.forEach((forma, valor) -> listaCaixa.add(alinhado(linhaValor(forma.getDescricao(), Formatador.moeda(valor)))));
        listaCaixa.revalidate();

        Movimento.animar(700, Movimento.SAIDA_SUAVE, t -> valorTotal.setText(Formatador.moeda(total * t)), null);
    }

    private JPanel linhaValor(String rotulo, String valor) {
        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        linha.add(Texto.apoio(rotulo), BorderLayout.WEST);
        linha.add(Texto.corpo(valor), BorderLayout.EAST);
        return linha;
    }

    private void preencherAtencao(long atrasadas, long aguardando, int estoqueBaixo) {
        listaAtencao.removeAll();
        if (atrasadas > 0) {
            listaAtencao.add(alinhado(itemAtencao(atrasadas + (atrasadas == 1 ? " OS atrasada" : " OS atrasadas"),
                    Tema.Tom.CRITICO, () -> Navegador.ir(new OrdemServicoConsultaPanel()))));
        }
        if (aguardando > 0) {
            listaAtencao.add(alinhado(itemAtencao(aguardando + " aguardando peça", Tema.Tom.ATENCAO,
                    () -> Navegador.ir(new OrdemServicoConsultaPanel()))));
        }
        if (estoqueBaixo > 0) {
            listaAtencao.add(alinhado(itemAtencao(estoqueBaixo + (estoqueBaixo == 1 ? " peça abaixo do mínimo" : " peças abaixo do mínimo"),
                    Tema.Tom.CRITICO, () -> Navegador.ir(new EstoqueConsultaPanel()))));
        }
        if (listaAtencao.getComponentCount() == 0) {
            listaAtencao.add(alinhado(new Etiqueta("Tudo em dia", Tema.Tom.SUCESSO)));
        }
        listaAtencao.revalidate();
    }

    private JComponent itemAtencao(String texto, Tema.Tom tom, Runnable abrir) {
        Etiqueta etiqueta = new Etiqueta(texto, tom);
        etiqueta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        etiqueta.setToolTipText("Abrir lista");
        etiqueta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrir.run();
            }
        });
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        linha.add(etiqueta);
        return linha;
    }

    private record Dados(List<OrdemServico> abertas, List<OrdemServico> concluidasHoje, List<Venda> vendasHoje,
                         int estoqueBaixo) {
    }
}
