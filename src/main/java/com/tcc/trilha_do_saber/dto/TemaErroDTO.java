// TemaErroDTO.java
package com.tcc.trilha_do_saber.dto;

// Guarda quantas questoes o aluno errou em cada tema
public class TemaErroDTO {

    private String nomeTema;
    private long quantidadeErros;

    public TemaErroDTO(String nomeTema, long quantidadeErros) {
        this.nomeTema = nomeTema;
        this.quantidadeErros = quantidadeErros;
    }

    public String getNomeTema() { return nomeTema; }
    public long getQuantidadeErros() { return quantidadeErros; }
}
