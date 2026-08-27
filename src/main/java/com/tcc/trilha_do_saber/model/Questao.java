package com.tcc.trilha_do_saber.model;

// Todos os importes do JPA
import jakarta.persistence.*;

@Entity // Representa uma tabela no banco de dados
public class Questao {
    @Id // Atributo que vai receber a chave primaria da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
    private Long id;

    @Column(columnDefinition = "TEXT")// Força a coluna a ser um TEXT
    private String enunciado;

    @Enumerated(EnumType.STRING) // Salvar o valor de ENUM como texto no Banco
    private Dificuldade dificuldade;

    @ManyToOne
    @JoinColumn(name = "tema_id") // Relacionamento de Questao para Tema (Muitas questoes pertencem a um tema)
    private Tema tema;

    //Construtor JPA
    public Questao(){

    }

    // Construtor da Classe
    public Questao(String enunciado, Dificuldade dificuldade, Tema tema){
        this.enunciado = enunciado;
        this.dificuldade = dificuldade;
        this.tema = tema;
    }

    public Long getId() { 
        return id;
    }

    public String getEnunciado(){
        return enunciado;
    }

    public Dificuldade getDificuldade(){
        return dificuldade;
    }

    public Tema getTema(){
        return tema;
    }
}
