package com.ofisystem.util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.type.TextAdjustEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RelatorioJasper {

    private static final int MARGEM = 28;
    private static final int ALTURA_LINHA = 16;
    private static final Color COR_CABECALHO = new Color(53, 69, 99);

    static {
        DefaultJasperReportsContext.getInstance()
                .setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
    }

    private RelatorioJasper() {
    }

    public static void exibir(JasperPrint print, String titulo) {
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setTitle(titulo);
        viewer.setVisible(true);
    }

    public static JasperPrint gerar(String titulo, List<String> cabecalho, String[] colunas,
                                    List<Object[]> linhas, List<String> resumo) throws JRException {
        boolean paisagem = colunas.length > 6;
        int larguraPagina = paisagem ? 842 : 595;
        int alturaPagina = paisagem ? 595 : 842;
        int larguraUtil = larguraPagina - 2 * MARGEM;

        JasperDesign design = new JasperDesign();
        design.setName("relatorio");
        design.setPageWidth(larguraPagina);
        design.setPageHeight(alturaPagina);
        design.setColumnWidth(larguraUtil);
        design.setLeftMargin(MARGEM);
        design.setRightMargin(MARGEM);
        design.setTopMargin(MARGEM);
        design.setBottomMargin(MARGEM);
        design.setOrientation(paisagem ? OrientationEnum.LANDSCAPE : OrientationEnum.PORTRAIT);
        design.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

        for (int i = 0; i < colunas.length; i++) {
            JRDesignField campo = new JRDesignField();
            campo.setName("c" + i);
            campo.setValueClass(String.class);
            design.addField(campo);
        }

        int[] larguras = calcularLarguras(colunas, linhas, larguraUtil);
        boolean[] numericas = colunasNumericas(colunas.length, linhas);

        design.setTitle(montarTitulo(titulo, cabecalho, larguraUtil));
        design.setColumnHeader(montarCabecalhoColunas(colunas, larguras, numericas));
        ((JRDesignSection) design.getDetailSection()).addBand(montarDetalhe(colunas.length, larguras, numericas));
        design.setPageFooter(montarRodapePagina(larguraUtil));
        design.setSummary(montarResumo(resumo, larguraUtil));

        JasperReport relatorio = JasperCompileManager.compileReport(design);

        List<Map<String, ?>> dados = new ArrayList<>();
        for (Object[] linha : linhas) {
            Map<String, Object> registro = new HashMap<>();
            for (int i = 0; i < colunas.length; i++) {
                registro.put("c" + i, i < linha.length ? Formatador.texto(linha[i]) : "");
            }
            dados.add(registro);
        }

        return JasperFillManager.fillReport(relatorio, new HashMap<>(), new JRMapCollectionDataSource(dados));
    }

    private static JRDesignBand montarTitulo(String titulo, List<String> cabecalho, int largura) {
        JRDesignBand banda = new JRDesignBand();
        int y = 0;

        String oficina = Sessao.getUsuariologado() != null ? Sessao.getUsuariologado().getNomeOficina() : null;
        banda.addElement(texto(oficina == null || oficina.isBlank() ? "OfiSystem" : oficina,
                0, y, largura, 14, 10, false, HorizontalTextAlignEnum.LEFT));
        banda.addElement(texto("Emitido em " + Formatador.dataHora(LocalDateTime.now()) + " por " + Sessao.getNome(),
                0, y, largura, 14, 8, false, HorizontalTextAlignEnum.RIGHT));
        y += 16;

        banda.addElement(texto(titulo, 0, y, largura, 22, 15, true, HorizontalTextAlignEnum.LEFT));
        y += 24;

        for (String linha : cabecalho) {
            banda.addElement(texto(linha, 0, y, largura, 14, 9, false, HorizontalTextAlignEnum.LEFT));
            y += 14;
        }

        y += 4;
        banda.addElement(linha(0, y, largura));
        banda.setHeight(y + 6);
        return banda;
    }

    private static JRDesignBand montarCabecalhoColunas(String[] colunas, int[] larguras, boolean[] numericas) {
        JRDesignBand banda = new JRDesignBand();
        banda.setHeight(20);
        int x = 0;
        for (int i = 0; i < colunas.length; i++) {
            JRDesignStaticText t = texto(colunas[i], x, 0, larguras[i], 20, 9, true,
                    numericas[i] ? HorizontalTextAlignEnum.RIGHT : HorizontalTextAlignEnum.LEFT);
            t.setMode(ModeEnum.OPAQUE);
            t.setBackcolor(COR_CABECALHO);
            t.setForecolor(Color.WHITE);
            banda.addElement(t);
            x += larguras[i];
        }
        return banda;
    }

    private static JRDesignBand montarDetalhe(int quantidade, int[] larguras, boolean[] numericas) {
        JRDesignBand banda = new JRDesignBand();
        banda.setHeight(ALTURA_LINHA);
        banda.setSplitType(SplitTypeEnum.STRETCH);
        int x = 0;
        for (int i = 0; i < quantidade; i++) {
            JRDesignTextField campo = new JRDesignTextField();
            campo.setX(x);
            campo.setY(0);
            campo.setWidth(larguras[i]);
            campo.setHeight(ALTURA_LINHA);
            campo.setFontSize(8f);
            campo.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
            campo.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
            campo.setHorizontalTextAlign(numericas[i] ? HorizontalTextAlignEnum.RIGHT : HorizontalTextAlignEnum.LEFT);
            campo.getLineBox().setLeftPadding(3);
            campo.getLineBox().setRightPadding(3);
            campo.getLineBox().getBottomPen().setLineWidth(0.5f);
            campo.getLineBox().getBottomPen().setLineColor(new Color(215, 215, 215));
            campo.setExpression(new JRDesignExpression("$F{c" + i + "}"));
            banda.addElement(campo);
            x += larguras[i];
        }
        return banda;
    }

    private static JRDesignBand montarRodapePagina(int largura) {
        JRDesignBand banda = new JRDesignBand();
        banda.setHeight(16);

        JRDesignTextField pagina = new JRDesignTextField();
        pagina.setX(0);
        pagina.setY(2);
        pagina.setWidth(largura - 40);
        pagina.setHeight(14);
        pagina.setFontSize(8f);
        pagina.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        pagina.setExpression(new JRDesignExpression("\"Página \" + $V{PAGE_NUMBER} + \" de\""));
        banda.addElement(pagina);

        JRDesignTextField total = new JRDesignTextField();
        total.setX(largura - 40);
        total.setY(2);
        total.setWidth(40);
        total.setHeight(14);
        total.setFontSize(8f);
        total.setEvaluationTime(EvaluationTimeEnum.REPORT);
        total.setExpression(new JRDesignExpression("\" \" + $V{PAGE_NUMBER}"));
        banda.addElement(total);
        return banda;
    }

    private static JRDesignBand montarResumo(List<String> resumo, int largura) {
        JRDesignBand banda = new JRDesignBand();
        if (resumo == null || resumo.isEmpty()) {
            banda.setHeight(0);
            return banda;
        }
        int y = 8;
        banda.addElement(linha(0, 4, largura));
        for (String linha : resumo) {
            banda.addElement(texto(linha, 0, y, largura, 15, 10, true, HorizontalTextAlignEnum.RIGHT));
            y += 15;
        }
        banda.setHeight(y + 4);
        return banda;
    }

    private static JRDesignStaticText texto(String valor, int x, int y, int largura, int altura,
                                            float fonte, boolean negrito, HorizontalTextAlignEnum alinhamento) {
        JRDesignStaticText t = new JRDesignStaticText();
        t.setText(valor == null ? "" : valor);
        t.setX(x);
        t.setY(y);
        t.setWidth(largura);
        t.setHeight(altura);
        t.setFontSize(fonte);
        t.setBold(negrito);
        t.setHorizontalTextAlign(alinhamento);
        t.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        t.getLineBox().setLeftPadding(3);
        t.getLineBox().setRightPadding(3);
        return t;
    }

    private static JRDesignLine linha(int x, int y, int largura) {
        JRDesignLine l = new JRDesignLine();
        l.setX(x);
        l.setY(y);
        l.setWidth(largura);
        l.setHeight(0);
        l.getLinePen().setLineColor(COR_CABECALHO);
        return l;
    }

    private static int[] calcularLarguras(String[] colunas, List<Object[]> linhas, int larguraUtil) {
        double[] pesos = new double[colunas.length];
        for (int i = 0; i < colunas.length; i++) {
            int maior = colunas[i].length();
            for (Object[] linha : linhas) {
                if (i < linha.length) {
                    maior = Math.max(maior, Formatador.texto(linha[i]).length());
                }
            }
            pesos[i] = Math.max(5, Math.min(maior, 45));
        }
        double soma = 0;
        for (double p : pesos) soma += p;

        int[] larguras = new int[colunas.length];
        int usado = 0;
        for (int i = 0; i < colunas.length; i++) {
            larguras[i] = (int) Math.floor(larguraUtil * pesos[i] / soma);
            usado += larguras[i];
        }
        larguras[colunas.length - 1] += larguraUtil - usado;
        return larguras;
    }

    private static boolean[] colunasNumericas(int quantidade, List<Object[]> linhas) {
        boolean[] numericas = new boolean[quantidade];
        for (int i = 0; i < quantidade; i++) {
            boolean algum = false;
            boolean todos = true;
            for (Object[] linha : linhas) {
                String v = i < linha.length ? Formatador.texto(linha[i]).trim() : "";
                if (v.isEmpty()) continue;
                algum = true;
                if (!v.matches("^-?(R\\$[\\s\\u00A0]?)?-?[\\d.]+(,\\d+)?$")) {
                    todos = false;
                    break;
                }
            }
            numericas[i] = algum && todos;
        }
        return numericas;
    }
}
