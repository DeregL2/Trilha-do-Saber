package com.tcc.trilha_do_saber.model;

// Todos os importes do JPA
import jakarta.persistence.*;

@Entity // Representa uma tabela no banco de dados
public class Alternativa {

    @Id // Atributo que vai receber a chave primaria da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
    private Long id;

    private String texto;
    private boolean correta;
    
    @ManyToOne
    @JoinColumn(name = "questao_id") // Relacionamento de Questao para Tema (Muitas questoes pertencem a um tema)
    private Questao questao;

    //Cosntrutor JPA
    public Alternativa(){

    }

    public Alternativa(String texto, boolean correta, Questao questao){
        this.texto = texto;
        this.correta = correta;
        this.questao = questao;
    }

    public Long getId(){
        return id;
    }

    public String getTexto(){
        return texto;
    }

    public boolean isCorreta(){
        return correta;
    }

    public Questao getQuestao(){
        return questao;
    }

}   
