package com.tcc.trilha_do_saber.dto;

import com.tcc.trilha_do_saber.model.TipoAcao;

public class ContagemAcaoDTO {

    private final TipoAcao acao;
    private final long quantidade;

    public ContagemAcaoDTO(TipoAcao acao, long quantidade) {
        this.acao = acao;
        this.quantidade = quantidade;
    }

    public TipoAcao getAcao() { return acao; }
    public long getQuantidade() { return quantidade; }
}