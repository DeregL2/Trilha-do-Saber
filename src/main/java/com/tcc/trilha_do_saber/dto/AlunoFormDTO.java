package com.tcc.trilha_do_saber.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

<<<<<<< HEAD
=======
// Dados do formulario de aluno. O mesmo DTO serve pra criar e editar.
>>>>>>> main
public class AlunoFormDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String senha; // em branco na edição = mantém a senha atual

    @NotBlank(message = "RA é obrigatório")
    private String ra;

    @NotBlank(message = "Curso é obrigatório")
    private String curso;

    private int semestre;

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public String getNome(){ return nome; }
    public void setNome(String nome){ this.nome = nome; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getSenha(){ return senha; }
    public void setSenha(String senha){ this.senha = senha; }

    public String getRa(){ return ra; }
    public void setRa(String ra){ this.ra = ra; }

    public String getCurso(){ return curso; }
    public void setCurso(String curso){ this.curso = curso; }

    public int getSemestre(){ return semestre; }
    public void setSemestre(int semestre){ this.semestre = semestre; }
}
