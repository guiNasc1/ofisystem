package com.ofisystem;

import com.ofisystem.controle.usuario.UsuarioController;
import com.ofisystem.entidade.Usuario;
import com.ofisystem.util.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioValidacaoTest {

    static Stream<Arguments> cadastrosInvalidos() {
        return Stream.of(
                Arguments.of(null, "teste@example.com", "senha123"),
                Arguments.of("   ", "teste@example.com", "senha123"),
                Arguments.of("ab", "teste@example.com", "senha123"),
                Arguments.of("a".repeat(81), "teste@example.com", "senha123"),
                Arguments.of("teste", null, "senha123"),
                Arguments.of("teste", "", "senha123"),
                Arguments.of("teste", "sem-arroba", "senha123"),
                Arguments.of("teste", "teste@", "senha123"),
                Arguments.of("teste", "a".repeat(243) + "@example.com", "senha123"),
                Arguments.of("teste", "teste@example.com", null),
                Arguments.of("teste", "teste@example.com", ""),
                Arguments.of("teste", "teste@example.com", "12345")
        );
    }

    @ParameterizedTest
    @MethodSource("cadastrosInvalidos")
    void rejeitaCadastroInvalidoAntesDeAcessarOBanco(String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        assertThrows(RegraNegocioException.class, () -> UsuarioController.validarCadastro(usuario));
        assertNull(usuario.getId());
    }

    @Test
    void aceitaLimitesENormalizaNomeEEmail() {
        Usuario usuario = new Usuario();
        usuario.setNome(" " + "A".repeat(80) + " ");
        usuario.setEmail(" TESTE+FACULDADE@EXAMPLE.COM ");
        usuario.setSenha("123456");
        UsuarioController.validarCadastro(usuario);
        assertEquals("a".repeat(80), usuario.getNome());
        assertEquals("teste+faculdade@example.com", usuario.getEmail());
        assertEquals("123456", usuario.getSenha());
    }
}
