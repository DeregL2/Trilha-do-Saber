package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;

@Entity
public class Aluno extends Usuario {

    private String rgm;
    private String curso;
    private int semestre;

    public Aluno(){
    }

    public Aluno(String nome, String email, String senha, String rgm, String curso, int semestre){
        super(nome, email, senha);
        this.rgm = rgm;
        this.curso = curso;
        this.semestre = semestre;
    }

    public String getRgm(){
        return rgm;
    }

    public String getCurso(){
        return curso;
    }

    public int getSemestre(){
        return semestre;
    }

    public void setRgm(String rgm){
        this.rgm = this.rgm;
    }

    public void setCurso(String curso){
        this.curso = curso;
    }

    public void setSemestre(int semestre){
        this.semestre = semestre;
    }
}