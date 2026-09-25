package com.tcc.trilha_do_saber.dto;

import java.io.Serializable;
import java.time.Instant;

public class VerificacaoSessaoDTO implements Serializable {

    private final UsuarioSessaoDTO usuario;
    private final String codigo;
    private final Instant expiraEm;
    private final boolean emailEnviado;

    public VerificacaoSessaoDTO(UsuarioSessaoDTO usuario, String codigo, Instant expiraEm, boolean emailEnviado){
        this.usuario = usuario;
        this.codigo = codigo;
        this.expiraEm = expiraEm;
        this.emailEnviado = emailEnviado;
    }

    public UsuarioSessaoDTO getUsuario(){ return usuario; }
    public String getCodigo(){ return codigo; }
    public Instant getExpiraEm(){ return expiraEm; }
    public boolean isEmailEnviado(){ return emailEnviado; }

    public boolean expirado(){
        return Instant.now().isAfter(expiraEm);
    }
}