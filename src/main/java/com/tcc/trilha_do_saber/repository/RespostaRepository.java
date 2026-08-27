package com.tcc.trilha_do_saber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.trilha_do_saber.model.Resposta;

// Repositório de Resposta — onde vamos salvar cada tentativa de resposta do usuário.
public interface RespostaRepository extends JpaRepository<Resposta, Long> {
}