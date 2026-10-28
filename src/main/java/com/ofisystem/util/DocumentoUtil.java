package com.ofisystem.util;

public final class DocumentoUtil {

    private DocumentoUtil() {
    }

    public static boolean cpfValido(String cpf) {
        String d = Formatador.somenteDigitos(cpf);
        if (d.length() != 11 || d.chars().distinct().count() == 1) return false;
        return digitoCpf(d, 9) == d.charAt(9) - '0' && digitoCpf(d, 10) == d.charAt(10) - '0';
    }

    public static boolean cnpjValido(String cnpj) {
        String d = Formatador.somenteDigitos(cnpj);
        if (d.length() != 14 || d.chars().distinct().count() == 1) return false;
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return digitoCnpj(d, pesos1) == d.charAt(12) - '0' && digitoCnpj(d, pesos2) == d.charAt(13) - '0';
    }

    private static int digitoCpf(String d, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (d.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private static int digitoCnpj(String d, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (d.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
