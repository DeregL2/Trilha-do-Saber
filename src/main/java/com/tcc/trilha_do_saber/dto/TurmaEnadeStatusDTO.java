package com.tcc.trilha_do_saber.dto;

public class TurmaEnadeStatusDTO {

    private final String curso;
    private final int semestre;
    private final String turma;
    private final String turno;
    private final boolean enade;

    public TurmaEnadeStatusDTO(String curso, int semestre, String turma, String turno, boolean enade) {
        this.curso = curso;
        this.semestre = semestre;
        this.turma = turma;
        this.turno = turno;
        this.enade = enade;
    }

    public String getCurso() {
        return curso;
    }

    public int getSemestre() {
        return semestre;
    }

    public String getTurma() {
        return turma;
    }

    public String getTurno() {
        return turno;
    }

    public boolean isEnade() {
        return enade;
    }
}