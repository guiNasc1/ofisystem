package com.ofisystem.ui.panels.os;

import Enums.StatusOS;
import com.ofisystem.entidade.*;
import com.ofisystem.util.Sessao;
import net.sf.jasperreports.engine.*;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImpressaoOsTest {

    @Test
    void impressaoDaOsPreservaItensDescontoETotal() throws Exception {
        JasperPrint print = ImpressaoOS.ordemServico(ordem());
        String texto = texto(print);
        assertTrue(texto.contains("Ordem de Serviço Nº 000001"));
        assertTrue(texto.contains("Servico de demonstracao"));
        assertTrue(texto.contains("Desconto:"));
        assertTrue(texto.contains("10,00"));
        assertTrue(texto.contains("TOTAL:"));
        assertTrue(texto.contains("120,00"));
        salvarEvidencia(print, "ordem-servico");
    }

    private OrdemServico ordem() {
        Usuario usuario = new Usuario();
        usuario.setNome("Teste de impressao");
        usuario.setNomeOficina("Oficina de demonstracao");
        Sessao.iniciar(usuario);
        Pessoa cliente = new Pessoa();
        cliente.setNome("Cliente de demonstracao");
        cliente.setDocumento("52998224725");
        OrdemServico os = new OrdemServico();
        os.setNumero("000001");
        os.setCliente(cliente);
        os.setStatus(StatusOS.CONCLUIDA);
        os.setDescricao("Teste de impressao academica");
        ItemOs item = new ItemOs();
        item.setDescricao("Servico de demonstracao");
        item.setQuantidade(1);
        item.setValorUnitario(130.0);
        item.calcularSubtotal();
        os.adicionarServico(item);
        os.setDesconto(10.0);
        os.recalcularTotais();
        return os;
    }

    private String texto(JasperPrint print) {
        StringBuilder texto = new StringBuilder();
        for (JRPrintPage page : print.getPages()) {
            for (JRPrintElement element : page.getElements()) {
                if (element instanceof JRPrintText t) texto.append(t.getFullText()).append('\n');
            }
        }
        return texto.toString();
    }

    private void salvarEvidencia(JasperPrint print, String nome) throws Exception {
        assertFalse(print.getPages().isEmpty());
        Path pasta = Path.of("target", "evidencias-testes");
        Files.createDirectories(pasta);
        JasperExportManager.exportReportToPdfFile(print, pasta.resolve(nome + ".pdf").toString());
        BufferedImage imagem = (BufferedImage) JasperPrintManager.printPageToImage(print, 0, 1.5f);
        ImageIO.write(imagem, "png", pasta.resolve(nome + ".png").toFile());
        Sessao.encerrar();
    }
}
