package com.tcc.trilha_do_saber.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ProfessorFormDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Registro profissional é obrigatório")
    private String registroProfissional;

    private String disciplina;

    private boolean ativo;

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public String getNome(){ return nome; }
    public void setNome(String nome){ this.nome = nome; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getRegistroProfissional(){ return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional){ this.registroProfissional = registroProfissional; }

    public String getDisciplina(){ return disciplina; }
    public void setDisciplina(String disciplina){ this.disciplina = disciplina; }

    public boolean isAtivo(){ return ativo; }
    public void setAtivo(boolean ativo){ this.ativo = ativo; }
}
