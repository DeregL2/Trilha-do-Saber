package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // Representa uma tabela no banco de dados
public class Prova {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Atributo para gerar as Chaves automaticas.
    private Long id;
    private int ano;
    private String curso;

    // Construtor JPA
    public Prova() {}

    //Construtor
    public Prova(int ano, String curso) {
        this.ano = ano;
        this.curso = curso;
    }

    public Long getId(){
        return id;
    }

    public int getAno(){
        return ano;
    }

    public String getCurso(){
        return curso;
    }

}
