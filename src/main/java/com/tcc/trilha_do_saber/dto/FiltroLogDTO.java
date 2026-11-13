package com.tcc.trilha_do_saber.dto;

import com.tcc.trilha_do_saber.model.TipoAcao;

import java.time.LocalDate;

public class FiltroLogDTO {

    private final String usuario;
    private final String perfil;
    private final TipoAcao acao;
    private final LocalDate de;
    private final LocalDate ate;

    public FiltroLogDTO(String usuario, String perfil, TipoAcao acao, LocalDate de, LocalDate ate) {
        this.usuario = usuario;
        this.perfil = perfil;
        this.acao = acao;
        this.de = de;
        this.ate = ate;
    }

    public String getUsuario() { return usuario; }
    public String getPerfil() { return perfil; }
    public TipoAcao getAcao() { return acao; }
    public LocalDate getDe() { return de; }
    public LocalDate getAte() { return ate; }

    public boolean temFiltro() {
        return (usuario != null && !usuario.isBlank())
                || (perfil != null && !perfil.isBlank())
                || acao != null
                || de != null
                || ate != null;
    }
}