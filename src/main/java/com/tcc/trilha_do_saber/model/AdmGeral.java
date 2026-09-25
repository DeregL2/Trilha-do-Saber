package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Entity;

@Entity
public class AdmGeral extends Usuario {

    public AdmGeral(){
    }

    public AdmGeral(String nome, String email, String senha){
        super(nome, email, senha);
    }
}
