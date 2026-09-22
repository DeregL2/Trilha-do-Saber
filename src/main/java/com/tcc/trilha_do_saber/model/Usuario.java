package com.tcc.trilha_do_saber.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;

    private boolean ativo;

    private LocalDateTime dataExclusao;

    private boolean anonimizado;

    public Usuario(){
    }

    public Usuario(String nome, String email, String senha){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = false;
    }

    public Long getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public String getEmail(){
        return email;
    }

    public String getSenha(){
        return senha;
    }

    public boolean isAtivo(){
        return ativo;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setSenha(String senha){
        this.senha = senha;
    }

    public void setAtivo(boolean ativo){
        this.ativo = ativo;
    }

    public LocalDateTime getDataExclusao(){
        return dataExclusao;
    }

    public void setDataExclusao(LocalDateTime dataExclusao){
        this.dataExclusao = dataExclusao;
    }

    public boolean isAnonimizado(){
        return anonimizado;
    }

    public void setAnonimizado(boolean anonimizado){
        this.anonimizado = anonimizado;
    }

    public boolean isExcluido(){
        return dataExclusao != null;
    }

}
