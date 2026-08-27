package com.tcc.trilha_do_saber.model;

// Todos os importes do JPA
import jakarta.persistence.*;

@Entity // Representa uma tabela no banco de dados
public class Tema {
    @Id // Atributo que vai receber a chave primaria da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
    private Long id;    
    private String nome;
    private String descricao;

    // Construtor para o JPA
    public Tema(){
    }

    //Construtor da Classe
    public Tema(String nome, String descricao){
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }
    
    public String getDescricao(){
        return descricao;
    }
}
