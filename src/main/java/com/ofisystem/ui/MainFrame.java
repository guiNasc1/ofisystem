package com.ofisystem.ui;

import com.formdev.flatlaf.util.Animator;
import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.ui.panels.estoque.EstoqueConsultaPanel;
import com.ofisystem.ui.panels.estoque.MovimentacaoEstoquePanel;
import com.ofisystem.ui.panels.estoque.PecaCadastroPanel;
import com.ofisystem.ui.panels.inicio.InicioPanel;
import com.ofisystem.ui.panels.os.OrdemServicoConsultaPanel;
import com.ofisystem.ui.panels.os.OrdemServicoPanel;
import com.ofisystem.ui.panels.pessoa.PessoaCadastroPanel;
import com.ofisystem.ui.panels.pessoa.PessoaConsultaPanel;
import com.ofisystem.ui.panels.relatorio.RelatorioEstoquePanel;
import com.ofisystem.ui.panels.relatorio.RelatorioFinanceiroPanel;
import com.ofisystem.ui.panels.relatorio.RelatorioOrdemServicoPanel;
import com.ofisystem.ui.panels.relatorio.RelatorioPessoasPanel;
import com.ofisystem.ui.panels.relatorio.RelatorioVendasPanel;
import com.ofisystem.ui.panels.servico.ServicoCadastroPanel;
import com.ofisystem.ui.panels.servico.ServicoConsultaPanel;
import com.ofisystem.ui.panels.venda.VendaConsultaPanel;
import com.ofisystem.ui.panels.venda.VendaPanel;
import com.ofisystem.ui.style.Botao;
import com.ofisystem.ui.style.EngrenagemForjada;
import com.ofisystem.ui.style.Icones;
import com.ofisystem.ui.style.ItemTrilho;
import com.ofisystem.ui.style.Movimento;
import com.ofisystem.ui.style.Superficie;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MainFrame {

    private record Destino(String secao, String item, Supplier<JPanel> criar, Class<?> tela) {
    }

    private static final String INICIO = "Início";

    private static final List<Destino> DESTINOS = List.of(
            new Destino("Ordens de Serviço", "Nova Ordem", OrdemServicoPanel::new, OrdemServicoPanel.class),
            new Destino("Ordens de Serviço", "Consultar", OrdemServicoConsultaPanel::new, OrdemServicoConsultaPanel.class),
            new Destino("Financeiro", "Nova Venda", VendaPanel::new, VendaPanel.class),
            new Destino("Financeiro", "Consultar Vendas", VendaConsultaPanel::new, VendaConsultaPanel.class),
            new Destino("Pessoas", "Cadastrar", PessoaCadastroPanel::new, PessoaCadastroPanel.class),
            new Destino("Pessoas", "Consultar", PessoaConsultaPanel::new, PessoaConsultaPanel.class),
            new Destino("Estoque", "Cadastrar Peça", PecaCadastroPanel::new, PecaCadastroPanel.class),
            new Destino("Estoque", "Consultar", EstoqueConsultaPanel::new, EstoqueConsultaPanel.class),
            new Destino("Estoque", "Movimentação", MovimentacaoEstoquePanel::new, MovimentacaoEstoquePanel.class),
            new Destino("Serviços", "Cadastrar", ServicoCadastroPanel::new, ServicoCadastroPanel.class),
            new Destino("Serviços", "Consultar", ServicoConsultaPanel::new, ServicoConsultaPanel.class),
            new Destino("Relatórios", "Ordens de Serviço", RelatorioOrdemServicoPanel::new, RelatorioOrdemServicoPanel.class),
            new Destino("Relatórios", "Vendas", RelatorioVendasPanel::new, RelatorioVendasPanel.class),
            new Destino("Relatórios", "Pessoas", RelatorioPessoasPanel::new, RelatorioPessoasPanel.class),
            new Destino("Relatórios", "Estoque", RelatorioEstoquePanel::new, RelatorioEstoquePanel.class),
            new Destino("Relatórios", "Financeiro", RelatorioFinanceiroPanel::new, RelatorioFinanceiroPanel.class));

    private static final Map<String, Icones.Tipo> ICONES_SECAO = Map.of(
            "Ordens de Serviço", Icones.Tipo.OS,
            "Financeiro", Icones.Tipo.VENDA,
            "Pessoas", Icones.Tipo.PESSOAS,
            "Estoque", Icones.Tipo.ESTOQUE,
            "Serviços", Icones.Tipo.SERVICOS,
            "Relatórios", Icones.Tipo.RELATORIOS);

    private JFrame frame;
    private JPanel painelCentral;
    private JLabel lblOficina;
    private ItemTrilho itemTema;

    private final List<JPanel> todosOsMenus = new ArrayList<>();
    private final List<ItemTrilho> todosBotoesTitulo = new ArrayList<>();
    private final Map<Class<?>, ItemTrilho> itemPorTela = new HashMap<>();
    private final Map<ItemTrilho, Integer> secaoDoItem = new HashMap<>();
    private ItemTrilho itemInicio;
    private ItemTrilho itemAtivo;

    private BufferedImage telaAnterior;
    private float opacidadeTelaAnterior;
    private Animator animadorTela;

    public MainFrame() {
        frame = new JFrame();
        atualizarTitulo();
        frame.setSize(1280, 780);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(criarTrilho(), BorderLayout.WEST);

        painelCentral = new JPanel(new BorderLayout()) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (telaAnterior != null && opacidadeTelaAnterior > 0f) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidadeTelaAnterior));
                    g2.drawImage(telaAnterior, 0, 0, null);
                    g2.dispose();
                }
            }
        };
        frame.add(painelCentral, BorderLayout.CENTER);
        Navegador.registrar(this::navegarPara);
        registrarAtalhos();

        navegarPara(new InicioPanel());

        String oficina = Sessao.getUsuariologado().getNomeOficina();
        if (oficina == null || oficina.isEmpty()) {
            SwingUtilities.invokeLater(this::mostrarTelaBoasVindas);
        }

        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                for (Component c : frame.getLayeredPane().getComponents()) {
                    if (c instanceof BoasVindasPanel) {
                        c.setBounds(0, 0, frame.getWidth(), frame.getHeight());
                    }
                }
            }
        });
    }

    public void exibirFrame() {
        frame.setVisible(true);
    }

    private void atualizarTitulo() {
        String oficina = Sessao.getUsuariologado().getNomeOficina();
        frame.setTitle(oficina == null || oficina.isBlank() ? "OfiSystem" : "OfiSystem — " + oficina);
    }

    private void navegarPara(JPanel painel) {
        Movimento.parar(animadorTela);
        BufferedImage captura = painelCentral.getComponentCount() > 0 ? Movimento.capturar(painelCentral) : null;

        painelCentral.removeAll();
        painelCentral.add(painel, BorderLayout.CENTER);
        painelCentral.revalidate();
        marcarAtivo(painel.getClass());

        telaAnterior = captura;
        opacidadeTelaAnterior = captura != null ? 1f : 0f;
        painelCentral.repaint();
        if (captura == null) return;

        animadorTela = Movimento.animar(160, Movimento.SAIDA_SUAVE, t -> {
            opacidadeTelaAnterior = 1f - t;
            painelCentral.repaint();
        }, () -> {
            telaAnterior = null;
            painelCentral.repaint();
        });
    }

    private void marcarAtivo(Class<?> tela) {
        ItemTrilho novo = tela == InicioPanel.class ? itemInicio : itemPorTela.get(tela);
        if (novo == null || novo == itemAtivo) return;
        if (itemAtivo != null) itemAtivo.setSelecionado(false);
        itemAtivo = novo;
        itemAtivo.setSelecionado(true);
        Integer secao = secaoDoItem.get(novo);
        if (secao != null && !estaAberto(todosOsMenus.get(secao))) {
            abrirSecao(secao);
        }
    }

    private void registrarAtalhos() {
        JRootPane raiz = frame.getRootPane();
        InputMap entradas = raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap acoes = raiz.getActionMap();
        entradas.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "novaOs");
        entradas.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "novaVenda");
        entradas.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "inicio");
        acoes.put("novaOs", acao(() -> navegarPara(new OrdemServicoPanel())));
        acoes.put("novaVenda", acao(() -> navegarPara(new VendaPanel())));
        acoes.put("inicio", acao(() -> navegarPara(new InicioPanel())));
    }

    private Action acao(Runnable r) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                r.run();
            }
        };
    }

    private JPanel criarTrilho() {
        JPanel trilho = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Tema.trilhoTopo(), 0, getHeight(), Tema.trilhoBase()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        trilho.setPreferredSize(new Dimension(248, 0));

        trilho.add(criarMarca(), BorderLayout.NORTH);

        JPanel itens = transparente(new JPanel());
        itens.setLayout(new BoxLayout(itens, BoxLayout.Y_AXIS));
        itens.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        itemInicio = new ItemTrilho(INICIO, ItemTrilho.Tipo.PRINCIPAL, Icones.Tipo.INICIO);
        itemInicio.setToolTipText("F1");
        itemInicio.addActionListener(e -> navegarPara(new InicioPanel()));
        itens.add(itemInicio);
        itens.add(Box.createVerticalStrut(8));

        Map<String, List<Destino>> secoes = new LinkedHashMap<>();
        for (Destino d : DESTINOS) {
            secoes.computeIfAbsent(d.secao(), k -> new ArrayList<>()).add(d);
        }
        secoes.forEach((secao, destinos) -> itens.add(criarMenuExpansivel(secao, destinos)));

        JPanel topo = transparente(new JPanel(new BorderLayout()));
        topo.add(itens, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(topo,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        trilho.add(rolagem, BorderLayout.CENTER);

        trilho.add(criarRodapeTrilho(), BorderLayout.SOUTH);
        return trilho;
    }

    private JPanel criarMarca() {
        JPanel marca = transparente(new JPanel(new BorderLayout(12, 0)));
        marca.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 16));

        EngrenagemForjada engrenagem = new EngrenagemForjada();
        engrenagem.setSomenteMarca(true);
        engrenagem.setPreferredSize(new Dimension(40, 40));
        marca.add(engrenagem, BorderLayout.WEST);

        JPanel textos = transparente(new JPanel(new GridLayout(2, 1)));
        JLabel nome = new JLabel("OfiSystem");
        nome.setFont(Tema.fonte(Tema.TAM_TEXTO, Font.BOLD));
        nome.setForeground(Color.WHITE);
        lblOficina = new JLabel();
        lblOficina.setFont(Tema.fonte(Tema.TAM_PEQUENO));
        lblOficina.setForeground(Tema.trilhoSuave());
        lblOficina.setText(textoOficina());
        textos.add(nome);
        textos.add(lblOficina);
        marca.add(textos, BorderLayout.CENTER);
        return marca;
    }

    private String textoOficina() {
        String nome = Sessao.getUsuariologado().getNomeOficina();
        return nome != null && !nome.isEmpty() ? nome : "Oficina não definida";
    }

    private void atualizarOficina() {
        lblOficina.setText(textoOficina());
        atualizarTitulo();
    }

    private JPanel criarRodapeTrilho() {
        JPanel rodape = transparente(new JPanel());
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 28)),
                BorderFactory.createEmptyBorder(8, 0, 16, 0)));

        Usuario usuario = Sessao.getUsuariologado();
        JLabel lblUsuario = new JLabel(usuario.getNome(), Icones.de(Icones.Tipo.USUARIO, 18, Tema::trilhoTexto), SwingConstants.LEFT);
        lblUsuario.setFont(Tema.fonte(Tema.TAM_TEXTO, Font.BOLD));
        lblUsuario.setForeground(Tema.trilhoTexto());
        lblUsuario.setIconTextGap(10);
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 16));
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (usuario.getPerfil() != null) {
            lblUsuario.setToolTipText(usuario.getPerfil().getDescricao());
        }
        rodape.add(lblUsuario);

        itemTema = new ItemTrilho(textoTema(), ItemTrilho.Tipo.PRINCIPAL, Tema.isEscuro() ? Icones.Tipo.SOL : Icones.Tipo.LUA);
        itemTema.addActionListener(e -> alternarTema());
        rodape.add(itemTema);

        ItemTrilho trocar = new ItemTrilho("Trocar usuário", ItemTrilho.Tipo.PRINCIPAL, Icones.Tipo.TROCAR);
        trocar.addActionListener(e -> trocarUsuario());
        rodape.add(trocar);

        ItemTrilho sair = new ItemTrilho("Sair", ItemTrilho.Tipo.PRINCIPAL, Icones.Tipo.SAIR);
        sair.addActionListener(e -> sair());
        rodape.add(sair);
        return rodape;
    }

    private String textoTema() {
        return Tema.isEscuro() ? "Tema claro" : "Tema escuro";
    }

    private void alternarTema() {
        Tema.alternar(frame);
        JPanel rodape = (JPanel) itemTema.getParent();
        int indice = List.of(rodape.getComponents()).indexOf(itemTema);
        rodape.remove(itemTema);
        itemTema = new ItemTrilho(textoTema(), ItemTrilho.Tipo.PRINCIPAL, Tema.isEscuro() ? Icones.Tipo.SOL : Icones.Tipo.LUA);
        itemTema.addActionListener(e -> alternarTema());
        rodape.add(itemTema, indice);
        rodape.revalidate();
        rodape.repaint();
    }

    private JPanel criarMenuExpansivel(String titulo, List<Destino> destinos) {
        JPanel container = transparente(new JPanel());
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

        ItemTrilho btnTitulo = new ItemTrilho(titulo, ItemTrilho.Tipo.SECAO, ICONES_SECAO.get(titulo));

        JPanel painelSub = transparente(new JPanel());
        painelSub.setLayout(new BoxLayout(painelSub, BoxLayout.Y_AXIS));
        painelSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelSub.setVisible(false);
        aplicarAltura(painelSub, 0);

        int indiceSecao = todosOsMenus.size();
        todosOsMenus.add(painelSub);
        todosBotoesTitulo.add(btnTitulo);

        for (Destino destino : destinos) {
            ItemTrilho btnSub = new ItemTrilho(destino.item(), ItemTrilho.Tipo.SUBITEM, null);
            btnSub.addActionListener(e -> navegarPara(destino.criar().get()));
            itemPorTela.putIfAbsent(destino.tela(), btnSub);
            secaoDoItem.put(btnSub, indiceSecao);
            if (destino.tela() == OrdemServicoPanel.class) btnSub.setToolTipText("F2");
            if (destino.tela() == VendaPanel.class) btnSub.setToolTipText("F3");
            painelSub.add(btnSub);
        }

        btnTitulo.addActionListener(e -> {
            boolean estavaAberto = estaAberto(painelSub);
            fecharTodosMenus();
            if (!estavaAberto) {
                abrirSecao(indiceSecao);
            }
        });

        container.add(btnTitulo);
        container.add(painelSub);
        return container;
    }

    private void abrirSecao(int indice) {
        fecharTodosMenus();
        animarAltura(todosOsMenus.get(indice), true);
        todosBotoesTitulo.get(indice).setAberto(true);
    }

    private void fecharTodosMenus() {
        for (int i = 0; i < todosOsMenus.size(); i++) {
            if (estaAberto(todosOsMenus.get(i))) {
                animarAltura(todosOsMenus.get(i), false);
            }
            todosBotoesTitulo.get(i).setAberto(false);
        }
    }

    private boolean estaAberto(JPanel painelSub) {
        return Boolean.TRUE.equals(painelSub.getClientProperty("menu.aberto"));
    }

    private void animarAltura(JPanel painelSub, boolean abrir) {
        Movimento.parar((Animator) painelSub.getClientProperty(Animator.class));
        painelSub.putClientProperty("menu.aberto", abrir);

        int inicial = painelSub.isVisible() ? painelSub.getPreferredSize().height : 0;
        int alvo = 0;
        if (abrir) {
            for (Component c : painelSub.getComponents()) {
                alvo += c.getMaximumSize().height;
            }
            aplicarAltura(painelSub, inicial);
            painelSub.setVisible(true);
        }

        int destino = alvo;
        painelSub.putClientProperty(Animator.class, Movimento.animar(abrir ? 220 : 150, Movimento.SAIDA_SUAVE,
                t -> aplicarAltura(painelSub, Math.round(inicial + (destino - inicial) * t)),
                () -> {
                    if (!abrir) painelSub.setVisible(false);
                }));
    }

    private void aplicarAltura(JPanel painelSub, int altura) {
        painelSub.setPreferredSize(new Dimension(0, altura));
        painelSub.setMinimumSize(new Dimension(0, altura));
        painelSub.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));
        painelSub.revalidate();
        painelSub.repaint();
    }

    private <T extends JComponent> T transparente(T componente) {
        componente.setOpaque(false);
        return componente;
    }

    private void mostrarTelaBoasVindas() {
        BoasVindasPanel boasVindas = new BoasVindasPanel(() -> {
            JLayeredPane camadas = frame.getLayeredPane();
            BufferedImage captura = null;
            for (Component c : camadas.getComponents()) {
                if (c instanceof BoasVindasPanel) {
                    captura = Movimento.capturar(c);
                    camadas.remove(c);
                }
            }

            atualizarOficina();
            navegarPara(new InicioPanel());

            camadas.revalidate();
            camadas.repaint();
            frame.revalidate();
            frame.repaint();

            if (captura == null) return;
            Movimento.CamadaDesvanecer desvanecer = new Movimento.CamadaDesvanecer(captura);
            desvanecer.setBounds(0, 0, captura.getWidth(), captura.getHeight());
            camadas.add(desvanecer, JLayeredPane.POPUP_LAYER);
            Movimento.animar(450, Movimento.SAIDA_SUAVE, t -> desvanecer.setOpacidade(1f - t), () -> {
                camadas.remove(desvanecer);
                camadas.repaint();
            });
        });

        boasVindas.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.getLayeredPane().add(boasVindas, JLayeredPane.POPUP_LAYER);
        frame.getLayeredPane().revalidate();
        frame.getLayeredPane().repaint();
        boasVindas.focar();
    }

    private void sair() {
        int resposta = JOptionPane.showConfirmDialog(frame,
                "Deseja sair do sistema?", "Sair", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            Sessao.encerrar();
            frame.dispose();
            new LoginFrame().exibir();
        }
    }

    private void trocarUsuario() {
        JDialog dialog = new JDialog(frame, "Trocar usuário", true);
        dialog.setLayout(new BorderLayout());

        Superficie cartao = new Superficie(new GridBagLayout(), 24, 24);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(Texto.titulo("Trocar usuário"), gbc);
        gbc.insets = new Insets(0, 0, 16, 0);
        cartao.add(Texto.apoio("Entre com outro usuário sem fechar o sistema."), gbc);

        JTextField txtUsuario = new JTextField(22);
        txtUsuario.putClientProperty("JTextField.leadingIcon", Icones.de(Icones.Tipo.USUARIO, 18, Tema::tintaSuave));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Usuário");
        JPasswordField txtSenha = new JPasswordField(22);
        txtSenha.putClientProperty("JTextField.leadingIcon", Icones.de(Icones.Tipo.CADEADO, 18, Tema::tintaSuave));
        txtSenha.putClientProperty("JTextField.placeholderText", "Senha");
        txtSenha.putClientProperty("JPasswordField.showRevealButton", true);
        for (JTextField campo : new JTextField[]{txtUsuario, txtSenha}) {
            campo.setPreferredSize(new Dimension(320, 40));
        }
        gbc.insets = new Insets(0, 0, 8, 0);
        cartao.add(txtUsuario, gbc);
        cartao.add(txtSenha, gbc);

        JLabel erro = Texto.erro();
        gbc.insets = new Insets(0, 0, 8, 0);
        cartao.add(erro, gbc);

        JPanel botoes = transparente(new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)));
        Botao btCancelar = new Botao("Cancelar", Botao.Estilo.DISCRETO);
        Botao btEntrar = new Botao("Entrar", Botao.Estilo.PRINCIPAL);
        botoes.add(btCancelar);
        botoes.add(btEntrar);
        gbc.insets = new Insets(8, 0, 0, 0);
        cartao.add(botoes, gbc);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        fundo.add(cartao, BorderLayout.CENTER);
        dialog.add(fundo, BorderLayout.CENTER);
        dialog.getRootPane().setDefaultButton(btEntrar);

        btCancelar.addActionListener(e -> dialog.dispose());
        btEntrar.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            String senha = new String(txtSenha.getPassword());
            if (usuario.isEmpty() || senha.isEmpty()) {
                erro.setText("Preencha usuário e senha.");
                return;
            }
            btEntrar.setEnabled(false);
            btEntrar.setText("Entrando...");
            new SwingWorker<Usuario, Void>() {
                @Override
                protected Usuario doInBackground() {
                    return new UsuarioDAO().autenticar(usuario, senha);
                }

                @Override
                protected void done() {
                    btEntrar.setEnabled(true);
                    btEntrar.setText("Entrar");
                    try {
                        Usuario u = get();
                        if (u == null) {
                            erro.setText("Usuário ou senha incorretos.");
                            txtSenha.setText("");
                            txtSenha.requestFocusInWindow();
                            return;
                        }
                        Sessao.iniciar(u);
                        dialog.dispose();
                        frame.dispose();
                        SwingUtilities.invokeLater(() -> new MainFrame().exibirFrame());
                    } catch (Exception ex) {
                        erro.setText("Não foi possível conectar. Verifique a internet.");
                    }
                }
            }.execute();
        });

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
}
