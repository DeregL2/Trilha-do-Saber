package com.tcc.trilha_do_saber.dto;

public class AlunoOpcaoDTO {

    private final Long id;
    private final String nome;
    private final String registro;

    public AlunoOpcaoDTO(Long id, String nome, String registro) {
        this.id = id;
        this.nome = nome;
        this.registro = registro;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getRegistro() { return registro; }
}