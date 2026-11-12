package com.tcc.trilha_do_saber.model;

public enum StatusProva {

    AGENDADA("Agendada"),
    ABERTA("Aberta"),
    ENCERRADA("Encerrada"),
    CANCELADA("Cancelada");

    private final String rotulo;

    StatusProva(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}