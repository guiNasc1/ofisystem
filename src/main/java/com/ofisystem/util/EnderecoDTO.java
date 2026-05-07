package com.ofisystem.util;

public class EnderecoDTO {

    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
    private String erro;

    public String getBairro() {
        return bairro;
    }

    public String getCep() {
        return cep;
    }

    public boolean isErro() {
        return "true".equals(erro);
    }

    public String getLocalidade() {
        return localidade;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getUf() {
        return uf;
    }
}
