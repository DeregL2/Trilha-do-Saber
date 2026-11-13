package com.tcc.trilha_do_saber.dto;

import com.tcc.trilha_do_saber.model.LogAuditoria;

import java.util.List;

public class ResultadoBuscaLogDTO {

    private final List<LogAuditoria> logs;
    private final int totalEncontrado;

    public ResultadoBuscaLogDTO(List<LogAuditoria> logs, int totalEncontrado) {
        this.logs = List.copyOf(logs);
        this.totalEncontrado = totalEncontrado;
    }

    public List<LogAuditoria> getLogs() { return logs; }
    public int getTotalEncontrado() { return totalEncontrado; }

    public boolean isCortado() {
        return totalEncontrado > logs.size();
    }
}