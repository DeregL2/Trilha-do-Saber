package com.tcc.trilha_do_saber.dto;

import java.util.List;

public class DetalheProvaAplicadaDTO {

    private final ProvaAplicadaResumoDTO prova;
    private final List<LinhaProvaAlunoDTO> linhas;
    private final int iniciaram;
    private final int finalizaram;
    private final int encerradasPorSaida;
    private final Double media;
    private final Double maiorNota;
    private final Double menorNota;
    private final List<FaixaNotaDTO> faixas;
    private final List<DesempenhoQuestaoDTO> questoes;

    public DetalheProvaAplicadaDTO(ProvaAplicadaResumoDTO prova, List<LinhaProvaAlunoDTO> linhas, int iniciaram,
                                   int finalizaram, int encerradasPorSaida, Double media) {
        this(prova, linhas, iniciaram, finalizaram, encerradasPorSaida, media, null, null, List.of(), List.of());
    }

    public DetalheProvaAplicadaDTO(ProvaAplicadaResumoDTO prova, List<LinhaProvaAlunoDTO> linhas, int iniciaram,
                                   int finalizaram, int encerradasPorSaida, Double media, Double maiorNota,
                                   Double menorNota, List<FaixaNotaDTO> faixas, List<DesempenhoQuestaoDTO> questoes) {
        this.prova = prova;
        this.linhas = List.copyOf(linhas);
        this.iniciaram = iniciaram;
        this.finalizaram = finalizaram;
        this.encerradasPorSaida = encerradasPorSaida;
        this.media = media;
        this.maiorNota = maiorNota;
        this.menorNota = menorNota;
        this.faixas = List.copyOf(faixas);
        this.questoes = List.copyOf(questoes);
    }

    public ProvaAplicadaResumoDTO getProva() { return prova; }
    public List<LinhaProvaAlunoDTO> getLinhas() { return linhas; }
    public int getIniciaram() { return iniciaram; }
    public int getFinalizaram() { return finalizaram; }
    public int getEncerradasPorSaida() { return encerradasPorSaida; }
    public Double getMedia() { return media; }
    public Double getMaiorNota() { return maiorNota; }
    public Double getMenorNota() { return menorNota; }
    public List<FaixaNotaDTO> getFaixas() { return faixas; }
    public List<DesempenhoQuestaoDTO> getQuestoes() { return questoes; }

    public int getTotalAlunos() {
        return linhas.size();
    }

    public boolean isTemMedia() {
        return media != null;
    }

    public int getMaiorFaixa() {
        return faixas.stream().mapToInt(FaixaNotaDTO::getQuantidade).max().orElse(0);
    }
}