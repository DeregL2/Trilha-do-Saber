package com.tcc.trilha_do_saber.dto;

public class FaixaNotaDTO {

    private final String rotulo;
    private final int quantidade;

    public FaixaNotaDTO(String rotulo, int quantidade) {
        this.rotulo = rotulo;
        this.quantidade = quantidade;
    }

    public String getRotulo() { return rotulo; }
    public int getQuantidade() { return quantidade; }
}