// ResultadoSimuladoDTO.java
package com.tcc.trilha_do_saber.dto;

import java.util.List;

// Carrega o resultado final do simulado pra exibir na tela
public class ResultadoSimuladoDTO {

    private int totalQuestoes;
    private int acertos;
    private int erros;
    private double percentualAcerto;
    private List<TemaErroDTO> temasParaEstudar;

    public ResultadoSimuladoDTO(int totalQuestoes, int acertos, int erros, double percentualAcerto, List<TemaErroDTO> temasParaEstudar) {
        this.totalQuestoes = totalQuestoes;
        this.acertos = acertos;
        this.erros = erros;
        this.percentualAcerto = percentualAcerto;
        this.temasParaEstudar = temasParaEstudar;
    }

    public int getTotalQuestoes() { return totalQuestoes; }
    public int getAcertos() { return acertos; }
    public int getErros() { return erros; }
    public double getPercentualAcerto() { return percentualAcerto; }
    public List<TemaErroDTO> getTemasParaEstudar() { return temasParaEstudar; }
}
