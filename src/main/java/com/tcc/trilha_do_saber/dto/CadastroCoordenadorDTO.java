package com.tcc.trilha_do_saber.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CadastroCoordenadorDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
            message = "A senha precisa ter no mínimo 8 caracteres, com letra, número e caractere especial"
    )
    private String senha;

    @NotBlank(message = "Confirme a senha")
    private String confirmarSenha;

    @NotBlank(message = "Registro de funcionário é obrigatório")
    private String registroFuncional;

    @NotBlank(message = "Curso é obrigatório")
    private String curso;

    @AssertTrue(message = "É necessário aceitar o termo de consentimento (LGPD) para continuar")
    private boolean aceitouTermos;

    public String getNome(){ return nome; }
    public void setNome(String nome){ this.nome = nome; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getSenha(){ return senha; }
    public void setSenha(String senha){ this.senha = senha; }

    public String getConfirmarSenha(){ return confirmarSenha; }
    public void setConfirmarSenha(String confirmarSenha){ this.confirmarSenha = confirmarSenha; }

    public String getRegistroFuncional(){ return registroFuncional; }
    public void setRegistroFuncional(String registroFuncional){ this.registroFuncional = registroFuncional; }

    public String getCurso(){ return curso; }
    public void setCurso(String curso){ this.curso = curso; }

    public boolean isAceitouTermos(){ return aceitouTermos; }
    public void setAceitouTermos(boolean aceitouTermos){ this.aceitouTermos = aceitouTermos; }

    public boolean senhasConferem(){
        return senha != null && senha.equals(confirmarSenha);
    }
}
