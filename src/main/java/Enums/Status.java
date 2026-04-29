package Enums;

public enum Status {

    ATIVO("Ativo"),
    INATIVO("Inativo"),
    PENDENTE("Pendente"),
    BLOQUEADO("Bloqueado"),
    CANCELADO("Cancelado");

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
