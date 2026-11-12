package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ProvaAplicadaTurma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prova_aplicada_id")
    private ProvaAplicada provaAplicada;

    @Column(nullable = false)
    private String curso;

    @Column(nullable = false)
    private int semestre;

    @Column(nullable = false)
    private String turma;

    @Column(nullable = false)
    private String turno;

    public ProvaAplicadaTurma() {
    }

    public ProvaAplicadaTurma(ProvaAplicada provaAplicada, String curso, int semestre, String turma, String turno) {
        this.provaAplicada = provaAplicada;
        this.curso = curso;
        this.semestre = semestre;
        this.turma = turma;
        this.turno = turno;
    }

    public Long getId() { return id; }
    public ProvaAplicada getProvaAplicada() { return provaAplicada; }
    public String getCurso() { return curso; }
    public int getSemestre() { return semestre; }
    public String getTurma() { return turma; }
    public String getTurno() { return turno; }

    public String getRotulo() {
        return curso + " · " + semestre + "º semestre · turma " + turma + " · " + turno;
    }
}