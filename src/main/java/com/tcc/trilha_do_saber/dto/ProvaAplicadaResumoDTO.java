package com.tcc.trilha_do_saber.dto;

import com.tcc.trilha_do_saber.model.StatusProva;

import java.time.LocalDateTime;
import java.util.List;

public class ProvaAplicadaResumoDTO {

    private final Long id;
    private final String titulo;
    private final LocalDateTime abertura;
    private final LocalDateTime encerramento;
    private final StatusProva status;
    private final long totalQuestoes;
    private final List<String> turmas;

    public ProvaAplicadaResumoDTO(Long id, String titulo, LocalDateTime abertura, LocalDateTime encerramento,
                                  StatusProva status, long totalQuestoes, List<String> turmas) {
        this.id = id;
        this.titulo = titulo;
        this.abertura = abertura;
        this.encerramento = encerramento;
        this.status = status;
        this.totalQuestoes = totalQuestoes;
        this.turmas = List.copyOf(turmas);
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public LocalDateTime getAbertura() { return abertura; }
    public LocalDateTime getEncerramento() { return encerramento; }
    public StatusProva getStatus() { return status; }
    public long getTotalQuestoes() { return totalQuestoes; }
    public List<String> getTurmas() { return turmas; }

    public boolean isAgendada() {
        return status == StatusProva.AGENDADA;
    }
}