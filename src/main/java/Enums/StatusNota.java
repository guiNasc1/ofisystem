package Enums;

public enum StatusNota {

    EMITIDA("Emitida"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusNota(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
