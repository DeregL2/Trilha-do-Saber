package com.tcc.trilha_do_saber.dto;

import java.util.List;

public class ProvaBancoDTO {

    private final Long provaId;
    private final int ano;
    private final String curso;
    private final List<QuestaoBancoDTO> questoes;

    public ProvaBancoDTO(Long provaId, int ano, String curso, List<QuestaoBancoDTO> questoes) {
        this.provaId = provaId;
        this.ano = ano;
        this.curso = curso;
        this.questoes = List.copyOf(questoes);
    }

    public Long getProvaId() { return provaId; }
    public int getAno() { return ano; }
    public String getCurso() { return curso; }
    public List<QuestaoBancoDTO> getQuestoes() { return questoes; }

    public String getRotulo() {
        return "ENADE " + ano + " · " + curso;
    }
}