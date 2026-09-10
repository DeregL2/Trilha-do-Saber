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

    @ManyToOne
    @JoinColumn(name = "prova_id")
    private Prova prova;

    @OneToOne
    @JoinColumn(name = "fonte_id")
    private Fonte fonte;

    //Construtor JPA
    public Questao(){

    }

    // Construtor da Classe
    public Questao(String enunciado, Dificuldade dificuldade, Tema tema, Prova prova, Fonte fonte){
        this.enunciado = enunciado;
        this.dificuldade = dificuldade;
        this.tema = tema;
        this.prova = prova;
        this.fonte = fonte;
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

    public Prova getProva(){return  prova;}

    public Fonte getFonte(){return fonte;}
}
