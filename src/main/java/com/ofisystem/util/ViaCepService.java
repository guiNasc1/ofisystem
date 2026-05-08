package com.ofisystem.util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViaCepService {

    private static final String URL_BASE = "https://viacep.com.br/ws/%s/json/";

    public static EnderecoDTO buscarEndereco(String cep) throws Exception {

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if(cepLimpo.length() != 8){
            throw new IllegalArgumentException("CEP inválido!");
        }

        String urlSrt = String.format(URL_BASE, cepLimpo);

        URL url = new URL(urlSrt);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.setConnectTimeout(5000);
        conexao.setReadTimeout(5000);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conexao.getInputStream()));
        StringBuilder resposta = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            resposta.append(linha);
        }
        reader.close();

        Gson gson = new Gson();
        EnderecoDTO endereco = gson.fromJson(resposta.toString(), EnderecoDTO.class);

        if (endereco.isErro()) {
            throw new IllegalArgumentException("CEP não encontrado!");
        }

        return endereco;

    }

}
