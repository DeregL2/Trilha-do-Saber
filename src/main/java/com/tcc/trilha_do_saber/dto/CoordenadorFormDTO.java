package com.tcc.trilha_do_saber.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CoordenadorFormDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Registro de funcionário é obrigatório")
    private String registroFuncional;

    @NotBlank(message = "Curso é obrigatório")
    private String curso;

    private boolean ativo;

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public String getNome(){ return nome; }
    public void setNome(String nome){ this.nome = nome; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getRegistroFuncional(){ return registroFuncional; }
    public void setRegistroFuncional(String registroFuncional){ this.registroFuncional = registroFuncional; }

    public String getCurso(){ return curso; }
    public void setCurso(String curso){ this.curso = curso; }

    public boolean isAtivo(){ return ativo; }
    public void setAtivo(boolean ativo){ this.ativo = ativo; }
}

