package Enums;

public enum StatusOS {

    ABERTA("Aberta"),
    EM_ANDAMENTO("Em andamento"),
    AGUARDANDO_PECA("Aguardando peça"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusOS(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isEditavel() {
        return this != CONCLUIDA && this != CANCELADA;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
