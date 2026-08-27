package com.tcc.trilha_do_saber.model;

import java.time.LocalDateTime;

// Todos os importes do JPA
import jakarta.persistence.*;

@Entity // Representa uma tabela no banco de dados
public class Resposta {

    @Id // Atributo que vai receber a chave primaria da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
    private Long id;

    private String nomeUsuario;

    @ManyToOne
    @JoinColumn(name = "questao_id") // Relacionamento de Questao para Tema (Muitas questoes pertencem a um tema)
    private Questao questao;

    @ManyToOne
    @JoinColumn(name = "alternativa_id")
    private Alternativa alternativaEscolhida;

    private boolean acertou;

    private LocalDateTime dataHora;

    //Construtor JPA
    public Resposta(){

    }

    //Construtor da Classe
    public Resposta(String nomeUsuario, Questao questao, Alternativa alternativaEscolhida, boolean acertou, LocalDateTime dataHora){

        this.nomeUsuario = nomeUsuario;
        this.questao = questao;
        this.alternativaEscolhida = alternativaEscolhida;
        this.acertou = acertou;
        this.dataHora = dataHora;
    }

    public Long getId(){
        return id;
    }

    public String getNomeUsuario(){
        return nomeUsuario;
    }

    public Questao getQuestao(){
        return questao;
    }

    public Alternativa getAlternativaEscolhida(){
        return alternativaEscolhida;
    }

    public boolean isAcertou(){
        return acertou;
    }

    public LocalDateTime getDataHora(){
        return dataHora;
    }
}
