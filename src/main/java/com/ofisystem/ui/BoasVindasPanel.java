package com.ofisystem.ui;

import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.ui.style.Adesivo;
import com.ofisystem.ui.style.Botao;
import com.ofisystem.ui.style.EngrenagemForjada;
import com.ofisystem.ui.style.Tema;
import com.ofisystem.ui.style.Texto;
import com.ofisystem.util.Sessao;

import javax.swing.*;
import java.awt.*;

public class BoasVindasPanel extends JPanel {

    private final JTextField txtNomeOficina;
    private final JLabel lblErro;
    private final Botao btConfirmar;
    private final Runnable aoConcluir;
    private final UsuarioDAO usuarioDAO;

    public BoasVindasPanel(Runnable aoConcluir) {
        super(new GridBagLayout());
        this.aoConcluir = aoConcluir;
        this.usuarioDAO = new UsuarioDAO();
        setOpaque(true);

        Adesivo cartao = new Adesivo("OfiSystem", "Primeiro acesso", Tema.Tom.NEUTRO, new GridBagLayout());
        cartao.setBorder(BorderFactory.createEmptyBorder(Adesivo.ALTURA_FAIXA + 24, 32, 36, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        EngrenagemForjada marca = new EngrenagemForjada();
        marca.setSomenteMarca(true);
        marca.setPreferredSize(new Dimension(56, 56));
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 16, 0);
        cartao.add(marca, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(Texto.titulo("Bem-vindo, " + Sessao.getNome() + "!"), gbc);
        gbc.insets = new Insets(0, 0, 24, 0);
        cartao.add(new JLabel("<html><body style='width:330px'>Antes de começar, dê um nome para sua oficina. "
                + "Ele aparece no menu e nos documentos impressos.</body></html>") {
            @Override
            public Color getForeground() {
                return Tema.tintaSuave();
            }
        }, gbc);

        gbc.insets = new Insets(0, 0, 6, 0);
        cartao.add(Texto.rotulo("Nome da oficina"), gbc);
        txtNomeOficina = new JTextField();
        txtNomeOficina.setPreferredSize(new Dimension(396, 40));
        txtNomeOficina.putClientProperty("JTextField.placeholderText", "Ex.: Oficina do João");
        gbc.insets = new Insets(0, 0, 8, 0);
        cartao.add(txtNomeOficina, gbc);

        lblErro = Texto.erro();
        gbc.insets = new Insets(0, 0, 12, 0);
        cartao.add(lblErro, gbc);

        btConfirmar = new Botao("Começar a usar", Botao.Estilo.PRINCIPAL);
        gbc.insets = new Insets(0, 0, 0, 0);
        cartao.add(btConfirmar, gbc);

        add(cartao);

        btConfirmar.addActionListener(e -> salvar());
        txtNomeOficina.addActionListener(e -> salvar());
    }

    public void focar() {
        SwingUtilities.invokeLater(txtNomeOficina::requestFocusInWindow);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, new Color(0x1B2B3D), getWidth(), getHeight(), new Color(0x0B1219)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(255, 255, 255, 10));
        for (int x = 32; x < getWidth(); x += 32) g2.drawLine(x, 0, x, getHeight());
        for (int y = 32; y < getHeight(); y += 32) g2.drawLine(0, y, getWidth(), y);
        g2.dispose();
    }

    private void salvar() {
        String nome = txtNomeOficina.getText().trim();
        if (nome.isEmpty()) {
            lblErro.setText("Digite o nome da oficina.");
            txtNomeOficina.requestFocusInWindow();
            return;
        }

        btConfirmar.setEnabled(false);
        btConfirmar.setText("Salvando...");
        Sessao.getUsuariologado().setNomeOficina(nome);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                usuarioDAO.atualizarNomeOficina(Sessao.getUsuariologado());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (aoConcluir != null) aoConcluir.run();
                } catch (Exception e) {
                    btConfirmar.setEnabled(true);
                    btConfirmar.setText("Começar a usar");
                    lblErro.setText("Não foi possível salvar. Verifique a conexão.");
                }
            }
        }.execute();
    }
}
