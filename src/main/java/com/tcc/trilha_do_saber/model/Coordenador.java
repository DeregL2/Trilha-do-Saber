package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;

@Entity
public class Coordenador extends Usuario {

    private String registroFuncional;
    private String curso;

    public Coordenador(){
    }

    public Coordenador(String nome, String email, String senha, String registroFuncional, String curso){
        super(nome, email, senha);
        this.registroFuncional = registroFuncional;
        this.curso = curso;
    }

    public String getRegistroFuncional(){
        return registroFuncional;
    }

    public String getCurso(){
        return curso;
    }

    public void setRegistroFuncional(String registroFuncional){
        this.registroFuncional = registroFuncional;
    }

    public void setCurso(String curso){
        this.curso = curso;
    }
}
