package com.ofisystem.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public final class Formatador {

    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private Formatador() {
    }

    public static String moeda(Double valor) {
        return NumberFormat.getCurrencyInstance(BRASIL).format(valor == null ? 0 : valor);
    }

    public static String decimal(Double valor) {
        NumberFormat nf = NumberFormat.getNumberInstance(BRASIL);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(valor == null ? 0 : valor);
    }

    public static String data(LocalDate data) {
        return data == null ? "" : DATA.format(data);
    }

    public static String data(LocalDateTime data) {
        return data == null ? "" : DATA.format(data);
    }

    public static String dataHora(LocalDateTime data) {
        return data == null ? "" : DATA_HORA.format(data);
    }

    public static LocalDate parseData(String texto) {
        if (texto == null || somenteDigitos(texto).isEmpty()) return null;
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: " + texto.trim() + ". Use o formato dd/mm/aaaa.");
        }
    }

    public static Double parseValor(String texto) {
        if (texto == null || texto.isBlank()) return null;
        String limpo = texto.replace("R$", "").replace(" ", "").trim();
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        }
        try {
            return Double.parseDouble(limpo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido: " + texto.trim());
        }
    }

    public static Integer parseInteiro(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido: " + texto.trim());
        }
    }

    public static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    public static String documento(String documento) {
        String d = somenteDigitos(documento);
        if (d.length() == 11) {
            return d.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        if (d.length() == 14) {
            return d.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        return documento == null ? "" : documento;
    }

    public static String telefone(String telefone) {
        String d = somenteDigitos(telefone);
        if (d.length() == 11) {
            return d.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        if (d.length() == 10) {
            return d.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return telefone == null ? "" : telefone;
    }

    public static String cep(String cep) {
        String d = somenteDigitos(cep);
        return d.length() == 8 ? d.substring(0, 5) + "-" + d.substring(5) : (cep == null ? "" : cep);
    }

    public static String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }
}
