package com.ofisystem;

import Enums.FormaPagamento;
import com.formdev.flatlaf.FlatLightLaf;
import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.controle.usuario.UsuarioController;
import com.ofisystem.controle.venda.VendaController;
import com.ofisystem.dao.EstoqueDAO;
import com.ofisystem.entidade.*;
import com.ofisystem.ui.panels.os.OrdemServicoConsultaPanel;
import com.ofisystem.ui.panels.relatorio.RelatorioFinanceiroPanel;
import com.ofisystem.util.JPAutil;
import com.ofisystem.util.Sessao;
import jakarta.persistence.EntityManager;

import javax.swing.*;

/** Executar pela IDE com o classpath de testes. Usa apenas H2 em memoria. */
public final class VerificacaoVisual {

    public static void main(String[] args) {
        System.setProperty("jakarta.persistence.jdbc.url", "jdbc:h2:mem:ofisystem_visual;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        System.setProperty("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        System.setProperty("jakarta.persistence.jdbc.user", "sa");
        System.setProperty("jakarta.persistence.jdbc.password", "");
        System.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        System.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        System.setProperty("hibernate.show_sql", "false");
        preparar();
        FlatLightLaf.setup();
        Runtime.getRuntime().addShutdownHook(new Thread(JPAutil::close));
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("OfiSystem - verificacao isolada (H2)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JTabbedPane abas = new JTabbedPane();
            abas.addTab("Ordens de servico", new OrdemServicoConsultaPanel());
            abas.addTab("Financeiro", new RelatorioFinanceiroPanel());
            frame.setContentPane(abas);
            frame.setSize(1100, 740);
            frame.setMinimumSize(new java.awt.Dimension(900, 600));
            frame.setLocationRelativeTo(null);
            if (java.util.Arrays.asList(args).contains("--sempre-visivel")) frame.setAlwaysOnTop(true);
            frame.setVisible(true);
        });
    }

    private static void preparar() {
        Usuario admin = new Usuario();
        admin.setNome("admin-teste");
        admin.setEmail("admin@example.com");
        admin.setSenha("teste123");
        admin.setNomeOficina("Demonstracao isolada");
        new UsuarioController().cadastrar(admin);
        Sessao.iniciar(admin);
        EntityManager em = JPAutil.getEntityManager();
        Pessoa cliente = new Pessoa();
        Servico servico = new Servico();
        try {
            if (!em.getEntityManagerFactory().getProperties().get("jakarta.persistence.jdbc.url")
                    .toString().startsWith("jdbc:h2:mem:")) {
                throw new IllegalStateException("Esta verificacao exige H2 em memoria.");
            }
            em.getTransaction().begin();
            cliente.setNome("Cliente de teste visual");
            cliente.setDocumento("52998224725");
            em.persist(cliente);
            servico.setNome("Servico de teste visual");
            servico.setDescricao("Demonstracao de OS");
            servico.setDuracaoMin(30);
            servico.setValor(80.0);
            em.persist(servico);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        Peca peca = new Peca();
        peca.setNome("Peca de teste visual");
        peca.setValorUnitario(25.0);
        peca = new EstoqueDAO().salvarPeca(peca, 2, 10, Sessao.getNome()).getPeca();
        for (int quantidade : new int[]{2, 1}) {
            OrdemServico os = new OrdemServico();
            os.setCliente(cliente);
            os.setDescricao("OS visual com " + quantidade + " peca(s)");
            ItemOs trabalho = new ItemOs();
            trabalho.setServico(servico);
            trabalho.setDescricao(servico.getNome());
            trabalho.setQuantidade(1);
            trabalho.setValorUnitario(80.0);
            os.adicionarServico(trabalho);
            ItemPeca material = new ItemPeca();
            material.setPeca(peca);
            material.setQuantidade(quantidade);
            material.setValorUnitario(25.0);
            os.adicionarPeca(material);
            new OrdemServicoController().salvar(os);
        }
        Venda venda = new Venda();
        venda.setCliente(cliente);
        ItemVenda item = new ItemVenda();
        item.setPeca(peca);
        item.setDescricao(peca.getNome());
        item.setQuantidade(1);
        item.setValorUnitario(25.0);
        venda.adicionarItem(item);
        venda.adicionarPagamento(new PagamentoVenda(FormaPagamento.PIX, 25.0));
        new VendaController().finalizar(venda);
    }
}
