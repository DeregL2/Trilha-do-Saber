package com.tcc.trilha_do_saber.dto;

public class QuestaoBancoDTO {

    private final Long id;
    private final int numero;
    private final String resumo;
    private final String enunciado;
    private final String dificuldade;
    private final boolean temImagem;

    public QuestaoBancoDTO(Long id, int numero, String resumo, String enunciado, String dificuldade, boolean temImagem) {
        this.id = id;
        this.numero = numero;
        this.resumo = resumo;
        this.enunciado = enunciado;
        this.dificuldade = dificuldade;
        this.temImagem = temImagem;
    }

    public Long getId() { return id; }
    public int getNumero() { return numero; }
    public String getResumo() { return resumo; }
    public String getEnunciado() { return enunciado; }
    public String getDificuldade() { return dificuldade; }
    public boolean isTemImagem() { return temImagem; }
    public boolean isResumoCompleto() { return resumo.equals(enunciado); }
}