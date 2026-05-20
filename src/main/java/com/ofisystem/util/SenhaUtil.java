package com.ofisystem.util;

import java.security.MessageDigest;
import java.util.Base64;

public class SenhaUtil {

    public static String criptografar(String senha){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }

    public static boolean verificar(String senha, String senhaCriptografada) {
        return criptografar(senha).equals(senhaCriptografada);
    }

}
