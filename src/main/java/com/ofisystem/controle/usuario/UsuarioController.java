package com.ofisystem.controle.usuario;

import com.ofisystem.dao.UsuarioDAO;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.util.RegraNegocioException;

import java.util.Locale;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void cadastrar(Usuario usuario) {
        validarCadastro(usuario);
        usuarioDAO.salvar(usuario);
    }

    public static void validarCadastro(Usuario usuario) {
        String nome = usuario.getNome() == null ? "" : usuario.getNome().trim().toLowerCase(Locale.ROOT);
        String email = usuario.getEmail() == null ? "" : usuario.getEmail().trim().toLowerCase(Locale.ROOT);
        if (nome.length() < 3 || nome.length() > 80) {
            throw new RegraNegocioException("Informe um usuário com 3 a 80 caracteres.");
        }
        if (email.length() > 254 || !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new RegraNegocioException("A senha precisa ter pelo menos 6 caracteres.");
        }
        usuario.setNome(nome);
        usuario.setEmail(email);
    }
}
