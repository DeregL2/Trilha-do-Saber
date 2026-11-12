package com.tcc.trilha_do_saber.dto;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public class TurmaAlvoDTO implements Serializable {

    private final String curso;
    private final int semestre;
    private final String turma;
    private final String turno;

    public TurmaAlvoDTO(String curso, int semestre, String turma, String turno) {
        this.curso = curso;
        this.semestre = semestre;
        this.turma = turma;
        this.turno = turno;
    }

    public static TurmaAlvoDTO de(String chave) {
        String[] partes = chave == null ? new String[0] : chave.split("\\|", -1);
        if (partes.length != 4) {
            throw new IllegalArgumentException("Turma inválida");
        }
        try {
            return new TurmaAlvoDTO(partes[0], Integer.parseInt(partes[1].trim()), partes[2], partes[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Turma inválida");
        }
    }

    public String getCurso() { return curso; }
    public int getSemestre() { return semestre; }
    public String getTurma() { return turma; }
    public String getTurno() { return turno; }

    public String getChave() {
        return curso + "|" + semestre + "|" + turma + "|" + turno;
    }

    public String getRotulo() {
        return curso + " · " + semestre + "º semestre · turma " + turma + " · " + turno;
    }

    private String normalizada() {
        return getChave().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof TurmaAlvoDTO turmaAlvo && normalizada().equals(turmaAlvo.normalizada());
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalizada());
    }
}