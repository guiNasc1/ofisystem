package com.ofisystem.ui.style;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public final class Tema {

    public enum Modo { CLARO, ESCURO }

    public enum Tom { NEUTRO, INFO, ATENCAO, CRITICO, SUCESSO, INATIVO }

    public static final int TAM_PEQUENO = 12;
    public static final int TAM_TEXTO = 14;
    public static final int TAM_TITULO = 22;

    public static final int RAIO = 12;
    public static final int FOLGA_SOMBRA = 12;
    public static final int RAIO_CONTROLE = 8;

    private static final String CHAVE_PREFERENCIA = "tema";
    private static Modo modo = Modo.CLARO;
    private static String familia;

    private Tema() {
    }

    public static Modo getModo() {
        return modo;
    }

    public static boolean isEscuro() {
        return modo == Modo.ESCURO;
    }

    public static Color chao() { return cor("#E9EDF2", "#0F151C"); }
    public static Color vinil() { return cor("#FFFFFF", "#18212B"); }
    public static Color vinil2() { return cor("#F4F6F9", "#1F2A36"); }
    public static Color borda() { return cor("#D3DAE2", "#2A3643"); }
    public static Color tinta() { return cor("#16202B", "#E6ECF2"); }
    public static Color tintaSuave() { return cor("#4E5B68", "#9DABB9"); }
    public static Color aco() { return cor("#2B4A6F", "#4A79AD"); }
    public static Color acoHover() { return cor("#223D5C", "#5A89BD"); }
    public static Color trilhoTopo() { return cor("#2B4A6F", "#1A2C40"); }
    public static Color trilhoBase() { return cor("#1F3A5A", "#121E2B"); }
    public static Color trilhoTexto() { return new Color(0xDCE6F1); }
    public static Color trilhoSuave() { return new Color(0xA9BDD3); }
    public static Color foco() { return cor("#4A79AD", "#7FA6D3"); }
    public static Color sombra() { return isEscuro() ? new Color(0, 0, 0, 115) : new Color(16, 24, 32, 36); }

    public static Color atencao() { return new Color(0xE8A33D); }
    public static Color critico() { return cor("#C8372D", "#E5605A"); }
    public static Color sucesso() { return cor("#2E8B57", "#4FB37E"); }

    public static Color fundo(Tom tom) {
        return switch (tom) {
            case INFO -> cor("#E1EAF4", "#1C2D40");
            case ATENCAO -> cor("#FDF1DC", "#3A2E17");
            case CRITICO -> cor("#FBE5E3", "#3A1E1E");
            case SUCESSO -> cor("#E3F4EA", "#18301F");
            case INATIVO -> cor("#EEF1F4", "#222C37");
            case NEUTRO -> vinil();
        };
    }

    public static Color texto(Tom tom) {
        return switch (tom) {
            case INFO -> cor("#1F3A5A", "#A9C6E6");
            case ATENCAO -> cor("#7A4E0B", "#F2C27A");
            case CRITICO -> cor("#A52A21", "#F19A94");
            case SUCESSO -> cor("#1E6B3A", "#8FD6AE");
            case INATIVO -> tintaSuave();
            case NEUTRO -> tinta();
        };
    }

    public static Color faixa(Tom tom) {
        return switch (tom) {
            case ATENCAO -> atencao();
            case CRITICO -> new Color(0xC8372D);
            case SUCESSO -> new Color(0x23744A);
            case INATIVO -> cor("#6B7785", "#4A5868");
            case INFO, NEUTRO -> acoForte();
        };
    }

    public static Color acoForte() { return cor("#2B4A6F", "#3D6A9C"); }
    public static Color acoForteHover() { return cor("#223D5C", "#4A79AD"); }

    public static Color textoFaixa(Tom tom) {
        return tom == Tom.ATENCAO ? new Color(0x16202B) : Color.WHITE;
    }

    public static Font fonte(int tamanho, int estilo) {
        return new Font(familia(), estilo, tamanho);
    }

    public static Font fonte(int tamanho) {
        return fonte(tamanho, Font.PLAIN);
    }

    public static String familia() {
        if (familia == null) {
            String[] preferidas = {"Segoe UI", "Inter", "Noto Sans", "Cantarell", "Ubuntu", "Liberation Sans"};
            java.util.Set<String> instaladas = java.util.Set.of(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            familia = Font.SANS_SERIF;
            for (String f : preferidas) {
                if (instaladas.contains(f)) {
                    familia = f;
                    break;
                }
            }
        }
        return familia;
    }

    public static void iniciar() {
        try {
            modo = Modo.valueOf(Preferences.userNodeForPackage(Tema.class).get(CHAVE_PREFERENCIA, Modo.CLARO.name()));
        } catch (Exception e) {
            modo = Modo.CLARO;
        }
        aplicarLaf();
    }

    public static void alternar(Window janela) {
        definir(isEscuro() ? Modo.CLARO : Modo.ESCURO, janela);
    }

    public static void definir(Modo novo, Window janela) {
        if (novo == modo) return;
        JRootPane raiz = janela instanceof RootPaneContainer r ? r.getRootPane() : null;
        BufferedImage antes = raiz == null ? null : Movimento.capturar(raiz);

        modo = novo;
        salvarPreferencia();
        aplicarLaf();
        FlatLaf.updateUI();

        if (antes == null) return;
        JLayeredPane camadas = raiz.getLayeredPane();
        Movimento.CamadaDesvanecer veu = new Movimento.CamadaDesvanecer(antes);
        veu.setBounds(0, 0, antes.getWidth(), antes.getHeight());
        camadas.add(veu, JLayeredPane.DRAG_LAYER);
        Movimento.animar(320, Movimento.SAIDA_SUAVE, t -> veu.setOpacidade(1f - t), () -> {
            camadas.remove(veu);
            camadas.repaint();
        });
    }

    private static void salvarPreferencia() {
        try {
            Preferences.userNodeForPackage(Tema.class).put(CHAVE_PREFERENCIA, modo.name());
        } catch (SecurityException | IllegalStateException e) {
            System.err.println("Não foi possível salvar o tema escolhido: " + e.getMessage());
        }
    }

    private static void aplicarLaf() {
        UIManager.put("defaultFont", new FontUIResource(familia(), Font.PLAIN, TAM_TEXTO));
        FlatLaf.setGlobalExtraDefaults(propriedades());
        if (isEscuro()) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
    }

    private static Map<String, String> propriedades() {
        Map<String, String> p = new HashMap<>();
        p.put("@accentColor", hex(aco()));
        p.put("@background", hex(chao()));
        p.put("@foreground", hex(tinta()));
        p.put("Panel.background", hex(chao()));
        p.put("OptionPane.background", hex(vinil()));
        p.put("RootPane.background", hex(chao()));
        p.put("Viewport.background", hex(vinil()));
        p.put("ScrollPane.background", hex(vinil()));
        p.put("Label.foreground", hex(tinta()));
        p.put("Label.disabledForeground", hex(tintaSuave()));

        p.put("Component.arc", String.valueOf(RAIO_CONTROLE));
        p.put("Button.arc", String.valueOf(RAIO_CONTROLE));
        p.put("TextComponent.arc", String.valueOf(RAIO_CONTROLE));
        p.put("CheckBox.arc", "4");
        p.put("Component.focusWidth", "2");
        p.put("Component.innerFocusWidth", "0");
        p.put("Component.focusColor", hex(foco()));
        p.put("Component.borderColor", hex(borda()));
        p.put("Component.focusedBorderColor", hex(foco()));
        p.put("Component.disabledBorderColor", hex(borda()));

        for (String c : new String[]{"TextField", "PasswordField", "FormattedTextField", "TextArea", "ComboBox", "Spinner"}) {
            p.put(c + ".background", hex(vinil()));
            p.put(c + ".foreground", hex(tinta()));
            p.put(c + ".disabledBackground", hex(vinil2()));
            p.put(c + ".inactiveBackground", hex(vinil2()));
        }
        p.put("TextField.placeholderForeground", hex(tintaSuave()));
        p.put("PasswordField.placeholderForeground", hex(tintaSuave()));
        p.put("FormattedTextField.placeholderForeground", hex(tintaSuave()));
        p.put("ComboBox.buttonBackground", hex(vinil()));
        p.put("ComboBox.popupBackground", hex(vinil()));
        p.put("ComboBox.selectionBackground", hex(aco()));
        p.put("List.selectionBackground", hex(aco()));
        p.put("TextComponent.selectionBackground", hex(foco()));
        p.put("TextComponent.selectionForeground", "#FFFFFF");

        p.put("Table.background", hex(vinil()));
        p.put("Table.foreground", hex(tinta()));
        p.put("Table.rowHeight", "36");
        p.put("Table.showHorizontalLines", "true");
        p.put("Table.showVerticalLines", "false");
        p.put("Table.intercellSpacing", "0,1");
        p.put("Table.gridColor", hex(borda()));
        p.put("Table.selectionBackground", hex(fundo(Tom.INFO)));
        p.put("Table.selectionForeground", hex(texto(Tom.INFO)));
        p.put("Table.selectionInactiveBackground", hex(fundo(Tom.INFO)));
        p.put("Table.selectionInactiveForeground", hex(texto(Tom.INFO)));
        p.put("Table.cellFocusColor", hex(foco()));
        p.put("TableHeader.background", hex(vinil2()));
        p.put("TableHeader.foreground", hex(tintaSuave()));
        p.put("TableHeader.height", "36");
        p.put("TableHeader.separatorColor", hex(vinil2()));
        p.put("TableHeader.bottomSeparatorColor", hex(borda()));

        p.put("ScrollBar.width", "10");
        p.put("ScrollBar.thumbArc", "999");
        p.put("ScrollBar.thumbInsets", "2,2,2,2");
        p.put("ScrollBar.showButtons", "false");
        p.put("ScrollBar.track", hex(vinil()));
        p.put("ScrollBar.thumb", hex(borda()));
        p.put("ScrollBar.hoverThumbColor", hex(tintaSuave()));

        p.put("TabbedPane.underlineColor", hex(aco()));
        p.put("TabbedPane.selectedForeground", hex(tinta()));
        p.put("TabbedPane.foreground", hex(tintaSuave()));
        p.put("TabbedPane.hoverColor", hex(vinil2()));
        p.put("TabbedPane.background", hex(vinil()));
        p.put("TabbedPane.contentAreaColor", hex(borda()));
        p.put("TabbedPane.tabHeight", "36");

        p.put("CheckBox.icon.selectedBackground", hex(aco()));
        p.put("CheckBox.icon.selectedBorderColor", hex(aco()));
        p.put("CheckBox.icon.checkmarkColor", "#FFFFFF");
        p.put("Separator.foreground", hex(borda()));
        p.put("ToolTip.background", hex(tinta()));
        p.put("ToolTip.foreground", hex(vinil()));
        p.put("PopupMenu.background", hex(vinil()));
        return p;
    }

    private static Color cor(String claro, String escuro) {
        return Color.decode(isEscuro() ? escuro : claro);
    }

    private static String hex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static javax.swing.border.Border linha() {
        return new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int largura, int altura) {
                g.setColor(borda());
                g.drawRect(x, y, largura - 1, altura - 1);
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                insets.set(1, 1, 1, 1);
                return insets;
            }
        };
    }

    public static Graphics2D suave(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g2;
    }

    public static void sombra(Graphics2D g2, int x, int y, int largura, int altura, int raio, int elevacao) {
        Color base = sombra();
        int desfoque = Math.min(FOLGA_SOMBRA - 1, 4 + elevacao);
        float total = base.getAlpha() / 255f;
        for (int i = desfoque; i >= 1; i--) {
            float peso = 1f - (float) (i - 1) / desfoque;
            float alfa = total * peso * peso * 0.42f;
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.min(255, Math.round(alfa * 255))));
            int recuo = Math.max(0, (desfoque - i) / 3);
            g2.fillRoundRect(x + recuo, y + i, largura - recuo * 2, altura, raio + 2, raio + 2);
        }
    }
}
