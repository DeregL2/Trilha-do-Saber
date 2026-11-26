package com.tcc.trilha_do_saber.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Usuário logado tentando agir sobre algo fora do que o perfil dele pode gerenciar (ex.: aluno de outro curso)
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
