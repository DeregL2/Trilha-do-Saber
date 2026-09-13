// ResultadoSimuladoDTO.java
package com.tcc.trilha_do_saber.dto;

// Carrega o resultado final do simulado pra exibir na tela
public class ResultadoSimuladoDTO {

    private int totalQuestoes;
    private int acertos;
    private int erros;
    private double percentualAcerto;

    public ResultadoSimuladoDTO(int totalQuestoes, int acertos, int erros, double percentualAcerto) {
        this.totalQuestoes = totalQuestoes;
        this.acertos = acertos;
        this.erros = erros;
        this.percentualAcerto = percentualAcerto;
    }

    public int getTotalQuestoes() { return totalQuestoes; }
    public int getAcertos() { return acertos; }
    public int getErros() { return erros; }
    public double getPercentualAcerto() { return percentualAcerto; }
}