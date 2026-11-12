package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "turma_enade", uniqueConstraints = @UniqueConstraint(
        columnNames = {"periodo_letivo", "curso", "semestre", "turma", "turno"}))
public class TurmaEnade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "periodo_letivo", nullable = false)
    private String periodoLetivo;

    @Column(nullable = false)
    private String curso;

    @Column(nullable = false)
    private int semestre;

    @Column(nullable = false)
    private String turma;

    @Column(nullable = false)
    private String turno;

    @Column(name = "marcado_em", nullable = false)
    private LocalDateTime marcadoEm;

    @Column(name = "marcado_por_nome")
    private String marcadoPorNome;

    public TurmaEnade() {
    }

    public TurmaEnade(String periodoLetivo, String curso, int semestre, String turma, String turno,
                      String marcadoPorNome) {
        this.periodoLetivo = periodoLetivo;
        this.curso = curso;
        this.semestre = semestre;
        this.turma = turma;
        this.turno = turno;
        this.marcadoEm = LocalDateTime.now();
        this.marcadoPorNome = marcadoPorNome;
    }

    public Long getId() {
        return id;
    }

    public String getPeriodoLetivo() {
        return periodoLetivo;
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

    public LocalDateTime getMarcadoEm() {
        return marcadoEm;
    }

    public String getMarcadoPorNome() {
        return marcadoPorNome;
    }
}