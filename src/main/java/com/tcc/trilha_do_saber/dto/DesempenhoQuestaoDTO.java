package com.tcc.trilha_do_saber.dto;

public class DesempenhoQuestaoDTO {

    private final int ordem;
    private final int numero;
    private final String resumo;
    private final long acertos;
    private final long total;

    public DesempenhoQuestaoDTO(int ordem, int numero, String resumo, long acertos, long total) {
        this.ordem = ordem;
        this.numero = numero;
        this.resumo = resumo;
        this.acertos = acertos;
        this.total = total;
    }

    public int getOrdem() { return ordem; }
    public int getNumero() { return numero; }
    public String getResumo() { return resumo; }
    public long getAcertos() { return acertos; }
    public long getTotal() { return total; }

    public double getPercentual() {
        return total > 0 ? acertos * 100.0 / total : 0.0;
    }

    public String getNivel() {
        double percentual = getPercentual();
        if (percentual >= 70) {
            return "facil";
        }
        if (percentual >= 40) {
            return "medio";
        }
        return "dificil";
    }
}