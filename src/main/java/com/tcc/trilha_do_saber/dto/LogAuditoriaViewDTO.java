package com.tcc.trilha_do_saber.dto;

import com.tcc.trilha_do_saber.model.LogAuditoria;
import com.tcc.trilha_do_saber.model.TipoAcao;

import java.time.LocalDateTime;

public class LogAuditoriaViewDTO {

    private final LogAuditoria log;
    private final boolean integro;

    public LogAuditoriaViewDTO(LogAuditoria log, boolean integro){
        this.log = log;
        this.integro = integro;
    }

    public Long getId(){ return log.getId(); }
    public LocalDateTime getDataHora(){ return log.getDataHora(); }
    public String getUsuarioNome(){ return log.getUsuarioNome(); }
    public String getUsuarioTipo(){ return log.getUsuarioTipo(); }
    public TipoAcao getAcao(){ return log.getAcao(); }
    public String getEntidadeTipo(){ return log.getEntidadeTipo(); }
    public Long getEntidadeId(){ return log.getEntidadeId(); }
    public String getDetalhes(){ return log.getDetalhes(); }
    public String getIp(){ return log.getIp(); }
    public Long getDuracaoMs(){ return log.getDuracaoMs(); }
    public boolean isIntegro(){ return integro; }
}