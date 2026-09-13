package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;

@Entity
public class Aluno extends Usuario {

    private String ra;
    private String curso;
    private int semestre;

    // Construtor JPA
    public Aluno(){
    }

    public Aluno(String nome, String email, String senha, String ra, String curso, int semestre){
        super(nome, email, senha);
        this.ra = ra;
        this.curso = curso;
        this.semestre = semestre;
    }

    public String getRa(){
        return ra;
    }

    public String getCurso(){
        return curso;
    }

    public int getSemestre(){
        return semestre;
    }

    public void setRa(String ra){
        this.ra = ra;
    }

    public void setCurso(String curso){
        this.curso = curso;
    }

    public void setSemestre(int semestre){
        this.semestre = semestre;
    }
}
