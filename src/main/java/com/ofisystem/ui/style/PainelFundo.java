package com.ofisystem.ui.style;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.imageio.ImageIO;

public class PainelFundo extends JPanel {

    private BufferedImage imagem;
    private BufferedImage imagemBlur;

    public PainelFundo(String caminhoImagem){
        try{
            imagem = ImageIO.read(
                    getClass().getResourceAsStream(caminhoImagem));
            imagemBlur = aplicarBlur(imagem, 1);
        }catch (Exception e){
            System.out.println("Erro ao criar imagem" + caminhoImagem);
        }
        setLayout(new GridBagLayout());
    }

    private BufferedImage aplicarBlur(BufferedImage imagem, int intensidade) {
        // monta o kernel de blur (matriz de média)
        int tamanho = intensidade * 2 + 1;
        float[] dados = new float[tamanho * tamanho];
        float valor = 1.0f / (tamanho * tamanho); // divide igualmente

        for (int i = 0; i < dados.length; i++) {
            dados[i] = valor;
        }

        Kernel kernel = new Kernel(tamanho, tamanho, dados);
        ConvolveOp op = new ConvolveOp(kernel,
                ConvolveOp.EDGE_NO_OP, null);

        // aplica o blur várias vezes para aumentar o efeito
        BufferedImage resultado = op.filter(imagem, null);
        resultado = op.filter(resultado, null); // segunda passagem
        resultado = op.filter(resultado, null); // terceira passagem

        return resultado;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        if (imagemBlur != null) {
            // desenha a imagem com blur esticada para cobrir o painel
            g2.drawImage(imagemBlur, 0, 0, getWidth(), getHeight(), this);

            // escurece levemente para o card se destacar
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
    }
}
