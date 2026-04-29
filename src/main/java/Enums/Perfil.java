package Enums;

public enum Perfil {

    ADMIN("Administrador"),
    USUARIO("Usuário"),
    GERENTE("Gerente"),
    SUPORTE("Suporte");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
