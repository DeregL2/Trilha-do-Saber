package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "prova_aplicada_aluno", uniqueConstraints = @UniqueConstraint(columnNames = {"prova_aplicada_id", "aluno_id"}))
public class ProvaAplicadaAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prova_aplicada_id")
    private ProvaAplicada provaAplicada;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    public ProvaAplicadaAluno() {
    }

    public ProvaAplicadaAluno(ProvaAplicada provaAplicada, Aluno aluno) {
        this.provaAplicada = provaAplicada;
        this.aluno = aluno;
    }

    public Long getId() { return id; }
    public ProvaAplicada getProvaAplicada() { return provaAplicada; }
    public Aluno getAluno() { return aluno; }
}