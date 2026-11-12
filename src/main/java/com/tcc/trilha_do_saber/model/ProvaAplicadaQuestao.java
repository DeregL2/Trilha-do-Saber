package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ProvaAplicadaQuestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prova_aplicada_id")
    private ProvaAplicada provaAplicada;

    @ManyToOne
    @JoinColumn(name = "questao_id")
    private Questao questao;

    private int ordem;

    public ProvaAplicadaQuestao() {
    }

    public ProvaAplicadaQuestao(ProvaAplicada provaAplicada, Questao questao, int ordem) {
        this.provaAplicada = provaAplicada;
        this.questao = questao;
        this.ordem = ordem;
    }

    public Long getId() { return id; }
    public ProvaAplicada getProvaAplicada() { return provaAplicada; }
    public Questao getQuestao() { return questao; }
    public int getOrdem() { return ordem; }
}