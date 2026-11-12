package com.tcc.trilha_do_saber.dto;

import java.util.List;

public class ModeloProvaDTO {

    private final String titulo;
    private final List<Long> questaoIds;

    public ModeloProvaDTO(String titulo, List<Long> questaoIds) {
        this.titulo = titulo;
        this.questaoIds = List.copyOf(questaoIds);
    }

    public String getTitulo() { return titulo; }
    public List<Long> getQuestaoIds() { return questaoIds; }
}