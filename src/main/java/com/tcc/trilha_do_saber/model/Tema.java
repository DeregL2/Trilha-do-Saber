package com.tcc.trilha_do_saber.model;

// Todos os importes do JPA
import jakarta.persistence.*;

@Entity // Representa uma tabela no banco de dados
public class Tema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    
    private String nome;
    private String descricao;

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
