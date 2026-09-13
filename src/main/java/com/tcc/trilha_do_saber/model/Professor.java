package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;

@Entity
public class Professor extends Usuario {

    private String registroProfissional;
    private String disciplina;

    // Construtor JPA
    public Professor(){
    }

    public Professor(String nome, String email, String senha, String registroProfissional, String disciplina){
        super(nome, email, senha);
        this.registroProfissional = registroProfissional;
        this.disciplina = disciplina;
    }

    public String getRegistroProfissional(){
        return registroProfissional;
    }

    public String getDisciplina(){
        return disciplina;
    }

    public void setRegistroProfissional(String registroProfissional){
        this.registroProfissional = registroProfissional;
    }

    public void setDisciplina(String disciplina){
        this.disciplina = disciplina;
    }
}
