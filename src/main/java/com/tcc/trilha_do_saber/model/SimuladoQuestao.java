package com.tcc.trilha_do_saber.model;

// Todos os importes do JPA
import jakarta.persistence.*;

@Entity // Representa uma tabela no banco de dados
public class SimuladoQuestao {

    @Id // Atributo que vai receber a chave primaria da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
    private Long id;

    @ManyToOne
    @JoinColumn(name = "simulado_id") // Muitas linhas pertencem a um Simulado
    private Simulado simulado;

    @ManyToOne
    @JoinColumn(name = "questao_id") // Muitas linhas apontam para uma Questao
    private Questao questao;

    private int ordem; // Posicao da questao no simulado: 1, 2, 3...

    // Construtor JPA
    public SimuladoQuestao() {

    }

    // Construtor da Classe
    public SimuladoQuestao(Simulado simulado, Questao questao, int ordem) {
        this.simulado = simulado;
        this.questao = questao;
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public Simulado getSimulado() {
        return simulado;
    }

    public Questao getQuestao() {
        return questao;
    }

    public int getOrdem() {
        return ordem;
    }
}