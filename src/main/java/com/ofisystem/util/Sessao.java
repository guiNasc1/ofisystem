package com.ofisystem.util;

import com.ofisystem.entidade.Usuario;

public class Sessao {

    private static Usuario usuariologado;

    public static void iniciar(Usuario usuario){
        usuariologado = usuario;
    }

    public static Usuario getUsuariologado(){
        return usuariologado;
    }

    public static String getNome(){
        return usuariologado != null ? usuariologado.getNome() : "";
    }

    public static void encerrar(){
        usuariologado = null;
    }

    public static boolean estaLogado(){
        return usuariologado != null;
    }

}
