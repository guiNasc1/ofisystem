package com.ofisystem.ui;

import com.formdev.flatlaf.util.Animator;
import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.ui.style.Adesivo;
import com.ofisystem.ui.style.Botao;
import com.ofisystem.ui.style.EngrenagemForjada;
import com.ofisystem.ui.style.Icones;
import com.ofisystem.ui.style.Movimento;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class LoginFrame {

    private JFrame frame;
    private EngrenagemForjada palco;
    private EntradaCartao entrada;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private Botao btnLogin;
    private Botao btnCadastrar;
    private Botao btnTema;
    private JLabel lblErro;
    private JLabel qtdUsuario;
    private KeyEventDispatcher pularComTecla;
    private AWTEventListener pularComClique;
    private Adesivo adesivoCartao;

    private final UsuarioDAO usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        construirTela();
    }

    public void exibir() {
        definirInterativo(false);
        frame.setVisible(true);
        entrada.setProgresso(0f);
        pularComTecla = e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && !palco.isConcluido()) {
                palco.pular();
                return true;
            }
            return false;
        };
        pularComClique = e -> {
            if (e.getID() == MouseEvent.MOUSE_PRESSED && !palco.isConcluido()) {
                palco.pular();
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(pularComTecla);
        Toolkit.getDefaultToolkit().addAWTEventListener(pularComClique, AWTEvent.MOUSE_EVENT_MASK);
        palco.forjar(this::mostrarCartao);
        carregarContagem();
    }

    private void definirInterativo(boolean interativo) {
        for (JComponent c : new JComponent[]{txtUsuario, txtPassword, btnLogin, btnCadastrar}) {
            c.setFocusable(interativo);
        }
        btnLogin.setEnabled(interativo);
        btnCadastrar.setEnabled(interativo);
    }

    private void construirTela() {
        frame = new JFrame("OfiSystem — Entrar");
        frame.setSize(1180, 720);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        palco = new EngrenagemForjada();
        palco.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                palco.pular();
            }
        });
        palco.setPreferredSize(new Dimension(560, 600));
        gbc.gridx = 0;
        gbc.weightx = 0.56;
        raiz.add(palco, gbc);

        JPanel lado = new JPanel(new BorderLayout());
        lado.setPreferredSize(new Dimension(440, 600));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 16));
        barra.setOpaque(false);
        btnTema = new Botao("", Botao.Estilo.CONTORNO, Tema.isEscuro() ? Icones.Tipo.SOL : Icones.Tipo.LUA);
        btnTema.setToolTipText(Tema.isEscuro() ? "Usar tema claro" : "Usar tema escuro");
        btnTema.addActionListener(e -> alternarTema());
        barra.add(btnTema);
        lado.add(barra, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        entrada = new EntradaCartao();
        entrada.add(criarCartao());
        centro.add(entrada);
        lado.add(centro, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.44;
        raiz.add(lado, gbc);

        frame.setContentPane(raiz);
        frame.getRootPane().setDefaultButton(btnLogin);
    }

    private Adesivo criarCartao() {
        Adesivo cartao = new Adesivo("", "", Tema.Tom.NEUTRO, new GridBagLayout());
        adesivoCartao = cartao;
        cartao.setBorder(BorderFactory.createEmptyBorder(Adesivo.ALTURA_FAIXA + 24, 32, 36, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(Texto.titulo("Entrar"), gbc);
        gbc.insets = new Insets(0, 0, 24, 0);
        cartao.add(Texto.apoio("Acesse com o usuário da sua oficina."), gbc);

        txtUsuario = new JTextField();
        txtUsuario.putClientProperty("JTextField.leadingIcon", Icones.de(Icones.Tipo.USUARIO, 18, Tema::tintaSuave));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Nome de usuário");
        txtUsuario.setPreferredSize(new Dimension(328, 40));
        gbc.insets = new Insets(0, 0, 6, 0);
        cartao.add(rotulo("Usuário"), gbc);
        gbc.insets = new Insets(0, 0, 16, 0);
        cartao.add(txtUsuario, gbc);

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.leadingIcon", Icones.de(Icones.Tipo.CADEADO, 18, Tema::tintaSuave));
        txtPassword.putClientProperty("JTextField.placeholderText", "Sua senha");
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        txtPassword.setPreferredSize(new Dimension(328, 40));
        gbc.insets = new Insets(0, 0, 6, 0);
        cartao.add(rotulo("Senha"), gbc);
        gbc.insets = new Insets(0, 0, 8, 0);
        cartao.add(txtPassword, gbc);

        lblErro = Texto.erro();
        gbc.insets = new Insets(0, 0, 12, 0);
        cartao.add(lblErro, gbc);

        btnLogin = new Botao("Entrar", Botao.Estilo.PRINCIPAL);
        btnLogin.addActionListener(e -> login());
        gbc.insets = new Insets(0, 0, 8, 0);
        cartao.add(btnLogin, gbc);

        btnCadastrar = new Botao("Criar conta", Botao.Estilo.DISCRETO);
        btnCadastrar.addActionListener(e -> abrirCadastro());
        cartao.add(btnCadastrar, gbc);

        qtdUsuario = new Texto(" ", Tema.TAM_PEQUENO, Font.PLAIN, Tema::tintaSuave);
        qtdUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(12, 0, 0, 0);
        cartao.add(qtdUsuario, gbc);
        return cartao;
    }

    private JLabel rotulo(String texto) {
        return Texto.rotulo(texto);
    }

    private void mostrarCartao() {
        if (pularComTecla != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(pularComTecla);
            pularComTecla = null;
        }
        if (pularComClique != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(pularComClique);
            pularComClique = null;
        }
        definirInterativo(true);
        Movimento.animar(520, Movimento.SAIDA_SUAVE, p -> {
            adesivoCartao.setDescolar(1f - p);
            entrada.setProgresso(p);
        }, () -> {
            adesivoCartao.setDescolar(0f);
            entrada.setProgresso(1f);
            txtUsuario.requestFocusInWindow();
        });
    }

    private void alternarTema() {
        Tema.alternar(frame);
        btnTema.setIcon(Icones.de(Tema.isEscuro() ? Icones.Tipo.SOL : Icones.Tipo.LUA, 18, btnTema::corTexto));
        btnTema.setToolTipText(Tema.isEscuro() ? "Usar tema claro" : "Usar tema escuro");
    }

    private void login() {
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || senha.isEmpty()) {
            mostrarErro(usuario.isEmpty() ? "Informe o usuário." : "Informe a senha.");
            (usuario.isEmpty() ? txtUsuario : txtPassword).requestFocusInWindow();
            return;
        }

        lblErro.setText(" ");
        definirOcupado(true);

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() {
                return new UsuarioDAO().autenticar(usuario, senha);
            }

            @Override
            protected void done() {
                try {
                    Usuario u = get();
                    if (u != null) {
                        Sessao.iniciar(u);
                        frame.dispose();
                        SwingUtilities.invokeLater(() -> new MainFrame().exibirFrame());
                        return;
                    }
                    mostrarErro("Usuário ou senha incorretos.");
                    txtPassword.setText("");
                    txtPassword.requestFocusInWindow();
                } catch (Exception ex) {
                    mostrarErro("Não foi possível conectar ao servidor.");
                } finally {
                    definirOcupado(false);
                }
            }
        }.execute();
    }

    private void definirOcupado(boolean ocupado) {
        txtUsuario.setEnabled(!ocupado);
        txtPassword.setEnabled(!ocupado);
        btnLogin.setEnabled(!ocupado);
        btnCadastrar.setEnabled(!ocupado);
        btnLogin.setText(ocupado ? "Entrando..." : "Entrar");
    }

    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        Point origem = entrada.getLocation();
        Movimento.animar(320, Movimento.SAIDA_SUAVE, t -> {
            int desvio = (int) Math.round(Math.sin(t * Math.PI * 4) * 6 * (1 - t));
            entrada.setLocation(origem.x + desvio, origem.y);
        }, () -> entrada.setLocation(origem));
    }

    private void carregarContagem() {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return usuarioDAO.contarUsuario();
            }

            @Override
            protected void done() {
                try {
                    String total = get();
                    qtdUsuario.setText(("1".equals(total) ? "1 usuário cadastrado" : total + " usuários cadastrados"));
                } catch (Exception e) {
                    qtdUsuario.setText(" ");
                }
            }
        }.execute();
    }

    private void abrirCadastro() {
        new CadastroUsuarioFrame(frame, this::carregarContagem).exibir();
    }

    private static class EntradaCartao extends JPanel {

        private float progresso = 1f;

        EntradaCartao() {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        }

        void setProgresso(float progresso) {
            this.progresso = progresso;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            if (progresso >= 1f || !Animator.useAnimation()) {
                super.paint(g);
                return;
            }
            if (progresso <= 0f || getWidth() <= 0) return;
            BufferedImage imagem = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D gi = imagem.createGraphics();
            gi.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            super.paint(gi);
            gi.dispose();

            Graphics2D g2 = Tema.suave(g);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, progresso * 1.6f)));
            double escala = 0.94 + 0.06 * progresso;
            g2.translate(getWidth() / 2.0, getHeight() / 2.0 + (1 - progresso) * 36);
            g2.rotate(Math.toRadians(-4 * (1 - progresso)));
            g2.scale(escala, escala);
            g2.drawImage(imagem, -getWidth() / 2, -getHeight() / 2, null);
            g2.dispose();
        }
    }
}
