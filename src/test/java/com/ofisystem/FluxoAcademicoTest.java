package com.ofisystem;

import Enums.FormaPagamento;
import Enums.Perfil;
import Enums.StatusOS;
import Enums.TipoMovimentacao;
import com.ofisystem.controle.os.OrdemServicoController;
import com.ofisystem.controle.usuario.UsuarioController;
import com.ofisystem.controle.venda.VendaController;
import com.ofisystem.dao.EstoqueDAO;
import com.ofisystem.dao.OrdemServicoDAO;
import com.ofisystem.dao.PessoaDAO;
import com.ofisystem.dao.RelatorioDAO;
import com.ofisystem.dao.ServicoDAO;
import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.*;
import com.ofisystem.util.JPAutil;
import com.ofisystem.util.RegraNegocioException;
import com.ofisystem.util.Sessao;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FluxoAcademicoTest {

    private final OrdemServicoController osController = new OrdemServicoController();
    private final EstoqueDAO estoqueDAO = new EstoqueDAO();
    private Pessoa cliente;
    private Servico servico;
    private Peca peca;

    @BeforeAll
    static void baseIsolada() {
        // Todos os acessos JPA deste processo usam exclusivamente H2 em memoria.
        System.setProperty("jakarta.persistence.jdbc.url", "jdbc:h2:mem:ofisystem_academico;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        System.setProperty("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        System.setProperty("jakarta.persistence.jdbc.user", "sa");
        System.setProperty("jakarta.persistence.jdbc.password", "");
        System.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        System.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        System.setProperty("hibernate.show_sql", "false");
    }

    @BeforeEach
    void preparar() {
        Usuario admin = new Usuario();
        admin.setNome("Administrador teste");
        admin.setPerfil(Perfil.ADMIN);
        Sessao.iniciar(admin);
        EntityManager em = JPAutil.getEntityManager();
        try {
            assertTrue(em.getEntityManagerFactory().getProperties().get("jakarta.persistence.jdbc.url")
                    .toString().startsWith("jdbc:h2:mem:"));
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Usuario").executeUpdate();
            em.getTransaction().commit();
            limparDadosDoUsuarioLogado();
            em.getTransaction().begin();
            cliente = new Pessoa();
            cliente.setNome("Cliente demonstracao");
            cliente.setDocumento("52998224725");
            em.persist(cliente);
            servico = new Servico();
            servico.setNome("Troca de oleo");
            servico.setDescricao("Servico de demonstracao");
            servico.setDuracaoMin(30);
            servico.setValor(80.0);
            em.persist(servico);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        Peca nova = new Peca();
        nova.setNome("Oleo demonstracao");
        nova.setValorUnitario(25.0);
        peca = estoqueDAO.salvarPeca(nova, 2, 10, Sessao.getNome()).getPeca();
    }

    /** Apaga os registros do usuário logado; cada conta só alcança os próprios dados. */
    private void limparDadosDoUsuarioLogado() {
        EntityManager em = JPAutil.getEntityManager();
        try {
            em.getTransaction().begin();
            for (String entidade : new String[]{"MovimentacaoEstoque", "ItemOs",
                    "ItemPeca", "PagamentoVenda", "ItemVenda", "Venda", "OrdemServico", "Estoque",
                    "Peca", "Servico", "Endereco", "Pessoa"}) {
                em.createQuery("DELETE FROM " + entidade).executeUpdate();
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    static void encerrar() {
        Sessao.encerrar();
        JPAutil.close();
    }

    @Test
    void cadastroDefinePerfisImpedeDuplicidadeEAutenticaSemDiferenciarMaiusculas() {
        UsuarioController controller = new UsuarioController();
        Usuario primeiro = usuario(" Admin ", " ADMIN@exemplo.com ");
        controller.cadastrar(primeiro);
        assertEquals(Perfil.ADMIN, primeiro.getPerfil());
        Usuario segundo = usuario("colega", "colega@exemplo.com");
        controller.cadastrar(segundo);
        assertEquals(Perfil.USUARIO, segundo.getPerfil());
        assertEquals(primeiro.getId(), new UsuarioDAO().autenticar(" ADMIN ", "senha123").getId());
        assertNull(new UsuarioDAO().autenticar("admin", "senhaErrada"));
        assertThrows(RegraNegocioException.class, () -> controller.cadastrar(usuario("ADMIN", "outro@exemplo.com")));
        assertThrows(RegraNegocioException.class, () -> controller.cadastrar(usuario("outro", "ADMIN@exemplo.com")));
        assertThrows(RegraNegocioException.class, () -> controller.cadastrar(usuario("outro", "invalido")));
        assertEquals("2", new UsuarioDAO().contarUsuario());
    }

    @Test
    void osAjustaEstoqueEConcluiSemGerarVenda() {
        OrdemServico os = osController.salvar(ordem(2));
        assertEquals(8, saldo());
        os.getItensPeca().get(0).setQuantidade(3);
        os = osController.salvar(os);
        assertEquals(7, saldo());
        assertEquals(155.0, os.getValorTotal(), 0.001);
        osController.concluir(os, FormaPagamento.PIX);
        assertEquals(7, saldo(), "Concluir nao deve baixar as pecas novamente");
        OrdemServico concluida = osController.buscarCompleta(os.getId());
        assertEquals(StatusOS.CONCLUIDA, concluida.getStatus());
        assertEquals(FormaPagamento.PIX, concluida.getFormaPagamento());
        assertNotNull(concluida.getDataConclusao());
        assertEquals(0, contar("Venda"), "A OS deve permanecer independente da venda de balcao");
        assertEquals(1, osController.concluidasNoPeriodo(LocalDate.now(), LocalDate.now()).size());
        assertThrows(RegraNegocioException.class, () -> osController.salvar(concluida));
        assertThrows(RegraNegocioException.class, () -> osController.cancelar(concluida));
        assertThrows(RegraNegocioException.class, () -> osController.concluir(concluida, FormaPagamento.PIX));
    }

    @Test
    void cadaUsuarioEnxergaSomenteOsProprioDados() {
        UsuarioController usuarios = new UsuarioController();
        Usuario ana = usuario("ana", "ana@exemplo.com");
        Usuario bia = usuario("bia", "bia@exemplo.com");
        usuarios.cadastrar(ana);
        usuarios.cadastrar(bia);

        Sessao.iniciar(ana);
        assertEquals(0, contar("Pessoa"), "A conta nova nao herda dados de outras contas");
        Pessoa clienteAna = new PessoaDAO().salvar(pessoa("Cliente da Ana"));
        Servico servicoAna = new ServicoDAO().salvar(servico("Alinhamento"));
        Peca pecaAna = estoqueDAO.salvarPeca(peca("Filtro da Ana"), 1, 5, Sessao.getNome()).getPeca();
        OrdemServico osAna = osController.salvar(ordem(clienteAna, servicoAna, pecaAna, 2));
        assertEquals("000001", osAna.getNumero());

        Sessao.iniciar(bia);
        assertEquals(0, contar("Pessoa"));
        assertEquals(0, contar("Servico"));
        assertEquals(0, contar("Peca"));
        assertEquals(0, contar("Estoque"));
        assertEquals(0, contar("OrdemServico"));
        assertEquals(0, contar("MovimentacaoEstoque"));
        assertTrue(new PessoaDAO().filtrar(null, null, null, null, null, null).isEmpty());
        assertTrue(osController.filtrar(null, null, null, null, false, null, null).isEmpty());
        assertNull(new PessoaDAO().buscarPorId(clienteAna.getId()));
        assertNull(osController.buscarCompleta(osAna.getId()));
        assertNull(estoqueDAO.buscarPorPeca(pecaAna.getId()));
        assertThrows(RegraNegocioException.class, () -> osController.cancelar(osAna));
        assertThrows(RegraNegocioException.class, () -> osController.concluir(osAna, FormaPagamento.PIX));
        assertThrows(RegraNegocioException.class, () -> osController.salvar(osAna));
        assertThrows(RegraNegocioException.class, () -> estoqueDAO.movimentar(pecaAna.getId(),
                TipoMovimentacao.SAIDA, 1, "Tentativa", Sessao.getNome()));
        assertTrue(new RelatorioDAO().vendasPorCliente(LocalDate.now(), LocalDate.now(), true, true).isEmpty());

        // Documento e numeracao sao independentes entre as contas.
        Pessoa clienteBia = new PessoaDAO().salvar(pessoa("Cliente da Bia"));
        OrdemServico osBia = osController.salvar(ordem(clienteBia, new ServicoDAO().salvar(servico("Alinhamento")),
                estoqueDAO.salvarPeca(peca("Filtro da Bia"), 1, 5, Sessao.getNome()).getPeca(), 1));
        assertEquals("000001", osBia.getNumero());
        assertEquals(1, contar("OrdemServico"));

        Sessao.iniciar(ana);
        assertEquals(1, contar("OrdemServico"));
        assertEquals(osAna.getId(), osController.filtrar(null, null, null, null, false, null, null).get(0).getId());
        assertEquals(3, estoqueDAO.buscarPorPeca(pecaAna.getId()).getQuantidade());
        limparDadosDoUsuarioLogado();
        Sessao.iniciar(bia);
        limparDadosDoUsuarioLogado();
    }

    private Pessoa pessoa(String nome) {
        Pessoa p = new Pessoa();
        p.setNome(nome);
        p.setDocumento("52998224725");
        return p;
    }

    private Servico servico(String nome) {
        Servico s = new Servico();
        s.setNome(nome);
        s.setDescricao("Servico de demonstracao");
        s.setDuracaoMin(30);
        s.setValor(80.0);
        return s;
    }

    private Peca peca(String nome) {
        Peca p = new Peca();
        p.setNome(nome);
        p.setValorUnitario(25.0);
        return p;
    }

    @Test
    void cancelamentoDevolvePecasUmaVezEExcluiOsDosRecebimentos() {
        OrdemServico os = osController.salvar(ordem(2));
        assertEquals(8, saldo());
        osController.cancelar(os);
        assertEquals(10, saldo());
        assertThrows(RegraNegocioException.class, () -> osController.cancelar(os));
        assertEquals(10, saldo());
        assertTrue(osController.concluidasNoPeriodo(LocalDate.now(), LocalDate.now()).isEmpty());
    }

    @Test
    void estoqueInsuficienteDesfazTodaAGravacaoDaOs() {
        assertThrows(RegraNegocioException.class, () -> osController.salvar(ordem(11)));
        assertEquals(10, saldo());
        assertEquals(0, contar("OrdemServico"));
        assertEquals(0, contar("ItemOs"));
        assertEquals(0, contar("ItemPeca"));
        assertEquals(1, contar("MovimentacaoEstoque"), "Somente a entrada inicial deve existir");
    }

    @Test
    void osNaoAceitaDescontoIntegralMasPermiteRascunhoSemItens() {
        OrdemServico os = ordem(2);
        os.setDesconto(130.0);
        assertThrows(RegraNegocioException.class, () -> osController.salvar(os));
        assertEquals(10, saldo());
        os.setDesconto(129.0);
        assertEquals(1.0, osController.salvar(os).getValorTotal(), 0.001);
        OrdemServico vazia = new OrdemServico();
        vazia.setCliente(cliente);
        vazia.setDescricao("Rascunho para avaliacao");
        OrdemServico salva = osController.salvar(vazia);
        assertThrows(RegraNegocioException.class, () -> osController.concluir(salva, FormaPagamento.PIX));
        assertThrows(RegraNegocioException.class, () -> new OrdemServicoDAO().concluir(salva.getId(), FormaPagamento.PIX));
        assertThrows(RegraNegocioException.class, () -> new OrdemServicoDAO().alterarStatus(salva.getId(), StatusOS.CONCLUIDA));
    }

    @Test
    void vendaDeBalcaoTemSeuProprioPagamentoEBaixaDeEstoque() {
        Venda venda = new Venda();
        venda.setCliente(cliente);
        ItemVenda item = new ItemVenda();
        item.setPeca(peca);
        item.setDescricao(peca.getNome());
        item.setQuantidade(2);
        item.setValorUnitario(25.0);
        venda.adicionarItem(item);
        venda.adicionarPagamento(new PagamentoVenda(FormaPagamento.PIX, 50.0));
        Venda salva = new VendaController().finalizar(venda);
        assertEquals(8, saldo());
        assertEquals(0, contar("OrdemServico"));
        assertEquals(50.0, VendaController.valorRecebido(salva, FormaPagamento.PIX), 0.001);
        new VendaController().cancelar(salva);
        assertEquals(10, saldo());
    }

    @Test
    void cadastroRespeitaPerfilEscolhidoExcetoNaPrimeiraConta() {
        UsuarioController controller = new UsuarioController();
        Usuario primeiro = usuario("primeira", "primeira@exemplo.com");
        primeiro.setPerfil(Perfil.USUARIO);
        controller.cadastrar(primeiro);
        assertEquals(Perfil.ADMIN, primeiro.getPerfil(), "A primeira conta da base e sempre ADMIN");
        Usuario admin = usuario("chefe", "chefe@exemplo.com");
        admin.setPerfil(Perfil.ADMIN);
        controller.cadastrar(admin);
        assertEquals(Perfil.ADMIN, new UsuarioDAO().buscarPorNome("chefe").getPerfil());
        Usuario comum = usuario("colega", "colega@exemplo.com");
        comum.setPerfil(Perfil.USUARIO);
        controller.cadastrar(comum);
        assertEquals(Perfil.USUARIO, new UsuarioDAO().buscarPorNome("colega").getPerfil());
    }

    @Test
    void cadastroRejeitadoPodeSerCorrigidoSemPerderSenhaOuCriarContaParcial() {
        UsuarioController controller = new UsuarioController();
        controller.cadastrar(usuario("admin", "admin@example.com"));
        Usuario repetido = usuario("colega", " ADMIN@example.com ");
        assertThrows(RegraNegocioException.class, () -> controller.cadastrar(repetido));
        assertNull(repetido.getId());
        assertEquals("senha123", repetido.getSenha());
        repetido.setEmail("colega@example.com");
        controller.cadastrar(repetido);
        assertEquals(Perfil.USUARIO, repetido.getPerfil());
        assertNotEquals("senha123", repetido.getSenha());
        assertNotNull(new UsuarioDAO().autenticar("colega", "senha123"));
        assertEquals("2", new UsuarioDAO().contarUsuario());
    }

    @Test
    void edicaoReduzERemovePecasDevolvendoSomenteADiferenca() {
        OrdemServico os = osController.salvar(ordem(3));
        assertEquals(7, saldo());
        os.getItensPeca().get(0).setQuantidade(1);
        os = osController.salvar(os);
        assertEquals(9, saldo());
        assertEquals(105.0, os.getValorTotal(), 0.001);
        os.getItensPeca().clear();
        os = osController.salvar(os);
        assertEquals(10, saldo());
        assertEquals(80.0, os.getValorTotal(), 0.001);
        assertTrue(osController.buscarCompleta(os.getId()).getItensPeca().isEmpty());
        osController.cancelar(os);
        assertEquals(10, saldo());
    }

    @Test
    void conclusaoValidaPagamentoERecalculaTotaisPersistidos() {
        OrdemServico os = osController.salvar(ordem(2));
        os.setDesconto(10.0);
        os = osController.salvar(os);
        Integer id = os.getId();
        assertThrows(RegraNegocioException.class, () -> new OrdemServicoDAO().concluir(id, null));
        OrdemServico aberta = osController.buscarCompleta(id);
        assertEquals(StatusOS.ABERTA, aberta.getStatus());
        assertNull(aberta.getDataConclusao());
        assertNull(aberta.getFormaPagamento());
        // Simula um total legado inconsistente; a conclusao deve usar os itens persistidos.
        EntityManager em = JPAutil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("UPDATE OrdemServico o SET o.valorTotal = 999.0 WHERE o.id = :id")
                    .setParameter("id", id).executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        new OrdemServicoDAO().concluir(id, FormaPagamento.DINHEIRO);
        OrdemServico concluida = osController.buscarCompleta(id);
        assertEquals(120.0, concluida.getValorTotal(), 0.001);
        assertEquals(8, saldo());
    }

    @Test
    void statusIntermediariosNaoEncerramOsENemGeramRecebimento() {
        OrdemServico os = osController.salvar(ordem(1));
        for (StatusOS status : StatusOS.values()) {
            if (status.isEditavel()) {
                osController.alterarStatus(os, status);
                assertEquals(status, osController.buscarCompleta(os.getId()).getStatus());
                assertEquals(9, saldo());
                assertTrue(osController.concluidasNoPeriodo(LocalDate.now(), LocalDate.now()).isEmpty());
            }
        }
        assertThrows(RegraNegocioException.class, () -> new OrdemServicoDAO().alterarStatus(os.getId(), null));
        osController.cancelar(os);
        OrdemServico cancelada = osController.buscarCompleta(os.getId());
        assertThrows(RegraNegocioException.class, () -> osController.salvar(cancelada));
        assertThrows(RegraNegocioException.class, () -> osController.concluir(cancelada, FormaPagamento.PIX));
    }

    @Test
    void relatoriosCombinamOsEBalcaoSemDuplicarEExcluemCancelados() {
        OrdemServico os = osController.salvar(ordem(3));
        osController.concluir(os, FormaPagamento.PIX);
        OrdemServico cancelada = osController.salvar(ordem(1));
        osController.cancelar(cancelada);
        osController.salvar(ordem(1)); // Aberta: nao e recebimento.
        Venda venda = new Venda();
        venda.setCliente(cliente);
        ItemVenda item = new ItemVenda();
        item.setPeca(peca);
        item.setDescricao(peca.getNome());
        item.setQuantidade(1);
        item.setValorUnitario(25.0);
        venda.adicionarItem(item);
        venda.adicionarPagamento(new PagamentoVenda(FormaPagamento.PIX, 25.0));
        VendaController vendas = new VendaController();
        Venda salva = vendas.finalizar(venda);
        RelatorioDAO relatorios = new RelatorioDAO();
        LocalDate hoje = LocalDate.now();
        var clientes = relatorios.vendasPorCliente(hoje, hoje, true, true);
        assertEquals(1, clientes.size());
        assertEquals(2, ((Number) clientes.get(0)[1]).intValue());
        assertEquals(180.0, ((Number) clientes.get(0)[2]).doubleValue(), 0.001);
        assertEquals(155.0, ((Number) relatorios.vendasPorCliente(hoje, hoje, true, false).get(0)[2]).doubleValue(), 0.001);
        assertEquals(25.0, ((Number) relatorios.vendasPorCliente(hoje, hoje, false, true).get(0)[2]).doubleValue(), 0.001);
        assertTrue(relatorios.vendasPorCliente(hoje.minusDays(2), hoje.minusDays(1), true, true).isEmpty());
        var produtos = relatorios.vendasPorProduto(hoje, hoje, true, true);
        assertEquals(1, produtos.size());
        assertEquals(4, ((Number) produtos.get(0)[2]).intValue());
        assertEquals(100.0, ((Number) produtos.get(0)[3]).doubleValue(), 0.001);
        var servicos = relatorios.vendasPorServico(hoje, hoje, true, true);
        assertEquals(1, servicos.size());
        assertEquals(80.0, ((Number) servicos.get(0)[2]).doubleValue(), 0.001);
        vendas.cancelar(salva);
        assertEquals(155.0, ((Number) relatorios.vendasPorCliente(hoje, hoje, true, true).get(0)[2]).doubleValue(), 0.001);
    }

    private Usuario usuario(String nome, String email) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha("senha123");
        return usuario;
    }

    private OrdemServico ordem(int quantidade) {
        return ordem(cliente, servico, peca, quantidade);
    }

    private OrdemServico ordem(Pessoa cliente, Servico servico, Peca peca, int quantidade) {
        OrdemServico os = new OrdemServico();
        os.setCliente(cliente);
        os.setDescricao("Manutencao de demonstracao");
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
        return os;
    }

    private int saldo() {
        return estoqueDAO.buscarPorPeca(peca.getId()).getQuantidade();
    }

    private long contar(String entidade) {
        EntityManager em = JPAutil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(e) FROM " + entidade + " e", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
